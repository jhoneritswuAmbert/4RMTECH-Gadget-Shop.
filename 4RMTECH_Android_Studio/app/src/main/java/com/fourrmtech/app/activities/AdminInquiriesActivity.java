package com.fourrmtech.app.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fourrmtech.app.adapters.InquiryAdapter;
import com.fourrmtech.app.api.ApiClient;
import com.fourrmtech.app.api.ApiService;
import com.fourrmtech.app.api.InquiryNotificationsApi;
import com.fourrmtech.app.models.MarkInquiriesReadRequest;
import com.fourrmtech.app.models.InquiryNotificationsResponse;
import com.fourrmtech.app.databinding.ActivityAdminInquiriesBinding;
import com.fourrmtech.app.models.Inquiry;
import com.fourrmtech.app.models.InquiryListResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminInquiriesActivity extends AppCompatActivity {

    private ActivityAdminInquiriesBinding binding;
    private InquiryAdapter adapter;
    private boolean inquiryScreenVisible = false;
    private Call<InquiryListResponse> listCall;

    private final List<Inquiry> allInquiries =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminInquiriesBinding.inflate(
                getLayoutInflater()
        );

        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(
                v -> finish()
        );

        adapter = new InquiryAdapter(
                this,
                new ArrayList<>(),
                this::showInquiryDetail
        );

        binding.rvInquiries.setLayoutManager(
                new LinearLayoutManager(this)
        );

        binding.rvInquiries.setAdapter(adapter);

        binding.etSearch.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        filterInquiries(
                                s.toString()
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );

    }

    @Override
    protected void onResume() {
        super.onResume();
        inquiryScreenVisible = true;
        loadInquiries();
    }

    private void loadInquiries() {

        ApiService apiService =
                ApiClient
                        .getClient()
                        .create(ApiService.class);

        if (listCall != null) listCall.cancel();
        listCall = apiService.getAllInquiries();
        listCall.enqueue(
                        new Callback<InquiryListResponse>() {

                            @Override
                            public void onResponse(
                                    @NonNull Call<InquiryListResponse> call,
                                    @NonNull Response<InquiryListResponse> response) {
                                if (!inquiryScreenVisible || call != listCall || isFinishing() || isDestroyed()) return;

                                if (
                                        response.isSuccessful()
                                                && response.body() != null
                                                && response.body().isSuccess()
                                ) {

                                    allInquiries.clear();
                                    List<Integer> unreadIds = new ArrayList<>();

                                    InquiryListResponse data =
                                            response.body();

                                    if (data.getInquiries() != null) {

                                        for (
                                                InquiryListResponse.InquiryData item
                                                : data.getInquiries()
                                        ) {

                                            if (item.isNew()) unreadIds.add(item.getInquiryId());
                                            String customerName =
                                                    safeValue(
                                                            item.getFirstName()
                                                    )
                                                            + " "
                                                            + safeValue(
                                                            item.getLastName()
                                                    );

                                            Inquiry inquiry =
                                                    new Inquiry(
                                                            item.getInquiryId(),
                                                            customerName.trim(),
                                                            item.getEmail(),
                                                            item.getContactNumber(),
                                                            item.getMessage(),
                                                            item.getDateSubmitted()
                                                    );

                                            allInquiries.add(
                                                    inquiry
                                            );
                                        }
                                    }

                                    filterInquiries(
                                            binding.etSearch
                                                    .getText()
                                                    .toString()
                                    );
                                    markLoadedInquiries(unreadIds);

                                } else {

                                    Toast.makeText(
                                            AdminInquiriesActivity.this,
                                            "Failed to load inquiries.",
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            }

                            @Override
                            public void onFailure(
                                    @NonNull Call<InquiryListResponse> call,
                                    @NonNull Throwable t) {
                                if (!inquiryScreenVisible || call.isCanceled() || call != listCall) return;

                                Toast.makeText(
                                        AdminInquiriesActivity.this,
                                        "Connection error: "
                                                + t.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    private void filterInquiries(String query) {

        String search =
                query == null
                        ? ""
                        : query.trim().toLowerCase();

        List<Inquiry> filtered =
                new ArrayList<>();

        for (Inquiry inquiry : allInquiries) {

            if (
                    inquiry.getCustomerName()
                            .toLowerCase()
                            .contains(search)

                            || inquiry.getEmail()
                            .toLowerCase()
                            .contains(search)

                            || inquiry.getMessage()
                            .toLowerCase()
                            .contains(search)
            ) {

                filtered.add(inquiry);
            }
        }

        adapter.updateList(filtered);
    }

    private void showInquiryDetail(Inquiry inquiry) {

        String details =
                "Customer: "
                        + inquiry.getCustomerName()
                        + "\n\n"

                        + "Email: "
                        + safeValue(inquiry.getEmail())
                        + "\n\n"

                        + "Contact: "
                        + safeValue(inquiry.getContactNumber())
                        + "\n\n"

                        + "Message:\n"
                        + safeValue(inquiry.getMessage())
                        + "\n\n"

                        + "Submitted: "
                        + safeValue(inquiry.getDateSubmitted());

        new AlertDialog.Builder(this)
                .setTitle("Inquiry Details")
                .setMessage(details)
                .setPositiveButton(
                        "Close",
                        null
                )
                .show();
    }

    private String safeValue(String value) {

        if (value == null
                || value.trim().isEmpty()) {

            return "Not provided";
        }

        return value;
    }
    @Override protected void onPause() {
        inquiryScreenVisible = false;
        if (listCall != null) listCall.cancel();
        super.onPause();
    }

    private void markLoadedInquiries(List<Integer> ids) {
        if (ids.isEmpty() || !inquiryScreenVisible) return;
        InquiryNotificationsApi api = ApiClient.getClient().create(InquiryNotificationsApi.class);
        for (int i = 0; i < ids.size(); i += 10000) {
            List<Integer> batch = new ArrayList<>(ids.subList(i, Math.min(i + 10000, ids.size())));
            api.markRead(new MarkInquiriesReadRequest(batch)).enqueue(new Callback<InquiryNotificationsResponse>() {
                @Override public void onResponse(Call<InquiryNotificationsResponse> call, Response<InquiryNotificationsResponse> response) {
                    if (!inquiryScreenVisible || isFinishing() || isDestroyed()) return;
                    if (!response.isSuccessful() || response.body() == null || !response.body().isSuccess()) showUnreadSaveError();
                }
                @Override public void onFailure(Call<InquiryNotificationsResponse> call, Throwable t) {
                    if (inquiryScreenVisible && !isFinishing() && !isDestroyed()) showUnreadSaveError();
                }
            });
        }
    }
    private void showUnreadSaveError() {
        Toast.makeText(this, "Messages loaded, but the new badge could not clear. Reopen Inquiries to retry.", Toast.LENGTH_LONG).show();
    }

}