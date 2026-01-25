package com.edulearn.kelompok3.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.edulearn.kelompok3.Model.ModelLatihanSoal;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SemuaLatihanViewModel extends AndroidViewModel {

    private static final String TAG = "SemuaLatihanViewModel";

    private final MutableLiveData<List<ModelLatihanSoal>> latihanSoalListLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final DatabaseReference databaseRef;

    public SemuaLatihanViewModel(@NonNull Application application) {
        super(application);
        databaseRef = FirebaseDatabase.getInstance().getReference();
    }

    public LiveData<List<ModelLatihanSoal>> getLatihanSoalListLiveData() {
        return latihanSoalListLiveData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void fetchSemuaLatihan() {
        isLoading.postValue(true);

        // Path: latihan_soal (semua latihan)
        databaseRef.child("latihan_soal")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isLoading.postValue(false);

                        if (!snapshot.exists()) {
                            latihanSoalListLiveData.setValue(new ArrayList<>());
                            errorMessage.setValue("Tidak ada latihan soal tersedia");
                            return;
                        }

                        List<ModelLatihanSoal> latihanSoalList = new ArrayList<>();

                        for (DataSnapshot latihanSnapshot : snapshot.getChildren()) {
                            try {
                                ModelLatihanSoal latihanSoal = new ModelLatihanSoal();

                                Integer id = latihanSnapshot.child("id").getValue(Integer.class);
                                String judulLatihan = latihanSnapshot.child("judul_latihan").getValue(String.class);
                                Integer jumlahSoal = latihanSnapshot.child("jumlah_soal").getValue(Integer.class);
                                String namaMapel = latihanSnapshot.child("nama_mapel").getValue(String.class);

                                if (id != null) latihanSoal.setId(id);
                                if (judulLatihan != null) latihanSoal.setJudulLatihan(judulLatihan);
                                if (jumlahSoal != null) latihanSoal.setJumlahSoal(jumlahSoal);
                                if (namaMapel != null) latihanSoal.setNamaMapel(namaMapel);

                                latihanSoalList.add(latihanSoal);

                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing latihan: " + e.getMessage());
                            }
                        }

                        latihanSoalListLiveData.setValue(latihanSoalList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        isLoading.postValue(false);
                        Log.e(TAG, "Firebase error: " + error.getMessage());
                        errorMessage.setValue("Gagal mengambil data: " + error.getMessage());
                    }
                });
    }
}