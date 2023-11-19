package com.project.allcreditcard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.allcreditcard.account.AmountPaymentActivity
import com.project.allcreditcard.account.CreditInformationActivity

class MyPageActivity : AppCompatActivity() {

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this@MyPageActivity, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_CreditPoint -> {
                    startActivity(Intent(this@MyPageActivity, CreditInformationActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_Payment -> {
                    startActivity(Intent(this@MyPageActivity, AmountPaymentActivity::class.java))
                    finish()
                    true
                }
                else -> true
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        val navView: BottomNavigationView = findViewById(R.id.mypage_nav)
        navView.setOnItemSelectedListener(onNavigationItemSelectedListener)
        navView.menu.findItem(R.id.nav_MyPage).isChecked = true
    }
}