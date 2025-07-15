////package com.example.myemailapp.repository;
////
////import android.content.Context;
////import android.content.SharedPreferences;
////import android.util.Log;
////
////import androidx.lifecycle.LiveData;
////import androidx.lifecycle.MutableLiveData;
////
////import com.example.myemailapp.model.Email;
////import com.example.myemailapp.network.ApiService;
////import com.example.myemailapp.network.TrashService;
////
////import java.util.List;
////
////import retrofit2.Retrofit;
////import retrofit2.converter.gson.GsonConverterFactory;
////
////public class TrashRepository {
////    private static final String TAG = "TrashRepository";
////    private static final String PREF_NAME = "auth";
////
////    private TrashService trashService;
////    private String authToken;
////    private Context context;
////
////    private MutableLiveData<List<Email>> trashEmails = new MutableLiveData<>();
////    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
////    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
////
////    public TrashRepository(Context context) {
////        this.context = context;
////
////        // Get auth token
////        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
////        authToken = "Bearer " + prefs.getString("jwt", "");
////
////        // Initialize service
////        Retrofit retrofit = new Retrofit.Builder()
////                .baseUrl("http://10.0.2.2:8080/api/")
////                .addConverterFactory(GsonConverterFactory.create())
////                .build();
////
////        trashService = new TrashService(retrofit);
////        isLoading.setValue(false);
////    }
////
////    public LiveData<List<Email>> getTrashEmails() {
////        return trashEmails;
////    }
////
////    public LiveData<String> getErrorMessage() {
////        return errorMessage;
////    }
////
////    public LiveData<Boolean> getIsLoading() {
////        return isLoading;
////    }
////
////    public void loadTrashEmails() {
////        isLoading.setValue(true);
////
////        trashService.getItems(authToken, new ApiService.Callback<List<Email>>() {
////            @Override
////            public void onSuccess(List<Email> result) {
////                Log.d(TAG, "Loaded " + result.size() + " trash emails");
////                trashEmails.postValue(result);
////                isLoading.postValue(false);
////            }
////
////            @Override
////            public void onError(Throwable t) {
////                Log.e(TAG, "Failed to load trash emails", t);
////                errorMessage.postValue("Failed to load trash emails: " + t.getMessage());
////                isLoading.postValue(false);
////            }
////        });
////    }
////
////    public void restoreFromTrash(Email email) {
////        trashService.restoreFromTrash(email.getId(), authToken, new ApiService.Callback<Void>() {
////            @Override
////            public void onSuccess(Void v) {
////                Log.d(TAG, "Restored email from trash: " + email.getId());
////
////                // Remove from current list
////                List<Email> currentEmails = trashEmails.getValue();
////                if (currentEmails != null) {
////                    currentEmails.remove(email);
////                    trashEmails.postValue(currentEmails);
////                }
////            }
////
////            @Override
////            public void onError(Throwable t) {
////                Log.e(TAG, "Failed to restore email from trash", t);
////                errorMessage.postValue("Failed to restore email");
////            }
////        });
////    }
////
////    public void deleteFromTrash(Email email) {
////        trashService.delete(email.getId(), authToken, new ApiService.Callback<Void>() {
////            @Override
////            public void onSuccess(Void v) {
////                Log.d(TAG, "Deleted email from trash: " + email.getId());
////
////                // Remove from current list
////                List<Email> currentEmails = trashEmails.getValue();
////                if (currentEmails != null) {
////                    currentEmails.remove(email);
////                    trashEmails.postValue(currentEmails);
////                }
////            }
////
////            @Override
////            public void onError(Throwable t) {
////                Log.e(TAG, "Failed to delete email from trash", t);
////                errorMessage.postValue("Failed to delete email");
////            }
////        });
////    }
////
////    public void markAsRead(Email email) {
////        trashService.markAsRead(email.getId(), authToken, new ApiService.Callback<Void>() {
////            @Override
////            public void onSuccess(Void v) {
////                Log.d(TAG, "Marked email as read: " + email.getId());
////
////                // Update email in current list
////                List<Email> currentEmails = trashEmails.getValue();
////                if (currentEmails != null) {
////                    email.setRead(true);
////                    trashEmails.postValue(currentEmails);
////                }
////            }
////
////            @Override
////            public void onError(Throwable t) {
////                Log.e(TAG, "Failed to mark email as read", t);
////                errorMessage.postValue("Failed to mark as read");
////            }
////        });
////    }
////
////    public void markAsUnread(Email email) {
////        trashService.markAsUnread(email.getId(), authToken, new ApiService.Callback<Void>() {
////            @Override
////            public void onSuccess(Void v) {
////                Log.d(TAG, "Marked email as unread: " + email.getId());
////
////                // Update email in current list
////                List<Email> currentEmails = trashEmails.getValue();
////                if (currentEmails != null) {
////                    email.setRead(false);
////                    trashEmails.postValue(currentEmails);
////                }
////            }
////
////            @Override
////            public void onError(Throwable t) {
////                Log.e(TAG, "Failed to mark email as unread", t);
////                errorMessage.postValue("Failed to mark as unread");
////            }
////        });
////    }
////
////    public void markAsSpam(Email email) {
////        trashService.markAsSpam(email.getId(), authToken, new ApiService.Callback<Void>() {
////            @Override
////            public void onSuccess(Void v) {
////                Log.d(TAG, "Marked email as spam: " + email.getId());
////
////                // Remove from current list
////                List<Email> currentEmails = trashEmails.getValue();
////                if (currentEmails != null) {
////                    currentEmails.remove(email);
////                    trashEmails.postValue(currentEmails);
////                }
////            }
////
////            @Override
////            public void onError(Throwable t) {
////                Log.e(TAG, "Failed to mark email as spam", t);
////                errorMessage.postValue("Failed to mark as spam");
////            }
////        });
////    }
////
////    public void editLabels(Email email, List<String> newLabels) {
////        trashService.editLabels(email.getId(), newLabels, authToken, new ApiService.Callback<Void>() {
////            @Override
////            public void onSuccess(Void v) {
////                Log.d(TAG, "Updated labels for email: " + email.getId());
////
////                // Update email in current list
////                List<Email> currentEmails = trashEmails.getValue();
////                if (currentEmails != null) {
////                    email.setLabels(newLabels);
////                    trashEmails.postValue(currentEmails);
////                }
////            }
////
////            @Override
////            public void onError(Throwable t) {
////                Log.e(TAG, "Failed to update labels", t);
////                errorMessage.postValue("Failed to update labels");
////            }
////        });
////    }
////}
//
//package com.example.myemailapp.repository;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.util.Log;
//
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//
//import com.example.myemailapp.model.Email;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//import retrofit2.http.Body;
//import retrofit2.http.DELETE;
//import retrofit2.http.GET;
//import retrofit2.http.Header;
//import retrofit2.http.PATCH;
//import retrofit2.http.POST;
//import retrofit2.http.Path;
//
//public class TrashRepository_old {
//    private static final String TAG = "TrashRepository";
//    private static final String PREF_NAME = "auth";
//
//    private final TrashRetrofitApi api;
//    private final String authToken;
//    private final Context context;
//
//    private final MutableLiveData<List<Email>> trashEmails = new MutableLiveData<>();
//    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
//    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
//
//    // Retrofit API definition
//    private interface TrashRetrofitApi {
//        @GET("trash")
//        Call<List<Email>> getTrashEmails(@Header("Authorization") String authToken);
//
//        @POST("trash/restore/{id}")
//        Call<Void> restoreFromTrash(@Path("id") String emailId, @Header("Authorization") String authToken);
//
//        @DELETE("trash/{id}")
//        Call<Void> deleteFromTrash(@Path("id") String emailId, @Header("Authorization") String authToken);
//
//        @POST("spam/{id}")
//        Call<Void> markAsSpam(@Path("id") String emailId, @Header("Authorization") String authToken);
//
//        @PATCH("trash/label/{id}")
//        Call<Void> editLabels(
//                @Path("id") String emailId,
//                @Body Map<String, List<String>> wrapper,
//                @Header("Authorization") String authToken
//        );
//    }
//
//    public TrashRepository_old(Context context) {
//        this.context = context;
//
//        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
//        this.authToken = "Bearer " + prefs.getString("jwt", "");
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://10.0.2.2:8080/api/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        this.api = retrofit.create(TrashRetrofitApi.class);
//        isLoading.setValue(false);
//    }
//
//    public LiveData<List<Email>> getTrashEmails() {
//        return trashEmails;
//    }
//
//    public LiveData<String> getErrorMessage() {
//        return errorMessage;
//    }
//
//    public LiveData<Boolean> getIsLoading() {
//        return isLoading;
//    }
//
//    public void loadTrashEmails() {
//        isLoading.setValue(true);
//        api.getTrashEmails(authToken).enqueue(new RetrofitCallback<List<Email>>() {
//            @Override
//            public void onResponse(Call<List<Email>> call, Response<List<Email>> response) {
//                isLoading.postValue(false);
//                if (response.isSuccessful() && response.body() != null) {
//                    Log.d(TAG, "Loaded " + response.body().size() + " trash emails");
//                    trashEmails.postValue(response.body());
//                } else {
//                    Log.e(TAG, "Failed to load trash emails: code " + response.code());
//                    errorMessage.postValue("Failed to load trash emails: " + response.code());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Email>> call, Throwable t) {
//                isLoading.postValue(false);
//                Log.e(TAG, "Error loading trash emails", t);
//                errorMessage.postValue("Failed to load trash emails: " + t.getMessage());
//            }
//        });
//    }
//
//    public void restoreFromTrash(Email email) {
//        api.restoreFromTrash(email.getId(), authToken).enqueue(new RetrofitCallback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                if (response.isSuccessful()) {
//                    Log.d(TAG, "Restored email from trash: " + email.getId());
//                    removeEmail(email);
//                } else {
//                    Log.e(TAG, "Failed to restore email: " + response.code());
//                    errorMessage.postValue("Failed to restore email");
//                }
//            }
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                Log.e(TAG, "Error restoring email", t);
//                errorMessage.postValue("Failed to restore email: " + t.getMessage());
//            }
//        });
//    }
//
//    public void deleteFromTrash(Email email) {
//        api.deleteFromTrash(email.getId(), authToken).enqueue(new RetrofitCallback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                if (response.isSuccessful()) {
//                    Log.d(TAG, "Deleted email from trash: " + email.getId());
//                    removeEmail(email);
//                } else {
//                    Log.e(TAG, "Failed to delete email: " + response.code());
//                    errorMessage.postValue("Failed to delete email");
//                }
//            }
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                Log.e(TAG, "Error deleting email", t);
//                errorMessage.postValue("Failed to delete email: " + t.getMessage());
//            }
//        });
//    }
//
//    public void markAsRead(Email email) {
//        // no dedicated endpoint; reusing restore for demo
//        api.restoreFromTrash(email.getId(), authToken).enqueue(new RetrofitCallback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                if (response.isSuccessful()) {
//                    Log.d(TAG, "Marked email as read: " + email.getId());
//                    updateReadState(email, true);
//                } else {
//                    Log.e(TAG, "Failed to mark as read: " + response.code());
//                    errorMessage.postValue("Failed to mark as read");
//                }
//            }
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                Log.e(TAG, "Error marking as read", t);
//                errorMessage.postValue("Failed to mark as read: " + t.getMessage());
//            }
//        });
//    }
//
////    public void markAsUnread(Email email) {
////        // no dedicated endpoint; reusing delete for demo
////        api.deleteFromTrash(email.getId(), authToken).enqueue(new RetrofitCallback<Void>() {
////            @Override
////            public void onResponse(Call<Void> call, Response<Void> response) {
////                if (response.isSuccessful()) {
////                    Log.d(TAG, "Marked email as unread: " + email.getId());
////                    updateReadState(email, false);
////                } else {
////                    Log.e(TAG, "Failed to mark as unread: " + response.code());
////                    errorMessage.postValue("Failed to mark as unread");
////                }
////            }
////            @Override
////            public void onFailure(Call<Void>> call, Throwable t) {
////                Log.e(TAG, "Error marking as unread", t);
////                errorMessage.postValue("Failed to mark as unread: " + t.getMessage());
////            }
////        });
////    }
//
//    public void markAsSpam(Email email) {
//        api.markAsSpam(email.getId(), authToken).enqueue(new RetrofitCallback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                if (response.isSuccessful()) {
//                    Log.d(TAG, "Marked email as spam: " + email.getId());
//                    removeEmail(email);
//                } else {
//                    Log.e(TAG, "Failed to mark as spam: " + response.code());
//                    errorMessage.postValue("Failed to mark as spam");
//                }
//            }
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                Log.e(TAG, "Error marking as spam", t);
//                errorMessage.postValue("Failed to mark as spam: " + t.getMessage());
//            }
//        });
//    }
//
//    public void editLabels(Email email, List<String> newLabels) {
//        Map<String, List<String>> body = Collections.singletonMap("labels", newLabels);
//        api.editLabels(email.getId(), body, authToken).enqueue(new RetrofitCallback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                if (response.isSuccessful()) {
//                    Log.d(TAG, "Updated labels for email: " + email.getId());
//                    email.setLabels(newLabels);
//                    trashEmails.postValue(trashEmails.getValue());
//                } else {
//                    Log.e(TAG, "Failed to update labels: " + response.code());
//                    errorMessage.postValue("Failed to update labels");
//                }
//            }
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                Log.e(TAG, "Error updating labels", t);
//                errorMessage.postValue("Failed to update labels: " + t.getMessage());
//            }
//        });
//    }
//
//    // Helper to remove an email from LiveData list
//    private void removeEmail(Email email) {
//        List<Email> current = trashEmails.getValue();
//        if (current != null) {
//            current.remove(email);
//            trashEmails.postValue(current);
//        }
//    }
//
//    // Helper to update read/unread state
//    private void updateReadState(Email email, boolean read) {
//        List<Email> current = trashEmails.getValue();
//        if (current != null) {
//            email.setRead(read);
//            trashEmails.postValue(current);
//        }
//    }
//
//    // Generic Retrofit callback wrapper
//    private abstract class RetrofitCallback<T> implements Callback<T> {}
//}
