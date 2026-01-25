package com.edulearn.kelompok3.ViewModel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.edulearn.kelompok3.Model.ModelJadwalDetail;
import com.edulearn.kelompok3.Model.ModelJadwalGrouped;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JadwalViewModel extends ViewModel {

    private final MutableLiveData<List<ModelJadwalGrouped>> jadwalGroupedList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private final List<String> hariOrder = List.of("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu");

    private DatabaseReference databaseRef;

    public JadwalViewModel() {
        databaseRef = FirebaseDatabase.getInstance().getReference();
    }

    public LiveData<List<ModelJadwalGrouped>> getJadwalGroupedList() {
        return jadwalGroupedList;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchJadwal(String uid) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        // Path: jadwal/{uid} atau jadwal (jika data sama untuk semua)
        databaseRef.child("schedules").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isLoading.setValue(false);

                        if (!snapshot.exists()) {
                            errorMessage.setValue("Tidak ada data jadwal");
                            jadwalGroupedList.setValue(new ArrayList<>());
                            return;
                        }

                        try {
                            Map<String, List<ModelJadwalDetail>> groupedData = new HashMap<>();

                            for (DataSnapshot hariSnapshot : snapshot.getChildren()) {
                                String hari = hariSnapshot.child("hari").getValue(String.class);

                                if (hari == null) continue;

                                List<ModelJadwalDetail> detailList = new ArrayList<>();
                                DataSnapshot mapelSnapshot = hariSnapshot.child("mapel");

                                for (DataSnapshot mapel : mapelSnapshot.getChildren()) {
                                    String namaMapel = mapel.child("nama_mapel").getValue(String.class);
                                    String jamMulai = mapel.child("jam_mulai").getValue(String.class);
                                    String jamSelesai = mapel.child("jam_selesai").getValue(String.class);

                                    if (namaMapel != null && jamMulai != null && jamSelesai != null) {
                                        detailList.add(new ModelJadwalDetail(namaMapel, jamMulai, jamSelesai));
                                    }
                                }

                                groupedData.put(hari, detailList);
                            }

                            List<ModelJadwalGrouped> groupedList = new ArrayList<>();
                            for (Map.Entry<String, List<ModelJadwalDetail>> entry : groupedData.entrySet()) {
                                groupedList.add(new ModelJadwalGrouped(entry.getKey(), entry.getValue()));
                            }

                            // Sort berdasarkan urutan hari
                            Collections.sort(groupedList, (o1, o2) -> {
                                int index1 = hariOrder.indexOf(o1.getHari());
                                int index2 = hariOrder.indexOf(o2.getHari());
                                return Integer.compare(index1, index2);
                            });

                            jadwalGroupedList.setValue(groupedList);

                        } catch (Exception e) {
                            errorMessage.setValue("Error parsing data: " + e.getMessage());
                            Log.e("JADWAL_VM_ERROR", "Error parsing Firebase data", e);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        isLoading.setValue(false);
                        errorMessage.setValue("Error Firebase: " + error.getMessage());
                        Log.e("JADWAL_VM_ERROR", "Firebase error", error.toException());
                    }
                });
    }
}