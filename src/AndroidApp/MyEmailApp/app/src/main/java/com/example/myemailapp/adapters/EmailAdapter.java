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
//import com.example.myemailapp.utils.EmailActionHandler;
//import java.util.List;
//
//public class EmailAdapter extends RecyclerView.Adapter<EmailAdapter.EmailViewHolder> {
//    private List<Email> emails;
//    private OnEmailClickListener listener;
//    private boolean isTrashContext;
//    private EmailActionHandler actionHandler;
//    private OnEmailListUpdateListener listUpdateListener;
//
//    public interface OnEmailListUpdateListener {
//        void onEmailRemoved(String emailId);
//        void onEmailReadStatusChanged(String emailId, boolean isRead);
//        void onEmailStarStatusChanged(String emailId, boolean isStarred);
//        void onEmailLabelsChanged(String emailId, List<String> labels);
//    }
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
//    public EmailAdapter(List<Email> emails, OnEmailClickListener listener, boolean isTrashContext,
//                        EmailActionHandler actionHandler, OnEmailListUpdateListener listUpdateListener) {
//        this.emails = emails;
//        this.listener = listener;
//        this.isTrashContext = isTrashContext;
//        this.actionHandler = actionHandler;
//        this.listUpdateListener = listUpdateListener;
//    }
//
//    public void updateEmailList(List<Email> newEmails) {
//        this.emails = newEmails;
//        notifyDataSetChanged();
//    }
//
//    @NonNull
//    @Override
//    public EmailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_email, parent, false);
//        return new EmailViewHolder(view, isTrashContext, actionHandler, listUpdateListener);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull EmailViewHolder holder, int position) {
//        holder.bind(emails.get(position), listener);
//    }
//
//    @Override
//    public int getItemCount() {
//        return emails.size();
//    }
//
//    static class EmailViewHolder extends RecyclerView.ViewHolder {
//        private TextView textFrom;
//        private TextView textSubjectBody;
//        private TextView textLabels;
//        private TextView textTimestamp;
//        private ImageButton buttonRestore;
//        private ImageButton buttonDelete;
//        private ImageButton buttonMarkRead;
//        private ImageButton buttonMarkUnread;
//        private ImageButton buttonSpam;
//        private ImageButton buttonLabels;
//        private ImageButton buttonStar;
//        private View itemContainer;
//        private final boolean isTrashContext;
//        private final EmailActionHandler actionHandler;
//        private final OnEmailListUpdateListener listUpdateListener;
//
//        public EmailViewHolder(@NonNull View itemView, boolean isTrashContext,
//                               EmailActionHandler actionHandler, OnEmailListUpdateListener listUpdateListener) {
//            super(itemView);
//            this.isTrashContext = isTrashContext;
//            this.actionHandler = actionHandler;
//            this.listUpdateListener = listUpdateListener;
//
//            textFrom = itemView.findViewById(R.id.text_from);
//            textSubjectBody = itemView.findViewById(R.id.text_subject_body);
//            textLabels = itemView.findViewById(R.id.text_labels);
//            textTimestamp = itemView.findViewById(R.id.text_timestamp);
//            buttonRestore = itemView.findViewById(R.id.button_restore);
//            buttonDelete = itemView.findViewById(R.id.button_delete);
//            buttonMarkRead = itemView.findViewById(R.id.button_mark_read);
//            buttonMarkUnread = itemView.findViewById(R.id.button_mark_unread);
//            buttonSpam = itemView.findViewById(R.id.button_spam);
//            buttonLabels = itemView.findViewById(R.id.button_labels);
//            buttonStar = itemView.findViewById(R.id.button_star);
//            itemContainer = itemView.findViewById(R.id.item_container);
//        }
//
//        public void bind(Email email, OnEmailClickListener listener) {
//            Context ctx = itemView.getContext();
//
//            textFrom.setText(email.getFrom());
//
//            // Combine subject and body for the third line
//            String subject = email.getSubject() != null ? email.getSubject() : "(No Subject)";
//            String body = email.getBody() != null ? email.getBody() : "";
//            String subjectBody = subject + " - " + body;
//            textSubjectBody.setText(subjectBody);
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
//            if ("drafts".equals(email.getTrashSource())) {
//                subjectBody = ctx.getString(R.string.draft_prefix, subject) + " - " + body;
//                textSubjectBody.setText(subjectBody);
//            }
//
//            textTimestamp.setText(email.getTimeStamp());
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
//            // Handle star button
//            if (email.isStarred()) {
//                buttonStar.setImageResource(R.drawable.star_filled);
//            } else {
//                buttonStar.setImageResource(R.drawable.star_outline);
//            }
//
//            // Handle trash context for spam button
//            if (isTrashContext) {
//                buttonSpam.setVisibility(View.VISIBLE);
//            } else {
//                buttonSpam.setVisibility(View.GONE);
//            }
//
//            // Set click listeners using the universal action handler
//            itemContainer.setOnClickListener(v -> listener.onEmailClick(email));
//
//            // Universal delete handler
//            buttonDelete.setOnClickListener(v -> {
//                actionHandler.deleteEmail(email.getId(), new EmailActionHandler.ActionCallback() {
//                    @Override
//                    public void onSuccess() {
//                        listUpdateListener.onEmailRemoved(email.getId());
//                        actionHandler.showToast("Email deleted successfully");
//                    }
//
//                    @Override
//                    public void onError(String error) {
//                        actionHandler.showToast("Delete failed: " + error);
//                    }
//                });
//            });
//
//            // Universal mark as read handler
//            buttonMarkRead.setOnClickListener(v -> {
//                actionHandler.markAsRead(email.getId(), new EmailActionHandler.ActionCallback() {
//                    @Override
//                    public void onSuccess() {
//                        listUpdateListener.onEmailReadStatusChanged(email.getId(), true);
//                        actionHandler.showToast("Marked as read");
//                    }
//
//                    @Override
//                    public void onError(String error) {
//                        actionHandler.showToast("Mark as read failed: " + error);
//                    }
//                });
//            });
//
//            // Universal mark as unread handler
//            buttonMarkUnread.setOnClickListener(v -> {
//                actionHandler.markAsUnread(email.getId(), new EmailActionHandler.ActionCallback() {
//                    @Override
//                    public void onSuccess() {
//                        listUpdateListener.onEmailReadStatusChanged(email.getId(), false);
//                        actionHandler.showToast("Marked as unread");
//                    }
//
//                    @Override
//                    public void onError(String error) {
//                        actionHandler.showToast("Mark as unread failed: " + error);
//                    }
//                });
//            });
//
//            // Universal spam handler
//            buttonSpam.setOnClickListener(v -> {
//                actionHandler.markAsSpam(email.getId(), new EmailActionHandler.ActionCallback() {
//                    @Override
//                    public void onSuccess() {
//                        listUpdateListener.onEmailRemoved(email.getId());
//                        actionHandler.showToast("Marked as spam");
//                    }
//
//                    @Override
//                    public void onError(String error) {
//                        actionHandler.showToast("Mark as spam failed: " + error);
//                    }
//                });
//            });
//
//            // Universal star toggle handler
//            buttonStar.setOnClickListener(v -> {
//                actionHandler.toggleStar(email.getId(), email.isStarred(), new EmailActionHandler.ActionCallback() {
//                    @Override
//                    public void onSuccess() {
//                        listUpdateListener.onEmailStarStatusChanged(email.getId(), !email.isStarred());
//                        actionHandler.showToast(email.isStarred() ? "Unstarred" : "Starred");
//                    }
//
//                    @Override
//                    public void onError(String error) {
//                        actionHandler.showToast("Star toggle failed: " + error);
//                    }
//                });
//            });
//
//            // Keep the original restore and labels handlers
//            buttonRestore.setOnClickListener(v -> listener.onRestoreClick(email));
//            buttonLabels.setOnClickListener(v -> listener.onLabelsClick(email));
//        }
//    }
//}
package com.example.myemailapp.adapters;
import android.content.Context;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myemailapp.R;
import com.example.myemailapp.model.Email;
import com.example.myemailapp.utils.EmailActionHandler;
import java.util.List;

