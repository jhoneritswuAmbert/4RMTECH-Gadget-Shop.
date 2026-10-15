
package com.fourrmtech.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fourrmtech.app.databinding.ActivityAddProductBinding;
import com.fourrmtech.app.models.AddProductRequest;
import com.fourrmtech.app.models.Category;
import com.fourrmtech.app.models.CategoryResponse;
import com.fourrmtech.app.models.ProductSingleResponse;
import com.fourrmtech.app.api.ApiService;

import com.fourrmtech.app.api.ApiClient;
import com.fourrmtech.app.api.ApiService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProductActivity extends AppCompatActivity {

    private ActivityAddProductBinding binding;
    private ApiService apiService;

    private final List<Category> categories = new ArrayList<>();
    private ArrayAdapter<String> categoryAdapter;

    private boolean categoriesLoaded = false;
    private boolean isSaving = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiService = ApiClient.getClient().create(ApiService.class);

        setupCategorySpinner();
        loadCategories();

        binding.btnSaveProduct.setOnClickListener(v -> saveProduct());

        binding.btnCancel.setOnClickListener(v -> finish());
    }

    private void setupCategorySpinner() {
        categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new ArrayList<>()
        );

        categoryAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        binding.spinnerCategory.setAdapter(categoryAdapter);
    }

    private void loadCategories() {
        binding.spinnerCategory.setEnabled(false);

        apiService.getCategories().enqueue(
                new Callback<CategoryResponse>() {

                    @Override
                    public void onResponse(
                            Call<CategoryResponse> call,
                            Response<CategoryResponse> response) {

                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        CategoryResponse result = response.body();

                        if (!response.isSuccessful()
                                || result == null
                                || !result.isSuccess()
                                || result.getCategories() == null) {

                            categoriesLoaded = false;

                            Toast.makeText(
                                    AddProductActivity.this,
                                    "Unable to load categories. HTTP "
                                            + response.code(),
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        categories.clear();
                        categories.addAll(result.getCategories());

                        List<String> names = new ArrayList<>();
                        names.add("Select a category");

                        for (Category category : categories) {
                            names.add(category.getName());
                        }

                        categoryAdapter.clear();
                        categoryAdapter.addAll(names);
                        categoryAdapter.notifyDataSetChanged();

                        categoriesLoaded = !categories.isEmpty();
                        binding.spinnerCategory.setEnabled(categoriesLoaded);

                        if (!categoriesLoaded) {
                            Toast.makeText(
                                    AddProductActivity.this,
                                    "No categories found. Add a category first.",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<CategoryResponse> call,
                            Throwable t) {

                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        categoriesLoaded = false;
                        binding.spinnerCategory.setEnabled(false);

                        Toast.makeText(
                                AddProductActivity.this,
                                "Failed to load categories: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private void saveProduct() {
        if (isSaving) {
            return;
        }

        if (!categoriesLoaded || categories.isEmpty()) {
            Toast.makeText(
                    this,
                    "Categories are not loaded yet. Please try again.",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        String productName =
                binding.etProductName.getText().toString().trim();

        String brand =
                binding.etBrand.getText().toString().trim();

        String description =
                binding.etDescription.getText().toString().trim();

        String priceText =
                binding.etPrice.getText().toString().trim();

        String stockText =
                binding.etStock.getText().toString().trim();

        String imageUrl =
                binding.etImageUrl.getText().toString().trim();

        if (productName.isEmpty()) {
            binding.etProductName.setError("Product name is required");
            binding.etProductName.requestFocus();
            return;
        }

        if (brand.isEmpty()) {
            binding.etBrand.setError("Brand is required");
            binding.etBrand.requestFocus();
            return;
        }

        if (priceText.isEmpty()) {
            binding.etPrice.setError("Price is required");
            binding.etPrice.requestFocus();
            return;
        }

        if (stockText.isEmpty()) {
            binding.etStock.setError("Stock quantity is required");
            binding.etStock.requestFocus();
            return;
        }

        double price;
        int stock;

        try {
            price = Double.parseDouble(priceText);

            if (!Double.isFinite(price) || price <= 0) {
                binding.etPrice.setError("Enter a price greater than zero");
                binding.etPrice.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            binding.etPrice.setError("Enter a valid price");
            binding.etPrice.requestFocus();
            return;
        }

        try {
            stock = Integer.parseInt(stockText);

            if (stock < 0) {
                binding.etStock.setError("Stock cannot be negative");
                binding.etStock.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            binding.etStock.setError("Enter a valid whole number");
            binding.etStock.requestFocus();
            return;
        }

        int selectedPosition =
                binding.spinnerCategory.getSelectedItemPosition();

        if (selectedPosition <= 0
                || selectedPosition > categories.size()) {

            Toast.makeText(
                    this,
                    "Please select a category.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        int categoryId =
                categories.get(selectedPosition - 1).getId();

        AddProductRequest request = new AddProductRequest(
                productName,
                brand,
                description,
                price,
                stock,
                categoryId,
                imageUrl
        );

        isSaving = true;
        binding.btnSaveProduct.setEnabled(false);

        apiService.addProduct(request).enqueue(
                new Callback<ProductSingleResponse>() {

                    @Override
                    public void onResponse(
                            Call<ProductSingleResponse> call,
                            Response<ProductSingleResponse> response) {

                        isSaving = false;

                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        binding.btnSaveProduct.setEnabled(true);

                        ProductSingleResponse result = response.body();

                        if (response.isSuccessful()
                                && result != null
                                && result.isSuccess()) {

                            Toast.makeText(
                                    AddProductActivity.this,
                                    "Product added successfully!",
                                    Toast.LENGTH_LONG
                            ).show();

                            finish();

                        } else {
                            String errorMessage =
                                    "Failed to add product. HTTP "
                                            + response.code();

                            if (response.errorBody() != null) {
                                try {
                                    String serverError =
                                            response.errorBody().string();

                                    if (!serverError.trim().isEmpty()) {
                                        errorMessage += ": " + serverError;
                                    }
                                } catch (IOException ignored) {
                                    // Keep the HTTP status message.
                                }
                            } else if (result != null) {
                                errorMessage +=
                                        ". Server returned success=false.";
                            }

                            Toast.makeText(
                                    AddProductActivity.this,
                                    errorMessage,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ProductSingleResponse> call,
                            Throwable t) {

                        isSaving = false;

                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        binding.btnSaveProduct.setEnabled(true);

                        Toast.makeText(
                                AddProductActivity.this,
                                "Connection error: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }
}