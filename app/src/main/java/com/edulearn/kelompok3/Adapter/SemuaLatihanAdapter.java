package com.edulearn.kelompok3.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.edulearn.kelompok3.Activity.DetailLatihanSoalActivity;
import com.edulearn.kelompok3.Model.ModelLatihanSoal;
import com.edulearn.kelompok3.R;

import java.util.ArrayList;
import java.util.List;

public class SemuaLatihanAdapter extends RecyclerView.Adapter<SemuaLatihanAdapter.ViewHolder> {

    private final Context context;
    private List<ModelLatihanSoal> latihanSoalList = new ArrayList<>();

    public SemuaLatihanAdapter(Context context) {
        this.context = context;
    }

    public void setLatihanSoalList(List<ModelLatihanSoal> latihanSoalList) {
        this.latihanSoalList = latihanSoalList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // PERBAIKAN: Menggunakan layout baru yang memiliki TextView untuk nama mapel
        View view = LayoutInflater.from(context).inflate(R.layout.item_semua_latihan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelLatihanSoal latihanSoal = latihanSoalList.get(position);
        holder.bind(latihanSoal);
    }

    @Override
    public int getItemCount() {
        return latihanSoalList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvJudulLatihan;
        private final TextView tvJumlahSoal;
        private final TextView tvNamaMapel; // View baru untuk nama mapel
        private final Button btnKerjakan;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJudulLatihan = itemView.findViewById(R.id.tv_judul_latihan);
            tvJumlahSoal = itemView.findViewById(R.id.tv_jumlah_soal);
            tvNamaMapel = itemView.findViewById(R.id.tv_nama_mapel); // Hubungkan ke ID baru
            btnKerjakan = itemView.findViewById(R.id.btn_kerjakan);
        }

        void bind(ModelLatihanSoal latihanSoal) {
            tvJudulLatihan.setText(latihanSoal.getJudulLatihan());
            tvJumlahSoal.setText(String.format("%d Soal", latihanSoal.getJumlahSoal()));
            
            // Tampilkan nama mapel jika ada
            if (latihanSoal.getNamaMapel() != null && !latihanSoal.getNamaMapel().isEmpty()) {
                tvNamaMapel.setText(latihanSoal.getNamaMapel());
                tvNamaMapel.setVisibility(View.VISIBLE);
            } else {
                tvNamaMapel.setVisibility(View.GONE);
            }

            btnKerjakan.setOnClickListener(v -> {
                Intent intent = new Intent(context, DetailLatihanSoalActivity.class);
                intent.putExtra("id_soal", latihanSoal.getId());
                intent.putExtra("judul_soal", latihanSoal.getJudulLatihan());
                context.startActivity(intent);
            });
        }
    }
}
