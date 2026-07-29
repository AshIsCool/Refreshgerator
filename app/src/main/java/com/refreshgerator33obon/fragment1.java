package com.refreshgerator33obon;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class fragment1 extends Fragment {




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment1, container, false);

        // Locate the button and set up the click listener
        Button myButton = view.findViewById(R.id.nxt_screen_btn); // Replace with your button's ID
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Switch to the second fragment (index 1) in the ViewPager
                if (getActivity() instanceof OnboardingActivity) {
                    ((OnboardingActivity) getActivity()).goToNextPage(1);
                }
            }
        });

        Button close_btn = view.findViewById(R.id.close_btn);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });

        return view; // Return the view instead of inflating directly
    }




}