package com.fourrmtech.app.models;

public class DeleteProductRequest {

    private int product_id;

    public DeleteProductRequest(int product_id) {
        this.product_id = product_id;
    }

    public int getProduct_id() {
        return product_id;
    }
}