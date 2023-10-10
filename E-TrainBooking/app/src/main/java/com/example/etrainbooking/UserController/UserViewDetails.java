package com.example.etrainbooking.UserController;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import androidx.appcompat.app.AppCompatActivity;

import com.example.etrainbooking.Auth.Login;
import com.example.etrainbooking.Auth.SessionManager;
import com.example.etrainbooking.DBHelper.DBHelper;
import com.example.etrainbooking.R;

public class UserViewDetails extends AppCompatActivity {

    private ImageView profileImage;
    private TextView userNameTextView, emailLabelTextView, emailValueTextView,
            mobileLabelTextView, mobileValueTextView,
            nicLabelTextView, nicValueTextView,
            addressLabelTextView, addressValueTextView;
    private Button goEdit,goDelete;
    private SessionManager sessionManager;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_prof);

        // Initialize views
        profileImage = findViewById(R.id.profile_image);
        userNameTextView = findViewById(R.id.user_name);
        emailLabelTextView = findViewById(R.id.email_label);
        emailValueTextView = findViewById(R.id.email_value);
        mobileLabelTextView = findViewById(R.id.mobile_label);
        mobileValueTextView = findViewById(R.id.mobile_value);
        nicLabelTextView = findViewById(R.id.nic_label);
        nicValueTextView = findViewById(R.id.nic_value);
        goEdit = findViewById(R.id.update_button);
        goDelete = findViewById(R.id.delete_button);

        // Initialize the SessionManager
        sessionManager = new SessionManager(getApplicationContext());

        // Initialize DBHelper
        dbHelper = new DBHelper(this);

        // Check if the user is logged in
        if (sessionManager.isUserLoggedIn()) {
            // Retrieve user id
            String userId = sessionManager.getUserId();

            // Use the user's email to retrieve user details from your database
            User user = dbHelper.getUserById(userId);

            if (user != null) {
                userNameTextView.setText(user.getFname() + " " + user.getLname());
                emailValueTextView.setText(user.getEmail());
                mobileValueTextView.setText(user.getMobileNo());
                nicValueTextView.setText(user.getNic());

                goEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(UserViewDetails.this, UserEditDetails.class);
                        intent.putExtra("id", user.getId());
                        startActivity(intent);
                    }
                });

                // Set an OnClickListener for the updateButton
                goDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Handle the update logic here
                        showDeleteConfirmationDialog(user);
                    }
                });

            } else {
                Toast.makeText(UserViewDetails.this, "User details not found, Please Try Again.", Toast.LENGTH_LONG).show();
            }
        } else {
            Intent intent = new Intent(UserViewDetails.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    // Implement the deleteUser method to delete the user from the database
    private void deleteUser(User user) {
        // Create or open the SQLite database
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Specify the WHERE clause to identify the row to delete (e.g., by user ID)
        String whereClause = DBHelper.Column_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(user.getId())};

        // Delete the user from the database
        int rowsDeleted = database.delete(DBHelper.TABLE_NAME, whereClause, whereArgs);

        // Close the database connection
        database.close();

        // Check if the deletion was successful
        if (rowsDeleted > 0) {
            Toast.makeText(this, "User deleted successfully.", Toast.LENGTH_SHORT).show();

            sessionManager.endSession();
            Intent intent = new Intent(UserViewDetails.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Failed to delete user. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    // Implement the showDeleteConfirmationDialog method to display the confirmation dialog
    private void showDeleteConfirmationDialog(User currentUser) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete your account?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User confirmed the deletion, call the deleteUser method
                        deleteUser(currentUser);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User canceled the deletion, do nothing or provide feedback
                    }
                });
        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
