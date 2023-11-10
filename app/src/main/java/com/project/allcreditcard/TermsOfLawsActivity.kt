package com.project.allcreditcard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast

class TermsOfLawsActivity : AppCompatActivity() {

    private lateinit var essentialCheckOne: CheckBox
    private lateinit var essentialCheckTwo: CheckBox
    private lateinit var essentialCheckThree: CheckBox
    private lateinit var allCheck: CheckBox
    private lateinit var okBtn: Button
    private lateinit var cancelBtn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_of_laws)

        essentialCheckOne = findViewById(R.id.essentialCheckOne)
        essentialCheckTwo = findViewById(R.id.essentialCheckTwo)
        essentialCheckThree = findViewById(R.id.essentialCheckThree)
        allCheck = findViewById(R.id.all_check)
        okBtn = findViewById(R.id.successButton)
        cancelBtn = findViewById(R.id.cancelButton)

        allCheck.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                essentialCheckOne.isChecked = true
                essentialCheckTwo.isChecked = true
                essentialCheckThree.isChecked = true
            }
            else {
                essentialCheckOne.isChecked = false
                essentialCheckTwo.isChecked = false
                essentialCheckThree.isChecked = false
            }
        }

        okBtn.setOnClickListener {
            if(essentialCheckOne.isChecked && essentialCheckTwo.isChecked && essentialCheckThree.isChecked) {
                Toast.makeText(this@TermsOfLawsActivity, "모두 체크확인", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@TermsOfLawsActivity, TermsOfUseActivity::class.java))
                finish()
            }
            else {
                Toast.makeText(this@TermsOfLawsActivity, "약관을 모두 동의해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
        cancelBtn.setOnClickListener {
            finish()
        }
    }
}