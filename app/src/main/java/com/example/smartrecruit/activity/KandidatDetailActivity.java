package com.example.smartrecruit.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartrecruit.R;
import com.example.smartrecruit.adapter.KandidatDetailAdapter;
import com.example.smartrecruit.model.Kandidat;
import com.example.smartrecruit.network.*;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KandidatDetailActivity extends AppCompatActivity {

    RecyclerView rv;
    FloatingActionButton fab;
    TextView tvHeader, tvBack;
    List<Kandidat> list = new ArrayList<>();
    KandidatDetailAdapter adapter;
    ProgressDialog progressDialog;
    int jabatanId;
    String jabatanName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kandidat_detail);

        jabatanId = getIntent().getIntExtra("jabatan_id", 0);
        jabatanName = getIntent().getStringExtra("jabatan_nama");

        rv = findViewById(R.id.rvKandidatDetail);
        fab = findViewById(R.id.fabTambah);
        tvHeader = findViewById(R.id.tvHeader);
        tvBack = findViewById(R.id.tvBack);

        if (jabatanName != null && !jabatanName.isEmpty()) {
            tvHeader.setText("Data Kandidat - " + jabatanName);
        }

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Menyimpan data...");
        progressDialog.setCancelable(false);

        adapter = new KandidatDetailAdapter(this, list, new KandidatDetailAdapter.OnKandidatActionListener() {
            @Override
            public void onEdit(Kandidat kandidat) {
                showEditDialog(kandidat);
            }

            @Override
            public void onDelete(Kandidat kandidat) {
                showDeleteConfirmation(kandidat);
            }
        });
        rv.setAdapter(adapter);

        loadData();

        fab.setOnClickListener(v -> showTambahDialog());
        tvBack.setOnClickListener(v -> finish());
    }

    private void loadData() {
        ApiClient.getApiService().getKandidatByJabatan(jabatanId).enqueue(new Callback<ApiResponse<PaginationResponse<Kandidat>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PaginationResponse<Kandidat>>> call, Response<ApiResponse<PaginationResponse<Kandidat>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    list.clear();
                    List<Kandidat> dataFromServer = response.body().data.data;
                    if (dataFromServer != null) {
                        for (Kandidat k : dataFromServer) {
                            loadLocalScores(k);
                            list.add(k);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<PaginationResponse<Kandidat>>> call, Throwable t) {}
        });
    }

    private void showTambahDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_kandidat, null);
        EditText etNama = view.findViewById(R.id.etNamaKandidat);
        EditText etTulis = view.findViewById(R.id.etTesTulis);
        EditText etWawancara = view.findViewById(R.id.etTesWawancara);
        EditText etKesehatan = view.findViewById(R.id.etTesKesehatan);
        EditText etKeterampilan = view.findViewById(R.id.etTesKeterampilan);

        new AlertDialog.Builder(this)
                .setTitle("Tambah Kandidat")
                .setView(view)
                .setPositiveButton("Simpan", (d, w) -> {
                    String nama = etNama.getText().toString().trim();
                    if (nama.isEmpty()) return;

                    int t1 = parseIntOrZero(etTulis.getText().toString());
                    int t2 = parseIntOrZero(etWawancara.getText().toString());
                    int t3 = parseIntOrZero(etKesehatan.getText().toString());
                    int t4 = parseIntOrZero(etKeterampilan.getText().toString());

                    Map<String, Object> body = new HashMap<>();
                    body.put("nama_kandidat", nama);
                    body.put("jabatan_id", jabatanId);
                    
                    simpanKandidat(body, t1, t2, t3, t4);
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void showEditDialog(Kandidat kandidat) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_kandidat, null);
        EditText etNama = view.findViewById(R.id.etNamaKandidat);
        EditText etTulis = view.findViewById(R.id.etTesTulis);
        EditText etWawancara = view.findViewById(R.id.etTesWawancara);
        EditText etKesehatan = view.findViewById(R.id.etTesKesehatan);
        EditText etKeterampilan = view.findViewById(R.id.etTesKeterampilan);

        etNama.setText(kandidat.nama_kandidat);
        etTulis.setText(String.valueOf(kandidat.tes_tulis != null ? kandidat.tes_tulis : 0));
        etWawancara.setText(String.valueOf(kandidat.tes_wawancara != null ? kandidat.tes_wawancara : 0));
        etKesehatan.setText(String.valueOf(kandidat.tes_kesehatan != null ? kandidat.tes_kesehatan : 0));
        etKeterampilan.setText(String.valueOf(kandidat.tes_keterampilan != null ? kandidat.tes_keterampilan : 0));

        new AlertDialog.Builder(this)
                .setTitle("Edit Kandidat")
                .setView(view)
                .setPositiveButton("Simpan", (d, w) -> {
                    String nama = etNama.getText().toString().trim();
                    if (nama.isEmpty()) return;

                    int t1 = parseIntOrZero(etTulis.getText().toString());
                    int t2 = parseIntOrZero(etWawancara.getText().toString());
                    int t3 = parseIntOrZero(etKesehatan.getText().toString());
                    int t4 = parseIntOrZero(etKeterampilan.getText().toString());

                    Map<String, Object> body = new HashMap<>();
                    body.put("nama_kandidat", nama);
                    body.put("jabatan_id", jabatanId);

                    updateKandidat(kandidat.id, body, t1, t2, t3, t4);
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void simpanKandidat(Map<String, Object> body, int t1, int t2, int t3, int t4) {
        progressDialog.show();
        ApiClient.getApiService().storeKandidat(body).enqueue(new Callback<ApiResponse<Kandidat>>() {
            @Override
            public void onResponse(Call<ApiResponse<Kandidat>> call, Response<ApiResponse<Kandidat>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int id = response.body().data.id;
                    saveLocalScores(id, t1, t2, t3, t4);
                    kirimKeAlternatif(id, t1, t2, t3, t4);
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(KandidatDetailActivity.this, "Gagal simpan kandidat", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Kandidat>> call, Throwable t) { progressDialog.dismiss(); }
        });
    }

    private void updateKandidat(int id, Map<String, Object> body, int t1, int t2, int t3, int t4) {
        progressDialog.show();
        ApiClient.getApiService().updateKandidat(id, body).enqueue(new Callback<ApiResponse<Kandidat>>() {
            @Override
            public void onResponse(Call<ApiResponse<Kandidat>> call, Response<ApiResponse<Kandidat>> response) {
                if (response.isSuccessful()) {
                    saveLocalScores(id, t1, t2, t3, t4);
                    updateAlternatif(id, t1, t2, t3, t4);
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(KandidatDetailActivity.this, "Gagal update kandidat", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Kandidat>> call, Throwable t) { progressDialog.dismiss(); }
        });
    }

    private void kirimKeAlternatif(int kandidatId, int t1, int t2, int t3, int t4) {
        Map<String, Object> body = new HashMap<>();
        body.put("kandidat_id", kandidatId);
        body.put("kriteria_id", Arrays.asList(1, 2, 3, 4)); 
        body.put("bobot", Arrays.asList(t1, t2, t3, t4));

        ApiClient.getApiService().storeAlternatif(body).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(KandidatDetailActivity.this, "Data & Bobot Berhasil Disimpan", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(KandidatDetailActivity.this, "Kandidat OK, tapi Bobot GAGAL: " + response.code(), Toast.LENGTH_LONG).show();
                }
                loadData();
            }
            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                progressDialog.dismiss();
                loadData();
            }
        });
    }

    private void updateAlternatif(int kandidatId, int t1, int t2, int t3, int t4) {
        Map<String, Object> body = new HashMap<>();
        body.put("kandidat_id", kandidatId);
        body.put("kriteria_id", Arrays.asList(1, 2, 3, 4));
        body.put("bobot", Arrays.asList(t1, t2, t3, t4));

        ApiClient.getApiService().updateAlternatif(kandidatId, body).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(KandidatDetailActivity.this, "Data & Bobot Berhasil Diupdate", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(KandidatDetailActivity.this, "Update Bobot GAGAL: " + response.code(), Toast.LENGTH_LONG).show();
                }
                loadData();
            }
            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                progressDialog.dismiss();
                loadData();
            }
        });
    }

    private void deleteKandidat(int id) {
        progressDialog.show();
        ApiClient.getApiService().deleteKandidat(id).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    removeLocalScores(id);
                    loadData();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) { progressDialog.dismiss(); }
        });
    }

    private void showDeleteConfirmation(Kandidat kandidat) {
        new AlertDialog.Builder(this)
                .setTitle("Hapus")
                .setMessage("Hapus " + kandidat.nama_kandidat + "?")
                .setPositiveButton("Hapus", (d, w) -> deleteKandidat(kandidat.id))
                .setNegativeButton("Batal", null)
                .show();
    }

    private void saveLocalScores(int id, int t1, int t2, int t3, int t4) {
        SharedPreferences pref = getSharedPreferences("KandidatScores", MODE_PRIVATE);
        pref.edit().putInt("t1_" + id, t1).putInt("t2_" + id, t2).putInt("t3_" + id, t3).putInt("t4_" + id, t4).apply();
    }

    private void loadLocalScores(Kandidat k) {
        SharedPreferences pref = getSharedPreferences("KandidatScores", MODE_PRIVATE);
        k.tes_tulis = pref.getInt("t1_" + k.id, 0);
        k.tes_wawancara = pref.getInt("t2_" + k.id, 0);
        k.tes_kesehatan = pref.getInt("t3_" + k.id, 0);
        k.tes_keterampilan = pref.getInt("t4_" + k.id, 0);
    }

    private void removeLocalScores(int id) {
        getSharedPreferences("KandidatScores", MODE_PRIVATE).edit().remove("t1_" + id).remove("t2_" + id).remove("t3_" + id).remove("t4_" + id).apply();
    }

    private int parseIntOrZero(String str) {
        try { return str.isEmpty() ? 0 : Integer.parseInt(str); } catch (Exception e) { return 0; }
    }
}
