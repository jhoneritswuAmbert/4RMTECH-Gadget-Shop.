package com.fourrmtech.app.models;

public class DeleteCategoryRequest {

    private int category_id;

    public DeleteCategoryRequest(int category_id) {
        this.category_id = category_id;
    }

    public int getCategory_id() {
        return category_id;
    }
}