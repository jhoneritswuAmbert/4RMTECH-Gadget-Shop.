package com.fourrmtech.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.fourrmtech.app.api.ApiClient;
import com.fourrmtech.app.api.ApiService;
import com.fourrmtech.app.api.InquiryNotificationsApi;
import com.fourrmtech.app.models.InquiryNotificationsResponse;
import com.fourrmtech.app.databinding.ActivityAdminDashboardBinding;
import com.fourrmtech.app.models.AppointmentResponse;
import com.fourrmtech.app.utils.DataManager;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardActivity extends AppCompatActivity {
    private ActivityAdminDashboardBinding binding;
    private ApiService api;
    private final List<Call<?>> requests = new ArrayList<>();
    private int refreshVersion = 0;
    private boolean errorShown = false;
    private boolean dashboardVisible = false;
    private InquiryNotificationsApi notificationApi;
    private Call<InquiryNotificationsResponse> notificationCall;
    private final Handler notificationHandler = new Handler(Looper.getMainLooper());
    private final Runnable notificationPoll = new Runnable() {
        @Override public void run() {
            if (!dashboardVisible) return;
            checkInquiries();
            notificationHandler.postDelayed(this, 15000);
        }
    };

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        api = ApiClient.getClient().create(ApiService.class);
        notificationApi = ApiClient.getClient().create(InquiryNotificationsApi.class);
        binding.cardProducts.setOnClickListener(v -> open(AdminProductsActivity.class));
        binding.cardCategories.setOnClickListener(v -> open(AdminCategoriesActivity.class));
        binding.cardAppointments.setOnClickListener(v -> open(AdminAppointmentsActivity.class));
        binding.cardCustomers.setOnClickListener(v -> open(AdminCustomersActivity.class));
        binding.cardInquiries.setOnClickListener(v -> open(AdminInquiriesActivity.class));
        binding.cardPendingBanner.setOnClickListener(v -> open(AdminAppointmentsActivity.class));
        binding.btnLogout.setOnClickListener(v -> {
            DataManager.getInstance().logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void open(Class<?> activity) {
        startActivity(new Intent(this, activity));
    }

    @Override protected void onResume() {
        super.onResume();
        refreshCounts();
        dashboardVisible = true;
        notificationHandler.removeCallbacks(notificationPoll);
        notificationHandler.post(notificationPoll);
    }

    @Override protected void onPause() {
        dashboardVisible = false;
        notificationHandler.removeCallbacks(notificationPoll);
        if (notificationCall != null) { notificationCall.cancel(); notificationCall = null; }
        super.onPause();
    }

    private void checkInquiries() {
        if (!dashboardVisible || notificationCall != null) return;
        Call<InquiryNotificationsResponse> call = notificationApi.getNotifications();
        notificationCall = call;
        call.enqueue(new Callback<InquiryNotificationsResponse>() {
            @Override public void onResponse(Call<InquiryNotificationsResponse> source, Response<InquiryNotificationsResponse> response) {
                if (source != notificationCall || !dashboardVisible) return;
                notificationCall = null;
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    int count = response.body().getUnreadCount();
                    binding.tvInquiryBadge.setText(count + " new");
                    binding.tvInquiryBadge.setContentDescription(count + " new customer inquiries");
                    binding.tvInquiryBadge.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
                } else showInquiryCheckError();
            }
            @Override public void onFailure(Call<InquiryNotificationsResponse> source, Throwable error) {
                if (source != notificationCall || !dashboardVisible || source.isCanceled()) return;
                notificationCall = null;
                showInquiryCheckError();
            }
        });
    }
    private void showInquiryCheckError() {
        binding.tvInquiryBadge.setText("Check connection");
        binding.tvInquiryBadge.setContentDescription("Could not check new inquiries. Check XAMPP and reopen this screen.");
        binding.tvInquiryBadge.setVisibility(View.VISIBLE);
    }

    private void refreshCounts() {
        int version = ++refreshVersion;
        for (Call<?> call : requests) call.cancel();
        requests.clear();
        errorShown = false;
        binding.tvTotalProducts.setText("…");
        binding.tvTotalCategories.setText("…");
        binding.tvTotalCustomers.setText("…");
        binding.tvPendingAppointments.setText("…");
        loadCount(api.getProducts(), binding.tvTotalProducts, version,
                data -> data.isSuccess() && data.getProducts() != null
                        ? data.getProducts().size() : -1);
        loadCount(api.getCategories(), binding.tvTotalCategories, version,
                data -> data.isSuccess() && data.getCategories() != null
                        ? data.getCategories().size() : -1);
        loadCount(api.getCustomers(), binding.tvTotalCustomers, version,
                data -> data.isSuccess() && data.getCustomers() != null
                        ? data.getCustomers().size() : -1);
        loadCount(api.getAllAppointments(), binding.tvPendingAppointments, version,
                data -> {
                    if (!data.isSuccess() || data.getAppointments() == null) return -1;
                    int pending = 0;
                    for (AppointmentResponse.AppointmentData item : data.getAppointments()) {
                        if (item != null && item.getStatus() != null
                                && "pending".equalsIgnoreCase(item.getStatus().trim())) pending++;
                    }
                    return pending;
                });
    }

    private interface CountReader<T> { int count(T data); }

    private <T> void loadCount(Call<T> call, TextView label, int version, CountReader<T> reader) {
        requests.add(call);
        call.enqueue(new Callback<T>() {
            @Override public void onResponse(Call<T> source, Response<T> response) {
                if (!current(version)) return;
                T data = response.body();
                int count = response.isSuccessful() && data != null ? reader.count(data) : -1;
                if (count >= 0) label.setText(String.valueOf(count));
                else {
                    label.setText("—");
                    showError(response.code() == 401
                            ? "Sign in again to load live dashboard counts."
                            : "Some dashboard counts could not load. Reopen this screen to retry.");
                }
            }
            @Override public void onFailure(Call<T> source, Throwable error) {
                if (!current(version) || source.isCanceled()) return;
                label.setText("—");
                showError("Could not load live counts. Check your connection and XAMPP.");
            }
        });
    }

    private boolean current(int version) {
        return version == refreshVersion && !isFinishing() && !isDestroyed();
    }

    private void showError(String message) {
        if (errorShown) return;
        errorShown = true;
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override protected void onDestroy() {
        dashboardVisible = false;
        notificationHandler.removeCallbacks(notificationPoll);
        if (notificationCall != null) notificationCall.cancel();
        ++refreshVersion;
        for (Call<?> call : requests) call.cancel();
        requests.clear();
        super.onDestroy();
    }
}
