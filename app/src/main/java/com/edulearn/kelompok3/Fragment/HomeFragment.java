package com.edulearn.kelompok3.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private String userToken;
    private TextView tvWelcome, tvNoJadwal;
    private RecyclerView rvJadwalHariIni, rvPengumuman;
    private HomeJadwalAdapter homeJadwalAdapter;
    private PengumumanAdapter pengumumanAdapter;
    private JadwalViewModel jadwalViewModel;
    private RequestQueue requestQueue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Inisialisasi Views
        initViews(rootView);

        // Setup Header
        setupHeader(rootView);

        // Setup Jadwal
        setupJadwal();

        // Setup Pengumuman
        setupPengumuman();

        // Setup Listeners
        setupListeners(rootView);

        return rootView;
    }

    private void initViews(View rootView) {
        tvWelcome = rootView.findViewById(R.id.tv_welcome);
        rvJadwalHariIni = rootView.findViewById(R.id.rv_jadwal_hari_ini);
        tvNoJadwal = rootView.findViewById(R.id.tv_no_jadwal);
        rvPengumuman = rootView.findViewById(R.id.rv_pengumuman);
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
        String quoteForToday;
        if (quotes.containsKey(currentDay)) {
            quoteForToday = quotes.get(currentDay);
        } else {
            quoteForToday = "Belajar adalah jendela dunia!";
        }
        tvQuote.setText(quoteForToday);

        tvWelcome.setText("Selamat Datang, Arya!");
    }

    private void setupJadwal() {
        rvJadwalHariIni.setLayoutManager(new LinearLayoutManager(getContext()));
        homeJadwalAdapter = new HomeJadwalAdapter(new ArrayList<>());
        rvJadwalHariIni.setAdapter(homeJadwalAdapter);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_session", requireContext().MODE_PRIVATE);
        userToken = sharedPreferences.getString("user_token", null);

        jadwalViewModel = new ViewModelProvider(requireActivity()).get(JadwalViewModel.class);
        observeViewModel();

        requestQueue = Volley.newRequestQueue(requireContext());
        if (userToken != null && !userToken.isEmpty()) {
            jadwalViewModel.fetchJadwal(requestQueue, userToken);
        } else {
            Log.w("HomeFragment", "User token is null or empty, cannot fetch schedule.");
        }
    }

    private void setupPengumuman() {
        rvPengumuman.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        List<Pengumuman> pengumumanList = new ArrayList<>();
        
        // Data Contoh Pengumuman
        pengumumanList.add(new Pengumuman("PPDB Tahun Ajaran 2024/2025 Telah Dibuka!", "15 Juli 2024", "https://images.unsplash.com/photo-1599837563057-70b3dd153673?w=500"));
        pengumumanList.add(new Pengumuman("Jadwal Lomba 17 Agustus Tingkat Sekolah", "14 Juli 2024", "https://images.unsplash.com/photo-1565879313438-d65a8a4f6d74?w=500"));
        pengumumanList.add(new Pengumuman("Informasi Kegiatan Karyawisata Kelas 6", "12 Juli 2024", "https://images.unsplash.com/photo-1523050854058-8df90110c9f1?w=500"));

        pengumumanAdapter = new PengumumanAdapter(getContext(), pengumumanList);
        rvPengumuman.setAdapter(pengumumanAdapter);
    }

    private void observeViewModel() {
        jadwalViewModel.getJadwalGroupedList().observe(getViewLifecycleOwner(), groupedList -> {
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
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupListeners(View rootView) {
        rootView.findViewById(R.id.tentang_card).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), TentangActivity.class));
        });

        rootView.findViewById(R.id.panduan_card).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), PanduanActivity.class));
        });

        rootView.findViewById(R.id.materi_card).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Fitur Materi segera hadir!", Toast.LENGTH_SHORT).show();
        });

        rootView.findViewById(R.id.tv_see_all_jadwal).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DetailJadwalActivity.class);
            intent.putExtra("user_token", userToken);
            startActivity(intent);
        });
    }

    private String getCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
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
