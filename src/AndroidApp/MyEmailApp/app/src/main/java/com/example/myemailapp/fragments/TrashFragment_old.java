////
////package com.example.myemailapp.fragments;
////import android.widget.ImageView;
////import android.widget.TextView;
////import android.content.SharedPreferences;
////import android.os.Bundle;
////import android.util.Log;
////import android.view.LayoutInflater;
////import android.view.View;
////import android.view.ViewGroup;
////import android.widget.Toast;
////
////import androidx.annotation.NonNull;
////import androidx.annotation.Nullable;
////import androidx.fragment.app.Fragment;
////import androidx.recyclerview.widget.LinearLayoutManager;
////import androidx.recyclerview.widget.RecyclerView;
////
////import com.example.myemailapp.R;
////import com.example.myemailapp.adapters.EmailAdapter;
////import com.example.myemailapp.model.Email;
////import com.example.myemailapp.network.ApiService;
////import com.example.myemailapp.network.TrashService;
////
////
////import java.util.ArrayList;
////import java.util.List;
////import androidx.appcompat.app.AlertDialog;
////
////import retrofit2.Retrofit;
////import retrofit2.converter.gson.GsonConverterFactory;
////import static android.content.Context.MODE_PRIVATE;
////public class TrashFragment extends Fragment {
////    private static final String TAG = "TrashFragment";
////    private RecyclerView recyclerView;
////    private EmailAdapter emailAdapter;
////    private List<Email> trashEmails;
////    private TrashService service;
////    private String authToken;
////    private TextView emptyText;
////    private TextView titleText;
////    private android.os.Handler handler;
////    private Runnable refreshRunnable;
////    private static final long REFRESH_INTERVAL = 2000; // 2 seconds
////
////
////    @Override
////    public void onCreate(@Nullable Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        SharedPreferences prefs = requireContext().getSharedPreferences("auth", MODE_PRIVATE);
////        authToken = "Bearer " + prefs.getString("jwt", "");
////
////        Retrofit retrofit = new Retrofit.Builder()
////                .baseUrl("http://10.0.2.2:8080/api/")
////                .addConverterFactory(GsonConverterFactory.create())
////                .build();
////
////        service = new TrashService(retrofit);
////        trashEmails = new ArrayList<>();
////    }
////
////    @Override
////    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
////                             Bundle savedInstanceState) {
////        View view = inflater.inflate(R.layout.fragment_trash, container, false);
////        recyclerView = view.findViewById(R.id.recycler_view_trash);
////        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
////        emptyText = view.findViewById(R.id.empty_trash_text);
////        titleText = view.findViewById(R.id.trash_title);
////
////        emailAdapter = new EmailAdapter(trashEmails, new EmailAdapter.OnEmailClickListener() {
////            @Override public void onEmailClick(Email email) { }
////            @Override public void onRestoreClick(Email email) { restoreFromTrash(email); }
////            @Override public void onDeleteClick(Email email) { delete(email); }
////            @Override public void onMarkReadClick(Email email) { markRead(email); }
////            @Override public void onMarkUnreadClick(Email email) { markUnread(email); }
////            public void onSpamClick(Email email) { markSpam(email); }
////            @Override public void onLabelsClick(Email email) { editLabels(email); }
////        }, true);
////        recyclerView.setAdapter(emailAdapter);
////        loadItems();
////        return view;
////    }
////
//////    private void loadItems() {
//////        service.getItems(authToken, new ApiService.Callback<List<Email>>() {
//////            @Override public void onSuccess(List<Email> result) {
//////                trashEmails.clear();
//////                trashEmails.addAll(result);
//////                emailAdapter.notifyDataSetChanged();
//////            }
//////            @Override public void onError(Throwable t) {
//////                Log.e(TAG, "load error", t);
//////                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
//////            }
//////        });
//////    }
////    private void loadItems() {
////    service.getItems(authToken, new ApiService.Callback<List<Email>>() {
////        @Override
////        public void onSuccess(List<Email> result) {
////            trashEmails.clear();
////            trashEmails.addAll(result);
////            emailAdapter.notifyDataSetChanged();
////
////            if (trashEmails.isEmpty()) {
////                emptyText.setVisibility(View.VISIBLE);
////                recyclerView.setVisibility(View.GONE);
////                titleText.setVisibility(View.GONE);
////            } else {
////                emptyText.setVisibility(View.GONE);
////                recyclerView.setVisibility(View.VISIBLE);
////                titleText.setVisibility(View.VISIBLE);
////            }
////        }
////
////        @Override
////        public void onError(Throwable t) {
////            Log.e(TAG, "load error", t);
////            Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
////        }
////    });
////}
////
////
////    private void restoreFromTrash(Email email) {
////        service.restoreFromTrash(email.getId(), authToken, new ApiService.Callback<Void>() {
////            public void onSuccess(Void v) {
////                trashEmails.remove(email);
////                emailAdapter.notifyDataSetChanged();
////            }
////            public void onError(Throwable t) {
////                Toast.makeText(getContext(), "Could not mark spam", Toast.LENGTH_SHORT).show();
////            }
////        });
////    }
////
////    private void delete(Email email) {
////        service.delete(email.getId(), authToken, new ApiService.Callback<Void>() {
////            @Override public void onSuccess(Void v) {
////                trashEmails.remove(email);
////                emailAdapter.notifyDataSetChanged();
////            }
////            @Override public void onError(Throwable t) {
////                Toast.makeText(getContext(), "Delete failed", Toast.LENGTH_SHORT).show();
////            }
////        });
////    }
////
////    private void markRead(Email email) {
////        service.markAsRead(email.getId(), authToken, new ApiService.Callback<Void>() {
////            @Override public void onSuccess(Void v) {
////                email.setRead(true);
////                emailAdapter.notifyDataSetChanged();
////            }
////            @Override public void onError(Throwable t) {
////                Toast.makeText(getContext(), "Mark as read failed", Toast.LENGTH_SHORT).show();
////            }
////        });
////    }
////
////    private void markUnread(Email email) {
////        service.markAsUnread(email.getId(), authToken, new ApiService.Callback<Void>() {
////            @Override public void onSuccess(Void v) {
////                email.setRead(true);
////                emailAdapter.notifyDataSetChanged();
////            }
////            @Override public void onError(Throwable t) {
////                Toast.makeText(getContext(), "Mark as unread failed", Toast.LENGTH_SHORT).show();
////            }
////        });
////    }
////    private void markSpam(Email email) {
////        service.markAsSpam(email.getId(), authToken, new ApiService.Callback<Void>() {
////            public void onSuccess(Void v) {
////                trashEmails.remove(email);
////                emailAdapter.notifyDataSetChanged();
////            }
////            public void onError(Throwable t) {
////                Toast.makeText(getContext(), "Could not mark spam", Toast.LENGTH_SHORT).show();
////            }
////        });
////    }
////
////
////    private void editLabels(Email email) {
////        // You could pop up an AlertDialog with multi‑select checkboxes for labels:
////        String[] allLabels = {"Work","Personal","Travel","Receipts"};
////        boolean[] checked = new boolean[allLabels.length];
////        List<String> current = email.getLabels();
////        for (int i = 0; i < allLabels.length; i++) {
////            checked[i] = current.contains(allLabels[i]);
////        }
////
////        new AlertDialog.Builder(getContext())
////                .setTitle("Edit Labels")
////                .setMultiChoiceItems(allLabels, checked, (dlg, which, isChecked) -> {
////                    checked[which] = isChecked;
////                })
////                .setPositiveButton("OK", (dlg, which) -> {
////                    List<String> newLabels = new ArrayList<>();
////                    for (int i = 0; i < allLabels.length; i++) {
////                        if (checked[i]) newLabels.add(allLabels[i]);
////                    }
////                    service.editLabels(email.getId(), newLabels, authToken, new ApiService.Callback<Void>() {
////                        public void onSuccess(Void v) {
////                            email.setLabels(newLabels);
////                            emailAdapter.notifyDataSetChanged();
////                        }
////                        public void onError(Throwable t) {
////                            Toast.makeText(getContext(), "Labels update failed", Toast.LENGTH_SHORT).show();
////                        }
////                    });
////                })
////                .setNegativeButton("Cancel", null)
////                .show();
////    }
////
////    @Override
////    public void onResume() {
////        super.onResume();
////        loadItems(); // initial load
////
////        handler = new android.os.Handler();
////        refreshRunnable = new Runnable() {
////            @Override
////            public void run() {
////                loadItems();
////                handler.postDelayed(this, REFRESH_INTERVAL); // repeat every 2 sec
////            }
////        };
////        handler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
////    }
////
////    @Override
////    public void onPause() {
////        super.onPause();
////        if (handler != null && refreshRunnable != null) {
////            handler.removeCallbacks(refreshRunnable);
////        }
////    }
////
////
////}
//package com.example.myemailapp.fragments;
//
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AlertDialog;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.ViewModelProvider;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.myemailapp.R;
//import com.example.myemailapp.adapters.EmailAdapter;
//import com.example.myemailapp.model.Email;
//import com.example.myemailapp.viewmodel.TrashViewModel;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class TrashFragment_old extends Fragment {
//    private static final String TAG = "TrashFragment";
//    private static final long REFRESH_INTERVAL = 2000; // 2 seconds
//
//    private RecyclerView recyclerView;
//    private EmailAdapter emailAdapter;
//    private TextView emptyText;
//    private TextView titleText;
//    private Handler handler;
//    private Runnable refreshRunnable;
//
//    private TrashViewModel trashViewModel;
//    private List<Email> trashEmails = new ArrayList<>();
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        // Initialize ViewModel
//        trashViewModel = new ViewModelProvider(this).get(TrashViewModel.class);
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_trash, container, false);
//
//        initializeViews(view);
//        setupRecyclerView();
//        setupObservers();
//
//        // Load initial data
//        trashViewModel.loadTrashEmails();
//
//        return view;
//    }
//
//    private void initializeViews(View view) {
//        recyclerView = view.findViewById(R.id.recycler_view_trash);
//        emptyText = view.findViewById(R.id.empty_trash_text);
//        titleText = view.findViewById(R.id.trash_title);
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//    }
//
//    private void setupRecyclerView() {
//        emailAdapter = new EmailAdapter(trashEmails, new EmailAdapter.OnEmailClickListener() {
//            @Override
//            public void onEmailClick(Email email) {
//                // Handle email click if needed
//            }
//
//            @Override
//            public void onRestoreClick(Email email) {
//                trashViewModel.restoreFromTrash(email);
//            }
//
//            @Override
//            public void onDeleteClick(Email email) {
//                trashViewModel.deleteFromTrash(email);
//            }
//
//            @Override
//            public void onMarkReadClick(Email email) {
//                trashViewModel.markAsRead(email);
//            }
//
//            @Override
//            public void onMarkUnreadClick(Email email) {
//                trashViewModel.markAsUnread(email);
//            }
//
//            @Override
//            public void onSpamClick(Email email) {
//                trashViewModel.markAsSpam(email);
//            }
//
//            @Override
//            public void onLabelsClick(Email email) {
//                showEditLabelsDialog(email);
//            }
//        }, true);
//
//        recyclerView.setAdapter(emailAdapter);
//    }
//
//    private void setupObservers() {
//        // Observe trash emails
//        trashViewModel.getTrashEmails().observe(getViewLifecycleOwner(), emails -> {
//            if (emails != null) {
//                trashEmails.clear();
//                trashEmails.addAll(emails);
//                emailAdapter.notifyDataSetChanged();
//
//                // Update empty state
//                trashViewModel.updateEmptyState(emails);
//
//                Log.d(TAG, "Updated trash emails: " + emails.size() + " items");
//            }
//        });
//
//        // Observe empty state
//        trashViewModel.getShouldShowEmpty().observe(getViewLifecycleOwner(), shouldShowEmpty -> {
//            if (shouldShowEmpty) {
//                emptyText.setVisibility(View.VISIBLE);
//                recyclerView.setVisibility(View.GONE);
//                titleText.setVisibility(View.GONE);
//            } else {
//                emptyText.setVisibility(View.GONE);
//                recyclerView.setVisibility(View.VISIBLE);
//                titleText.setVisibility(View.VISIBLE);
//            }
//        });
//
//        // Observe error messages
//        trashViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
//            if (error != null) {
//                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
//                Log.e(TAG, "Error: " + error);
//            }
//        });
//
//        // Observe loading state (optional - you can add a progress indicator)
//        trashViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
//            // You can show/hide a progress indicator here if needed
//            Log.d(TAG, "Loading state: " + isLoading);
//        });
//    }
//
//    private void showEditLabelsDialog(Email email) {
//        String[] allLabels = {"Work", "Personal", "Travel", "Receipts"};
//        boolean[] checked = new boolean[allLabels.length];
//        List<String> currentLabels = email.getLabels();
//
//        // Set current state
//        for (int i = 0; i < allLabels.length; i++) {
//            checked[i] = currentLabels != null && currentLabels.contains(allLabels[i]);
//        }
//
//        new AlertDialog.Builder(requireContext())
//                .setTitle("Edit Labels")
//                .setMultiChoiceItems(allLabels, checked, (dialog, which, isChecked) -> {
//                    checked[which] = isChecked;
//                })
//                .setPositiveButton("OK", (dialog, which) -> {
//                    List<String> newLabels = new ArrayList<>();
//                    for (int i = 0; i < allLabels.length; i++) {
//                        if (checked[i]) {
//                            newLabels.add(allLabels[i]);
//                        }
//                    }
//                    trashViewModel.editLabels(email, newLabels);
//                })
//                .setNegativeButton("Cancel", null)
//                .show();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        // Load initial data
//        trashViewModel.loadTrashEmails();
//
//        // Start periodic refresh
//        startPeriodicRefresh();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//
//        // Stop periodic refresh
//        stopPeriodicRefresh();
//    }
//
//    private void startPeriodicRefresh() {
//        handler = new Handler();
//        refreshRunnable = new Runnable() {
//            @Override
//            public void run() {
//                trashViewModel.loadTrashEmails();
//                handler.postDelayed(this, REFRESH_INTERVAL);
//            }
//        };
//        handler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
//    }
//
//    private void stopPeriodicRefresh() {
//        if (handler != null && refreshRunnable != null) {
//            handler.removeCallbacks(refreshRunnable);
//        }
//    }
//}