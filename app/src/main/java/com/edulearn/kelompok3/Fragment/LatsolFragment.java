package com.edulearn.kelompok3.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.edulearn.kelompok3.Activity.DetailLatihanSoalActivity;
import com.edulearn.kelompok3.Adapter.LatihanSoalAdapter;
import com.edulearn.kelompok3.Model.ModelLatihanSoal;
import com.edulearn.kelompok3.R;
import com.edulearn.kelompok3.ViewModel.LatihanSoalViewModel;

import java.util.ArrayList;

// Implementasikan OnItemClickListener dari adapter
public class LatsolFragment extends Fragment implements LatihanSoalAdapter.OnItemClickListener {

    private static final String ARG_ID_MAPEL = "id_mapel";
    private int idMapel;
    private RecyclerView recyclerView;
    private LatihanSoalAdapter adapter;
    private LatihanSoalViewModel latihanSoalViewModel;
    private TextView tvNoLatihanSoal;

    public static LatsolFragment newInstance(int idMapel) {
        LatsolFragment fragment = new LatsolFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID_MAPEL, idMapel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idMapel = getArguments().getInt(ARG_ID_MAPEL);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_latsol, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewLatihanSoal);
        tvNoLatihanSoal = view.findViewById(R.id.tv_noLatihanSoal);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inisialisasi adapter dengan memberikan 'this' sebagai listener.
        // Ini memastikan metode onItemClick di bawah akan dipanggil.
        adapter = new LatihanSoalAdapter(getContext(), new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        latihanSoalViewModel = new ViewModelProvider(this).get(LatihanSoalViewModel.class);

        latihanSoalViewModel.getLatihanSoalListLiveData().observe(getViewLifecycleOwner(), latihanSoalList -> {
            if (latihanSoalList != null && !latihanSoalList.isEmpty()) {
                adapter.setLatihanSoalList(latihanSoalList);
                recyclerView.setVisibility(View.VISIBLE);
                tvNoLatihanSoal.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                tvNoLatihanSoal.setVisibility(View.VISIBLE);
                tvNoLatihanSoal.setText("Tidak ada latihan soal untuk mata pelajaran ini.");
            }
        });

        latihanSoalViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                tvNoLatihanSoal.setText(error);
                tvNoLatihanSoal.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        latihanSoalViewModel.fetchLatihanSoal(idMapel);

        return view;
    }

    /**
     * Ini adalah metode yang akan dipanggil oleh Adapter saat tombol "Kerjakan" di klik.
     * Di sinilah kita membuat "paket" dan mengirimkannya.
     */
    @Override
    public void onItemClick(ModelLatihanSoal latihanSoal) {
        // Tambahkan Log untuk memastikan metode ini dipanggil
        Log.d("LatsolFragment", "onItemClick: Membuka detail untuk ID: " + latihanSoal.getId() + ", Judul: " + latihanSoal.getJudulLatihan());

        // Buat Intent untuk membuka DetailLatihanSoalActivity
        Intent intent = new Intent(getActivity(), DetailLatihanSoalActivity.class);

        // Masukkan ID dan Judul ke dalam "paket" Intent
        intent.putExtra("id_soal", latihanSoal.getId());
        intent.putExtra("judul_soal", latihanSoal.getJudulLatihan());

        // Kirim paket dan mulai activity baru
        startActivity(intent);
    }
}
