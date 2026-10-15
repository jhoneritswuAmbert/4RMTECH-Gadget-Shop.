package com.fourrmtech.app.models;

public class AddCategoryRequest {

    private String category_name;
    private String description;

    public AddCategoryRequest(
            String category_name,
            String description) {

        this.category_name = category_name;
        this.description = description;
    }

    public String getCategory_name() {
        return category_name;
    }

    public String getDescription() {
        return description;
    }
}