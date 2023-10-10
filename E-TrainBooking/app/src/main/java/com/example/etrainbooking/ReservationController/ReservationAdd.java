package com.example.etrainbooking.ReservationController;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.etrainbooking.DBHelper.DBHelper;
import com.example.etrainbooking.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.provider.Settings.System.getInt;
import static com.example.etrainbooking.DBHelper.DBHelper.Column_TID;

public class ReservationAdd extends AppCompatActivity {

    private EditText editTname, editTnic, editDate, editTrain;
    private Spinner editStart, editDestination;
    private Button addButton;
    private DBHelper dbHelper;
    SQLiteDatabase sqLiteDatabaseObj;
    String tname, tnic, train, date, start, destination;
    Button dateButton;
    Boolean EditTextEmptyHolder;
    String SQLiteDataBaseQueryHolder ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        // Initialize UI elements
        editTname = findViewById(R.id.edit_tname);
        editTnic = findViewById(R.id.edit_tnic);
        editTrain = findViewById(R.id.edit_train);
        editStart = findViewById(R.id.edit_start);
        editDestination = findViewById(R.id.edit_destination);
        editDate = findViewById(R.id.edit_date);
        addButton = findViewById(R.id.btn_register);
        dateButton = findViewById(R.id.btn_date_picker);

        // Initialize DBHelper
        dbHelper = new DBHelper(this);

        // Set up Spinner for Train Selection
        ArrayAdapter<CharSequence> trainAdapter = ArrayAdapter.createFromResource(this,
                R.array.start_station_array, android.R.layout.simple_spinner_item);
        trainAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editStart.setAdapter(trainAdapter);

        // Set up Spinner for Start Station Selection
        ArrayAdapter<CharSequence> startAdapter = ArrayAdapter.createFromResource(this,
                R.array.destination_station_array, android.R.layout.simple_spinner_item);
        startAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editDestination.setAdapter(startAdapter);

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        // Set an OnClickListener for the addButton
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println("SQLiteDBBuild");
                SQLiteDBBuild();

                // Creating SQLite table if dose n't exists.
                System.out.println("SQLiteDBTableBuild");
                SQLiteDBTableBuild();

                // Checking EditText is empty or Not.
                System.out.println("CheckEditTextStatus");
                CheckEditTextStatus();

                // Handle the reservation creation logic here
                addReservation();
            }
        });
    }

    public void SQLiteDBBuild(){
        sqLiteDatabaseObj = openOrCreateDatabase(DBHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
    }
    private void SQLiteDBTableBuild() {
        sqLiteDatabaseObj.execSQL("CREATE TABLE IF NOT EXISTS "
                + DBHelper.TABLE_NAME1
                + "(" + Column_TID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DBHelper.Column_Tname + " VARCHAR, "
                + DBHelper.Column_Tnic + " VARCHAR, "
                + DBHelper.Column_Train + " VARCHAR, "
                + DBHelper.Column_Date + " VARCHAR, "
                + DBHelper.Column_Start + " VARCHAR, "
                + DBHelper.Column_Destination + " VARCHAR);");
    }

    private void CheckEditTextStatus() {
        // Getting value from All EditText and storing into String Variables.
        tname = editTname.getText().toString();
        tnic = editTnic.getText().toString();
        train = editTrain.getText().toString();
        date = editDate.getText().toString();
        start = editStart.getSelectedItem().toString();
        destination = editDestination.getSelectedItem().toString();

        if (TextUtils.isEmpty(tnic) || TextUtils.isEmpty(start) || TextUtils.isEmpty(destination) || TextUtils.isEmpty(date)) {
            EditTextEmptyHolder = false;
        } else {
            EditTextEmptyHolder = true;
        }

    }

    // Implement the addReservation method to add a new reservation to the database
    @SuppressLint("Range")
    private void addReservation() {
        long reservationId = -1;
        // If editText is not empty then this block will be executed.
        if (EditTextEmptyHolder) {
            // Check if the NIC already has 5 or more reservations
            int numberOfReservations = dbHelper.getReservationCountForNIC(tnic);

            if (numberOfReservations >= 5) {
                // Display a message indicating that the user cannot add more reservations
                Toast.makeText(ReservationAdd.this, "You cannot add more than 5 reservations.", Toast.LENGTH_LONG).show();
            } else {
                // SQLite query to insert data into table.
                SQLiteDataBaseQueryHolder = "INSERT INTO " + DBHelper.TABLE_NAME1 + " (tname,tnic,train,date,start,destination) VALUES('" + tname + "','" + tnic + "', '" + train + "', '" + date + "', '" + start + "','" + destination + "');";
                // Executing query.
                sqLiteDatabaseObj.execSQL(SQLiteDataBaseQueryHolder);

                // Get the ID of the last inserted row
                Cursor cursor = sqLiteDatabaseObj.rawQuery("SELECT last_insert_rowid() as tid", null);
                if (cursor.moveToFirst()) {
                    reservationId = cursor.getLong(cursor.getColumnIndex("tid"));
                }

                // Closing SQLite database object.
                sqLiteDatabaseObj.close();
                cursor.close();

                // Printing toast message after done inserting.
                Toast.makeText(ReservationAdd.this, "Booking Added Successfully", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ReservationAdd.this, ResViewDetails.class);
                intent.putExtra("id", String.valueOf(reservationId));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        } else {
            // This block will execute if any of the registration EditText is empty.
            // Printing toast message if any of EditText is empty.
            Toast.makeText(ReservationAdd.this, "Please Fill All The Required Fields.", Toast.LENGTH_LONG).show();
        }
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Handle the selected date here
                        // Create a Calendar instance and set the selected date
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(Calendar.YEAR, year);
                        selectedDate.set(Calendar.MONTH, monthOfYear);
                        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Format the date as yyyy.MM.dd
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.US);
                        String formattedDate = sdf.format(selectedDate.getTime());

                        // Set the formatted date to the EditText
                        editDate.setText(formattedDate);

                        // Store the selected date as a Date object
                        Date dateObject = selectedDate.getTime();

                        // Now, you can use the 'dateObject' to store in the database as a Date
                    }
                },
                // Set the current date as the default
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }



}

