package com.edulearn.kelompok3.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.edulearn.kelompok3.Activity.IntroActivity;
import com.edulearn.kelompok3.Activity.UbahSandiActivity;
import com.edulearn.kelompok3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private TextView tvnama, tvnisn, tvkelas, tv_nomor, tvnamawalikelas;

    // Firebase
    private DatabaseReference databaseRef;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Cek apakah user sudah login
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            navigateToIntro();
            return view;
        }

        // Inisialisasi Firebase Database
        databaseRef = FirebaseDatabase.getInstance().getReference();

        // Inisialisasi Views
        tvnama = view.findViewById(R.id.tvnama);
        tvnisn = view.findViewById(R.id.tvnisn);
        tvkelas = view.findViewById(R.id.tvkelas);
        tv_nomor = view.findViewById(R.id.tv_nomor);
        tvnamawalikelas = view.findViewById(R.id.namawalikelas);

        // Load data dari Firebase
        loadProfile();
        loadWaliKelas();

        // Setup listeners
        CardView logoutCard = view.findViewById(R.id.card_logout);
        logoutCard.setOnClickListener(v -> showLogoutConfirmationDialog());

        CardView gantiPw = view.findViewById(R.id.gantipw);
        ImageView icEditPw = view.findViewById(R.id.ic_editpw);
        gantiPw.setOnClickListener(v -> openUbahSandiActivity());
        icEditPw.setOnClickListener(v -> openUbahSandiActivity());

        return view;
    }

    private void loadProfile() {
        String uid = currentUser.getUid();

        Log.d(TAG, "Loading profile for UID: " + uid);

        // Path: users/{uid}/profile
        databaseRef.child("users").child(uid).child("profile")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            try {
                                String nama = snapshot.child("nama").getValue(String.class);
                                String nisn = snapshot.child("nisn").getValue(String.class);
                                String kelas = snapshot.child("kelas").getValue(String.class);
                                String nomorHp = snapshot.child("nomor_hp").getValue(String.class);

                                tvnama.setText(nama != null ? nama : "Nama tidak tersedia");
                                tvnisn.setText(nisn != null ? nisn : "-");
                                tvkelas.setText(kelas != null ? kelas : "-");
                                tv_nomor.setText(nomorHp != null ? nomorHp : "-");

                                Log.d(TAG, "Profile loaded successfully");
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing profile data: " + e.getMessage());
                                showDetailedErrorAlert("Kesalahan Data",
                                        "Gagal memuat data profil: " + e.getMessage());
                            }
                        } else {
                            Log.w(TAG, "Profile data not found for UID: " + uid);
                            showDetailedErrorAlert("Data Tidak Ditemukan",
                                    "Profil Anda belum tersedia di database.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Firebase error: " + error.getMessage());
                        showDetailedErrorAlert("Kesalahan Database",
                                "Gagal memuat profil: " + error.getMessage());
                    }
                });
    }

    private void loadWaliKelas() {
        String uid = currentUser.getUid();

        // Ambil kelas user terlebih dahulu
        databaseRef.child("users").child(uid).child("profile").child("kelas")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String kelas = snapshot.getValue(String.class);

                            if (kelas != null) {
                                // Ambil wali kelas berdasarkan kelas
                                // Path: wali_kelas/{kelas}
                                databaseRef.child("wali_kelas").child(kelas)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot waliSnapshot) {
                                                if (waliSnapshot.exists()) {
                                                    String namaWali = waliSnapshot.child("nama").getValue(String.class);
                                                    tvnamawalikelas.setText(namaWali != null ? namaWali : "Tidak ditemukan");
                                                } else {
                                                    tvnamawalikelas.setText("Tidak ditemukan");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Log.e(TAG, "Error loading wali kelas: " + error.getMessage());
                                                tvnamawalikelas.setText("Gagal memuat");
                                            }
                                        });
                            } else {
                                tvnamawalikelas.setText("Kelas tidak ditemukan");
                            }
                        } else {
                            tvnamawalikelas.setText("Data kelas tidak tersedia");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error loading kelas: " + error.getMessage());
                        tvnamawalikelas.setText("Gagal memuat");
                    }
                });
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
        FirebaseAuth.getInstance().signOut();
        navigateToIntro();
    }

    private void navigateToIntro() {
        Intent intent = new Intent(getActivity(), IntroActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private void showDetailedErrorAlert(String title, String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message + "\n\nSilakan coba lagi atau hubungi admin.")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("Logout", (dialog, which) -> logout())
                .show();
    }
}