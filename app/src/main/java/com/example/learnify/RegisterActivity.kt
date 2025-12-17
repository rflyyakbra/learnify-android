package com.example.learnify

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.learnify.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol Back
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Tombol Next -> Pindah ke ProfileOnBoardActivity (Tempat form berada)
        binding.btnNext.setOnClickListener {
            val intent = Intent(this, ProfileOnBoardActivity::class.java)
            startActivity(intent)
        }
    }
}