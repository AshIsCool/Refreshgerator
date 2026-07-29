package com.refreshgerator33obon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;

import com.google.android.material.tabs.TabLayout;

public class OnboardingActivity extends AppCompatActivity {


    private TabLayout tabLayout;
    private ViewPager viewPager;

    Button next_screen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);




        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        tabLayout.setupWithViewPager(viewPager);

        VPAdapter vpAdapter = new VPAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragment(new fragment1(),"");
        vpAdapter.addFragment(new fragment2(),"");
        vpAdapter.addFragment(new fragment3(),"");
        vpAdapter.addFragment(new fragment4(),"");
        viewPager.setAdapter(vpAdapter);






    }

    public void goToNextPage(int pageIndex) {
        viewPager.setCurrentItem(pageIndex);
    }
}