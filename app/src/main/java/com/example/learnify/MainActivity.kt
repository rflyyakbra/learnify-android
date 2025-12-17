package com.example.learnify

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView // Tambahan import
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
// Tambahan Import Firebase
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    // 1. Variabel Firebase Auth
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 2. Inisialisasi Firebase Auth
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        // 3. Cek Login: Jika belum login, lempar ke LoginActivity
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Tutup MainActivity agar user tidak bisa back ke sini
            return
        }

        // 4. LOGIKA GANTI NAMA (UPDATE UI)
        // Ambil ID tvWelcome dari XML kamu
        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)

        // Ambil nama dari Firebase (DisplayName). Jika kosong, pakai "Siswa"
        val userName = currentUser.displayName ?: "Siswa"

        // Ubah teksnya
        tvWelcome.text = "Halo, $userName!"


        // --- NAVIGASI BAWAH (BOTTOM NAV) ---

        // Tombol Search
        val navSearch = findViewById<LinearLayout>(R.id.navSearch)
        navSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        // Tombol Achievement
        findViewById<LinearLayout>(R.id.navAchievement).setOnClickListener {
            startActivity(Intent(this, AchievementActivity::class.java))
        }

        // Tombol Profile
        findViewById<LinearLayout>(R.id.navProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // --- MENU TOPIK (KARTU) ---
        val cardPU = findViewById<CardView>(R.id.cardPU)
        val cardPK = findViewById<CardView>(R.id.cardPK)
        val cardLit = findViewById<CardView>(R.id.cardLit)

        cardPU.setOnClickListener { openDetail("Penalaran Umum") }
        cardPK.setOnClickListener { openDetail("Pengetahuan Kuantitatif") }
        cardLit.setOnClickListener { openDetail("Literasi B.Indonesia") }
    }

    private fun openDetail(topik: String) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("NAMA_TOPIK", topik)
        startActivity(intent)
    }
}