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

import com.example.etrainbooking.Auth.Login;
import com.example.etrainbooking.Auth.SessionManager;
import com.example.etrainbooking.DBHelper.DBHelper;
import com.example.etrainbooking.R;

public class UserEditDetails extends AppCompatActivity {

    private EditText editFirstName, editLastName, editEmail, editNIC, editMobile;
    private Button updateButton;
    private SessionManager sessionManager;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);

        // Initialize UI elements
        editFirstName = findViewById(R.id.edit_fname);
        editLastName = findViewById(R.id.edit_lname);
        editEmail = findViewById(R.id.edit_email);
        editNIC = findViewById(R.id.edit_nic);
        editMobile = findViewById(R.id.edit_mobile);
        updateButton = findViewById(R.id.update_button);

        // Retrieve the email from the Intent
        String userId = getIntent().getStringExtra("id");

        // Initialize the SessionManager
        sessionManager = new SessionManager(getApplicationContext());

        // Initialize DBHelper
        dbHelper = new DBHelper(this);

        // Fetch user details based on the email from the database
        User currentUser = dbHelper.getUserById(userId);

        if (sessionManager.isUserLoggedIn()) {
            // Check if the user was found
            if (currentUser != null) {
                // Populate the input fields with the current user data
                editFirstName.setText(currentUser.getFname());
                editLastName.setText(currentUser.getLname());
                editEmail.setText(currentUser.getEmail());
                editNIC.setText(currentUser.getNic());
                editMobile.setText(currentUser.getMobileNo());
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
        String newLastName = editLastName.getText().toString();
        String newEmail = editEmail.getText().toString();
        String newNIC = editNIC.getText().toString();
        String newMobile = editMobile.getText().toString();

        // Check if any of the fields are empty (you can add more validation as needed)
        if (TextUtils.isEmpty(newFirstName) || TextUtils.isEmpty(newLastName) || TextUtils.isEmpty(newEmail)) {
            Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return; // Exit without updating if any field is empty
        }

        // Create or open the SQLite database
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Create a ContentValues object to store the new values
        ContentValues values = new ContentValues();
        values.put(DBHelper.Column_Fname, newFirstName);
        values.put(DBHelper.Column_Lname, newLastName);
        values.put(DBHelper.Column_Email, newEmail);
        values.put(DBHelper.Column_Nic, newNIC);
        values.put(DBHelper.Column_Mobile, newMobile);

        // Specify the WHERE clause to identify the row to update (e.g., by user ID)
        String whereClause = DBHelper.Column_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(currentuser.getId())};

        // Update the user information in the database
        int rowsUpdated = database.update(DBHelper.TABLE_NAME, values, whereClause, whereArgs);

        // Close the database connection
        database.close();

        // Check if the update was successful
        if (rowsUpdated > 0) {
            Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UserEditDetails.this, UserViewDetails.class);
            startActivity(intent);
            finish(); // Close the current activity
        } else {
            Toast.makeText(this, "Failed to update profile. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

}
