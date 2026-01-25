package com.edulearn.kelompok3.ViewModel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.edulearn.kelompok3.Model.ModelLatihanSoal;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LatihanSoalViewModel extends AndroidViewModel {

    private final MutableLiveData<List<ModelLatihanSoal>> latihanSoalListLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final DatabaseReference databaseRef;

    public LatihanSoalViewModel(@NonNull Application application) {
        super(application);
        databaseRef = FirebaseDatabase.getInstance().getReference();
    }

    public MutableLiveData<List<ModelLatihanSoal>> getLatihanSoalListLiveData() {
        return latihanSoalListLiveData;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchLatihanSoal(int idMapel) {
        isLoading.postValue(true);

        // Path: latihan_soal (filter by id_mapel)
        databaseRef.child("latihan_soal")
                .orderByChild("id_mapel")
                .equalTo(idMapel)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isLoading.postValue(false);

                        if (!snapshot.exists()) {
                            latihanSoalListLiveData.postValue(new ArrayList<>());
                            errorMessage.postValue("Tidak ada latihan untuk mata pelajaran ini");
                            return;
                        }

                        List<ModelLatihanSoal> list = new ArrayList<>();

                        for (DataSnapshot latihanSnapshot : snapshot.getChildren()) {
                            try {
                                Integer id = latihanSnapshot.child("id").getValue(Integer.class);
                                String judul = latihanSnapshot.child("judul_latihan").getValue(String.class);
                                Integer jumlahSoal = latihanSnapshot.child("jumlah_soal").getValue(Integer.class);

                                if (id != null && judul != null && jumlahSoal != null) {
                                    list.add(new ModelLatihanSoal(id, judul, jumlahSoal));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        latihanSoalListLiveData.postValue(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        isLoading.postValue(false);
                        errorMessage.postValue("Error: " + error.getMessage());
                        latihanSoalListLiveData.postValue(new ArrayList<>());
                    }
                });
    }
}