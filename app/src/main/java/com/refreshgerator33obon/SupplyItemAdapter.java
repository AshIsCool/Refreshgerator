package com.refreshgerator33obon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;
public class SupplyItemAdapter extends BaseAdapter {

    private Context context;
    private List<SupplyItem> supplyItems;

    public SupplyItemAdapter(Context context, List<SupplyItem> supplyItems) {
        this.context = context;
        this.supplyItems = supplyItems;
    }

    @Override
    public int getCount() {
        return supplyItems.size();
    }

    @Override
    public Object getItem(int position) {
        return supplyItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return supplyItems.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_supply, parent, false);
        }

        // Get current item
        SupplyItem supplyItem = supplyItems.get(position);

        // Bind views
        TextView tvItemName = convertView.findViewById(R.id.tvItemName);
        TextView tvQuantity = convertView.findViewById(R.id.tvQuantity);
        TextView tvEndDate = convertView.findViewById(R.id.tvEndDate);
        TextView tvItemStorage = convertView.findViewById(R.id.item_storage);
        TextView tvItemPoday = convertView.findViewById(R.id.item_poday);


        // Fetch product name for tvItemName
        DatabaseHelper db = new DatabaseHelper(context); //53 number line is this line
        String productName = db.getProductNameById(supplyItem.getItemId());
        if (productName == null || productName.trim().isEmpty()) {
            productName = "Unknown";
        }
        // Add this method
        tvItemName.setText(productName != null ? productName : "Unknown");




        // Fetch storage value and set it to the item_storage TextView
        int storageValue = db.getStorageValueById(supplyItem.getItemId());  // Add method to fetch storage value
        String storageText = getStorageText(storageValue);  // Convert storage value to text
        tvItemStorage.setText(storageText);  // Set storage value in the TextView


        int podayText = db.getPodayValueById(supplyItem.getItemId());  // Add method to fetch storage value
         // Convert storage value to text
        tvItemPoday.setText(String.valueOf(podayText));  // Set storage value in the TextView








        // Set other values
        tvQuantity.setText("" + String.valueOf(supplyItem.getShelfLife())+" days");
        tvQuantity.setVisibility(View.GONE);
        tvEndDate.setText("Deadline: " + (supplyItem.getDate2() != null ? supplyItem.getDate2() : "N/A"));



        return convertView;
    }

    public void updateItems(List<SupplyItem> newSupplyItems) {
        this.supplyItems = newSupplyItems;
        notifyDataSetChanged();
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

}

