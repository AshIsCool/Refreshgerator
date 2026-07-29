package com.refreshgerator33obon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
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
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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

public class ProductDetailsActivity2 extends AppCompatActivity {

    private EditText startDateEditText, endDateEditText,endDateEditText1;
    private ToggleButton followToggle, openToggle;
    private Button updateButton, deleteButton;
    private TextView productNameTextView, shelfLifeTextView;


    private List<Category> categories;

    private DatabaseHelper dbHelper;
    private int supplyId, productId, shelfnum,categoryId;
    private String productName, startDate,shelfLife, endDate;
    private boolean isFollowed, isOpened;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details2);

        // Initialize views
        productNameTextView = findViewById(R.id.product_name);
        shelfLifeTextView = findViewById(R.id.shelf_life);
        startDateEditText = findViewById(R.id.start_date);
        endDateEditText = findViewById(R.id.end_date);
//        endDateEditText1 = findViewById(R.id.end_date1);
        followToggle = findViewById(R.id.follow_toggle);
        openToggle = findViewById(R.id.open_toggle);
        updateButton = findViewById(R.id.update_button);
        deleteButton = findViewById(R.id.delete_button);
        TextView startText = findViewById(R.id.start_text);


        dbHelper = new DatabaseHelper(this);

        // Make the EditText completely non-focusable
        startDateEditText.setFocusable(false);
        startDateEditText.setClickable(true);
        endDateEditText.setKeyListener(null);

// Set OnClickListener to open the DatePickerDialog
//        startDateEditText.setOnClickListener(v -> {
//            // Get the current date
//            Calendar calendar = Calendar.getInstance();
//            int year = calendar.get(Calendar.YEAR);
//            int month = calendar.get(Calendar.MONTH);
//            int day = calendar.get(Calendar.DAY_OF_MONTH);
//
//            // Create and show the DatePickerDialog
//            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
//                    (view, selectedYear, selectedMonth, selectedDay) -> {
//                        // Format the selected date as "yyyy-MM-dd"
//                        String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
//
//                        // Set the selected date in the EditText
//                        startDateEditText.setText(selectedDate);
//                    }, year, month, day);
//
//            datePickerDialog.show();
//        });






































//        DatabaseHelper db = new DatabaseHelper(this);
//        int storageValue = db.getStorageValueById(productId);
//        int poDayValue = db.getPodayValueById(productId);





        // Set start date using DatePicker and pass productId, storageValue, and endDateEditText
