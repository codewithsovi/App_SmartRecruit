package com.example.smartrecruit.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartrecruit.R;
import com.example.smartrecruit.adapter.JabatanAdapter;
import com.example.smartrecruit.model.Jabatan;
import com.example.smartrecruit.network.*;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JabatanFragment extends Fragment {

    RecyclerView rv;
    FloatingActionButton fab;
    List<Jabatan> list = new ArrayList<>();
    JabatanAdapter adapter;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_jabatan, container, false);

        rv = view.findViewById(R.id.rvJabatan);
        fab = view.findViewById(R.id.fabTambah);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setHasFixedSize(true);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        adapter = new JabatanAdapter(getContext(), list, new JabatanAdapter.OnDataChangedListener() {
            @Override
            public void onDataChanged() {
                // Optional: reload data dari server untuk sinkronisasi
                // loadData();
            }
        });
        rv.setAdapter(adapter);

        loadData();

        fab.setOnClickListener(v -> showTambahDialog());

        return view;
    }

    private void loadData() {
        progressDialog.show();

        ApiService api = ApiClient.getApiService();

        api.getJabatan().enqueue(new Callback<ApiResponse<PaginationResponse<Jabatan>>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<PaginationResponse<Jabatan>>> call,
                    Response<ApiResponse<PaginationResponse<Jabatan>>> response
            ) {
                progressDialog.dismiss();

                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show();
                    return;
                }

                ApiResponse<PaginationResponse<Jabatan>> body = response.body();

                if (body == null || body.data == null || body.data.data == null) {
                    Toast.makeText(getContext(), "Data tidak tersedia", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Update list dan notify adapter
                list.clear();
                list.addAll(body.data.data);
                adapter.notifyDataSetChanged();

                // Tampilkan pesan jika data kosong
                if (list.isEmpty()) {
                    Toast.makeText(getContext(), "Belum ada data jabatan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PaginationResponse<Jabatan>>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTambahDialog() {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_jabatan, null);

        EditText et = view.findViewById(R.id.etNamaJabatan);

        new AlertDialog.Builder(getContext())
                .setTitle("Tambah Jabatan")
                .setView(view)
                .setPositiveButton("Simpan", (d, w) -> {
                    String namaJabatan = et.getText().toString().trim();

                    if (namaJabatan.isEmpty()) {
                        Toast.makeText(getContext(), "Nama jabatan tidak boleh kosong", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    simpanJabatan(namaJabatan);
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void simpanJabatan(String namaJabatan) {
        progressDialog.show();

        Map<String, String> body = new HashMap<>();
        body.put("nama_jabatan", namaJabatan);

        ApiService api = ApiClient.getApiService();

        api.storeJabatan(body).enqueue(new Callback<ApiResponse<Jabatan>>() {
            @Override
            public void onResponse(Call<ApiResponse<Jabatan>> call, Response<ApiResponse<Jabatan>> response) {
                progressDialog.dismiss();

                android.util.Log.d("API_RESPONSE", "Response Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Jabatan> apiResponse = response.body();

                    android.util.Log.d("API_RESPONSE", "Success: " + apiResponse.success);
                    android.util.Log.d("API_RESPONSE", "Message: " + apiResponse.message);

                    if (apiResponse.success) {
                        Toast.makeText(getContext(), "Jabatan berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                        loadData();
                    } else {
                        Toast.makeText(getContext(), "Gagal: " + apiResponse.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        android.util.Log.e("API_ERROR", "Error Body: " + errorBody);
                        Toast.makeText(getContext(), "Error " + response.code() + ": " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Gagal menambahkan jabatan. Code: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Jabatan>> call, Throwable t) {
                progressDialog.dismiss();
                android.util.Log.e("API_FAILURE", "Error: " + t.getMessage());
                t.printStackTrace();
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}