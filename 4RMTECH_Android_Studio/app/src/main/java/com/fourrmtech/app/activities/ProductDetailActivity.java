package com.fourrmtech.app.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.fourrmtech.app.R;
import com.fourrmtech.app.api.ApiClient;
import com.fourrmtech.app.api.ApiService;
import com.fourrmtech.app.databinding.ActivityProductDetailBinding;
import com.fourrmtech.app.models.Product;
import com.fourrmtech.app.models.ProductSingleResponse;
import com.fourrmtech.app.models.CreateAppointmentResponse;
import com.fourrmtech.app.models.DeleteProductRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private ActivityProductDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        int productId = getIntent().getIntExtra("product_id", -1);

        boolean isAdmin =
                getIntent().getBooleanExtra("is_admin", false);

        if (isAdmin) {

            binding.btnEditProduct.setVisibility(
                    android.view.View.VISIBLE
            );

            binding.btnDeleteProduct.setVisibility(
                    android.view.View.VISIBLE
            );

            binding.btnEditProduct.setOnClickListener(v -> {

                Intent intent = new Intent(
                        ProductDetailActivity.this,
                        EditProductActivity.class
                );

                intent.putExtra(
                        "product_id",
                        productId
                );

                startActivity(intent);
            });

            binding.btnDeleteProduct.setOnClickListener(v ->
                    confirmDeleteProduct(productId)
            );
        }

        if (productId == -1) {
            Toast.makeText(
                    this,
                    "Invalid product.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        loadProduct(productId);
    }

    private void loadProduct(int productId) {

        ApiService apiService =
                ApiClient.getClient().create(ApiService.class);

        Call<ProductSingleResponse> call =
                apiService.getProductById(productId);

        call.enqueue(new Callback<ProductSingleResponse>() {

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

                    displayProduct(product);

                } else {

                    Toast.makeText(
                            ProductDetailActivity.this,
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
                        ProductDetailActivity.this,
                        "Unable to connect to the server.",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void displayProduct(Product product) {

        Glide.with(this)
                .load(product.getImageUrl())
                .centerCrop()
                .into(binding.ivProduct);

        binding.tvBrand.setText(product.getBrand());
        binding.tvName.setText(product.getName());
        binding.tvCategory.setText(product.getCategory());
        binding.tvPrice.setText(product.getFormattedPrice());
        binding.tvDescription.setText(product.getDescription());
        binding.tvStockCount.setText(
                "(" + product.getStock() + " units)"
        );

        String stockStatus = product.getStockStatus();

        binding.tvStock.setText(stockStatus);

        switch (stockStatus) {

            case "In Stock":
                binding.tvStock.setTextColor(
                        Color.parseColor("#10B981")
                );

                binding.tvStock.setBackgroundResource(
                        R.drawable.bg_status_approved
                );
                break;

            case "Low Stock":
                binding.tvStock.setTextColor(
                        Color.parseColor("#F59E0B")
                );

                binding.tvStock.setBackgroundResource(
                        R.drawable.bg_status_pending
                );
                break;

            default:
                binding.tvStock.setTextColor(
                        Color.parseColor("#EF4444")
                );

                binding.tvStock.setBackgroundResource(
                        R.drawable.bg_status_cancelled
                );
                break;
        }

        binding.btnBookRepair.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                ProductDetailActivity.this,
                                RepairServicesActivity.class
                        )
                )
        );
    }

    private void confirmDeleteProduct(int productId) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Product")
                .setMessage(
                        "Are you sure you want to delete this product?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) ->
                                deleteProduct(productId)
                )
                .show();
    }

    private void deleteProduct(int productId) {

        ApiService apiService =
                ApiClient
                        .getClient()
                        .create(ApiService.class);

        DeleteProductRequest request =
                new DeleteProductRequest(productId);

        binding.btnDeleteProduct.setEnabled(false);

        apiService.deleteProduct(request)
                .enqueue(new Callback<CreateAppointmentResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<CreateAppointmentResponse> call,
                            @NonNull Response<CreateAppointmentResponse> response) {

                        binding.btnDeleteProduct.setEnabled(true);

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            Toast.makeText(
                                    ProductDetailActivity.this,
                                    "Product deleted successfully.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            finish();

                        } else {

                            Toast.makeText(
                                    ProductDetailActivity.this,
                                    "Unable to delete product.",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<CreateAppointmentResponse> call,
                            @NonNull Throwable t) {

                        binding.btnDeleteProduct.setEnabled(true);

                        Toast.makeText(
                                ProductDetailActivity.this,
                                "Connection error: "
                                        + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}