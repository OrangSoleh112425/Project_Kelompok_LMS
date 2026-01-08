package com.edulearn.kelompok3.Model;

public class ModelSoal {
    private int id;
    private String pertanyaan;
    private String pilihanA;
    private String pilihanB;
    private String pilihanC;
    private String pilihanD;
    private String kunciJawaban;
    private String selectedAnswer; // Untuk menyimpan jawaban yang dipilih pengguna

    // Konstruktor yang sesuai dengan data dari get_soal.php
    public ModelSoal(int id, String pertanyaan, String pilihanA, String pilihanB, String pilihanC, String pilihanD, String kunciJawaban) {
        this.id = id;
        this.pertanyaan = pertanyaan;
        this.pilihanA = pilihanA;
        this.pilihanB = pilihanB;
        this.pilihanC = pilihanC;
        this.pilihanD = pilihanD;
        this.kunciJawaban = kunciJawaban;
        this.selectedAnswer = null; // Default, belum dijawab
    }

    // --- Getters ---
    public int getId() {
        return id;
    }

    public String getPertanyaan() {
        return pertanyaan;
    }

    public String getPilihanA() {
        return pilihanA;
    }

    public String getPilihanB() {
        return pilihanB;
    }

    public String getPilihanC() {
        return pilihanC;
    }

    public String getPilihanD() {
        return pilihanD;
    }

    public String getKunciJawaban() {
        return kunciJawaban;
    }

    public String getSelectedAnswer() {
        return selectedAnswer;
    }

    // --- Setter untuk jawaban pengguna ---
    public void setSelectedAnswer(String selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }

    // Getter kompatibilitas (jika ada file lain yang masih memakai getSoal())
    public String getSoal() {
        return pertanyaan;
    }
}
