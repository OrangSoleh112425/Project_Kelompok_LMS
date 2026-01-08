package com.edulearn.kelompok3.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.edulearn.kelompok3.R;

public class DetailLatihanSoalActivity extends AppCompatActivity {

    private TextView tvJudul;
    private Button btnMulai, btnKembali;
    private int idSoal;
    private String judulSoal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Menggunakan layout baru yang sudah disederhanakan
        setContentView(R.layout.activity_detail_latihan_soal);

        // Menghubungkan ke UI yang baru
        tvJudul = findViewById(R.id.tv_judul);
        btnMulai = findViewById(R.id.btn_mulai);
        btnKembali = findViewById(R.id.btn_kembali);

        // Ambil data dari Intent yang dikirim oleh LatsolFragment
        Intent intent = getIntent();
        idSoal = intent.getIntExtra("id_soal", -1);
        judulSoal = intent.getStringExtra("judul_soal");

        // Jika karena alasan tertentu data tidak ada, tampilkan error dan kembali
        if (idSoal == -1 || judulSoal == null) {
            Toast.makeText(this, "Gagal memuat detail latihan. Silakan coba lagi.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Atur judul latihan dari data yang diterima
        tvJudul.setText(judulSoal);

        // Listener untuk tombol kembali
        btnKembali.setOnClickListener(v -> finish());

        // Listener untuk tombol mulai
        btnMulai.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Mulai Latihan")
                    .setMessage("Anda tidak dapat keluar setelah memulai. Lanjutkan?")
                    .setPositiveButton("Ya", (dialog, which) -> {
                        // Buat intent baru untuk halaman pengerjaan soal
                        Intent prosesIntent = new Intent(DetailLatihanSoalActivity.this, ProsesLatihanSoalActivity.class);
                        
                        // (INI YANG PALING PENTING) Kirim id_soal ke activity berikutnya
                        prosesIntent.putExtra("id_soal", idSoal);
                        
                        startActivity(prosesIntent);
                    })
                    .setNegativeButton("Tidak", null)
                    .show();
        });
    }
}
