package com.fourrmtech.app.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.text.InputType;
import android.text.InputFilter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fourrmtech.app.adapters.AppointmentAdapter;
import com.fourrmtech.app.api.ApiClient;
import com.fourrmtech.app.api.ApiService;
import com.fourrmtech.app.databinding.ActivityAdminAppointmentsBinding;
import com.fourrmtech.app.models.Appointment;
import com.fourrmtech.app.models.AppointmentResponse;
import com.fourrmtech.app.models.CreateAppointmentResponse;
import com.fourrmtech.app.models.UpdateAppointmentStatusRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminAppointmentsActivity extends AppCompatActivity {

    private ActivityAdminAppointmentsBinding binding;
    private AppointmentAdapter adapter;
    private String selectedFilter = "All";

    private final List<Appointment> allAppointments =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminAppointmentsBinding.inflate(
                getLayoutInflater()
        );

        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        adapter = new AppointmentAdapter(
                this,
                new ArrayList<>(),
                true
        );

        adapter.setOnClickListener(
                appt -> showAppointmentDetail(appt)
        );

        binding.rvAppointments.setLayoutManager(
                new LinearLayoutManager(this)
        );

        binding.rvAppointments.setAdapter(adapter);

        // Filter chips
        binding.chipAll.setOnClickListener(
                v -> applyFilter("All")
        );

        binding.chipPending.setOnClickListener(
                v -> applyFilter("Pending")
        );

        binding.chipApproved.setOnClickListener(
                v -> applyFilter("Approved")
        );

        binding.chipReadyToClaim.setOnClickListener(v -> applyFilter("Ready to Claim"));

        binding.chipCompleted.setOnClickListener(
                v -> applyFilter("Completed")
        );

        binding.chipCancelled.setOnClickListener(
                v -> applyFilter("Cancelled")
        );



        // Search
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

                        filterBySearch(s.toString());
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );

        // Load real appointments from PostgreSQL
        loadAppointments();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadAppointments();
    }

    private void loadAppointments() {

        ApiService apiService =
                ApiClient
                        .getClient()
                        .create(ApiService.class);

        apiService.getAllAppointments()
                .enqueue(
                        new Callback<AppointmentResponse>() {

                            @Override
                            public void onResponse(
                                    @NonNull Call<AppointmentResponse> call,
                                    @NonNull Response<AppointmentResponse> response) {

                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body().isSuccess()) {

                                    AppointmentResponse data =
                                            response.body();

                                    allAppointments.clear();

                                    if (data.getAppointments() != null) {

                                        for (
                                                AppointmentResponse.AppointmentData item
                                                : data.getAppointments()
                                        ) {

                                            Appointment.Status status =
                                                    convertStatus(
                                                            item.getStatus()
                                                    );

                                            String customerName =
                                                    item.getFirstName()
                                                            + " "
                                                            + item.getLastName();

                                            Appointment appointment =
                                                    new Appointment(
                                                            item.getAppointmentId(),
                                                            customerName,
                                                            item.getContactNumber(),
                                                            item.getDeviceType(),
                                                            item.getDeviceModel(),
                                                            item.getServiceDescription(),
                                                            item.getAppointmentDate(),
                                                            item.getAppointmentTime(),
                                                            status
                                                    );

                                            appointment.setRepairSummary(item.getRepairNotes(), item.getRepairTotal());

                                            allAppointments.add(
                                                    appointment
                                            );
                                        }
                                    }

                                    applyCurrentFilter();

                                } else {

                                    Toast.makeText(
                                            AdminAppointmentsActivity.this,
                                            "Failed to load appointments.",
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            }

                            @Override
                            public void onFailure(
                                    @NonNull Call<AppointmentResponse> call,
                                    @NonNull Throwable t) {

                                Toast.makeText(
                                        AdminAppointmentsActivity.this,
                                        "Connection error: "
                                                + t.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    private Appointment.Status convertStatus(
            String status) {

        if (status == null) {
            return Appointment.Status.PENDING;
        }

        switch (status) {

            case "Approved":
                return Appointment.Status.APPROVED;

            case "Ready to Claim":
                return Appointment.Status.READY_TO_CLAIM;

            case "Completed":
                return Appointment.Status.COMPLETED;

            case "Cancelled":
                return Appointment.Status.CANCELLED;

            case "Pending":
            default:
                return Appointment.Status.PENDING;
        }
    }

    private void applyFilter(String status) {

        selectedFilter = status;

        applyCurrentFilter();
    }

    private void applyCurrentFilter() {

        List<Appointment> filtered =
                new ArrayList<>();

        String search =
                binding.etSearch
                        .getText()
                        .toString()
                        .trim()
                        .toLowerCase();

        for (Appointment appointment :
                allAppointments) {

            boolean matchesStatus =
                    selectedFilter.equals("All")
                            || appointment
                            .getStatusLabel()
                            .equalsIgnoreCase(
                                    selectedFilter
                            );

            boolean matchesSearch =
                    search.isEmpty()
                            || appointment
                            .getCustomerName()
                            .toLowerCase()
                            .contains(search)
                            || appointment
                            .getDeviceModel()
                            .toLowerCase()
                            .contains(search);

            if (matchesStatus && matchesSearch) {

                filtered.add(appointment);
            }
        }

        adapter.updateList(filtered);
    }

    private void filterBySearch(String query) {

        applyCurrentFilter();
    }

    private void showAppointmentDetail(
            Appointment appt) {

        String details =
                "Customer: "
                        + appt.getCustomerName()
                        + "\n\n"
                        + "Contact: "
                        + appt.getContactNumber()
                        + "\n\n"
                        + "Device: "
                        + appt.getDeviceSummary()
                        + "\n\n"
                        + "Issue: "
                        + appt.getIssue()
                        + "\n\n"
                        + "Date: "
                        + appt.getDate()
                        + "\n\n"
                        + "Time: "
                        + appt.getTime()
                        + "\n\n"
                        + "Status: "
                        + appt.getStatusLabel();

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);

        builder.setTitle("Appointment Details");
        if (!appt.getRepairSummary().isEmpty()) details += "\n\n" + appt.getRepairSummary();
        builder.setMessage(details);

        if (
                appt.getStatus()
                        == Appointment.Status.PENDING
        ) {

            builder.setNegativeButton(
                    "Cancel",
                    (dialog, which) ->
                            updateAppointmentStatus(
                                    appt,
                                    "Cancelled"
                            )
            );

            builder.setPositiveButton(
                    "Approve",
                    (dialog, which) ->
                            updateAppointmentStatus(
                                    appt,
                                    "Approved"
                            )
            );

        } else if (appt.getStatus() == Appointment.Status.APPROVED) {
            builder.setNegativeButton("Close", null);
            builder.setPositiveButton("Ready to Claim", (dialog, which) -> showClaimForm(appt));
        } else if (appt.getStatus() == Appointment.Status.READY_TO_CLAIM) {
            builder.setNegativeButton("Close", null);
            builder.setPositiveButton("Complete", (dialog, which) ->
                    new AlertDialog.Builder(this).setTitle("Confirm collection")
                            .setMessage("Has the customer collected this device?")
                            .setNegativeButton("Back", null)
                            .setPositiveButton("Yes, complete", (d, w) -> updateAppointmentStatus(appt, "Completed"))
                            .show());
        } else {

            builder.setPositiveButton(
                    "Close",
                    null
            );
        }

        builder.show();
    }

    private void showClaimForm(Appointment appt) {
        LinearLayout fields = new LinearLayout(this);
        fields.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (20 * getResources().getDisplayMetrics().density);
        fields.setPadding(padding, padding, padding, padding);
        EditText notes = new EditText(this);
        notes.setHint("Work done / parts and price breakdown\nExample: LCD ₱1,500; labor ₱300");
        notes.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        notes.setMinLines(4);
        notes.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3000)});
        fields.addView(notes, new LinearLayout.LayoutParams(-1, -2));
        EditText total = new EditText(this);
        total.setHint("Total amount (PHP) · 0 for a free repair");
        total.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        total.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        fields.addView(total, new LinearLayout.LayoutParams(-1, -2));
        android.widget.TextView help = new android.widget.TextView(this);
        help.setText("The customer can view these details. Payment is handled at the shop.");
        fields.addView(help);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(fields);
        AlertDialog form = new AlertDialog.Builder(this).setTitle("Ready to Claim · Repair summary")
                .setView(scroll).setNegativeButton("Back", null).setPositiveButton("Save", null).create();
        form.setOnShowListener(dialog -> form.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String repairNotes = notes.getText().toString().trim();
            String repairTotal = total.getText().toString().trim();
            if (repairNotes.isEmpty()) { notes.setError("Describe the work done."); return; }
            if (!repairTotal.matches("[0-9]{1,8}(\\.[0-9]{1,2})?")) {
                total.setError("Enter 0 to 99,999,999.99 with at most two decimal places."); return;
            }
            form.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            form.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(false);
            form.setCancelable(false);
            form.setCanceledOnTouchOutside(false);
            sendStatus(appt, "Ready to Claim", repairNotes, repairTotal, form);
        }));
        form.show();
    }

    private void updateAppointmentStatus(Appointment appt, String newStatus) {
        sendStatus(appt, newStatus, null, null, null);
    }

    private void sendStatus(Appointment appt, String newStatus, String notes, String total, AlertDialog form) {
        UpdateAppointmentStatusRequest request = new UpdateAppointmentStatusRequest(appt.getId(), newStatus, notes, total);

        ApiService apiService =
                ApiClient
                        .getClient()
                        .create(ApiService.class);

        apiService.updateAppointmentStatus(request)
                .enqueue(
                        new Callback<CreateAppointmentResponse>() {

                            @Override
                            public void onResponse(
                                    @NonNull Call<CreateAppointmentResponse> call,
                                    @NonNull Response<CreateAppointmentResponse> response) {

                                if (
                                        response.isSuccessful()
                                                && response.body() != null
                                                && response.body().isSuccess()
                                ) {

                                    Toast.makeText(
                                            AdminAppointmentsActivity.this,
                                            "Appointment status updated.",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    if (form != null) form.dismiss();
                                    loadAppointments();

                                } else {
                                    restoreClaimForm(form);

                                    Toast.makeText(
                                            AdminAppointmentsActivity.this,
                                            "Could not save. Refresh appointments and check the database update.",
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            }

                            @Override
                            public void onFailure(
                                    @NonNull Call<CreateAppointmentResponse> call,
                                    @NonNull Throwable t) {
                                restoreClaimForm(form);

                                Toast.makeText(
                                        AdminAppointmentsActivity.this,
                                        "Connection error: "
                                                + t.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }
    private void restoreClaimForm(AlertDialog form) {
        if (form == null || !form.isShowing()) return;
        form.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
        form.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(true);
        form.setCancelable(true);
        form.setCanceledOnTouchOutside(true);
    }

}