package com.project.allcreditcard.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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

class RegisterActivity : AppCompatActivity() {

    private lateinit var name : EditText
    private lateinit var id : EditText
    private lateinit var registNum : EditText
    private lateinit var email: EditText
    private lateinit var phoneNum : EditText
    private lateinit var certiNum : EditText
    private lateinit var pw : EditText
    private lateinit var pwCheck : EditText
    private lateinit var joinButton : Button
    private lateinit var sendButton: Button
    private lateinit var checkButton: Button

    private lateinit var retrofit: Retrofit
    private var service: APIService? = null
    private val url = BuildConfig.SERVER_IP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firstInit()

        name = findViewById(R.id.join_name)
        id = findViewById(R.id.join_id)
        registNum = findViewById(R.id.join_regist_numr)
        email = findViewById(R.id.join_email)
        phoneNum = findViewById(R.id.join_phone_num)
        certiNum = findViewById(R.id.certi_num)
        pw = findViewById(R.id.join_pw)
        pwCheck = findViewById(R.id.join_pwCheck)

        joinButton = findViewById(R.id.join_button)
        sendButton = findViewById(R.id.sendNumber)
        checkButton = findViewById(R.id.checkNumber)

        joinButton.setOnClickListener {
            val joinName = name.text.toString()
            val joinId = id.text.toString()
            val joinRegistNum = registNum.text.toString()
            val joinEmail = email.text.toString()
            val joinPhoneNum = phoneNum.text.toString()
            val joinCertiNum = certiNum.text.toString()
            val joinPw = pw.text.toString()
            val joinPwCheck = pwCheck.text.toString()

            val fieldMap = mapOf(joinName to "이름", joinId to "아이디", joinRegistNum to "주민번호", joinEmail to "이메일",
                joinPhoneNum to "전화번호", joinCertiNum to "인증번호", joinPw to "비밀번호", joinPwCheck to "비밀번호 확인")

            val emptyField = fieldMap.entries.find { it.key.isEmpty() }
            if (emptyField != null) {
                val fieldName = emptyField.value
                Toast.makeText(this, "${fieldName}을(를) 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                if (passwordCheck(joinPw, joinPwCheck)) {
                    requestRegister(joinName, joinId, joinRegistNum, joinEmail, joinPhoneNum, joinPw)
                } else {
                    Toast.makeText(this, "비밀번호가 서로 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
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

    private fun passwordCheck(pw: String, pwCheck: String): Boolean {
        return pw == pwCheck
    }
    /*
        private fun certificateNumber(certiNum: Int) {
           //인증번호 기능 구현 필요
        }
     */
}