package com.example.smartrecruit.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartrecruit.R;
import com.example.smartrecruit.model.Hasil;
import com.example.smartrecruit.network.ApiClient;
import com.example.smartrecruit.network.ApiResponse;
import com.example.smartrecruit.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HasilDetailActivity extends AppCompatActivity {

    private RecyclerView rv;
    private ProgressBar pb;
    private TextView tvBack;
    private List<Hasil> list = new ArrayList<>();
    private HasilRankAdapter adapter;
    private int jabatanId;
    private String jabatanNama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasil_detail);

        jabatanId = getIntent().getIntExtra("jabatan_id", 0);
        jabatanNama = getIntent().getStringExtra("jabatan_nama");

        rv = findViewById(R.id.rvHasilDetail);
        pb = findViewById(R.id.pbHasilDetail);
        tvBack = findViewById(R.id.tvBackHasil);

        tvBack.setOnClickListener(v -> finish());

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HasilRankAdapter();
        rv.setAdapter(adapter);

        sinkronkanDanMuatData();
    }

    private void sinkronkanDanMuatData() {
        pb.setVisibility(View.VISIBLE);
        ApiService api = ApiClient.getApiService();

        api.hitungFuzzy(jabatanId).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                loadDataHasil();
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                loadDataHasil();
            }
        });
    }

    private void loadDataHasil() {
        ApiService api = ApiClient.getApiService();
        api.getHasilByJabatan(jabatanId).enqueue(new Callback<ApiResponse<List<Hasil>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Hasil>>> call, Response<ApiResponse<List<Hasil>>> response) {
                pb.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    list.clear();
                    if (response.body().data != null) {
                        list.addAll(response.body().data);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(HasilDetailActivity.this, "Gagal memuat hasil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Hasil>>> call, Throwable t) {
                pb.setVisibility(View.GONE);
                Toast.makeText(HasilDetailActivity.this, "Error Jaringan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class HasilRankAdapter extends RecyclerView.Adapter<HasilRankAdapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hasil_rank, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Hasil item = list.get(position);
            holder.tvRank.setText("#" + (position + 1));
            
            if (item.kandidat != null) {
                holder.tvNama.setText(item.kandidat.nama_kandidat);
            } else {
                // Perbaikan variabel: kandidatId sesuai model Hasil.java
                holder.tvNama.setText("Kandidat #" + item.kandidatId);
            }
            
            holder.tvJabatan.setText("Jabatan : " + (jabatanNama != null ? jabatanNama : "-"));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvRank, tvNama, tvJabatan;
            ViewHolder(View itemView) {
                super(itemView);
                tvRank = itemView.findViewById(R.id.tvRank);
                tvNama = itemView.findViewById(R.id.tvNamaKandidatHasil);
                tvJabatan = itemView.findViewById(R.id.tvJabatanKandidatHasil);
            }
        }
    }
}
