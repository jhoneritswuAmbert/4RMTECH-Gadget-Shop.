package com.fourrmtech.app.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fourrmtech.app.api.ApiClient;
import com.fourrmtech.app.api.ApiService;
import com.fourrmtech.app.databinding.ActivityAddCategoryBinding;
import com.fourrmtech.app.models.AddCategoryRequest;
import com.fourrmtech.app.models.CategoryResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCategoryActivity extends AppCompatActivity {

    private ActivityAddCategoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddCategoryBinding.inflate(
                getLayoutInflater()
        );

        setContentView(binding.getRoot());

        // Back button
        binding.btnBack.setOnClickListener(v ->
                finish()
        );

        // Add category button
        binding.btnAddCategory.setOnClickListener(v ->
                addCategory()
        );
    }

    private void addCategory() {

        String categoryName =
                binding.etCategoryName
                        .getText()
                        .toString()
                        .trim();

        String description =
                binding.etDescription
                        .getText()
                        .toString()
                        .trim();

        if (categoryName.isEmpty()) {

            Toast.makeText(
                    this,
                    "Category name is required.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        AddCategoryRequest request =
                new AddCategoryRequest(
                        categoryName,
                        description
                );

        ApiService apiService =
                ApiClient
                        .getClient()
                        .create(ApiService.class);

        binding.btnAddCategory.setEnabled(false);

        apiService.addCategory(request)
                .enqueue(new Callback<CategoryResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<CategoryResponse> call,
                            @NonNull Response<CategoryResponse> response) {

                        binding.btnAddCategory.setEnabled(true);

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            Toast.makeText(
                                    AddCategoryActivity.this,
                                    "Category added successfully.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            finish();

                        } else {

                            Toast.makeText(
                                    AddCategoryActivity.this,
                                    "Failed to add category.",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<CategoryResponse> call,
                            @NonNull Throwable t) {

                        binding.btnAddCategory.setEnabled(true);

                        Toast.makeText(
                                AddCategoryActivity.this,
                                "Connection error: "
                                        + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}