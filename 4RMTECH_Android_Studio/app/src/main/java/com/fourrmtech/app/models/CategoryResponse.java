package com.fourrmtech.app.models;

import java.util.List;

public class CategoryResponse {

    private boolean success;
    private List<Category> categories;

    public boolean isSuccess() {
        return success;
    }

    public List<Category> getCategories() {
        return categories;
    }
}