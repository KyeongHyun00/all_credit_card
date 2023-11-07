package com.project.allcreditcard.login

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
import org.w3c.dom.Text
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var id : EditText
    private lateinit var pw : EditText
    private lateinit var autoLogin : CheckBox
    private lateinit var bioLogin : CheckBox
    private lateinit var pwFind : TextView
    private lateinit var sign : TextView
    private lateinit var quickLogin : Button
    private lateinit var loginButton : Button

    private lateinit var retrofit: Retrofit
    private var service: APIService? = null
    private val url = BuildConfig.SERVER_IP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firstInit()

        id = findViewById(R.id.editID)
        pw = findViewById(R.id.editPW)
        autoLogin = findViewById(R.id.autoLogin)
        bioLogin = findViewById(R.id.biometricsLogin)
        pwFind = findViewById(R.id.Findpassword)
        sign = findViewById(R.id.signin)
        quickLogin = findViewById(R.id.quickloginbutton)
        loginButton = findViewById(R.id.loginbutton)

        loginButton.setOnClickListener {
            val login_id = id.text.toString()
            val login_pw = pw.text.toString()

            val fieldMap = mapOf(login_id to "아이디", login_pw to "비밀번호")

            val emptyField = fieldMap.entries.find { it.key.isEmpty() }

            if(emptyField != null) {
                val fieldName = emptyField.value
                Toast.makeText(this, "${fieldName}을(를) 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else{
                loginRequest(login_id, login_pw)
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

    private fun loginRequest(id: String, pw:String): Boolean{
        //DB에서 ID, PW 받아와서 체크하기
        return true
    }
}


