package com.example.myemailapp.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Get username from SharedPreferences
        String username = "Moo"; // default
        if (getContext() != null) {
            SharedPreferences prefs = getContext().getSharedPreferences("auth", MODE_PRIVATE);
            username = prefs.getString("username", "Moo");
        }

        // Create the layout programmatically
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 80, 60, 60);
        layout.setGravity(android.view.Gravity.CENTER);

        // Welcome message
        TextView welcomeText = new TextView(getContext());
        welcomeText.setText("Hello " + username + "!");
        welcomeText.setTextSize(28);
        welcomeText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        welcomeText.setPadding(0, 0, 0, 40);
        welcomeText.setTextColor(0xFF2196F3); // Blue color

        // App description
        TextView descText = new TextView(getContext());
        descText.setText("Welcome to your Email App!\n\nUse the menu to navigate between:\n• Inbox - View received emails\n• Sent - View sent emails\n• Spam - Check spam folder");
        descText.setTextSize(16);
        descText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        descText.setLineSpacing(8, 1.2f);

        layout.addView(welcomeText);
        layout.addView(descText);

        return layout;
    }
}