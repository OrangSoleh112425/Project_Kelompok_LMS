package com.edulearn.kelompok3.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.edulearn.kelompok3.R;

import java.util.ArrayList;
import java.util.List;

public class AkademikDetailActivity extends AppCompatActivity {

    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_akademik_detail);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Ambil nama mapel dari intent (jika ada)
        String namaMapel = getIntent().getStringExtra("NAMA_MAPEL");
        if (namaMapel != null) {
            getSupportActionBar().setTitle("Analisis Nilai: " + namaMapel);
        }

        // Inisialisasi Grafik
        lineChart = findViewById(R.id.line_chart);

        // Tampilkan data contoh pada grafik
        setupLineChart();
        tampilkanDataGrafikContoh();
        tampilkanKomponenNilaiContoh();
    }

    private void setupLineChart() {
        lineChart.getDescription().setEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setGranularity(1f);
    }

    private void tampilkanDataGrafikContoh() {
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 75)); // Tugas 1
        entries.add(new Entry(1, 88)); // Tugas 2
        entries.add(new Entry(2, 82)); // UTS
        entries.add(new Entry(3, 90)); // Tugas 3
        entries.add(new Entry(4, 95)); // UAS

        LineDataSet dataSet = new LineDataSet(entries, "Nilai Siswa");
        dataSet.setColor(Color.parseColor("#007AFF"));
        dataSet.setCircleColor(Color.parseColor("#007AFF"));
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleRadius(5f);
        dataSet.setValueTextSize(12f);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Label untuk sumbu X (Tugas, UTS, UAS)
        final String[] labels = new String[]{"Tugas 1", "Tugas 2", "UTS", "Tugas 3", "UAS"};
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));

        lineChart.invalidate(); // Refresh chart
    }
    
    private void tampilkanKomponenNilaiContoh() {
        // Karena kita tidak memiliki ID spesifik untuk layout komponen,
        // kita tambahkan secara manual ke parent layout di dalam CardView.
        // Ini adalah pendekatan sederhana untuk data contoh.
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Tombol kembali di toolbar
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
