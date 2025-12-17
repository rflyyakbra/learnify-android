package com.example.learnify

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class ListMateriActivity : AppCompatActivity() {

    // KITA BUAT DATA CLASS UNTUK MENYIMPAN INFO MATERI
    data class MateriData(val judul: String, val deskripsi: String, val linkVideo: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_materi)

        val namaTopik = intent.getStringExtra("NAMA_TOPIK") ?: "Materi"

        // Setup UI Dasar
        findViewById<TextView>(R.id.tvHeaderTitle).text = namaTopik
        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        // --- LOGIKA GANTI BACKGROUND HEADER ---
        val imgHeaderBg = findViewById<ImageView>(R.id.imgHeaderBg)

        when (namaTopik) {
            "Penalaran Umum", "Penalaran Matematika" -> {
                imgHeaderBg.setImageResource(R.drawable.bg_header_pu)
            }
            "Literasi B.Indonesia", "Literasi Bahasa Inggris", "Pemahaman Bacaan & Menulis", "Pengetahuan & Pemahaman Umum" -> {
                imgHeaderBg.setImageResource(R.drawable.bg_header_lit)
            }
            else -> {
                imgHeaderBg.setImageResource(R.drawable.bg_header_pk) // Default PK
            }
        }

        // --- 1. BANK MATERI 7 TOPIK ---
        val listMateri = when (namaTopik) {
            "Penalaran Umum" -> listOf(
                MateriData("Logika Deduktif", "Silogisme & Kesimpulan", "https://www.youtube.com/watch?v=uv9X26Gzfyk"),
                MateriData("Logika Induktif", "Generalisasi & Analogi", "https://www.youtube.com/watch?v=Nb4P8Xs2N_w"),
                MateriData("Pola Gambar", "Deret & Rotasi", "https://youtu.be/y2M8wwg9lww?si=TvKMYB5atFSxiFFB"),
                MateriData("Logika Analitik", "Urutan & Posisi", "https://www.youtube.com/watch?v=uv9X26Gzfyk"),
                MateriData("Kesesuaian Pernyataan", "Membedah Teks", "https://www.youtube.com/watch?v=gtXJcgyk8kA")
            )
            "Literasi B.Indonesia" -> listOf(
                MateriData("Ide Pokok", "Gagasan Utama", "https://www.youtube.com/watch?v=bsW1hPDZwbs"),
                MateriData("Makna Kata", "Sinonim & Konteks", "https://www.youtube.com/watch?v=pzPCcOIlI5A"),
                MateriData("Kalimat Efektif", "PUEBI", "https://www.youtube.com/watch?v=mMK9d62KHYk"),
                MateriData("Simpulan Teks", "Ringkasan", "https://youtu.be/7oEVGzHMyvY?si=LZR3b5a9sNFE9VNT"),
                MateriData("Opini & Fakta", "Membedakan Informasi", "https://www.youtube.com/watch?v=BpX1CCFWikk")
            )
            "Pemahaman Bacaan & Menulis" -> listOf(
                MateriData("Ejaan (PUEBI)", "Huruf Kapital & Miring", "https://www.youtube.com/watch?v=_mA8ALF0njs"),
                MateriData("Tanda Baca", "Titik, Koma, Titik Dua", "https://www.youtube.com/watch?v=_mA8ALF0njs"),
                MateriData("Kalimat Efektif", "Subjek, Predikat, Hemat", "https://www.youtube.com/watch?v=mMK9d62KHYk"),
                MateriData("Konjungsi", "Kata Hubung Antarkalimat", "https://www.youtube.com/watch?v=qFAtK5upkqY"),
                MateriData("Simpulan Paragraf", "Menarik Intisari", "https://www.youtube.com/watch?v=bsW1hPDZwbs")
            )
            "Pengetahuan & Pemahaman Umum" -> listOf(
                MateriData("Makna Kata", "Denotasi & Konotasi", "https://www.youtube.com/watch?v=nIz-qn4xcGk"),
                MateriData("Imbuhan", "Awalan, Akhiran, Sisipan", "https://www.youtube.com/watch?v=a9KBK0BoksM"),
                MateriData("Hubungan Kata", "Sinonim & Antonim", "https://www.youtube.com/watch?v=Ng9R8PTzfC4"),
                MateriData("Frasa", "Kelompok Kata", "https://www.youtube.com/watch?v=Ng9R8PTzfC4"),
                MateriData("Rujukan Kata", "Kata Ganti & Penunjuk", "https://www.youtube.com/watch?v=y27hQFcfkZs")
            )
            "Literasi Bahasa Inggris" -> listOf(
                MateriData("Main Idea", "Topic & Main Point", "https://youtu.be/qpZW6ZzNAeA?si=12bbwHPkrHQfQqV8"),
                MateriData("Explicit Info", "Scanning Details", "https://youtu.be/2RzVuxsxjpk?si=YNHVFU5d8pDhZagU"),
                MateriData("Implicit Info", "Inference & Conclusion", "https://youtu.be/2KyoUyf4DJ8?si=FiUSGdq0yqLOThiz"),
                MateriData("Vocabulary", "Synonym in Context", "https://youtu.be/v6NmetUyDyg?si=SlKMpUg9ujoeGNQE"),
                MateriData("Author's Attitude", "Tone & Purpose", "https://youtu.be/ArH6BUO9Ajk?si=rc7bgFm1ni0-Lgzc")
            )
            "Penalaran Matematika" -> listOf(
                MateriData("Aritmatika Sosial", "Untung, Rugi, Diskon", "https://www.youtube.com/watch?v=XZ5Yj2zyiN0"),
                MateriData("Perbandingan", "Senilai & Berbalik Nilai", "https://www.youtube.com/watch?v=jxEhD5v2YOQ"),
                MateriData("Geometri Praktis", "Luas Tanah & Volume", "https://www.youtube.com/watch?v=PndRnk8wqJc"),
                MateriData("Statistika Data", "Membaca Grafik & Tabel", "https://www.youtube.com/watch?v=MCvRs3eaobA"),
                MateriData("Peluang Kejadian", "Kemungkinan Peristiwa", "https://www.youtube.com/watch?v=qIkrAB0uzmo")
            )
            else -> listOf( // Default: Pengetahuan Kuantitatif
                MateriData("Bilangan", "Operasi Hitung", "https://www.youtube.com/watch?v=xVJySCHEVpA"),
                MateriData("Aljabar", "Persamaan Linear", "https://www.youtube.com/watch?v=JNJTHI7jIKU"),
                MateriData("Geometri", "Bangun Datar", "https://www.youtube.com/watch?v=8wlnCpvMGns"),
                MateriData("Statistika", "Mean, Median, Modus", "https://www.youtube.com/watch?v=SEoRbkhXOA4"),
                MateriData("Peluang", "Permutasi & Kombinasi", "https://www.youtube.com/watch?v=BqM9SbVyt0w")
            )
        }

        // --- 2. SETUP UI LOOPING ---
        val cardIds = listOf(R.id.cardMateri1, R.id.cardMateri2, R.id.cardMateri3, R.id.cardMateri4, R.id.cardMateri5)
        val titleIds = listOf(R.id.tvTitle1, R.id.tvTitle2, R.id.tvTitle3, R.id.tvTitle4, R.id.tvTitle5)
        val descIds = listOf(R.id.tvDesc1, R.id.tvDesc2, R.id.tvDesc3, R.id.tvDesc4, R.id.tvDesc5)
        val iconIds = listOf(R.id.imgCheck1, R.id.imgCheck2, R.id.imgCheck3, R.id.imgCheck4, R.id.imgCheck5)

        for (i in listMateri.indices) {
            // Set Teks
            findViewById<TextView>(titleIds[i]).text = listMateri[i].judul
            findViewById<TextView>(descIds[i]).text = listMateri[i].deskripsi

            // Cek Status (Centang Hijau)
            val isDone = loadStatus(namaTopik, i)
            if (isDone) {
                val iconView = findViewById<ImageView>(iconIds[i])
                iconView.setImageResource(android.R.drawable.checkbox_on_background)
                iconView.setColorFilter(resources.getColor(android.R.color.holo_green_dark))
            }

            // Klik Card
            findViewById<CardView>(cardIds[i]).setOnClickListener {
                saveStatus(namaTopik, i)
                updateProgressUI(namaTopik)

                // Ubah jadi centang
                val iconView = findViewById<ImageView>(iconIds[i])
                iconView.setImageResource(android.R.drawable.checkbox_on_background)
                iconView.setColorFilter(resources.getColor(android.R.color.holo_green_dark))

                // Buka Youtube
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(listMateri[i].linkVideo))
                startActivity(intent)
            }
        }

        updateProgressUI(namaTopik)
    }

    private fun saveStatus(topik: String, index: Int) {
        val sharedPref = getSharedPreferences("LearnifyProgress", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("DONE_${topik}_$index", true)
        editor.apply()
    }

    private fun loadStatus(topik: String, index: Int): Boolean {
        val sharedPref = getSharedPreferences("LearnifyProgress", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("DONE_${topik}_$index", false)
    }

    private fun updateProgressUI(topik: String) {
        var countDone = 0
        for (i in 0 until 5) {
            if (loadStatus(topik, i)) countDone++
        }
        val progressBar = findViewById<ProgressBar>(R.id.progressBarMateri)
        val tvProgress = findViewById<TextView>(R.id.tvProgressCount)
        progressBar.progress = countDone
        tvProgress.text = "$countDone/5 Selesai"
    }
}