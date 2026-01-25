package com.edulearn.kelompok3.Activity;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import com.google.firebase.auth.FirebaseAuth;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.edulearn.kelompok3.R;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("FLOW", "IntroActivity opened");

        setContentView(R.layout.activity_intro);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish(); // WAJIB
        } else {
            View rootLayout = findViewById(R.id.main);
            rootLayout.setOnClickListener(v -> navigateToLogin());
        }
    }

    private void navigateToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish(); // tutup intro
    }
}
