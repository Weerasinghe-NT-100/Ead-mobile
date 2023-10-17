package com.example.etrainbooking.ReservationController;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.etrainbooking.Auth.Login;
import com.example.etrainbooking.Auth.SessionManager;
import com.example.etrainbooking.DBHelper.DBHelper;
import com.example.etrainbooking.Home;
import com.example.etrainbooking.R;
import com.example.etrainbooking.UserController.User;
import com.example.etrainbooking.UserController.UserViewDetails;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PastReservations extends AppCompatActivity {

    private TextView userTittle;
    private RecyclerView recyclerView;
    FloatingActionButton btnLogout;
    LinearLayout empty_imageview;
    ScrollView scrollView;
    private SessionManager sessionManager;
    private DBHelper dbHelper;

    CustomAdapter customAdapter;
    ArrayList<Reservation> reservations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_res);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        userTittle = findViewById(R.id.txt_titleName);
        empty_imageview = findViewById(R.id.user_img);
        scrollView = findViewById(R.id.scrollView);
        btnLogout = findViewById(R.id.btn_Logout);

        // Initialize DBHelper
        dbHelper = new DBHelper(this);

        // Initialize the SessionManager
        sessionManager = new SessionManager(getApplicationContext());
        String userNic = sessionManager.getUserNic();

        // Fetch user details based on the email from the database
        User currentUser = dbHelper.getUserByNIC(userNic);

        userTittle.setText("Hey " + currentUser.getUserName());

        userTittle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserViewDetails.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.endSession();
                Intent intent = new Intent(PastReservations.this, Login.class);
                startActivity(intent);
            }
        });

        // Initialize the RecyclerView and layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reservations = new ArrayList<>(); // Initialize the list

        // Fetch and populate reservation data
        fetchReservationData(currentUser.getNic());

        // Initialize the custom adapter
        customAdapter = new CustomAdapter(PastReservations.this, reservations);
        recyclerView.setAdapter(customAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Handle any result from child activities here
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                recreate();
            }
        }
    }

    // Fetch reservation data from the database and update the list
    private void fetchReservationData(String nic) {
        // Replace this with your actual code to fetch reservation data
        // For example:
        reservations.clear();
        ArrayList<Reservation> allReservations = dbHelper.getReservationsByNIC(nic);

        // Get the current date as a Date object
        Date currentDate = Calendar.getInstance().getTime();

        // Define a date format to parse the stored date strings
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.US);

        for (Reservation reservation : allReservations) {
            try {
                // Parse the stored date string to a Date object
                Date reservationDate = dateFormat.parse(reservation.getDate());

                // Compare the reservation date with the current date
                if (reservationDate != null && reservationDate.before(currentDate)) {
                    // The reservation date is before the current date, so add it to the list
                    reservations.add(reservation);
                }
            } catch (ParseException e) {
                // Handle the case where the date string is not in the expected format
                e.printStackTrace();
                // You can log the error, display a message to the user, or handle it as needed
            }
        }

        if (reservations.isEmpty()) {
            empty_imageview.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        } else {
            empty_imageview.setVisibility(View.GONE);
        }

        // Notify the RecyclerView adapter of data changes
        //customAdapter.notifyDataSetChanged();
    }

}
