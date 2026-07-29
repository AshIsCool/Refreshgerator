package com.refreshgerator33obon;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView categoryListView;
    private List<Category> categories; // Use List instead of ArrayList for flexibility
    private CategoryAdapter categoryAdapter;
    private DatabaseHelper dbHelper;

    LinearLayout nb_supply, nb_list, nb_settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nb_supply = findViewById(R.id.nb_supply);
        nb_list = findViewById(R.id.nb_list);
        nb_settings = findViewById(R.id.nb_settings);
        createNotificationChannel();




        nb_supply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SupplyActivity.class));
                finish();
                overridePendingTransition(R.anim.inright, R.anim.outleft);
            }
        });
        nb_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, List_Activity.class));
                finish();
                overridePendingTransition(R.anim.inright, R.anim.outleft);
            }
        });
        nb_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Notification.class));
                finish();
                overridePendingTransition(R.anim.inright, R.anim.outleft);
            }
        });


        categoryListView = findViewById(R.id.category_list_view);
        categories = new ArrayList<>();
        dbHelper = new DatabaseHelper(this);

        // Load categories from the database
        initializePredefinedCategories();
        categories.addAll(dbHelper.getAllCategories());



        // Setting the adapter to the ListView
        categoryAdapter = new CategoryAdapter(this, categories,dbHelper);
        categoryListView.setAdapter(categoryAdapter);
        categoryAdapter.notifyDataSetChanged();

        // Add category button
        ImageView btnAddCategory = findViewById(R.id.btn_add);
        btnAddCategory.setOnClickListener(v -> showAddCategoryDialog());


        //Category item click listener
        categoryListView.setOnItemClickListener((parent, view, position, id) -> {
            Category clickedCategory = categories.get(position);

            // Start ProductListActivity with category info
            Intent intent = new Intent(MainActivity.this, ProductListActivity.class);
            intent.putExtra("categoryId", clickedCategory.getCategoryId());
            intent.putExtra("categoryName", clickedCategory.getName());
            startActivity(intent);
            overridePendingTransition(R.anim.inright, R.anim.outleft);
        });







    }

    private void showAddCategoryDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_add_category);
        // Find the Bottom Sheet's container and apply the rounded background
        FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            bottomSheet.setBackgroundResource(R.drawable.corner_radius); // Apply the rounded background
        }

        EditText categoryNameEditText = bottomSheetDialog.findViewById(R.id.editTextCategoryName);
        TextView saveButton = bottomSheetDialog.findViewById(R.id.btnSaveCategory);
        TextView cancelButton = bottomSheetDialog.findViewById(R.id.btnCancelCategory);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        saveButton.setOnClickListener(v -> {
            String categoryName = categoryNameEditText.getText().toString().trim();
            if (!categoryName.isEmpty()) {
                addNewCategory(categoryName);
                bottomSheetDialog.dismiss();
            } else {
                Toast.makeText(MainActivity.this, "Category name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        bottomSheetDialog.show();
    }

    private void addNewCategory(String categoryName) {
        // Save the category in the database
        dbHelper.addCategory(categoryName, false);

        // Fetch the latest category ID from the database
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM categories WHERE category_name = ?", new String[]{categoryName});

        int newCategoryId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("id"); // Check for 'id' column
            if (idIndex != -1) {
                newCategoryId = cursor.getInt(idIndex); // Fetch the category ID
            } else {
                Log.e("Database Error", "'id' column not found in the query.");
            }
            cursor.close();
        } else {
            Log.e("Database Error", "No category found with the name: " + categoryName);
        }

        // Add the category to the list if the ID is valid
        if (newCategoryId != -1) {
            categories.add(new Category(newCategoryId,categoryName, 0, false)); // No icon for user-added categories
            categoryAdapter.notifyDataSetChanged();
        } else {
            Log.e("Category Add Error", "Failed to fetch the category ID.");
        }
    }



    private void initializePredefinedCategories() {
        List<String> predefinedCategoryNames = Arrays.asList(
                "Grocery", "Meat and Fish", "Eggs and Dairy Products", "Drinks",
                "Cereals and Pasta", "Frozen Food", "Canned Food", "Condiments", "Bread and Confectionery"
        );

        // Check if there are any predefined categories in the database
        List<Category> existingCategories = dbHelper.getAllCategories();
        for (String name : predefinedCategoryNames) {
            boolean alreadyExists = false;
            for (Category category : existingCategories) {
                if (category.getName().equals(name)) {
                    alreadyExists = true;
                    break;
                }
            }

            // Add predefined category if it doesn't exist
            if (!alreadyExists) {
                dbHelper.addCategory(name, true);
            }
        }
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "supply_notifications";
            String channelName = "Supply Notifications";
            String channelDescription = "Notifications for supply follow reminders.";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }


}