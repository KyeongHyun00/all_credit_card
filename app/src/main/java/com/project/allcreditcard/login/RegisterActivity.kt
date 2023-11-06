package com.project.allcreditcard.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.project.allcreditcard.R
import org.w3c.dom.Text

class RegisterActivity : AppCompatActivity() {

    var IsExistBlank = false
    var IsPwCheck = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val name : EditText = findViewById(R.id.join_name)
        val id : EditText = findViewById(R.id.join_id)
        val regist_num : EditText = findViewById(R.id.join_regist_numr)
        val phone_num : EditText = findViewById(R.id.join_phone_num)
        val certi_num : EditText = findViewById(R.id.certi_num)
        val pw : EditText = findViewById(R.id.join_pw)
        val pwCheck : EditText = findViewById(R.id.join_pwCheck)

        val join_button : Button = findViewById<Button>(R.id.join_button)

        join_button.setOnClickListener{
            val join_name : String = name.getText().toString()
            val join_id : String = id.getText().toString()
            val join_regist_num : String = regist_num.getText().toString()
            val join_phone_num : String = phone_num.getText().toString()
            val join_certi_num : String = certi_num.getText().toString()
            val join_pw : String = pw.getText().toString()
            val join_pwCheck : String = pwCheck.getText().toString()

            if(join_name.isEmpty() || join_id.isEmpty() || join_regist_num.isEmpty()||
                join_phone_num.isEmpty() || join_certi_num.isEmpty() || join_pw.isEmpty()){
                IsExistBlank = true
            }else{
                if(join_pw == join_pwCheck){
                    IsPwCheck = true
                }
            }

            if(!IsExistBlank && IsPwCheck){
                Toast.makeText(this, " 성공 테레비 ", Toast.LENGTH_SHORT).show()
            }
        }

    }
}