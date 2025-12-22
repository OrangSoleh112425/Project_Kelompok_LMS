package com.edulearn.kelompok3.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.edulearn.kelompok3.Model.ModelJadwalDetail;
import com.edulearn.kelompok3.R;

import java.util.List;

public class JadwalDetailAdapter extends RecyclerView.Adapter<JadwalDetailAdapter.ViewHolder> {

    private final List<ModelJadwalDetail> mapelList;

    public JadwalDetailAdapter(List<ModelJadwalDetail> mapelList) {
        this.mapelList = mapelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_mapel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelJadwalDetail mapel = mapelList.get(position);
        holder.tvNamaMapel.setText(mapel.getNamaMapel());
        holder.tvJamMapel.setText(mapel.getJamMulai() + " - " + mapel.getJamSelesai());
    }

    @Override
    public int getItemCount() {
        return mapelList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamaMapel;
        TextView tvJamMapel;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaMapel = itemView.findViewById(R.id.tv_nama_mapel_detail);
            tvJamMapel = itemView.findViewById(R.id.tv_jam_mapel_detail);
        }
    }
}
