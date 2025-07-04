package com.example.myemailapp.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Email {
    @SerializedName("id")
    private String id;

    @SerializedName("from")
    private String from;

    @SerializedName("to")
    private List<String> to;

    @SerializedName("subject")
    private String subject;

    @SerializedName("body")
    private String body;

    @SerializedName("timeStamp")
    private String timeStamp;

    @SerializedName("isRead")
    private boolean isRead;

    @SerializedName("labels")
    private List<String> labels;

    // Nullable: only set for emails retrieved from Trash
    @SerializedName("trashSource")
    private String trashSource;

    // Constructors
    public Email() {}

    public Email(String id, String from, List<String> to, String subject, String body,
                 String timeStamp, boolean isRead, List<String> labels) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.timeStamp = timeStamp;
        this.isRead = isRead;
        this.labels = labels;
    }

    // Optional constructor with trashSource
    public Email(String id, String from, List<String> to, String subject, String body,
                 String timeStamp, boolean isRead, List<String> labels, String trashSource) {
        this(id, from, to, subject, body, timeStamp, isRead, labels);
        this.trashSource = trashSource;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }

    public List<String> getTo() { return to; }
    public void setTo(List<String> to) { this.to = to; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getTimeStamp() { return timeStamp; }
    public void setTimeStamp(String timeStamp) { this.timeStamp = timeStamp; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public List<String> getLabels() { return labels; }
    public void setLabels(List<String> labels) { this.labels = labels; }

    // Nullable trashSource
    public String getTrashSource() { return trashSource; }

    public boolean isFromTrash() {
        return trashSource != null;
    }

    @Override
    public String toString() {
        return "Email{" +
                "id='" + id + '\'' +
                ", from='" + from + '\'' +
                ", subject='" + subject + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", isRead=" + isRead +
                ", trashSource=" + trashSource +
                '}';
    }
}
