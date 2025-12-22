package com.edulearn.kelompok3.ViewModel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.edulearn.kelompok3.ApiConfig;
import com.edulearn.kelompok3.Model.ModelLatihanSoal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LatihanSoalViewModel extends AndroidViewModel {

    private final MutableLiveData<List<ModelLatihanSoal>> latihanSoalListLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final RequestQueue requestQueue;

    public LatihanSoalViewModel(@NonNull Application application) {
        super(application);
        requestQueue = Volley.newRequestQueue(application.getApplicationContext());
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
        // URL baru yang menunjuk ke file PHP kita dengan parameter id_mapel
        String url = ApiConfig.BASE_URL + "api/latihan_soal.php?id_mapel=" + idMapel;

        isLoading.postValue(true);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    isLoading.postValue(false);
                    try {
                        if ("success".equals(response.getString("status"))) {
                            JSONArray dataArray = response.getJSONArray("data");
                            List<ModelLatihanSoal> list = new ArrayList<>();
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject obj = dataArray.getJSONObject(i);

                                // Parsing data sesuai dengan model dan tabel baru
                                int id = obj.getInt("id");
                                String judul = obj.getString("judul_latihan");
                                int jumlahSoal = obj.getInt("jumlah_soal");

                                // Buat objek ModelLatihanSoal dengan konstruktor baru
                                list.add(new ModelLatihanSoal(id, judul, jumlahSoal));
                            }
                            latihanSoalListLiveData.postValue(list);
                        } else {
                            String message = response.optString("message", "Gagal mengambil data latihan soal.");
                            errorMessage.postValue(message);
                            latihanSoalListLiveData.postValue(new ArrayList<>()); // Kirim list kosong
                        }
                    } catch (JSONException e) {
                        errorMessage.postValue("Kesalahan parsing data: " + e.getMessage());
                        latihanSoalListLiveData.postValue(new ArrayList<>());
                    }
                },
                error -> {
                    isLoading.postValue(false);
                    String errorMsg = (error.getMessage() != null) ? error.getMessage() : "Kesalahan jaringan";
                    errorMessage.postValue(errorMsg);
                    latihanSoalListLiveData.postValue(new ArrayList<>());
                });

        requestQueue.add(jsonObjectRequest);
    }
}
