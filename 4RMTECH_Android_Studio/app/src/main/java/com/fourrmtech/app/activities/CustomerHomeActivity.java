package com.fourrmtech.app.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.fourrmtech.app.R;
import com.fourrmtech.app.databinding.ActivityCustomerHomeBinding;
import com.fourrmtech.app.fragments.BookingsFragment;
import com.fourrmtech.app.fragments.HomeFragment;
import com.fourrmtech.app.fragments.ProductsFragment;
import com.fourrmtech.app.fragments.ProfileFragment;

public class CustomerHomeActivity extends AppCompatActivity {

    private ActivityCustomerHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Load default fragment
        loadFragment(new HomeFragment());

        binding.bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selected = new HomeFragment();
            } else if (id == R.id.nav_products) {
                selected = new ProductsFragment();
            } else if (id == R.id.nav_bookings) {
                selected = new BookingsFragment();
            } else if (id == R.id.nav_profile) {
                selected = new ProfileFragment();
            } else {
                selected = new HomeFragment();
            }

            loadFragment(selected);
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
