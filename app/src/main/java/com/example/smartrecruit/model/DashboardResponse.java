package com.example.smartrecruit.model;

import com.google.gson.annotations.SerializedName;

public class DashboardResponse {
    @SerializedName("total_kriteria")
    public int totalKriteria;

    @SerializedName("total_jabatan")
    public int totalJabatan;

    @SerializedName("total_kandidat")
    public int totalKandidat;
}
