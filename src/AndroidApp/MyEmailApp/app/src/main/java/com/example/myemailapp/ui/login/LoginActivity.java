
package com.example.myemailapp.ui.login;
import com.example.myemailapp.MainActivity;
import android.content.SharedPreferences;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myemailapp.R;
import com.example.myemailapp.network.ApiClient;
import com.example.myemailapp.network.AuthService;
import com.example.myemailapp.network.LoginRequest;
import com.example.myemailapp.network.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;

public class LoginActivity extends AppCompatActivity {
    private EditText etUser, etPass;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUser = findViewById(R.id.etUsername);
        etPass = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
//        Toast.makeText(this, "Login clicked", Toast.LENGTH_SHORT).show();
        String u = etUser.getText().toString().trim();
        String p = etPass.getText().toString();

        // simple validation
        if (u.isEmpty() || p.isEmpty()) {
            Toast.makeText(this, "Username and password required", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest req = new LoginRequest(u, p);
        AuthService api = ApiClient.getService(this);

        api.login(req).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> res) {
                try {
                    if (res.isSuccessful() && res.body() != null) {
                        LoginResponse loginResponse = res.body();
                        String jwt     = loginResponse.getToken();
                        String message = loginResponse.getMessage();
                        LoginResponse.User user = loginResponse.getUserJson();

                        Log.d("LoginActivity", "Login success: " + message + ", token: " + jwt);
                        if (user != null) {
                            Log.d("LoginActivity", "User: " + user.getUsername()
                                    + " (" + user.getFirstName() + " " + user.getLastName() + ")");
                        }

                        // Save token + full user profile into SharedPreferences
                        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
                        prefs.edit()
                                .putString("jwt",       jwt)
                                .putString("username",  user != null ? user.getUsername()  : "")
                                .putString("firstName", user != null ? user.getFirstName() : "")
                                .putString("lastName",  user != null ? user.getLastName()  : "")
                                // add any other fields you need:
                                .putString("profilePic", user != null ? user.getProfilePic() : "")
                                .putString("phoneNumber", user != null ? user.getPhoneNumber() : "")
                                .putString("birthDate",  user != null ? user.getBirthDate()  : "")
                                .putString("gender",     user != null ? user.getGender()     : "")
                                .apply();

                        // Now navigate into your MainActivity
                        navigateToInbox();

                    } else {
                        String errorMsg = "Login failed";
                        if (res.errorBody() != null) {
                            try {
                                errorMsg = res.errorBody().string();
                            } catch (Exception e) {
                                errorMsg = "Login failed: " + res.code();
                            }
                        }
                        Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        Log.e("LoginActivity", "Login failed: " + res.code() + " " + errorMsg);
                    }
                } catch (Exception e) {
                    Log.e("LoginActivity", "Error processing login response", e);
                    Toast.makeText(LoginActivity.this, "Error processing login response", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("LoginActivity", "Login request failed", t);
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToken(String token) {
        getSharedPreferences("auth", MODE_PRIVATE)
                .edit()
                .putString("jwt", token)
                .apply();
    }

    private void saveUsername(String username) {
        getSharedPreferences("auth", MODE_PRIVATE)
                .edit()
                .putString("username", username)
                .apply();
    }

    private void navigateToInbox() {
        try {
            Log.d("LoginActivity", "Attempting to navigate to MainActivity");

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();

        } catch (Exception e) {
            Log.e("LoginActivity", "Failed to navigate to MainActivity", e);
            Toast.makeText(this, "Error opening main screen: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}