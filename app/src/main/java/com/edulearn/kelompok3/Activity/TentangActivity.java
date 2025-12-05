package com.edulearn.kelompok3.Activity;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.edulearn.kelompok3.R;

public class TentangActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Mengatur file layout yang sudah bersih dan rapi
        setContentView(R.layout.activity_tentang);

        // Menemukan tombol kembali dari layout dan mengatur listener-nya
        ImageView backButton = findViewById(R.id.ic_kembali);
        backButton.setOnClickListener(v -> {
            // Menutup activity ini dan kembali ke halaman sebelumnya
            finish();
        });
    }
}