//        startDateEditText.setOnClickListener(v -> setDatePicker2(startDateEditText, endDateEditText, productId, storageValue));






        // Retrieve supply data passed from SupplyActivity
        Intent intent = getIntent();
        supplyId = intent.getIntExtra("supply_id", -1);
        productId = intent.getIntExtra("item_id", -1);
        shelfLife = intent.getStringExtra("shelf_life");

        isFollowed = intent.getBooleanExtra("is_follow_enabled", false);
        isOpened = intent.getBooleanExtra("is_open_enabled", false);
        startDate = intent.getStringExtra("deadline_start");
        endDate = intent.getStringExtra("deadline_end");
        productName = intent.getStringExtra("name");
        shelfnum = Integer.parseInt(getShelfLifeByProductId(productId));
        int poday = Integer.parseInt(getPodayByProductId(productId));
        int storageValue = Integer.parseInt(getStorageValueById(productId));








        startDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setDatePicker(startDateEditText, endDateEditText, productId, storageValue);

            }
        });




        if(storageValue==1){
            startText.append("Purchased ");
        } else if(storageValue==2){
            startText.append("Made Since ");
        }else if(storageValue==3){
            startText.append("Good Until ");
        }

        // Populate UI with data
        productNameTextView.setText(productName != null ? productName : "Unknown");

        if (shelfnum==-1){
            shelfLifeTextView.setVisibility(View.GONE);
        } else{
            shelfLifeTextView.setVisibility(View.VISIBLE);

            shelfLifeTextView.setText("Shelf Life: " + shelfLife);
        }

        startDateEditText.setText(startDate != null ? startDate : "");
        endDateEditText.setText(endDate != null ? endDate : "");
        followToggle.setChecked(isFollowed);
        openToggle.setChecked(isOpened);




        if (isFollowed) { // Assume `isFollowed` is a boolean loaded from the database
            followToggle.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.green));
        } else {
            followToggle.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));
        }

        if (isOpened) { // Assume `isOpened` is a boolean loaded from the database
            openToggle.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.green));
            endDateEditText.setVisibility(View.VISIBLE);
        } else {
            openToggle.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));
            endDateEditText.setVisibility(View.GONE);
            if (poday==-1&&(storageValue==1||storageValue==2)){
                endDateEditText.setVisibility(View.VISIBLE);
            }
        }


        if((storageValue==1||storageValue==2)&&poday!=-1){
            openToggle.setVisibility(View.VISIBLE);
            endDateEditText.setVisibility(View.VISIBLE);
        } else if(storageValue==3){
            openToggle.setVisibility(View.VISIBLE);
            if (isOpened){
                endDateEditText.setVisibility(View.VISIBLE);
            }else{
                endDateEditText.setVisibility(View.GONE);
            }


        }else {
            openToggle.setVisibility(View.GONE);

            endDateEditText.setVisibility(View.VISIBLE);
        }


        if (poday==-1){
            openToggle.setVisibility(View.GONE);

        }else{
            openToggle.setVisibility(View.VISIBLE);
        }


        openToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                endDateEditText.setVisibility(View.VISIBLE);

                openToggle.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.green));
                if(storageValue==1 || storageValue==2){
                    setDatePicker2(startDateEditText, endDateEditText, productId, storageValue);
                } else if(storageValue==3){
                    setDatePicker2(startDateEditText, endDateEditText, productId, storageValue);
                }


            } else {
                openToggle.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));
                if(storageValue==1||storageValue==2){
                    String endDate = calculateEndDate(startDateEditText.getText().toString(), shelfnum);  // Calculate the end date
                    endDateEditText.setText(endDate);
                }else{
                    endDateEditText.setVisibility(View.GONE);
                }


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
                     //   scheduleNotification(this, productName, oneDayBeforeMillis, false);

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



        // Set up button listeners
        updateButton.setOnClickListener(v -> updateSupplyDetails());
        deleteButton.setOnClickListener(v -> deleteSupply());







    }

    private void updateSupplyDetails() {
        String updatedStartDate = startDateEditText.getText().toString();
        String updatedEndDate = endDateEditText.getText().toString();
        boolean updatedFollowed = followToggle.isChecked();
        boolean updatedOpened = openToggle.isChecked();



        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("deadline_start", updatedStartDate);
        values.put("deadline_end", updatedEndDate);
        values.put("follow", updatedFollowed ? 1 : 0);
        values.put("open", updatedOpened ? 1 : 0);

        // Handle toggle changes dynamically



        int rowsAffected = db.update("supply", values, "id = ?", new String[]{String.valueOf(supplyId)});

        if (rowsAffected > 0) {
            Toast.makeText(this, "Supply details updated successfully.", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);  // Notify SupplyActivity to refresh the list
            startActivity(new Intent(ProductDetailsActivity2.this, SupplyActivity.class));
            overridePendingTransition(R.anim.inright, R.anim.outleft);
            finish(); // Close the activity
        } else {
            Toast.makeText(this, "Failed to update supply details.", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteSupply() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted = db.delete("supply", "id = ?", new String[]{String.valueOf(supplyId)});

        if (rowsDeleted > 0) {
            Toast.makeText(this, "Supply deleted successfully.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProductDetailsActivity2.this, SupplyActivity.class));
            overridePendingTransition(R.anim.inright, R.anim.outleft);
            setResult(RESULT_OK);  // Notify SupplyActivity to refresh the list
            finish(); // Close the activity
        } else {
            Toast.makeText(this, "Failed to delete supply.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ProductDetailsActivity2.this, SupplyActivity.class);
        overridePendingTransition(R.anim.inright, R.anim.outleft);
        intent.putExtra("categoryId", getIntent().getIntExtra("categoryId", -1));
        intent.putExtra("categoryName", getIntent().getStringExtra("categoryName"));
        startActivity(intent);
        finish();
    }











    //New add=======================================================

    private String getStorageValueById(int productId) {
        // Open the database for reading
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Perform a query to fetch the shelf life for the given product ID
        Cursor cursor = db.query(
                "products",                          // Table name
                new String[]{"storage"},           // Columns to retrieve
                "id=?",                              // WHERE clause (change product_id to id)
                new String[]{String.valueOf(productId)}, // WHERE arguments (product ID)
                null,                                // GROUP BY
                null,                                // HAVING
                null                                 // ORDER BY
        );

        // Check if the cursor contains data
        if (cursor != null && cursor.moveToFirst()) {
            // Get the index of the "shelf_life" column
            int storageColumnIndex = cursor.getColumnIndex("storage");

            // Check if the column index is valid (>=0)
            if (storageColumnIndex >= 0) {
                // Retrieve the shelf life value from the cursor
                String storage = cursor.getString(storageColumnIndex);
                cursor.close(); // Always close the cursor when done
                return storage;
            } else {
                // If the column doesn't exist, log an error and return a default value
                Log.e("storageQuery", "Column 'storage' not found.");
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






    private void setDatePicker3(final EditText startDateEditText, final EditText endDateEditText, final int productId, final int storageValue) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);



        View bottomSheetView2 = getLayoutInflater().inflate(R.layout.bottom_sheet_supply2, null);
        ToggleButton openToggle = bottomSheetView2.findViewById(R.id.open_toggle);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    // Format the selected date as "YYYY-MM-DD"
                    String selectedDate = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
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



    private void setDatePicker2(final EditText startDateEditText, final EditText endDateEditText, final int productId, final int storageValue) {
        if (storageValue == 3 ||storageValue==2||storageValue==1) {
            // Get the product's poday
            int poday = Integer.parseInt(getPodayByProductId(productId));

            // Get the current date as the start date
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String startDate = sdf.format(calendar.getTime());

            // Set the start date in the startDateEditText
//            startDateEditText.setText(startDate);

            // Calculate and set the end date in the endDateEditText
            String endDate = calculatepoEndDate(startDate, poday);
            endDateEditText.setText(endDate);
        } else {
            // If not storage value 3, do nothing
            startDateEditText.setText("");
            endDateEditText.setText("");
        }

        if (storageValue==3){
            validateAndSetEndDate(startDateEditText, endDateEditText);
        } else if(storageValue==1 || storageValue==2){
            validateAndSetEndDateWithShelfLife(startDateEditText, endDateEditText, shelfnum);
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


//    public void sendNotifications(Context context, String title, String message) {
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



    private void validateAndSetEndDate(EditText startDateEditText, EditText endDateEditText) {

        // Date format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            // Parse dates from the EditText fields
            String startDateText = startDateEditText.getText().toString().trim();
            String endDateText = endDateEditText.getText().toString().trim();

            // Ensure both dates are non-empty
            if (!startDateText.isEmpty() && !endDateText.isEmpty()) {
                Date startDate = sdf.parse(startDateText); // Convert start date text to Date
                Date endDate = sdf.parse(endDateText);     // Convert end date text to Date

                // Compare the dates
                if (endDate.after(startDate)) {
                    // If endDate is greater, set endDate to startDate
                    endDateEditText.setText(startDateText);
                }
            } else {
                Toast.makeText(this, "Start date or end date is missing!", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid date format!", Toast.LENGTH_SHORT).show();
        }
    }



    private void validateAndSetEndDateWithShelfLife(EditText startDateEditText, EditText endDateEditText, int shelfnum) {

        // Date format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            // Parse the start date from the EditText
            String startDateText = startDateEditText.getText().toString().trim();
            String endDateText = endDateEditText.getText().toString().trim();

            // Ensure both dates are non-empty
            if (!startDateText.isEmpty() && !endDateText.isEmpty()) {
                Date startDate = sdf.parse(startDateText); // Convert start date text to Date
                Date endDate = sdf.parse(endDateText);     // Convert end date text to Date

                // Add shelfLife to startDate
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startDate);
                calendar.add(Calendar.DAY_OF_YEAR, shelfnum);
                Date startDatePlusShelfLife = calendar.getTime();

                // Compare the dates
                if (endDate.after(startDatePlusShelfLife)) {
                    // If endDate is greater, set endDate to startDate + shelfLife
                    endDateEditText.setText(sdf.format(startDatePlusShelfLife));
                }
            } else {
                Toast.makeText(this, "Start date or end date is missing!", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid date format!", Toast.LENGTH_SHORT).show();
        }
    }





    //===========================================================================


    //===========================================================================



















}