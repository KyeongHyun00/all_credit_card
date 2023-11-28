package com.project.allcreditcard

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.project.allcreditcard.login.LoginActivity
import com.project.allcreditcard.utility.APIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException

class LoadingActivity : AppCompatActivity() {

    private lateinit var stateTv: TextView
    private lateinit var statePb: ProgressBar
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var retrofit: Retrofit
    private var service: APIService? = null
    private val url = BuildConfig.SERVER_IP
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        firstInit()

        stateTv = findViewById(R.id.stateTv)
        statePb = findViewById(R.id.statePb)

        if (internetCheck(this)) {
            handler.postDelayed({
                statePb.progress = 33
                serverCheck { isConnected ->
                    if (isConnected) {
                        handler.postDelayed({
                            statePb.progress = 66
                            handler.postDelayed({
                                statePb.progress = 100
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            }, 500)
                        }, 500)
                    } else {
                        alertMsgShow("서버 연결", "서버와 연결을 실패했습니다.\n어플을 재시작 해주세요")
                    }
                }
            }, 1000)
        } else {
            alertMsgShow("인터넷 연결", "인터넷 연결을 확인하세요")
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

    private fun alertMsgShow(title: String, content: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(content)
            .setCancelable(false)
            .setPositiveButton("확인") { _, _ ->
                finish()
            }
        builder.show()
    }

    private fun internetCheck(context: Context): Boolean {
        stateTv.text = "인터넷 연결 확인중"
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                stateTv.text = "인터넷 연결 확인"
                true
            }

            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                stateTv.text = "인터넷 연결 확인"
                true
            }

            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                stateTv.text = "인터넷 연결 확인"
                true
            }

            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> {
                stateTv.text = "인터넷 연결 확인"
                true
            }

            else -> {
                stateTv.text = "인터넷 연결 실패"
                false
            }
        }
    }

    private fun serverCheck(completion: (Boolean) -> Unit) {
        var serverBoolean = false
        stateTv.text = "서버 연결 확인중"
        val callPost = service?.requestConnect("00")
        callPost?.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if (response.isSuccessful) {
                    try {
                        val result = response.body()!!.toString()
                        if (result == "conPass") {
                            stateTv.text = "서버 연결 확인"
                            serverBoolean = true
                        } else if (result == "conFail") {
                            serverBoolean = false
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        serverBoolean = false
                    }
                }
                completion(serverBoolean)
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                completion(false)
            }
        })
    }
}