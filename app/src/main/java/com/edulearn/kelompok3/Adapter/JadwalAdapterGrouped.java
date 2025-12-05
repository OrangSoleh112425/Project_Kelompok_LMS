package com.edulearn.kelompok3.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.edulearn.kelompok3.Model.ModelJadwalGrouped;
import com.edulearn.kelompok3.R;

import java.util.ArrayList;
import java.util.List;

public class JadwalAdapterGrouped extends RecyclerView.Adapter<JadwalAdapterGrouped.ViewHolder> {

    private final Context context;
    private List<ModelJadwalGrouped> jadwalGroupedList;

    public JadwalAdapterGrouped(Context context, List<ModelJadwalGrouped> jadwalGroupedList) {
        this.context = context;
        this.jadwalGroupedList = jadwalGroupedList != null ? jadwalGroupedList : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Menggunakan layout grup baru yang kita buat
        View view = LayoutInflater.from(context).inflate(R.layout.item_grup_jadwal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelJadwalGrouped group = jadwalGroupedList.get(position);
        
        // Set nama hari
        holder.tvHari.setText(group.getHari());

        // Siapkan RecyclerView internal untuk daftar mapel
        holder.rvMapel.setLayoutManager(new LinearLayoutManager(context));
        
        // Buat adapter detail dan berikan daftar mapel dari grup saat ini
        JadwalDetailAdapter detailAdapter = new JadwalDetailAdapter(group.getMapelList());
        holder.rvMapel.setAdapter(detailAdapter);
    }

    @Override
    public int getItemCount() {
        return jadwalGroupedList.size();
    }

    public void updateData(List<ModelJadwalGrouped> newGroupedList) {
        this.jadwalGroupedList = newGroupedList != null ? newGroupedList : new ArrayList<>();
        notifyDataSetChanged();
    }

    // ViewHolder sekarang merujuk ke elemen di item_grup_jadwal.xml
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHari;
        RecyclerView rvMapel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHari = itemView.findViewById(R.id.tv_hari_grup);
            rvMapel = itemView.findViewById(R.id.rv_mapel_grup);
        }
    }
}
