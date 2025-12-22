package com.edulearn.kelompok3.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.edulearn.kelompok3.Activity.DetailMapelActivity;
import com.edulearn.kelompok3.Model.MataPelajaran;
import com.edulearn.kelompok3.R;

import java.util.List;

public class MataPelajaranAdapter extends RecyclerView.Adapter<MataPelajaranAdapter.MataPelajaranViewHolder> {

    private List<MataPelajaran> mataPelajaranList;
    private Context context;

    // Array warna baru dari palet desain modern
    private final int[] colors = {
            R.color.menu_button_blue,
            R.color.menu_button_yellow,
            R.color.menu_button_purple,
            R.color.menu_button_lightblue
    };

    // Constructor
    public MataPelajaranAdapter(Context context, List<MataPelajaran> mataPelajaranList) {
        this.context = context;
        this.mataPelajaranList = mataPelajaranList;
    }

    @NonNull
    @Override
    public MataPelajaranViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kelas, parent, false);
        return new MataPelajaranViewHolder(view);
    }

    public void setMataPelajaranList(List<MataPelajaran> mataPelajaranList) {
        this.mataPelajaranList = mataPelajaranList;
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(@NonNull MataPelajaranViewHolder holder, int position) {
        MataPelajaran mataPelajaran = mataPelajaranList.get(position);

        // Atur data ke view
        holder.tvJudulMapel.setText(mataPelajaran.getNamaMapel());
        holder.tvNamaGuru.setText(mataPelajaran.getNamaGuru());

        // Tentukan warna background berdasarkan posisi (loop kembali ke awal setelah 4 item)
        int colorIndex = position % colors.length; // Ambil indeks warna berdasarkan posisi
        int backgroundColor = context.getResources().getColor(colors[colorIndex]);

        // Terapkan warna ke background `ConstraintLayout`
        holder.bgWarna.setBackgroundColor(backgroundColor);

        // Klik pada item untuk membuka DetailMapelActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailMapelActivity.class);
            intent.putExtra("id_mapel", mataPelajaran.getId());
            intent.putExtra("nama_mapel", mataPelajaran.getNamaMapel());
            intent.putExtra("nama_guru", mataPelajaran.getNamaGuru());
            context.startActivity(intent);
        });

        // Klik pada tombol "Masuk Kelas" untuk membuka DetailMapelActivity
        holder.btnMasukKelas.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailMapelActivity.class);
            intent.putExtra("id_mapel", mataPelajaran.getId());
            intent.putExtra("nama_mapel", mataPelajaran.getNamaMapel());
            intent.putExtra("nama_guru", mataPelajaran.getNamaGuru());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mataPelajaranList.size();
    }

    public static class MataPelajaranViewHolder extends RecyclerView.ViewHolder {
        TextView tvJudulMapel, tvNamaGuru;
        View bgWarna;
        TextView btnMasukKelas; // Ubah dari Button ke TextView

        public MataPelajaranViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJudulMapel = itemView.findViewById(R.id.tv_judul_mapel);
            tvNamaGuru = itemView.findViewById(R.id.tv_guru); // DIUBAH
            bgWarna = itemView.findViewById(R.id.layout_background); // DIUBAH
            btnMasukKelas = itemView.findViewById(R.id.btn_masuk); // DIUBAH
        }
    }
}
