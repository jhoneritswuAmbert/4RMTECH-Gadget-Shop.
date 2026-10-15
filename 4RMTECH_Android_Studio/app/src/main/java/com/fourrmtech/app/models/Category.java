package com.fourrmtech.app.models;

import com.google.gson.annotations.SerializedName;

public class Category {

    @SerializedName("category_id")
    private int id;

    @SerializedName("category_name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("product_count")
    private int productCount;

    private int imageResource;

    public Category(
            int id,
            String name,
            String description,
            int productCount,
            int imageResource) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.productCount = productCount;
        this.imageResource = imageResource;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getProductCount() {
        return productCount;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }
}