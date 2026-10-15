package com.fourrmtech.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fourrmtech.app.adapters.ProductAdapter;
import com.fourrmtech.app.api.ApiClient;
import com.fourrmtech.app.api.ApiService;
import com.fourrmtech.app.databinding.ActivityAdminProductsBinding;
import com.fourrmtech.app.models.Product;
import com.fourrmtech.app.models.ProductResponse;

import com.fourrmtech.app.activities.AddProductActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminProductsActivity extends AppCompatActivity
        implements ProductAdapter.OnProductClickListener {

    private ActivityAdminProductsBinding binding;
    private ProductAdapter adapter;

    private List<Product> allProducts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminProductsBinding.inflate(
                getLayoutInflater()
        );

        setContentView(binding.getRoot());

        // Back button
        binding.btnBack.setOnClickListener(v -> finish());

        // Product list
        adapter = new ProductAdapter(
                this,
                new ArrayList<>(),
                this
        );

        binding.rvProducts.setLayoutManager(
                new LinearLayoutManager(this)
        );

        binding.rvProducts.setAdapter(adapter);

        // Search
        binding.etSearch.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        filterProducts(s.toString());
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );

        // Add Product
        binding.fabAddProduct.setOnClickListener(v -> {

            Intent intent = new Intent(
                    AdminProductsActivity.this,
                    AddProductActivity.class
            );

            startActivity(intent);
        });

        // Category filters
        binding.chipAll.setOnClickListener(v ->
                adapter.updateList(allProducts)
        );

        binding.chipSmartphones.setOnClickListener(v ->
                filterByCategory("Smartphones")
        );

        binding.chipLaptops.setOnClickListener(v ->
                filterByCategory("Laptops")
        );

        binding.chipAccessories.setOnClickListener(v ->
                filterByCategory("Accessories")
        );

        binding.chipParts.setOnClickListener(v ->
                filterByCategory("Computer Parts")
        );

        // Load products from PostgreSQL
        loadProducts();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Reload products whenever returning to this screen.
        // This allows newly added products to appear.
        loadProducts();
    }

    private void loadProducts() {

        ApiService apiService =
                ApiClient
                        .getClient()
                        .create(ApiService.class);

        apiService.getProducts()
                .enqueue(new Callback<ProductResponse>() {

                    @Override
                    public void onResponse(
                            Call<ProductResponse> call,
                            Response<ProductResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            List<Product> products =
                                    response.body().getProducts();

                            if (products != null) {

                                allProducts =
                                        new ArrayList<>(products);

                                adapter.updateList(
                                        new ArrayList<>(allProducts)
                                );
                            }

                        } else {

                            Toast.makeText(
                                    AdminProductsActivity.this,
                                    "Failed to load products.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ProductResponse> call,
                            Throwable t) {

                        Toast.makeText(
                                AdminProductsActivity.this,
                                "Connection error: "
                                        + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void filterByCategory(String category) {

        List<Product> filteredProducts =
                new ArrayList<>();

        for (Product product : allProducts) {

            if (product.getCategory() != null
                    && product.getCategory()
                    .equalsIgnoreCase(category)) {

                filteredProducts.add(product);
            }
        }

        adapter.updateList(filteredProducts);
    }

    private void filterProducts(String query) {

        String search =
                query.toLowerCase().trim();

        List<Product> filteredProducts =
                new ArrayList<>();

        for (Product product : allProducts) {

            String name =
                    product.getName() != null
                            ? product.getName().toLowerCase()
                            : "";

            String brand =
                    product.getBrand() != null
                            ? product.getBrand().toLowerCase()
                            : "";

            if (name.contains(search)
                    || brand.contains(search)) {

                filteredProducts.add(product);
            }
        }

        adapter.updateList(filteredProducts);
    }

    @Override
    public void onProductClick(Product product) {

        Intent intent = new Intent(
                AdminProductsActivity.this,
                ProductDetailActivity.class
        );

        intent.putExtra(
                "product_id",
                product.getId()
        );

        intent.putExtra(
                "is_admin",
                true
        );

        startActivity(intent);
    }
}