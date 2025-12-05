package com.edulearn.kelompok3.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.edulearn.kelompok3.Adapter.TugasAdapter;
import com.edulearn.kelompok3.R;
import com.edulearn.kelompok3.ViewModel.TugasViewModel;

import java.util.ArrayList;

public class TugasFragment extends Fragment {

    private static final String ARG_ID_MAPEL = "id_mapel";
    private static final String ARG_NAMA_MAPEL = "nama_mapel";

    private RecyclerView recyclerView;
    private TugasAdapter adapter;
    private TugasViewModel tugasViewModel;
    private TextView tvNoData;
    private int idMapel;
    private String namaMapel;

    public static TugasFragment newInstance(int idMapel, String namaMapel) {
        TugasFragment fragment = new TugasFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID_MAPEL, idMapel);
        args.putString(ARG_NAMA_MAPEL, namaMapel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idMapel = getArguments().getInt(ARG_ID_MAPEL);
            namaMapel = getArguments().getString(ARG_NAMA_MAPEL);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tugas, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        tvNoData = view.findViewById(R.id.tv_no_data);

        adapter = new TugasAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        tugasViewModel = new ViewModelProvider(this).get(TugasViewModel.class);

        tugasViewModel.getTugasListLiveData().observe(getViewLifecycleOwner(), tugasList -> {
            if (tugasList != null && !tugasList.isEmpty()) {
                adapter.updateData(tugasList);
                tvNoData.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            } else {
                tvNoData.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        tugasViewModel.getErrorMessageLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        tugasViewModel.fetchTugas(idMapel);

        return view;
    }
}
