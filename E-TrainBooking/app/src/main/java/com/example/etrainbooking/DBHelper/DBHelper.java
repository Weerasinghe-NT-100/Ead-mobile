package com.example.etrainbooking.DBHelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.etrainbooking.ReservationController.Reservation;
import com.example.etrainbooking.UserController.User;

import java.util.ArrayList;
import java.util.UUID;

public class DBHelper extends SQLiteOpenHelper {

    UUID uuid = UUID.randomUUID();
    String uuidAsString = uuid.toString();

    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "TicketReservation.db";

  /*-----------------------Uers table------------------------*/
    public static final String TABLE_NAME="User";
    public static final String Column_Name="fname";
    public static final String Column_NIC="nic";
    public static final String Column_Mobile="contactNo";
    public static final String Column_Age="age";
    public static final String Column_Address="address";
    public static final String Column_Username="username";
    public static final String Column_Password = "password";
    public static final String Column_Role="role";
    public static final String Column_IsActive="isActive";

  /*-----------------------Reservation table------------------------*/

    public static final String TABLE_NAME1="Reservation";
    public static final String Column_TID="tid";
    public static final String Column_Tname="tname";
    public static final String Column_Tnic="tnic";
    public static final String Column_Train="train";
    public static final String Column_Date="date";
    public static final String Column_Start="start";
    public static final String Column_Destination="destination";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

/*-----------------------User functions------------------------*/

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                Column_Name + " VARCHAR, " +
                Column_NIC + " VARCHAR PRIMARY KEY, " +
                Column_Mobile + " VARCHAR, " +
                Column_Age + " VARCHAR, " +
                Column_Address + " VARCHAR, " +
                Column_Username + " VARCHAR, " +
                Column_Password + " TEXT, " +
                Column_Role + " VARCHAR, " +
                Column_IsActive + " VARCHAR)";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    @SuppressLint("Range")
    public User getUserByNIC(String nic) {
        User user = null;
        SQLiteDatabase database = this.getReadableDatabase();

        // Define the columns you want to retrieve
        String[] columns = {
                Column_Name,
                Column_NIC,
                Column_Mobile,
                Column_Age,
                Column_Address,
                Column_Username,
                Column_Password,
                Column_Role,
                Column_IsActive
        };

        // Define the selection criteria (WHERE clause)
        String selection = Column_NIC + " = ?";
        String[] selectionArgs = {nic};

        // Query the database
        Cursor cursor = database.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            user = new User(
                    cursor.getString(cursor.getColumnIndex(Column_NIC)),
                    cursor.getString(cursor.getColumnIndex(Column_Username)),
                    cursor.getString(cursor.getColumnIndex(Column_Password)),
                    cursor.getString(cursor.getColumnIndex(Column_Role)),
                    cursor.getString(cursor.getColumnIndex(Column_Mobile)),
                    cursor.getString(cursor.getColumnIndex(Column_Name)),
                    cursor.getString(cursor.getColumnIndex(Column_Age)),
                    cursor.getString(cursor.getColumnIndex(Column_Address)),
                    Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(Column_IsActive))
                    ));
            cursor.close();
        }

        return user;
    }

    @SuppressLint("Range")
    public User getUserByUserName(String username) {
        User user = null;
        SQLiteDatabase database = this.getReadableDatabase();

        // Define the columns you want to retrieve
        String[] columns = {
                Column_Name,
                Column_NIC,
                Column_Mobile,
                Column_Age,
                Column_Address,
                Column_Username,
                Column_Password,
                Column_Role,
                Column_IsActive
        };

        // Define the selection criteria (WHERE clause)
        String selection = Column_Username + " = ?";
        String[] selectionArgs = {username};

        // Query the database
        Cursor cursor = database.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            user = new User(
                    cursor.getString(cursor.getColumnIndex(Column_NIC)),
                    cursor.getString(cursor.getColumnIndex(Column_Username)),
                    cursor.getString(cursor.getColumnIndex(Column_Password)),
                    cursor.getString(cursor.getColumnIndex(Column_Role)),
                    cursor.getString(cursor.getColumnIndex(Column_Mobile)),
                    cursor.getString(cursor.getColumnIndex(Column_Name)),
                    cursor.getString(cursor.getColumnIndex(Column_Age)),
                    cursor.getString(cursor.getColumnIndex(Column_Address)),
                    Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(Column_IsActive))
                    ));
            cursor.close();
        }

        return user;
    }


    /*-----------------------Reservation functions------------------------*/
    public void onCreate1(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE1 = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME1 + " (" + Column_TID + " INTEGER PRIMARY KEY, " + Column_Tname + " VARCHAR," + Column_Tnic + " VARCHAR, " + Column_Train + " VARCHAR, " + Column_Date + " VARCHAR, " + Column_Start + " VARCHAR, " + Column_Destination + " VARCHAR)";
        sqLiteDatabase.execSQL(CREATE_TABLE1);
    }

    public void onUpgrade1(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME1);
        onCreate1(sqLiteDatabase);
    }

    @SuppressLint("Range")
    public Reservation getReservationById(String tid) {
        Reservation reservation = null;
        SQLiteDatabase database = this.getReadableDatabase();

        // Define the columns you want to retrieve
        String[] columns = {
                Column_TID,
                Column_Tname,
                Column_Tnic,
                Column_Train,
                Column_Date,
                Column_Start,
                Column_Destination
        };

        // Define the selection criteria (WHERE clause)
        String selection = Column_TID + " = ?";
        String[] selectionArgs = {tid};

        // Query the database
        Cursor cursor1 = database.query(TABLE_NAME1, columns, selection, selectionArgs, null, null, null);

        if (cursor1 != null && cursor1.moveToFirst()) {
            reservation = new Reservation(
                    cursor1.getInt(cursor1.getColumnIndex(Column_TID)),
                    cursor1.getString(cursor1.getColumnIndex(Column_Tname)),
                    cursor1.getString(cursor1.getColumnIndex(Column_Tnic)),
                    cursor1.getString(cursor1.getColumnIndex(Column_Train)),
                    cursor1.getString(cursor1.getColumnIndex(Column_Date)),
                    cursor1.getString(cursor1.getColumnIndex(Column_Start)),
                    cursor1.getString(cursor1.getColumnIndex(Column_Destination))
            );
            cursor1.close();
        }

        return reservation;
    }

    // DBHelper.java
    public String getReservationIdByNIC(String nic) {
        String reservationId = null;
        SQLiteDatabase database = this.getReadableDatabase();

        // Define the columns you want to retrieve
        String[] columns = {
                Column_TID
        };

        // Define the selection criteria (WHERE clause)
        String selection = Column_Tnic + " = ?";
        String[] selectionArgs = {nic};

        // Query the database
        Cursor cursor = database.query(TABLE_NAME1, columns, selection, selectionArgs, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(Column_TID);
                if (columnIndex != -1) {
                    reservationId = cursor.getString(columnIndex);
                }
            }
            cursor.close();
        }

        return reservationId;
    }

    @SuppressLint("Range")
    public ArrayList<Reservation> getReservationsByNIC(String nic) {
        ArrayList<Reservation> reservationList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM " + TABLE_NAME1 + " WHERE " + Column_Tnic + " = ?";
            cursor = db.rawQuery(query, new String[]{nic});

            while (cursor.moveToNext()) {
                Reservation reservation = new Reservation(
                        cursor.getInt(cursor.getColumnIndex(Column_TID)),
                        cursor.getString(cursor.getColumnIndex(Column_Tname)),
                        cursor.getString(cursor.getColumnIndex(Column_Tnic)),
                        cursor.getString(cursor.getColumnIndex(Column_Train)),
                        cursor.getString(cursor.getColumnIndex(Column_Date)),
                        cursor.getString(cursor.getColumnIndex(Column_Start)),
                        cursor.getString(cursor.getColumnIndex(Column_Destination))
                );

                reservationList.add(reservation);
            }
        } catch (Exception e) {
            // Handle any exceptions here
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return reservationList;
    }

    public int getReservationCountForNIC(String nic) {
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT COUNT(*) FROM " + TABLE_NAME1 + " WHERE " + Column_Tnic + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{nic});

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return count;
    }


}
