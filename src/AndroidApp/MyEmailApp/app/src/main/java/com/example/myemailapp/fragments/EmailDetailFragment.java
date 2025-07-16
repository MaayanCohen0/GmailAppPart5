
package com.example.myemailapp.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.myemailapp.R;
import com.example.myemailapp.model.Email;
import com.example.myemailapp.utils.EmailActionHandler;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class EmailDetailFragment extends Fragment {

    private static final String TAG = "EmailDetailFragment";
    private TextView textFrom, textSubject, textTimestamp, textLabels, textBody;
    private Button buttonBack;
    private EmailActionHandler actionHandler;

    public EmailDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_email_detail, container, false);
        textFrom      = view.findViewById(R.id.text_from);
        textSubject   = view.findViewById(R.id.text_subject);
        textTimestamp = view.findViewById(R.id.text_timestamp);
        textLabels    = view.findViewById(R.id.text_labels);
        textBody      = view.findViewById(R.id.text_body);
        buttonBack    = view.findViewById(R.id.button_back);
        return view;
    }

    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        Bundle args = getArguments();
//        if (args == null) return;
//
//        // 1) Read the strings out of the bundle
//        String from      = args.getString("from",      "(no sender)");
//        String subject   = args.getString("subject",   "(no subject)");
//        String timestamp = args.getString("timestamp", "");
//        String body      = args.getString("body",      "");
//        String labels    = args.getString("labels",    "");
//
//        // 2) Bind them to the views
//        textFrom.setText(from);
//        textSubject.setText(subject);
//        textTimestamp.setText(timestamp);
//        textBody.setText(body);
//
//        if (!labels.isEmpty()) {
//            textLabels.setText(labels);
//        } else {
//            textLabels.setText("No labels");
//        }
//
//        // 3) Wire up the back button
//        buttonBack.setOnClickListener(v ->
//                requireActivity().getSupportFragmentManager().popBackStack()
//        );
//    }
//    private void bindEmail(Email email) {
//        textFrom.setText(email.getFrom());
//        textSubject.setText(email.getSubject() != null
//                ? email.getSubject()
//                : "(No Subject)");
//        textTimestamp.setText(email.getTimeStamp());
//        textBody.setText(email.getBody() != null
//                ? email.getBody()
//                : "");
//        // Labels: join or show “none”
//        if (email.getLabels() != null && !email.getLabels().isEmpty()) {
//            textLabels.setText(TextUtils.join(", ", email.getLabels()));
//        } else {
//            textLabels.setText("No labels");
//        }
//    }


    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args == null) return;

        // 1) Read the strings out of the bundle
        String from      = args.getString("from",      "(no sender)");
        String subject   = args.getString("subject",   "(no subject)");
        String timestamp = args.getString("timestamp", "");
        String body      = args.getString("body",      "");
        String labels    = args.getString("labels",    "");

        // 2) Bind them to the views with prefixes
        textFrom.setText("From: " + from);
        textSubject.setText("Subject: " + subject);
        textTimestamp.setText("Timestamp: " + timestamp);
        textBody.setText("Body: " + body);
        if (!labels.isEmpty()) {
            textLabels.setText("Labels: " + labels);
        } else {
            textLabels.setText("Labels: No labels");
        }

        // 3) Wire up the back button
        buttonBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );
    }

    private void bindEmail(Email email) {
        textFrom.setText(getString(R.string.email_from, email.getFrom()));

        String subject = email.getSubject();
        if (subject == null) {
            textSubject.setText(getString(R.string.email_subject, getString(R.string.email_no_subject)));
        } else {
            textSubject.setText(getString(R.string.email_subject, subject));
        }

        textTimestamp.setText(getString(R.string.email_timestamp, email.getTimeStamp()));

        String body = email.getBody();
        if (body == null) {
            textBody.setText(getString(R.string.email_body, "")); // Or consider a "no body" string if needed
        } else {
            textBody.setText(getString(R.string.email_body, body));
        }

        // Labels: join or show “none”
        if (email.getLabels() != null && !email.getLabels().isEmpty()) {
            textLabels.setText(getString(R.string.email_labels, TextUtils.join(", ", email.getLabels())));
        } else {
            textLabels.setText(getString(R.string.email_no_labels));
        }
    }
    // Add a setter for actionHandler if you don't have one
    public void setActionHandler(EmailActionHandler actionHandler) {
        this.actionHandler = actionHandler;
        Log.d(TAG, "ActionHandler set: " + (actionHandler != null));
    }
}