public class EmailAdapter extends RecyclerView.Adapter<EmailAdapter.EmailViewHolder> {
    private List<Email> emails;
    private OnEmailClickListener listener;
    private boolean isTrashContext;
    private EmailActionHandler actionHandler;
    private OnEmailListUpdateListener listUpdateListener;

    public interface OnEmailListUpdateListener {
        void onEmailRemoved(String emailId);
        void onEmailReadStatusChanged(String emailId, boolean isRead);
        void onEmailStarStatusChanged(String emailId, boolean isStarred);
        void onEmailLabelsChanged(String emailId, List<String> labels);
    }

    public interface OnEmailClickListener {
        void onEmailClick(Email email);
        void onRestoreClick(Email email);
        void onDeleteClick(Email email);
        void onMarkReadClick(Email email);
        void onMarkUnreadClick(Email email);
        void onSpamClick(Email email);
        void onLabelsClick(Email email);
    }

    public EmailAdapter(List<Email> emails, OnEmailClickListener listener, boolean isTrashContext,
                        EmailActionHandler actionHandler, OnEmailListUpdateListener listUpdateListener) {
        this.emails = emails;
        this.listener = listener;
        this.isTrashContext = isTrashContext;
        this.actionHandler = actionHandler;
        this.listUpdateListener = listUpdateListener;
    }

