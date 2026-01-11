package com.example.smartrecruit.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartrecruit.R;
import com.example.smartrecruit.activity.LoginActivity;
import com.example.smartrecruit.model.DashboardResponse;
import com.example.smartrecruit.network.ApiClient;
import com.example.smartrecruit.network.ApiResponse;
import com.example.smartrecruit.network.ApiService;
import com.example.smartrecruit.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private TextView tvKriteria, tvJabatan, tvKandidat;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvKriteria = view.findViewById(R.id.tvJumlahKriteria);
        tvJabatan = view.findViewById(R.id.tvJumlahJabatan);
        tvKandidat = view.findViewById(R.id.tvJumlahKandidat);

        View btnMenu = view.findViewById(R.id.btnMenu);
        LinearLayout logoutPopup = view.findViewById(R.id.logoutPopup);
        TextView btnLogout = view.findViewById(R.id.btnLogout);
        View rootContent = view.findViewById(R.id.rootContent);

        loadDashboardData();

        // Toggle popup
        btnMenu.setOnClickListener(v -> {
            logoutPopup.setVisibility(
                    logoutPopup.getVisibility() == View.VISIBLE
                            ? View.GONE
                            : View.VISIBLE
            );
        });

        rootContent.setOnClickListener(v -> logoutPopup.setVisibility(View.GONE));

        btnLogout.setOnClickListener(v -> {
            new SessionManager(requireContext()).logout();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    private void loadDashboardData() {
        ApiService api = ApiClient.getApiService();
        api.getDashboardStats().enqueue(new Callback<ApiResponse<DashboardResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<DashboardResponse>> call, Response<ApiResponse<DashboardResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    DashboardResponse data = response.body().data;
                    tvKriteria.setText(String.valueOf(data.totalKriteria));
                    tvJabatan.setText(String.valueOf(data.totalJabatan));
                    tvKandidat.setText(String.valueOf(data.totalKandidat));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<DashboardResponse>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDashboardData();
    }
}
