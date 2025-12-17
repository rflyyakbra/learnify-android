package com.example.learnify

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu // Import Wajib untuk Menu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
// Import Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class ProfileActivity : AppCompatActivity() {

    // 1. Variabel Firebase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // 2. Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()

        setupNavigation()
        setupEditName()
        setupMenuAction()
    }

    override fun onResume() {
        super.onResume()
        loadStatistics() // Refresh data statistik & nama saat halaman dibuka kembali
    }

    private fun loadStatistics() {
        val sharedPref = getSharedPreferences("LearnifyProgress", Context.MODE_PRIVATE)
        val tvName = findViewById<TextView>(R.id.tvName)
        val tvMateri = findViewById<TextView>(R.id.tvStatMateri)
        val tvBadge = findViewById<TextView>(R.id.tvStatBadge)
        val tvScore = findViewById<TextView>(R.id.tvStatScore)

        // --- BAGIAN LOAD NAMA (FIREBASE PRIORITY) ---
        val currentUser = auth.currentUser

        // Logika: Cek Firebase dulu. Jika ada nama di Firebase, pakai itu.
        // Jika tidak ada (null), baru pakai data lokal atau default "Pejuang UTBK"
        if (currentUser != null && !currentUser.displayName.isNullOrEmpty()) {
            tvName.text = currentUser.displayName
        } else {
            val savedName = sharedPref.getString("USER_NAME", "Pejuang UTBK")
            tvName.text = savedName
        }
        // ---------------------------------------------

        // 2. HITUNG TOTAL MATERI SELESAI
        var totalMateri = 0
        val allTopics = listOf(
            "Penalaran Umum", "Pengetahuan Kuantitatif",
            "Pemahaman Bacaan & Menulis", "Pengetahuan & Pemahaman Umum",
            "Literasi B.Indonesia", "Literasi Bahasa Inggris", "Penalaran Matematika"
        )

        for (topik in allTopics) {
            for (i in 0 until 5) {
                if (sharedPref.getBoolean("DONE_${topik}_$i", false)) totalMateri++
            }
        }
        tvMateri.text = totalMateri.toString()

        // 3. HITUNG ACHIEVEMENT
        var badgeCount = 0
        if (totalMateri >= 5) badgeCount++
        if (totalMateri >= 10) badgeCount++
        if (totalMateri >= 15) badgeCount++
        if (totalMateri >= 20) badgeCount++
        tvBadge.text = badgeCount.toString()

        // 4. CARI NILAI TERTINGGI (HIGH SCORE)
        var maxScore = 0
        for (topik in allTopics) {
            val score = sharedPref.getInt("SCORE_$topik", 0)
            if (score > maxScore) maxScore = score
        }
        val tobkScore = sharedPref.getInt("TRYOUT_SCORE_TOBK", 0)
        if (tobkScore > maxScore) maxScore = tobkScore

        tvScore.text = maxScore.toString()
    }

    // Setup tombol Pensil kecil di sebelah nama
    private fun setupEditName() {
        val btnEdit = findViewById<ImageView>(R.id.btnEditName)
        btnEdit.setOnClickListener {
            showEditNameDialog() // Panggil fungsi dialog yang sama
        }
    }

    // Logic Popup Menu untuk Tombol Settings (Garis Tiga/Gerigi)
    private fun setupMenuAction() {
        val btnSettings = findViewById<ImageView>(R.id.btnSettings)

        btnSettings.setOnClickListener { view ->
            // Membuat Popup Menu
            val popup = PopupMenu(this, view)
            // Pastikan file res/menu/menu_profile_options.xml sudah dibuat
            popup.menuInflater.inflate(R.menu.menu_profile_options, popup.menu)

            // Aksi saat item menu diklik
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_edit_name -> {
                        showEditNameDialog() // Buka dialog edit nama
                        true
                    }
                    R.id.action_logout -> {
                        showLogoutDialog() // Buka dialog logout
                        true
                    }
                    else -> false
                }
            }
            // Tampilkan menu
            popup.show()
        }

        // Klik Menu Achievement di Dashboard (Card Kuning)
        findViewById<LinearLayout>(R.id.btnMenuAchievement).setOnClickListener {
            startActivity(Intent(this, AchievementActivity::class.java))
        }

        // Tombol Back
        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }
    }

    // Dialog Edit Nama (Dipisah agar bisa dipanggil dari Pensil maupun Menu)
    private fun showEditNameDialog() {
        val currentName = findViewById<TextView>(R.id.tvName).text.toString()
        val editText = EditText(this)
        editText.setText(currentName)

        AlertDialog.Builder(this)
            .setTitle("Ubah Nama Profil")
            .setView(editText)
            .setPositiveButton("Simpan") { _, _ ->
                val newName = editText.text.toString().trim()
                if (newName.isNotEmpty()) {
                    updateNameInFirebase(newName)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    // Fungsi Update Nama ke Server Firebase
    private fun updateNameInFirebase(newName: String) {
        val user = auth.currentUser

        // 1. Update ke Firebase Profile
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newName)
            .build()

        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 2. Update Tampilan Langsung
                    findViewById<TextView>(R.id.tvName).text = newName

                    // 3. Simpan juga ke Local (Backup)
                    val sharedPref = getSharedPreferences("LearnifyProgress", Context.MODE_PRIVATE)
                    sharedPref.edit().putString("USER_NAME", newName).apply()

                    Toast.makeText(this, "Nama berhasil diubah!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Gagal mengubah nama.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Dialog Konfirmasi Logout
    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Apakah Anda yakin ingin keluar akun?")
            .setPositiveButton("Ya, Keluar") { _, _ ->
                auth.signOut() // Logout Firebase

                // Kembali ke Login Activity & Hapus History Stack
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun setupNavigation() {
        findViewById<LinearLayout>(R.id.navLearn).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TOP })
        }
        findViewById<LinearLayout>(R.id.navSearch).setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.navAchievement).setOnClickListener {
            startActivity(Intent(this, AchievementActivity::class.java))
        }
    }
}