package com.edulearn.kelompok3.Model;

import java.util.List;

public class ModelJadwalGrouped {
    private String hari;
    private List<ModelJadwalDetail> mapelList;

    public ModelJadwalGrouped(String hari, List<ModelJadwalDetail> mapelList) {
        this.hari = hari;
        this.mapelList = mapelList;
    }

    public String getHari() {
        return hari;
    }

    public List<ModelJadwalDetail> getMapelList() {
        return mapelList;
    }
}
