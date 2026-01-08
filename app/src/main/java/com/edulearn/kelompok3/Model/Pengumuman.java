package com.edulearn.kelompok3.Model;

public class Pengumuman {

    private String judul;
    private String tanggal;
    private String imageUrl;

    public Pengumuman(String judul, String tanggal, String imageUrl) {
        this.judul = judul;
        this.tanggal = tanggal;
        this.imageUrl = imageUrl;
    }

    public String getJudul() {
        return judul;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
