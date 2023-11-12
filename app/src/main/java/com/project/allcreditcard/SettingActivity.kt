package com.project.allcreditcard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingActivity : AppCompatActivity() {

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this@SettingActivity, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_CreditPoint -> {
                    startActivity(Intent(this@SettingActivity, CreditInformationActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_MyPage -> {
                    startActivity(Intent(this@SettingActivity, MyPageActivity::class.java))
                    finish()
                    true
                }
                else -> true
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val navView: BottomNavigationView = findViewById(R.id.setting_nav)
        navView.setOnItemSelectedListener(onNavigationItemSelectedListener)
        navView.menu.findItem(R.id.nav_Setting).isChecked = true
    }
}