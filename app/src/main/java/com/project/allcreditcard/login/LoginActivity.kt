package com.project.allcreditcard.login

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.project.allcreditcard.BuildConfig
import com.project.allcreditcard.MainActivity
import com.project.allcreditcard.R
import com.project.allcreditcard.TermsOfLawsActivity
import com.project.allcreditcard.utility.APIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.util.concurrent.Executor

class LoginActivity : AppCompatActivity() {

    companion object {
        const val TAG: String = "BiometricActivity"
    }

    private var idData = ""
    private var pwData = ""
    private lateinit var id: EditText
    private lateinit var pw: EditText
    private lateinit var autoLogin: CheckBox
    private lateinit var bioLogin: CheckBox
    private lateinit var pwFind: TextView
    private lateinit var sign: TextView
    private lateinit var quickLogin: Button
    private lateinit var loginButton: Button

    private lateinit var retrofit: Retrofit
    private var service: APIService? = null
    private val url = BuildConfig.SERVER_IP

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private var executor: Executor? = null
    private var biometricPrompt: BiometricPrompt? = null
    private var promptInfo: BiometricPrompt.PromptInfo? = null

    @RequiresApi(Build.VERSION_CODES.R)
    private val loginLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d(TAG, "registerForActivityResult - result : $result")

            if (result.resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "registerForActivityResult - RESULT_OK")
                authenticateToEncrypt()
            } else {
                Log.d(TAG, "registerForActivityResult - NOT RESULT_OK")
            }
        }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onStart() {
        super.onStart()
        preferences = getSharedPreferences("Account", Context.MODE_PRIVATE)
        if (autoLogin.isChecked) {
            showToast("!!!!!!!자동 로그인 성공!!!!!!!")
        } else if (bioLogin.isChecked) {
            authenticateToEncrypt()
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        biometricPrompt = setBiometricPrompt()
        promptInfo = setPromptInfo()

        preferences = getSharedPreferences("Account", Context.MODE_PRIVATE)
        editor = preferences.edit()

        firstInit()

        id = findViewById(R.id.editID)
        pw = findViewById(R.id.editPW)
        autoLogin = findViewById(R.id.autoLogin)
        bioLogin = findViewById(R.id.biometricsLogin)
        pwFind = findViewById(R.id.Findpassword)
        sign = findViewById(R.id.signin)
        quickLogin = findViewById(R.id.quickloginbutton)
        loginButton = findViewById(R.id.loginbutton)

        val storeID = preferences.getString("userID", "")
        val storeBioLogin = preferences.getString("bioLoginUse", "")
        autoLogin.isChecked = storeID != ""
        bioLogin.isChecked = storeBioLogin != ""

        sign.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        pwFind.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgetPasswordActivity::class.java))
        }

        quickLogin.setOnClickListener {
            /*val dialog = CustomDialog(this)
            dialog.setOnClickedListener {
                Toast.makeText(this@LoginActivity, it, Toast.LENGTH_SHORT).show()
            }
            dialog.show("제목", "내용 부분 입니다.")*/
            startActivity(Intent(this@LoginActivity, TermsOfLawsActivity::class.java))
        }

        loginButton.setOnClickListener {
            val loginId = id.text.toString()
            val loginPw = pw.text.toString()

            val fieldMap = mapOf(loginId to "아이디", loginPw to "비밀번호")

            val emptyField = fieldMap.entries.find { it.key.isEmpty() }

            if (emptyField != null) {
                val fieldName = emptyField.value
                showToast("${fieldName}을(를) 입력해주세요.")
            } else {
                loginRequest(loginId, loginPw)
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun setPromptInfo(): BiometricPrompt.PromptInfo {
        val promptBuilder: BiometricPrompt.PromptInfo.Builder = BiometricPrompt.PromptInfo.Builder()

        promptBuilder.setTitle("생체 인식 로그인")
        promptBuilder.setSubtitle("생체 인식을 사용하여 로그인합니다.")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            promptBuilder.setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
        }

        promptInfo = promptBuilder.build()
        return promptInfo as BiometricPrompt.PromptInfo
    }

    private fun setBiometricPrompt(): BiometricPrompt {
        executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(this@LoginActivity, executor!!, object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                showToast("""지문 인식 ERROR [ errorCode: $errorCode, errString: $errString ]""")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                showToast("지문 인식 성공")
                login2(bioLogin.isChecked, autoLogin.isChecked, idData, pwData)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                showToast("지문 인식 실패")
            }

        })
        return biometricPrompt as BiometricPrompt
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun authenticateToEncrypt() {
        Log.d(TAG, "authenticateToEncrypt() ")

        val textStatus: String
        val biometricManager = BiometricManager.from(this@LoginActivity)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> textStatus = "App can authenticate using biometrics."
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> textStatus = "No biometric features available on this device."
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> textStatus = "Biometric features are currently unavailable."
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                textStatus = "Prompts the user to create credentials that your app accepts."

                showEnrollmentDialog()
            }
            else ->  textStatus = "Fail Biometric facility"
        }
        Log.d(TAG, textStatus)
        goAuthenticate()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun showEnrollmentDialog() {
        val dialogBuilder = AlertDialog.Builder(this@LoginActivity)
        dialogBuilder
            .setTitle("생체 인식")
            .setMessage("지문 등록이 필요합니다. 지문등록 화면으로 이동하시겠습니까?")
            .setPositiveButton("확인") { _: DialogInterface, _: Int -> goBiometricSettings() }
            .setNegativeButton("취소") { _: DialogInterface, _: Int -> }
        dialogBuilder.show()
    }

    private fun goAuthenticate() {
        Log.d(TAG, "goAuthenticate - promptInfo : $promptInfo")
        promptInfo?.let {
            biometricPrompt?.authenticate(it)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun goBiometricSettings() {
        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
            putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
        }
        loginLauncher.launch(enrollIntent)
    }

    private fun firstInit() {
        val gson: Gson = GsonBuilder().setLenient().create()
        retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        service = retrofit.create(APIService::class.java)
    }

    private fun loginRequest(id: String, pw: String) {
        val callPost = service?.requestLogin(id, pw)
        callPost?.enqueue(object : Callback<String?> {
            @RequiresApi(Build.VERSION_CODES.R)
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if (response.isSuccessful) {
                    try {
                        val result = response.body()!!.toString()
                        login(result, id, pw)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                showToast("서버 연결에 오류가 발생했습니다")
            }
        })
    }

    private fun login2(bio: Boolean, auto: Boolean, id: String, pw: String) {
        val userID = if (auto) id else ""
        val userPW = if (auto) pw else ""
        val bioLoginUse = if (bio) "true" else ""

        editor.putString("userID", userID)
        editor.putString("userPW", userPW)
        editor.putString("bioLoginUse", bioLoginUse)
        editor.apply()

        showToast("로그인 성공!!!!")

        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun login(result: String, id: String, pw: String) {
        when (result) {
            "pass" -> {
                if (autoLogin.isChecked) {
                    if (bioLogin.isChecked) {
                        idData = id
                        pwData = pw
                        authenticateToEncrypt()
                    } else {
                        handleLoginSuccess(id, pw)
                    }
                } else {
                    idData = ""
                    pwData = ""
                    if (bioLogin.isChecked) {
                        idData = id
                        pwData = pw
                        authenticateToEncrypt()
                    } else {
                        handleLoginSuccess("", "")
                    }
                }
            }
            "pwFail" -> showToast("비밀번호가 틀립니다")
            "idFail" -> showToast("아이디가 틀립니다")
        }
    }

    private fun handleLoginSuccess(userID: String, userPW: String) {
        editor.putString("userID", userID)
        editor.putString("userPW", userPW)
        editor.putString("bioLoginUse", "")
        editor.apply()
        showToast("로그인 성공!!!!")
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }

    private var backPressedTime: Long = 0

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (backPressedTime + 3000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finish()
        } else {
            showToast("한번 더 뒤로가기 버튼을 누르면 종료됩니다.")
        }
        backPressedTime = System.currentTimeMillis()
    }
}