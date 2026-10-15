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

import com.fourrmtech.app.activities.LoginActivity;
import com.fourrmtech.app.api.ApiClient;
import com.fourrmtech.app.api.ApiService;
import com.fourrmtech.app.databinding.FragmentProfileBinding;
import com.fourrmtech.app.models.CreateAppointmentResponse;
import com.fourrmtech.app.models.Customer;
import com.fourrmtech.app.models.UpdateCustomerRequest;
import com.fourrmtech.app.utils.DataManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(
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

        Customer user =
                DataManager.getInstance().getCurrentUser();

        // Display customer information
        if (user != null) {

            displayCustomer(user);

            fillEditFields(user);
        }

        // Show Edit Profile form
        binding.btnEditProfile.setOnClickListener(v -> {

            binding.cardEditProfile.setVisibility(
                    View.VISIBLE
            );

            binding.btnEditProfile.setVisibility(
                    View.GONE
            );
        });

        // Cancel editing
        binding.btnCancelEdit.setOnClickListener(v -> {

            Customer currentUser =
                    DataManager.getInstance().getCurrentUser();

            if (currentUser != null) {
                fillEditFields(currentUser);
            }

            binding.cardEditProfile.setVisibility(
                    View.GONE
            );

            binding.btnEditProfile.setVisibility(
                    View.VISIBLE
            );
        });

        // Save changes
        binding.btnSaveProfile.setOnClickListener(v ->
                saveProfile()
        );

        // Logout
        binding.btnLogout.setOnClickListener(v -> {

            DataManager.getInstance().logout();

            Intent intent =
                    new Intent(
                            requireContext(),
                            LoginActivity.class
                    );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
        });
    }

    private void displayCustomer(Customer user) {

        binding.tvInitials.setText(
                user.getInitials()
        );

        binding.tvFullName.setText(
                user.getFullName()
        );

        binding.tvEmail.setText(
                user.getEmail()
        );

        binding.tvFirstNameValue.setText(
                user.getFirstName()
        );

        binding.tvLastNameValue.setText(
                user.getLastName()
        );

        binding.tvContactValue.setText(
                user.getContactNumber()
        );

        binding.tvEmailValue.setText(
                user.getEmail()
        );

        binding.tvAddressValue.setText(
                user.getAddress()
        );
    }

    private void fillEditFields(Customer user) {

        binding.etEditFirstName.setText(
                user.getFirstName()
        );

        binding.etEditLastName.setText(
                user.getLastName()
        );

        binding.etEditContact.setText(
                user.getContactNumber()
        );

        binding.etEditStreet.setText(
                user.getStreet()
        );

        binding.etEditBarangay.setText(
                user.getBarangay()
        );

        binding.etEditCity.setText(
                user.getCity()
        );

        binding.etEditPostalCode.setText(
                user.getPostalCode()
        );
    }

    private void saveProfile() {

        Customer user =
                DataManager.getInstance().getCurrentUser();

        if (user == null) {

            Toast.makeText(
                    requireContext(),
                    "No customer is currently logged in.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String firstName =
                binding.etEditFirstName
                        .getText()
                        .toString()
                        .trim();

        String lastName =
                binding.etEditLastName
                        .getText()
                        .toString()
                        .trim();

        String contactNumber =
                binding.etEditContact
                        .getText()
                        .toString()
                        .trim();

        String street =
                binding.etEditStreet
                        .getText()
                        .toString()
                        .trim();

        String barangay =
                binding.etEditBarangay
                        .getText()
                        .toString()
                        .trim();

        String city =
                binding.etEditCity
                        .getText()
                        .toString()
                        .trim();

        String postalCode =
                binding.etEditPostalCode
                        .getText()
                        .toString()
                        .trim();

        // Validate required fields
        if (firstName.isEmpty()) {

            binding.etEditFirstName.setError(
                    "First name is required."
            );

            binding.etEditFirstName.requestFocus();

            return;
        }

        if (lastName.isEmpty()) {

            binding.etEditLastName.setError(
                    "Last name is required."
            );

            binding.etEditLastName.requestFocus();

            return;
        }

        UpdateCustomerRequest request =
                new UpdateCustomerRequest(
                        user.getId(),
                        firstName,
                        lastName,
                        contactNumber,
                        street,
                        barangay,
                        city,
                        postalCode
                );

        ApiService apiService =
                ApiClient
                        .getClient()
                        .create(ApiService.class);

        binding.btnSaveProfile.setEnabled(false);

        apiService
                .updateCustomer(request)
                .enqueue(new Callback<CreateAppointmentResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<CreateAppointmentResponse> call,
                            @NonNull Response<CreateAppointmentResponse> response) {

                        binding.btnSaveProfile.setEnabled(true);

                        if (!response.isSuccessful()
                                || response.body() == null) {

                            Toast.makeText(
                                    requireContext(),
                                    "Failed to update profile.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        CreateAppointmentResponse result =
                                response.body();

                        if (result.isSuccess()) {

                            // Update local Customer object
                            user.setFirstName(firstName);
                            user.setLastName(lastName);
                            user.setContactNumber(contactNumber);
                            user.setStreet(street);
                            user.setBarangay(barangay);
                            user.setCity(city);
                            user.setPostalCode(postalCode);

                            // Rebuild combined address
                            StringBuilder address =
                                    new StringBuilder();

                            if (!street.isEmpty()) {
                                address.append(street);
                            }

                            if (!barangay.isEmpty()) {

                                if (address.length() > 0) {
                                    address.append(", ");
                                }

                                address.append(barangay);
                            }

                            if (!city.isEmpty()) {

                                if (address.length() > 0) {
                                    address.append(", ");
                                }

                                address.append(city);
                            }

                            if (!postalCode.isEmpty()) {

                                if (address.length() > 0) {
                                    address.append(", ");
                                }

                                address.append(postalCode);
                            }

                            user.setAddress(
                                    address.toString()
                            );

                            // Refresh profile display
                            displayCustomer(user);

                            // Hide edit form
                            binding.cardEditProfile
                                    .setVisibility(View.GONE);

                            binding.btnEditProfile
                                    .setVisibility(View.VISIBLE);

                            Toast.makeText(
                                    requireContext(),
                                    "Profile updated successfully.",
                                    Toast.LENGTH_SHORT
                            ).show();

                        } else {

                            Toast.makeText(
                                    requireContext(),
                                    result.getMessage(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<CreateAppointmentResponse> call,
                            @NonNull Throwable t) {

                        binding.btnSaveProfile.setEnabled(true);

                        Toast.makeText(
                                requireContext(),
                                "Unable to connect to the server.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}