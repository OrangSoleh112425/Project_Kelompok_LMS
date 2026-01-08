package com.edulearn.kelompok3.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.edulearn.kelompok3.Adapter.DetailMapelAdapter;
import com.edulearn.kelompok3.R;

public class DetailMapelActivity extends AppCompatActivity {

    private int idMapel;
    private String namaMapel, namaGuru;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_mapel);

        // Ambil data dari Intent
        Intent intent = getIntent();
        idMapel = intent.getIntExtra("id_mapel", -1);
        namaMapel = intent.getStringExtra("nama_mapel");
        namaGuru = intent.getStringExtra("nama_guru");

        // Inisialisasi Views dari layout
        TextView tvJudulKelas = findViewById(R.id.tv_judul_kelas);
        TextView tvNamaGuru = findViewById(R.id.tv_nama_guru);
        ImageView btnBack = findViewById(R.id.btn_back);
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        // Set data ke header
        if (namaMapel != null) {
            tvJudulKelas.setText(namaMapel);
        }
        if (namaGuru != null) {
            tvNamaGuru.setText(namaGuru);
        }

        // Setup tombol kembali
        btnBack.setOnClickListener(v -> onBackPressed());

        // Setup ViewPager2 dengan Adapter
        DetailMapelAdapter adapter = new DetailMapelAdapter(this, idMapel, namaMapel);
        viewPager.setAdapter(adapter);

        // Hubungkan TabLayout dengan ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Materi");
                            break;
                        case 1:
                            tab.setText("Latihan Soal");
                            break;
                        case 2:
                            tab.setText("Tugas");
                            break;
                    }
                }
        ).attach();
    }
}
