////
////package com.example.myemailapp.adapters;
////import android.content.Context;
////import android.view.LayoutInflater;
////import android.view.View;
////import android.view.ViewGroup;
////import android.widget.Button;
////import android.widget.ImageButton;
////import android.widget.TextView;
////
////import androidx.annotation.NonNull;
////import androidx.recyclerview.widget.RecyclerView;
////
////import com.example.myemailapp.R;
////import com.example.myemailapp.model.Email;
////
////import java.util.List;
////
////public class EmailAdapter extends RecyclerView.Adapter<EmailAdapter.EmailViewHolder> {
////    private List<Email> emails;
////    private OnEmailClickListener listener;
////    private final boolean isTrashContext; // Make it final to ensure it's set once
////
////    public interface OnEmailClickListener {
////        void onEmailClick(Email email);
////        void onRestoreClick(Email email);
////        void onDeleteClick(Email email);
////        void onMarkReadClick(Email email);
////        void onMarkUnreadClick(Email email);
////        void onSpamClick(Email email);
////        void onLabelsClick(Email email);
////    }
////
////    // Update constructor to accept isTrashContext as a parameter
////    public EmailAdapter(List<Email> emails, OnEmailClickListener listener, boolean isTrashContext) {
////        this.emails = emails;
////        this.listener = listener;
////        this.isTrashContext = isTrashContext; // Assign the parameter to the instance variable
////    }
////
////    @NonNull
////    @Override
////    public EmailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
////        View view = LayoutInflater.from(parent.getContext())
////                .inflate(R.layout.item_email, parent, false);
////        return new EmailViewHolder(view, isTrashContext); // Pass isTrashContext to ViewHolder
////    }
////
////    @Override
////    public void onBindViewHolder(@NonNull EmailViewHolder holder, int position) {
////        Email email = emails.get(position);
////        holder.bind(email, listener);
////    }
////
////    @Override
////    public int getItemCount() {
////        return emails.size();
////    }
////
////    static class EmailViewHolder extends RecyclerView.ViewHolder {
////        private TextView textFrom;
////        private TextView textSubject;
////        private TextView textBody;
////        private TextView textTimestamp;
////        private TextView textLabels;
////        private ImageButton buttonRestore;
////        private ImageButton buttonDelete;
////        private ImageButton buttonMarkRead;
////        private ImageButton buttonMarkUnread;
////        private ImageButton buttonSpam;
////        private ImageButton buttonLabels;
////        private View itemContainer;
////        private final boolean isTrashContext; // Store isTrashContext in ViewHolder
////
////        public EmailViewHolder(@NonNull View itemView, boolean isTrashContext) {
////            super(itemView);
////            this.isTrashContext = isTrashContext; // Initialize the field
////            textFrom = itemView.findViewById(R.id.text_from);
////            textSubject = itemView.findViewById(R.id.text_subject);
////            textBody = itemView.findViewById(R.id.text_body);
////            textTimestamp = itemView.findViewById(R.id.text_timestamp);
////            textLabels = itemView.findViewById(R.id.text_labels);
////            buttonRestore = itemView.findViewById(R.id.button_restore);
////            buttonDelete = itemView.findViewById(R.id.button_delete);
////            buttonMarkRead = itemView.findViewById(R.id.button_mark_read);
////            buttonMarkUnread = itemView.findViewById(R.id.button_mark_unread);
////            buttonSpam = itemView.findViewById(R.id.button_spam);
////            buttonLabels = itemView.findViewById(R.id.button_labels);
////            itemContainer = itemView.findViewById(R.id.item_container);
////        }
////
////        public void bind(Email email, OnEmailClickListener listener) {
////
////            Context ctx = itemView.getContext();
////
////            textFrom.setText(email.getFrom());
////            textSubject.setText(email.getSubject() != null ? email.getSubject() : "(No Subject)");
////            textBody.setText(email.getBody());
////            textTimestamp.setText(email.getTimeStamp());
////
////            // Handle labels
////            if (email.getLabels() != null && !email.getLabels().isEmpty()) {
////                textLabels.setText(String.join(", ", email.getLabels()));
////                textLabels.setVisibility(View.VISIBLE);
////            } else {
////                textLabels.setText(ctx.getString(R.string.no_labels));
////                textLabels.setVisibility(View.VISIBLE);
////            }
////
////            // Show draft indicator if from drafts
////            // Subject
////            String subj = email.getSubject() != null
////                    ? email.getSubject()
////                    : ctx.getString(R.string.no_subject);
////            // Draft?
////            if ("drafts".equals(email.getTrashSource())) {
////                subj = ctx.getString(R.string.draft_prefix, subj);
////            }
////            textSubject.setText(subj);
////
////            // Change appearance based on read status
////            if (email.isRead()) {
////                itemContainer.setAlpha(0.7f);
////                buttonMarkRead.setText(ctx.getString(R.string.mark_unread));
////            } else {
////                itemContainer.setAlpha(1.0f);
////                buttonMarkRead.setText(ctx.getString(R.string.mark_read));
////            }
////
////            // Use the ViewHolder's isTrashContext
////            if (isTrashContext) {
////                buttonSpam.setVisibility(View.VISIBLE);
////                buttonSpam.setOnClickListener(v -> listener.onSpamClick(email));
////            } else {
////                buttonSpam.setVisibility(View.GONE);
////            }
////
////            // Set click listeners
////            itemContainer.setOnClickListener(v -> listener.onEmailClick(email));
////            buttonRestore.setOnClickListener(v -> listener.onRestoreClick(email));
////            buttonDelete.setOnClickListener(v -> listener.onDeleteClick(email));
////            buttonMarkRead.setOnClickListener(v -> listener.onMarkReadClick(email));
////            buttonMarkUnread.setOnClickListener(v -> listener.onMarkUnreadClick(email));
////            // Uncomment if needed: buttonSpam.setOnClickListener(v -> listener.onSpamClick(email));
////            buttonLabels.setOnClickListener(v -> listener.onLabelsClick(email));
////        }
////    }
////}
//
//package com.example.myemailapp.adapters;
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageButton;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.myemailapp.R;
//import com.example.myemailapp.model.Email;
//
//import java.util.List;
//
//public class EmailAdapter extends RecyclerView.Adapter<EmailAdapter.EmailViewHolder> {
//    private List<Email> emails;
//    private OnEmailClickListener listener;
//    private final boolean isTrashContext;
//
//    public interface OnEmailClickListener {
//        void onEmailClick(Email email);
//        void onRestoreClick(Email email);
//        void onDeleteClick(Email email);
//        void onMarkReadClick(Email email);
//        void onMarkUnreadClick(Email email);
//        void onSpamClick(Email email);
//        void onLabelsClick(Email email);
//    }
//
//    public EmailAdapter(List<Email> emails, OnEmailClickListener listener, boolean isTrashContext) {
//        this.emails = emails;
//        this.listener = listener;
//        this.isTrashContext = isTrashContext;
//    }
//
//    @NonNull
//    @Override
//    public EmailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_email, parent, false);
//        return new EmailViewHolder(view, isTrashContext);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull EmailViewHolder holder, int position) {
//        Email email = emails.get(position);
//        holder.bind(email, listener);
//    }
//
//    @Override
//    public int getItemCount() {
//        return emails.size();
//    }
//
//    static class EmailViewHolder extends RecyclerView.ViewHolder {
//        private TextView textFrom;
//        private TextView textSubject;
//        private TextView textBody;
//        private TextView textTimestamp;
//        private TextView textLabels;
//        private ImageButton buttonRestore;
//        private ImageButton buttonDelete;
//        private ImageButton buttonMarkRead;
//        private ImageButton buttonMarkUnread;
//        private ImageButton buttonSpam;
//        private ImageButton buttonLabels;
//        private View itemContainer;
//        private final boolean isTrashContext;
//
//        public EmailViewHolder(@NonNull View itemView, boolean isTrashContext) {
//            super(itemView);
//            this.isTrashContext = isTrashContext;
//            textFrom = itemView.findViewById(R.id.text_from);
//            textSubject = itemView.findViewById(R.id.text_subject);
//            textBody = itemView.findViewById(R.id.text_body);
//            textTimestamp = itemView.findViewById(R.id.text_timestamp);
//            textLabels = itemView.findViewById(R.id.text_labels);
//            buttonRestore = itemView.findViewById(R.id.button_restore);
//            buttonDelete = itemView.findViewById(R.id.button_delete);
//            buttonMarkRead = itemView.findViewById(R.id.button_mark_read);
//            buttonMarkUnread = itemView.findViewById(R.id.button_mark_unread);
//            buttonSpam = itemView.findViewById(R.id.button_spam);
//            buttonLabels = itemView.findViewById(R.id.button_labels);
//            itemContainer = itemView.findViewById(R.id.item_container);
//        }
//
//        public void bind(Email email, OnEmailClickListener listener) {
//            Context ctx = itemView.getContext();
//
//            textFrom.setText(email.getFrom());
//            textSubject.setText(email.getSubject() != null ? email.getSubject() : "(No Subject)");
//            textBody.setText(email.getBody());
//            textTimestamp.setText(email.getTimeStamp());
//
//            // Handle labels
//            if (email.getLabels() != null && !email.getLabels().isEmpty()) {
//                textLabels.setText(String.join(", ", email.getLabels()));
//                textLabels.setVisibility(View.VISIBLE);
//            } else {
//                textLabels.setText(ctx.getString(R.string.no_labels));
//                textLabels.setVisibility(View.VISIBLE);
//            }
//
//            // Handle draft indicator
//            String subj = email.getSubject() != null
//                    ? email.getSubject()
//                    : ctx.getString(R.string.no_subject);
//            if ("drafts".equals(email.getTrashSource())) {
//                subj = ctx.getString(R.string.draft_prefix, subj);
//            }
//            textSubject.setText(subj);
//
//            // Toggle visibility based on read status
//            if (email.isRead()) {
//                itemContainer.setAlpha(0.7f);
//                buttonMarkRead.setVisibility(View.GONE);
//                buttonMarkUnread.setVisibility(View.VISIBLE);
//            } else {
//                itemContainer.setAlpha(1.0f);
//                buttonMarkRead.setVisibility(View.VISIBLE);
//                buttonMarkUnread.setVisibility(View.GONE);
//            }
//
//            // Handle trash context for spam button
//            if (isTrashContext) {
//                buttonSpam.setVisibility(View.VISIBLE);
//                buttonSpam.setOnClickListener(v -> listener.onSpamClick(email));
//            } else {
//                buttonSpam.setVisibility(View.GONE);
//            }
//
//            // Set click listeners
//            itemContainer.setOnClickListener(v -> listener.onEmailClick(email));
//            buttonRestore.setOnClickListener(v -> listener.onRestoreClick(email));
//            buttonDelete.setOnClickListener(v -> listener.onDeleteClick(email));
//            buttonMarkRead.setOnClickListener(v -> listener.onMarkReadClick(email));
//            buttonMarkUnread.setOnClickListener(v -> listener.onMarkUnreadClick(email));
//            buttonLabels.setOnClickListener(v -> listener.onLabelsClick(email));
//        }
//    }
//}

