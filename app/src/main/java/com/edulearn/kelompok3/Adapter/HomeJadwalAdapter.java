package com.edulearn.kelompok3.Adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.edulearn.kelompok3.Model.ModelJadwalDetail;
import com.edulearn.kelompok3.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeJadwalAdapter extends RecyclerView.Adapter<HomeJadwalAdapter.ViewHolder> {

    private List<ModelJadwalDetail> jadwalList;

    public HomeJadwalAdapter(List<ModelJadwalDetail> jadwalList) {
        this.jadwalList = jadwalList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_jadwal_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelJadwalDetail jadwal = jadwalList.get(position);
        holder.bind(jadwal);
    }

    @Override
    public int getItemCount() {
        return jadwalList != null ? jadwalList.size() : 0;
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.stopUpdates();
    }

    public void updateData(List<ModelJadwalDetail> newJadwalList) {
        this.jadwalList = newJadwalList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamaMapel, tvWaktuMapel, tvStatusJadwal;
        private final Handler handler = new Handler(Looper.getMainLooper());
        private Runnable updateTimeRunnable;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaMapel = itemView.findViewById(R.id.tv_nama_mapel_home);
            tvWaktuMapel = itemView.findViewById(R.id.tv_waktu_mapel_home);
            tvStatusJadwal = itemView.findViewById(R.id.tv_status_jadwal_home);
        }

        void bind(ModelJadwalDetail jadwal) {
            tvNamaMapel.setText(jadwal.getNamaMapel());
            tvWaktuMapel.setText(String.format("%s–%s", jadwal.getJamMulai(), jadwal.getJamSelesai()));

            // Hentikan pembaruan sebelumnya untuk menghindari tumpang tindih
            stopUpdates();

            // Buat Runnable baru untuk item ini
            updateTimeRunnable = new Runnable() {
                @Override
                public void run() {
                    updateStatus(jadwal);
                    // Jadwalkan pembaruan berikutnya dalam 60 detik
                    handler.postDelayed(this, 60000);
                }
            };

            // Jalankan pembaruan status segera
            handler.post(updateTimeRunnable);
        }

        private void updateStatus(ModelJadwalDetail jadwal) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                Date now = new Date();

                Date startTime = sdf.parse(jadwal.getJamMulai());
                Date endTime = sdf.parse(jadwal.getJamSelesai());

                // Format waktu saat ini ke dalam jam dan menit saja untuk perbandingan yang akurat
                Date currentTime = sdf.parse(sdf.format(now));

                Context context = itemView.getContext();

                if (currentTime.after(endTime)) {
                    tvStatusJadwal.setText("Selesai");
                    tvStatusJadwal.setTextColor(ContextCompat.getColor(context, R.color.dark_text));
                } else if (currentTime.equals(startTime) || (currentTime.after(startTime) && currentTime.before(endTime))) {
                    tvStatusJadwal.setText("Berlangsung");
                    tvStatusJadwal.setTextColor(ContextCompat.getColor(context, R.color.menu_button_green));
                } else {
                    tvStatusJadwal.setText("Belum dimulai");
                    tvStatusJadwal.setTextColor(ContextCompat.getColor(context, R.color.menu_button_yellow));
                }

            } catch (ParseException e) {
                tvStatusJadwal.setText("-");
                tvStatusJadwal.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.light_text));
            }
        }

        void stopUpdates() {
            handler.removeCallbacks(updateTimeRunnable);
        }
    }
}
