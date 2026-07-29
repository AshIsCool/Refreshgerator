package com.refreshgerator33obon;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ListSupplyAdapter extends ArrayAdapter<ListSupplyItem> {
    private Context context;
    private List<ListSupplyItem> listsupplyItems;
    private DatabaseHelper dbHelper;
    private boolean isToBuySection; // Flag for "To Buy" section
    private boolean isMenuSection; // Flag for "Supply" menu section
    private SharedPreferences prefs;

    public ListSupplyAdapter(Context context, List<ListSupplyItem> listsupplyItems, boolean isToBuySection, boolean isMenuSection) {
        super(context, 0, listsupplyItems);
        this.context = context;
        this.listsupplyItems = listsupplyItems;
        this.dbHelper = new DatabaseHelper(context);
        this.isToBuySection = isToBuySection; // Indicates if it's the "To Buy" list
        this.isMenuSection = isMenuSection; // Indicates if it's the "Supply" menu section

        // Initialize SharedPreferences
        this.prefs = context.getSharedPreferences("ToBuyPrefs", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listsupplyitem, parent, false);
        }

        // Get views
        TextView itemName = convertView.findViewById(R.id.tvItemName);
        TextView shelfLife = convertView.findViewById(R.id.tvShelfLife);
        Button toBuyButton = convertView.findViewById(R.id.btnToBuy);
        CheckBox itemCheckbox = convertView.findViewById(R.id.itemCheckbox);

        // Get the current supply item
        ListSupplyItem listsupplyItem = listsupplyItems.get(position);

        // Set item details
        itemName.setText(listsupplyItem.getItemName());
        shelfLife.setText(listsupplyItem.getShelfLife()); // Display end_date as shelf life

        // Control CheckBox visibility based on isMenuSection
        if (isMenuSection) {
            itemCheckbox.setVisibility(View.GONE);
        } else {
            itemCheckbox.setVisibility(View.VISIBLE);

            // Manage CheckBox state
            String checkboxKey = "checkbox_" + listsupplyItem.getId();
            boolean isChecked = prefs.getBoolean(checkboxKey, false);
            itemCheckbox.setChecked(isChecked);

            // Save CheckBox state on toggle
            itemCheckbox.setOnCheckedChangeListener((buttonView, isChecked1) -> {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(checkboxKey, isChecked1);
                editor.apply();
            });
        }

        // Hide the To Buy button if in the To Buy section
        if (isToBuySection) {
            toBuyButton.setVisibility(View.GONE);
        } else {
            toBuyButton.setVisibility(View.VISIBLE);

            // Handle To Buy button click
            toBuyButton.setOnClickListener(v -> {
                // Add the item to the Buy table
                dbHelper.addToBuy(listsupplyItem.getItemName(), listsupplyItem.getShelfLife());

                // Provide feedback to the user
                Toast.makeText(context, listsupplyItem.getItemName() + " added to To Buy list.", Toast.LENGTH_SHORT).show();
            });
        }

        return convertView;
    }
}