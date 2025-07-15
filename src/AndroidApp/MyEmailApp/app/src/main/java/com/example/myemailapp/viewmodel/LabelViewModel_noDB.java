////package com.example.myemailapp.viewmodel;
////
////import android.app.Application;
////import android.content.SharedPreferences;
////import androidx.annotation.NonNull;
////import androidx.lifecycle.AndroidViewModel;
////import androidx.lifecycle.LiveData;
////import androidx.lifecycle.MutableLiveData;
////import com.example.myemailapp.repository.LabelRepository;
////import java.util.List;
////import static android.content.Context.MODE_PRIVATE;
////
////public class LabelViewModel extends AndroidViewModel {
////    private static final String TAG = "LabelViewModel";
////
////    private LabelRepository labelRepository;
////
////    public LabelViewModel(@NonNull Application application) {
////        super(application);
////
////        // Get auth token
////        SharedPreferences prefs = application.getSharedPreferences("auth", MODE_PRIVATE);
////        String authToken = prefs.getString("jwt", "");
////
////        // Initialize repository
////        labelRepository = LabelRepository.getInstance(application, authToken);
////    }
////
////    public LiveData<List<String>> getLabels() {
////        return labelRepository.getLabels();
////    }
////
////    public LiveData<String> getErrorMessage() {
////        return labelRepository.getError();
////    }
////
////    public LiveData<Boolean> getIsLoading() {
////        return labelRepository.getIsLoading();
////    }
////
////    public void loadLabels() {
////        labelRepository.loadLabels();
////    }
////
////    public void refreshLabels() {
////        labelRepository.refreshLabels();
////    }
////}
//
//package com.example.myemailapp.viewmodel;
//
//import static android.content.Context.MODE_PRIVATE;
//
//import android.app.Application;
//import android.content.SharedPreferences;
//import android.os.Handler;
//import android.os.Looper;
//
//import androidx.annotation.NonNull;
//import androidx.lifecycle.AndroidViewModel;
//import androidx.lifecycle.LiveData;
//
//import com.example.myemailapp.repository.LabelRepository;
//
//import java.util.List;
//
//public class LabelViewModel_old extends AndroidViewModel {
//    private static final String TAG = "LabelViewModel";
//    private static final long REFRESH_INTERVAL = 5000; // 5 seconds
//
//    private LabelRepository labelRepository;
//    private Handler handler;
//    private Runnable refreshRunnable;
//    private boolean isPeriodicRefreshActive = false;
//
//    public LabelViewModel_old(@NonNull Application application) {
//        super(application);
//
//        // Get auth token
//        SharedPreferences prefs = application.getSharedPreferences("auth", MODE_PRIVATE);
//        String authToken = prefs.getString("jwt", "");
//
//        // Initialize repository
//        labelRepository = LabelRepository.getInstance(application, authToken);
//
//        // Initialize handler for periodic refresh
//        handler = new Handler(Looper.getMainLooper());
//    }
//
//    public LiveData<List<String>> getLabels() {
//        return labelRepository.getLabels();
//    }
//
//    public LiveData<String> getErrorMessage() {
//        return labelRepository.getError();
//    }
//
//    public LiveData<Boolean> getIsLoading() {
//        return labelRepository.getIsLoading();
//    }
//
//    public void loadLabels() {
//        labelRepository.loadLabels();
//    }
//
//    public void refreshLabels() {
//        labelRepository.refreshLabels();
//    }
//
//    /**
//     * Start periodic refresh of labels
//     */
//    public void startPeriodicRefresh() {
//        if (isPeriodicRefreshActive) {
//            return; // Already running
//        }
//
//        isPeriodicRefreshActive = true;
//        refreshRunnable = new Runnable() {
//            @Override
//            public void run() {
//                if (isPeriodicRefreshActive) {
//                    labelRepository.refreshLabels();
//                    handler.postDelayed(this, REFRESH_INTERVAL);
//                }
//            }
//        };
//
//        // Start the periodic refresh
//        handler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
//    }
//
//    /**
//     * Stop periodic refresh of labels
//     */
//    public void stopPeriodicRefresh() {
//        isPeriodicRefreshActive = false;
//        if (handler != null && refreshRunnable != null) {
//            handler.removeCallbacks(refreshRunnable);
//        }
//    }
//
//    /**
//     * Set custom refresh interval (in milliseconds)
//     */
//    public void setRefreshInterval(long intervalMs) {
//        stopPeriodicRefresh();
//        // You could store this in a field if you want to make it configurable
//        startPeriodicRefresh();
//    }
//
//    /**
//     * Check if periodic refresh is currently active
//     */
//    public boolean isPeriodicRefreshActive() {
//        return isPeriodicRefreshActive;
//    }
//
//    @Override
//    protected void onCleared() {
//        super.onCleared();
//        // Stop periodic refresh when ViewModel is destroyed
//        stopPeriodicRefresh();
//    }
//}