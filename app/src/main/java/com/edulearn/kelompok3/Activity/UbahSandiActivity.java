package com.edulearn.kelompok3.Activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.edulearn.kelompok3.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UbahSandiActivity extends AppCompatActivity {

    private EditText edtCurrentPassword, edtPassword, edtConfirmPassword;
    private Button btnKonfirm;
    private ImageView backIcon;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubahsandi);

        // Inisialisasi Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://db-lms-edulearn-default-rtdb.asia-southeast1.firebasedatabase.app"
        );

        DatabaseReference ref = database.getReference();

        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "User belum login", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inisialisasi komponen UI
        edtCurrentPassword = findViewById(R.id.edtCurrentPassword);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtconfirmPassword);
        btnKonfirm = findViewById(R.id.btnkonfirm);
        backIcon = findViewById(R.id.back_icon);

        // Tambahkan listener untuk kembali ke aktivitas sebelumnya
        backIcon.setOnClickListener(v -> finish());

        // Listener untuk tombol Konfirmasi
        btnKonfirm.setOnClickListener(v -> updatePassword());
    }

    private void updatePassword() {
        String currentPassword = edtCurrentPassword.getText().toString().trim();
        String newPassword = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        // Validasi input
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Peringatan", "Semua kolom harus diisi");
            return;
        }

        if (newPassword.length() < 8 || confirmPassword.length() < 8) {
            showAlert("Peringatan", "Kata sandi baru minimal 8 karakter");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert("Peringatan", "Kata sandi baru dan konfirmasi tidak cocok");
            return;
        }

        // Ambil email user
        String email = currentUser.getEmail();

        if (email == null || email.isEmpty()) {
            showAlert("Error", "Email user tidak ditemukan");
            return;
        }

        // Re-authenticate user dengan password lama
        AuthCredential credential = EmailAuthProvider.getCredential(email, currentPassword);

        currentUser.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Re-authentication berhasil, update password
                        currentUser.updatePassword(newPassword)
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        Toast.makeText(UbahSandiActivity.this,
                                                "Kata sandi berhasil diubah",
                                                Toast.LENGTH_SHORT).show();

                                        // Clear input fields
                                        edtCurrentPassword.setText("");
                                        edtPassword.setText("");
                                        edtConfirmPassword.setText("");

                                        // Kembali ke activity sebelumnya
                                        finish();
                                    } else {
                                        String errorMsg = updateTask.getException() != null ?
                                                updateTask.getException().getMessage() :
                                                "Gagal mengubah password";
                                        showAlert("Kesalahan", errorMsg);
                                    }
                                });
                    } else {
                        // Re-authentication gagal (password lama salah)
                        showAlert("Kesalahan", "Kata sandi saat ini tidak cocok");
                    }
                });
    }

    // Fungsi untuk menampilkan alert dialog dengan tombol OK berwarna biru
    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

        // Mengubah warna tombol OK
        Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        okButton.setTextColor(ContextCompat.getColor(this, R.color.dark_text));
    }
}