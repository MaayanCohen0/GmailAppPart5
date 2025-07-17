package com.example.myemailapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.myemailapp.model.Email;
import com.example.myemailapp.repository.InboxRepository;
import com.example.myemailapp.utils.Resource;
import java.util.List;

public class SearchResultViewModel extends AndroidViewModel {

    private final InboxRepository emailRepository;

    private final MutableLiveData<Resource<List<Email>>> _emails = new MutableLiveData<>();
    public final LiveData<Resource<List<Email>>> emails = _emails;

    private final MutableLiveData<String> _searchType = new MutableLiveData<>();
    public final LiveData<String> searchType = _searchType;

    private final MutableLiveData<String> _searchTerm = new MutableLiveData<>();
    public final LiveData<String> searchTerm = _searchTerm;

    public SearchResultViewModel(@NonNull Application application) {
        super(application);
        this.emailRepository = new InboxRepository(application.getApplicationContext());
//        shouldShowEmpty
    }

    public void setSearchParameters(String searchType, String searchTerm, String labelId) {
        _searchType.setValue(searchType);
        _searchTerm.setValue(searchTerm);
        loadEmails(searchType, searchTerm);
    }

    public void loadEmails(String searchType, String searchTerm) {
        _emails.setValue(Resource.loading(null));
        emailRepository.searchEmailsByLabel(searchTerm);
//        emailRepository.searchEmailsByLabel(searchTerm, new InboxRepository.ApiCallback<List<Email>>() {
//            @Override
//            public void onSuccess(List<Email> result) {
//                _emails.setValue(Resource.success(result));
//            }
//
//            @Override
//            public void onError(String error) {
//                _emails.setValue(Resource.error(error, null));
//            }
//        });
    }

    public void refreshEmails() {
        String currentSearchType = _searchType.getValue();
        String currentSearchTerm = _searchTerm.getValue();
        if (currentSearchType != null && currentSearchTerm != null) {
            loadEmails(currentSearchType, currentSearchTerm);
        }
    }
}