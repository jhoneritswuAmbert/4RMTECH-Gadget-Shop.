package com.fourrmtech.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fourrmtech.app.activities.RepairServicesActivity;
import com.fourrmtech.app.activities.ProductDetailActivity;
import com.fourrmtech.app.databinding.FragmentHomeBinding;
import com.fourrmtech.app.utils.DataManager;
import com.fourrmtech.app.models.Customer;
import com.fourrmtech.app.activities.CustomerInquiryActivity;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Customer user = DataManager.getInstance().getCurrentUser();
        binding.tvUserName.setText(user.getFullName());

        binding.btnNotification.setOnClickListener(v -> {
            androidx.appcompat.app.AlertDialog info = new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("About 4RMTECH")
                    .setMessage("Browse available gadgets, book a repair, and track your appointments."
                            + "\n\nProduct purchases are made at the shop. Bring your device to the shop for repair assessment."
                            + "\n\nWhen your device is Ready to Claim, open Bookings to view the repair summary and total."
                            + "\n\nHave a question? Open Inquiries & FAQs to find answers or send a message.")
                    .setPositiveButton("Close", null)
                    .setNeutralButton("Inquiries & FAQs", (dialog, which) ->
                            startActivity(new Intent(requireContext(), CustomerInquiryActivity.class)))
                    .create();
            info.show();
            info.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(android.graphics.Color.parseColor("#0B1748"));
            info.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEUTRAL).setTextColor(android.graphics.Color.parseColor("#0B1748"));
        });

        // Feature card clicks
        binding.cardProducts.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(com.fourrmtech.app.R.id.fragmentContainer, new ProductsFragment())
                        .commit());

        binding.cardRepair.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), RepairServicesActivity.class)));

        binding.cardAppointments.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(com.fourrmtech.app.R.id.fragmentContainer, new BookingsFragment())
                        .commit());

        binding.cardProfile.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(com.fourrmtech.app.R.id.fragmentContainer, new ProfileFragment())
                        .commit());

        binding.btnViewAll.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(com.fourrmtech.app.R.id.fragmentContainer, new BookingsFragment())
                        .commit());

        binding.btnShopNow.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(com.fourrmtech.app.R.id.fragmentContainer, new ProductsFragment())
                        .commit());

        binding.cardInquiry.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), CustomerInquiryActivity.class)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
