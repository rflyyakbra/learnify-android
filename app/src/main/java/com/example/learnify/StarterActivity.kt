package com.example.learnify

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class StarterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starter)

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val dotsLayout = findViewById<LinearLayout>(R.id.dotsLayout)
        val btnRegister = findViewById<Button>(R.id.btn_register)
        val btnLogin = findViewById<Button>(R.id.btn_login)

        val titles = listOf(
            "Learn everything for you for all time",
            "Upgrade Your Skills",
            "Learn Anytime Anywhere"
        )

        val descs = listOf(
            "Mari belajar dan berkembang bersama Learnify untuk masa depan yang lebih baik",
            "Materi terbaik untuk menunjang kemampuan kamu",
            "Belajar fleksibel kapan saja dan di mana saja"
        )

        viewPager.adapter = OnboardingAdapter(titles, descs)

        val dots = Array(titles.size) { View(this) }

        dots.forEachIndexed { index, dot ->
            val params = LinearLayout.LayoutParams(
                if (index == 0) 24 else 8,
                8
            )
            params.marginEnd = 8
            dot.layoutParams = params
            dot.setBackgroundResource(
                if (index == 0) R.drawable.dot_active
                else R.drawable.dot_inactive
            )
            dotsLayout.addView(dot)
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                dots.forEachIndexed { index, dot ->
                    val params = dot.layoutParams as LinearLayout.LayoutParams
                    if (index == position) {
                        params.width = 24
                        dot.setBackgroundResource(R.drawable.dot_active)
                    } else {
                        params.width = 8
                        dot.setBackgroundResource(R.drawable.dot_inactive)
                    }
                    dot.layoutParams = params
                }
            }
        })

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
