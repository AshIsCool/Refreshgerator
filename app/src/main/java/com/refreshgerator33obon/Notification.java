package com.refreshgerator33obon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;;
import android.content.pm.PackageManager;
import android.Manifest;


public class Notification extends AppCompatActivity {

    LinearLayout nb_supply,nb_list,nb_products;


    private TextView notificationStatusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        nb_supply = findViewById(R.id.nb_supply);
        nb_list = findViewById(R.id.nb_list);
        nb_products = findViewById(R.id.nb_product);
        Button intro = findViewById(R.id.intro);
        Button expiry = findViewById(R.id.expiry);

        expiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Notification.this,WindowActivity.class));


            }
        });

        intro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Notification.this, OnboardingActivity.class));
                finish();
                overridePendingTransition(R.anim.inright, R.anim.outleft);
            }
        });


        nb_products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Notification.this,MainActivity.class));
                finish();
                overridePendingTransition(R.anim.inleft,R.anim.outright);
            }
        });


        nb_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Notification.this,List_Activity.class));
                finish();
                overridePendingTransition(R.anim.inleft,R.anim.outright);
            }
        });

        nb_supply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Notification.this, SupplyActivity.class));
                finish();
                overridePendingTransition(R.anim.inleft,R.anim.outright);
            }
        });


        notificationStatusText = findViewById(R.id.notification_status_text);

        // Update notification status text based on permission
        updateNotificationStatus();

        // Set an onClickListener for the TextView to open notification settings
        notificationStatusText.setOnClickListener(v -> openNotificationSettings());
    }

    private void updateNotificationStatus() {
        if (isNotificationPermissionGranted()) {
            notificationStatusText.setText("ON");
        } else {
            notificationStatusText.setText("OFF");
        }
    }

    private boolean isNotificationPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        }
        return true; // Notifications are enabled by default for versions below Android 13
    }

    private void openNotificationSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Open app-specific notification settings for Android 13 and above
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            startActivity(intent);
        } else {
            // For versions below Android 13, open the app's general settings to enable notifications
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        }
    }
}