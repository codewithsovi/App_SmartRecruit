package com.example.smartrecruit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartrecruit.R;
import com.example.smartrecruit.model.User;
import com.example.smartrecruit.network.ApiClient;
import com.example.smartrecruit.network.ApiService;
import com.example.smartrecruit.network.ApiResponse;
import com.example.smartrecruit.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;

    private SessionManager sessionManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(getWindow().getDecorView(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            navigateToMainActivity();
            return;
        }

        apiService = ApiClient.getApiService();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(v -> performLogin());
    }

    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email tidak boleh kosong");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password tidak boleh kosong");
            return;
        }

        showLoading(true);

        apiService.login(email, password)
                .enqueue(new Callback<ApiResponse<User>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<User>> call,
                                           Response<ApiResponse<User>> response) {

                        showLoading(false);

                        if (response.isSuccessful() && response.body() != null) {

                            ApiResponse<User> apiResponse = response.body();

                            if (apiResponse.success && apiResponse.data != null) {

                                User user = apiResponse.data;

                                sessionManager.createLoginSession(
                                        user.token,
                                        user.user_id,
                                        user.name,
                                        user.email
                                );

                                Toast.makeText(
                                        LoginActivity.this,
                                        "Selamat datang, " + user.name,
                                        Toast.LENGTH_SHORT
                                ).show();

                                navigateToMainActivity();

                            } else {
                                Toast.makeText(
                                        LoginActivity.this,
                                        apiResponse.message,
                                        Toast.LENGTH_SHORT
                                ).show();
                            }

                        } else if (response.code() == 401) {
                            Toast.makeText(
                                    LoginActivity.this,
                                    "Email atau password salah",
                                    Toast.LENGTH_SHORT
                            ).show();
                        } else {
                            Toast.makeText(
                                    LoginActivity.this,
                                    "Login gagal",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                        showLoading(false);
                        Toast.makeText(
                                LoginActivity.this,
                                "Koneksi gagal: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!show);
        btnLogin.setAlpha(show ? 0.5f : 1f);
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
