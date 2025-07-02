package com.example.myemailapp.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("tokens")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
}
