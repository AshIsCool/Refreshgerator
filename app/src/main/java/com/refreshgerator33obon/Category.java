package com.refreshgerator33obon;

public class Category{

    private int categoryId; // Unique ID from the database
    private String name;
    private int iconResId; // Icon resource ID for predefined categories
    private boolean isPredefined; // True if predefined category, false if user-added

    // Constructor for categories
    public Category(int categoryId, String name, int iconResId, boolean isPredefined) {
        this.categoryId = categoryId;
        this.name = name;
        this.iconResId = iconResId;
        this.isPredefined = isPredefined;
    }

    // Getter for categoryId
    public int getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public int getIconResId() {
        return iconResId;
    }

    public boolean isPredefined() {
        return isPredefined;
    }


    @Override
    public String toString() {
        return name; // Ensures that the name is displayed in ArrayAdapter
    }
}