package com.edulearn.kelompok3.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.edulearn.kelompok3.R;
import com.edulearn.kelompok3.Utils.ImageUtil;
import com.edulearn.kelompok3.Utils.SharedPreferencesUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DetailTugasActivity extends AppCompatActivity {

    private TextView tvJudulTgs, tvTenggat, tvDeskripsi, tvNilai, tvStatus;
    private LinearLayout linearLayoutThumbnails;
    private Button btnKirim;
    private int id;
    private List<Uri> imageUris = new ArrayList<>();
    private String tanggalTenggat;
    private ProgressDialog loadingOverlay;

    // Firebase
    private DatabaseReference databaseRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_tugas);

        // Inisialisasi Firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            showErrorAndExit("User belum login!");
            return;
        }

        databaseRef = FirebaseDatabase.getInstance().getReference();

        initUI();

        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);

        if (id == -1) {
            showErrorAndExit("ID tidak valid!");
            return;
        }

        getDataFromFirebase();
        getNilaiTugas();
    }

    private void initUI() {
        tvJudulTgs = findViewById(R.id.tv_judultgs);
        tvTenggat = findViewById(R.id.tv_tenggat);
        tvDeskripsi = findViewById(R.id.tv_deskripsi);
        tvNilai = findViewById(R.id.tv_nilai);
        tvStatus = findViewById(R.id.tv_status);
        linearLayoutThumbnails = findViewById(R.id.linearLayoutThumbnails);
        btnKirim = findViewById(R.id.btn_kirim);

        findViewById(R.id.ic_kembali).setOnClickListener(v -> finish());
        findViewById(R.id.uploadtugas).setOnClickListener(v -> openGallery());
        btnKirim.setOnClickListener(v -> submitTask());
    }

    private void getDataFromFirebase() {
        ProgressDialog progressDialog = createProgressDialog("Memuat data tugas...");
        progressDialog.show();

        // Path: tugas/{id}
        databaseRef.child("tugas").child(String.valueOf(id))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressDialog.dismiss();

                        if (snapshot.exists()) {
                            String judulTugas = snapshot.child("judul_tugas").getValue(String.class);
                            tanggalTenggat = snapshot.child("deadline").getValue(String.class);
                            String deskripsi = snapshot.child("deskripsi").getValue(String.class);

                            tvJudulTgs.setText(judulTugas != null ? judulTugas : "Judul tidak tersedia");
                            tvTenggat.setText("Deadline: " + (tanggalTenggat != null ? tanggalTenggat : "Tidak ada deadline"));
                            tvDeskripsi.setText(deskripsi != null ? deskripsi : "Deskripsi tidak tersedia");
                        } else {
                            showError("Data tugas tidak ditemukan.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        showError("Error: " + error.getMessage());
                    }
                });
    }

    private void showExistingFoto(String fotoBase64) {
        if (fotoBase64 == null || fotoBase64.isEmpty()) {
            showError("Foto tidak tersedia.");
            return;
        }

        try {
            String base64Image = "data:image/png;base64," + fotoBase64;

            runOnUiThread(() -> {
                View itemView = getLayoutInflater().inflate(R.layout.item_pilihfoto, linearLayoutThumbnails, false);
                ImageView imageThumbnail = itemView.findViewById(R.id.tempatthumbnail);

                Glide.with(DetailTugasActivity.this)
                        .load(base64Image)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(imageThumbnail);

                ImageView imageClose = itemView.findViewById(R.id.ic_remove);
                imageClose.setVisibility(View.GONE);

                linearLayoutThumbnails.addView(itemView);
            });

        } catch (Exception e) {
            e.printStackTrace();
            showError("Gagal menampilkan foto.");
        }
    }

    private void getNilaiTugas() {
        ProgressDialog progressDialog = new ProgressDialog(DetailTugasActivity.this);
        progressDialog.setMessage("Memuat nilai...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String uid = currentUser.getUid();

        // Path: pengumpulan_tugas/{uid}/{id_tugas}
        databaseRef.child("pengumpulan_tugas")
                .child(uid)
                .child(String.valueOf(id))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressDialog.dismiss();

                        if (snapshot.exists()) {
                            // Ambil nilai
                            String nilai = snapshot.child("grade").getValue(String.class);
                            tvNilai.setText("Nilai: " + (nilai != null ? nilai : "belum dinilai"));

                            // Ambil tanggal pengumpulan
                            String tanggalPengumpulan = snapshot.child("tanggal").getValue(String.class);

                            // Cek status
                            if (tanggalTenggat != null && tanggalPengumpulan != null) {
                                String statusTugas = checkTugasStatus(tanggalTenggat, tanggalPengumpulan);
                                tvStatus.setText(statusTugas);
                            }

                            // Ambil foto
                            String fotoBase64 = snapshot.child("foto").getValue(String.class);
                            String savedFotoBase64 = SharedPreferencesUtil.getSavedImageFromPreferences(DetailTugasActivity.this);

                            if (fotoBase64 != null && !fotoBase64.isEmpty()) {
                                showLoadingOverlay();
                                new Thread(() -> {
                                    try {
                                        Thread.sleep(2000);
                                        if (!fotoBase64.equals(savedFotoBase64)) {
                                            SharedPreferencesUtil.saveImageToPreferences(DetailTugasActivity.this, fotoBase64);
                                            runOnUiThread(() -> {
                                                showExistingFoto(fotoBase64);
                                                hideLoadingOverlay();
                                            });
                                        } else {
                                            runOnUiThread(() -> {
                                                showExistingFoto(savedFotoBase64);
                                                hideLoadingOverlay();
                                            });
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }).start();
                            }
                        } else {
                            // Belum ada pengumpulan
                            tvNilai.setText("Nilai: tidak ada");
                            tvStatus.setText("Belum dikumpulkan");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        showError("Error: " + error.getMessage());
                    }
                });
    }

    private String checkTugasStatus(String tanggalTenggat, String tanggalPengumpulan) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date tenggatTugas = sdf.parse(tanggalTenggat);
            Date tanggalTugas = sdf.parse(tanggalPengumpulan);

            if (tanggalTugas != null && tenggatTugas != null) {
                if (tanggalTugas.after(tenggatTugas)) {
                    return "Telat dikumpulkan";
                } else {
                    return "Tepat waktu";
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "Status Tidak Diketahui";
    }

    private void showLoadingOverlay() {
        if (loadingOverlay == null) {
            loadingOverlay = new ProgressDialog(DetailTugasActivity.this);
            loadingOverlay.setMessage("Sedang memuat...");
            loadingOverlay.setCancelable(false);
        }
        loadingOverlay.show();
    }

    private void hideLoadingOverlay() {
        if (loadingOverlay != null && loadingOverlay.isShowing()) {
            loadingOverlay.dismiss();
        }
    }

    private void loadSavedFoto() {
        String savedFotoBase64 = SharedPreferencesUtil.getSavedImageFromPreferences(this);
        if (savedFotoBase64 != null && !savedFotoBase64.isEmpty()) {
            showExistingFoto(savedFotoBase64);
        }
    }

    private void submitTask() {
        if (imageUris.isEmpty()) {
            showError("Silahkan upload foto terlebih dahulu.");
            return;
        }

        ProgressDialog progressDialog = createProgressDialog("Mengirim tugas...");
        progressDialog.show();

        String uid = currentUser.getUid();
        String fotoBase64 = ImageUtil.compressImageAndConvertToBase64(this, imageUris.get(0));

        // Siapkan data untuk dikirim
        Map<String, Object> tugasData = new HashMap<>();
        tugasData.put("id_tugas", id);
        tugasData.put("foto", fotoBase64);
        tugasData.put("tanggal", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        tugasData.put("grade", null); // Nilai belum ada

        // Path: pengumpulan_tugas/{uid}/{id_tugas}
        databaseRef.child("pengumpulan_tugas")
                .child(uid)
                .child(String.valueOf(id))
                .setValue(tugasData)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(DetailTugasActivity.this, "Tugas berhasil dikirim!", Toast.LENGTH_SHORT).show();

                    // Simpan foto ke SharedPreferences
                    SharedPreferencesUtil.saveImageToPreferences(DetailTugasActivity.this, fotoBase64);

                    recreate();
                    loadSavedFoto();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    showError("Gagal mengirim tugas: " + e.getMessage());
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                if (imageUris.size() >= 1) {
                    showAlertDialog("Anda hanya bisa mengirim 1 foto.");
                    return;
                }

                imageUris.add(imageUri);
                View itemView = getLayoutInflater().inflate(R.layout.item_pilihfoto, linearLayoutThumbnails, false);
                ImageView imageThumbnail = itemView.findViewById(R.id.tempatthumbnail);
                imageThumbnail.setImageURI(imageUri);

                ImageView imageClose = itemView.findViewById(R.id.ic_remove);
                imageClose.setOnClickListener(v -> {
                    linearLayoutThumbnails.removeView(itemView);
                    imageUris.remove(imageUri);
                });

                linearLayoutThumbnails.addView(itemView);
            }
        }
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Peringatan")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (okButton != null) {
            okButton.setTextColor(getResources().getColor(R.color.dark_text));
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    private ProgressDialog createProgressDialog(String message) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showErrorAndExit(String message) {
        showError(message);
        finish();
    }
}