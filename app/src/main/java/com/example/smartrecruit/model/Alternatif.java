package com.example.smartrecruit.model;

import com.google.gson.annotations.SerializedName;

public class Alternatif {
    @SerializedName("id")
    public int id;

    @SerializedName("kandidat_id")
    public int kandidatId;

    @SerializedName("kriteria_id")
    public int kriteriaId;

    @SerializedName("bobot")
    public double bobot;
}
