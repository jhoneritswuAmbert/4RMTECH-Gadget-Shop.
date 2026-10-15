package com.fourrmtech.app.models;

import com.google.gson.annotations.SerializedName;

public class Product {

    @SerializedName("product_id")
    private int id;

    @SerializedName("product_name")
    private String name;

    @SerializedName("brand")
    private String brand;

    @SerializedName("category_id")
    private int categoryId;

    @SerializedName("category_name")
    private String category;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private double price;

    @SerializedName("stock_quantity")
    private int stock;

    @SerializedName("image_url")
    private String imageUrl;

    public Product(
            int id,
            String name,
            String brand,
            int categoryId,
            String category,
            String description,
            double price,
            int stock,
            String imageUrl) {

        this.id = id;
        this.name = name;
        this.brand = brand;
        this.categoryId = categoryId;
        this.category = category;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.imageUrl = imageUrl;
    }

    public Product(
            int id,
            String name,
            String brand,
            String category,
            String description,
            double price,
            int stock,
            String imageUrl) {

        this.id = id;
        this.name = name;
        this.brand = brand;
        this.category = category;

        if ("Smartphones".equalsIgnoreCase(category)) {
            this.categoryId = 1;
        } else if ("Laptops".equalsIgnoreCase(category)) {
            this.categoryId = 2;
        } else if ("Accessories".equalsIgnoreCase(category)) {
            this.categoryId = 3;
        } else if ("Computer Parts".equalsIgnoreCase(category)) {
            this.categoryId = 4;
        } else {
            this.categoryId = 0;
        }

        this.description = description;
        this.price = price;
        this.stock = stock;
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getStockStatus() {
        if (stock == 0) {
            return "Out of Stock";
        }

        if (stock <= 5) {
            return "Low Stock";
        }

        return "In Stock";
    }

    public String getFormattedPrice() {
        return "₱" + String.format("%,.0f", price);
    }
}