    public void updateEmailList(List<Email> newEmails) {
        this.emails = newEmails;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EmailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_email, parent, false);
        return new EmailViewHolder(view, isTrashContext, actionHandler, listUpdateListener);
    }

    @Override
    public void onBindViewHolder(@NonNull EmailViewHolder holder, int position) {
        holder.bind(emails.get(position), listener);
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
        private ImageButton buttonStar;
        private View itemContainer;
        private final boolean isTrashContext;
        private final EmailActionHandler actionHandler;
        private final OnEmailListUpdateListener listUpdateListener;

        public EmailViewHolder(@NonNull View itemView, boolean isTrashContext,
                               EmailActionHandler actionHandler, OnEmailListUpdateListener listUpdateListener) {
            super(itemView);
            this.isTrashContext = isTrashContext;
            this.actionHandler = actionHandler;
            this.listUpdateListener = listUpdateListener;

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
            buttonStar = itemView.findViewById(R.id.button_star);
            itemContainer = itemView.findViewById(R.id.item_container);
        }

        public void bind(Email email, OnEmailClickListener listener) {
            Context ctx = itemView.getContext();

            textFrom.setText(email.getFrom());

            // Combine subject and body for the third line
            String subject = email.getSubject() != null ? email.getSubject() : "(No Subject)";
            String body = email.getBody() != null ? email.getBody() : "";
            String subjectBody = subject + " - " + body;

            // Handle draft indicator with red color for "Draft" prefix
            if ("drafts".equals(email.getTrashSource())) {
                String draftPrefix = ctx.getString(R.string.draft_prefix, ""); // Get prefix without subject
                String fullText = draftPrefix + subject + " - " + body;
                SpannableString spannable = new SpannableString(fullText);
                // Apply red color only to the draft prefix
                spannable.setSpan(
                        new ForegroundColorSpan(ContextCompat.getColor(ctx, android.R.color.holo_red_dark)),
                        0, draftPrefix.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                textSubjectBody.setText(spannable);
            } else {
                textSubjectBody.setText(subjectBody);
            }

            // Handle labels
            if (email.getLabels() != null && !email.getLabels().isEmpty()) {
                textLabels.setText(String.join(", ", email.getLabels()));
                textLabels.setVisibility(View.VISIBLE);
            } else {
                textLabels.setText(ctx.getString(R.string.no_labels));
                textLabels.setVisibility(View.VISIBLE);
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

            // Handle star button
            if (email.isStarred()) {
                buttonStar.setImageResource(R.drawable.star_filled);
            } else {
                buttonStar.setImageResource(R.drawable.star_outline);
            }

            // Handle trash context for spam button
            if (isTrashContext) {
                buttonSpam.setVisibility(View.VISIBLE);
            } else {
                buttonSpam.setVisibility(View.GONE);
            }

            // Set click listeners using the universal action handler
            itemContainer.setOnClickListener(v -> listener.onEmailClick(email));

            // Universal delete handler
            buttonDelete.setOnClickListener(v -> {
                actionHandler.deleteEmail(email.getId(), new EmailActionHandler.ActionCallback() {
                    @Override
                    public void onSuccess() {
                        listUpdateListener.onEmailRemoved(email.getId());
                        actionHandler.showToast("Email deleted successfully");
                    }

                    @Override
                    public void onError(String error) {
                        actionHandler.showToast("Delete failed: " + error);
                    }
                });
            });

            // Universal mark as read handler
            buttonMarkRead.setOnClickListener(v -> {
                actionHandler.markAsRead(email.getId(), new EmailActionHandler.ActionCallback() {
                    @Override
                    public void onSuccess() {
                        listUpdateListener.onEmailReadStatusChanged(email.getId(), true);
                        actionHandler.showToast("Marked as read");
                    }

                    @Override
                    public void onError(String error) {
                        actionHandler.showToast("Mark as read failed: " + error);
                    }
                });
            });

            // Universal mark as unread handler
            buttonMarkUnread.setOnClickListener(v -> {
                actionHandler.markAsUnread(email.getId(), new EmailActionHandler.ActionCallback() {
                    @Override
                    public void onSuccess() {
                        listUpdateListener.onEmailReadStatusChanged(email.getId(), false);
                        actionHandler.showToast("Marked as unread");
                    }

                    @Override
                    public void onError(String error) {
                        actionHandler.showToast("Mark as unread failed: " + error);
                    }
                });
            });

            // Universal spam handler
            buttonSpam.setOnClickListener(v -> {
                actionHandler.markAsSpam(email.getId(), new EmailActionHandler.ActionCallback() {
                    @Override
                    public void onSuccess() {
                        listUpdateListener.onEmailRemoved(email.getId());
                        actionHandler.showToast("Marked as spam");
                    }

                    @Override
                    public void onError(String error) {
                        actionHandler.showToast("Mark as spam failed: " + error);
                    }
                });
            });

            // Universal star toggle handler
            buttonStar.setOnClickListener(v -> {
                actionHandler.toggleStar(email.getId(), email.isStarred(), new EmailActionHandler.ActionCallback() {
                    @Override
                    public void onSuccess() {
                        listUpdateListener.onEmailStarStatusChanged(email.getId(), !email.isStarred());
                        actionHandler.showToast(email.isStarred() ? "Unstarred" : "Starred");
                    }

                    @Override
                    public void onError(String error) {
                        actionHandler.showToast("Star toggle failed: " + error);
                    }
                });
            });

            // Keep the original restore and labels handlers
            buttonRestore.setOnClickListener(v -> listener.onRestoreClick(email));
            buttonLabels.setOnClickListener(v -> listener.onLabelsClick(email));
        }
    }
}