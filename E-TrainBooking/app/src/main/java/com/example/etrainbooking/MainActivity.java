package com.example.etrainbooking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.etrainbooking.Auth.Login;
import com.example.etrainbooking.Auth.Register;
import com.example.etrainbooking.TrainController.ScheduleView;
import com.example.etrainbooking.UserController.User;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    Button goSinup, goSignin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        goSignin = findViewById(R.id.btn_login);
        goSinup = findViewById(R.id.btn_signup);

        try
        {
            this.getSupportActionBar().hide();

        }
        catch (NullPointerException e){}

        SharedPreferences sharedPreferences = getSharedPreferences("UserSharedPref",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("Token", "");
        User obj = gson.fromJson(json, User.class);

        goSinup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);
            }
        });

        goSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
            }
        });

//        Intent intent = new Intent(this, Login.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
    }
}