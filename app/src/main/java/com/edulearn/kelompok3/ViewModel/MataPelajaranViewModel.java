package com.edulearn.kelompok3.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.edulearn.kelompok3.Model.MataPelajaran;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MataPelajaranViewModel extends AndroidViewModel {

    private final MutableLiveData<List<MataPelajaran>> mataPelajaranListLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final DatabaseReference databaseRef;

    public MataPelajaranViewModel(@NonNull Application application) {
        super(application);
        databaseRef = FirebaseDatabase.getInstance().getReference();
    }

    public MutableLiveData<List<MataPelajaran>> getMataPelajaranListLiveData() {
        return mataPelajaranListLiveData;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchMataPelajaran() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            errorMessage.postValue("User belum login");
            return;
        }

        String uid = currentUser.getUid();
        isLoading.postValue(true);

        // Path: mapel_kelas/{uid} atau mapel_kelas (jika sama untuk semua)
        databaseRef.child("mapel_kelas").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isLoading.postValue(false);

                        if (!snapshot.exists()) {
                            mataPelajaranListLiveData.postValue(new ArrayList<>());
                            errorMessage.postValue("Tidak ada mata pelajaran");
                            return;
                        }

                        List<MataPelajaran> mataPelajaranList = new ArrayList<>();

                        for (DataSnapshot mapelSnapshot : snapshot.getChildren()) {
                            try {
                                Integer id = mapelSnapshot.child("id").getValue(Integer.class);
                                String namaMapel = mapelSnapshot.child("nama_mapel").getValue(String.class);
                                String namaGuru = mapelSnapshot.child("nama_guru").getValue(String.class);

                                if (id != null && namaMapel != null && namaGuru != null) {
                                    mataPelajaranList.add(new MataPelajaran(id, namaMapel, namaGuru));
                                }
                            } catch (Exception e) {
                                Log.e("MataPelajaranVM", "Error parsing: " + e.getMessage());
                            }
                        }

                        mataPelajaranListLiveData.postValue(mataPelajaranList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        isLoading.postValue(false);
                        errorMessage.postValue("Error: " + error.getMessage());
                        Log.e("MataPelajaranVM", "Firebase error", error.toException());
                    }
                });
    }
}