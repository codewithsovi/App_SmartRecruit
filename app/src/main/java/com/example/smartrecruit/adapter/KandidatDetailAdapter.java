package com.example.smartrecruit.adapter;

import android.content.Context;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.smartrecruit.R;
import com.example.smartrecruit.model.Kandidat;

import java.util.List;

public class KandidatDetailAdapter extends RecyclerView.Adapter<KandidatDetailAdapter.ViewHolder> {

    Context context;
    List<Kandidat> list;
    OnKandidatActionListener listener;

    public interface OnKandidatActionListener {
        void onEdit(Kandidat kandidat);
        void onDelete(Kandidat kandidat);
    }

    public KandidatDetailAdapter(Context context, List<Kandidat> list, OnKandidatActionListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_kandidat_detail, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Kandidat kandidat = list.get(position);

        holder.tvNama.setText(kandidat.nama_kandidat);

        if (kandidat.jabatan != null) {
            holder.tvJabatan.setText("Jabatan : " + kandidat.jabatan.nama_jabatan);
        } else {
            holder.tvJabatan.setText("Jabatan : -");
        }

        holder.tvTulis.setText(kandidat.tes_tulis != null ? String.valueOf(kandidat.tes_tulis) : "0");
        holder.tvWawancara.setText(kandidat.tes_wawancara != null ? String.valueOf(kandidat.tes_wawancara) : "0");
        holder.tvKesehatan.setText(kandidat.tes_kesehatan != null ? String.valueOf(kandidat.tes_kesehatan) : "0");
        holder.tvKeterampilan.setText(kandidat.tes_keterampilan != null ? String.valueOf(kandidat.tes_keterampilan) : "0");

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(kandidat);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(kandidat);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvJabatan;
        TextView tvTulis, tvWawancara, tvKesehatan, tvKeterampilan;
        ImageView btnEdit, btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNamaKandidat);
            tvJabatan = itemView.findViewById(R.id.tvJabatanKandidat);
            tvTulis = itemView.findViewById(R.id.tvTesTulis);
            tvWawancara = itemView.findViewById(R.id.tvTesWawancara);
            tvKesehatan = itemView.findViewById(R.id.tvTesKesehatan);
            tvKeterampilan = itemView.findViewById(R.id.tvTesKeterampilan);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
