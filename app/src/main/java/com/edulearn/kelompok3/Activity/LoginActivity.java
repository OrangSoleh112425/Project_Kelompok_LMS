package com.edulearn.kelompok3.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.edulearn.kelompok3.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    private EditText etIdentitas, etPassword;
    private Button btnSignIn;
    private TextView hubAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();

        etIdentitas = findViewById(R.id.etIdentitas);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        hubAdmin = findViewById(R.id.hubadmin);

        hubAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        btnSignIn.setOnClickListener(v -> {
            String nisn = etIdentitas.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (nisn.isEmpty() || password.isEmpty()) {
                showAlert("Peringatan", "Username dan Password tidak boleh kosong!");
            } else {
                login(nisn, password);
            }
        });
    }

    private void openWhatsApp(String phoneNumber, String message) {
        String url = "https://wa.me/" + phoneNumber + "?text=" + message;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(android.net.Uri.parse(url));
        startActivity(intent);
    }

    private void login(String username, String password) {
        if (password.length() < 8) {
            showAlert("Peringatan", "Password harus minimal 8 karakter.");
            return;
        }

        String email = username + "@edulearn.id";

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        showAlert("Login Gagal", "username atau password salah.");
                    }
                });
    }

    private void showAlert(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.dark_text));
    }
}
