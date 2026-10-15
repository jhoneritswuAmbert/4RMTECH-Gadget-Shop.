package com.fourrmtech.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.fourrmtech.app.api.ApiClient;
import com.fourrmtech.app.api.ApiService;
import com.fourrmtech.app.api.ForgotPasswordRequest;
import com.fourrmtech.app.api.ForgotPasswordResponse;
import com.fourrmtech.app.api.VerifyResetRequest;
import com.fourrmtech.app.api.FinishResetRequest;
import com.fourrmtech.app.databinding.ActivityForgotPasswordBinding;
import com.fourrmtech.app.utils.DataManager;
import com.google.gson.Gson;
import java.nio.charset.StandardCharsets;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {
    private ActivityForgotPasswordBinding binding;
    private ApiService api;
    private int step = 0;
    private String requestId = "";
    private String resetToken = "";
    private boolean busy = false;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        api = ApiClient.getClient().create(ApiService.class);
        binding.tvBackToLogin.setOnClickListener(v -> { if (!busy) finish(); });
        binding.btnSendResetCode.setOnClickListener(v -> submit());
        binding.btnResetStartOver.setOnClickListener(v -> {
            if (busy) return;
            step = 0; requestId = ""; resetToken = "";
            binding.etResetCode.setText("");
            binding.etNewPassword.setText("");
            binding.etConfirmPassword.setText("");
            showStep();
        });
        showStep();
    }
    private void showStep() {
        binding.tilResetEmail.setVisibility(step == 0 ? View.VISIBLE : View.GONE);
        binding.tilResetCode.setVisibility(step == 1 ? View.VISIBLE : View.GONE);
        binding.tilNewPassword.setVisibility(step == 2 ? View.VISIBLE : View.GONE);
        binding.tilConfirmPassword.setVisibility(step == 2 ? View.VISIBLE : View.GONE);
        binding.btnResetStartOver.setVisibility(step == 0 ? View.GONE : View.VISIBLE);
        binding.btnSendResetCode.setEnabled(!busy);
        binding.btnResetStartOver.setEnabled(!busy);
        binding.btnSendResetCode.setText(busy ? "Please wait..." : step == 0 ? "Send Reset Code" : step == 1 ? "Verify Code" : "Save New Password");
        binding.tvForgotPasswordTitle.setText(step == 0 ? "Forgot Password?" : step == 1 ? "Check Your Email" : "New Password");
        binding.tvForgotPasswordDescription.setText(step == 0 ? "Enter your customer account email. If it is registered, we will send a reset code." : step == 1 ? "Enter the six-digit code sent to your email. It expires 10 minutes after your request. Check your spam folder too." : "Enter and confirm your new password. Use at least 6 characters. You will sign in again afterward.");
    }
    private void submit() {
        if (busy) return;
        Call<ForgotPasswordResponse> call;
        if (step == 0) {
            String email = binding.etResetEmail.getText().toString().trim();
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.tilResetEmail.setError("Enter a valid email address."); return;
            }
            binding.tilResetEmail.setError(null);
            call = api.requestPasswordReset(new ForgotPasswordRequest(email));
        } else if (step == 1) {
            String code = binding.etResetCode.getText().toString().trim();
            if (!code.matches("[0-9]{6}")) { binding.tilResetCode.setError("Enter the six-digit code."); return; }
            binding.tilResetCode.setError(null);
            call = api.verifyPasswordReset(new VerifyResetRequest(requestId, code));
        } else {
            String password = binding.etNewPassword.getText().toString();
            String confirm = binding.etConfirmPassword.getText().toString();
            if (password.length() < 6 || password.getBytes(StandardCharsets.UTF_8).length > 72) {
                binding.tilNewPassword.setError("Use at least 6 characters and at most 72 bytes."); return;
            }
            binding.tilNewPassword.setError(null);
            if (!password.equals(confirm)) { binding.tilConfirmPassword.setError("Passwords do not match."); return; }
            binding.tilConfirmPassword.setError(null);
            call = api.finishPasswordReset(new FinishResetRequest(resetToken, password, confirm));
        }
        busy = true; showStep();
        call.enqueue(new Callback<ForgotPasswordResponse>() {
            @Override public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                if (isFinishing() || isDestroyed()) return;
                busy = false;
                ForgotPasswordResponse data = response.body();
                if (!response.isSuccessful() || data == null || !data.isSuccess()) {
                    String message = data != null ? data.getMessage() : "Could not complete password recovery. Try again.";
                    try {
                        if (response.errorBody() != null) {
                            ForgotPasswordResponse error = new Gson().fromJson(response.errorBody().string(), ForgotPasswordResponse.class);
                            if (error != null && error.getMessage() != null) message = error.getMessage();
                        }
                    } catch (Exception ignored) { }
                    Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_LONG).show();
                    showStep(); return;
                }
                if (step == 0 && data.getRequestId() != null) { requestId = data.getRequestId(); step = 1; }
                else if (step == 1 && data.getResetToken() != null) { resetToken = data.getResetToken(); step = 2; }
                else if (step == 2) {
                    resetToken = ""; requestId = "";
                    DataManager.getInstance().logout();
                    Toast.makeText(ForgotPasswordActivity.this, data.getMessage(), Toast.LENGTH_LONG).show();
                    finish(); return;
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Invalid server response. Update the reset endpoints.", Toast.LENGTH_LONG).show();
                    showStep(); return;
                }
                Toast.makeText(ForgotPasswordActivity.this, data.getMessage(), Toast.LENGTH_LONG).show();
                showStep();
            }
            @Override public void onFailure(Call<ForgotPasswordResponse> call, Throwable error) {
                if (isFinishing() || isDestroyed()) return;
                busy = false; showStep();
                Toast.makeText(ForgotPasswordActivity.this, "Unable to reach the server. Check your connection.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
