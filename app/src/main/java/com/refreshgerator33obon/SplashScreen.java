package com.refreshgerator33obon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        // Delayed navigation to the main activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check if onboarding has been shown
                SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                boolean isFirstLaunch = prefs.getBoolean("isFirstLaunch", true);

                if (isFirstLaunch) {
                    // Show the Onboarding Activity
                    startActivity(new Intent(SplashScreen.this, OnboardingActivity.class));

                    // Update SharedPreferences to indicate onboarding has been shown
                    prefs.edit().putBoolean("isFirstLaunch", false).apply();
                } else {
                    // Skip to the main activity
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                }
                finish(); // Close SplashActivity
            }
        }, 3000);
    }
}