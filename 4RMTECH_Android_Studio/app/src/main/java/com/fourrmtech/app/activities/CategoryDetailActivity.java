package com.fourrmtech.app.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fourrmtech.app.R;
import com.fourrmtech.app.api.ApiClient;
import com.fourrmtech.app.api.ApiService;
import com.fourrmtech.app.models.CreateAppointmentResponse;
import com.fourrmtech.app.models.DeleteCategoryRequest;
import com.fourrmtech.app.models.UpdateCategoryRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryDetailActivity extends AppCompatActivity {

    private int categoryId;
    private String categoryName;
    private String categoryDescription;
    private int productCount;

    private EditText etCategoryName;
    private EditText etCategoryDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_category_detail);

        Button btnBack =
                findViewById(R.id.btnBack);

        Button btnSaveCategory =
                findViewById(R.id.btnSaveCategory);

        Button btnDeleteCategory =
                findViewById(R.id.btnDeleteCategory);

        TextView tvCategoryName =
                findViewById(R.id.tvCategoryName);

        TextView tvCategoryId =
                findViewById(R.id.tvCategoryId);

        TextView tvCategoryDescription =
                findViewById(R.id.tvCategoryDescription);

        TextView tvProductCount =
                findViewById(R.id.tvProductCount);

        etCategoryName =
                findViewById(R.id.etCategoryName);

        etCategoryDescription =
                findViewById(R.id.etCategoryDescription);

        categoryName =
                getIntent().getStringExtra("category_name");

        categoryDescription =
                getIntent().getStringExtra("category_description");

        categoryId =
                getIntent().getIntExtra("category_id", -1);

        productCount =
                getIntent().getIntExtra("product_count", 0);

        tvCategoryName.setText(categoryName);

        tvCategoryId.setText(
                "Category ID: " + categoryId
        );

        tvCategoryDescription.setText(
                categoryDescription
        );

        tvProductCount.setText(
                productCount + " Products"
        );

        // Put current values into the edit fields
        etCategoryName.setText(categoryName);

        etCategoryDescription.setText(
                categoryDescription
        );

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Save button
        btnSaveCategory.setOnClickListener(v -> {

            String newName =
                    etCategoryName.getText()
                            .toString()
                            .trim();

            String newDescription =
                    etCategoryDescription.getText()
                            .toString()
                            .trim();

            if (newName.isEmpty()) {

                etCategoryName.setError(
                        "Category name is required."
                );

                etCategoryName.requestFocus();

                return;
            }

            if (newDescription.isEmpty()) {

                etCategoryDescription.setError(
                        "Category description is required."
                );

                etCategoryDescription.requestFocus();

                return;
            }

            // Create API request
            UpdateCategoryRequest request =
                    new UpdateCategoryRequest(
                            categoryId,
                            newName,
                            newDescription
                    );

            // Get API service
            ApiService apiService =
                    ApiClient.getClient()
                            .create(ApiService.class);

            // Send update to PostgreSQL
            apiService.updateCategory(request)
                    .enqueue(new Callback<CreateAppointmentResponse>() {

                        @Override
                        public void onResponse(
                                Call<CreateAppointmentResponse> call,
                                Response<CreateAppointmentResponse> response) {

                            if (response.isSuccessful()
                                    && response.body() != null) {

                                // Update displayed information
                                tvCategoryName.setText(newName);

                                tvCategoryDescription.setText(
                                        newDescription
                                );

                                // Update local variables
                                categoryName = newName;
                                categoryDescription = newDescription;

                                // Also update local DataManager
                                com.fourrmtech.app.utils.DataManager
                                        .getInstance()
                                        .updateCategory(
                                                categoryId,
                                                newName,
                                                newDescription
                                        );

                                Toast.makeText(
                                        CategoryDetailActivity.this,
                                        "Category updated successfully.",
                                        Toast.LENGTH_SHORT
                                ).show();

                            } else {

                                Toast.makeText(
                                        CategoryDetailActivity.this,
                                        "Failed to update category.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }

                        @Override
                        public void onFailure(
                                Call<CreateAppointmentResponse> call,
                                Throwable t) {

                            Toast.makeText(
                                    CategoryDetailActivity.this,
                                    "Connection failed: "
                                            + t.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
        });

        // Delete button
        btnDeleteCategory.setOnClickListener(v -> {

            new AlertDialog.Builder(
                    CategoryDetailActivity.this
            )
                    .setTitle("Delete Category")
                    .setMessage(
                            "Are you sure you want to delete \""
                                    + categoryName
                                    + "\"?"
                    )
                    .setNegativeButton(
                            "Cancel",
                            null
                    )
                    .setPositiveButton(
                            "Delete",
                            (dialog, which) -> {

                                deleteCategory();

                            }
                    )
                    .show();
        });
    }

    private void deleteCategory() {

        DeleteCategoryRequest request =
                new DeleteCategoryRequest(categoryId);

        ApiService apiService =
                ApiClient.getClient()
                        .create(ApiService.class);

        apiService.deleteCategory(request)
                .enqueue(new Callback<CreateAppointmentResponse>() {

                    @Override
                    public void onResponse(
                            Call<CreateAppointmentResponse> call,
                            Response<CreateAppointmentResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            Toast.makeText(
                                    CategoryDetailActivity.this,
                                    "Category deleted successfully.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            boolean deleted =
                                    com.fourrmtech.app.utils.DataManager
                                            .getInstance()
                                            .deleteCategory(categoryId);

                            Toast.makeText(
                                    CategoryDetailActivity.this,
                                    "Local delete result: " + deleted,
                                    Toast.LENGTH_LONG
                            ).show();

// Remove the category from the local Android list
                            com.fourrmtech.app.utils.DataManager
                                    .getInstance()
                                    .deleteCategory(categoryId);

// Return to Categories screen
                            finish();

                        } else {

                            Toast.makeText(
                                    CategoryDetailActivity.this,
                                    "Category could not be deleted. "
                                            + "It may have products assigned to it.",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<CreateAppointmentResponse> call,
                            Throwable t) {

                        Toast.makeText(
                                CategoryDetailActivity.this,
                                "Connection failed: "
                                        + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}