
package com.example.myemailapp.ui.login;
import com.example.myemailapp.model.User;
import com.example.myemailapp.MainActivity;
import com.example.myemailapp.R;
import com.example.myemailapp.network.ApiClient;
import com.example.myemailapp.network.AuthService;
import com.example.myemailapp.network.LoginRequest;
import com.example.myemailapp.network.LoginResponse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText etUser, etPass;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is already logged in
        if (isUserLoggedIn()) {
            Log.d("LoginActivity", "User already logged in, navigating to MainActivity");
            navigateToInbox();
            return;
        }

        setContentView(R.layout.activity_login);

        etUser = findViewById(R.id.etUsername);
        etPass = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> attemptLogin());

        Button btnCreate = findViewById(R.id.btnCreateAccount);
        btnCreate.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }

    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String jwt = prefs.getString("jwt", "");
        String username = prefs.getString("username", "");

        // Check if we have both JWT token and username saved
        return !jwt.isEmpty() && !username.isEmpty();
    }

    private void attemptLogin() {
        String u = etUser.getText().toString().trim();
        String p = etPass.getText().toString();

        // simple validation
        if (u.isEmpty() || p.isEmpty()) {
            Toast.makeText(
                    this,
                    getString(R.string.error_empty_credentials),
                    Toast.LENGTH_SHORT
            ).show();
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
                        User user = loginResponse.getUserJson();

                        Log.d("LoginActivity", "Login success: " + message + ", token: " + jwt);
                        if (user != null) {
                            Log.d("LoginActivity", "User: " + user.getUsername()
                                    + " (" + user.getFirstName() + " " + user.getLastName() + ")");
                        }

                        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
                        prefs.edit()
                                .putString("jwt",       jwt)
                                .putString("username",  user != null ? user.getUsername()  : "")
                                .putString("firstName", user != null ? user.getFirstName() : "")
                                .putString("lastName",  user != null ? user.getLastName()  : "")
                                .putString("profilePic", user != null ? user.getProfilePic() : "")
                                .putString("phoneNumber", user != null ? user.getPhoneNumber() : "")
                                .putString("birthDate",  user != null ? user.getBirthDate()  : "")
                                .putString("gender",     user != null ? user.getGender()     : "")
                                .apply();

                        navigateToInbox();

                    } else {
                        String errorMsg = getString(R.string.error_login_failed);
                        if (res.errorBody() != null) {
                            try {
                                errorMsg = res.errorBody().string();
                            } catch (Exception e) {
                                errorMsg = getString(
                                        R.string.error_login_failed_with_code,
                                        res.code()
                                );
                            }
                        }
                        Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        Log.e("LoginActivity", "Login failed: " + res.code() + " " + errorMsg);
                    }
                } catch (Exception e) {
                    Log.e("LoginActivity", "Error processing login response", e);
                    Toast.makeText(
                            LoginActivity.this,
                            getString(R.string.error_processing_response),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("LoginActivity", "Login request failed", t);
                Toast.makeText(
                        LoginActivity.this,
                        getString(R.string.error_network, t.getMessage()),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void navigateToInbox() {
        try {
            Log.d("LoginActivity", "Attempting to navigate to MainActivity");

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();

        } catch (Exception e) {
            Log.e("LoginActivity", "Failed to navigate to MainActivity", e);
            Toast.makeText(
                    this,
                    getString(R.string.error_opening_main, e.getMessage()),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    // Static method to clear login state (call this when user logs out)
    public static void clearLoginState(android.content.Context context) {
        SharedPreferences prefs = context.getSharedPreferences("auth", context.MODE_PRIVATE);
        prefs.edit().clear().apply();
        Log.d("LoginActivity", "Login state cleared");
    }
}