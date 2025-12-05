package com.edulearn.kelompok3.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.edulearn.kelompok3.Adapter.TugasAdapter;
import com.edulearn.kelompok3.Model.ModelTugas;
import com.edulearn.kelompok3.R;
import java.util.ArrayList;
import java.util.List;

public class TugasActivity extends AppCompatActivity {

    private RecyclerView rvTugas;
    private TextView tvNoTugas;
    private TugasAdapter tugasAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tugas);

        rvTugas = findViewById(R.id.rv_tugas);
        tvNoTugas = findViewById(R.id.tv_no_tugas);

        setupRecyclerView();
        loadDummyData();

        findViewById(R.id.ic_kembali).setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        rvTugas.setLayoutManager(new LinearLayoutManager(this));
        // Initialize with an empty list, data will be loaded soon
        tugasAdapter = new TugasAdapter(this, new ArrayList<>());
        rvTugas.setAdapter(tugasAdapter);
    }

    private void loadDummyData() {
        List<ModelTugas> dummyTugasList = new ArrayList<>();
        dummyTugasList.add(new ModelTugas(1, "Membuat Rangkuman Bab 1", "Bahasa Indonesia", "2024-05-20"));
        dummyTugasList.add(new ModelTugas(2, "Mengerjakan LKS Hal. 25", "Matematika", "2024-05-22"));
        dummyTugasList.add(new ModelTugas(3, "Praktikum Fotosintesis", "IPA", "2024-05-24"));

        if (dummyTugasList.isEmpty()) {
            rvTugas.setVisibility(View.GONE);
            tvNoTugas.setVisibility(View.VISIBLE);
        } else {
            rvTugas.setVisibility(View.VISIBLE);
            tvNoTugas.setVisibility(View.GONE);
            tugasAdapter.updateData(dummyTugasList);
        }
    }
}
