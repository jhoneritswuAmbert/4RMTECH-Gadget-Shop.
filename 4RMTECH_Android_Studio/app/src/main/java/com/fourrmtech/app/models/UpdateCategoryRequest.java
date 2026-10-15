package com.fourrmtech.app.models;

public class UpdateCategoryRequest {

    private int category_id;
    private String category_name;
    private String description;

    public UpdateCategoryRequest(
            int category_id,
            String category_name,
            String description) {

        this.category_id = category_id;
        this.category_name = category_name;
        this.description = description;
    }

    public int getCategory_id() {
        return category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public String getDescription() {
        return description;
    }
}