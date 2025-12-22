package com.edulearn.kelompok3.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.edulearn.kelompok3.Adapter.MataPelajaranAdapter;
import com.edulearn.kelompok3.R;
import com.edulearn.kelompok3.ViewModel.MataPelajaranViewModel;

import java.util.ArrayList;

public class KelasFragment extends Fragment {

    private RecyclerView recyclerView;
    private MataPelajaranAdapter adapter;
    private MataPelajaranViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kelas, container, false);

        recyclerView = view.findViewById(R.id.recycler_mapel);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inisialisasi adapter dengan list kosong
        adapter = new MataPelajaranAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Inisialisasi ViewModel
        viewModel = new ViewModelProvider(this).get(MataPelajaranViewModel.class);

        // Observasi data dari ViewModel
        viewModel.getMataPelajaranListLiveData().observe(getViewLifecycleOwner(), mataPelajaranList -> {
            if (mataPelajaranList != null && !mataPelajaranList.isEmpty()) {
                adapter.setMataPelajaranList(mataPelajaranList);
            }
        });

        // Panggil data dari API
        viewModel.fetchMataPelajaran();

        return view;
    }
}
