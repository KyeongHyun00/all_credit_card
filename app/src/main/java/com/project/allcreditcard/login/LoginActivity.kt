package com.project.allcreditcard.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.project.allcreditcard.BuildConfig
import com.project.allcreditcard.R
import com.project.allcreditcard.utility.APIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException

class LoginActivity : AppCompatActivity() {

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
    private lateinit var editor: Editor


    override fun onStart() {
        super.onStart()
        preferences = getSharedPreferences("Account", Context.MODE_PRIVATE)
        if(autoLogin.isChecked) {
            Toast.makeText(this@LoginActivity, "!!!!!!!자동 로그인 성공!!!!!!!", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
        if(storeID != "") {
            autoLogin.isChecked = true
        }

        sign.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        pwFind.setOnClickListener{
            startActivity(Intent(this@LoginActivity, ForgetPasswordActivity::class.java))
        }

        loginButton.setOnClickListener {
            val loginId = id.text.toString()
            val loginPw = pw.text.toString()

            val fieldMap = mapOf(loginId to "아이디", loginPw to "비밀번호")

            val emptyField = fieldMap.entries.find { it.key.isEmpty() }

            if (emptyField != null) {
                val fieldName = emptyField.value
                Toast.makeText(this, "${fieldName}을(를) 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                loginRequest(loginId, loginPw)
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

    private fun loginRequest(id: String, pw: String): Boolean {
        val callPost = service?.requestLogin(id, pw)
        callPost?.enqueue(object: Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        val result = response.body()!!.toString()
                        if(result == "pass") {
                            if(autoLogin.isChecked) {
                                editor.putString("userID", id)
                                editor.putString("userPW", pw)
                                editor.apply()

                                Toast.makeText(this@LoginActivity, "로그인 성공!!!!", Toast.LENGTH_SHORT).show()
                            }
                            else {
                                Toast.makeText(this@LoginActivity, "로그인 성공!!!!", Toast.LENGTH_SHORT).show()
                            }
                        }
                        if(result == "pwFail") {
                            Toast.makeText(this@LoginActivity,"비밀번호가 틀립니다", Toast.LENGTH_SHORT).show()
                        }
                        if(result == "idFail"){
                            Toast.makeText(this@LoginActivity,"아이디가 틀립니다",Toast.LENGTH_SHORT).show()
                        }
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "서버 연결에 오류가 발생했습니다",Toast.LENGTH_SHORT).show()
            }

        })
        return true
    }
}


