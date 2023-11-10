package com.project.allcreditcard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast

class TermsOfUseActivity : AppCompatActivity() {

    private lateinit var essentialCheck: CheckBox
    private lateinit var doubleCheck: CheckBox
    private lateinit var allCheck: CheckBox
    private lateinit var okBtn: Button
    private lateinit var cancelBtn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_of_use)

        essentialCheck = findViewById(R.id.essentialCheck)
        doubleCheck = findViewById(R.id.doubleCheck)
        allCheck = findViewById(R.id.all_check)
        okBtn = findViewById(R.id.successButton)
        cancelBtn = findViewById(R.id.cancelButton)

        allCheck.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                essentialCheck.isChecked = true
                doubleCheck.isChecked = true
            }
            else {
                essentialCheck.isChecked = false
                doubleCheck.isChecked = false
            }
        }

        okBtn.setOnClickListener {
            if(essentialCheck.isChecked) {
                Toast.makeText(this@TermsOfUseActivity, "모두 체크확인", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this@TermsOfUseActivity, "약관을 모두 동의해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
        cancelBtn.setOnClickListener {
            finish()
        }
    }
}