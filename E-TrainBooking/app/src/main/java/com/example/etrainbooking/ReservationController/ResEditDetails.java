package com.example.etrainbooking.ReservationController;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.etrainbooking.Auth.Login;
import com.example.etrainbooking.Auth.SessionManager;
import com.example.etrainbooking.DBHelper.DBHelper;
import com.example.etrainbooking.R;

public class ResEditDetails extends AppCompatActivity {

    private EditText editTname, editTnic, editDate, editTrain;
    private Spinner editStart, editDestination;
    private Button updateButton;
    private SessionManager sessionManager;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_edit);

        // Initialize UI elements
        editTname = findViewById(R.id.edit_tname);
        editTnic = findViewById(R.id.edit_tnic);
        editTrain = findViewById(R.id.edit_train);
        editDate = findViewById(R.id.edit_date);
        editStart = findViewById(R.id.edit_start);
        editDestination = findViewById(R.id.edit_destination);
        updateButton = findViewById(R.id.update_button);

        // Initialize DBHelper
        dbHelper = new DBHelper(this);

        // Initialize the SessionManager
        sessionManager = new SessionManager(getApplicationContext());

        // Retrieve the reservation ID from the Intent
        String reservationId = getIntent().getStringExtra("id");

        // Fetch reservation details based on the ID from the database
        Reservation currentReservation = dbHelper.getReservationById(reservationId);

        if (sessionManager.isUserLoggedIn()) {

            // Check if the reservation was found
            if (currentReservation != null) {
                // Populate the input fields with the current reservation data
                editTname.setText(currentReservation.getTname());
                editTnic.setText(currentReservation.getTnic());
                editTrain.setText(currentReservation.getTrain());
                editDate.setText(currentReservation.getDate());

                // Debugging: Print the values to check if they are correct
                Log.d("SpinnerDebug", "Start: " + currentReservation.getStart());
                Log.d("SpinnerDebug", "Destination: " + currentReservation.getDestination());


                //Set the selected item for the start station spinner
                ArrayAdapter<CharSequence> startAdapter = ArrayAdapter.createFromResource(
                        this,
                        R.array.start_station_array,
                        android.R.layout.simple_spinner_item
                );
                startAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                editStart.setAdapter(startAdapter);

                //Set the selected item for the destination station spinner
                ArrayAdapter<CharSequence> destinationAdapter = ArrayAdapter.createFromResource(
                        this,
                        R.array.destination_station_array,
                        android.R.layout.simple_spinner_item
                );
                destinationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                editDestination.setAdapter(destinationAdapter);

                // Set an OnClickListener for the updateButton
                updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Handle the update logic here
                        updateReservationInfo(currentReservation);
                    }
                });
            } else {
                Toast.makeText(this, "Reservation not found.", Toast.LENGTH_SHORT).show();
            }
        }else {
            Intent intent = new Intent(ResEditDetails.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    // Implement the updateReservationInfo method to update reservation information in the database
    private void updateReservationInfo(Reservation currentReservation) {
        String newTname = editTname.getText().toString();
        String newTnic = editTnic.getText().toString();
        String newDate = editDate.getText().toString();
        String newTrain = editTrain.getText().toString();
        String newStart = editStart.getSelectedItem().toString();
        String newDestination = editDestination.getSelectedItem().toString();

        // Check if any of the fields are empty (you can add more validation as needed)
        if (TextUtils.isEmpty(newTname) || TextUtils.isEmpty(newTnic) || TextUtils.isEmpty(newDate) ||
                TextUtils.isEmpty(newTrain) || TextUtils.isEmpty(newStart) || TextUtils.isEmpty(newDestination)) {
            Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return; // Exit without updating if any field is empty
        }

        // Create or open the SQLite database
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Create a ContentValues object to store the new values
        ContentValues values = new ContentValues();
        values.put(DBHelper.Column_Tname, newTname);
        values.put(DBHelper.Column_Tnic, newTnic);
        values.put(DBHelper.Column_Date, newDate);
        values.put(DBHelper.Column_Train, newTrain);
        values.put(DBHelper.Column_Start, newStart);
        values.put(DBHelper.Column_Destination, newDestination);

        // Specify the WHERE clause to identify the row to update (e.g., by reservation ID)
        String whereClause = DBHelper.Column_TID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(currentReservation.getTid())};

        // Update the reservation information in the database
        int rowsUpdated = database.update(DBHelper.TABLE_NAME1, values, whereClause, whereArgs);

        // Close the database connection
        database.close();

        // Check if the update was successful
        if (rowsUpdated > 0) {
            Toast.makeText(this, "Booking updated successfully.", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ResEditDetails.this, ResViewDetails.class);
            intent.putExtra("id", String.valueOf(currentReservation.getTid()));
            startActivity(intent);
            finish(); // Close the current activity after updating
        } else {
            Toast.makeText(this, "Failed to update the booking. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
