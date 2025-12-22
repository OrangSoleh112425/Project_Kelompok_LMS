package com.edulearn.kelompok3.Adapter;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.edulearn.kelompok3.Fragment.LatsolFragment;
import com.edulearn.kelompok3.Fragment.MateriFragment; // Asumsi ada fragment ini
import com.edulearn.kelompok3.Fragment.TugasFragment;

public class DetailMapelAdapter extends FragmentStateAdapter {

    private final int idMapel;
    private final String namaMapel;

    public DetailMapelAdapter(@NonNull FragmentActivity fragmentActivity, int idMapel, String namaMapel) {
        super(fragmentActivity);
        this.idMapel = idMapel;
        this.namaMapel = namaMapel;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new MateriFragment(); // Tab pertama
                break;
            case 1:
                fragment = LatsolFragment.newInstance(idMapel); // Tab kedua
                break;
            case 2:
                fragment = TugasFragment.newInstance(idMapel, namaMapel); // Tab ketiga
                break;
            default:
                // Fallback ke fragment pertama jika posisi tidak valid
                fragment = new MateriFragment();
                break;
        }

        // Jika fragment membutuhkan argumen, kita bisa memberikannya di sini
        // Contoh: LatsolFragment dan TugasFragment sudah menggunakan newInstance
        // Untuk MateriFragment, jika perlu, bisa ditambahkan juga
        if (fragment.getArguments() == null) {
            Bundle args = new Bundle();
            args.putInt("id_mapel", idMapel);
            args.putString("nama_mapel", namaMapel);
            fragment.setArguments(args);
        }

        return fragment;
    }

    @Override
    public int getItemCount() {
        return 3; // Jumlah tab: Materi, Latihan Soal, Tugas
    }
}
