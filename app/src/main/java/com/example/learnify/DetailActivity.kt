package com.example.learnify

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class DetailActivity : AppCompatActivity() {

    private var namaTopik: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        namaTopik = intent.getStringExtra("NAMA_TOPIK")

        val tvJudul = findViewById<TextView>(R.id.tvTopikTitle)
        tvJudul.text = if (namaTopik == "TOBK") "Try Out UTBK Final" else (namaTopik ?: "Materi Belajar")

        // --- LOGIKA GAMBAR HEADER ---
        val imgHeader = findViewById<ImageView>(R.id.imgHeader)

        when (namaTopik) {
            "Penalaran Umum", "Penalaran Matematika" -> {
                imgHeader.setImageResource(R.drawable.header_penalaran_umum)
            }
            "Literasi B.Indonesia", "Literasi Bahasa Inggris", "Pemahaman Bacaan & Menulis", "Pengetahuan & Pemahaman Umum" -> {
                imgHeader.setImageResource(R.drawable.header_literasi)
            }
            "TOBK" -> {
                // Gunakan header PK atau header khusus jika ada
                imgHeader.setImageResource(R.drawable.header_kuantitatif)
            }
            else -> {
                imgHeader.setImageResource(R.drawable.header_kuantitatif)
            }
        }

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<ImageView>(R.id.btnMenu).setOnClickListener {
            Toast.makeText(this, "Menu segera hadir!", Toast.LENGTH_SHORT).show()
        }

        val cardMateri = findViewById<CardView>(R.id.cardMateri)
        val cardLatihan = findViewById<CardView>(R.id.cardLatihan)
        val cardTryOut = findViewById<CardView>(R.id.cardTryOut)

        // --- KHUSUS TOBK: MATERI & LATIHAN SOAL DINONAKTIFKAN ---
        // Karena TOBK hanya ujian final (Try Out)
        if (namaTopik == "TOBK") {
            cardMateri.alpha = 0.5f
            cardMateri.setOnClickListener {
                Toast.makeText(this, "Materi tidak tersedia untuk TOBK.", Toast.LENGTH_SHORT).show()
            }

            cardLatihan.alpha = 0.5f
            cardLatihan.setOnClickListener {
                Toast.makeText(this, "Latihan soal tidak tersedia untuk TOBK.", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Normal untuk topik lain
            cardMateri.setOnClickListener {
                val intent = Intent(this, ListMateriActivity::class.java)
                intent.putExtra("NAMA_TOPIK", namaTopik)
                startActivity(intent)
            }

            cardLatihan.setOnClickListener {
                val intent = Intent(this, QuizActivity::class.java)
                intent.putExtra("NAMA_TOPIK", namaTopik)
                startActivity(intent)
            }
        }

        // Try Out selalu aktif (termasuk TOBK)
        cardTryOut.setOnClickListener {
            val intent = Intent(this, TryOutActivity::class.java)
            intent.putExtra("NAMA_TOPIK", namaTopik)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        if (namaTopik != "TOBK") {
            checkProgressMateri()
            checkScoreQuiz()
        }
        checkScoreTryOut()
    }

    private fun checkProgressMateri() {
        val sharedPref = getSharedPreferences("LearnifyProgress", Context.MODE_PRIVATE)
        var countDone = 0
        val topikKey = namaTopik ?: "Materi"
        for (i in 0 until 5) {
            if (sharedPref.getBoolean("DONE_${topikKey}_$i", false)) countDone++
        }
        val pbMateri = findViewById<ProgressBar>(R.id.pbMateri)
        val tvCount = findViewById<TextView>(R.id.tvMateriCount)
        pbMateri.max = 5
        pbMateri.progress = countDone
        tvCount.text = "$countDone/5"
    }

    private fun checkScoreQuiz() {
        val sharedPref = getSharedPreferences("LearnifyProgress", Context.MODE_PRIVATE)
        val topikKey = namaTopik ?: "Materi"
        val savedScore = sharedPref.getInt("SCORE_$topikKey", -1)
        val pbLatihan = findViewById<ProgressBar>(R.id.pbLatihan)
        val tvLatihan = findViewById<TextView>(R.id.tvLatihanScore)
        if (savedScore != -1) {
            pbLatihan.max = 100
            pbLatihan.progress = savedScore
            tvLatihan.text = "Skor: $savedScore"
        } else {
            pbLatihan.progress = 0
            tvLatihan.text = "Belum"
        }
    }

    private fun checkScoreTryOut() {
        val sharedPref = getSharedPreferences("LearnifyProgress", Context.MODE_PRIVATE)
        val topikKey = namaTopik ?: "Materi"
        val savedScore = sharedPref.getInt("TRYOUT_SCORE_$topikKey", -1)
        val pbTryOut = findViewById<ProgressBar>(R.id.pbTryOut)
        val tvTryOut = findViewById<TextView>(R.id.tvTryOutScore)

        // Kalau TOBK, max score bisa disesuaikan (misal 600 atau 1000), tapi disini kita pakai 100 dulu
        if (savedScore != -1) {
            pbTryOut.max = if(namaTopik == "TOBK") 600 else 100 // Contoh max score 600
            pbTryOut.progress = savedScore
            tvTryOut.text = "Skor: $savedScore"
        } else {
            pbTryOut.progress = 0
            tvTryOut.text = "Belum"
        }
    }
}