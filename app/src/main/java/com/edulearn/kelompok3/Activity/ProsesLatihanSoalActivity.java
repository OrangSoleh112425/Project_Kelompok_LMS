package com.edulearn.kelompok3.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.edulearn.kelompok3.Adapter.LatihanSoalPagerAdapter;
import com.edulearn.kelompok3.ApiConfig;
import com.edulearn.kelompok3.Model.ModelSoal;
import com.edulearn.kelompok3.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProsesLatihanSoalActivity extends AppCompatActivity {

    private ViewPager2 viewPagerSoal;
    private LatihanSoalPagerAdapter adapter;
    private List<ModelSoal> soalList = new ArrayList<>();
    private ImageView icNextPage, icBackPage;
    private Button btnSelesai;
    private int idLatihan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proses_latihan_soal);

        viewPagerSoal = findViewById(R.id.viewPagerSoal);
        icNextPage = findViewById(R.id.ic_nextpage);
        icBackPage = findViewById(R.id.ic_backpage);
        btnSelesai = findViewById(R.id.btn_selesai);

        // Ambil ID latihan dari Intent dengan nama "id_soal"
        idLatihan = getIntent().getIntExtra("id_soal", -1);

        if (idLatihan == -1) {
            Toast.makeText(this, "ID Latihan tidak valid.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Ambil data soal dari server
        fetchSoalItems();

        // Nonaktifkan tombol kembali bawaan Android
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Toast.makeText(ProsesLatihanSoalActivity.this, "Gunakan tombol navigasi di dalam soal.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchSoalItems() {
        // URL baru yang menunjuk ke get_soal.php
        String url = ApiConfig.BASE_URL + "api/get_soal.php?id_latihan=" + idLatihan;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if ("success".equals(response.getString("status"))) {
                            JSONArray data = response.getJSONArray("data");
                            if (data.length() == 0) {
                                Toast.makeText(this, "Belum ada soal untuk latihan ini.", Toast.LENGTH_LONG).show();
                                finish();
                                return;
                            }

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                soalList.add(new ModelSoal(
                                        obj.getInt("id"),
                                        obj.getString("pertanyaan"),
                                        obj.getString("pilihan_a"),
                                        obj.getString("pilihan_b"),
                                        obj.getString("pilihan_c"),
                                        obj.getString("pilihan_d"),
                                        obj.getString("kunci_jawaban")
                                ));
                            }
                            setupViewPager();
                        } else {
                            String msg = response.optString("message", "Gagal memuat soal.");
                            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                            finish();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Kesalahan format data dari server.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                },
                error -> {
                    Toast.makeText(this, "Gagal terhubung ke server.", Toast.LENGTH_LONG).show();
                    finish();
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void setupViewPager() {
        adapter = new LatihanSoalPagerAdapter(soalList);
        viewPagerSoal.setAdapter(adapter);
        viewPagerSoal.setUserInputEnabled(false); // Nonaktifkan geser manual

        updateNavigationControls(0);

        viewPagerSoal.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateNavigationControls(position);
            }
        });

        icNextPage.setOnClickListener(v -> {
            viewPagerSoal.setCurrentItem(viewPagerSoal.getCurrentItem() + 1, true);
        });

        icBackPage.setOnClickListener(v -> {
            viewPagerSoal.setCurrentItem(viewPagerSoal.getCurrentItem() - 1, true);
        });

        btnSelesai.setOnClickListener(v -> showConfirmationDialog());
    }

    private void updateNavigationControls(int position) {
        // Atur visibilitas tombol kembali
        icBackPage.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);

        // Atur visibilitas tombol next dan selesai
        if (position == soalList.size() - 1) {
            icNextPage.setVisibility(View.GONE);
            btnSelesai.setVisibility(View.VISIBLE);
        } else {
            icNextPage.setVisibility(View.VISIBLE);
            btnSelesai.setVisibility(View.GONE);
        }
    }
    
    private void showConfirmationDialog() {
        // Cek apakah semua soal sudah dijawab
        for (ModelSoal soal : soalList) {
            if (soal.getSelectedAnswer() == null) {
                new AlertDialog.Builder(this)
                        .setTitle("Belum Selesai")
                        .setMessage("Harap jawab semua pertanyaan sebelum menyelesaikan latihan.")
                        .setPositiveButton("OK", null)
                        .show();
                return; // Hentikan proses jika ada soal yang belum dijawab
            }
        }

        // Jika semua sudah dijawab, tampilkan konfirmasi
        new AlertDialog.Builder(this)
                .setTitle("Kumpulkan Jawaban")
                .setMessage("Apakah Anda yakin ingin menyelesaikan latihan ini?")
                .setPositiveButton("Ya, Kumpulkan", (dialog, which) -> {
                    // Logika untuk mengirim jawaban ke server bisa ditambahkan di sini
                    Toast.makeText(this, "Latihan Selesai! (Logika pengiriman belum diimplementasikan)", Toast.LENGTH_LONG).show();
                    finish(); // Kembali ke halaman sebelumnya
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}
