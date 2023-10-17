package com.example.etrainbooking.UserController;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

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

public class UserViewDetails extends AppCompatActivity {

    private ImageView profileImage;
    private TextView userNameTextView,
            nameLabelTextView, nameValueTextView,
            mobileLabelTextView, mobileValueTextView,
            nicLabelTextView, nicValueTextView,
            addressLabelTextView, addressValueTextView;
    private Button goEdit,goDelete;
    private SessionManager sessionManager;
    private DBHelper dbHelper;
    APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_prof);

        // Initialize views
        profileImage = findViewById(R.id.profile_image);
        userNameTextView = findViewById(R.id.user_name);
        nameLabelTextView = findViewById(R.id.name_label);
        nameValueTextView = findViewById(R.id.name_value);
        mobileLabelTextView = findViewById(R.id.mobile_label);
        mobileValueTextView = findViewById(R.id.mobile_value);
        nicLabelTextView = findViewById(R.id.nic_label);
        nicValueTextView = findViewById(R.id.nic_value);
        addressLabelTextView = findViewById(R.id.address_label);
        addressValueTextView = findViewById(R.id.address_value);
        goEdit = findViewById(R.id.update_button);
        goDelete = findViewById(R.id.delete_button);

        // Initialize the SessionManager
        sessionManager = new SessionManager(getApplicationContext());

        // Initialize DBHelper
        dbHelper = new DBHelper(this);

        // Create a Retrofit instance for the MongoDB API
        apiInterface = APIClient.getClient().create(APIInterface.class);

        // Check if the user is logged in
        if (sessionManager.isUserLoggedIn()) {
            // Retrieve user id
            String userNic = sessionManager.getUserNic();

            // Use the user's email to retrieve user details from your database
            User user = dbHelper.getUserByNIC(userNic);

            // Check network availability
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                // Device is online, fetch user data from the API
                fetchUserDataFromAPI(user.getNic());
            } else {
                // Device is offline, fetch user data from the SQLite database
                fetchUserDataFromSQLite(userNic);
            }
        } else {
            Intent intent = new Intent(UserViewDetails.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    // Implement the fetchUserDataFromAPI method to fetch user data from the API
    private void fetchUserDataFromAPI(String nic) {
        Call<User> call = apiInterface.getUserByNIC(nic);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();

                    if (user != null) {
                        userNameTextView.setText(user.getUserName());
                        nameValueTextView.setText(user.getName());
                        mobileValueTextView.setText(user.getContactNo());
                        nicValueTextView.setText(user.getNic());
                        addressValueTextView.setText(user.getAddress());

                        goEdit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(UserViewDetails.this, UserEditDetails.class);
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
                    Toast.makeText(UserViewDetails.this, "Failed to fetch user data from API. Please try again.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(UserViewDetails.this, "Network request failed. Please check your internet connection.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchUserDataFromSQLite(String userNic){
        // Use the user's email to retrieve user details from your database
        User user = dbHelper.getUserByNIC(userNic);

        if (user != null) {
            userNameTextView.setText(user.getUserName());
            nameValueTextView.setText(user.getName());
            mobileValueTextView.setText(user.getContactNo());
            nicValueTextView.setText(user.getNic());
            addressValueTextView.setText(user.getAddress());

            goEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(UserViewDetails.this, UserEditDetails.class);
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

        } else{
            Toast.makeText(UserViewDetails.this, "User details not found, Please Try Again.", Toast.LENGTH_LONG).show();
        }
    }


    // Implement the deleteUser method to delete the user from the database
    private void deleteUser(User user) {
        // Create or open the SQLite database
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Specify the WHERE clause to identify the row to delete (e.g., by user ID)
        String whereClause = DBHelper.Column_NIC + " = ?";
        String[] whereArgs = new String[]{user.getNic()};

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
