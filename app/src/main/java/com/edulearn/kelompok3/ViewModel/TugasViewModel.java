package com.edulearn.kelompok3.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.edulearn.kelompok3.Model.ModelTugas;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TugasViewModel extends AndroidViewModel {

    private final DatabaseReference databaseRef;
    private final MutableLiveData<List<ModelTugas>> tugasListLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();

    public TugasViewModel(@NonNull Application application) {
        super(application);
        databaseRef = FirebaseDatabase.getInstance().getReference();
    }

    public MutableLiveData<List<ModelTugas>> getTugasListLiveData() {
        return tugasListLiveData;
    }

    public MutableLiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    public MutableLiveData<Boolean> getIsLoadingLiveData() {
        return isLoadingLiveData;
    }

    public void fetchTugas(int idMapel) {
        isLoadingLiveData.postValue(true);

        Log.d("TugasViewModel", "Fetching tugas for mapel ID: " + idMapel);

        // Path: tugas (filter by id_mapel)
        databaseRef.child("tugas")
                .orderByChild("id_mapel")
                .equalTo(idMapel)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isLoadingLiveData.postValue(false);

                        if (!snapshot.exists()) {
                            tugasListLiveData.postValue(new ArrayList<>());
                            errorMessageLiveData.postValue("Tidak ada tugas untuk mata pelajaran ini");
                            return;
                        }

                        List<ModelTugas> tugasList = new ArrayList<>();

                        for (DataSnapshot tugasSnapshot : snapshot.getChildren()) {
                            try {
                                Integer id = tugasSnapshot.child("id").getValue(Integer.class);
                                String namaTugas = tugasSnapshot.child("judul_tugas").getValue(String.class);
                                String namaMapel = tugasSnapshot.child("nama_mapel").getValue(String.class);
                                String deadline = tugasSnapshot.child("deadline").getValue(String.class);

                                if (id != null) {
                                    tugasList.add(new ModelTugas(
                                            id,
                                            namaTugas != null ? namaTugas : "Tugas Tidak Diketahui",
                                            namaMapel != null ? namaMapel : "-",
                                            deadline != null ? deadline : "Tidak ada deadline"
                                    ));
                                }
                            } catch (Exception e) {
                                Log.e("TugasViewModel", "Error parsing tugas: " + e.getMessage());
                            }
                        }

                        tugasListLiveData.postValue(tugasList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        isLoadingLiveData.postValue(false);
                        errorMessageLiveData.postValue("Error: " + error.getMessage());
                        Log.e("TugasViewModel", "Firebase error", error.toException());
                    }
                });
    }
}