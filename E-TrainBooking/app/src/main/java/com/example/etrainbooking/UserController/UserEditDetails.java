package com.example.etrainbooking.UserController;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.etrainbooking.APIClient;
import com.example.etrainbooking.APIInterface;
import com.example.etrainbooking.Auth.Login;
import com.example.etrainbooking.Auth.SessionManager;
import com.example.etrainbooking.DBHelper.DBHelper;
import com.example.etrainbooking.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserEditDetails extends AppCompatActivity {

    private EditText editFirstName, editNIC, editMobile, editAge, editAddress, editUsername;
    private Button updateButton;
    private SessionManager sessionManager;
    private DBHelper dbHelper;
    private APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);

        // Initialize UI elements
        editFirstName = findViewById(R.id.edit_fname);
        editNIC = findViewById(R.id.edit_nic);
        editMobile = findViewById(R.id.edit_mobile);
        editAge = findViewById(R.id.edit_age);
        editAddress = findViewById(R.id.edit_address);
        editUsername = findViewById(R.id.edit_username);
        updateButton = findViewById(R.id.update_button);

        // Retrieve the email from the Intent
        String userId = getIntent().getStringExtra("id");

        // Initialize the SessionManager
        sessionManager = new SessionManager(getApplicationContext());
        // Retrieve user id
        String userNic = sessionManager.getUserNic();

        // Initialize DBHelper
        dbHelper = new DBHelper(this);

        // Create a Retrofit instance for the MongoDB API
        apiInterface = APIClient.getClient().create(APIInterface.class);

        // Fetch user details based on the email from the database
        User currentUser = dbHelper.getUserByNIC(userNic);

        if (sessionManager.isUserLoggedIn()) {
            // Check if the user was found
            if (currentUser != null) {
                // Populate the input fields with the current user data
                editFirstName.setText(currentUser.getName());
                editNIC.setText(currentUser.getNic());
                editMobile.setText(currentUser.getContactNo());
                editAge.setText(currentUser.getAge());
                editAddress.setText(currentUser.getAddress());
                editUsername.setText(currentUser.getUserName());
            } else {

                Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show();
            }

            // Set an OnClickListener for the updateButton
            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Handle the update logic here
                    updateUserInfo(currentUser);
                }
            });

        }else{
            Intent intent = new Intent(UserEditDetails.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    // Implement the updateUserInfo method to update user information in the database
    private void updateUserInfo(User currentuser) {
        String newFirstName = editFirstName.getText().toString();
        String newMobile = editMobile.getText().toString();
        String newAge = editAge.getText().toString();
        String newAddress = editAddress.getText().toString();
        String newUsername = editUsername.getText().toString();

        // Check if any of the fields are empty (you can add more validation as needed)
        if (TextUtils.isEmpty(newFirstName) || TextUtils.isEmpty(newUsername) || TextUtils.isEmpty(currentuser.getNic())) {
            Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return; // Exit without updating if any field is empty
        }

        // Create or open the SQLite database
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Create a ContentValues object to store the new values
        ContentValues values = new ContentValues();
        values.put(DBHelper.Column_Name, newFirstName);
        values.put(DBHelper.Column_Mobile, newMobile);
        values.put(DBHelper.Column_Age, newAge);
        values.put(DBHelper.Column_Address, newAddress);
        values.put(DBHelper.Column_Username, newUsername);

        // Specify the WHERE clause to identify the row to update (e.g., by user ID)
        String whereClause = DBHelper.Column_NIC + " = ?";
        String[] whereArgs = new String[]{currentuser.getNic()};

        // Update the user information in the database
        int rowsUpdated = database.update(DBHelper.TABLE_NAME, values, whereClause, whereArgs);

        // Close the database connection
        database.close();

        // Check if the update was successful
        if (rowsUpdated > 0) {
            // Update the user data in MongoDB
            updateUserInMongoDB(currentuser, newFirstName, newMobile, newAge, newAddress, newUsername);
            Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UserEditDetails.this, UserViewDetails.class);
            startActivity(intent);
            finish(); // Close the current activity
        } else {
            Toast.makeText(this, "Failed to update profile. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    // Implement the method to update user data in MongoDB
    private void updateUserInMongoDB(User currentUser, String newFirstName, String newMobile, String newAge, String newAddress, String newUsername) {

        // Create a User object with updated information
        User updatedUser = new User(
                newFirstName,
                currentUser.getNic(),
                newMobile,
                newAge,
                newAddress,
                newUsername,
                currentUser.getPassword(),
                currentUser.getRole(),
                currentUser.getIsActive()
        );

        // Make the API call to update the user in MongoDB
        Call<User> call = apiInterface.updateUser(updatedUser);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    // Update successful
                    Toast.makeText(UserEditDetails.this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UserEditDetails.this, UserViewDetails.class);
                    startActivity(intent);
                    finish(); // Close the current activity
                } else {
                    // Update in MongoDB failed
                    Toast.makeText(UserEditDetails.this, "Failed to update profile in MongoDB. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Handle the network request failure
                Toast.makeText(UserEditDetails.this, "Network request failed. Please check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
