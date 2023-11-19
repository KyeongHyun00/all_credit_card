package com.project.allcreditcard.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.allcreditcard.MainActivity
import com.project.allcreditcard.MyPageActivity
import com.project.allcreditcard.R

class AmountPaymentActivity : AppCompatActivity() {

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this@AmountPaymentActivity, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_CreditPoint -> {
                    startActivity(Intent(this@AmountPaymentActivity, CreditInformationActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_Payment -> true
                else -> {
                    startActivity(Intent(this@AmountPaymentActivity, MyPageActivity::class.java))
                    finish()
                    true
                }
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_amount_payment)

        val navView: BottomNavigationView = findViewById(R.id.paymemt_nav)
        navView.setOnItemSelectedListener(onNavigationItemSelectedListener)
        navView.menu.findItem(R.id.nav_Payment).isChecked = true
    }
}