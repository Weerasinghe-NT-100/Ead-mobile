package com.example.etrainbooking.ReservationController;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.etrainbooking.Auth.Login;
import com.example.etrainbooking.Auth.Register;
import com.example.etrainbooking.Auth.SessionManager;
import com.example.etrainbooking.DBHelper.DBHelper;
import com.example.etrainbooking.Home;
import com.example.etrainbooking.R;
import com.example.etrainbooking.UserController.User;
import com.example.etrainbooking.UserController.UserEditDetails;
import com.example.etrainbooking.UserController.UserViewDetails;

public class ResViewDetails extends AppCompatActivity {

    private TextView txtTname, txtTrainId, txtTnic, txtTrain, txtDate, txtStart, txtDestination;
    private Button goEdit, goDelete;
    private SessionManager sessionManager;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_view);

        // Initialize views
        txtTrainId = findViewById(R.id.txt_trainid);
        txtTname = findViewById(R.id.txtTname);
        txtTnic = findViewById(R.id.txtTnic);
        txtTrain = findViewById(R.id.txtTrain);
        txtDate = findViewById(R.id.txtDate);
        txtStart = findViewById(R.id.txtStart);
        txtDestination = findViewById(R.id.txtDestination);
        goEdit = findViewById(R.id.update_button);
        goDelete = findViewById(R.id.delete_button);

        dbHelper = new DBHelper(this);

        // Initialize the SessionManager
        sessionManager = new SessionManager(getApplicationContext());

        // Check if the user is logged in
        if (sessionManager.isUserLoggedIn()) {

            // Retrieve the email from the Intent
            String reservationId = getIntent().getStringExtra("id");

            if(reservationId != null){
                // Use the user's email to retrieve user details from your database
                Reservation reservation = dbHelper.getReservationById(reservationId);

                if (reservation != null) {
                    txtTrainId.setText("Res ID: " + reservation.getTid());
                    txtTname.setText(reservation.getTname());
                    txtTnic.setText(reservation.getTnic());
                    txtTrain.setText(reservation.getTrain());
                    txtDate.setText(reservation.getDate());
                    txtStart.setText(reservation.getStart());
                    txtDestination.setText(reservation.getDestination());

                    goEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ResViewDetails.this, ResEditDetails.class);
                            intent.putExtra("id", String.valueOf(reservation.getTid()));
                            startActivity(intent);
                        }
                    });

                    // Set an OnClickListener for the updateButton
                    goDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Handle the update logic here
                            showDeleteConfirmationDialog(reservation);
                        }
                    });

                } else {
                    Toast.makeText(ResViewDetails.this, "Booking details not found, Please Try Again.", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(ResViewDetails.this, "Error", Toast.LENGTH_LONG).show();
            }
        } else {
            Intent intent = new Intent(ResViewDetails.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    // Implement the deleteUser method to delete the user from the database
    private void deleteBooking(Reservation reservation) {
        // Create or open the SQLite database
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Specify the WHERE clause to identify the row to delete (e.g., by user ID)
        String whereClause = DBHelper.Column_TID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(reservation.getTid())};

        // Delete the user from the database
        int rowsDeleted = database.delete(DBHelper.TABLE_NAME1, whereClause, whereArgs);

        // Close the database connection
        database.close();

        // Check if the deletion was successful
        if (rowsDeleted > 0) {
            Toast.makeText(this, "Booking deleted successfully.", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ResViewDetails.this, Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Failed to delete booking. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    // Implement the showDeleteConfirmationDialog method to display the confirmation dialog
    private void showDeleteConfirmationDialog(Reservation currentreservation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to Cancel your Booking?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User confirmed the deletion, call the deleteUser method
                        deleteBooking(currentreservation);
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
