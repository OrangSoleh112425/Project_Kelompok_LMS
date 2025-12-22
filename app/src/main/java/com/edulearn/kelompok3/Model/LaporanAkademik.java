package com.edulearn.kelompok3.Model;

public class LaporanAkademik {

    private String namaMapel;
    private String nilaiAkhir;
    private String nilaiHuruf;
    private String kehadiran;

    // Constructor
    public LaporanAkademik(String namaMapel, String nilaiAkhir, String nilaiHuruf, String kehadiran) {
        this.namaMapel = namaMapel;
        this.nilaiAkhir = nilaiAkhir;
        this.nilaiHuruf = nilaiHuruf;
        this.kehadiran = kehadiran;
    }

    // Getters
    public String getNamaMapel() {
        return namaMapel;
    }

    public String getNilaiAkhir() {
        return nilaiAkhir;
    }

    public String getNilaiHuruf() {
        return nilaiHuruf;
    }

    public String getKehadiran() {
        return kehadiran;
    }
}
