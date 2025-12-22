package com.edulearn.kelompok3.ViewModel;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.edulearn.kelompok3.Model.ModelLatihanSoal;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class SemuaLatihanViewModel extends AndroidViewModel {

    private static final String TAG = "SemuaLatihanViewModel";
    // Endpoint untuk mengambil semua latihan soal (mungkin perlu disesuaikan)
    private static final String URL_GET_ALL_LATIHAN = "https://sdnkalisat.com/api/latihan";

    private final MutableLiveData<List<ModelLatihanSoal>> latihanSoalListLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final RequestQueue requestQueue;

    public SemuaLatihanViewModel(@NonNull Application application) {
        super(application);
        requestQueue = Volley.newRequestQueue(application);
    }

    public LiveData<List<ModelLatihanSoal>> getLatihanSoalListLiveData() {
        return latihanSoalListLiveData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchSemuaLatihan() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL_GET_ALL_LATIHAN, null,
                response -> {
                    try {
                        List<ModelLatihanSoal> latihanSoalList = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            ModelLatihanSoal latihanSoal = new ModelLatihanSoal();
                            latihanSoal.setId(jsonObject.getInt("id"));
                            latihanSoal.setJudulLatihan(jsonObject.getString("judul_latihan"));
                            latihanSoal.setJumlahSoal(jsonObject.getInt("jumlah_soal"));
                            // Kita juga perlu nama mapel, kita asumsikan API mengembalikannya
                            if (jsonObject.has("nama_mapel")) {
                                latihanSoal.setNamaMapel(jsonObject.getString("nama_mapel"));
                            }
                            latihanSoalList.add(latihanSoal);
                        }
                        latihanSoalListLiveData.setValue(latihanSoalList);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                        errorMessage.setValue("Gagal memproses data dari server.");
                    }
                },
                error -> {
                    Log.e(TAG, "Volley error: " + error.toString());
                    errorMessage.setValue("Gagal mengambil data. Periksa koneksi internet Anda.");
                });

        requestQueue.add(jsonArrayRequest);
    }
}
