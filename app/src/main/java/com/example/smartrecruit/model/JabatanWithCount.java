package com.example.smartrecruit.model;

import com.google.gson.annotations.SerializedName;

public class JabatanWithCount {

    @SerializedName("id")
    public int id;

    @SerializedName("nama_jabatan")
    public String nama_jabatan;

    @SerializedName("kandidat_count")
    public int kandidat_count;

    public JabatanWithCount() {
    }

    @Override
    public String toString() {
        return "JabatanWithCount{" +
                "id=" + id +
                ", nama_jabatan='" + nama_jabatan + '\'' +
                ", kandidat_count=" + kandidat_count +
                '}';
    }
}