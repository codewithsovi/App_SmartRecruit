package com.example.smartrecruit.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.*;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.smartrecruit.R;
import com.example.smartrecruit.model.Jabatan;
import com.example.smartrecruit.network.ApiClient;
import com.example.smartrecruit.network.ApiResponse;
import com.example.smartrecruit.network.ApiService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JabatanAdapter extends RecyclerView.Adapter<JabatanAdapter.ViewHolder> {

    Context context;
    List<Jabatan> list;
    OnDataChangedListener listener;

    public interface OnDataChangedListener {
        void onDataChanged();
    }

    public JabatanAdapter(Context context, List<Jabatan> list, OnDataChangedListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_jabatan, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Jabatan jabatan = list.get(position);
        holder.tvNama.setText(jabatan.nama_jabatan);

        holder.btnEdit.setOnClickListener(v -> showEditDialog(jabatan, position));
    }

    private void showEditDialog(Jabatan jabatan, int position) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.dialog_jabatan, null);

        EditText etNama = view.findViewById(R.id.etNamaJabatan);
        etNama.setText(jabatan.nama_jabatan);

        new AlertDialog.Builder(context)
                .setTitle("Edit Jabatan")
                .setView(view)
                .setPositiveButton("Simpan", (d, w) -> {
                    String namaJabatan = etNama.getText().toString().trim();

                    // Validasi input
                    if (namaJabatan.isEmpty()) {
                        Toast.makeText(context, "Nama jabatan tidak boleh kosong", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Update jabatan
                    updateJabatan(jabatan.id, namaJabatan, position);
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void updateJabatan(int id, String namaJabatan, int position) {
        Map<String, String> body = new HashMap<>();
        body.put("nama_jabatan", namaJabatan);

        ApiService api = ApiClient.getApiService();

        api.updateJabatan(id, body).enqueue(new Callback<ApiResponse<Jabatan>>() {
            @Override
            public void onResponse(Call<ApiResponse<Jabatan>> call, Response<ApiResponse<Jabatan>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Jabatan> apiResponse = response.body();

                    if (apiResponse.success && apiResponse.data != null) {
                        // Update data di list
                        list.set(position, apiResponse.data);
                        notifyItemChanged(position);

                        Toast.makeText(context, "Jabatan berhasil diupdate", Toast.LENGTH_SHORT).show();

                        // Callback untuk refresh data jika diperlukan
                        if (listener != null) {
                            listener.onDataChanged();
                        }
                    } else {
                        Toast.makeText(context, "Gagal update: " + apiResponse.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Gagal update jabatan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Jabatan>> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama;
        ImageView btnEdit;

        ViewHolder(View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNamaJabatan);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}