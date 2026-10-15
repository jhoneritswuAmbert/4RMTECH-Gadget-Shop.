package com.fourrmtech.app.models;

public class AddProductRequest {

    private String product_name;
    private String brand;
    private String description;
    private double price;
    private int stock_quantity;
    private int category_id;
    private String image_url;

    public AddProductRequest(
            String product_name,
            String brand,
            String description,
            double price,
            int stock_quantity,
            int category_id,
            String image_url) {

        this.product_name = product_name;
        this.brand = brand;
        this.description = description;
        this.price = price;
        this.stock_quantity = stock_quantity;
        this.category_id = category_id;
        this.image_url = image_url;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getBrand() {
        return brand;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getStock_quantity() {
        return stock_quantity;
    }

    public int getCategory_id() {
        return category_id;
    }

    public String getImage_url() {
        return image_url;
    }
}