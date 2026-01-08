package com.edulearn.kelompok3.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.edulearn.kelompok3.Adapter.LaporanAkademikAdapter;
import com.edulearn.kelompok3.Model.LaporanAkademik;
import com.edulearn.kelompok3.R;

import java.util.ArrayList;
import java.util.List;

// Ini adalah file baru, rumah untuk fitur laporan akademik kita.
public class AkademikFragment extends Fragment {

    // Deklarasi untuk komponen tampilan
    private AutoCompleteTextView dropdownKelas;
    private AutoCompleteTextView dropdownPeriode;
    private Button tombolTampilkan;
    private RecyclerView rvDaftarLaporan;
    private LaporanAkademikAdapter laporanAdapter;
    private List<LaporanAkademik> laporanList = new ArrayList<>();

    public static AkademikFragment newInstance() {
        return new AkademikFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 1. Menghubungkan file logika ini dengan file tampilannya (fragment_akademik.xml)
        View view = inflater.inflate(R.layout.fragment_akademik, container, false);

        // 2. Mengenali setiap komponen dari file tampilan
        dropdownKelas = view.findViewById(R.id.dropdown_kelas);
        dropdownPeriode = view.findViewById(R.id.dropdown_periode);
        tombolTampilkan = view.findViewById(R.id.btn_tampilkan_laporan);
        rvDaftarLaporan = view.findViewById(R.id.rv_laporan_akademik);

        // 3. Setup RecyclerView
        setupRecyclerView();

        // 4. Mengisi pilihan untuk dropdown Kelas dan Periode
        isiPilihanDropdown();

        // 5. Memberi perintah pada tombol "Tampilkan Laporan"
        tombolTampilkan.setOnClickListener(v -> {
            // Saat tombol diklik, panggil metode untuk menampilkan data contoh
            tampilkanDataContoh();
        });

        return view;
    }

    private void setupRecyclerView() {
        // Mengatur layout manager untuk RecyclerView
        rvDaftarLaporan.setLayoutManager(new LinearLayoutManager(getContext()));
        // Membuat dan memasang adapter ke RecyclerView
        laporanAdapter = new LaporanAkademikAdapter(getContext(), laporanList);
        rvDaftarLaporan.setAdapter(laporanAdapter);
    }

    private void isiPilihanDropdown() {
        // Logika cerdas: Hanya menampilkan kelas yang sudah/sedang dijalani siswa.
        ArrayList<String> daftarKelas = new ArrayList<>();
        daftarKelas.add("Kelas 1");
        daftarKelas.add("Kelas 2");
        daftarKelas.add("Kelas 3"); // (Nanti diisi data asli)

        ArrayList<String> daftarPeriode = new ArrayList<>();
        daftarPeriode.add("Semester Ganjil");
        daftarPeriode.add("Semester Genap");

        // Memasang daftar pilihan ke komponen dropdown
        ArrayAdapter<String> adapterKelas = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, daftarKelas);
        dropdownKelas.setAdapter(adapterKelas);

        ArrayAdapter<String> adapterPeriode = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, daftarPeriode);
        dropdownPeriode.setAdapter(adapterPeriode);
    }

    private void tampilkanDataContoh() {
        // Buat daftar kosong untuk diisi data contoh
        List<LaporanAkademik> dataContoh = new ArrayList<>();

        // Tambahkan 3 mata pelajaran contoh
        dataContoh.add(new LaporanAkademik("Matematika", "85", "A-", "98%"));
        dataContoh.add(new LaporanAkademik("Bahasa Indonesia", "92", "A", "100%"));
        dataContoh.add(new LaporanAkademik("Ilmu Pengetahuan Alam (IPA)", "78", "B+", "95%"));

        // Perbarui data di adapter dan beri tahu RecyclerView untuk refresh
        laporanAdapter.updateData(dataContoh);
    }
}
