package com.example.learnify

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import java.util.Calendar

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setupCountdown()
        setupNavigation()
        setupSearch()
        setupBottomNav()
    }

    private fun setupBottomNav() {
        // Tombol Learn (Kembali ke MainActivity)
        findViewById<LinearLayout>(R.id.navLearn).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            // Bendera ini mencegah Activity baru dibuat jika sudah ada di tumpukan
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        // START: KODE YANG DITAMBAHKAN UNTUK ACHIEVEMENT
        findViewById<LinearLayout>(R.id.navAchievement).setOnClickListener {
            val intent = Intent(this, AchievementActivity::class.java)
            startActivity(intent)
        }
        // END: KODE YANG DITAMBAHKAN UNTUK ACHIEVEMENT

        // START: PERUBAHAN UNTUK PROFILE
        findViewById<LinearLayout>(R.id.navProfile)?.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        // END: PERUBAHAN UNTUK PROFILE
    }

    private fun setupCountdown() {
        val tvCountdown = findViewById<TextView>(R.id.tvCountdown)
        val utbkDate = Calendar.getInstance()
        utbkDate.set(2025, Calendar.MAY, 1)

        val today = Calendar.getInstance()
        val diffMillis = utbkDate.timeInMillis - today.timeInMillis
        val daysLeft = diffMillis / (1000 * 60 * 60 * 24)

        tvCountdown.text = if (daysLeft > 0) daysLeft.toString() else "0"
    }

    private fun setupNavigation() {
        // TAMBAHKAN TOBK KE MAP
        val menuMap = mapOf(
            R.id.itemPU to "Penalaran Umum",
            R.id.itemPK to "Pengetahuan Kuantitatif",
            R.id.itemPBM to "Pemahaman Bacaan & Menulis",
            R.id.itemPPU to "Pengetahuan & Pemahaman Umum",
            R.id.itemLitIndo to "Literasi B.Indonesia",
            R.id.itemLitIng to "Literasi Bahasa Inggris",
            R.id.itemPM to "Penalaran Matematika",
            R.id.itemTOBK to "TOBK" // <--- PENTING
        )

        for ((id, topik) in menuMap) {
            findViewById<CardView>(id).setOnClickListener {
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("NAMA_TOPIK", topik)
                startActivity(intent)
            }
        }
    }

    private fun setupSearch() {
        val etSearch = findViewById<EditText>(R.id.etSearch)
        val container = findViewById<LinearLayout>(R.id.containerTopicList)

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().lowercase()
                for (i in 0 until container.childCount) {
                    val child = container.getChildAt(i)
                    if (child is CardView) {
                        val topicName = child.tag.toString().lowercase()
                        if (topicName.contains(query)) {
                            child.visibility = View.VISIBLE
                        } else {
                            child.visibility = View.GONE
                        }
                    }
                }
            }
        })
    }
}