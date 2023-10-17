package com.example.etrainbooking.Auth;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.etrainbooking.APIClient;
import com.example.etrainbooking.APIInterface;
import com.example.etrainbooking.DBHelper.DBHelper;
import com.example.etrainbooking.Home;
import com.example.etrainbooking.R;
import com.example.etrainbooking.UserController.User;

import org.json.JSONException;
import org.json.JSONObject;

import at.favre.lib.crypto.bcrypt.BCrypt;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {
    private EditText Username, Password;
    private Button Login;
    private TextView goRegister;
    private DBHelper DB;
    private Boolean EditTextEmptyHolder;
    private SQLiteDatabase sqLiteDatabaseObj;
    private Cursor cursor;
    APIInterface apiInterface;
    private String hashedPassword = "NOT_FOUND";
    private String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Username = findViewById(R.id.inp_username);
        Password = findViewById(R.id.inp_password);
        goRegister = findViewById(R.id.btn_createAccount);
        Login = findViewById(R.id.btn_login);

        DB = new DBHelper(this);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        // Create a Retrofit instance for the MongoDB API
        apiInterface = APIClient.getClient().create(APIInterface.class);

        goRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                username = Username.getText().toString();
                password = Password.getText().toString();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    EditTextEmptyHolder = false;
                } else {
                    EditTextEmptyHolder = true;
                }

                if (networkInfo != null && networkInfo.isConnected()) {
                    // Device is online
                    loginFromCloud();
                    ClearEditText();
                } else {
                    loginFromSqlite();
                    ClearEditText();
                }
            }
        });
    }

    @SuppressLint("Range")
    private void loginFromSqlite() {
        if (EditTextEmptyHolder) {
            // Opening SQLite database write permission.
            sqLiteDatabaseObj = DB.getWritableDatabase();
            // Adding search email query to cursor.
            cursor = sqLiteDatabaseObj.query(DBHelper.TABLE_NAME, null, " " + DBHelper.Column_Username + "=?", new String[]{username}, null, null, null);
            while (cursor.moveToNext()) {
                if (cursor.isFirst()) {
                    cursor.moveToFirst();

                    hashedPassword = cursor.getString(cursor.getColumnIndex(DBHelper.Column_Password));

                    // Closing cursor.
                    cursor.close();
                }
            }

            CheckFinalResult();

        } else {
            // If any of login EditText is empty, then this block will be executed.
            Toast.makeText(Login.this, "Please Enter UserName or Password.", Toast.LENGTH_LONG).show();
        }
    }

    private void loginFromCloud() {

        // Create a JSON object to represent the login request
        JSONObject loginJson = new JSONObject();
        try {
            loginJson.put("username", username);
            loginJson.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create a request body with the JSON object
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), loginJson.toString());

        // Call your API method for login
        Call<JSONObject> call = apiInterface.login(requestBody);

        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (response.isSuccessful()) {
                    // Successful API response
                    JSONObject loginResponse = response.body();
                    // Handle the login response (e.g., extract token)
                    handleLoginResponse(loginResponse);
                } else {
                    // Handle login failure
                    handleLoginFailure();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                // Handle API call failure
                handleApiCallFailure(t);
            }
        });
    }

    // Handle the login response (e.g., extract token)
    private void handleLoginResponse(JSONObject loginResponse) {
        try {
            // Extract the token or other information from the response JSON
            String token = loginResponse.getString("token");
            Log.e("token","The token is: "+ token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Handle login failure
    private void handleLoginFailure() {
        // Handle the case where login was not successful (e.g., show an error message)
    }

    // Handle API call failure
    private void handleApiCallFailure(Throwable t) {
        // Handle the case where the API call failed (e.g., show an error message)
    }


    private void ClearEditText() {
        Username.getText().clear();
        Password.getText().clear();
    }

    // Checking entered password from SQLite database email associated password.
    public void CheckFinalResult() {
        if (BCrypt.verifyer().verify(password.toCharArray(), hashedPassword).verified == true) {
            // Fetch user details based on the provided email
            User user = DB.getUserByUserName(username);

            if (user != null) {
                Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_LONG).show();
                Log.e("Register", "Error in Register button click" + user.getNic());

                // Start a session for the user
                SessionManager sessionManager = new SessionManager(getApplicationContext());
                sessionManager.startSession(user.getNic());

                // Going to Dashboard activity after login success message.
                Intent intent = new Intent(Login.this, Home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                // Handle the case where user details are not found
                Toast.makeText(Login.this, "User details not found. Please try again.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(Login.this, "UserName or Password is Wrong, Please Try Again.", Toast.LENGTH_LONG).show();
        }
        hashedPassword = "NOT_FOUND";
    }

}