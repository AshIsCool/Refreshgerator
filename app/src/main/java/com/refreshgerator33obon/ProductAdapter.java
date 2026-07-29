package com.refreshgerator33obon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ProductAdapter extends BaseAdapter {
    private Context context;
    private List<Product> products;
    private LayoutInflater inflater;

    // Constructor
    public ProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return products.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.product_item, parent, false);
        }

        // Get product at the current position
        Product product = products.get(position);

        // Bind data to views
        TextView nameTextView = convertView.findViewById(R.id.product_name);
        TextView shelfLifeTextView = convertView.findViewById(R.id.product_shelf_life);

        nameTextView.setText(product.getName());
        if(product.getShelfLife()==-1){

            shelfLifeTextView.setVisibility(View.GONE);
        } else {
            shelfLifeTextView.setVisibility(View.VISIBLE);
            shelfLifeTextView.setText("Shelf Life: " + product.getShelfLife() + " days");
        }


        return convertView;
    }
}
