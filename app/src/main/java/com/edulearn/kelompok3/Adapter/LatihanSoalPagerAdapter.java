package com.edulearn.kelompok3.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.edulearn.kelompok3.Model.ModelSoal;
import com.edulearn.kelompok3.R;

import java.util.Arrays;
import java.util.List;

public class LatihanSoalPagerAdapter extends RecyclerView.Adapter<LatihanSoalPagerAdapter.ViewHolder> {

    private List<ModelSoal> soalList;

    public LatihanSoalPagerAdapter(List<ModelSoal> soalList) {
        this.soalList = soalList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Menggunakan layout item_latihansoal.xml yang baru
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_latihansoal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelSoal soal = soalList.get(position);
        holder.bind(soal);
    }

    @Override
    public int getItemCount() {
        return soalList != null ? soalList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSoal, tvAnswer1, tvAnswer2, tvAnswer3, tvAnswer4;
        List<TextView> answerViews;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSoal = itemView.findViewById(R.id.tv_soal);
            tvAnswer1 = itemView.findViewById(R.id.tv_answer1);
            tvAnswer2 = itemView.findViewById(R.id.tv_answer2);
            tvAnswer3 = itemView.findViewById(R.id.tv_answer3);
            tvAnswer4 = itemView.findViewById(R.id.tv_answer4);
            answerViews = Arrays.asList(tvAnswer1, tvAnswer2, tvAnswer3, tvAnswer4);
        }

        public void bind(ModelSoal soal) {
            // Mengisi data soal dan pilihan
            tvSoal.setText(soal.getPertanyaan());
            tvAnswer1.setText(String.format("A. %s", soal.getPilihanA()));
            tvAnswer2.setText(String.format("B. %s", soal.getPilihanB()));
            tvAnswer3.setText(String.format("C. %s", soal.getPilihanC()));
            tvAnswer4.setText(String.format("D. %s", soal.getPilihanD()));

            // Atur listener untuk setiap pilihan
            tvAnswer1.setOnClickListener(v -> handleSelection(soal, "A"));
            tvAnswer2.setOnClickListener(v -> handleSelection(soal, "B"));
            tvAnswer3.setOnClickListener(v -> handleSelection(soal, "C"));
            tvAnswer4.setOnClickListener(v -> handleSelection(soal, "D"));

            // Perbarui tampilan berdasarkan jawaban yang sudah ada
            updateSelection(soal.getSelectedAnswer());
        }

        private void handleSelection(ModelSoal soal, String selectedOption) {
            soal.setSelectedAnswer(selectedOption);
            updateSelection(selectedOption);
        }

        private void updateSelection(String selectedOption) {
            for (int i = 0; i < answerViews.size(); i++) {
                String option = String.valueOf((char) ('A' + i));
                answerViews.get(i).setSelected(option.equals(selectedOption));
            }
        }
    }
}
