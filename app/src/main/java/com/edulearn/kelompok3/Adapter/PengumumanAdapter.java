package com.edulearn.kelompok3.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.edulearn.kelompok3.Model.Pengumuman;
import com.edulearn.kelompok3.R;

import java.util.List;

public class PengumumanAdapter extends RecyclerView.Adapter<PengumumanAdapter.ViewHolder> {

    private Context context;
    private List<Pengumuman> pengumumanList;

    public PengumumanAdapter(Context context, List<Pengumuman> pengumumanList) {
        this.context = context;
        this.pengumumanList = pengumumanList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pengumuman, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pengumuman pengumuman = pengumumanList.get(position);

        holder.tvJudul.setText(pengumuman.getJudul());
        holder.tvTanggal.setText(pengumuman.getTanggal());

        // Menggunakan Glide untuk memuat gambar dari URL
        Glide.with(context)
                .load(pengumuman.getImageUrl())
                .placeholder(R.drawable.ic_image) // Gambar sementara saat loading
                .error(R.drawable.ic_image)       // Gambar jika terjadi error
                .into(holder.ivGambar);

        // Nanti bisa ditambahkan OnClickListener untuk membuka detail pengumuman
        // holder.itemView.setOnClickListener(v -> { ... });
    }

    @Override
    public int getItemCount() {
        return pengumumanList.size();
    }

    public void updateData(List<Pengumuman> newList) {
        this.pengumumanList.clear();
        this.pengumumanList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivGambar;
        TextView tvJudul, tvTanggal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGambar = itemView.findViewById(R.id.iv_gambar_pengumuman);
            tvJudul = itemView.findViewById(R.id.tv_judul_pengumuman);
            tvTanggal = itemView.findViewById(R.id.tv_tanggal_pengumuman);
        }
    }
}
