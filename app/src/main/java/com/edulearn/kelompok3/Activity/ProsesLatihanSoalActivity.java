package com.edulearn.kelompok3.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.edulearn.kelompok3.Adapter.LatihanSoalPagerAdapter;
import com.edulearn.kelompok3.Model.ModelSoal;
import com.edulearn.kelompok3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProsesLatihanSoalActivity extends AppCompatActivity {

    private ViewPager2 viewPagerSoal;
    private LatihanSoalPagerAdapter adapter;
    private List<ModelSoal> soalList = new ArrayList<>();
    private ImageView icNextPage, icBackPage;
    private Button btnSelesai;
    private int idLatihan;

    // Firebase
    private DatabaseReference databaseRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proses_latihan_soal);

        // Inisialisasi Firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User belum login.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        databaseRef = FirebaseDatabase.getInstance().getReference();

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

        // Ambil data soal dari Firebase
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
        // Path: soal/{id_latihan}
        databaseRef.child("soal")
                .child(String.valueOf(idLatihan))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            Toast.makeText(ProsesLatihanSoalActivity.this,
                                    "Belum ada soal untuk latihan ini.", Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }

                        soalList.clear();

                        for (DataSnapshot soalSnapshot : snapshot.getChildren()) {
                            try {
                                Integer id = soalSnapshot.child("id").getValue(Integer.class);
                                String pertanyaan = soalSnapshot.child("pertanyaan").getValue(String.class);
                                String pilihanA = soalSnapshot.child("pilihan_a").getValue(String.class);
                                String pilihanB = soalSnapshot.child("pilihan_b").getValue(String.class);
                                String pilihanC = soalSnapshot.child("pilihan_c").getValue(String.class);
                                String pilihanD = soalSnapshot.child("pilihan_d").getValue(String.class);
                                String kunciJawaban = soalSnapshot.child("kunci_jawaban").getValue(String.class);

                                if (id != null && pertanyaan != null && pilihanA != null &&
                                        pilihanB != null && pilihanC != null && pilihanD != null &&
                                        kunciJawaban != null) {

                                    soalList.add(new ModelSoal(
                                            id,
                                            pertanyaan,
                                            pilihanA,
                                            pilihanB,
                                            pilihanC,
                                            pilihanD,
                                            kunciJawaban
                                    ));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        if (soalList.isEmpty()) {
                            Toast.makeText(ProsesLatihanSoalActivity.this,
                                    "Belum ada soal untuk latihan ini.", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            setupViewPager();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ProsesLatihanSoalActivity.this,
                                "Gagal memuat soal: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
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
                return;
            }
        }

        // Jika semua sudah dijawab, tampilkan konfirmasi
        new AlertDialog.Builder(this)
                .setTitle("Kumpulkan Jawaban")
                .setMessage("Apakah Anda yakin ingin menyelesaikan latihan ini?")
                .setPositiveButton("Ya, Kumpulkan", (dialog, which) -> {
                    submitAnswersToFirebase();
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void submitAnswersToFirebase() {
        String uid = currentUser.getUid();

        // Hitung skor
        int benar = 0;
        int total = soalList.size();

        Map<String, Object> jawabanData = new HashMap<>();

        for (int i = 0; i < soalList.size(); i++) {
            ModelSoal soal = soalList.get(i);
            String selectedAnswer = soal.getSelectedAnswer();
            String kunciJawaban = soal.getKunciJawaban();

            if (selectedAnswer != null && selectedAnswer.equals(kunciJawaban)) {
                benar++;
            }

            // Simpan jawaban per soal
            Map<String, String> jawaban = new HashMap<>();
            jawaban.put("jawaban", selectedAnswer != null ? selectedAnswer : "");
            jawaban.put("benar", (selectedAnswer != null && selectedAnswer.equals(kunciJawaban)) ? "true" : "false");

            jawabanData.put("soal_" + soal.getId(), jawaban);
        }

        // Hitung nilai
        double nilai = ((double) benar / total) * 100;

        jawabanData.put("nilai", String.valueOf((int) nilai));
        jawabanData.put("benar", benar);
        jawabanData.put("salah", total - benar);
        jawabanData.put("total_soal", total);
        jawabanData.put("timestamp", System.currentTimeMillis());

        // Simpan ke Firebase: hasil_latihan/{uid}/{id_latihan}
        databaseRef.child("hasil_latihan")
                .child(uid)
                .child(String.valueOf(idLatihan))
                .setValue(jawabanData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this,
                            "Latihan Selesai! Nilai Anda: " + (int) nilai,
                            Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Gagal menyimpan hasil: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}