package com.fourrmtech.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fourrmtech.app.R;
import com.fourrmtech.app.databinding.ActivityRepairServicesBinding;

public class RepairServicesActivity extends AppCompatActivity {

    private ActivityRepairServicesBinding binding;

    private static final String[][] SERVICES = {
            {"Smartphone Repair", "Screen, battery, charging port, water damage, software issues", "Smartphone"},
            {"Laptop Repair", "Screen, keyboard, battery, motherboard, cooling system, OS reinstall", "Laptop"},
            {"Desktop / PC Repair", "Hardware upgrades, virus removal, OS repair, power supply, GPU issues", "Desktop/PC"},
            {"Tablet Repair", "iPad and Android tablet screen, charging port, and software repair", "Tablet"},
            {"Other Gadget Repair", "Smartwatches, earbuds, gaming consoles, cameras, and other electronics", "Other"},
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRepairServicesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        LayoutInflater inflater = LayoutInflater.from(this);
        for (String[] service : SERVICES) {
            View card = inflater.inflate(R.layout.item_repair_service, binding.servicesContainer, false);
            ((TextView) card.findViewById(R.id.tvServiceName)).setText(service[0]);
            ((TextView) card.findViewById(R.id.tvServiceDesc)).setText(service[1]);

            String deviceType = service[2];
            ((Button) card.findViewById(R.id.btnBook)).setOnClickListener(v -> {
                Intent intent = new Intent(this, BookAppointmentActivity.class);
                intent.putExtra("device_type", deviceType);
                startActivity(intent);
            });

            binding.servicesContainer.addView(card);
        }
    }
}
