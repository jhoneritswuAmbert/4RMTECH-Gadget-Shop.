package com.fourrmtech.app.activities;

import android.content.Intent;
import android.os.Bundle;
import com.fourrmtech.app.api.AuthStore;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fourrmtech.app.api.ApiClient;
import com.fourrmtech.app.api.ApiService;
import com.fourrmtech.app.databinding.ActivityRegisterBinding;
import com.fourrmtech.app.models.Customer;
import com.fourrmtech.app.models.RegisterRequest;
import com.fourrmtech.app.models.RegisterResponse;
import com.fourrmtech.app.utils.DataManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnRegister.setOnClickListener(v -> handleRegister());

        binding.tvLoginLink.setOnClickListener(v -> finish());
    }

    private void handleRegister() {

        String firstName = getText(binding.etFirstName);
        String lastName = getText(binding.etLastName);
        String email = getText(binding.etEmail);
        String password = getText(binding.etPassword);
        String confirm = getText(binding.etConfirmPassword);

        // Validate fields
        if (TextUtils.isEmpty(firstName)) {
            binding.tilFirstName.setError("Required");
            return;
        }

        if (TextUtils.isEmpty(lastName)) {
            binding.tilLastName.setError("Required");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            binding.tilEmail.setError("Required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            binding.tilPassword.setError("Required");
            return;
        }

        if (!password.equals(confirm)) {
            binding.tilConfirmPassword.setError("Passwords do not match");
            return;
        }

        // Clear errors
        binding.tilFirstName.setError(null);
        binding.tilLastName.setError(null);
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);
        binding.tilConfirmPassword.setError(null);

        // Create registration request
        RegisterRequest request = new RegisterRequest(
                firstName,
                lastName,
                email,
                password
        );

        // Create API service
        ApiService apiService =
                ApiClient.getClient().create(ApiService.class);

        // Send registration request
        Call<RegisterResponse> call =
                apiService.register(request);

        call.enqueue(new Callback<RegisterResponse>() {

            @Override
            public void onResponse(
                    @NonNull Call<RegisterResponse> call,
                    @NonNull Response<RegisterResponse> response) {

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().isSuccess()) {

                    // Get registration response
                    RegisterResponse registerResponse =
                            response.body();

                    // Create the newly registered customer
                    Customer newCustomer = new Customer(
                            registerResponse.getCustomerId(),
                            registerResponse.getFirstName(),
                            registerResponse.getLastName(),
                            email,
                            "",
                            "",
                            ""
                    );

                    // Set the newly registered customer
                    // as the current user
                    AuthStore.save(registerResponse.getToken());
                    DataManager.getInstance().setCurrentUser(
                            newCustomer
                    );

                    Toast.makeText(
                            RegisterActivity.this,
                            "Registration successful!",
                            Toast.LENGTH_SHORT
                    ).show();

                    // Go to Customer Home
                    Intent intent = new Intent(
                            RegisterActivity.this,
                            CustomerHomeActivity.class
                    );

                    intent.setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    );

                    startActivity(intent);

                } else {

                    String message = "Registration failed.";

                    if (response.body() != null
                            && response.body().getMessage() != null) {

                        message = response.body().getMessage();
                    }

                    Toast.makeText(
                            RegisterActivity.this,
                            message,
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<RegisterResponse> call,
                    @NonNull Throwable t) {

                Toast.makeText(
                        RegisterActivity.this,
                        "Unable to connect to the server.",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private String getText(
            com.google.android.material.textfield.TextInputEditText et) {

        return et.getText() != null
                ? et.getText().toString().trim()
                : "";
    }
}