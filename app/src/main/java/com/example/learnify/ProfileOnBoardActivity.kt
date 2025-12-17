package com.example.learnify

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
// Import Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class ProfileOnBoardActivity : AppCompatActivity() {

    // Komponen UI
    private lateinit var btnBack: View
    private lateinit var etName: TextInputEditText
    private lateinit var etAge: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var tvPasswordStrengthValue: TextView
    private lateinit var btnStart: MaterialButton
    private lateinit var tvLogin: TextView

    // Variabel Firebase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_onboard)

        // 1. Inisialisasi Firebase Auth
        auth = FirebaseAuth.getInstance()

        initViews()
        setupListeners()
        setupPasswordStrengthChecker()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack1)
        etName = findViewById(R.id.etName1)
        etAge = findViewById(R.id.etAge1)
        etEmail = findViewById(R.id.etEmail1)
        etPassword = findViewById(R.id.etPassword1)
        tvPasswordStrengthValue = findViewById(R.id.tvPasswordStrengthValue1)
        btnStart = findViewById(R.id.btnStart1)
        tvLogin = findViewById(R.id.tvLogin1)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnStart.setOnClickListener {
            validateAndSubmit()
        }

        tvLogin.setOnClickListener {
            // Arahkan ke LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupPasswordStrengthChecker() {
        etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updatePasswordStrength(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updatePasswordStrength(password: String) {
        when {
            password.isEmpty() -> {
                tvPasswordStrengthValue.text = ""
                tvPasswordStrengthValue.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
            }
            password.length < 6 -> {
                tvPasswordStrengthValue.text = "Weak"
                tvPasswordStrengthValue.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            }
            password.length < 8 -> {
                tvPasswordStrengthValue.text = "Medium"
                tvPasswordStrengthValue.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark))
            }
            isStrongPassword(password) -> {
                tvPasswordStrengthValue.text = "Super Strong"
                tvPasswordStrengthValue.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
            }
            else -> {
                tvPasswordStrengthValue.text = "Strong"
                tvPasswordStrengthValue.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_light))
            }
        }
    }

    private fun isStrongPassword(password: String): Boolean {
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }
        return password.length >= 8 && hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar
    }

    private fun validateAndSubmit() {
        val name = etName.text.toString().trim()
        val age = etAge.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        when {
            name.isEmpty() -> { etName.error = "Please enter your name"; etName.requestFocus(); return }
            age.isEmpty() -> { etAge.error = "Please enter your age"; etAge.requestFocus(); return }
            age.toIntOrNull() == null || age.toInt() < 1 || age.toInt() > 150 -> { etAge.error = "Please enter a valid age"; etAge.requestFocus(); return }
            email.isEmpty() -> { etEmail.error = "Please enter your email"; etEmail.requestFocus(); return }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> { etEmail.error = "Please enter a valid email"; etEmail.requestFocus(); return }
            password.isEmpty() -> { etPassword.error = "Please enter your password"; etPassword.requestFocus(); return }
            password.length < 6 -> { etPassword.error = "Password must be at least 6 characters"; etPassword.requestFocus(); return }
        }

        // Jika validasi lolos, jalankan proses Register Firebase
        registerUserToFirebase(name, email, password)
    }

    // --- FUNGSI BARU: REGISTER FIREBASE ---
    private fun registerUserToFirebase(name: String, email: String, password: String) {
        // Tampilkan loading (opsional: matikan tombol agar tidak diklik 2x)
        btnStart.isEnabled = false
        btnStart.text = "Creating Account..."

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 1. Akun berhasil dibuat
                    val user = auth.currentUser

                    // 2. Update Display Name (Nama User) ke Firebase User Profile
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()

                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                // 3. Sukses total -> Pindah ke MainActivity
                                Toast.makeText(this, "Welcome, $name!", Toast.LENGTH_SHORT).show()
                                navigateToMain()
                            } else {
                                // Gagal update nama, tapi akun sudah jadi. Tetap login.
                                navigateToMain()
                            }
                        }
                } else {
                    // Gagal Register
                    btnStart.isEnabled = true
                    btnStart.text = getString(R.string.start) // Kembalikan teks tombol
                    Toast.makeText(this, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        // Hapus history stack agar user tidak bisa back ke halaman register
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}