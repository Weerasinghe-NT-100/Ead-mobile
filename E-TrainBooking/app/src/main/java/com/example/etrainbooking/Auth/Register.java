package com.example.etrainbooking.Auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.etrainbooking.DBHelper.DBHelper;
import com.example.etrainbooking.Validations.Validations;
//import com.example.easyqueue.Home;
import com.example.etrainbooking.R;

import java.nio.charset.StandardCharsets;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class Register extends AppCompatActivity {
    private EditText Fname,Lname,Email,MobileNo,Nic,Password,ConfirmPassword;
    private Button Register;
    private TextView goLogin;
    String fname,lname,email,mobileNo,nic,password,confirmpassword;
    SQLiteDatabase sqLiteDatabaseObj;
    DBHelper DB;
    RequestQueue queue;
    String SQLiteDataBaseQueryHolder ;
    Boolean EditTextEmptyHolder;
    String F_Result = "Not_Found";
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

            queue = Volley.newRequestQueue(this);
            Fname= findViewById(R.id.edit_fname);
            Lname= findViewById(R.id.edit_lname);
            Email= findViewById(R.id.edit_email);
            MobileNo= findViewById(R.id.edit_mobile);
            Nic= findViewById(R.id.edit_nic);
            Password= findViewById(R.id.edit_password);
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

                System.out.println("SQLiteDBBuild");
                SQLiteDBBuild();
                // Creating SQLite table if dose n't exists.
                System.out.println("SQLiteDBTableBuild");
                SQLiteDBTableBuild();
                // Checking EditText is empty or Not.
                System.out.println("CheckEditTextStatus");
                CheckEditTextStatus();
                // Method to check Email is already exists or not.
                System.out.println("CheckingEmailExists");
                CheckingEmailExists();
                // Empty EditText After done inserting process.

            }
        });

    }

    public void SQLiteDBBuild(){
        sqLiteDatabaseObj = openOrCreateDatabase(DBHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
    }
    private void SQLiteDBTableBuild() {
        sqLiteDatabaseObj.execSQL("CREATE TABLE IF NOT EXISTS "
                + DBHelper.TABLE_NAME
                + "(" + DBHelper.Column_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DBHelper.Column_Fname + " VARCHAR, "
                + DBHelper.Column_Lname + " VARCHAR, "
                + DBHelper.Column_Email + " VARCHAR, "
                + DBHelper.Column_Mobile + " VARCHAR, "
                + DBHelper.Column_Nic + " VARCHAR, "
                + DBHelper.Column_Password + " VARCHAR);");
    }
    private void CheckEditTextStatus() {
        // Getting value from All EditText and storing into String Variables.
        fname = Fname.getText().toString();
        lname = Lname.getText().toString();
        email = Email.getText().toString();
        mobileNo = MobileNo.getText().toString();
        nic = Nic.getText().toString();
        password = Password.getText().toString();

        // Validate email
        if (!Validations.isEmailValid(email)) {
            Toast.makeText(Register.this, "Please enter a valid email address.", Toast.LENGTH_LONG).show();
            EditTextEmptyHolder = false;
            return;
        }

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

        if (TextUtils.isEmpty(fname) || TextUtils.isEmpty(password) || TextUtils.isEmpty(nic) || TextUtils.isEmpty(email)) {
            EditTextEmptyHolder = false;
        } else {
            EditTextEmptyHolder = true;
        }

    }
    private void CheckingEmailExists() {
        // Opening SQLite database write permission.
        sqLiteDatabaseObj = DB.getWritableDatabase();
        // Adding search email query to cursor.
        cursor = sqLiteDatabaseObj.query(DBHelper.TABLE_NAME, null, " " + DBHelper.Column_Email + "=?", new String[]{email}, null, null, null);
        while (cursor.moveToNext()) {
            if (cursor.isFirst()) {
                cursor.moveToFirst();
                // If Email is already exists then Result variable value set as Email Found.
                F_Result = "Email Found";
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
        if(F_Result.equalsIgnoreCase("Email Found"))
        {
            // If email is exists then toast msg will display.
            Toast.makeText(Register.this,"Email Already Exists",Toast.LENGTH_LONG).show();
        }
        else {
            // If email already dose n't exists then user registration details will entered to SQLite database.
            SubmitUser();
        }
        F_Result = "Not_Found" ;

    }
    private void ClearEditText() {
        Fname.getText().clear();
        Lname.getText().clear();
        Email.getText().clear();
        MobileNo.getText().clear();
        Nic.getText().clear();
        Password.getText().clear();

    }

    private void SubmitUser() {
        // If editText is not empty then this block will executed.
        if(EditTextEmptyHolder == true)
        {
            // Hash the password before storing it
            String hashedPassword = hashPassword(password);

            // SQLite query to insert data into table.
            SQLiteDataBaseQueryHolder = "INSERT INTO " +DBHelper.TABLE_NAME +" (fname,lname,email,mobileNo,nic,password) VALUES('" +fname +"','"+lname+"', '" +email +"', '" +mobileNo +"', '" +nic +"','"+hashedPassword+"');";
            // Executing query.
            sqLiteDatabaseObj.execSQL(SQLiteDataBaseQueryHolder);
            // Closing SQLite database object.
            sqLiteDatabaseObj.close();
            // Printing toast message after done inserting.
            Toast.makeText(Register.this,"User Registered Successfully", Toast.LENGTH_LONG).show();
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

}