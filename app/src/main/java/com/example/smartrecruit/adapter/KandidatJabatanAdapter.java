package com.example.smartrecruit.adapter;

import android.content.Context;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.smartrecruit.R;
import com.example.smartrecruit.model.JabatanWithCount;

import java.util.List;

public class KandidatJabatanAdapter extends RecyclerView.Adapter<KandidatJabatanAdapter.ViewHolder> {

    Context context;
    List<JabatanWithCount> list;
    OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int jabatanId);
    }

    public KandidatJabatanAdapter(Context context, List<JabatanWithCount> list, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_kandidat_jabatan, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        JabatanWithCount jabatan = list.get(position);

        holder.tvNama.setText(jabatan.nama_jabatan);
        holder.tvJumlah.setText(jabatan.kandidat_count + " Kandidat");

        // Click listener untuk seluruh card
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(jabatan.id);
            }
        });

        // Click listener untuk button view
        holder.btnView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(jabatan.id);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvJumlah;
        ImageView btnView;

        ViewHolder(View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNamaJabatan);
            tvJumlah = itemView.findViewById(R.id.tvJumlahKandidat);
            btnView = itemView.findViewById(R.id.btnView);
        }
    }
}