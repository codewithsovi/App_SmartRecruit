package com.example.smartrecruit.model;

import com.google.gson.annotations.SerializedName;

public class Kandidat {

    @SerializedName("id")
    public int id;

    @SerializedName("nama_kandidat")
    public String nama_kandidat;

    @SerializedName("jabatan_id")
    public int jabatan_id;

    @SerializedName("jabatan")
    public Jabatan jabatan;

    @SerializedName("tes_tulis")
    public Integer tes_tulis;

    @SerializedName("tes_wawancara")
    public Integer tes_wawancara;

    @SerializedName("tes_kesehatan")
    public Integer tes_kesehatan;

    @SerializedName("tes_keterampilan")
    public Integer tes_keterampilan;

    // Constructor
    public Kandidat() {
    }

    @Override
    public String toString() {
        return "Kandidat{" +
                "id=" + id +
                ", nama_kandidat='" + nama_kandidat + '\'' +
                ", jabatan_id=" + jabatan_id +
                '}';
    }
}