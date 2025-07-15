package com.example.myemailapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class LabelsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Create the layout programmatically
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 60, 40, 40);

        // Title
        TextView titleText = new TextView(getContext());
        titleText.setText("Labels");
        titleText.setTextSize(24);
        titleText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        titleText.setPadding(0, 0, 0, 30);
        titleText.setTextColor(0xFFFF5722); // Orange-red color

        // Content
        TextView contentText = new TextView(getContext());
        contentText.setText("Labels yayyyy");
        contentText.setTextSize(16);
        contentText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        contentText.setLineSpacing(8, 1.2f);

        layout.addView(titleText);
        layout.addView(contentText);

        return layout;
    }
}