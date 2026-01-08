package com.edulearn.kelompok3.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.edulearn.kelompok3.Activity.IntroActivity;
import com.edulearn.kelompok3.Activity.UbahSandiActivity;
import com.edulearn.kelompok3.ApiConfig;
import com.edulearn.kelompok3.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private String userToken;

    private TextView tvnama, tvnisn, tvkelas, tv_nomor, tvnamawalikelas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_session", requireContext().MODE_PRIVATE);
        userToken = sharedPreferences.getString("user_token", null);

        if (userToken == null || userToken.isEmpty()) {
            navigateToIntro();
            return view;
        }

        tvnama = view.findViewById(R.id.tvnama);
        tvnisn = view.findViewById(R.id.tvnisn);
        tvkelas = view.findViewById(R.id.tvkelas);
        tv_nomor = view.findViewById(R.id.tv_nomor);
        tvnamawalikelas = view.findViewById(R.id.namawalikelas);

        loadProfile();
        loadWaliKelas();

        CardView logoutCard = view.findViewById(R.id.card_logout);
        logoutCard.setOnClickListener(v -> showLogoutConfirmationDialog());

        CardView gantiPw = view.findViewById(R.id.gantipw);
        ImageView icEditPw = view.findViewById(R.id.ic_editpw);
        gantiPw.setOnClickListener(v -> openUbahSandiActivity());
        icEditPw.setOnClickListener(v -> openUbahSandiActivity());

        return view;
    }

    private void loadProfile() {
        String url = ApiConfig.BASE_URL + "api/profile.php";
        Log.d(TAG, "Requesting profile data from: " + url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d(TAG, "Profile Response: " + response.toString());
                        String status = response.getString("status");
                        if ("success".equals(status)) {
                            JSONArray dataArray = response.getJSONArray("data");
                            if (dataArray.length() > 0) {
                                JSONObject data = dataArray.getJSONObject(0);
                                String nama = data.getString("nama");
                                String nisn = data.getString("nisn");
                                String kelas = data.getString("kelas");
                                String nomor_hp = data.getString("nomor_hp");
                                tvnama.setText(nama);
                                tvnisn.setText(nisn);
                                tvkelas.setText(kelas);
                                tv_nomor.setText(nomor_hp);
                            } else {
                                showDetailedErrorAlert("Data Profil Kosong", "Server merespons sukses tetapi tidak ada data profil.");
                            }
                        } else {
                            String message = response.optString("message", "Status respons bukan 'success'.");
                            showDetailedErrorAlert("Gagal Memuat Profil", message);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
                        showDetailedErrorAlert("Kesalahan Data Server", "Gagal mem-parsing data profil. Format respons dari server mungkin salah. Detail: " + e.getMessage());
                    }
                },
                error -> {
                    String errorMsg = "Terjadi kesalahan jaringan.";
                    if (error.networkResponse != null) {
                        errorMsg = "Error: " + error.networkResponse.statusCode + " - " + new String(error.networkResponse.data);
                    }
                    Log.e(TAG, "Volley Error: " + error.toString());
                    showDetailedErrorAlert("Kesalahan Jaringan", errorMsg);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + userToken);
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        queue.add(request);
    }

    private void loadWaliKelas() {
        String url = ApiConfig.BASE_URL + "api/wali.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String status = response.getString("status");
                        if (status.equals("success")) {
                            JSONObject data = response.getJSONObject("data");
                            String namaWali = data.getString("nama");
                            tvnamawalikelas.setText(namaWali);
                        } else {
                            tvnamawalikelas.setText("Tidak ditemukan");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        tvnamawalikelas.setText("Error memuat data");
                    }
                },
                error -> {
                    error.printStackTrace();
                    tvnamawalikelas.setText("Gagal memuat");
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + userToken);
                headers.put("Accept", "application/json");
                return headers;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        queue.add(request);
    }

    private void openUbahSandiActivity() {
        Intent intent = new Intent(getActivity(), UbahSandiActivity.class);
        startActivity(intent);
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin logout?")
                .setPositiveButton("OK", (dialogInterface, which) -> logout())
                .setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss());
        dialog.show();
    }

    private void logout() {
        // 1. Hapus sesi (token) yang tersimpan secara lokal
        clearSession();

        // 2. Arahkan pengguna kembali ke halaman Intro/Login
        navigateToIntro();
    }

    private void clearSession() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_session", requireContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("user_token");
        editor.apply();
    }

    private void navigateToIntro() {
        Intent intent = new Intent(getActivity(), IntroActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void showDetailedErrorAlert(String title, String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message + "\n\nSilakan periksa file PHP Anda atau login kembali.")
                .setCancelable(false)
                .setPositiveButton("Login Ulang", (dialog, which) -> {
                    clearSession();
                    navigateToIntro();
                })
                .setNegativeButton("Tutup", null)
                .show();
    }
}
