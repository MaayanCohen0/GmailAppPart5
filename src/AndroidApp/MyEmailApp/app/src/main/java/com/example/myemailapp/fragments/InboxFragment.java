package com.example.myemailapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class InboxFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Create the layout programmatically
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 60, 40, 40);

        // Title
        TextView titleText = new TextView(getContext());
        titleText.setText("📥 Inbox");
        titleText.setTextSize(24);
        titleText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        titleText.setPadding(0, 0, 0, 30);
        titleText.setTextColor(0xFF4CAF50); // Green color

        // Content
        TextView contentText = new TextView(getContext());
        contentText.setText("Your inbox is currently empty.\n\nNew emails will appear here when received.");
        contentText.setTextSize(16);
        contentText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        contentText.setLineSpacing(8, 1.2f);

        // Add sample emails (you can replace this with real data later)
        TextView sampleEmails = new TextView(getContext());
        sampleEmails.setText("\n\n📧 Sample Email 1\nFrom: example@email.com\nSubject: Welcome to Email App\n\n📧 Sample Email 2\nFrom: notifications@app.com\nSubject: Your account is ready");
        sampleEmails.setTextSize(14);
        sampleEmails.setPadding(20, 20, 20, 20);
        sampleEmails.setBackgroundColor(0xFFF5F5F5); // Light gray background

        layout.addView(titleText);
        layout.addView(contentText);
        layout.addView(sampleEmails);

        return layout;
    }
}