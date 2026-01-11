package com.example.smartrecruit.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartrecruit.R;
import com.example.smartrecruit.activity.HasilDetailActivity;
import com.example.smartrecruit.model.JabatanWithCount;
import com.example.smartrecruit.network.ApiClient;
import com.example.smartrecruit.network.ApiResponse;
import com.example.smartrecruit.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HasilFragment extends Fragment {

    private RecyclerView rv;
    private ProgressBar pb;
    private List<JabatanWithCount> list = new ArrayList<>();
    private HasilJabatanAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hasil, container, false);

        rv = v.findViewById(R.id.rvHasilJabatan);
        pb = v.findViewById(R.id.pbHasil);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HasilJabatanAdapter();
        rv.setAdapter(adapter);

        loadData();

        return v;
    }

    private void loadData() {
        pb.setVisibility(View.VISIBLE);
        ApiService api = ApiClient.getApiService();
        api.getJabatanHasil().enqueue(new Callback<ApiResponse<List<JabatanWithCount>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<JabatanWithCount>>> call, Response<ApiResponse<List<JabatanWithCount>>> response) {
                pb.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    list.clear();
                    list.addAll(response.body().data);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<JabatanWithCount>>> call, Throwable t) {
                pb.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class HasilJabatanAdapter extends RecyclerView.Adapter<HasilJabatanAdapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hasil_jabatan, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            JabatanWithCount item = list.get(position);
            holder.tvNama.setText(item.nama_jabatan);
            holder.tvJumlah.setText(item.kandidat_count + " Kandidat");

            holder.btnView.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), HasilDetailActivity.class);
                intent.putExtra("jabatan_id", item.id);
                intent.putExtra("jabatan_nama", item.nama_jabatan);
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            android.widget.TextView tvNama, tvJumlah;
            View btnView;
            ViewHolder(View itemView) {
                super(itemView);
                tvNama = itemView.findViewById(R.id.tvNamaJabatanHasil);
                tvJumlah = itemView.findViewById(R.id.tvJumlahKandidatHasil);
                btnView = itemView.findViewById(R.id.btnViewHasil);
            }
        }
    }
}
