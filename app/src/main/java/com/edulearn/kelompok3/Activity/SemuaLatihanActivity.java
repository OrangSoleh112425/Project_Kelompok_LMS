package com.edulearn.kelompok3.Activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.edulearn.kelompok3.Adapter.SemuaLatihanAdapter;
import com.edulearn.kelompok3.R;
import com.edulearn.kelompok3.ViewModel.SemuaLatihanViewModel;

public class SemuaLatihanActivity extends AppCompatActivity {

    private RecyclerView rvSemuaLatihan;
    private SemuaLatihanAdapter adapter;
    private SemuaLatihanViewModel viewModel;
    private ProgressBar progressBar;
    private TextView tvNoLatihan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semua_latihan);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_semua_latihan);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Inisialisasi Views
        rvSemuaLatihan = findViewById(R.id.rv_semua_latihan);
        progressBar = findViewById(R.id.progress_bar_semua_latihan);
        tvNoLatihan = findViewById(R.id.tv_no_latihan_semua);

        // Setup RecyclerView
        rvSemuaLatihan.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SemuaLatihanAdapter(this);
        rvSemuaLatihan.setAdapter(adapter);

        // Setup ViewModel
        viewModel = new ViewModelProvider(this).get(SemuaLatihanViewModel.class);

        observeViewModel();

        // Mulai mengambil data
        viewModel.fetchSemuaLatihan();
    }

    private void observeViewModel() {
        // Tampilkan progress bar saat loading
        progressBar.setVisibility(View.VISIBLE);
        rvSemuaLatihan.setVisibility(View.GONE);
        tvNoLatihan.setVisibility(View.GONE);

        // Amati perubahan pada daftar latihan soal
        viewModel.getLatihanSoalListLiveData().observe(this, latihanSoalList -> {
            progressBar.setVisibility(View.GONE);
            if (latihanSoalList != null && !latihanSoalList.isEmpty()) {
                adapter.setLatihanSoalList(latihanSoalList);
                rvSemuaLatihan.setVisibility(View.VISIBLE);
            } else {
                tvNoLatihan.setVisibility(View.VISIBLE);
            }
        });

        // Amati pesan error
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                tvNoLatihan.setVisibility(View.VISIBLE);
            }
        });
    }

    // Handle tombol kembali di toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Kembali ke halaman sebelumnya
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
