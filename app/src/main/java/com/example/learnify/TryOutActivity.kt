package com.example.learnify

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class TryOutActivity : AppCompatActivity() {

    data class QuestionModel(
        val question: String,
        val options: List<String>,
        val correctAnswerIndex: Int
    )

    private var currentQuestionIndex = 0
    private var score = 0
    private var selectedTopic = ""
    private var selectedOptionIndex = -1

    private lateinit var questionList: List<QuestionModel>
    private val userAnswers = HashMap<Int, Int>()

    private lateinit var tvSoal: TextView
    private lateinit var tvTimer: TextView
    private lateinit var btnOptions: List<TextView>
    private lateinit var numIndicators: List<TextView>
    private lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_try_out)

        selectedTopic = intent.getStringExtra("NAMA_TOPIK") ?: "Pengetahuan Kuantitatif"

        tvSoal = findViewById(R.id.tvSoal)
        tvTimer = findViewById(R.id.tvTimer)
        btnNext = findViewById(R.id.btnNextSoal)

        btnOptions = listOf(
            findViewById(R.id.optA),
            findViewById(R.id.optB),
            findViewById(R.id.optC),
            findViewById(R.id.optD),
            findViewById(R.id.optE)
        )

        numIndicators = listOf(
            findViewById(R.id.tvNum1),
            findViewById(R.id.tvNum2),
            findViewById(R.id.tvNum3),
            findViewById(R.id.tvNum4),
            findViewById(R.id.tvNum5)
        )

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        // 🔥 Tombol BottomSheet
        findViewById<ImageView>(R.id.btnQuestionList).setOnClickListener {
            showQuestionBottomSheet()
        }

        questionList = getTryOutQuestions(selectedTopic)
        startTimer()
        displayQuestion()

        // Klik opsi
        btnOptions.forEachIndexed { index, tv ->
            tv.setOnClickListener {
                selectedOptionIndex = index
                updateOptionUI()
            }
        }

        // Klik nomor kecil (window 5)
        numIndicators.forEachIndexed { i, tv ->
            tv.setOnClickListener {
                val start = (currentQuestionIndex / 5) * 5
                val target = start + i
                if (target < questionList.size) {
                    currentQuestionIndex = target
                    displayQuestion()
                }
            }
        }

        btnNext.setOnClickListener {
            if (selectedOptionIndex == -1) {
                Toast.makeText(this, "Pilih jawaban dulu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            userAnswers[currentQuestionIndex] = selectedOptionIndex

            if (selectedOptionIndex ==
                questionList[currentQuestionIndex].correctAnswerIndex) {
                score += 20
            }

            if (currentQuestionIndex == questionList.size - 1) {
                showFinishConfirmation()
            } else {
                currentQuestionIndex++
                displayQuestion()
            }
        }
    }

    // ================= UI =================

    private fun displayQuestion() {
        val q = questionList[currentQuestionIndex]
        tvSoal.text = q.question

        selectedOptionIndex = userAnswers[currentQuestionIndex] ?: -1

        btnOptions.forEachIndexed { i, tv ->
            tv.text = q.options[i]
        }

        updateOptionUI()
        updateIndicatorsWindow()
    }

    private fun updateOptionUI() {
        for (i in btnOptions.indices) {
            if (i == selectedOptionIndex) {
                btnOptions[i].background =
                    ContextCompat.getDrawable(this, R.drawable.bg_option_selected)
                btnOptions[i].setTextColor(Color.WHITE)
            } else {
                btnOptions[i].background =
                    ContextCompat.getDrawable(this, R.drawable.bg_option_default)
                btnOptions[i].setTextColor(
                    ContextCompat.getColor(this, R.color.text_dark)
                )
            }
        }
    }

    private fun updateIndicatorsWindow() {
        val start = (currentQuestionIndex / 5) * 5
        for (i in numIndicators.indices) {
            val index = start + i
            val tv = numIndicators[i]

            if (index < questionList.size) {
                tv.text = (index + 1).toString()
                tv.visibility = View.VISIBLE

                when {
                    index == currentQuestionIndex -> {
                        tv.backgroundTintList =
                            ColorStateList.valueOf(Color.parseColor("#B71C1C"))
                        tv.setTextColor(Color.WHITE)
                    }
                    userAnswers.containsKey(index) -> {
                        tv.backgroundTintList =
                            ColorStateList.valueOf(Color.parseColor("#2E7D32"))
                        tv.setTextColor(Color.WHITE)
                    }
                    else -> {
                        tv.backgroundTintList =
                            ColorStateList.valueOf(Color.parseColor("#FFEBEE"))
                        tv.setTextColor(Color.parseColor("#EF5350"))
                    }
                }
            } else {
                tv.visibility = View.INVISIBLE
            }
        }
    }

    // ================= BOTTOM SHEET =================

    private fun showQuestionBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val view = LayoutInflater.from(this)
            .inflate(R.layout.bottom_sheet_question_list, null)

        val chipGroup = view.findViewById<ChipGroup>(R.id.chipGroupQuestions)

        for (i in questionList.indices) {
            val chip = Chip(this)
            chip.text = (i + 1).toString()
            chip.isCheckable = false

            when {
                i == currentQuestionIndex -> {
                    chip.setChipBackgroundColorResource(R.color.learnify_red)
                    chip.setTextColor(Color.WHITE)
                }
                userAnswers.containsKey(i) -> {
                    chip.setChipBackgroundColorResource(R.color.learnify_green)
                    chip.setTextColor(Color.WHITE)
                }
                else -> {
                    chip.setChipBackgroundColorResource(R.color.learnify_gray_light)
                }
            }

            chip.setOnClickListener {
                currentQuestionIndex = i
                dialog.dismiss()
                displayQuestion()
            }

            chipGroup.addView(chip)
        }

        dialog.setContentView(view)
        dialog.show()
    }

    // ================= TIMER & SAVE =================

    private fun startTimer() {
        val duration = if (selectedTopic == "TOBK") 1800000L else 600000L
        object : CountDownTimer(duration, 1000) {
            override fun onTick(ms: Long) {
                val min = (ms / 1000) / 60
                val sec = (ms / 1000) % 60
                tvTimer.text = String.format("%02d:%02d", min, sec)
            }
            override fun onFinish() {
                saveTryOutScore()
                finish()
            }
        }.start()
    }

    private fun saveTryOutScore() {
        getSharedPreferences("LearnifyProgress", Context.MODE_PRIVATE)
            .edit()
            .putInt("TRYOUT_SCORE_$selectedTopic", score)
            .apply()
    }
    private fun showFinishConfirmation() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Selesaikan Try Out?")
            .setMessage("Apakah kamu yakin ingin mengakhiri Try Out dan melihat skor?")
            .setCancelable(false)
            .setPositiveButton("Ya, Selesai") { _, _ ->
                saveTryOutScore()
                Toast.makeText(this, "Try Out Selesai! Skor: $score", Toast.LENGTH_LONG).show()
                finish()
            }
            .setNegativeButton("Belum") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    // ================= BANK SOAL (SINGKAT) =================
    private fun getTryOutQuestions(topic: String): List<QuestionModel> {
        if (topic == "TOBK") {
            val allQuestions = ArrayList<QuestionModel>()
            allQuestions.addAll(getTryOutQuestions("Penalaran Umum"))
            allQuestions.addAll(getTryOutQuestions("Literasi B.Indonesia"))
            allQuestions.addAll(getTryOutQuestions("Pemahaman Bacaan & Menulis"))
            allQuestions.addAll(getTryOutQuestions("Pengetahuan & Pemahaman Umum"))
            allQuestions.addAll(getTryOutQuestions("Literasi Bahasa Inggris"))
            allQuestions.addAll(getTryOutQuestions("Penalaran Matematika"))
            allQuestions.addAll(getTryOutQuestions("Pengetahuan Kuantitatif"))

            // Ambil 30 soal secara acak
            return allQuestions.shuffled().take(30)
        }

        return when (topic) {
            "Penalaran Umum" -> listOf(
                QuestionModel(
                    "Premis 1: Jika hujan, tanah basah. Premis 2: Tanah tidak basah. Kesimpulan?",
                    listOf(
                        "A. Hujan",
                        "B. Tidak hujan",
                        "C. Tanah kering",
                        "D. Banjir",
                        "E. Tidak dapat disimpulkan"
                    ),
                    1
                ),
                QuestionModel(
                    "2, 3, 5, 7, 11, ... Angka selanjutnya?",
                    listOf("A. 12", "B. 13", "C. 14", "D. 15", "E. 17"),
                    1
                ),
                QuestionModel(
                    "KODE: A=1, B=2. Maka BADAK = ?",
                    listOf("A. 21411", "B. 21412", "C. 214111", "D. 12411", "E. 214511"),
                    0
                ),
                QuestionModel(
                    "Semua dokter pandai. Sebagian dokter suka musik.",
                    listOf(
                        "A. Semua dokter suka musik",
                        "B. Sebagian orang pandai suka musik",
                        "C. Dokter tidak suka musik",
                        "D. Sebagian yang suka musik bukan dokter",
                        "E. Tidak ada kesimpulan"
                    ),
                    1
                ),
                QuestionModel(
                    "Meja : Kayu = ... : ...",
                    listOf(
                        "A. Emas : Cincin",
                        "B. Baju : Kain",
                        "C. Rumah : Atap",
                        "D. Lantai : Tanah",
                        "E. Besi : Pagar"
                    ),
                    1
                )
            )

            "Literasi B.Indonesia" -> listOf(
                QuestionModel(
                    "Apa judul yang tepat untuk teks tentang bahaya plastik bagi laut?",
                    listOf(
                        "A. Plastik Murah",
                        "B. Ancaman Sampah Plastik",
                        "C. Laut Indonesia",
                        "D. Manfaat Daur Ulang",
                        "E. Ikan di Laut"
                    ),
                    1
                ),
                QuestionModel(
                    "Kata baku dari 'praktek' adalah...",
                    listOf(
                        "A. Praktik",
                        "B. Prektik",
                        "C. Paraktik",
                        "D. Peraktik",
                        "E. Praktikum"
                    ),
                    0
                ),
                QuestionModel(
                    "Makna peribahasa 'Besar pasak daripada tiang'?",
                    listOf(
                        "A. Orang kuat",
                        "B. Pengeluaran > Pendapatan",
                        "C. Rumah besar",
                        "D. Tiang kokoh",
                        "E. Orang kaya"
                    ),
                    1
                ),
                QuestionModel(
                    "Manakah kalimat fakta?",
                    listOf(
                        "A. Bakso ini enak",
                        "B. Gadis itu cantik",
                        "C. Indonesia merdeka tahun 1945",
                        "D. Film itu seru",
                        "E. Baju ini mahal"
                    ),
                    2
                ),
                QuestionModel(
                    "Sinonim 'Akurat'?",
                    listOf("A. Cepat", "B. Tepat", "C. Lambat", "D. Salah", "E. Meleset"),
                    1
                )
            )

            "Pemahaman Bacaan & Menulis" -> listOf(
                QuestionModel(
                    "Manakah penulisan kata yang baku?",
                    listOf("A. Cidera", "B. Cedera", "C. Cidra", "D. Cadera", "E. Ciderah"),
                    1
                ),
                QuestionModel(
                    "Tanda baca yang salah terdapat pada kalimat...",
                    listOf(
                        "A. Hai, apa kabar?",
                        "B. Ibu membeli: apel, jeruk.",
                        "C. Saya mau pergi, tetapi hujan.",
                        "D. Wow! Indah sekali.",
                        "E. Jakarta, 12 Januari 2024"
                    ),
                    1
                ),
                QuestionModel(
                    "Subjek dari kalimat 'Ibu memasak nasi di dapur' adalah...",
                    listOf("A. Ibu", "B. Memasak", "C. Nasi", "D. Di dapur", "E. Ibu memasak"),
                    0
                ),
                QuestionModel(
                    "Konjungsi intrakalimat contohnya...",
                    listOf("A. Namun", "B. Akan tetapi", "C. Oleh karena itu", "D. Dan", "E. Jadi"),
                    3
                ),
                QuestionModel(
                    "Judul karangan: 'kunjungan ke museum'. Penulisan yang benar?",
                    listOf(
                        "A. Kunjungan Ke Museum",
                        "B. Kunjungan ke Museum",
                        "C. Kunjungan Ke museum",
                        "D. kunjungan ke museum",
                        "E. KUNJUNGAN KE MUSEUM"
                    ),
                    1
                )
            )

            "Pengetahuan & Pemahaman Umum" -> listOf(
                QuestionModel(
                    "Antonim 'Skeptis' adalah...",
                    listOf("A. Ragu", "B. Percaya", "C. Curiga", "D. Takut", "E. Malas"),
                    1
                ),
                QuestionModel(
                    "Awalan 'pe-' yang bermakna 'alat' terdapat pada...",
                    listOf("A. Penulis", "B. Penyapu", "C. Pedagang", "D. Petani", "E. Pelari"),
                    1
                ),
                QuestionModel(
                    "Kata 'Mobil' termasuk kata...",
                    listOf("A. Kerja", "B. Sifat", "C. Benda", "D. Keterangan", "E. Ganti"),
                    2
                ),
                QuestionModel(
                    "Makna konotasi 'Kambing Hitam'?",
                    listOf(
                        "A. Hewan kurban",
                        "B. Orang yang disalahkan",
                        "C. Warna hitam",
                        "D. Peternakan",
                        "E. Makanan"
                    ),
                    1
                ),
                QuestionModel(
                    "Sinonim 'Kompleks'?",
                    listOf("A. Sederhana", "B. Rumit", "C. Mudah", "D. Gampang", "E. Tunggal"),
                    1
                )
            )

            "Literasi Bahasa Inggris" -> listOf(
                QuestionModel(
                    "What is the text mostly about?",
                    listOf(
                        "A. Specific detail",
                        "B. General topic",
                        "C. The writer",
                        "D. The ending",
                        "E. A character"
                    ),
                    1
                ),
                QuestionModel(
                    "Antonym of 'Huge' is...",
                    listOf("A. Big", "B. Large", "C. Enormous", "D. Tiny", "E. Giant"),
                    3
                ),
                QuestionModel(
                    "The purpose of a recount text is to...",
                    listOf(
                        "A. Entertain",
                        "B. Persuade",
                        "C. Retell past events",
                        "D. Describe something",
                        "E. Explain how"
                    ),
                    2
                ),
                QuestionModel(
                    "'They' usually refers to...",
                    listOf(
                        "A. Plural nouns",
                        "B. Singular nouns",
                        "C. Verbs",
                        "D. Adverbs",
                        "E. Places"
                    ),
                    0
                ),
                QuestionModel(
                    "Which is an opinion?",
                    listOf(
                        "A. The sun is hot",
                        "B. She is beautiful",
                        "C. Water is liquid",
                        "D. Fish swim",
                        "E. Birds fly"
                    ),
                    1
                )
            )

            "Penalaran Matematika" -> listOf(
                QuestionModel(
                    "Bunga tunggal 10% setahun. Nabung 1 juta. Setahun jadi?",
                    listOf(
                        "A. 1.050.000",
                        "B. 1.100.000",
                        "C. 1.200.000",
                        "D. 1.010.000",
                        "E. 1.500.000"
                    ),
                    1
                ),
                QuestionModel(
                    "Skala peta 1:100.000. Jarak 5cm. Jarak asli?",
                    listOf("A. 0.5 km", "B. 5 km", "C. 50 km", "D. 500 km", "E. 5000 km"),
                    1
                ),
                QuestionModel(
                    "Volume kubus sisi 10 cm?",
                    listOf("A. 100", "B. 1000", "C. 10", "D. 10000", "E. 600"),
                    1
                ),
                QuestionModel(
                    "Median data: 3, 5, 7, 9, 11?",
                    listOf("A. 5", "B. 7", "C. 9", "D. 6", "E. 8"),
                    1
                ),
                QuestionModel(
                    "Peluang dadu ganjil?",
                    listOf("A. 1/2", "B. 1/3", "C. 1/4", "D. 1/6", "E. 1"),
                    0
                )
            )

            else -> listOf( // PK
                QuestionModel(
                    "3x - 5 = 10. Nilai x?",
                    listOf("A. 3", "B. 4", "C. 5", "D. 6", "E. 7"),
                    2
                ),
                QuestionModel(
                    "Luas persegi panjang p=10, l=5?",
                    listOf("A. 15", "B. 25", "C. 50", "D. 100", "E. 150"),
                    2
                ),
                QuestionModel(
                    "1/2 + 1/4 = ...",
                    listOf("A. 1/3", "B. 2/4", "C. 3/4", "D. 4/4", "E. 1"),
                    2
                ),
                QuestionModel(
                    "Jika a=2, b=3. Nilai a^2 + b?",
                    listOf("A. 5", "B. 6", "C. 7", "D. 8", "E. 9"),
                    2
                ),
                QuestionModel(
                    "Sudut siku-siku besarnya...",
                    listOf("A. 45", "B. 60", "C. 90", "D. 180", "E. 360"),
                    2
                )
            )

        }
    }

}
