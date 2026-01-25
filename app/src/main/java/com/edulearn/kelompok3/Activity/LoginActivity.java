package com.edulearn.kelompok3.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.edulearn.kelompok3.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText etIdentitas, etPassword;
    private Button btnSignIn;
    private TextView hubAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FLOW", "loginActivity opened");

        FirebaseApp.initializeApp(this); // Inisialisasi Firebase
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://db-lms-edulearn-default-rtdb.asia-southeast1.firebasedatabase.app"
        );

        DatabaseReference ref = database.getReference();

        etIdentitas = findViewById(R.id.etIdentitas);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        hubAdmin = findViewById(R.id.hubadmin);

        hubAdmin.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        btnSignIn.setOnClickListener(v -> {
            String identitas = etIdentitas.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (identitas.isEmpty() || password.isEmpty()) {
                showAlert("Peringatan", "Identitas dan Password tidak boleh kosong!");
            } else {
                login(identitas, password);
            }
        });
//        FirebaseAuth.getInstance().signOut();
    }

    private void login(String identitas, String password) {
        if (password.length() < 8) {
            showAlert("Peringatan", "Password minimal 8 karakter.");
            return;
        }

        // Cek apakah input mengandung @, jika tidak tambahkan domain otomatis
        String emailFinal = identitas.contains("@") ? identitas : identitas + "@edulearn.id";

        auth.signInWithEmailAndPassword(emailFinal, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Berhasil login, pindah ke MainActivity
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        // Ambil pesan error asli dari Firebase agar tidak crash
                        String pesanError = "Gagal login.";
                        if (task.getException() != null) {
                            Exception e = task.getException();
                            if (e instanceof FirebaseAuthInvalidUserException) {
                                pesanError = "User/Email tidak ditemukan.";
                            } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                pesanError = "Password salah.";
                            } else {
                                pesanError = e.getMessage();
                            }
                        }
                        showAlert("Login Gagal", pesanError);
                    }
                });
    }

    private void showAlert(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}