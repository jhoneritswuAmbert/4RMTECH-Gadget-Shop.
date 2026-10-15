package com.fourrmtech.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fourrmtech.app.activities.ProductDetailActivity;
import com.fourrmtech.app.adapters.ProductAdapter;
import com.fourrmtech.app.api.ApiClient;
import com.fourrmtech.app.api.ApiService;
import com.fourrmtech.app.databinding.FragmentProductsBinding;
import com.fourrmtech.app.models.Product;
import com.fourrmtech.app.models.ProductResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsFragment extends Fragment
        implements ProductAdapter.OnProductClickListener {

    private FragmentProductsBinding binding;
    private ProductAdapter adapter;

    private String selectedCategory = "All";

    // Products received from the PHP API
    private List<Product> allProducts = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentProductsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        // Set up RecyclerView
        adapter = new ProductAdapter(
                getContext(),
                allProducts,
                this
        );

        // One product per row
        binding.rvProducts.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        binding.rvProducts.setAdapter(adapter);

        // Category chip listeners
        binding.chipAll.setOnClickListener(
                v -> filterByCategory("All")
        );

        binding.chipSmartphones.setOnClickListener(
                v -> filterByCategory("Smartphones")
        );

        binding.chipLaptops.setOnClickListener(
                v -> filterByCategory("Laptops")
        );

        binding.chipAccessories.setOnClickListener(
                v -> filterByCategory("Accessories")
        );

        binding.chipParts.setOnClickListener(
                v -> filterByCategory("Computer Parts")
        );

        // Search
        binding.etSearch.addTextChangedListener(new TextWatcher() {

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
            public void afterTextChanged(Editable s) {
            }
        });

        // Load products from PHP API
        loadProducts();
    }

    private void loadProducts() {

        ApiService apiService =
                ApiClient.getClient().create(ApiService.class);

        Call<ProductResponse> call =
                apiService.getProducts();

        call.enqueue(new Callback<ProductResponse>() {

            @Override
            public void onResponse(
                    @NonNull Call<ProductResponse> call,
                    @NonNull Response<ProductResponse> response) {

                if (!isAdded() || binding == null) {
                    return;
                }

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().isSuccess()) {

                    allProducts = response.body().getProducts();

                    if (allProducts == null) {
                        allProducts = new ArrayList<>();
                    }

                    // Display API products
                    filterProducts(
                            binding.etSearch.getText().toString()
                    );

                } else {

                    Toast.makeText(
                            requireContext(),
                            "Failed to load products.",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<ProductResponse> call,
                    @NonNull Throwable t) {

                if (!isAdded() || binding == null) {
                    return;
                }

                Toast.makeText(
                        requireContext(),
                        "Unable to connect to the server.",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void filterByCategory(String category) {

        selectedCategory = category;

        filterProducts(
                binding.etSearch.getText().toString()
        );
    }

    private void filterProducts(String query) {

        List<Product> filtered =
                new ArrayList<>();

        String searchQuery =
                query.toLowerCase(Locale.ROOT);

        for (Product p : allProducts) {

            boolean matchCategory =
                    selectedCategory.equals("All")
                            || p.getCategory().equals(selectedCategory);

            boolean matchSearch =
                    p.getName()
                            .toLowerCase(Locale.ROOT)
                            .contains(searchQuery)
                            ||
                            p.getBrand()
                                    .toLowerCase(Locale.ROOT)
                                    .contains(searchQuery);

            if (matchCategory && matchSearch) {
                filtered.add(p);
            }
        }

        adapter.updateList(filtered);
    }

    @Override
    public void onProductClick(Product product) {

        Intent intent = new Intent(
                getContext(),
                ProductDetailActivity.class
        );

        intent.putExtra(
                "product_id",
                product.getId()
        );

        startActivity(intent);
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();

        binding = null;
    }
}