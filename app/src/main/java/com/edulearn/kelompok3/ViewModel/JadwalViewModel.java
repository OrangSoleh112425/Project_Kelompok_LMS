package com.edulearn.kelompok3.ViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edulearn.kelompok3.ApiConfig;
import com.edulearn.kelompok3.Model.ModelJadwalDetail;
import com.edulearn.kelompok3.Model.ModelJadwalGrouped;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public LiveData<List<ModelJadwalGrouped>> getJadwalGroupedList() {
        return jadwalGroupedList;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchJadwal(RequestQueue requestQueue, String token) {
        isLoading.setValue(true);
        errorMessage.setValue(null); // Reset error message

        // PERUBAHAN: Menambahkan token sebagai parameter di URL
        String url = ApiConfig.BASE_URL + "api/jadwal.php?token=" + token;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if ("success".equals(response.getString("status")) && response.has("data")) {
                            JSONArray dataArray = response.getJSONArray("data");
                            Map<String, List<ModelJadwalDetail>> groupedData = new HashMap<>();

                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject hariObj = dataArray.getJSONObject(i);
                                String hari = hariObj.getString("hari");
                                JSONArray mapelArray = hariObj.getJSONArray("mapel");

                                List<ModelJadwalDetail> detailList = new ArrayList<>();
                                for (int j = 0; j < mapelArray.length(); j++) {
                                    JSONObject mapelObj = mapelArray.getJSONObject(j);
                                    String namaMapel = mapelObj.getString("nama_mapel");
                                    String jamMulai = mapelObj.getString("jam_mulai");
                                    String jamSelesai = mapelObj.getString("jam_selesai");
                                    detailList.add(new ModelJadwalDetail(namaMapel, jamMulai, jamSelesai));
                                }
                                groupedData.put(hari, detailList);
                            }

                            List<ModelJadwalGrouped> groupedList = new ArrayList<>();
                            for (Map.Entry<String, List<ModelJadwalDetail>> entry : groupedData.entrySet()) {
                                groupedList.add(new ModelJadwalGrouped(entry.getKey(), entry.getValue()));
                            }

                            Collections.sort(groupedList, (o1, o2) -> {
                                int index1 = hariOrder.indexOf(o1.getHari());
                                int index2 = hariOrder.indexOf(o2.getHari());
                                return Integer.compare(index1, index2);
                            });

                            jadwalGroupedList.setValue(groupedList);
                        } else {
                            errorMessage.setValue("Respons tidak valid dari server. Status bukan success atau tidak ada data.");
                        }
                    } catch (JSONException e) {
                        errorMessage.setValue("Error parsing JSON: " + e.getMessage());
                        Log.e("JADWAL_VM_ERROR", "JSON parsing error", e);
                    } finally {
                        isLoading.setValue(false);
                    }
                },
                error -> {
                    if (error.networkResponse != null) {
                        String errorBody = new String(error.networkResponse.data);
                        errorMessage.setValue("Error " + error.networkResponse.statusCode + ": " + errorBody);
                        Log.e("JADWAL_VM_ERROR", "Volley error: " + error.networkResponse.statusCode + " Body: " + errorBody, error);
                    } else {
                        errorMessage.setValue("Error jaringan: " + error.getMessage());
                        Log.e("JADWAL_VM_ERROR", "Volley network error", error);
                    }
                    isLoading.setValue(false);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                // PERUBAHAN: Header Authorization tidak lagi dibutuhkan di sini
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000, // 10 detik
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(jsonObjectRequest);
    }
}
