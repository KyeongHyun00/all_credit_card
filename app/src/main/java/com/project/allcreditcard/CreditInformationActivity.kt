package com.project.allcreditcard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView

class CreditInformationActivity : AppCompatActivity() {

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this@CreditInformationActivity, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_CreditPoint -> true
                R.id.nav_MyPage -> {
                    startActivity(Intent(this@CreditInformationActivity, MyPageActivity::class.java))
                    finish()
                    true
                }
                else -> {
                    startActivity(Intent(this@CreditInformationActivity, SettingActivity::class.java))
                    finish()
                    true
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_information)

        val navView: BottomNavigationView = findViewById(R.id.creditscore_nav)
        navView.setOnItemSelectedListener(onNavigationItemSelectedListener)
        navView.menu.findItem(R.id.nav_CreditPoint).isChecked = true
    }
}