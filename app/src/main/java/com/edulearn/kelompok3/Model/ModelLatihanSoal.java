package com.edulearn.kelompok3.Model;

public class ModelLatihanSoal {
    private int id;
    private String judulLatihan;
    private int jumlahSoal;
    private String namaMapel; // <-- FIELD YANG HILANG, DITAMBAHKAN DI SINI

    // Constructor kosong diperlukan oleh ViewModel
    public ModelLatihanSoal() {
    }

    // Konstruktor yang sudah ada
    public ModelLatihanSoal(int id, String judulLatihan, int jumlahSoal) {
        this.id = id;
        this.judulLatihan = judulLatihan;
        this.jumlahSoal = jumlahSoal;
    }

    // Getter dan Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJudulLatihan() {
        return judulLatihan;
    }

    public void setJudulLatihan(String judulLatihan) {
        this.judulLatihan = judulLatihan;
    }

    public int getJumlahSoal() {
        return jumlahSoal;
    }

    public void setJumlahSoal(int jumlahSoal) {
        this.jumlahSoal = jumlahSoal;
    }

    // --- GETTER DAN SETTER YANG HILANG, DITAMBAHKAN DI SINI ---
    public String getNamaMapel() {
        return namaMapel;
    }

    public void setNamaMapel(String namaMapel) {
        this.namaMapel = namaMapel;
    }
    // --------------------------------------------------------

    // Getter lama untuk kompatibilitas
    public String getJudulSoal() {
        return judulLatihan;
    }
}
