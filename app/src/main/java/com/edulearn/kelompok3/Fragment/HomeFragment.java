package com.edulearn.kelompok3.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.edulearn.kelompok3.Activity.DetailJadwalActivity;
import com.edulearn.kelompok3.Activity.PanduanActivity;
import com.edulearn.kelompok3.Activity.TentangActivity;
import com.edulearn.kelompok3.Adapter.HomeJadwalAdapter;
import com.edulearn.kelompok3.Adapter.PengumumanAdapter;
import com.edulearn.kelompok3.Model.ModelJadwalDetail;
import com.edulearn.kelompok3.Model.ModelJadwalGrouped;
import com.edulearn.kelompok3.Model.Pengumuman;
import com.edulearn.kelompok3.R;
import com.edulearn.kelompok3.ViewModel.JadwalViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private TextView tvWelcome, tvNoJadwal;
    private RecyclerView rvJadwalHariIni, rvPengumuman;
    private HomeJadwalAdapter homeJadwalAdapter;
    private PengumumanAdapter pengumumanAdapter;
    private JadwalViewModel jadwalViewModel;
    private RequestQueue requestQueue;
    private ProgressBar loading;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("FLOW", "HomeFragment onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(rootView);
        setupHeader(rootView);
        setupJadwal();
        setupPengumuman();
        setupListeners(rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("FLOW", "HomeFragment onViewCreated");
    }


    private void initViews(View rootView) {
        tvWelcome = rootView.findViewById(R.id.tv_welcome);
        rvJadwalHariIni = rootView.findViewById(R.id.rv_jadwal_hari_ini);
        tvNoJadwal = rootView.findViewById(R.id.tv_no_jadwal);
        rvPengumuman = rootView.findViewById(R.id.rv_pengumuman);
        loading = rootView.findViewById(R.id.loading);
    }

    private void setupHeader(View rootView) {
        Map<String, String> quotes = new HashMap<>();
        quotes.put("Senin", "Awali pekan ini dengan belajar penuh semangat!");
        quotes.put("Selasa", "Belajar hari ini adalah investasi masa depan.");
        quotes.put("Rabu", "Raih pemahaman baru di tengah pekan.");
        quotes.put("Kamis", "Tetap fokus, ilmu adalah kunci kesuksesan.");
        quotes.put("Jumat", "Akhiri hari dengan prestasi terbaik.");
        quotes.put("Sabtu", "Santai, namun tetap asah pengetahuan.");
        quotes.put("Minggu", "Persiapkan diri untuk belajar yang produktif.");

        TextView tvQuote = rootView.findViewById(R.id.tv_quote);
        String currentDay = getCurrentDay();
//        tvQuote.setText(quotes.getOrDefault(currentDay, "Belajar adalah jendela dunia!"));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            tvWelcome.setText("Selamat Datang, " + user.getDisplayName() + "!");
        } else {
            tvWelcome.setText("Selamat Datang!");
        }
    }

    private void setupJadwal() {

        rvJadwalHariIni.setLayoutManager(new LinearLayoutManager(getContext()));
        homeJadwalAdapter = new HomeJadwalAdapter(new ArrayList<>());
        rvJadwalHariIni.setAdapter(homeJadwalAdapter);
//        loading.setVisibility(View.GONE);

        // ✅ INIT VIEWMODEL
        jadwalViewModel = new ViewModelProvider(this).get(JadwalViewModel.class);
        observeViewModel();

        requestQueue = Volley.newRequestQueue(requireContext());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            jadwalViewModel.fetchJadwal(user.getUid());
        } else {
            loading.setVisibility(View.GONE);
            tvNoJadwal.setVisibility(View.VISIBLE);
            tvNoJadwal.setText("User belum login");
        }
    }

    private void observeViewModel() {

        jadwalViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            loading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        jadwalViewModel.getJadwalGroupedList().observe(getViewLifecycleOwner(), groupedList -> {

            if (groupedList == null || groupedList.isEmpty()) {
                rvJadwalHariIni.setVisibility(View.GONE);
                tvNoJadwal.setVisibility(View.VISIBLE);
                tvNoJadwal.setText("Tidak ada jadwal hari ini.");
                return;
            }

            String currentDay = getCurrentDay();
            List<ModelJadwalDetail> todaySchedules = new ArrayList<>();

            for (ModelJadwalGrouped group : groupedList) {
                if (group.getHari().equalsIgnoreCase(currentDay)) {
                    todaySchedules = group.getMapelList();
                    break;
                }
            }

            if (todaySchedules.isEmpty()) {
                rvJadwalHariIni.setVisibility(View.GONE);
                tvNoJadwal.setVisibility(View.VISIBLE);
                tvNoJadwal.setText("Tidak ada jadwal hari ini.");
            } else {
                rvJadwalHariIni.setVisibility(View.VISIBLE);
                tvNoJadwal.setVisibility(View.GONE);

                if (todaySchedules.size() > 3) {
                    homeJadwalAdapter.updateData(todaySchedules.subList(0, 3));
                } else {
                    homeJadwalAdapter.updateData(todaySchedules);
                }
            }
        });

        jadwalViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                rvJadwalHariIni.setVisibility(View.GONE);
                tvNoJadwal.setVisibility(View.VISIBLE);
                tvNoJadwal.setText("⚠️ Gagal memuat jadwal.");
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupPengumuman() {

        rvPengumuman.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        List<Pengumuman> pengumumanList = new ArrayList<>();
        pengumumanList.add(new Pengumuman("PPDB 2024/2025 Dibuka", "15 Juli 2024",
                "https://images.unsplash.com/photo-1523050854058-8df90110c9f1?w=500"));
        pengumumanList.add(new Pengumuman("Lomba 17 Agustus", "14 Juli 2024",
                "https://images.unsplash.com/photo-1503676260728-1c00da094a0b?w=500"));

        pengumumanAdapter = new PengumumanAdapter(getContext(), pengumumanList);
        rvPengumuman.setAdapter(pengumumanAdapter);
    }

    private void setupListeners(View rootView) {

        rootView.findViewById(R.id.tentang_card)
                .setOnClickListener(v -> startActivity(new Intent(getActivity(), TentangActivity.class)));

        rootView.findViewById(R.id.panduan_card)
                .setOnClickListener(v -> startActivity(new Intent(getActivity(), PanduanActivity.class)));

        rootView.findViewById(R.id.materi_card)
                .setOnClickListener(v -> Toast.makeText(getContext(), "Fitur segera hadir", Toast.LENGTH_SHORT).show());

        rootView.findViewById(R.id.tv_see_all_jadwal)
                .setOnClickListener(v -> startActivity(new Intent(getActivity(), DetailJadwalActivity.class)));
    }

    private String getCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY: return "Senin";
            case Calendar.TUESDAY: return "Selasa";
            case Calendar.WEDNESDAY: return "Rabu";
            case Calendar.THURSDAY: return "Kamis";
            case Calendar.FRIDAY: return "Jumat";
            case Calendar.SATURDAY: return "Sabtu";
            case Calendar.SUNDAY: return "Minggu";
            default: return "";
        }
    }
}
