package com.refreshgerator33obon;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.Manifest;
public class SupplyActivity extends AppCompatActivity {

    private LinearLayout nb_setings, nb_list, nb_products;
    private ImageView btnAddItem;
    private HorizontalScrollView categoryScrollView;
    private LinearLayout categoryLinearLayout;
    private LinearLayout tvNoItems;
    private ListView itemListView;

    private DatabaseHelper dbHelper;
    private List<Category> categories;
    private List<SupplyItem> supplyItems;
    private SupplyItemAdapter supplyItemAdapter;

    private GestureDetector gestureDetector;
    private int selectedCategoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supply);

        // Initialize navigation bar
        initializeNavigation();

        // Initialize views
        btnAddItem = findViewById(R.id.btn_add);
        categoryScrollView = findViewById(R.id.categoryScrollView);
        categoryLinearLayout = findViewById(R.id.categoryLinearLayout);
        tvNoItems = findViewById(R.id.tvNoItems);
        itemListView = findViewById(R.id.itemListView);


        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Load categories from the database
        categories = dbHelper.getAllCategories();

        // Display categories in the HorizontalScrollView
        displayCategories();

        // Default selection: Grocery
        if (!categories.isEmpty()) {
            selectCategory(categories.get(0).getCategoryId());
        }

        // Add button listener
        btnAddItem.setOnClickListener(v -> showCategorySelectionDialog());


        enableSwipeToDelete();

    }

    private void initializeNavigation() {
        nb_setings = findViewById(R.id.nb_settings);
        nb_list = findViewById(R.id.nb_list);
        nb_products = findViewById(R.id.nb_product);

        nb_products.setOnClickListener(v -> navigateTo(MainActivity.class));
        nb_list.setOnClickListener(v -> navigateTo(List_Activity.class));
        nb_setings.setOnClickListener(v -> navigateTo(Notification.class));
    }

    private void navigateTo(Class<?> targetActivity) {
        startActivity(new Intent(SupplyActivity.this, targetActivity));
        finish();
        overridePendingTransition(R.anim.inright, R.anim.outleft);
    }

    private Button selectedCategoryButton = null; // Variable to store the currently selected button

    private void displayCategories() {
        categoryLinearLayout.removeAllViews();
        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);

            // Create the button for each category
            Button categoryButton = new Button(this);
            categoryButton.setText(category.getName());
            categoryButton.setTag(category.getCategoryId());
            categoryButton.setAllCaps(false);
            categoryButton.setBackgroundResource(R.drawable.category_button_background); // Background selector
            categoryButton.setTextColor(Color.BLACK); // Default text color
            categoryButton.setPadding(16, 8, 16, 8);

            // Set layout parameters to add margin between buttons
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(16, 0, 16, 0); // 16px margin on both sides of the button
            categoryButton.setLayoutParams(params);

            // Set click listener for category buttons
            categoryButton.setOnClickListener(v -> {
                // If there's a previously selected button, reset its text color to black
                if (selectedCategoryButton != null) {
                    selectedCategoryButton.setSelected(false); // Reset selected state
                    selectedCategoryButton.setTextColor(Color.BLACK); // Set text color to black
                }

                // Set the clicked button as selected
                categoryButton.setSelected(true);
                categoryButton.setTextColor(Color.WHITE); // Change text color to white for selected button
                selectedCategoryButton = categoryButton; // Update the selected button

                // Call the function to select the category
                selectCategory((int) v.getTag());
            });

            // Add the button to the LinearLayout
            categoryLinearLayout.addView(categoryButton);

            // Set the first button as selected by default
            if (i == 0) {
                categoryButton.setSelected(true);
                categoryButton.setTextColor(Color.WHITE); // Change the text color to white for the first button
                selectedCategoryButton = categoryButton; // Set the first button as the selected button
            }
        }
    }

    private void selectCategory(int categoryId) {
        selectedCategoryId = categoryId;


        // Fetch items for the selected category
        List<SupplyItem> itemsForCategory = dbHelper.getSupplyItemsByCategoryId(categoryId);

        // Update the adapter
        if (supplyItemAdapter != null) {
            supplyItemAdapter.updateItems(itemsForCategory); // Update adapter with new data
        }


        // Fetch supply items for the selected category
        supplyItems = dbHelper.getSupplyItemsByCategoryId(categoryId);

        // Update the ListView or show "No added items"
        if (supplyItems.isEmpty()) {
            itemListView.setVisibility(View.GONE);
            tvNoItems.setVisibility(View.VISIBLE);
        } else {
            tvNoItems.setVisibility(View.GONE);
            itemListView.setVisibility(View.VISIBLE);

            if (supplyItemAdapter == null) {
                supplyItemAdapter = new SupplyItemAdapter(this, supplyItems);
                itemListView.setAdapter(supplyItemAdapter);
            } else {
                supplyItemAdapter.updateItems(supplyItems);
            }

            // Set item click listener
            itemListView.setOnItemClickListener((parent, view, position, id) -> {
                SupplyItem selectedItem = supplyItems.get(position);

                String productName = ((TextView) view.findViewById(R.id.tvItemName)).getText().toString();
                String shelfLife = ((TextView) view.findViewById(R.id.tvQuantity)).getText().toString();

                // Open ProductDetailsActivity
                Intent intent = new Intent(SupplyActivity.this, ProductDetailsActivity2.class);
                intent.putExtra("supply_id", selectedItem.getId());
                intent.putExtra("item_id", selectedItem.getItemId());
                intent.putExtra("name", productName);
                intent.putExtra("shelf_life", shelfLife);
                intent.putExtra("is_follow_enabled", selectedItem.isFollowEnabled());
                intent.putExtra("is_open_enabled", selectedItem.isOpenEnabled());
                intent.putExtra("deadline_start", selectedItem.getDate1());
                intent.putExtra("deadline_end", selectedItem.getDate2());

                startActivity(intent);
                finish();
            });
        }
    }

    private void showCategorySelectionDialog() {
        // Inflate Bottom Sheet Dialog layout for categories
        View categorySheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_category_list, null);
        BottomSheetDialog categoryDialog = new BottomSheetDialog(this);
        categoryDialog.setContentView(categorySheetView);
        FrameLayout bottomSheet = categoryDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            bottomSheet.setBackgroundResource(R.drawable.corner_radius); // Apply the rounded background
        }

        // Reference the ListView inside the BottomSheet
        ListView lvCategories = categorySheetView.findViewById(R.id.lvCategories);
        TextView cancel = categorySheetView.findViewById(R.id.btnCancelCategory);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialog.dismiss();
            }
        });

        // Set up an ArrayAdapter for the ListView using the original Category objects
        ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories);
        lvCategories.setAdapter(categoryAdapter);

        // Handle category selection
        lvCategories.setOnItemClickListener((adapterView, view, position, id) -> {
            // Get the selected Category object
            Category selectedCategory = (Category) adapterView.getItemAtPosition(position);

            // Open the second bottom sheet with items in the selected category
            showProductsBottomSheet(selectedCategory.getCategoryId(), selectedCategory.getName());
        });

        categoryDialog.show();
    }

    private void showProductsBottomSheet(int categoryId, String categoryName) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_products, null);
        bottomSheetDialog.setContentView(sheetView);
        FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            bottomSheet.setBackgroundResource(R.drawable.corner_radius); // Apply the rounded background
        }

        // Set the category name
        TextView categoryNameTextView = sheetView.findViewById(R.id.selected_category_name);
        categoryNameTextView.setText(categoryName);

        // Fetch products from the database
        List<Product> products = dbHelper.getProductsByCategoryId(categoryId);

        // Set up the ListView and Adapter
        ListView productsListView = sheetView.findViewById(R.id.products_list_view);
        ProductAdapter productAdapter = new ProductAdapter(this, products);
        productsListView.setAdapter(productAdapter);

        productsListView.setOnItemClickListener((parent, view, position, id) -> {
            Product selectedProduct = products.get(position);
            // Open the third bottom sheet with details for the selected product
            showSupplyBottomSheet2(selectedProduct.getId(), categoryId, selectedProduct.getName());
            bottomSheetDialog.dismiss();  // Close the products bottom sheet
        });

        // Show the Bottom Sheet
        bottomSheetDialog.show();
    }

    @SuppressLint("ResourceAsColor")
    private void showSupplyBottomSheet2(int productId, int categoryId, String productName) {
        BottomSheetDialog bottomSheetDialog2 = new BottomSheetDialog(this);
        View bottomSheetView2 = getLayoutInflater().inflate(R.layout.bottom_sheet_supply2, null);
        bottomSheetDialog2.setContentView(bottomSheetView2);
        FrameLayout bottomSheet = bottomSheetDialog2.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            bottomSheet.setBackgroundResource(R.drawable.corner_radius); // Apply the rounded background
        }

        // Set up UI elements
        EditText startDateEditText = bottomSheetView2.findViewById(R.id.start_date);
        EditText endDateEditText = bottomSheetView2.findViewById(R.id.end_date);
        TextView itemNameTextView = bottomSheetView2.findViewById(R.id.item_name);
        TextView shelfLifeTextView = bottomSheetView2.findViewById(R.id.shelf_life);
        TextView itemStorageTextView = bottomSheetView2.findViewById(R.id.item_storage);
        TextView itemPodayTextView = bottomSheetView2.findViewById(R.id.item_poday);
        ToggleButton followToggle = bottomSheetView2.findViewById(R.id.follow_toggle);
        ToggleButton openToggle = bottomSheetView2.findViewById(R.id.open_toggle);
        Button saveButton = bottomSheetView2.findViewById(R.id.save_supply_button);
        TextView start_text = bottomSheetView2.findViewById(R.id.start_text);
        // ImageView for start date picker

        // Set item name and shelf life
        itemNameTextView.setText(productName);
        int shelfLife = Integer.parseInt(getShelfLifeByProductId(productId));  // Get shelf life from product
        if(shelfLife==-1){
            shelfLifeTextView.setVisibility(View.GONE);
        }else{
            shelfLifeTextView.setVisibility(View.VISIBLE);
            shelfLifeTextView.setText("Shelf Life: " + shelfLife);
        }


        // Fetch the storage value and display it in item_storage TextView
        DatabaseHelper db = new DatabaseHelper(this);
        int storageValue = db.getStorageValueById(productId);  // Fetch storage value
        String storageText = getStorageText(storageValue);  // Convert storage value to text
        itemStorageTextView.setText("Storage: " + storageText);  // Set storage value
        int podayText = db.getPodayValueById(productId);
        itemPodayTextView.setText(String.valueOf(podayText));

        // Set default values
        startDateEditText.setText("");  // Default to empty, or set to existing value if updating
        endDateEditText.setText("");    // Default to empty, or set to existing value if updating
        followToggle.setChecked(false);
        openToggle.setChecked(false);

        int poDayValue = db.getPodayValueById(productId);

        if (poDayValue==-1){
            openToggle.setVisibility(View.GONE);
        }


        // If storage value is 1 ("From Purchased"), disable the end date input
        if (storageValue == 1) {
            start_text.setText("Purchased");
            endDateEditText.setEnabled(false);  // Disable the end date input field
        } else if (storageValue == 2) {
            start_text.setText("Made Since");
            endDateEditText.setEnabled(false);
        } else if (storageValue == 3) {
            start_text.setText("Good Until");
            endDateEditText.setVisibility(View.GONE);
            endDateEditText.setEnabled(false);
            openToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    endDateEditText.setVisibility(View.VISIBLE); // Show the view
                    // Optional: Update the text
                } else {
                    endDateEditText.setVisibility(View.GONE); // Hide the view
                    // Optional: Update the text
                }
            });
        }



        openToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Change background tint to green when toggled on
                openToggle.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.green));
                endDateEditText.setVisibility(View.VISIBLE);
            } else {
                // Change background tint back to black when toggled off
                openToggle.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));
                endDateEditText.setVisibility(View.GONE);
            }
        });



        followToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            requestNotificationPermission();
            if (isChecked) {
                followToggle.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.green));
                // Schedule notifications
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date endDate = sdf.parse(endDateEditText.getText().toString()); // Ensure endDate is in correct format

                    if (endDate != null) {
                        // 1 day before
                        long oneDayBeforeMillis = endDate.getTime() - (24 * 60 * 60 * 1000);
                      //  scheduleNotification(this, productName, oneDayBeforeMillis, false);

                        // On end date
                      //  scheduleNotification(this, productName, endDate.getTime(), true);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                followToggle.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));
                // Remove existing notifications
                // Not implemented here for simplicity
            }
        });


        startDateEditText.setFocusable(false);
        startDateEditText.clearFocus();

        // Set start date using DatePicker and pass productId, storageValue, and endDateEditText
        startDateEditText.setOnClickListener(v -> setDatePicker(startDateEditText, endDateEditText, productId, storageValue));

        // Save button click listener
        saveButton.setOnClickListener(v -> {
            // Save the supply info
            if(startDateEditText.getText().toString().trim().isEmpty()){
                Toast.makeText(this, "Date field required", Toast.LENGTH_SHORT).show();
            }else{
                saveSupply(productId, categoryId, productName, startDateEditText, endDateEditText, followToggle, openToggle);
                bottomSheetDialog2.dismiss();  // Close the bottom sheet after saving
            }


        });

        // Show the Bottom Sheet
        bottomSheetDialog2.show();
    }


    private void saveSupply(int productId, int categoryId, String productName, EditText startDateEditText, EditText endDateEditText, ToggleButton followToggle, ToggleButton openToggle) {
        // Get values from the UI
        String startDate = startDateEditText.getText().toString();
        String endDate = endDateEditText.getText().toString();
        boolean isFollowed = followToggle.isChecked();
        boolean isOpened = openToggle.isChecked();

        // Fetch the storage value from the database
        DatabaseHelper db1 = new DatabaseHelper(this);
        int storageValue = db1.getStorageValueById(productId);  // Get the storage value for the product
        int podayText = db1.getPodayValueById(productId);
        String storageText = getStorageText(storageValue);  // Convert storage value to text


        // Save data into the database
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("item_id", productId);  // Use item_id instead of product_name
        values.put("shelf_life", getShelfLifeByProductId(productId));
        values.put("category_id", categoryId);  // Add category_id
        values.put("deadline_start", startDate);  // deadline_start -> date1
        values.put("deadline_end", endDate);    // deadline_end -> date2
        values.put("follow", isFollowed ? 1 : 0);
        values.put("open", isOpened ? 1 : 0);
        values.put("storage", storageValue);  // Add storage value to the supply entry
        values.put("poday", podayText);  // Add storage value to the supply entry

        long result = db.insert("supply", null, values);

        if (result != -1) {
            // Successfully inserted
            selectCategory(categoryId);
            Toast.makeText(this, "Supply saved successfully!", Toast.LENGTH_SHORT).show();
        } else {
            // Failed to insert
            Toast.makeText(this, "Failed to save supply.", Toast.LENGTH_SHORT).show();
        }
    }


    private String getPodayByProductId(int productId) {
        // Open the database for reading
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Perform a query to fetch the shelf life for the given product ID
        Cursor cursor = db.query(
                "products",                          // Table name
                new String[]{"poday"},           // Columns to retrieve
                "id=?",                              // WHERE clause (change product_id to id)
                new String[]{String.valueOf(productId)}, // WHERE arguments (product ID)
                null,                                // GROUP BY
                null,                                // HAVING
                null                                 // ORDER BY
        );

        // Check if the cursor contains data
        if (cursor != null && cursor.moveToFirst()) {
            // Get the index of the "shelf_life" column
            int podayColumnIndex = cursor.getColumnIndex("poday");

            // Check if the column index is valid (>=0)
            if (podayColumnIndex >= 0) {
                // Retrieve the shelf life value from the cursor
                String poday = cursor.getString(podayColumnIndex);
                cursor.close(); // Always close the cursor when done
                return poday;
            } else {
                // If the column doesn't exist, log an error and return a default value
                Log.e("podayQuery", "Column 'poday' not found.");
                cursor.close();
                return "N/A";
            }
        }

        // If no data found, return a default value
        if (cursor != null) {
            cursor.close(); // Close cursor if no data
        }
        return "N/A"; // Default shelf life if not found
    }


    private String getShelfLifeByProductId(int productId) {
        // Open the database for reading
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Perform a query to fetch the shelf life for the given product ID
        Cursor cursor = db.query(
                "products",                          // Table name
                new String[]{"shelf_life"},           // Columns to retrieve
                "id=?",                              // WHERE clause (change product_id to id)
                new String[]{String.valueOf(productId)}, // WHERE arguments (product ID)
                null,                                // GROUP BY
                null,                                // HAVING
                null                                 // ORDER BY
        );

        // Check if the cursor contains data
        if (cursor != null && cursor.moveToFirst()) {
            // Get the index of the "shelf_life" column
            int shelfLifeColumnIndex = cursor.getColumnIndex("shelf_life");

            // Check if the column index is valid (>=0)
            if (shelfLifeColumnIndex >= 0) {
                // Retrieve the shelf life value from the cursor
                String shelfLife = cursor.getString(shelfLifeColumnIndex);
                cursor.close(); // Always close the cursor when done
                return shelfLife;
            } else {
                // If the column doesn't exist, log an error and return a default value
                Log.e("ShelfLifeQuery", "Column 'shelf_life' not found.");
                cursor.close();
                return "N/A";
            }
        }

        // If no data found, return a default value
        if (cursor != null) {
            cursor.close(); // Close cursor if no data
        }
        return "N/A"; // Default shelf life if not found
    }


    private void enableSwipeToDelete() {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                // Detect swipe direction
                float deltaX = e2.getX() - e1.getX();
                if (Math.abs(deltaX) > Math.abs(e2.getY() - e1.getY()) && Math.abs(deltaX) > 100 && Math.abs(velocityX) > 100) {
                    // Determine swipe direction
                    if (deltaX > 0) {
                        // Swipe right
                        return onSwipeRight(e1);
                    } else {
                        // Swipe left
                        return onSwipeLeft(e1);
                    }
                }
                return false;
            }
        });

        // Set touch listener on the ListView
        itemListView.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }

    private boolean onSwipeLeft(MotionEvent e1) {
        // Get the position of the swiped item
        int position = itemListView.pointToPosition((int) e1.getX(), (int) e1.getY());
        if (position != AdapterView.INVALID_POSITION) {
            SupplyItem swipedItem = supplyItems.get(position);

            // Show confirmation dialog
            new AlertDialog.Builder(SupplyActivity.this)
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete this item?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Delete the item from the database
                        deleteSupplyFromDatabase(swipedItem.getId());

                        // Remove the item from the list and notify the adapter
                        supplyItems.remove(position);
                        supplyItemAdapter.notifyDataSetChanged();

                        // Show confirmation
                        Toast.makeText(SupplyActivity.this, "Item deleted.", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        // Do nothing, item remains in the list
                        dialog.dismiss();
                    })
                    .setCancelable(false)
                    .show();

            return true;
        }
        return false;
    }

    private boolean onSwipeRight(MotionEvent e1) {
        // Optionally, handle right-swipe actions here (e.g., edit or archive).
        return false;
    }

    // Delete item from the database
    private void deleteSupplyFromDatabase(int supplyId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted = db.delete("supply", "id=?", new String[]{String.valueOf(supplyId)});
        if (rowsDeleted > 0) {
            Log.d("SupplyActivity", "Deleted item with ID: " + supplyId);
        } else {
            Log.e("SupplyActivity", "Failed to delete item with ID: " + supplyId);
        }
    }


    private String getStorageText(int storageValue) {
        switch (storageValue) {
            case 1:
                return "From Purchase";
            case 2:
                return "From Made";
            case 3:
                return "Good Until";
            default:
                return "Unknown";
        }
    }


    private void setDatePicker(final EditText startDateEditText, final EditText endDateEditText, final int productId, final int storageValue) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);



        View bottomSheetView2 = getLayoutInflater().inflate(R.layout.bottom_sheet_supply2, null);
        ToggleButton openToggle = bottomSheetView2.findViewById(R.id.open_toggle);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    // Format the selected date as "YYYY-MM-DD"
                    String selectedDate = String.format("%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                    startDateEditText.setText(selectedDate);  // Set the selected date in the startDateEditText

                    // If the storage value is "From Purchased", calculate the end date automatically
                    int shelfLife = Integer.parseInt(getShelfLifeByProductId(productId));
                    int poday = Integer.parseInt(getPodayByProductId(productId));// Get shelf life of the product
                    if (storageValue == 1) {
                        String endDate = calculateEndDate(selectedDate, shelfLife);  // Calculate the end date
                        endDateEditText.setText(endDate);  // Set the calculated end date in the endDateEditText
                    } else if (storageValue == 2) {

                        String endDate = calculateEndDate(selectedDate, shelfLife);  // Calculate the end date
                        endDateEditText.setText(endDate);

                    } else if (storageValue == 3) {
                        String endDate = calculatepoEndDate(selectedDate, poday);  // Calculate the end date
                        endDateEditText.setText(endDate);
                    }



                }, year, month, day);

        datePickerDialog.show();
    }


    private String calculateEndDate(String startDate, int shelfLife) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date start = sdf.parse(startDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(start);
            calendar.add(Calendar.DAY_OF_YEAR, shelfLife);  // Add shelf life to the start date
            Date end = calendar.getTime();
            return sdf.format(end);  // Return the calculated end date
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


    private String calculatepoEndDate(String startDate, int poday) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Calendar calendar = Calendar.getInstance(); // Initialize with the current date
            calendar.add(Calendar.DAY_OF_YEAR, poday);  // Add the specified number of days to the current date
            Date end = calendar.getTime();             // Get the calculated end date
            return sdf.format(end);                    // Return the formatted end date
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Request permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }
    }


//    public void sendNotification(Context context, String title, String message) {
//        Intent intent = new Intent(context, MainActivity.class); // Change MainActivity to the relevant activity
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "supply_notifications")
//
//                .setContentTitle(title)
//                .setContentText(message)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setContentIntent(pendingIntent)
//                .setAutoCancel(true);
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
//    }
//
//
//    @SuppressLint("ScheduleExactAlarm")
//    private void scheduleNotification(Context context, String productName, long triggerAtMillis, boolean isExpiration) {
//        Intent intent = new Intent(context, NotificationReceiver.class);
//        intent.putExtra("productName", productName);
//        intent.putExtra("isExpiration", isExpiration);
//
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(
//                context,
//                (int) System.currentTimeMillis(),
//                intent,
//                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE // Add FLAG_IMMUTABLE for Android 12+
//        );
//
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        if (alarmManager != null) {
//            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
//        }
//    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == 1001 && resultCode == RESULT_OK) {



            if (selectedCategoryButton != null) {
                int selectedCategoryId = (int) selectedCategoryButton.getTag();
                selectCategory(selectedCategoryId); // Re-fetch and display items for the selected category
            }
        }
    }









}
