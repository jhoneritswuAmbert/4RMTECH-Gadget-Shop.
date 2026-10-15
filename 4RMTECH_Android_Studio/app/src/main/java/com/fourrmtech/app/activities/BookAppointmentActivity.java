package com.fourrmtech.app.activities;

import android.os.Bundle;
import android.app.DatePickerDialog;
import java.util.Calendar;
import java.util.Locale;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fourrmtech.app.api.ApiClient;
import com.fourrmtech.app.api.ApiService;
import com.fourrmtech.app.databinding.ActivityBookAppointmentBinding;
import com.fourrmtech.app.models.CreateAppointmentRequest;
import com.fourrmtech.app.models.CreateAppointmentResponse;
import com.fourrmtech.app.models.Customer;
import com.fourrmtech.app.utils.DataManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookAppointmentActivity extends AppCompatActivity {

    private ActivityBookAppointmentBinding binding;
    private DatePickerDialog datePickerDialog;

    private static final String[] DEVICE_TYPES = {
            "Smartphone",
            "Laptop",
            "Desktop/PC",
            "Tablet",
            "Other"
    };

    private static final String[] TIME_SLOTS = {
            "9:00 AM",
            "10:00 AM",
            "11:00 AM",
            "1:00 PM",
            "2:00 PM",
            "3:00 PM",
            "4:00 PM"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityBookAppointmentBinding.inflate(
                getLayoutInflater()
        );

        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());
        binding.etDate.setKeyListener(null);
        binding.etDate.setShowSoftInputOnFocus(false);
        binding.etDate.setOnClickListener(v -> showDatePicker());
        binding.dateInputLayout.setEndIconOnClickListener(v -> showDatePicker());
        if (savedInstanceState != null) binding.etDate.setText(savedInstanceState.getString("preferred_date", ""));

        // Get currently logged-in customer
        Customer currentUser =
                DataManager.getInstance().getCurrentUser();

        if (currentUser == null) {

            Toast.makeText(
                    this,
                    "Please log in first.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        // Pre-fill customer information
        binding.etCustomerName.setText(
                currentUser.getFullName()
        );

        binding.etContact.setText(
                currentUser.getContactNumber()
        );

        // Device type dropdown
        ArrayAdapter<String> deviceAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        DEVICE_TYPES
                );

        binding.actvDeviceType.setAdapter(deviceAdapter);

        // Pre-select device type if passed through Intent
        String deviceType =
                getIntent().getStringExtra("device_type");

        if (deviceType != null) {

            binding.actvDeviceType.setText(
                    deviceType,
                    false
            );
        }

        // Time slot dropdown
        ArrayAdapter<String> timeAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        TIME_SLOTS
                );

        binding.actvTime.setAdapter(timeAdapter);

        binding.btnSubmit.setOnClickListener(
                v -> submitAppointment()
        );
    }

    private void showDatePicker() {
        if (datePickerDialog != null && datePickerDialog.isShowing()) return;
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        Calendar selected = (Calendar) today.clone();
        String current = binding.etDate.getText() == null ? "" : binding.etDate.getText().toString();
        if (current.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
            try {
                java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US);
                format.setLenient(false);
                java.util.Date parsed = format.parse(current);
                if (parsed != null && !parsed.before(today.getTime())) selected.setTime(parsed);
            } catch (java.text.ParseException ignored) { }
        }
        datePickerDialog = new DatePickerDialog(this,
                android.R.style.Theme_Material_Light_Dialog_Alert,
                (view, year, month, day) -> binding.etDate.setText(
                        String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day)),
                selected.get(Calendar.YEAR), selected.get(Calendar.MONTH), selected.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(today.getTimeInMillis());
        datePickerDialog.show();
        android.widget.Button confirm = datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE);
        android.widget.Button cancel = datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE);
        confirm.setText("Set date");
        cancel.setText("Cancel");
        confirm.setTextColor(android.graphics.Color.parseColor("#0B1748"));
        cancel.setTextColor(android.graphics.Color.parseColor("#0B1748"));
        confirm.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#CEE800")));
        cancel.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#EEF2FF")));
        confirm.setAllCaps(false);
        cancel.setAllCaps(false);
        int buttonHeight = (int) (48 * getResources().getDisplayMetrics().density);
        confirm.setMinHeight(buttonHeight);
        cancel.setMinHeight(buttonHeight);
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        outState.putString("preferred_date", binding.etDate.getText() == null ? "" : binding.etDate.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override protected void onDestroy() {
        if (datePickerDialog != null) datePickerDialog.dismiss();
        super.onDestroy();
    }

    private void submitAppointment() {

        Customer currentUser =
                DataManager.getInstance().getCurrentUser();

        if (currentUser == null) {

            Toast.makeText(
                    this,
                    "Please log in first.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String name =
                binding.etCustomerName
                        .getText()
                        .toString()
                        .trim();

        String contact =
                binding.etContact
                        .getText()
                        .toString()
                        .trim();

        String device =
                binding.actvDeviceType
                        .getText()
                        .toString()
                        .trim();

        String model =
                binding.etDeviceModel
                        .getText()
                        .toString()
                        .trim();

        String issue =
                binding.etIssue
                        .getText()
                        .toString()
                        .trim();

        String date =
                binding.etDate
                        .getText()
                        .toString()
                        .trim();

        String time =
                binding.actvTime
                        .getText()
                        .toString()
                        .trim();

        // Validate fields
        if (
                name.isEmpty()
                        || model.isEmpty()
                        || issue.isEmpty()
                        || date.isEmpty()
                        || time.isEmpty()
        ) {

            Toast.makeText(
                    this,
                    "Please fill in all fields.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (device.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please select a device type.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Prevent multiple submissions
        binding.btnSubmit.setEnabled(false);

        // Create API request
        CreateAppointmentRequest request =
                new CreateAppointmentRequest(
                        currentUser.getId(),
                        device,
                        model,
                        issue,
                        date,
                        time
                );

        // Create Retrofit service
        ApiService apiService =
                ApiClient
                        .getClient()
                        .create(ApiService.class);

        // Send appointment to PHP API
        Call<CreateAppointmentResponse> call =
                apiService.createAppointment(request);

        call.enqueue(
                new Callback<CreateAppointmentResponse>() {

                    @Override
                    public void onResponse(
                            Call<CreateAppointmentResponse> call,
                            Response<CreateAppointmentResponse> response
                    ) {

                        if (isFinishing()) {
                            return;
                        }

                        binding.btnSubmit.setEnabled(true);

                        if (
                                response.isSuccessful()
                                        && response.body() != null
                                        && response.body().isSuccess()
                        ) {

                            // Appointment was successfully
                            // saved to PostgreSQL.

                            binding.formLayout.setVisibility(
                                    View.GONE
                            );

                            binding.confirmationLayout.setVisibility(
                                    View.VISIBLE
                            );

                            binding.tvConfirmDevice.setText(
                                    device + " — " + model
                            );

                            binding.tvConfirmDate.setText(
                                    date
                            );

                            binding.tvConfirmTime.setText(
                                    time
                            );

                            binding.tvConfirmIssue.setText(
                                    issue
                            );

                            binding.btnGoHome.setOnClickListener(
                                    v -> finish()
                            );

                            Toast.makeText(
                                    BookAppointmentActivity.this,
                                    "Appointment booked successfully.",
                                    Toast.LENGTH_SHORT
                            ).show();

                        } else {

                            Toast.makeText(
                                    BookAppointmentActivity.this,
                                    "Failed to create appointment.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<CreateAppointmentResponse> call,
                            Throwable t
                    ) {

                        if (isFinishing()) {
                            return;
                        }

                        binding.btnSubmit.setEnabled(true);

                        Toast.makeText(
                                BookAppointmentActivity.this,
                                "Unable to connect to the server.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }
}