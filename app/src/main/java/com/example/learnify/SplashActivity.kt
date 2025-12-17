package com.example.learnify

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import kotlin.jvm.java

class SplashActivity : AppCompatActivity() {
    private val SPLASH_DELAY = 1800L // 1.8 detik, sesuaikan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // pindah ke Starter setelah delay
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@SplashActivity, StarterActivity::class.java))
            finish()
        }, SPLASH_DELAY)
    }
}
