package com.fourrmtech.app.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fourrmtech.app.api.ApiClient;
import com.fourrmtech.app.api.ApiService;
import com.fourrmtech.app.databinding.ActivityCustomerInquiryBinding;
import com.fourrmtech.app.models.InquiryRequest;
import com.fourrmtech.app.models.InquiryRequestResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerInquiryActivity extends AppCompatActivity {

    private ActivityCustomerInquiryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCustomerInquiryBinding.inflate(
                getLayoutInflater()
        );

        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnSubmit.setOnClickListener(v -> submitInquiry());
    }

    private void submitInquiry() {

        String message =
                binding.etMessage.getText()
                        .toString()
                        .trim();

        if (message.isEmpty()) {

            binding.etMessage.setError(
                    "Please enter your message."
            );

            return;
        }

        int customerId =
                com.fourrmtech.app.utils.DataManager
                        .getInstance()
                        .getCurrentUser()
                        .getId();

        binding.btnSubmit.setEnabled(false);

        InquiryRequest request =
                new InquiryRequest(
                        customerId,
                        message
                );

        ApiService apiService =
                ApiClient
                        .getClient()
                        .create(ApiService.class);

        apiService.submitInquiry(request)
                .enqueue(
                        new Callback<InquiryRequestResponse>() {

                            @Override
                            public void onResponse(
                                    Call<InquiryRequestResponse> call,
                                    Response<InquiryRequestResponse> response) {

                                binding.btnSubmit.setEnabled(true);

                                if (
                                        response.isSuccessful()
                                                && response.body() != null
                                                && response.body().isSuccess()
                                ) {

                                    Toast.makeText(
                                            CustomerInquiryActivity.this,
                                            "Inquiry sent successfully.",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    binding.etMessage.setText("");

                                    finish();

                                } else {

                                    Toast.makeText(
                                            CustomerInquiryActivity.this,
                                            "Failed to send inquiry.",
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            }

                            @Override
                            public void onFailure(
                                    Call<InquiryRequestResponse> call,
                                    Throwable t) {

                                binding.btnSubmit.setEnabled(true);

                                Toast.makeText(
                                        CustomerInquiryActivity.this,
                                        "Connection error: "
                                                + t.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }
}