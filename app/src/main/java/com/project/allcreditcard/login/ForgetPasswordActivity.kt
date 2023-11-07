package com.project.allcreditcard.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.project.allcreditcard.R

class ForgetPasswordActivity : AppCompatActivity() {

    private lateinit var forgetBtn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        forgetBtn = findViewById(R.id.forgetBtn)

        forgetBtn.setOnClickListener {
            startActivity(Intent(this@ForgetPasswordActivity, ChangePasswordActivity::class.java))
            finish()
        }
    }
}