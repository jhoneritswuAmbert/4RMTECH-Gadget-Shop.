package com.fourrmtech.app.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fourrmtech.app.api.ApiClient;
import com.fourrmtech.app.api.ApiService;
import com.fourrmtech.app.databinding.ActivityEditProductBinding;
import com.fourrmtech.app.models.Product;
import com.fourrmtech.app.models.ProductSingleResponse;
import com.fourrmtech.app.models.UpdateProductRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProductActivity extends AppCompatActivity {

    private ActivityEditProductBinding binding;

    private int productId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditProductBinding.inflate(
                getLayoutInflater()
        );

        setContentView(binding.getRoot());

        productId = getIntent().getIntExtra(
                "product_id",
                -1
        );

        if (productId == -1) {

            Toast.makeText(
                    this,
                    "Invalid product.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        binding.btnBack.setOnClickListener(v ->
                finish()
        );

        binding.btnSaveProduct.setOnClickListener(v ->
                updateProduct()
        );

        loadProduct();
    }

    private void loadProduct() {

        ApiService apiService =
                ApiClient
                        .getClient()
                        .create(ApiService.class);

        apiService.getProductById(productId)
                .enqueue(new Callback<ProductSingleResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<ProductSingleResponse> call,
                            @NonNull Response<ProductSingleResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()
                                && response.body().getProduct() != null) {

                            Product product =
                                    response.body().getProduct();

                            binding.etProductName.setText(
                                    product.getName()
                            );

                            binding.etBrand.setText(
                                    product.getBrand()
                            );

                            binding.etDescription.setText(
                                    product.getDescription()
                            );

                            binding.etPrice.setText(
                                    String.valueOf(product.getPrice())
                            );

                            binding.etStock.setText(
                                    String.valueOf(product.getStock())
                            );

                            binding.etCategoryId.setText(
                                    String.valueOf(product.getCategoryId())
                            );

                            binding.etImageUrl.setText(
                                    product.getImageUrl()
                            );

                        } else {

                            Toast.makeText(
                                    EditProductActivity.this,
                                    "Product not found.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            finish();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<ProductSingleResponse> call,
                            @NonNull Throwable t) {

                        Toast.makeText(
                                EditProductActivity.this,
                                "Unable to load product.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void updateProduct() {

        String productName =
                binding.etProductName
                        .getText()
                        .toString()
                        .trim();

        String brand =
                binding.etBrand
                        .getText()
                        .toString()
                        .trim();

        String description =
                binding.etDescription
                        .getText()
                        .toString()
                        .trim();

        String priceText =
                binding.etPrice
                        .getText()
                        .toString()
                        .trim();

        String stockText =
                binding.etStock
                        .getText()
                        .toString()
                        .trim();

        String categoryText =
                binding.etCategoryId
                        .getText()
                        .toString()
                        .trim();

        String imageUrl =
                binding.etImageUrl
                        .getText()
                        .toString()
                        .trim();

        if (productName.isEmpty()
                || brand.isEmpty()
                || description.isEmpty()
                || priceText.isEmpty()
                || stockText.isEmpty()
                || categoryText.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please complete all required fields.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        double price;
        int stock;
        int categoryId;

        try {

            price = Double.parseDouble(priceText);
            stock = Integer.parseInt(stockText);
            categoryId = Integer.parseInt(categoryText);

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Please enter valid numbers.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (price < 0) {

            Toast.makeText(
                    this,
                    "Price cannot be negative.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (stock < 0) {

            Toast.makeText(
                    this,
                    "Stock cannot be negative.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (categoryId <= 0) {

            Toast.makeText(
                    this,
                    "Invalid category ID.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        UpdateProductRequest request =
                new UpdateProductRequest(
                        productId,
                        productName,
                        brand,
                        description,
                        price,
                        stock,
                        categoryId,
                        imageUrl
                );

        ApiService apiService =
                ApiClient
                        .getClient()
                        .create(ApiService.class);

        binding.btnSaveProduct.setEnabled(false);

        apiService.updateProduct(request)
                .enqueue(new Callback<ProductSingleResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<ProductSingleResponse> call,
                            @NonNull Response<ProductSingleResponse> response) {

                        binding.btnSaveProduct.setEnabled(true);

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            Toast.makeText(
                                    EditProductActivity.this,
                                    "Product updated successfully.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            finish();

                        } else {

                            Toast.makeText(
                                    EditProductActivity.this,
                                    "Failed to update product.",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<ProductSingleResponse> call,
                            @NonNull Throwable t) {

                        binding.btnSaveProduct.setEnabled(true);

                        Toast.makeText(
                                EditProductActivity.this,
                                "Connection error: "
                                        + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}