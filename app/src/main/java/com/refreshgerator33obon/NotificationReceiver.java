package com.refreshgerator33obon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String productName = intent.getStringExtra("productName");
        boolean isExpiration = intent.getBooleanExtra("isExpiration", false);

        String title = isExpiration ? "Product Expired" : "Product Expiry Reminder";
        String message = isExpiration
                ? "Your " + productName + " is expired."
                : "Your " + productName + " is about to expire.";

       // new SupplyActivity().sendNotification(context, title, message);
       // new ProductDetailsActivity2().sendNotifications(context, title, message);
    }
}