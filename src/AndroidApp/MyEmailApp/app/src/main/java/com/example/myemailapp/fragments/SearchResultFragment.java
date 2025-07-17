package com.example.myemailapp.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.myemailapp.utils.EmailActionHandler;
import com.google.android.material.appbar.MaterialToolbar;
//import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.example.myemailapp.R;
import com.example.myemailapp.adapters.EmailAdapter;
import com.example.myemailapp.model.Email;
import com.example.myemailapp.utils.Resource;
import com.example.myemailapp.viewmodel.SearchResultViewModel;
import java.util.ArrayList;
import java.util.List;
//import dagger.hilt.android.AndroidEntryPoint;

public class SearchResultFragment extends Fragment implements EmailAdapter.OnEmailListUpdateListener {

    private RecyclerView recyclerView;
    private EmailAdapter emailAdapter;
    private EmailActionHandler actionHandler;
    private String authToken;
    private List<Email> emailList = new ArrayList<>();
//    private SwipeRefreshLayout swipeRefreshLayout;
//    private LinearProgressIndicator progressIndicator;
    private MaterialToolbar toolbar;

    private SearchResultViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(SearchResultViewModel.class);
        SharedPreferences prefs = requireContext().getSharedPreferences("auth", MODE_PRIVATE);
        authToken = prefs.getString("jwt", "");
        actionHandler = new EmailActionHandler(requireContext(), authToken);
        // Get arguments and set search parameters
        Bundle args = getArguments();
        if (args != null) {
            String searchType = args.getString("search_type", "label");
            String searchTerm = args.getString("search_term", "");
            String labelId = args.getString("label_id", "");

            viewModel.setSearchParameters(searchType, searchTerm, labelId);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_result, container, false);

