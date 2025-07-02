
package com.example.myemailapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myemailapp.R;
import com.example.myemailapp.model.Email;

import java.util.List;

public class EmailAdapter extends RecyclerView.Adapter<EmailAdapter.EmailViewHolder> {
    private List<Email> emails;
    private OnEmailClickListener listener;
    private final boolean isTrashContext; // Make it final to ensure it's set once

    public interface OnEmailClickListener {
        void onEmailClick(Email email);
        void onRestoreClick(Email email);
        void onDeleteClick(Email email);
        void onMarkReadClick(Email email);
        void onMarkUnreadClick(Email email);
        void onSpamClick(Email email);
        void onLabelsClick(Email email);
    }

    // Update constructor to accept isTrashContext as a parameter
    public EmailAdapter(List<Email> emails, OnEmailClickListener listener, boolean isTrashContext) {
        this.emails = emails;
        this.listener = listener;
        this.isTrashContext = isTrashContext; // Assign the parameter to the instance variable
    }

    @NonNull
    @Override
    public EmailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_email, parent, false);
        return new EmailViewHolder(view, isTrashContext); // Pass isTrashContext to ViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull EmailViewHolder holder, int position) {
        Email email = emails.get(position);
        holder.bind(email, listener);
    }

    @Override
    public int getItemCount() {
        return emails.size();
    }

    static class EmailViewHolder extends RecyclerView.ViewHolder {
        private TextView textFrom;
        private TextView textSubject;
        private TextView textBody;
        private TextView textTimestamp;
        private TextView textLabels;
        private Button buttonRestore;
        private Button buttonDelete;
        private Button buttonMarkRead;
        private Button buttonMarkUnread;
        private Button buttonSpam;
        private Button buttonLabels;
        private View itemContainer;
        private final boolean isTrashContext; // Store isTrashContext in ViewHolder

        public EmailViewHolder(@NonNull View itemView, boolean isTrashContext) {
            super(itemView);
            this.isTrashContext = isTrashContext; // Initialize the field
            textFrom = itemView.findViewById(R.id.text_from);
            textSubject = itemView.findViewById(R.id.text_subject);
            textBody = itemView.findViewById(R.id.text_body);
            textTimestamp = itemView.findViewById(R.id.text_timestamp);
            textLabels = itemView.findViewById(R.id.text_labels);
            buttonRestore = itemView.findViewById(R.id.button_restore);
            buttonDelete = itemView.findViewById(R.id.button_delete);
            buttonMarkRead = itemView.findViewById(R.id.button_mark_read);
            buttonMarkUnread = itemView.findViewById(R.id.button_mark_unread);
            buttonSpam = itemView.findViewById(R.id.button_spam);
            buttonLabels = itemView.findViewById(R.id.button_labels);
            itemContainer = itemView.findViewById(R.id.item_container);
        }

        public void bind(Email email, OnEmailClickListener listener) {
            textFrom.setText(email.getFrom());
            textSubject.setText(email.getSubject() != null ? email.getSubject() : "(No Subject)");
            textBody.setText(email.getBody());
            textTimestamp.setText(email.getTimeStamp());

            // Handle labels
            if (email.getLabels() != null && !email.getLabels().isEmpty()) {
                textLabels.setText(String.join(", ", email.getLabels()));
                textLabels.setVisibility(View.VISIBLE);
            } else {
                textLabels.setText("No labels");
                textLabels.setVisibility(View.VISIBLE);
            }

            // Show draft indicator if from drafts
            if ("drafts".equals(email.getTrashSource())) {
                textSubject.setText("Draft: " + textSubject.getText());
            }

            // Change appearance based on read status
            if (email.isRead()) {
                itemContainer.setAlpha(0.7f);
                buttonMarkRead.setText("Mark Unread");
            } else {
                itemContainer.setAlpha(1.0f);
                buttonMarkRead.setText("Mark Read");
            }

            // Use the ViewHolder's isTrashContext
            if (isTrashContext) {
                buttonSpam.setVisibility(View.VISIBLE);
                buttonSpam.setOnClickListener(v -> listener.onSpamClick(email));
            } else {
                buttonSpam.setVisibility(View.GONE);
            }

            // Set click listeners
            itemContainer.setOnClickListener(v -> listener.onEmailClick(email));
            buttonRestore.setOnClickListener(v -> listener.onRestoreClick(email));
            buttonDelete.setOnClickListener(v -> listener.onDeleteClick(email));
            buttonMarkRead.setOnClickListener(v -> listener.onMarkReadClick(email));
            buttonMarkUnread.setOnClickListener(v -> listener.onMarkUnreadClick(email));
            // Uncomment if needed: buttonSpam.setOnClickListener(v -> listener.onSpamClick(email));
            buttonLabels.setOnClickListener(v -> listener.onLabelsClick(email));
        }
    }
}