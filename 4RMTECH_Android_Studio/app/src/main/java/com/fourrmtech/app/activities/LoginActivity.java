package com.fourrmtech.app.activities;

import android.content.Intent;
import android.os.Bundle;
import com.fourrmtech.app.api.AuthStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.fourrmtech.app.api.ApiClient;
import com.fourrmtech.app.api.ApiService;
import com.fourrmtech.app.databinding.ActivityLoginBinding;
import com.fourrmtech.app.models.Customer;
import com.fourrmtech.app.models.LoginRequest;
import com.fourrmtech.app.models.LoginResponse;
import com.fourrmtech.app.models.AdminLoginRequest;
import com.fourrmtech.app.models.AdminLoginResponse;
import com.fourrmtech.app.utils.DataManager;
import com.fourrmtech.app.models.CustomerResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private boolean isAdminMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize DataManager and load saved customer
        DataManager.getInstance()
                .initialize(getApplicationContext());

        setupTabs();
        setupClickListeners();

        // Check if a customer is already logged in
        checkSavedLogin();
    }

    private void checkSavedLogin() {

        // Check if a customer is already loaded
        Customer savedUser =
                DataManager.getInstance().getCurrentUser();

        if (savedUser != null) {

            openCustomerHome();
            return;
        }

        // Get the saved customer ID
        int savedCustomerId =
                DataManager.getInstance().getSavedCustomerId();

        // No saved customer
        if (savedCustomerId == -1) {
            return;
        }

        // Connect to the API
        ApiService apiService =
                ApiClient.getClient().create(ApiService.class);

        Call<CustomerResponse> call =
                apiService.getCustomerById(savedCustomerId);

        call.enqueue(new Callback<CustomerResponse>() {

            @Override
            public void onResponse(
                    @NonNull Call<CustomerResponse> call,
                    @NonNull Response<CustomerResponse> response) {

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().isSuccess()) {

                    CustomerResponse customerResponse =
                            response.body();

                    Customer customer = new Customer(
                            customerResponse.getCustomerId(),
                            customerResponse.getFirstName(),
                            customerResponse.getLastName(),
                            customerResponse.getEmail(),
                            customerResponse.getContactNumber(),
                            customerResponse.getAddress(),
                            customerResponse.getJoinedDate()
                    );

// Store separate address fields
                    customer.setStreet(
                            customerResponse.getStreet()
                    );

                    customer.setBarangay(
                            customerResponse.getBarangay()
                    );

                    customer.setCity(
                            customerResponse.getCity()
                    );

                    customer.setPostalCode(
                            customerResponse.getPostalCode()
                    );
                    // Save the customer in DataManager
                    DataManager.getInstance()
                            .setCurrentUserFromApi(customer);

                    // Open customer home
                    openCustomerHome();

                } else {

                    // Saved customer no longer exists
                    DataManager.getInstance().logout();
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<CustomerResponse> call,
                    @NonNull Throwable t) {

                // Stay on the login screen if the API
                // cannot be reached.
            }
        });
    }

    private void openCustomerHome() {

        Intent intent =
                new Intent(
                        LoginActivity.this,
                        CustomerHomeActivity.class
                );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }

    private void setupTabs() {

        binding.tabLayout.addTab(
                binding.tabLayout.newTab().setText("Customer")
        );

        binding.tabLayout.addTab(
                binding.tabLayout.newTab().setText("Admin")
        );

        binding.tabLayout.addOnTabSelectedListener(
                new TabLayout.OnTabSelectedListener() {

                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {

                        isAdminMode = tab.getPosition() == 1;

                        updateUI();
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                    }
                }
        );
    }

    private void updateUI() {

        if (isAdminMode) {

            binding.btnLogin.setText("Login as Admin");

            binding.tvRegisterLink.setVisibility(View.GONE);

        } else {

            binding.btnLogin.setText("Login");

            binding.tvRegisterLink.setVisibility(View.VISIBLE);
        }
    }

    private void setupClickListeners() {

        binding.btnLogin.setOnClickListener(v -> handleLogin());

        binding.tvCreateAccount.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                this,
                                RegisterActivity.class
                        )
                )
        );

        binding.tvForgotPassword.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                this,
                                ForgotPasswordActivity.class
                        )
                )
        );
    }

    private void handleLogin() {

        String email =
                binding.etEmail.getText() != null
                        ? binding.etEmail.getText().toString().trim()
                        : "";

        String password =
                binding.etPassword.getText() != null
                        ? binding.etPassword.getText().toString().trim()
                        : "";

        // Validate email
        if (TextUtils.isEmpty(email)) {

            binding.tilEmail.setError("Email is required");

            return;
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {

            binding.tilPassword.setError("Password is required");

            return;
        }

        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);

        // Admin login
        if (isAdminMode) {

            AdminLoginRequest request =
                    new AdminLoginRequest(
                            email,
                            password
                    );

            ApiService apiService =
                    ApiClient.getClient().create(ApiService.class);

            Call<AdminLoginResponse> call =
                    apiService.adminLogin(request);

            call.enqueue(
                    new Callback<AdminLoginResponse>() {

                        @Override
                        public void onResponse(
                                @NonNull Call<AdminLoginResponse> call,
                                @NonNull Response<AdminLoginResponse> response) {

                            if (response.isSuccessful()
                                    && response.body() != null
                                    && response.body().isSuccess()) {

                                DataManager.getInstance().logout();
                                AuthStore.save(response.body().getToken());
                                Toast.makeText(
                                        LoginActivity.this,
                                        "Admin login successful!",
                                        Toast.LENGTH_SHORT
                                ).show();

                                Intent intent =
                                        new Intent(
                                                LoginActivity.this,
                                                AdminDashboardActivity.class
                                        );

                                intent.setFlags(
                                        Intent.FLAG_ACTIVITY_NEW_TASK
                                                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                );

                                startActivity(intent);

                                finish();

                            } else {

                                String message =
                                        "Invalid username or password.";

                                if (response.body() != null
                                        && response.body().getMessage() != null) {

                                    message =
                                            response.body().getMessage();
                                }

                                Toast.makeText(
                                        LoginActivity.this,
                                        message,
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }

                        @Override
                        public void onFailure(
                                @NonNull Call<AdminLoginResponse> call,
                                @NonNull Throwable t) {

                            Toast.makeText(
                                    LoginActivity.this,
                                    "Unable to connect to the server.",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
            );

            return;
        }
        // Customer login
        LoginRequest request =
                new LoginRequest(
                        email,
                        password
                );

        ApiService apiService =
                ApiClient.getClient().create(ApiService.class);

        Call<LoginResponse> call =
                apiService.login(request);

        call.enqueue(
                new Callback<LoginResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<LoginResponse> call,
                            @NonNull Response<LoginResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            AuthStore.save(response.body().getToken());
                            LoginResponse loginResponse =
                                    response.body();

                            Customer customer =
                                    new Customer(
                                            loginResponse.getCustomerId(),
                                            loginResponse.getFirstName(),
                                            loginResponse.getLastName(),
                                            loginResponse.getEmail(),
                                            loginResponse.getContactNumber(),
                                            loginResponse.getAddress(),
                                            loginResponse.getJoinedDate()
                                    );

// Store separate address fields
                            customer.setStreet(
                                    loginResponse.getStreet()
                            );

                            customer.setBarangay(
                                    loginResponse.getBarangay()
                            );

                            customer.setCity(
                                    loginResponse.getCity()
                            );

                            customer.setPostalCode(
                                    loginResponse.getPostalCode()
                            );

                            // Save current customer
                            DataManager.getInstance()
                                    .setCurrentUser(customer);

                            Toast.makeText(
                                    LoginActivity.this,
                                    "Login successful!",
                                    Toast.LENGTH_SHORT
                            ).show();

                            Intent intent =
                                    new Intent(
                                            LoginActivity.this,
                                            CustomerHomeActivity.class
                                    );

                            intent.setFlags(
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
                            );

                            startActivity(intent);

                        } else {

                            String message =
                                    "Invalid email or password.";

                            if (response.body() != null
                                    && response.body().getMessage() != null) {

                                message =
                                        response.body().getMessage();
                            }

                            Toast.makeText(
                                    LoginActivity.this,
                                    message,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<LoginResponse> call,
                            @NonNull Throwable t) {

                        Toast.makeText(
                                LoginActivity.this,
                                "Unable to connect to the server.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }
}