        initViews(view);
        setupRecyclerView();
//        setupSwipeRefresh();
        observeViewModel();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
//        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
//        progressIndicator = view.findViewById(R.id.progress_indicator);
        toolbar = view.findViewById(R.id.toolbar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Set up back button
//        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
//        toolbar.setNavigationOnClickListener(v -> {
//            if (getActivity() != null) {
//                getActivity().onBackPressed();
//            }
//        });
    }
    private void setupRecyclerView() {
        emailAdapter = new EmailAdapter(emailList, new EmailAdapter.OnEmailClickListener() {
            @Override
            public void onEmailClick(Email email) {
                onMarkReadClick(email);
                Bundle args = new Bundle();
                args.putString("from",      email.getFrom());
                args.putString("subject",   email.getSubject());
                args.putString("timestamp", email.getTimeStamp());
                args.putString("body",      email.getBody());
                // Labels: convert list → comma‐separated string (or empty)
                args.putString("labels",
                        email.getLabels() != null
                                ? TextUtils.join(", ", email.getLabels())
                                : "");

                EmailDetailFragment detailFragment = new EmailDetailFragment();
                detailFragment.setArguments(args);

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, detailFragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onRestoreClick(Email email) {
                // Inbox emails typically don't have a restore action
            }

            @Override
            public void onDeleteClick(Email email) {
                // Use universal action handler for delete
                actionHandler.deleteEmail(email.getId(), new EmailActionHandler.ActionCallback() {
                    @Override
                    public void onSuccess() {
//                        onEmailRemoved(email.getId());
//                        actionHandler.showToast("Email deleted");
                    }

                    @Override
                    public void onError(String error) {
//                        actionHandler.showToast("Delete failed: " + error);
                    }
                });
            }

            @Override
            public void onMarkReadClick(Email email) {
                // Use universal action handler for mark as read
                actionHandler.markAsRead(email.getId(), new EmailActionHandler.ActionCallback() {
                    @Override
                    public void onSuccess() {
//                        onEmailReadStatusChanged(email.getId(), true);
//                        actionHandler.showToast("Marked as read");
                    }

                    @Override
                    public void onError(String error) {
//                        actionHandler.showToast("Mark as read failed: " + error);
                    }
                });
            }

            @Override
            public void onMarkUnreadClick(Email email) {
                // Use universal action handler for mark as unread
                actionHandler.markAsUnread(email.getId(), new EmailActionHandler.ActionCallback() {
                    @Override
                    public void onSuccess() {
//                        onEmailReadStatusChanged(email.getId(), false);
//                        actionHandler.showToast("Marked as unread");
                    }

                    @Override
                    public void onError(String error) {
//                        actionHandler.showToast("Mark as unread failed: " + error);
                    }
                });
            }

            @Override
            public void onSpamClick(Email email) {
                // Use universal action handler for spam
                actionHandler.markAsSpam(email.getId(), new EmailActionHandler.ActionCallback() {
                    @Override
                    public void onSuccess() {
//                        onEmailRemoved(email.getId());
//                        actionHandler.showToast("Marked as spam");
                    }

                    @Override
                    public void onError(String error) {
//                        actionHandler.showToast("Mark as spam failed: " + error);
                    }
                });
            }

            @Override
            public void onLabelsClick(Email email) {
//                showEditLabelsDialog(email);
            }
        }, false, actionHandler, this); // Pass false for isTrash, and pass action handler and listener

        recyclerView.setAdapter(emailAdapter);
    }
//    private void setupRecyclerView() {
//        emailList = new ArrayList<>();
//        emailAdapter = new EmailAdapter(emailList, getContext());
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.setAdapter(emailAdapter);
//    }

//    private void setupSwipeRefresh() {
//        swipeRefreshLayout.setOnRefreshListener(() -> {
//            viewModel.refreshEmails();
//        });
//    }

    private void observeViewModel() {
        // Observe search type for toolbar title
//        viewModel.searchType.observe(getViewLifecycleOwner(), searchType -> {
//            if ("draft".equals(searchType)) {
//                toolbar.setTitle("Drafts");
//            } else {
//                // Will be updated when searchTerm is observed
//            }
//        });

        // Observe search term for toolbar title
        viewModel.searchTerm.observe(getViewLifecycleOwner(), searchTerm -> {
            if (viewModel.searchType.getValue() != null &&
                    !"draft".equals(viewModel.searchType.getValue())) {
                toolbar.setTitle("Label: " + searchTerm);
            }
        });

        // Observe email list changes
        viewModel.emails.observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        showLoading(true);
                        break;

                    case SUCCESS:
                        showLoading(false);
                        updateEmailList(resource.data);
                        break;

                    case ERROR:
                        showLoading(false);
                        showError(resource.message);
                        break;
                }
            }
        });
    }

    private void updateEmailList(List<Email> emails) {
        emailList.clear();
        if (emails != null) {
            emailList.addAll(emails);
        }
        emailAdapter.notifyDataSetChanged();

        // Show empty state if no emails found
        if (emailList.isEmpty()) {
            showEmptyState();
        }
    }

    private void showLoading(boolean show) {
//        if (show) {
//            progressIndicator.setVisibility(View.VISIBLE);
//            progressIndicator.show();
//        } else {
//            progressIndicator.setVisibility(View.GONE);
//            progressIndicator.hide();
////            swipeRefreshLayout.setRefreshing(false);
//        }
    }

    private void showError(String message) {
        Toast.makeText(getContext(), message != null ? message : "An error occurred",
                Toast.LENGTH_LONG).show();
    }

    private void showEmptyState() {
        String searchType = viewModel.searchType.getValue();
        String searchTerm = viewModel.searchTerm.getValue();

        String message = "draft".equals(searchType) ?
                "No drafts found" :
                "No emails found for label: " + searchTerm;
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh emails when returning to fragment
        // This handles the case where labels might have been edited
        viewModel.refreshEmails();
    }

    @Override
    public void onEmailRemoved(String emailId) {

    }

    @Override
    public void onEmailReadStatusChanged(String emailId, boolean isRead) {

    }

    @Override
    public void onEmailStarStatusChanged(String emailId, boolean isStarred) {

    }

    @Override
    public void onEmailLabelsChanged(String emailId, List<String> labels) {

    }
}