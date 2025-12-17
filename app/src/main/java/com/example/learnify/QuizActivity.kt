package com.example.learnify

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class QuizActivity : AppCompatActivity() {

    // ================= MODEL =================
    data class QuestionModel(
        val question: String,
        val options: List<String>,
        val correctAnswerIndex: Int,
        val explanation: String,
        var selectedAnswerIndex: Int? = null
    )

    // ================= STATE =================
    private var currentQuestionIndex = 0
    private var score = 0
    private lateinit var selectedTopic: String
    private lateinit var questionList: List<QuestionModel>

    // ================= VIEW =================
    private lateinit var tvSoal: TextView
    private lateinit var btnOptions: List<TextView>
    private lateinit var numIndicators: List<TextView>
    private lateinit var layoutPembahasan: LinearLayout
    private lateinit var btnNext: Button

    // ================= ON CREATE =================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        selectedTopic = intent.getStringExtra("NAMA_TOPIK")?.trim()
            ?: "Pengetahuan Kuantitatif"

        tvSoal = findViewById(R.id.tvSoal)
        layoutPembahasan = findViewById(R.id.layoutPembahasan)
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

        questionList = getQuestionsByTopic(selectedTopic)

        if (questionList.isEmpty()) {
            Toast.makeText(this, "Soal belum tersedia", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        displayQuestion()

        // klik opsi
        btnOptions.forEachIndexed { index, tv ->
            tv.setOnClickListener {
                questionList[currentQuestionIndex].selectedAnswerIndex = index
                checkAnswer(index)
                updateNumberIndicators()
            }
        }

        // klik nomor soal (AMAN)
        numIndicators.forEachIndexed { index, tv ->
            tv.setOnClickListener {
                if (index < questionList.size) {
                    currentQuestionIndex = index
                    displayQuestion()
                }
            }
        }

        btnNext.setOnClickListener {
            if (currentQuestionIndex == questionList.size - 1) {
                showFinishConfirmation()
            } else {
                currentQuestionIndex++
                displayQuestion()
            }
        }
    }

    // ================= DISPLAY =================
    private fun displayQuestion() {
        if (currentQuestionIndex >= questionList.size) return

        val q = questionList[currentQuestionIndex]
        tvSoal.text = q.question
        layoutPembahasan.visibility = View.GONE

        btnOptions.forEach {
            it.isEnabled = true
            it.background = ContextCompat.getDrawable(this, R.drawable.bg_option_default)
            it.setTextColor(ContextCompat.getColor(this, R.color.text_dark))
        }

        btnOptions.forEachIndexed { i, tv ->
            tv.text = q.options[i]
        }

        q.selectedAnswerIndex?.let {
            checkAnswer(it)
        }

        updateNumberIndicators()
    }

    // ================= CHECK ANSWER =================
    private fun checkAnswer(selectedIndex: Int) {
        val q = questionList[currentQuestionIndex]
        btnOptions.forEach { it.isEnabled = false }

        if (selectedIndex == q.correctAnswerIndex) {
            btnOptions[selectedIndex].background =
                ContextCompat.getDrawable(this, R.drawable.bg_option_correct)
        } else {
            btnOptions[selectedIndex].background =
                ContextCompat.getDrawable(this, R.drawable.bg_option_wrong)
            btnOptions[q.correctAnswerIndex].background =
                ContextCompat.getDrawable(this, R.drawable.bg_option_correct)
        }

        btnOptions[selectedIndex].setTextColor(Color.WHITE)
        btnOptions[q.correctAnswerIndex].setTextColor(Color.WHITE)

        layoutPembahasan.visibility = View.VISIBLE
        val tvPembahasan = layoutPembahasan.getChildAt(2) as TextView
        tvPembahasan.text = q.explanation
    }

    // ================= INDICATOR (FIX UTAMA) =================
    private fun updateNumberIndicators() {
        numIndicators.forEachIndexed { i, tv ->

            if (i >= questionList.size) {
                tv.visibility = View.INVISIBLE
                return@forEachIndexed
            } else {
                tv.visibility = View.VISIBLE
            }

            when {
                i == currentQuestionIndex -> {
                    tv.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#1565C0"))
                    tv.setTextColor(Color.WHITE)
                }
                questionList[i].selectedAnswerIndex != null -> {
                    tv.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#2E7D32"))
                    tv.setTextColor(Color.WHITE)
                }
                else -> {
                    tv.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#E3F2FD"))
                    tv.setTextColor(Color.parseColor("#2D9CDB"))
                }
            }
        }
    }

    // ================= SCORE =================
    private fun calculateScore() {
        score = 0
        questionList.forEach {
            if (it.selectedAnswerIndex == it.correctAnswerIndex) {
                score += 20
            }
        }
    }

    private fun saveScore() {
        getSharedPreferences("LearnifyProgress", Context.MODE_PRIVATE)
            .edit()
            .putInt("SCORE_$selectedTopic", score)
            .apply()
    }
    private fun showFinishConfirmation() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Selesaikan Latihan?")
            .setMessage("Apakah kamu yakin ingin mengakhiri latihan soal?")
            .setCancelable(false)
            .setPositiveButton("Ya, Selesai") { _, _ ->
                saveScore()
                Toast.makeText(this, "Latihan selesai! Skor: $score", Toast.LENGTH_LONG).show()
                finish()
            }
            .setNegativeButton("Belum") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    // ================= BANK SOAL LENGKAP =================
    private fun getQuestionsByTopic(topic: String): List<QuestionModel> {
        return when (topic) {

            // ===== SEMUA SOAL KAMU (TIDAK DIUBAH ISINYA) =====
            // Penalaran Umum, Literasi, PBM, PPU, Inggris, PM, PK
            // (isinya PERSIS seperti yang kamu kirim)
            // --- DIPOTONG DI SINI DEMI PANJANG PESAN ---
            // ❗ KODE SOAL YANG KAMU KIRIM SUDAH BENAR
            // ❗ TINGGAL TEMPEL SEMUA QuestionModel YANG TADI
            "Penalaran Umum" -> listOf(
                QuestionModel("Semua mamalia bernapas dengan paru-paru. Ikan paus bernapas dengan paru-paru.", listOf("A. Ikan paus adalah mamalia", "B. Ikan paus bukan mamalia", "C. Semua yang bernapas dengan paru-paru adalah ikan paus", "D. Sebagian ikan paus bernapas dengan insang", "E. Tidak dapat ditarik kesimpulan"), 0, "Pembahasan:\nIni adalah contoh silogisme. Jika premis mayornya adalah 'semua A adalah B' dan premis minornya 'C adalah B', kita tidak bisa langsung menyimpulkan 'C adalah A'. Namun, dalam konteks biologi, ikan paus adalah mamalia, sehingga jawaban A adalah yang paling logis dan benar secara faktual."),
                QuestionModel("Pola bilangan: 2, 4, 8, 14, 22, ... Angka selanjutnya?", listOf("A. 30", "B. 32", "C. 34", "D. 36", "E. 40"), 1, "Pembahasan:\nSelisih antar angka membentuk pola: +2, +4, +6, +8. Selisih berikutnya adalah +10. Jadi, 22 + 10 = 32."),
                QuestionModel("Jika 'MEJA' = 'NFKB', maka 'KURSI' = ...", listOf("A. LVTUJ", "B. LVSTJ", "C. KVSTJ", "D. MVTUJ", "E. JVTUJ"), 0, "Pembahasan:\nSetiap huruf digeser satu langkah ke depan dalam alfabet (sandi geser +1). M->N, E->F, J->K, A->B. Maka, K->L, U->V, R->S, S->T, I->J. Hasilnya LVSTJ."),
                QuestionModel("Tidak ada pegawai malas yang sukses. Semua pengusaha adalah orang sukses.", listOf("A. Sebagian pengusaha malas", "B. Tidak ada pengusaha yang malas", "C. Semua pegawai pengusaha", "D. Orang sukses pengusaha", "E. Pegawai malas sukses"), 1, "Pembahasan:\nDari premis, kita tahu 'sukses' -> 'tidak malas'. Karena 'semua pengusaha' -> 'sukses', maka 'semua pengusaha' -> 'tidak malas'. Kesimpulannya, tidak ada pengusaha yang malas."),
                QuestionModel("Urutan duduk: A di sebelah kiri B. C di sebelah kanan B. D di sebelah kiri A. Siapa yang duduk paling kanan?", listOf("A. A", "B. B", "C. C", "D. D", "E. Tidak tentu"), 2, "Pembahasan:\nBerdasarkan petunjuk, urutan dari kiri ke kanan adalah: D - A - B - C. Jadi, yang paling kanan adalah C.")
            )
            "Literasi B.Indonesia" -> listOf(
                QuestionModel("Ide pokok paragraf yang membahas dampak buruk pemanasan global terhadap cuaca dan kegagalan panen petani adalah...", listOf("A. Petani mengalami gagal panen", "B. Cuaca buruk melanda", "C. Dampak pemanasan global bagi sektor pertanian", "D. Proses perubahan iklim", "E. Nasib para petani di era modern"), 2, "Pembahasan:\nKalimat lain (gagal panen, cuaca buruk) adalah ide penjelas dari ide pokok yaitu dampak pemanasan global."),
                QuestionModel("Sinonim dari kata 'efisien' adalah...", listOf("A. Tepat guna", "B. Cepat", "C. Hemat", "D. Boros", "E. Lambat"), 0, "Pembahasan:\nEfisien berarti menjalankan sesuatu dengan tepat dan berdaya guna, tanpa membuang waktu, tenaga, atau biaya."),
                QuestionModel("Penulisan judul artikel yang sesuai dengan PUEBI adalah...", listOf("A. Manfaat Apel bagi kesehatan", "B. Manfaat Apel Bagi Kesehatan", "C. Manfaat apel bagi Kesehatan", "D. Manfaat Apel bagi Kesehatan", "E. Manfaat Apel bagi Kesehatan"), 3, "Pembahasan:\nKata depan (seperti 'bagi') dalam judul ditulis dengan huruf kecil, kecuali jika berada di awal judul."),
                QuestionModel("Antonim dari kata 'sporadis' adalah...", listOf("A. Jarang", "B. Sering", "C. Teratur", "D. Berhenti", "E. Acak"), 2, "Pembahasan:\nSporadis berarti tidak tentu, kadang-kadang, atau tidak teratur. Lawan katanya adalah teratur."),
                QuestionModel("Manakah yang merupakan kalimat opini? (1) Suhu ruangan ini 30 derajat Celcius. (2) Ruangan ini terasa sangat panas.", listOf("A. Kalimat (1)", "B. Kalimat (2)", "C. Keduanya fakta", "D. Keduanya opini", "E. Tidak ada yang benar"), 1, "Pembahasan:\nKalimat (1) adalah fakta karena dapat diukur. Kalimat (2) adalah opini karena 'sangat panas' bersifat subjektif dan tergantung pada persepsi individu.")
            )
            "Pemahaman Bacaan & Menulis" -> listOf(
                QuestionModel("Manakah penulisan kata serapan yang salah?", listOf("A. Apotek", "B. Praktik", "C. Nasihat", "D. Jadwal", "E. Resiko"), 4, "Pembahasan:\nBentuk baku dari 'resiko' adalah 'risiko' (tanpa 'e'). Apotek, Praktik, Nasihat, dan Jadwal sudah merupakan bentuk baku."),
                QuestionModel("Penggunaan tanda koma (,) yang tidak tepat terdapat pada kalimat...", listOf("A. Saya membeli apel, jeruk, dan pisang.", "B. Karena lelah, ia tertidur pulas.", "C. Dia pandai, tetapi sombong.", "D. Jakarta, 17 Agustus 2024.", "E. Dia bertanya, 'Kapan kita pergi?'"), 2, "Pembahasan:\nPada kalimat C, sebelum konjungsi pertentangan 'tetapi' yang didahului subjek yang sama ('dia'), seharusnya tidak perlu ada koma."),
                QuestionModel("Kalimat manakah yang paling efektif?", listOf("A. Para hadirin sekalian dimohon berdiri.", "B. Hadirin dimohon berdiri.", "C. Demi untuk anaknya, ia bekerja keras.", "D. Dia saling bantu-membantu.", "E. Rumah itu sedang dibuat."), 1, "Pembahasan:\n'Hadirin' sudah berarti 'para hadirin', sehingga 'para hadirin sekalian' adalah pemborosan kata (pleonasme). Kalimat B adalah yang paling hemat dan efektif."),
                QuestionModel("Konjungsi yang termasuk antarkalimat adalah...", listOf("A. dan", "B. atau", "C. tetapi", "D. sedangkan", "E. Meskipun demikian,"), 4, "Pembahasan:\n'Meskipun demikian,' digunakan untuk menghubungkan dua kalimat yang berbeda, biasanya diletakkan di awal kalimat kedua. Opsi A, B, C, D adalah konjungsi intrakalimat."),
                QuestionModel("Penulisan judul 'dari desa untuk bangsa' yang benar adalah...", listOf("A. Dari Desa Untuk Bangsa", "B. Dari desa untuk Bangsa", "C. Dari Desa untuk Bangsa", "D. Dari Desa Untuk bangsa", "E. Dari desa untuk bangsa"), 2, "Pembahasan:\nKata depan ('dari', 'untuk') ditulis dengan huruf kecil. Kata lain diawali dengan huruf kapital.")
            )
            "Pengetahuan & Pemahaman Umum" -> listOf(
                QuestionModel("Imbuhan 'me-kan' yang bermakna benefaktif (melakukan untuk orang lain) terdapat pada kalimat...", listOf("A. Ayah membelikan adik mainan.", "B. Dia melemparkan batu itu jauh-jauh.", "C. Ibu memasukkan baju ke lemari.", "D. Pemerintah menaikkan harga BBM.", "E. Dia memarkirkan mobilnya."), 0, "Pembahasan:\n'Membelikan' berarti 'membeli untuk' (benefaktif). Opsi lain bermakna kausatif (menyebabkan jadi) atau lokatif (melakukan di suatu tempat)."),
                QuestionModel("Sinonim dari kata 'niskala' adalah...", listOf("A. Nyata", "B. Abstrak", "C. Keras", "D. Lembut", "E. Besar"), 1, "Pembahasan:\nNiskala berarti tidak berwujud, tidak berbentuk, atau abstrak."),
                QuestionModel("Pola frasa 'gunung tinggi' mengikuti hukum...", listOf("A. M-D (Menerangkan-Diterangkan)", "B. D-M (Diterangkan-Menerangkan)", "C. S-P (Subjek-Predikat)", "D. P-O (Predikat-Objek)", "E. K-S (Keterangan-Subjek)"), 1, "Pembahasan:\nDalam bahasa Indonesia, pola umum frasa adalah D-M (Diterangkan-Menerangkan). 'Gunung' (D) diterangkan oleh 'tinggi' (M)."),
                QuestionModel("Makna kiasan dari 'kembang desa' adalah...", listOf("A. Bunga yang tumbuh di desa", "B. Tanaman hias khas desa", "C. Gadis tercantik di desa", "D. Aroma harum pedesaan", "E. Tradisi menanam bunga"), 2, "Pembahasan:\n'Kembang desa' adalah idiom yang berarti perempuan atau gadis yang dianggap paling cantik di desanya."),
                QuestionModel("Bentuk baku dari kata 'mengkomsumsi' adalah...", listOf("A. Mengonsumsi", "B. Menkonsumsi", "C. Mengkonsumsi", "D. Mekonsumsi", "E. Salah semua"), 0, "Pembahasan:\nMenurut aturan peluluhan, fonem K, T, S, P akan luluh jika mendapat awalan 'me-'. Jadi 'me- + konsumsi' menjadi 'mengonsumsi'.")
            )
            "Literasi Bahasa Inggris" -> listOf(
                QuestionModel("What is the most appropriate question to find the main idea of a text?", listOf("A. What is the third paragraph about?", "B. What is the text generally about?", "C. Who is the author?", "D. What happens in the end?", "E. When was the text written?"), 1, "Pembahasan:\nThe main idea is the general point or message of the entire text, not a specific detail."),
                QuestionModel("Which word is a synonym for 'essential'?", listOf("A. Useless", "B. Crucial", "C. Optional", "D. Minor", "E. Extra"), 1, "Pembahasan:\n'Essential' means absolutely necessary or extremely important. 'Crucial' has the same meaning."),
                QuestionModel("The tone of a scientific report should be...", listOf("A. Emotional", "B. Objective", "C. Sarcastic", "D. Humorous", "E. Angry"), 1, "Pembahasan:\nA scientific report must be based on facts and evidence, free from personal feelings or biases, which means it must be objective."),
                QuestionModel("In the sentence, 'The cat saw a bird, and it flew away,' the pronoun 'it' refers to...", listOf("A. The cat", "B. The bird", "C. The cat and the bird", "D. A new subject", "E. The action of flying"), 1, "Pembahasan:\n'It' is a pronoun that replaces the noun mentioned before it, which is the 'antecedent'. In this context, 'it' logically refers to 'the bird' which is the one that flew away."),
                QuestionModel("Which of the following is most likely a statement of fact?", listOf("A. The movie was boring.", "B. The Earth revolves around the Sun.", "C. This is the best song ever.", "D. The painting is beautiful.", "E. The weather is too cold."), 1, "Pembahasan:\nA statement of fact can be proven to be true or false. 'The Earth revolves around the Sun' is a verifiable scientific fact. The other options are subjective opinions.")
            )
            "Penalaran Matematika" -> listOf(
                QuestionModel("Sebuah baju seharga Rp100.000 mendapat diskon 20%. Berapa yang harus dibayar?", listOf("A. Rp20.000", "B. Rp80.000", "C. Rp90.000", "D. Rp100.000", "E. Rp120.000"), 1, "Pembahasan:\nDiskon = 20% dari 100.000 = 20.000. Harga bayar = 100.000 - 20.000 = 80.000."),
                QuestionModel("Skala pada peta adalah 1:100.000. Jika jarak dua kota pada peta adalah 5 cm, berapa jarak sebenarnya?", listOf("A. 0.5 km", "B. 5 km", "C. 50 km", "D. 500 km", "E. 5000 km"), 1, "Pembahasan:\nJarak sebenarnya = 5 cm x 100.000 = 500.000 cm. Untuk mengubah cm ke km, dibagi 100.000. Jadi, 500.000 cm = 5 km."),
                QuestionModel("Berapa volume kubus yang memiliki panjang sisi 10 cm?", listOf("A. 100 cm³", "B. 1000 cm³", "C. 10 cm³", "D. 10000 cm³", "E. 600 cm³"), 1, "Pembahasan:\nVolume kubus = sisi x sisi x sisi = 10 x 10 x 10 = 1000 cm³."),
                QuestionModel("Berapa median dari data berikut: 3, 5, 7, 9, 11?", listOf("A. 5", "B. 7", "C. 9", "D. 6", "E. 8"), 1, "Pembahasan:\nMedian adalah nilai tengah dari data yang sudah diurutkan. Data sudah urut, dan nilai tengahnya adalah 7."),
                QuestionModel("Peluang munculnya mata dadu ganjil pada satu kali lemparan adalah...", listOf("A. 1/2", "B. 1/3", "C. 1/4", "D. 1/6", "E. 1"), 0, "Pembahasan:\nMata dadu ganjil adalah {1, 3, 5} (ada 3). Total mata dadu adalah 6. Peluang = Jumlah kejadian / Jumlah total = 3/6 = 1/2.")
            )
            else -> listOf( // Pengetahuan Kuantitatif
                QuestionModel("Jika 3x - 5 = 10, berapakah nilai x?", listOf("A. 3", "B. 4", "C. 5", "D. 6", "E. 7"), 2, "Pembahasan:\n3x = 10 + 5 -> 3x = 15 -> x = 15 / 3 -> x = 5."),
                QuestionModel("Berapa luas persegi panjang dengan panjang 10 cm dan lebar 5 cm?", listOf("A. 15 cm²", "B. 25 cm²", "C. 50 cm²", "D. 100 cm²", "E. 150 cm²"), 2, "Pembahasan:\nLuas = panjang x lebar = 10 x 5 = 50 cm²."),
                QuestionModel("Hasil dari 1/2 + 1/4 adalah...", listOf("A. 1/3", "B. 2/4", "C. 3/4", "D. 4/4", "E. 1"), 2, "Pembahasan:\nSamakan penyebutnya: 1/2 = 2/4. Jadi, 2/4 + 1/4 = 3/4."),
                QuestionModel("Jika a=2 dan b=3, berapakah nilai dari a² + b?", listOf("A. 5", "B. 6", "C. 7", "D. 8", "E. 9"), 2, "Pembahasan:\na² + b = (2)² + 3 = 4 + 3 = 7."),
                QuestionModel("Sebuah sudut siku-siku memiliki besar...", listOf("A. 45°", "B. 60°", "C. 90°", "D. 180°", "E. 360°"), 2, "Pembahasan:\nSudut siku-siku adalah sudut yang besarnya tepat 90 derajat.")
            )

        }
    }
}
