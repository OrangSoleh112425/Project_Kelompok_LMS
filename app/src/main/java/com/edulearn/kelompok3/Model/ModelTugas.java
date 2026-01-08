package com.edulearn.kelompok3.Model;

public class ModelTugas {
    private int id;
    private String namaTugas;
    private String namaMapel;
    private String deadline;

    public ModelTugas(int id, String namaTugas, String namaMapel, String deadline) {
        this.id = id;
        this.namaTugas = namaTugas;
        this.namaMapel = namaMapel;
        this.deadline = deadline;
    }

    public int getId() {
        return id;
    }

    public String getNamaTugas() {
        return namaTugas;
    }

    public String getNamaMapel() {
        return namaMapel;
    }

    public String getDeadline() {
        return deadline;
    }
}
