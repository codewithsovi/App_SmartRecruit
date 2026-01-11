package com.example.smartrecruit.network;

import com.example.smartrecruit.model.User;
import com.example.smartrecruit.model.Jabatan;
import com.example.smartrecruit.model.JabatanWithCount;
import com.example.smartrecruit.model.Kandidat;
import com.example.smartrecruit.model.Hasil;
import com.example.smartrecruit.model.Alternatif;
import com.example.smartrecruit.model.DashboardResponse;

import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    @FormUrlEncoded
    @POST("login")
    Call<ApiResponse<User>> login(
            @Field("email") String email,
            @Field("password") String password
    );

    @POST("logout")
    Call<ApiResponse<Object>> logout(
            @Header("Authorization") String token
    );

    // Dashboard
    @GET("dashboard/stats")
    Call<ApiResponse<DashboardResponse>> getDashboardStats();

    // Jabatan
    @GET("jabatan")
    Call<ApiResponse<PaginationResponse<Jabatan>>> getJabatan();

    @POST("jabatan")
    @Headers("Content-Type: application/json")
    Call<ApiResponse<Jabatan>> storeJabatan(
            @Body Map<String, String> body
    );

    @PUT("jabatan/{id}")
    @Headers("Content-Type: application/json")
    Call<ApiResponse<Jabatan>> updateJabatan(
            @Path("id") int id,
            @Body Map<String, String> body
    );

    // Kandidat
    @GET("kandidat/jabatan")
    Call<ApiResponse<List<JabatanWithCount>>> getJabatanWithKandidat();

    @GET("kandidat/{jabatan_id}")
    Call<ApiResponse<PaginationResponse<Kandidat>>> getKandidatByJabatan(
            @Path("jabatan_id") int jabatanId
    );

    @POST("kandidat/store")
    @Headers("Content-Type: application/json")
    Call<ApiResponse<Kandidat>> storeKandidat(
            @Body Map<String, Object> body
    );

    @PUT("kandidat/update/{id}")
    @Headers("Content-Type: application/json")
    Call<ApiResponse<Kandidat>> updateKandidat(
            @Path("id") int id,
            @Body Map<String, Object> body
    );

    @DELETE("kandidat/delete/{id}")
    Call<ApiResponse<Object>> deleteKandidat(
            @Path("id") int id
    );

    // Hasil
    @GET("hasil/jabatan")
    Call<ApiResponse<List<JabatanWithCount>>> getJabatanHasil();

    @GET("hasil/jabatan/{jabatan_id}")
    Call<ApiResponse<List<Hasil>>> getHasilByJabatan(
            @Path("jabatan_id") int jabatanId
    );

    // Perhitungan
    @GET("perhitungan/hitung/{jabatan_id}")
    Call<ApiResponse<Object>> hitungFuzzy(
            @Path("jabatan_id") int jabatanId
    );

    // Alternatif
    @GET("alternatif/jabatan/{jabatan_id}")
    Call<ApiResponse<List<Alternatif>>> getAlternatifByJabatan(
            @Path("jabatan_id") int jabatanId
    );

    @POST("alternatif/store")
    @Headers("Content-Type: application/json")
    Call<ApiResponse<Object>> storeAlternatif(
            @Body Map<String, Object> body
    );

    @PUT("alternatif/update/{id}")
    @Headers("Content-Type: application/json")
    Call<ApiResponse<Object>> updateAlternatif(
            @Path("id") int id,
            @Body Map<String, Object> body
    );
}
