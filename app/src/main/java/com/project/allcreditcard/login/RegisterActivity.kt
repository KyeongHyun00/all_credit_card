package com.project.allcreditcard.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.project.allcreditcard.R
import com.project.allcreditcard.utility.APIService
import retrofit2.Retrofit
import com.project.allcreditcard.BuildConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

class RegisterActivity : AppCompatActivity() {

    private lateinit var name : EditText
    private lateinit var id : EditText
    private lateinit var registNum : EditText
    private lateinit var email: EditText
    private lateinit var phoneNum : EditText
    private lateinit var pw : EditText
    private lateinit var pwCheck : EditText
    private lateinit var joinButton : Button
    private lateinit var pwSendButton: Button
    private lateinit var idCheckButton: Button
    private lateinit var verificationNum: EditText
    private lateinit var verificationCheck: Button
    private lateinit var verificationLayout: LinearLayout

    private lateinit var retrofit: Retrofit
    private var service: APIService? = null
    private val url = BuildConfig.SERVER_IP

    private val auth = Firebase.auth
    private var verificationId = ""
    private var verificationCheckBoolean = false
    private var idCheckBoolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firstInit()

        name = findViewById(R.id.join_name)
        id = findViewById(R.id.join_id)
        registNum = findViewById(R.id.join_regist_numr)
        email = findViewById(R.id.join_email)
        phoneNum = findViewById(R.id.join_phone_num)
        pw = findViewById(R.id.join_pw)
        pwCheck = findViewById(R.id.join_pwCheck)

        joinButton = findViewById(R.id.join_button)
        pwSendButton = findViewById(R.id.phone_Check)
        idCheckButton = findViewById(R.id.id_Check)
        verificationNum = findViewById(R.id.verificationNum)
        verificationCheck = findViewById(R.id.verification_Check)
        verificationLayout = findViewById(R.id.verificationLayout)

        idCheckButton.setOnClickListener {
            val userID = id.text.toString()
            idCheck(userID)
        }

        pwSendButton.setOnClickListener {
            if(phoneNum.text.isNotEmpty()) {

                val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) { }
                    override fun onVerificationFailed(e: FirebaseException) {
                        Toast.makeText(this@RegisterActivity, "인증번호 전송에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                    override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                        this@RegisterActivity.verificationId = verificationId
                    }
                }

                val optionsCompat =  PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(phoneNumber82(phoneNum.text.toString()))
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(callbacks)
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(optionsCompat)
                auth.setLanguageCode("kr")

            }
        }

        verificationCheck.setOnClickListener {
            if(verificationNum.text.isNotEmpty()) {
                val credential = PhoneAuthProvider.getCredential(verificationId, verificationNum.text.toString())
                signInWithPhoneAuthCredential(credential)
            }
            else {
                Toast.makeText(this, "인증번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        joinButton.setOnClickListener {
            val joinName = name.text.toString()
            val joinId = id.text.toString()
            val joinRegistNum = registNum.text.toString()
            val joinEmail = email.text.toString()
            val joinPhoneNum = phoneNum.text.toString()
            val joinPw = pw.text.toString()
            val joinPwCheck = pwCheck.text.toString()

            val fieldMap = mapOf(joinName to "이름", joinId to "아이디", joinRegistNum to "주민번호", joinEmail to "이메일",
                joinPhoneNum to "전화번호", joinPw to "비밀번호", joinPwCheck to "비밀번호 확인")

            val emptyField = fieldMap.entries.find { it.key.isEmpty() }
            if (emptyField != null) {
                val fieldName = emptyField.value
                Toast.makeText(this, "${fieldName}을(를) 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                if(idCheckBoolean) {
                    if(verificationCheckBoolean) {
                        if (passwordCheck(joinPw, joinPwCheck)) {
                            requestRegister(joinName, joinId, joinRegistNum, joinEmail, joinPhoneNum, joinPw)
                        } else {
                            Toast.makeText(this, "비밀번호가 서로 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else {
                        Toast.makeText(this, "전화번호 인증을 먼저 해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    Toast.makeText(this@RegisterActivity, "아이디 중복 확인을 먼저 해주세요.", Toast.LENGTH_SHORT).show()

                }
            }
        }
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

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "인증 성공", Toast.LENGTH_SHORT).show()
                    verificationCheckBoolean = true
                    phoneNum.isEnabled = false
                    pwSendButton.isEnabled = false
                    verificationLayout.visibility = View.GONE


                }
                else {
                    Toast.makeText(this@RegisterActivity, "인증 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun phoneNumber82(msg : String) : String{
        val firstNumber : String = msg.substring(0,3)
        var phoneEdit = msg.substring(3)

        when(firstNumber){
            "010" -> phoneEdit = "+8210$phoneEdit"
            "011" -> phoneEdit = "+8211$phoneEdit"
            "016" -> phoneEdit = "+8216$phoneEdit"
            "017" -> phoneEdit = "+8217$phoneEdit"
            "018" -> phoneEdit = "+8218$phoneEdit"
            "019" -> phoneEdit = "+8219$phoneEdit"
            "106" -> phoneEdit = "+82106$phoneEdit"
        }
        return phoneEdit
    }

    private fun requestRegister(name: String, id: String, regiNum: String, email: String, phone: String, pw: String) {
        val callPost = service?.requestRegister(name, id,regiNum,email,phone,pw)
        callPost?.enqueue(object: Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        val result = response.body()!!.toString()
                        if(result == "pass") {
                            Toast.makeText(this@RegisterActivity, "${name}님 환영합니다.", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        }
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "서버 연결에 오류가 발생했습니다",Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun idCheck(userID: String) {
        val callPost = service?.idCheck(userID)
        callPost?.enqueue(object: Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        val result = response.body()!!.toString()
                        if(result == "pass") {
                            idCheckButton.isEnabled = false
                            id.isEnabled = false
                            Toast.makeText(this@RegisterActivity, "사용 가능한 아이디 입니다.", Toast.LENGTH_SHORT).show()
                            idCheckBoolean = true
                        }
                        else if(result == "fail") {
                            Toast.makeText(this@RegisterActivity, "이미 사용 중인 아이디 입니다.", Toast.LENGTH_SHORT).show()
                            idCheckBoolean = false
                        }
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "서버 연결에 오류가 발생했습니다",Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun passwordCheck(pw: String, pwCheck: String): Boolean {
        return pw == pwCheck
    }
}