package com.fourrmtech.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fourrmtech.app.activities.BookAppointmentActivity;
import com.fourrmtech.app.adapters.AppointmentAdapter;
import com.fourrmtech.app.api.ApiClient;
import com.fourrmtech.app.api.ApiService;
import com.fourrmtech.app.databinding.FragmentBookingsBinding;
import com.fourrmtech.app.models.Appointment;
import com.fourrmtech.app.models.AppointmentResponse;
import com.fourrmtech.app.models.CancelAppointmentRequest;
import com.fourrmtech.app.models.CreateAppointmentResponse;
import com.fourrmtech.app.models.Customer;
import com.fourrmtech.app.utils.DataManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingsFragment extends Fragment {

    private FragmentBookingsBinding binding;
    private AppointmentAdapter adapter;
    private String selectedFilter = "All";

    private List<Appointment> appointments =
            new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        binding = FragmentBookingsBinding.inflate(
                inflater,
                container,
                false
        );

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupFilters();
        setupBookButton();

        loadAppointments();
    }

    private void setupRecyclerView() {

        adapter = new AppointmentAdapter(
                requireContext(),
                appointments,
                false
        );

        adapter.setOnClickListener(appointment -> {
            String details = "Device: " + appointment.getDeviceSummary()
                    + "\n\nIssue: " + appointment.getIssue()
                    + "\n\nStatus: " + appointment.getStatusLabel()
                    + "\n\nDate: " + appointment.getDate() + " · " + appointment.getTime();
            if (!appointment.getRepairSummary().isEmpty()) details += "\n\n" + appointment.getRepairSummary();
            if (appointment.getStatus() == Appointment.Status.READY_TO_CLAIM)
                details += "\n\nYour device is ready for collection.";
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Appointment details").setMessage(details).setPositiveButton("Close", null).show();
        });

        adapter.setOnCancelAppointmentListener(
                this::cancelAppointment
        );

        binding.rvAppointments.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        binding.rvAppointments.setAdapter(adapter);
    }

    private void setupFilters() {

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
    }

    private void setupBookButton() {

        binding.btnBookNew.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            requireContext(),
                            BookAppointmentActivity.class
                    )
            );
        });
    }

    private void loadAppointments() {

        Customer currentUser =
                DataManager.getInstance().getCurrentUser();

        if (currentUser == null) {

            if (isAdded()) {

                Toast.makeText(
                        requireContext(),
                        "Please log in first.",
                        Toast.LENGTH_SHORT
                ).show();
            }

            return;
        }

        int customerId =
                currentUser.getId();

        ApiService apiService =
                ApiClient
                        .getClient()
                        .create(ApiService.class);

        Call<AppointmentResponse> call =
                apiService.getAppointments(customerId);

        call.enqueue(
                new Callback<AppointmentResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<AppointmentResponse> call,
                            @NonNull Response<AppointmentResponse> response) {

                        if (!isAdded() || binding == null) {
                            return;
                        }

                        if (
                                response.isSuccessful()
                                        && response.body() != null
                                        && response.body().isSuccess()
                        ) {

                            AppointmentResponse appointmentResponse =
                                    response.body();

                            List<Appointment> loadedAppointments =
                                    new ArrayList<>();

                            if (
                                    appointmentResponse
                                            .getAppointments() != null
                            ) {

                                for (
                                        AppointmentResponse.AppointmentData data
                                        : appointmentResponse
                                        .getAppointments()
                                ) {

                                    Appointment.Status status =
                                            convertStatus(
                                                    data.getStatus()
                                            );

                                    Appointment appointment =
                                            new Appointment(
                                                    data.getAppointmentId(),
                                                    currentUser.getFullName(),
                                                    currentUser.getContactNumber(),
                                                    data.getDeviceType(),
                                                    data.getDeviceModel(),
                                                    data.getServiceDescription(),
                                                    data.getAppointmentDate(),
                                                    data.getAppointmentTime(),
                                                    status
                                            );

                                    appointment.setRepairSummary(data.getRepairNotes(), data.getRepairTotal());

                                    loadedAppointments.add(
                                            appointment
                                    );
                                }
                            }

                            appointments.clear();

                            appointments.addAll(
                                    loadedAppointments
                            );

                            applyFilter(
                                    selectedFilter
                            );

                        } else {

                            Toast.makeText(
                                    requireContext(),
                                    "Failed to load appointments.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<AppointmentResponse> call,
                            @NonNull Throwable t) {

                        if (!isAdded() || binding == null) {
                            return;
                        }

                        Toast.makeText(
                                requireContext(),
                                "Unable to connect to the server.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }

    private void cancelAppointment(
            Appointment appointment) {

        Customer currentUser =
                DataManager.getInstance().getCurrentUser();

        if (currentUser == null) {

            Toast.makeText(
                    requireContext(),
                    "Please log in first.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        int customerId =
                currentUser.getId();

        int appointmentId =
                appointment.getId();

        CancelAppointmentRequest request =
                new CancelAppointmentRequest(
                        appointmentId,
                        customerId
                );

        ApiService apiService =
                ApiClient
                        .getClient()
                        .create(ApiService.class);

        Call<CreateAppointmentResponse> call =
                apiService.cancelAppointment(request);

        call.enqueue(
                new Callback<CreateAppointmentResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<CreateAppointmentResponse> call,
                            @NonNull Response<CreateAppointmentResponse> response) {

                        if (!isAdded() || binding == null) {
                            return;
                        }

                        if (
                                response.isSuccessful()
                                        && response.body() != null
                                        && response.body().isSuccess()
                        ) {

                            Toast.makeText(
                                    requireContext(),
                                    "Appointment cancelled.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            // Reload appointments from PostgreSQL
                            loadAppointments();

                        } else {

                            String message =
                                    "Unable to cancel appointment.";

                            if (
                                    response.body() != null
                                            && response.body().getMessage() != null
                            ) {

                                message =
                                        response.body().getMessage();
                            }

                            Toast.makeText(
                                    requireContext(),
                                    message,
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<CreateAppointmentResponse> call,
                            @NonNull Throwable t) {

                        if (!isAdded() || binding == null) {
                            return;
                        }

                        Toast.makeText(
                                requireContext(),
                                "Unable to connect to the server.",
                                Toast.LENGTH_SHORT
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

        switch (status.toLowerCase()) {

            case "approved":
                return Appointment.Status.APPROVED;

            case "ready to claim":
                return Appointment.Status.READY_TO_CLAIM;

            case "completed":
                return Appointment.Status.COMPLETED;

            case "cancelled":
                return Appointment.Status.CANCELLED;

            case "pending":
            default:
                return Appointment.Status.PENDING;
        }
    }

    private void applyFilter(String status) {

        selectedFilter = status;

        List<Appointment> filtered =
                new ArrayList<>();

        for (Appointment appointment : appointments) {

            if (
                    status.equals("All")
                            || appointment
                            .getStatusLabel()
                            .equalsIgnoreCase(status)
            ) {

                filtered.add(appointment);
            }
        }

        adapter.updateList(filtered);
    }

    @Override
    public void onResume() {

        super.onResume();

        if (binding != null) {
            loadAppointments();
        }
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();

        binding = null;
    }
}