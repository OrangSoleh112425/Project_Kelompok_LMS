package com.edulearn.kelompok3.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.edulearn.kelompok3.Model.ModelLatihanSoal;
import com.edulearn.kelompok3.R;

import java.util.List;

public class LatihanSoalAdapter extends RecyclerView.Adapter<LatihanSoalAdapter.ViewHolder> {

    private Context context;
    private List<ModelLatihanSoal> latihanSoalList;
    private OnItemClickListener listener;

    // Interface untuk menangani klik, yang akan diimplementasikan oleh LatsolFragment
    public interface OnItemClickListener {
        void onItemClick(ModelLatihanSoal latihanSoal);
    }

    public LatihanSoalAdapter(Context context, List<ModelLatihanSoal> latihanSoalList, OnItemClickListener listener) {
        this.context = context;
        this.latihanSoalList = latihanSoalList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Menggunakan layout item_latihan_soal.xml yang sudah benar
        View view = LayoutInflater.from(context).inflate(R.layout.item_latihan_soal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelLatihanSoal latihanSoal = latihanSoalList.get(position);
        // Mengikat data dan listener untuk item ini
        holder.bind(latihanSoal, listener);
    }

    @Override
    public int getItemCount() {
        return latihanSoalList != null ? latihanSoalList.size() : 0;
    }

    public void setLatihanSoalList(List<ModelLatihanSoal> latihanSoalList) {
        this.latihanSoalList = latihanSoalList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvJudulLatihan, tvJumlahSoal;
        Button btnKerjakan; // Referensi ke tombol "Kerjakan"

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // (PERBAIKAN SINKRONISASI 1) - Menghubungkan ke ID yang BENAR di item_latihan_soal.xml
            tvJudulLatihan = itemView.findViewById(R.id.tv_judul_latihan);
            tvJumlahSoal = itemView.findViewById(R.id.tv_jumlah_soal);
            btnKerjakan = itemView.findViewById(R.id.btn_kerjakan); // Menghubungkan ke tombol
        }

        // Metode bind untuk mengatur data dan listener
        public void bind(final ModelLatihanSoal latihanSoal, final OnItemClickListener listener) {
            tvJudulLatihan.setText(latihanSoal.getJudulLatihan());
            tvJumlahSoal.setText(String.format("%d Soal", latihanSoal.getJumlahSoal()));

            // (PERBAIKAN SINKRONISASI 2) - Listener sekarang dipasang HANYA pada tombol "btn_kerjakan"
            btnKerjakan.setOnClickListener(v -> {
                if (listener != null) {
                    // Memanggil metode interface yang diimplementasikan oleh LatsolFragment
                    listener.onItemClick(latihanSoal);
                }
            });
        }
    }
}
