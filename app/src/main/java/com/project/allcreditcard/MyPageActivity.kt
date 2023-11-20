package com.project.allcreditcard

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.project.allcreditcard.account.AmountPaymentActivity
import com.project.allcreditcard.account.CreditInformationActivity
import com.project.allcreditcard.login.LoginActivity
import com.project.allcreditcard.utility.APIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
class MyPageActivity : AppCompatActivity() {

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    navigateTo(MainActivity::class.java)
                    true
                }
                R.id.nav_CreditPoint -> {
                    navigateTo(CreditInformationActivity::class.java)
                    true
                }
                R.id.nav_Payment -> {
                    navigateTo(AmountPaymentActivity::class.java)
                    true
                }
                else -> false
            }
        }

    private lateinit var logoutTv : TextView
    private lateinit var unregisterTv: TextView

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var retrofit: Retrofit
    private var service: APIService? = null
    private val url = BuildConfig.SERVER_IP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        val navView: BottomNavigationView = findViewById(R.id.mypage_nav)
        navView.setOnItemSelectedListener(onNavigationItemSelectedListener)
        navView.menu.findItem(R.id.nav_MyPage).isChecked = true

        firstInit()

        logoutTv = findViewById(R.id.logoutTv)
        unregisterTv = findViewById(R.id.unregisterTv)

        preferences = getSharedPreferences("Account", Context.MODE_PRIVATE)
        editor = preferences.edit()

        logoutTv.setOnClickListener {
            showConfirmationDialog("로그아웃","로그아웃 하겠습니까?") {
                editor.putString("userID", "")
                editor.putString("userPW", "")
                editor.commit()
                navigateTo(LoginActivity::class.java)
            }
        }

        unregisterTv.setOnClickListener {
            showConfirmationDialog("회원탈퇴","정말 회원탈퇴를 하시겠습니까?") {
                val userID = preferences.getString("saveID", "")
                val callPost = service?.requestUnregister(userID!!)
                callPost?.enqueue(object: Callback<String?> {
                    override fun onResponse(call: Call<String?>, response: Response<String?>) {
                        if (response.isSuccessful) {
                            try {
                                val result = response.body()!!.toString()
                                if(result=="pass") {
                                    editor.remove("userID")
                                    editor.remove("userPW")
                                    editor.commit()
                                    showToast("그 동안 이용해주셔서 감사합니다.")
                                    navigateTo(LoginActivity::class.java)
                                }
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
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showConfirmationDialog(title: String, message: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("확인") { _, _ -> onConfirm.invoke() }
            setNegativeButton("취소") { _, _ -> }
            show()
        }
    }

    private fun navigateTo(destination: Class<*>) {
        startActivity(Intent(this, destination))
        finish()
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
}