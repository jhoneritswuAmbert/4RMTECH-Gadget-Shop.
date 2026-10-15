package com.fourrmtech.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fourrmtech.app.R;
import com.fourrmtech.app.databinding.ActivityAdminCategoriesBinding;
import com.fourrmtech.app.models.Category;
import com.fourrmtech.app.utils.DataManager;

import android.widget.Toast;

import com.fourrmtech.app.api.ApiClient;
import com.fourrmtech.app.api.ApiService;
import com.fourrmtech.app.models.CategoryResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCategoriesActivity extends AppCompatActivity {

    private ActivityAdminCategoriesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminCategoriesBinding.inflate(
                getLayoutInflater()
        );

        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        binding.fabAddCategory.setOnClickListener(v -> {

            Intent intent = new Intent(
                    AdminCategoriesActivity.this,
                    AddCategoryActivity.class
            );

            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadCategories();
    }

    private void loadCategories() {

        ApiService apiService =
                ApiClient.getClient()
                        .create(ApiService.class);

        apiService.getCategories()
                .enqueue(new Callback<CategoryResponse>() {

                    @Override
                    public void onResponse(
                            Call<CategoryResponse> call,
                            Response<CategoryResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            binding.categoriesContainer
                                    .removeAllViews();

                            LayoutInflater inflater =
                                    LayoutInflater.from(
                                            AdminCategoriesActivity.this
                                    );

                            for (Category cat :
                                    response.body().getCategories()) {

                                View card = inflater.inflate(
                                        R.layout.item_category_card,
                                        binding.categoriesContainer,
                                        false
                                );

                                TextView tvCategoryName =
                                        card.findViewById(
                                                R.id.tvCategoryName
                                        );

                                TextView tvCategoryDesc =
                                        card.findViewById(
                                                R.id.tvCategoryDesc
                                        );

                                TextView tvProductCount =
                                        card.findViewById(
                                                R.id.tvProductCount
                                        );

                                ImageView ivCategory =
                                        card.findViewById(
                                                R.id.ivCategory
                                        );

                                tvCategoryName.setText(
                                        cat.getName()
                                );

                                tvCategoryDesc.setText(
                                        cat.getDescription()
                                );

                                tvProductCount.setText(
                                        cat.getProductCount()
                                                + " Products"
                                );

                                // Use the appropriate local image
                                if (cat.getId() == 1) {
                                    ivCategory.setImageResource(
                                            R.drawable.category_smartphones
                                    );
                                } else if (cat.getId() == 2) {
                                    ivCategory.setImageResource(
                                            R.drawable.category_laptops
                                    );
                                } else if (cat.getId() == 3) {
                                    ivCategory.setImageResource(
                                            R.drawable.category_accessories
                                    );
                                } else {
                                    ivCategory.setImageResource(
                                            R.drawable.category_computer_parts
                                    );
                                }

                                card.setOnClickListener(v -> {

                                    Intent intent =
                                            new Intent(
                                                    AdminCategoriesActivity.this,
                                                    CategoryDetailActivity.class
                                            );

                                    intent.putExtra(
                                            "category_id",
                                            cat.getId()
                                    );

                                    intent.putExtra(
                                            "category_name",
                                            cat.getName()
                                    );

                                    intent.putExtra(
                                            "category_description",
                                            cat.getDescription()
                                    );

                                    intent.putExtra(
                                            "product_count",
                                            cat.getProductCount()
                                    );

                                    startActivity(intent);
                                });

                                binding.categoriesContainer
                                        .addView(card);
                            }

                        } else {

                            Toast.makeText(
                                    AdminCategoriesActivity.this,
                                    "Failed to load categories.",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<CategoryResponse> call,
                            Throwable t) {

                        Toast.makeText(
                                AdminCategoriesActivity.this,
                                "Connection failed: "
                                        + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

}