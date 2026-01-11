package com.example.smartrecruit.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartrecruit.R;
import com.example.smartrecruit.activity.KandidatDetailActivity;
import com.example.smartrecruit.adapter.KandidatJabatanAdapter;
import com.example.smartrecruit.model.JabatanWithCount;
import com.example.smartrecruit.network.*;

import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KandidatFragment extends Fragment {

    RecyclerView rv;
    List<JabatanWithCount> list = new ArrayList<>();
    KandidatJabatanAdapter adapter;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_kandidat, container, false);

        rv = view.findViewById(R.id.rvKandidat);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setHasFixedSize(true);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        adapter = new KandidatJabatanAdapter(getContext(), list, jabatanId -> {
            Intent intent = new Intent(getContext(), KandidatDetailActivity.class);
            intent.putExtra("jabatan_id", jabatanId);
            startActivity(intent);
        });
        rv.setAdapter(adapter);

        loadData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        progressDialog.show();

        ApiService api = ApiClient.getApiService();

        android.util.Log.d("API_REQUEST", "Loading jabatan with kandidat count...");

        api.getJabatanWithKandidat().enqueue(new Callback<ApiResponse<List<JabatanWithCount>>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<List<JabatanWithCount>>> call,
                    Response<ApiResponse<List<JabatanWithCount>>> response
            ) {
                progressDialog.dismiss();

                android.util.Log.d("API_RESPONSE", "Response Code: " + response.code());

                if (!response.isSuccessful()) {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        android.util.Log.e("API_ERROR", "Error Body: " + errorBody);
                        Toast.makeText(getContext(), "Error loading data", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error loading data", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }

                ApiResponse<List<JabatanWithCount>> body = response.body();

                if (body == null || body.data == null) {
                    Toast.makeText(getContext(), "Data tidak tersedia", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Update list dan notify adapter
                list.clear();
                list.addAll(body.data);
                adapter.notifyDataSetChanged();

                android.util.Log.d("API_SUCCESS", "Loaded " + list.size() + " jabatan");

                if (list.isEmpty()) {
                    Toast.makeText(getContext(), "Belum ada data kandidat", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<JabatanWithCount>>> call, Throwable t) {
                progressDialog.dismiss();
                android.util.Log.e("API_FAILURE", "Error: " + t.getMessage());
                t.printStackTrace();
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}