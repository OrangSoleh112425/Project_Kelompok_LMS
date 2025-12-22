package com.edulearn.kelompok3.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.edulearn.kelompok3.Activity.AkademikDetailActivity;
import com.edulearn.kelompok3.Model.LaporanAkademik;
import com.edulearn.kelompok3.R;
import java.util.List;

public class LaporanAkademikAdapter extends RecyclerView.Adapter<LaporanAkademikAdapter.ViewHolder> {

    private Context context;
    private List<LaporanAkademik> laporanList;

    public LaporanAkademikAdapter(Context context, List<LaporanAkademik> laporanList) {
        this.context = context;
        this.laporanList = laporanList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_laporan_akademik, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LaporanAkademik laporan = laporanList.get(position);

        holder.tvNamaMapel.setText(laporan.getNamaMapel());
        holder.tvNilaiAkhir.setText(laporan.getNilaiAkhir());
        holder.tvNilaiHuruf.setText(laporan.getNilaiHuruf());
        holder.tvKehadiran.setText(laporan.getKehadiran());

        // Membuat seluruh item bisa diklik
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AkademikDetailActivity.class);
            // Mengirim nama mata pelajaran ke halaman detail
            intent.putExtra("NAMA_MAPEL", laporan.getNamaMapel());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return laporanList.size();
    }

    public void updateData(List<LaporanAkademik> newList) {
        this.laporanList.clear();
        this.laporanList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamaMapel, tvNilaiAkhir, tvNilaiHuruf, tvKehadiran;
        ImageView ivArrow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaMapel = itemView.findViewById(R.id.tv_nama_mapel);
            tvNilaiAkhir = itemView.findViewById(R.id.tv_nilai_akhir);
            tvNilaiHuruf = itemView.findViewById(R.id.tv_nilai_huruf);
            tvKehadiran = itemView.findViewById(R.id.tv_kehadiran);
            ivArrow = itemView.findViewById(R.id.iv_arrow);
        }
    }
}
