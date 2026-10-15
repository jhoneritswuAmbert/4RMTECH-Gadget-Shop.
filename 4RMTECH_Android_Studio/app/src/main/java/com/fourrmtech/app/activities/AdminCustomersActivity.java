package com.fourrmtech.app.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fourrmtech.app.adapters.CustomerAdapter;
import com.fourrmtech.app.api.ApiClient;
import com.fourrmtech.app.api.ApiService;
import com.fourrmtech.app.databinding.ActivityAdminCustomersBinding;
import com.fourrmtech.app.models.Customer;
import com.fourrmtech.app.models.CustomerListResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCustomersActivity extends AppCompatActivity {

    private ActivityAdminCustomersBinding binding;
    private CustomerAdapter adapter;

    private final List<Customer> allCustomers =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminCustomersBinding.inflate(
                getLayoutInflater()
        );

        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(
                v -> finish()
        );

        adapter = new CustomerAdapter(
                this,
                new ArrayList<>(),
                customer -> showCustomerDetail(customer)
        );

        binding.rvCustomers.setLayoutManager(
                new LinearLayoutManager(this)
        );

        binding.rvCustomers.setAdapter(adapter);

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

                        filterCustomers(
                                s.toString()
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );

        loadCustomers();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadCustomers();
    }

    private void loadCustomers() {

        ApiService apiService =
                ApiClient
                        .getClient()
                        .create(ApiService.class);

        apiService.getCustomers()
                .enqueue(
                        new Callback<CustomerListResponse>() {

                            @Override
                            public void onResponse(
                                    @NonNull Call<CustomerListResponse> call,
                                    @NonNull Response<CustomerListResponse> response) {

                                if (
                                        response.isSuccessful()
                                                && response.body() != null
                                                && response.body().isSuccess()
                                ) {

                                    allCustomers.clear();

                                    CustomerListResponse data =
                                            response.body();

                                    if (data.getCustomers() != null) {

                                        for (
                                                CustomerListResponse.CustomerData item
                                                : data.getCustomers()
                                        ) {

                                            Customer customer =
                                                    new Customer(
                                                            item.getCustomerId(),
                                                            item.getFirstName(),
                                                            item.getLastName(),
                                                            item.getEmail(),
                                                            item.getContactNumber(),
                                                            buildAddress(item),
                                                            ""
                                                    );

                                            customer.setStreet(
                                                    item.getStreet()
                                            );

                                            customer.setBarangay(
                                                    item.getBarangay()
                                            );

                                            customer.setCity(
                                                    item.getCity()
                                            );

                                            customer.setPostalCode(
                                                    item.getPostalCode()
                                            );

                                            allCustomers.add(
                                                    customer
                                            );
                                        }
                                    }

                                    filterCustomers(
                                            binding.etSearch
                                                    .getText()
                                                    .toString()
                                    );

                                } else {

                                    Toast.makeText(
                                            AdminCustomersActivity.this,
                                            "Failed to load customers.",
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            }

                            @Override
                            public void onFailure(
                                    @NonNull Call<CustomerListResponse> call,
                                    @NonNull Throwable t) {

                                Toast.makeText(
                                        AdminCustomersActivity.this,
                                        "Connection error: "
                                                + t.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    private String buildAddress(
            CustomerListResponse.CustomerData item) {

        List<String> parts =
                new ArrayList<>();

        addAddressPart(
                parts,
                item.getStreet()
        );

        addAddressPart(
                parts,
                item.getBarangay()
        );

        addAddressPart(
                parts,
                item.getCity()
        );

        addAddressPart(
                parts,
                item.getPostalCode()
        );

        return android.text.TextUtils.join(
                ", ",
                parts
        );
    }

    private void addAddressPart(
            List<String> parts,
            String value) {

        if (value != null
                && !value.trim().isEmpty()) {

            parts.add(value.trim());
        }
    }

    private void filterCustomers(
            String query) {

        String search =
                query == null
                        ? ""
                        : query.trim().toLowerCase();

        List<Customer> filtered =
                new ArrayList<>();

        for (Customer customer :
                allCustomers) {

            if (
                    customer
                            .getFullName()
                            .toLowerCase()
                            .contains(search)
                            || customer
                            .getEmail()
                            .toLowerCase()
                            .contains(search)
            ) {

                filtered.add(customer);
            }
        }

        adapter.updateList(filtered);
    }

    private void showCustomerDetail(
            Customer customer) {

        String details =
                "Name: "
                        + customer.getFullName()
                        + "\n\n"
                        + "Email: "
                        + customer.getEmail()
                        + "\n\n"
                        + "Contact: "
                        + safeValue(
                        customer.getContactNumber()
                )
                        + "\n\n"
                        + "Address: "
                        + safeValue(
                        customer.getAddress()
                );

        new AlertDialog.Builder(this)
                .setTitle("Customer Details")
                .setMessage(details)
                .setPositiveButton(
                        "Close",
                        null
                )
                .show();
    }

    private String safeValue(
            String value) {

        if (value == null
                || value.trim().isEmpty()) {

            return "Not provided";
        }

        return value;
    }
}