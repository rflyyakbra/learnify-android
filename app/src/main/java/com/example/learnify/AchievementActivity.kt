package com.example.learnify

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class AchievementActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievement)

        setupNavigation()
        setupTargetScore()
        setupChart()
        setupAchievements()
    }

    private fun setupNavigation() {
        // Tombol Learn (Home)
        findViewById<LinearLayout>(R.id.navLearn).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
        // Tombol Search
        findViewById<LinearLayout>(R.id.navSearch).setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        // START: PERUBAHAN UNTUK PROFILE
        findViewById<LinearLayout>(R.id.navProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        // END: PERUBAHAN UNTUK PROFILE
    }

    private fun setupTargetScore() {
        val sharedPref = getSharedPreferences("LearnifyProgress", Context.MODE_PRIVATE)
        val etTarget = findViewById<EditText>(R.id.etTarget)
        val btnSave = findViewById<Button>(R.id.btnSaveTarget)
        val tvGap = findViewById<TextView>(R.id.tvGapInfo)

        // Load Target yang sudah disimpan
        val savedTarget = sharedPref.getInt("TARGET_SCORE", 0)
        if (savedTarget > 0) etTarget.setText(savedTarget.toString())

        // Load Skor TOBK Terakhir
        val currentScore = sharedPref.getInt("TRYOUT_SCORE_TOBK", 0)

        updateGapText(tvGap, currentScore, savedTarget)

        btnSave.setOnClickListener {
            val input = etTarget.text.toString()
            if (input.isNotEmpty()) {
                val target = input.toInt()
                sharedPref.edit().putInt("TARGET_SCORE", target).apply()
                Toast.makeText(this, "Target Disimpan!", Toast.LENGTH_SHORT).show()

                // Refresh Grafik & Text
                setupChart()
                updateGapText(tvGap, currentScore, target)
            }
        }
    }

    private fun updateGapText(tv: TextView, current: Int, target: Int) {
        if (target == 0) {
            tv.text = "Tentukan targetmu!"
            tv.setTextColor(Color.GRAY)
            return
        }

        if (current >= target) {
            tv.text = "Selamat! Target Tercapai! (+${current - target})"
            tv.setTextColor(Color.parseColor("#4CAF50")) // Hijau
        } else {
            tv.text = "Kurang ${target - current} poin lagi menuju target."
            tv.setTextColor(Color.parseColor("#F44336")) // Merah
        }
    }

    private fun setupChart() {
        val chart = findViewById<LineChart>(R.id.chartTOBK)
        val sharedPref = getSharedPreferences("LearnifyProgress", Context.MODE_PRIVATE)

        // 1. Ambil Skor TOBK
        val scoreTOBK = sharedPref.getInt("TRYOUT_SCORE_TOBK", 0)
        val targetScore = sharedPref.getInt("TARGET_SCORE", 0)

        // 2. Buat Data Dummy (Seolah-olah ada history) + Data Asli
        // Karena kita baru simpan 1 skor, kita buat grafik naik turun dikit biar bagus
        val entries = ArrayList<Entry>()
        entries.add(Entry(1f, 300f)) // TOBK 1 (Dummy)
        entries.add(Entry(2f, 450f)) // TOBK 2 (Dummy)
        entries.add(Entry(3f, scoreTOBK.toFloat())) // TOBK 3 (Asli)

        val dataSet = LineDataSet(entries, "Nilai TOBK")
        dataSet.color = Color.parseColor("#673AB7")
        dataSet.valueTextColor = Color.BLACK
        dataSet.lineWidth = 2f
        dataSet.circleRadius = 4f
        dataSet.setCircleColor(Color.parseColor("#673AB7"))

        // 3. Garis Target (Limit Line)
        chart.axisLeft.removeAllLimitLines() // Hapus yang lama
        if (targetScore > 0) {
            val limitLine = LimitLine(targetScore.toFloat(), "Target: $targetScore")
            limitLine.lineWidth = 2f
            limitLine.lineColor = Color.parseColor("#F44336") // Merah
            limitLine.textColor = Color.parseColor("#F44336")
            chart.axisLeft.addLimitLine(limitLine)
        }

        // 4. Set Data ke Chart
        val lineData = LineData(dataSet)
        chart.data = lineData
        chart.description.isEnabled = false
        chart.xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
        chart.animateY(1000)
        chart.invalidate() // Refresh
    }

    private fun setupAchievements() {
        val sharedPref = getSharedPreferences("LearnifyProgress", Context.MODE_PRIVATE)

        // 1. HITUNG TOTAL MATERI SELESAI
        // Kita loop semua kemungkinan key. (Ini cara sederhana karena kita tidak pakai database SQL)
        var totalCompleted = 0
        val allTopics = listOf(
            "Penalaran Umum", "Pengetahuan Kuantitatif", "Pemahaman Bacaan & Menulis",
            "Pengetahuan & Pemahaman Umum", "Literasi B.Indonesia",
            "Literasi Bahasa Inggris", "Penalaran Matematika"
        )

        // Cek setiap materi (0-4) di setiap topik
        for (topik in allTopics) {
            for (i in 0 until 5) {
                if (sharedPref.getBoolean("DONE_${topik}_$i", false)) {
                    totalCompleted++
                }
            }
        }

        // 2. UNLOCK ACHIEVEMENTS
        var unlockedCount = 0

        // Studious (5 Materi)
        if (totalCompleted >= 5) {
            unlockUI(R.id.cardAch1, R.id.imgAch1, R.id.tvTitleAch1)
            unlockedCount++
        }

        // Quickie (10 Materi)
        if (totalCompleted >= 10) {
            unlockUI(R.id.cardAch2, R.id.imgAch2, R.id.tvTitleAch2)
            unlockedCount++
        }

        // Ambitious (15 Materi)
        if (totalCompleted >= 15) {
            unlockUI(R.id.cardAch3, R.id.imgAch3, R.id.tvTitleAch3)
            unlockedCount++
        }

        // Perfectionist (20 Materi)
        if (totalCompleted >= 20) {
            unlockUI(R.id.cardAch4, R.id.imgAch4, R.id.tvTitleAch4)
            unlockedCount++
        }

        // Update Total Badge di Header
        val tvBadge = findViewById<TextView>(R.id.tvTotalBadge)
        tvBadge.text = "$unlockedCount/4 Terbuka"
    }

    private fun unlockUI(cardId: Int, imgId: Int, textId: Int) {
        val card = findViewById<CardView>(cardId)
        val img = findViewById<ImageView>(imgId)
        val text = findViewById<TextView>(textId)

        card.setCardBackgroundColor(Color.WHITE) // Jadi Putih Terang
        img.setColorFilter(Color.parseColor("#FFC107")) // Jadi Emas
        text.setTextColor(Color.BLACK) // Teks Hitam
    }
}