package com.example.myemailapp.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myemailapp.R;
import com.example.myemailapp.model.Email;

import java.util.List;

public class EmailAdapter extends RecyclerView.Adapter<EmailAdapter.EmailViewHolder> {
    private List<Email> emails;
    private OnEmailClickListener listener;
    private final boolean isTrashContext;

    public interface OnEmailClickListener {
        void onEmailClick(Email email);
        void onRestoreClick(Email email);
        void onDeleteClick(Email email);
        void onMarkReadClick(Email email);
        void onMarkUnreadClick(Email email);
        void onSpamClick(Email email);
        void onLabelsClick(Email email);
    }

    public EmailAdapter(List<Email> emails, OnEmailClickListener listener, boolean isTrashContext) {
        this.emails = emails;
        this.listener = listener;
        this.isTrashContext = isTrashContext;
    }

    @NonNull
    @Override
    public EmailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_email, parent, false);
        return new EmailViewHolder(view, isTrashContext);
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
        private TextView textSubjectBody;
        private TextView textLabels;
        private TextView textTimestamp;
        private ImageButton buttonRestore;
        private ImageButton buttonDelete;
        private ImageButton buttonMarkRead;
        private ImageButton buttonMarkUnread;
        private ImageButton buttonSpam;
        private ImageButton buttonLabels;
        private View itemContainer;
        private final boolean isTrashContext;

        public EmailViewHolder(@NonNull View itemView, boolean isTrashContext) {
            super(itemView);
            this.isTrashContext = isTrashContext;
            textFrom = itemView.findViewById(R.id.text_from);
            textSubjectBody = itemView.findViewById(R.id.text_subject_body);
            textLabels = itemView.findViewById(R.id.text_labels);
            textTimestamp = itemView.findViewById(R.id.text_timestamp);
            buttonRestore = itemView.findViewById(R.id.button_restore);
            buttonDelete = itemView.findViewById(R.id.button_delete);
            buttonMarkRead = itemView.findViewById(R.id.button_mark_read);
            buttonMarkUnread = itemView.findViewById(R.id.button_mark_unread);
            buttonSpam = itemView.findViewById(R.id.button_spam);
            buttonLabels = itemView.findViewById(R.id.button_labels);
            itemContainer = itemView.findViewById(R.id.item_container);
        }

        public void bind(Email email, OnEmailClickListener listener) {
            Context ctx = itemView.getContext();

            textFrom.setText(email.getFrom());

            // Combine subject and body for the third line
            String subject = email.getSubject() != null ? email.getSubject() : "(No Subject)";
            String body = email.getBody() != null ? email.getBody() : "";
            String subjectBody = subject + " - " + body;
            textSubjectBody.setText(subjectBody);

            // Handle labels
            if (email.getLabels() != null && !email.getLabels().isEmpty()) {
                textLabels.setText(String.join(", ", email.getLabels()));
                textLabels.setVisibility(View.VISIBLE);
            } else {
                textLabels.setText(ctx.getString(R.string.no_labels));
                textLabels.setVisibility(View.VISIBLE);
            }

            // Handle draft indicator
            if ("drafts".equals(email.getTrashSource())) {
                subjectBody = ctx.getString(R.string.draft_prefix, subject) + " - " + body;
                textSubjectBody.setText(subjectBody);
            }

            textTimestamp.setText(email.getTimeStamp());

            // Toggle visibility based on read status
            if (email.isRead()) {
                itemContainer.setAlpha(0.7f);
                buttonMarkRead.setVisibility(View.GONE);
                buttonMarkUnread.setVisibility(View.VISIBLE);
            } else {
                itemContainer.setAlpha(1.0f);
                buttonMarkRead.setVisibility(View.VISIBLE);
                buttonMarkUnread.setVisibility(View.GONE);
            }

            // Handle trash context for spam button
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
            buttonLabels.setOnClickListener(v -> listener.onLabelsClick(email));
        }
    }
}