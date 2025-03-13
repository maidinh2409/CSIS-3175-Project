package com.example.doryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
import androidx.core.database.sqlite.SQLiteDatabaseKt;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String dbName = "DoryApp.db";
    private static final int dbVersion = 1;

    private static final String table_users = "users";
    private static final String column_id = "id";
    private static final String column_name = "name";
    private static final String column_email = "email";
    private static final String column_password = "password";
    private static final String column_company = "company";
    private static final String column_role = "role";

    public DatabaseHelper(Context context) {
        super(context, dbName, null, dbVersion);
    }
    private static final String CREATE_USERS_TABLE =
            "CREATE TABLE " + table_users + " (" +
                    column_id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    column_name + " TEXT NOT NULL, " +
                    column_email + " TEXT UNIQUE NOT NULL, " +
                    column_password + " TEXT NOT NULL, " +
                    column_company + " TEXT NOT NULL, " +
                    column_role + " TEXT NOT NULL);";


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + table_users);
        onCreate(db);
    }

    public boolean registerUser(String name, String email, String password, String company, String role) {
        SQLiteDatabase db = this.getWritableDatabase();


        Cursor cursor = db.rawQuery("SELECT * FROM " + table_users + " WHERE " + column_email + " = ?", new String[]{email});
        if (cursor.getCount() > 0) {
            cursor.close();
            db.close();
            return false;
        }
        cursor.close();


        ContentValues values = new ContentValues();
        values.put(column_name, name);
        values.put(column_email, email);
        values.put(column_password, password);
        values.put(column_company, company);
        values.put(column_role, role);

        long result = db.insert(table_users, null, values);
        db.close();

        return result != -1;
    }

    public User checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + table_users + " WHERE " +
                        column_email + " = ? AND " + column_password + " = ?",
                new String[]{email, password});

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(0),     // ID
                    cursor.getString(1),  // Name
                    cursor.getString(2),  // Email
                    cursor.getString(3),  // Password
                    cursor.getString(4),  // Company
                    cursor.getString(5)   // Role
            );
        }

        cursor.close();
        db.close();
        return user;
    }

}
