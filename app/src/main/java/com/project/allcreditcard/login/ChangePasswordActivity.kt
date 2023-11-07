package com.project.allcreditcard.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var changePwEt: EditText
    private lateinit var changePwEt_Check: EditText
    private lateinit var changeBtn: Button

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var retrofit: Retrofit
    private var service: APIService? = null
    private val url = BuildConfig.SERVER_IP
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        preferences = getSharedPreferences("Account", Context.MODE_PRIVATE)
        val savedId = preferences.getString("userID", "").toString()

        changePwEt = findViewById(R.id.changePwEt)
        changePwEt_Check = findViewById(R.id.changePwEt_Check)
        changeBtn = findViewById(R.id.changeBtn)

        editor = preferences.edit()
        firstInit()

        changeBtn.setOnClickListener {
            val changePwText = changePwEt.text.toString()
            val changePwCheckText = changePwEt_Check.text.toString()

            if(changePwText.isNotBlank()) {
                if(changePwCheckText.isNotBlank()){
                    if(changePwText.length >= 8) {
                        if(changePwText == changePwCheckText){
                            changePassword(savedId, changePwText)
                        }
                        else {
                            changePwEt_Check.requestFocus()
                            Toast.makeText(this, "비밀번호가 서로 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else {
                        changePwEt.requestFocus()
                        Toast.makeText(this, "비밀번호는 8자리 이상으로 해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    changePwEt_Check.requestFocus()
                    Toast.makeText(this, "변경하실 비밀번호를 한번 더 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                changePwEt.requestFocus()
                Toast.makeText(this, "변경하실 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
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

    private fun changePassword(userID: String, changePW: String) {
        val callPost = service?.changePw(userID, changePW)
        callPost?.enqueue(object: Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        editor.clear()
                        editor.commit()
                        Toast.makeText(this@ChangePasswordActivity, "비밀번호가 변경되었습니다. \n다시 로그인 해주세요.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@ChangePasswordActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                else {
                    Toast.makeText(this@ChangePasswordActivity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@ChangePasswordActivity, "서버 연결에 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }

        })
    }
}