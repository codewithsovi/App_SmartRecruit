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

import com.example.smartrecruit.activity.MainActivity;
import com.example.smartrecruit.R;
import com.example.smartrecruit.network.ApiClient;
import com.example.smartrecruit.model.User;
import com.example.smartrecruit.response.LoginResponse;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Apply window insets for edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(getWindow().getDecorView(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize session manager
        sessionManager = new SessionManager(this);

        // Check if already logged in
        if (sessionManager.isLoggedIn()) {
            navigateToMainActivity();
            return;
        }

        // Initialize views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);

        // Set click listener
        btnLogin.setOnClickListener(v -> performLogin());
    }

    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email tidak boleh kosong");
            etEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Format email tidak valid");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password tidak boleh kosong");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password minimal 6 karakter");
            etPassword.requestFocus();
            return;
        }

        // Show loading
        showLoading(true);

        // Create login request
        LoginRequest loginRequest = new LoginRequest(email, password);

        // Call API
        Call<LoginResponse> call = ApiClient.getApiService().login(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    if (loginResponse.isSuccess()) {
                        // Save session
                        sessionManager.createLoginSession(
                                loginResponse.getToken(),
                                loginResponse.getData().getId(),
                                loginResponse.getData().getName(),
                                loginResponse.getData().getEmail()
                        );

                        Toast.makeText(LoginActivity.this,
                                "Selamat datang, " + loginResponse.getData().getName() + "!",
                                Toast.LENGTH_SHORT).show();

                        // Navigate to main activity
                        navigateToMainActivity();
                    } else {
                        Toast.makeText(LoginActivity.this,
                                loginResponse.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle error response
                    String errorMessage = "Login gagal. Silakan coba lagi.";
                    if (response.code() == 401) {
                        errorMessage = "Email atau password salah";
                    } else if (response.code() == 422) {
                        errorMessage = "Data tidak valid. Periksa inputan Anda.";
                    } else if (response.code() >= 500) {
                        errorMessage = "Server error. Silakan coba lagi nanti.";
                    }
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                showLoading(false);
                String errorMessage = "Error koneksi: " + t.getMessage();

                // Handle specific errors
                if (t instanceof java.net.UnknownHostException) {
                    errorMessage = "Tidak ada koneksi internet. Periksa jaringan Anda.";
                } else if (t instanceof java.net.SocketTimeoutException) {
                    errorMessage = "Koneksi timeout. Silakan coba lagi.";
                }

                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoading(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
            btnLogin.setAlpha(0.5f);
        } else {
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
            btnLogin.setAlpha(1f);
        }
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}