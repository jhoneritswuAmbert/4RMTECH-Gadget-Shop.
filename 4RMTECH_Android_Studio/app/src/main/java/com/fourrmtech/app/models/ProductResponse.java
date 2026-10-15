package com.fourrmtech.app.models;

import java.util.List;

public class ProductResponse {

    private boolean success;
    private List<Product> products;

    public boolean isSuccess() {
        return success;
    }

    public List<Product> getProducts() {
        return products;
    }
}