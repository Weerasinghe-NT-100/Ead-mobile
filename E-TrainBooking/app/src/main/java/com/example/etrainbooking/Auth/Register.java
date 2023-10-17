package com.example.etrainbooking.Auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.etrainbooking.APIClient;
import com.example.etrainbooking.APIInterface;
import com.example.etrainbooking.DBHelper.DBHelper;
import com.example.etrainbooking.UserController.User;
import com.example.etrainbooking.Validations.Validations;
import com.example.etrainbooking.R;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import at.favre.lib.crypto.bcrypt.BCrypt;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Register extends AppCompatActivity {
    private EditText Fname,Nic,MobileNo,Age,Address,Username,Password,ConfirmPassword;
    private Button Register;
    private TextView goLogin;
    String fname,nic,mobileNo,age,address,username,password,confirmpassword,role;
    boolean isActive;
    SQLiteDatabase sqLiteDatabaseObj;
    DBHelper DB;
    RequestQueue queue;
    String SQLiteDataBaseQueryHolder ;
    Boolean EditTextEmptyHolder;
    String F_Result = "Not_Found";
    Cursor cursor;
    APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

            queue = Volley.newRequestQueue(this);
            Fname = findViewById(R.id.edit_fname);
            Nic = findViewById(R.id.edit_nic);
            MobileNo = findViewById(R.id.edit_mobile);
            Age = findViewById(R.id.edit_age);
            Address = findViewById(R.id.edit_address);
            Username = findViewById(R.id.edit_username);
            Password = findViewById(R.id.edit_password);
            ConfirmPassword = findViewById(R.id.edit_cpass);

            goLogin= findViewById(R.id.btn_gotoLogin);
            Register = findViewById(R.id.btn_register);

            DB = new DBHelper(this);

        goLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    System.out.println("SQLiteDBBuild");
                    SQLiteDBBuild();
                    // Creating SQLite table if it doesn't exist.
                    System.out.println("SQLiteDBTableBuild");
                    SQLiteDBTableBuild();
                    // Checking EditText is empty or Not.
                    System.out.println("CheckEditTextStatus");
                    CheckEditTextStatus();
                    // Method to check NIC is already exists or not.
                    System.out.println("CheckingNicExists");
                    CheckingNicExists();

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Register", "Error in Register button click: " + e.getMessage());
                    Toast.makeText(Register.this, "An error occurred during registration. Please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void SQLiteDBBuild(){
        sqLiteDatabaseObj = openOrCreateDatabase(DBHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
    }
    private void SQLiteDBTableBuild() {
        sqLiteDatabaseObj.execSQL("CREATE TABLE IF NOT EXISTS "
                + DBHelper.TABLE_NAME
                + "(" + DBHelper.Column_Name + " VARCHAR, "
                + DBHelper.Column_NIC + " VARCHAR PRIMARY KEY, "
                + DBHelper.Column_Mobile + " VARCHAR, "
                + DBHelper.Column_Age + " VARCHAR, "
                + DBHelper.Column_Address + " VARCHAR, "
                + DBHelper.Column_Username + " VARCHAR, "
                + DBHelper.Column_Password + " TEXT, "
                + DBHelper.Column_Role + " VARCHAR, "
                + DBHelper.Column_IsActive + " VARCHAR);");
    }
    private void CheckEditTextStatus() {
        // Getting value from All EditText and storing into String Variables.
        fname = Fname.getText().toString();
        nic = Nic.getText().toString();
        mobileNo = MobileNo.getText().toString();
        age = Age.getText().toString();
        address = Address.getText().toString();
        username = Username.getText().toString();
        password = Password.getText().toString();
        role = "traveller";
        isActive = true;

        // Validate NIC
        if (!Validations.isNICValid(nic)) {
            Toast.makeText(Register.this, "NIC must have 10-12 characters.", Toast.LENGTH_LONG).show();
            EditTextEmptyHolder = false;
            return;
        }

        // Validate phone number
        if (!Validations.isPhoneNumberValid(mobileNo)) {
            Toast.makeText(Register.this, "Phone number must have 10 characters.", Toast.LENGTH_LONG).show();
            EditTextEmptyHolder = false;
            return;
        }

        if (TextUtils.isEmpty(fname) || TextUtils.isEmpty(password) || TextUtils.isEmpty(nic) || TextUtils.isEmpty(nic)) {
            EditTextEmptyHolder = false;
        } else {
            EditTextEmptyHolder = true;
        }

    }

    private void CheckingNicExists() {
        // Opening SQLite database write permission.
        sqLiteDatabaseObj = DB.getWritableDatabase();
        // Adding search email query to cursor.
        cursor = sqLiteDatabaseObj.query(DBHelper.TABLE_NAME, null, " " + DBHelper.Column_NIC + "=?", new String[]{nic}, null, null, null);
        while (cursor.moveToNext()) {
            if (cursor.isFirst()) {
                cursor.moveToFirst();
                // If Email is already exists then Result variable value set as Email Found.
                F_Result = "NIC Found";
                // Closing cursor.
                cursor.close();
            }
        }
        // Calling method to check final result and insert data into SQLite database.
        CheckFinalResult();
    }
    // Checking result
    public void CheckFinalResult(){
        // Checking whether email is already exists or not.
        if(F_Result.equalsIgnoreCase("NIC Found"))
        {
            // If email is exists then toast msg will display.
            Toast.makeText(Register.this,"NIC Already Exists",Toast.LENGTH_LONG).show();
        }
        else {
            // If email already dose n't exists then user registration details will entered to SQLite database.
            SubmitUser();
        }
        F_Result = "Not_Found" ;

    }

    private void ClearEditText() {
        Fname.getText().clear();
        Nic.getText().clear();
        MobileNo.getText().clear();
        Age.getText().clear();
        Address.getText().clear();
        Username.getText().clear();
        Password.getText().clear();
        ConfirmPassword.getText().clear();

    }

    private void SubmitUser() {
        // If editText is not empty then this block will executed.
        if(EditTextEmptyHolder == true)
        {
            // Hash the password before storing it
            String hashedPassword = hashPassword(password);

            // SQLite query to insert data into the table.
            SQLiteDataBaseQueryHolder = "INSERT INTO " + DBHelper.TABLE_NAME + " (fname,nic,contactNo,age,address,username,password,role,isActive) VALUES('" + fname + "','" + nic + "', '" + mobileNo + "', '" + age + "','" + address + "', '" + username + "','"+hashedPassword+"', '" + role + "', '" + isActive + "');";
            // Executing query.
            sqLiteDatabaseObj.execSQL(SQLiteDataBaseQueryHolder);
            // Closing the SQLite database object.
            sqLiteDatabaseObj.close();

            // Add this line to make an API call to register the user in MongoDB
            registerUserInMongoDB(nic, username, hashedPassword, role, mobileNo, fname, age, address, isActive);

            // Printing a toast message after successful registration.
            Toast.makeText(Register.this, "User Registered Successfully", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Register.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            ClearEditText();
        }
        // This block will execute if any of the registration EditText is empty.
        else {
            // Printing toast message if any of EditText is empty.
            Toast.makeText(Register.this,"Please Fill All The Required Fields.", Toast.LENGTH_LONG).show();
        }
    }

    private String hashPassword(String plainPassword) {
        try {
            byte[] hashedPassword = BCrypt.withDefaults().hash(12, plainPassword.toCharArray());
            return new String(hashedPassword, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void registerUserInMongoDB(String nic, String username, String password, String role, String contactNo, String name, String age, String address, boolean isActive) {
        // Create a Retrofit instance for the MongoDB API
        apiInterface = APIClient.getClient().create(APIInterface.class);

        // Create a User object to send to the MongoDB API
        User user = new User(nic, username, password, role, contactNo, name, age, address, isActive);

        // Call the MongoDB API to register the user
        Call<User> call = apiInterface.registerUser(user);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    // Printing a toast message after successfully registering the user in MongoDB
                    Toast.makeText(Register.this, "User Registered online Successfully", Toast.LENGTH_LONG).show();
                } else {
                    int statusCode = response.code(); // Get the status code

                    // Registration failed, log the status code and response body
                    Log.e("Registration Error", "Status Code: " + statusCode);
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("Registration Error", "Error Response: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(Register.this, "User Registration Failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Registration request failed
                Toast.makeText(Register.this, "Registration Request Failed", Toast.LENGTH_LONG).show();
            }
        });
    }

}