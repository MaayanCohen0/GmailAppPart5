package com.example.myemailapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myemailapp.model.Email;
import com.example.myemailapp.repository.InboxRepository;

import java.util.List;

public class InboxViewModel extends AndroidViewModel {
    private InboxRepository inboxRepository;
    private MutableLiveData<Boolean> shouldShowEmpty = new MutableLiveData<>();

    public InboxViewModel(@NonNull Application application) {
        super(application);
        inboxRepository = new InboxRepository(application.getApplicationContext());
        shouldShowEmpty.setValue(false);
    }

    public LiveData<List<Email>> getInboxEmails() {
        return inboxRepository.getInboxEmails();
    }

    public LiveData<String> getErrorMessage() {
        return inboxRepository.getErrorMessage();
    }

    public LiveData<Boolean> getIsLoading() {
        return inboxRepository.getIsLoading();
    }

    public LiveData<Boolean> getShouldShowEmpty() {
        return shouldShowEmpty;
    }

    public void loadInboxEmails() {
        inboxRepository.loadInboxEmails();
    }

    public void deleteFromInbox(Email email) {
        inboxRepository.deleteFromInbox(email);
    }

    public void markAsRead(Email email) {
        inboxRepository.markAsRead(email);
    }

    public void markAsUnread(Email email) {
        inboxRepository.markAsUnread(email);
    }

    public void markAsSpam(Email email) {
        inboxRepository.markAsSpam(email);
    }

    public void editLabels(Email email, List<String> newLabels) {
        inboxRepository.editLabels(email, newLabels);
    }

    public void updateEmptyState(List<Email> emails) {
        shouldShowEmpty.setValue(emails == null || emails.isEmpty());
    }
}