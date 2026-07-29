package com.refreshgerator33obon;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends  ArrayAdapter<Category> {

    private Context context;
    private List<Category> categories;

    private DatabaseHelper dbHelper;

    public CategoryAdapter(Context context, List<Category> categories, DatabaseHelper dbHelper) {
        super(context, 0, categories);
        this.context = context;
        this.categories = categories;
        this.dbHelper = dbHelper;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        }

        Category category = categories.get(position);

        // Set category name
        TextView categoryName = convertView.findViewById(R.id.category_name);
        categoryName.setText(category.getName());

        // Set icon for predefined categories
        ImageView categoryIcon = convertView.findViewById(R.id.category_icon);
        if (category.isPredefined()) {
            categoryIcon.setVisibility(View.VISIBLE);
            categoryIcon.setImageResource(category.getIconResId());
        } else {
            categoryIcon.setVisibility(View.GONE); // Hide icon for user-added categories
        }

        // Handle delete functionality for non-predefined categories
        ImageView deleteIcon = convertView.findViewById(R.id.delete_icon);
        if (category.isPredefined()) {
            deleteIcon.setVisibility(View.GONE); // Hide delete icon for predefined categories
        } else {
            deleteIcon.setVisibility(View.VISIBLE); // Show delete icon for user-added categories
            deleteIcon.setOnClickListener(v -> {
                Category categoryToDelete = categories.get(position);

                if (!categoryToDelete.isPredefined()) { // Prevent deletion of predefined categories
                    // Delete the category from the database
                    dbHelper.deleteCategory(categoryToDelete.getName());

                    // Remove it from the list and update the UI
                    categories.remove(position);
                    notifyDataSetChanged();

                    Toast.makeText(context, "Category deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Cannot delete predefined categories", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return convertView;
    }
}