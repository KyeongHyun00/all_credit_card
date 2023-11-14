package com.project.allcreditcard

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.project.allcreditcard.login.LoginActivity

class LoadingActivity : AppCompatActivity() {

    private lateinit var loadingAnimation: LottieAnimationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        loadingAnimation = findViewById(R.id.lottieLoad)

        loadingAnimation.addAnimatorListener(object: Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {}
            override fun onAnimationEnd(p0: Animator) {}
            override fun onAnimationCancel(p0: Animator) {}
            override fun onAnimationRepeat(p0: Animator) {
                Handler().postDelayed({
                    startActivity(Intent(this@LoadingActivity, LoginActivity::class.java))
                    finish()
                }, 2000)
            }


        })
    }
}