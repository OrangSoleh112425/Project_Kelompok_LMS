package com.edulearn.kelompok3.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.edulearn.kelompok3.ApiConfig;
import com.edulearn.kelompok3.Model.ModelTugas;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TugasViewModel extends AndroidViewModel {
    private final RequestQueue requestQueue;
    private final MutableLiveData<List<ModelTugas>> tugasListLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();

    public TugasViewModel(@NonNull Application application) {
        super(application);
        requestQueue = Volley.newRequestQueue(application.getApplicationContext());
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
        String url = ApiConfig.BASE_URL + "api/tugas.php?id_mapel=" + idMapel;

        Log.d("TugasViewModel", "URL API: " + url);

        isLoadingLiveData.postValue(true);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    isLoadingLiveData.postValue(false);
                    try {
                        if (response.getString("status").equals("success")) {
                            JSONArray dataArray = response.getJSONArray("data");
                            List<ModelTugas> tugasList = new ArrayList<>();

                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject dataObj = dataArray.getJSONObject(i);
                                int id = dataObj.getInt("id");
                                // PERBAIKAN: Menggunakan "nama_tugas" sesuai dengan file PHP
                                String namaTugas = dataObj.optString("nama_tugas", "Tugas Tidak Diketahui");
                                String namaMapel = dataObj.optString("nama_mapel", "-");
                                String deadline = dataObj.optString("deadline", "Tidak ada deadline");

                                // PERBAIKAN: Menggunakan namaMapel yang sudah diambil
                                tugasList.add(new ModelTugas(id, namaTugas, namaMapel, deadline));
                            }

                            tugasListLiveData.postValue(tugasList);
                        } else {
                            errorMessageLiveData.postValue("Gagal mengambil data: " + response.optString("message"));
                        }
                    } catch (JSONException e) {
                        errorMessageLiveData.postValue("Kesalahan parsing data: " + e.getMessage());
                        e.printStackTrace();
                    }
                },
                error -> {
                    isLoadingLiveData.postValue(false);
                    String errorMsg = (error.getMessage() != null) ? error.getMessage() : "Kesalahan jaringan";
                    errorMessageLiveData.postValue("Kesalahan koneksi: " + errorMsg);
                    error.printStackTrace();
                });

        requestQueue.add(jsonObjectRequest);
    }
}
