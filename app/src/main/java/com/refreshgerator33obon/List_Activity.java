package com.refreshgerator33obon;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class List_Activity extends AppCompatActivity {

    private LinearLayout nb_setings, nb_supply, nb_products;
    private ListView listViewItems; // Single ListView
    private Button supplyButton, toBuyButton ;
    private ImageView addButton;
    private ListSupplyAdapter supplyAdapter;
    private ListSupplyAdapter toBuyAdapter;
    private DatabaseHelper dbHelper;
    private boolean isToBuySection = false; // To track which section is active
    private GestureDetector gestureDetector; // Gesture detector for swipe detection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);
        initializeNavigation();

        // Initialize views
        listViewItems = findViewById(R.id.listViewItems);
        supplyButton = findViewById(R.id.btnSupply);
        toBuyButton = findViewById(R.id.btnToBuy);
        addButton = findViewById(R.id.btn_add);

        // Initialize adapters
        supplyAdapter = new ListSupplyAdapter(this, new ArrayList<>(), false, true); // isMenuSection = true
        toBuyAdapter = new ListSupplyAdapter(this, new ArrayList<>(), true, false);  // isMenuSection = false

        // Set initial adapter for supply list
        listViewItems.setAdapter(supplyAdapter);

        // Load supply items initially
        loadSupplyItems();


        // Set button click listeners
//        supplyButton.setOnClickListener(v -> showSupplySection());
//        toBuyButton.setOnClickListener(v -> showToBuySection());
        addButton.setOnClickListener(v -> showAddItemBottomSheet());

        supplyButton.setBackgroundColor(getResources().getColor(R.color.green));
        toBuyButton.setBackgroundColor(getResources().getColor(R.color.white));
        toBuyButton.setTextColor(Color.BLACK);
        supplyButton.setTextColor(Color.WHITE);

        supplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                supplyButton.setBackgroundColor(getResources().getColor(R.color.green));
                toBuyButton.setBackgroundColor(getResources().getColor(R.color.white));
                toBuyButton.setTextColor(Color.BLACK);
                supplyButton.setTextColor(Color.WHITE);
                showSupplySection();
            }
        });


        toBuyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                supplyButton.setBackgroundColor(getResources().getColor(R.color.white));
                toBuyButton.setBackgroundColor(getResources().getColor(R.color.green));
                toBuyButton.setTextColor(Color.WHITE);
                supplyButton.setTextColor(Color.BLACK);
                showToBuySection();
            }
        });

        // Initially display supply section
        showSupplySection();
    }

    private void showSupplySection() {
        isToBuySection = false;
        addButton.setVisibility(View.GONE); // No add button for Supply section
        supplyButton.setEnabled(false); // Disable the active button
        toBuyButton.setEnabled(true); // Enable the "To Buy" button

        // Set adapter and load supply items
        listViewItems.setAdapter(supplyAdapter);
        loadSupplyItems();

        // Disable swipe-to-delete for the Supply section
        disableSwipeToDelete();
    }

    private void showToBuySection() {
        isToBuySection = true;
        addButton.setVisibility(View.VISIBLE); // Show add button for To Buy section
        supplyButton.setEnabled(true); // Enable the "Supply" button
        toBuyButton.setEnabled(false); // Disable the active button

        // Set adapter and load To Buy items
        listViewItems.setAdapter(toBuyAdapter);
        loadBuyItems();

        // Enable swipe-to-delete for the To Buy section
        enableSwipeToDelete();
    }

    private void loadSupplyItems() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Modified query to include the condition for follow = 1
        Cursor cursor = db.rawQuery("SELECT supply.id, products.name, supply.deadline_end " +
                "FROM supply " +
                "JOIN products ON products.id = supply.item_id " +
                "WHERE supply.is_to_buy = 0 AND supply.follow = 1", null);

        supplyAdapter.clear();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String endDate = cursor.getString(2) != null ? cursor.getString(2) : "N/A";

                supplyAdapter.add(new ListSupplyItem(id, name, endDate));
            }
            cursor.close();
        }

        supplyAdapter.notifyDataSetChanged();
    }

    private void loadBuyItems() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, name, end_date FROM buy", null);

        toBuyAdapter.clear();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String endDate = cursor.getString(2) != null ? cursor.getString(2) : "N/A";

                toBuyAdapter.add(new ListSupplyItem(id, name, endDate));
            }
            cursor.close();
        }

        toBuyAdapter.notifyDataSetChanged();
    }

    private void showAddItemBottomSheet() {
        // Display the Bottom Sheet Dialog for adding items to the To Buy list
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_add_item, null);
        FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            bottomSheet.setBackgroundResource(R.drawable.corner_radius); // Apply the rounded background
        }

        EditText itemNameInput = bottomSheetView.findViewById(R.id.etItemName);
        EditText noteInput = bottomSheetView.findViewById(R.id.etNote);
        Button saveButton = bottomSheetView.findViewById(R.id.btnSave);
        Button cancelButton = bottomSheetView.findViewById(R.id.btnCancelItem);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        saveButton.setOnClickListener(v -> {
            String itemName = itemNameInput.getText().toString().trim();
            String note = noteInput.getText().toString().trim();

            if (!itemName.isEmpty()) {
                // Add the new item to the Buy table
                dbHelper.addToBuy(itemName, note);

                // Refresh the To Buy list
                loadBuyItems();

                // Dismiss the dialog
                bottomSheetDialog.dismiss();

                Toast.makeText(this, "Item added to To Buy list.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Item name cannot be empty.", Toast.LENGTH_SHORT).show();
            }
        });





        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void initializeNavigation() {
        nb_setings = findViewById(R.id.nb_settings);
        nb_supply = findViewById(R.id.nb_supply);
        nb_products = findViewById(R.id.nb_product);

        nb_products.setOnClickListener(v -> navigateTo(MainActivity.class));
        nb_supply.setOnClickListener(v -> navigateTo(SupplyActivity.class));
        nb_setings.setOnClickListener(v -> navigateTo(Notification.class));
    }

    private void navigateTo(Class<?> targetActivity) {
        startActivity(new Intent(List_Activity.this, targetActivity));
        finish();
        overridePendingTransition(R.anim.inright, R.anim.outleft);
    }

    // Enable swipe to delete for the To Buy section
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
                        
                    } else {
                        // Swipe left
                        return onSwipeLeft(e1);
                    }
                }
                return false;
            }
        });

        // Set touch listener on the To Buy ListView
        listViewItems.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }

    private void disableSwipeToDelete() {
        // Disable swipe detection for the list view
        listViewItems.setOnTouchListener(null); // Remove touch listener
    }

    private boolean onSwipeLeft(MotionEvent e1) {
        // Get the position of the swiped item
        int position = listViewItems.pointToPosition((int) e1.getX(), (int) e1.getY());
        if (position != AdapterView.INVALID_POSITION) {
            ListSupplyItem swipedItem = toBuyAdapter.getItem(position);

            // Show confirmation dialog
            new AlertDialog.Builder(List_Activity.this)
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete this item?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Delete the item from the Buy table
                        deleteToBuyItem(swipedItem.getId());

                        // Remove the item from the list and notify the adapter
                        toBuyAdapter.remove(swipedItem);
                        toBuyAdapter.notifyDataSetChanged();

                        Toast.makeText(List_Activity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
        return true;
    }

    private void deleteToBuyItem(int itemId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("buy", "id = ?", new String[]{String.valueOf(itemId)});
    }
}