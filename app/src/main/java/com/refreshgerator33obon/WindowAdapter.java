package com.refreshgerator33obon;

import android.content.Context;
import android.content.SharedPreferences;
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

public class WindowAdapter extends ArrayAdapter<WindowItem> {
    private Context context;

    public WindowAdapter(@NonNull Context context, @NonNull List<WindowItem> objects) {
        super(context, 0, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.window_item, parent, false);
        }

        WindowItem currentItem = getItem(position);

        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvEndDate = convertView.findViewById(R.id.tvEndDate);
        TextView tvDateDifference = convertView.findViewById(R.id.tvDateDifference);

        if (currentItem != null) {
            tvName.setText(currentItem.getName());
            tvEndDate.setText(currentItem.getEndDate());
            tvDateDifference.setText(currentItem.getDateDifference());
        }

        return convertView;
    }
}