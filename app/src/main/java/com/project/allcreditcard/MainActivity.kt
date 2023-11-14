package com.project.allcreditcard

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.allcreditcard.account.CreditInformationActivity

class MainActivity : AppCompatActivity() {

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_CreditPoint -> {
                    startActivity(Intent(this@MainActivity, CreditInformationActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_MyPage -> {
                    startActivity(Intent(this@MainActivity, MyPageActivity::class.java))
                    finish()
                    true
                }
                else -> {
                    startActivity(Intent(this@MainActivity, SettingActivity::class.java))
                    finish()
                    true
                }
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.main_nav)
        navView.setOnItemSelectedListener(onNavigationItemSelectedListener)
        navView.menu.findItem(R.id.nav_home).isChecked = true
    }
}