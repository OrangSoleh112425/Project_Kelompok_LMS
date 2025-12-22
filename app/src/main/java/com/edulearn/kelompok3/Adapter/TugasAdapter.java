package com.edulearn.kelompok3.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.edulearn.kelompok3.Activity.DetailTugasActivity;
import com.edulearn.kelompok3.Model.ModelTugas;
import com.edulearn.kelompok3.R;
import java.util.List;

public class TugasAdapter extends RecyclerView.Adapter<TugasAdapter.ViewHolder> {

    private Context context;
    private List<ModelTugas> tugasList;

    public TugasAdapter(Context context, List<ModelTugas> tugasList) {
        this.context = context;
        this.tugasList = tugasList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tugas, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelTugas tugas = tugasList.get(position);
        holder.tvJudulTugas.setText(tugas.getNamaTugas());
        holder.tvDeadline.setText("Tenggat: " + tugas.getDeadline());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailTugasActivity.class);
            intent.putExtra("id", tugas.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return tugasList.size();
    }

    public void updateData(List<ModelTugas> newTugasList) {
        this.tugasList = newTugasList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvJudulTugas, tvDeadline;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJudulTugas = itemView.findViewById(R.id.tv_judultugas);
            tvDeadline = itemView.findViewById(R.id.tv_deadline);
        }
    }
}
