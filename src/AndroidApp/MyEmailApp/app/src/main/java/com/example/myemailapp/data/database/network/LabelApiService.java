package com.example.myemailapp.data.database.network;

import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.myemailapp.data.database.entity.Label;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LabelApiService {
    private static final String TAG = "LabelApiService";
    private final Context context;
    private final String baseUrl;
    private final String token;

    public LabelApiService(Context context, String token) {
        this.context = context.getApplicationContext();
        this.token = token;
        this.baseUrl = "http://10.0.2.2:8080/api"; // Adjust port as needed
    }

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }

    public void fetchLabels(ApiCallback<List<Label>> callback) {
        String url = baseUrl + "/labels";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        List<Label> labels = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            String id = jsonObject.optString("id", String.valueOf(i));
                            String name = jsonObject.getString("name");
                            labels.add(new Label(id, name));
                        }
                        callback.onSuccess(labels);
                        Log.d(TAG, "Labels fetched successfully: " + labels.size() + " labels");
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing labels response", e);
                        callback.onError("Failed to parse labels: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e(TAG, "Error fetching labels", error);
                    String errorMessage = "Failed to fetch labels";
                    if (error.networkResponse != null) {
                        errorMessage += " (Status: " + error.networkResponse.statusCode + ")";
                    }
                    callback.onError(errorMessage);
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
}