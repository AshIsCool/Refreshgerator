package com.refreshgerator33obon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class WindowActivity extends AppCompatActivity {
    private ListView listView;

    ImageView btn_back;
    private WindowAdapter windowsAdapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window);


        btn_back = findViewById(R.id.btn_back);
        listView = findViewById(R.id.listViewWindows);
        windowsAdapter = new WindowAdapter(this, new ArrayList<>());
        listView.setAdapter(windowsAdapter);

        dbHelper = new DatabaseHelper(this);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Load items into the ListView
        loadWindowsItems();
    }

    private void loadWindowsItems() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Query to fetch items from supply and products
        Cursor cursor = db.rawQuery("SELECT supply.id, products.name, supply.deadline_end " +
                "FROM supply " +
                "JOIN products ON products.id = supply.item_id", null);

        windowsAdapter.clear();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0); // Supply ID
                String name = cursor.getString(1); // Product name
                String endDate = cursor.getString(2) != null ? cursor.getString(2) : "N/A"; // Deadline end date

                // Calculate date difference
                String dateDifference = "N/A";
                if (!endDate.equals("N/A")) {
                    dateDifference = calculateDateDifference(endDate);
                }

                // Add to adapter
                windowsAdapter.add(new WindowItem(id, name, endDate, dateDifference));
            }
            cursor.close();
        }

        windowsAdapter.notifyDataSetChanged();
    }

    private String calculateDateDifference(String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date currentDate = new Date();
            Date endDateParsed = sdf.parse(endDate);

            long diffInMillis = endDateParsed.getTime() - currentDate.getTime();
            long daysDifference = TimeUnit.MILLISECONDS.toDays(diffInMillis);

            if (daysDifference >= 0) {
                return "Remaining "+daysDifference + " days ";

            } else {
                return "Expired "+Math.abs(daysDifference) + " days ago";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "Invalid date";
        }
    }
}