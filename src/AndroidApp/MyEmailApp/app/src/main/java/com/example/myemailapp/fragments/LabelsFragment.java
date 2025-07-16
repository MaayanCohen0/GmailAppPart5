//package com.example.myemailapp.fragments;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.fragment.app.Fragment;
//
//public class LabelsFragment extends Fragment {
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // Create the layout programmatically
//        LinearLayout layout = new LinearLayout(getContext());
//        layout.setOrientation(LinearLayout.VERTICAL);
//        layout.setPadding(40, 60, 40, 40);
//
//        // Title
//        TextView titleText = new TextView(getContext());
//        titleText.setText("Labels");
//        titleText.setTextSize(24);
//        titleText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//        titleText.setPadding(0, 0, 0, 30);
//        titleText.setTextColor(0xFFFF5722); // Orange-red color
//
//        // Content
//        TextView contentText = new TextView(getContext());
//        contentText.setText("Labels yayyyy");
//        contentText.setTextSize(16);
//        contentText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//        contentText.setLineSpacing(8, 1.2f);
//
//        layout.addView(titleText);
//        layout.addView(contentText);
//
//        return layout;
//    }
//}
package com.example.myemailapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myemailapp.R;
import com.example.myemailapp.adapters.LabelAdapter;
import com.example.myemailapp.data.database.entity.Label;
import com.example.myemailapp.viewmodel.LabelViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import java.util.List;

public class LabelsFragment extends Fragment implements LabelAdapter.LabelClickListener {

    private LabelViewModel labelViewModel;
    private LabelAdapter labelAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddLabel;
    private SearchView searchView;
    private List<Label> allLabels = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        labelViewModel = new ViewModelProvider(this).get(LabelViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_labels, container, false);

        initViews(view);
        setupRecyclerView();
//        setupSearchView();
        setupFab();
        observeViewModel();

        // Load labels when fragment is created
        labelViewModel.loadLabels();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_labels);
        fabAddLabel = view.findViewById(R.id.fab_add_label);
//        searchView = view.findViewById(R.id.search_view_labels);
    }

    private void setupRecyclerView() {
        labelAdapter = new LabelAdapter(new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(labelAdapter);
    }

//    private void setupSearchView() {
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                filterLabels(query);
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                filterLabels(newText);
//                return true;
//            }
//        });
//    }

    private void setupFab() {
        fabAddLabel.setOnClickListener(v -> showAddLabelDialog());
    }

    private void observeViewModel() {
        // Observe label entities (full Label objects)
        labelViewModel.getLabelEntities().observe(getViewLifecycleOwner(), labels -> {
            if (labels != null) {
                allLabels = new ArrayList<>(labels);
                labelAdapter.updateLabels(labels);
            }
        });

        // Observe loading state
        labelViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // You can show/hide a progress bar here
            if (isLoading != null && isLoading) {
                // Show loading indicator
            } else {
                // Hide loading indicator
            }
        });

        // Observe errors
        labelViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterLabels(String query) {
        if (query == null || query.trim().isEmpty()) {
            labelAdapter.updateLabels(allLabels);
        } else {
            List<Label> filteredLabels = new ArrayList<>();
            for (Label label : allLabels) {
                if (label.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredLabels.add(label);
                }
            }
            labelAdapter.updateLabels(filteredLabels);
        }
    }

    private void showAddLabelDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_label, null);
        TextInputLayout textInputLayout = dialogView.findViewById(R.id.text_input_layout);
        TextInputEditText editText = dialogView.findViewById(R.id.edit_text_label_name);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add New Label")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String labelName = editText.getText().toString().trim();
                    if (!labelName.isEmpty()) {
                        addLabel(labelName);
                    } else {
                        textInputLayout.setError("Label name cannot be empty");
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditLabelDialog(Label label) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_label, null);
        TextInputLayout textInputLayout = dialogView.findViewById(R.id.text_input_layout);
        TextInputEditText editText = dialogView.findViewById(R.id.edit_text_label_name);

        editText.setText(label.getName());

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Edit Label")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String labelName = editText.getText().toString().trim();
                    if (!labelName.isEmpty()) {
                        updateLabel(label.getId(), labelName);
                    } else {
                        textInputLayout.setError("Label name cannot be empty");
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteConfirmationDialog(Label label) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Label")
                .setMessage("Are you sure you want to delete \"" + label.getName() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> deleteLabel(label.getId()))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addLabel(String labelName) {
        // You'll need to implement this in your repository
        // For now, we'll create a temporary label object
        Label newLabel = new Label();
        newLabel.setName(labelName);
        newLabel.setId(String.valueOf(System.currentTimeMillis())); // Temporary ID

        labelViewModel.addLabel(newLabel);
        Toast.makeText(getContext(), "Label added: " + labelName, Toast.LENGTH_SHORT).show();
    }

    private void updateLabel(String labelId, String newName) {
        // You'll need to implement this in your repository
        labelViewModel.updateLabel(labelId, newName);
        Toast.makeText(getContext(), "Label updated", Toast.LENGTH_SHORT).show();
    }

    private void deleteLabel(String labelId) {
        labelViewModel.deleteLabel(labelId);
        Toast.makeText(getContext(), "Label deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditClick(Label label) {
        showEditLabelDialog(label);
    }

    @Override
    public void onDeleteClick(Label label) {
        showDeleteConfirmationDialog(label);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh labels when fragment becomes visible
        labelViewModel.refreshLabels();
    }
}