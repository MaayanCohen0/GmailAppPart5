package com.example.myemailapp.repository;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LabelRepository {
    private static final String TAG = "LabelRepository";
    private static LabelRepository instance;
    private final Context context;
    private final String baseUrl;
    private final String token;

    private MutableLiveData<List<String>> labelsLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();

    private LabelRepository(Context context, String token) {
        this.context = context.getApplicationContext();
        this.token = token;
        this.baseUrl = "http://10.0.2.2:8080/api"; // Adjust port as needed
    }

    public static synchronized LabelRepository getInstance(Context context, String token) {
        if (instance == null) {
            instance = new LabelRepository(context, token);
        }
        return instance;
    }

    public MutableLiveData<List<String>> getLabels() {
        return labelsLiveData;
    }

    public MutableLiveData<String> getError() {
        return errorLiveData;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }

//    public void loadLabels() {
//        isLoadingLiveData.postValue(true);
//
//        String url = baseUrl + "/labels"; // Adjust endpoint as needed
//
//        JsonArrayRequest request = new JsonArrayRequest(
//                Request.Method.GET,
//                url,
//                null,
//                response -> {
//                    try {
//                        List<String> labels = new ArrayList<>();
//                        for (int i = 0; i < response.length(); i++) {
//                            labels.add(response.getString(i));
//                        }
//                        labelsLiveData.postValue(labels);
//                        Log.d(TAG, "Labels loaded successfully: " + labels.size() + " labels");
//                    } catch (JSONException e) {
//                        Log.e(TAG, "Error parsing labels response", e);
//                        errorLiveData.postValue("Failed to parse labels: " + e.getMessage());
//                    } finally {
//                        isLoadingLiveData.postValue(false);
//                    }
//                },
//                error -> {
//                    Log.e(TAG, "Error loading labels", error);
//                    String errorMessage = "Failed to load labels";
//                    if (error.networkResponse != null) {
//                        errorMessage += " (Status: " + error.networkResponse.statusCode + ")";
//                    }
//                    errorLiveData.postValue(errorMessage);
//                    isLoadingLiveData.postValue(false);
//                }
//        ) {
//            @Override
//            public Map<String, String> getHeaders() {
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Authorization", "Bearer " + token);
//                return headers;
//            }
//        };
//
//        Volley.newRequestQueue(context).add(request);
//    }
public void loadLabels() {
    isLoadingLiveData.postValue(true);

    String url = baseUrl + "/labels"; // Adjust endpoint as needed

    JsonArrayRequest request = new JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            response -> {

                try {
                    List<String> labels = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        labels.add(jsonObject.getString("name"));
                    }
                    labelsLiveData.postValue(labels);
                    Log.d(TAG, "Labels loaded successfully: " + labels.size() + " labels");
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing labels response", e);
                    errorLiveData.postValue("Failed to parse labels: " + e.getMessage());
                } finally {
                    isLoadingLiveData.postValue(false);
                }
            },
            error -> {
                Log.e(TAG, "Error loading labels", error);
                String errorMessage = "Failed to load labels";
                if (error.networkResponse != null) {
                    errorMessage += " (Status: " + error.networkResponse.statusCode + ")";
                }
                errorLiveData.postValue(errorMessage);
                isLoadingLiveData.postValue(false);
            }
    ) {
        @Override
        public Map<String, String> getHeaders() {
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + token);
            return headers;
        }
    };

    Volley.newRequestQueue(context).add(request);
}
    public void refreshLabels() {
        loadLabels();
    }
}