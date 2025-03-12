package com.example.dory.userDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDBHandler extends  SQLiteOpenHelper {
    private static final String DB_NAME = "userdb";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "usertable";
    private static final String ID_COL = "id";
    private static final String NAME_COL = "name";
    private static final String EMAIL_COL = "email";
    private static final String PASSWORD_SALT_COL = "passwordsalt";
    private static final String PASSWORD_HASH_COL = "passwordhash";
    private static final String PHOTO_COL = "profilephoto";
    private static final String ROLE_COL = "role";

    public UserDBHandler(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME_COL + " VARCHAR(50) NOT NULL, "
                + EMAIL_COL + " VARCHAR(50) NOT NULL UNIQUE, "
                + PASSWORD_HASH_COL + " CHAR(64) NOT NULL, "
                + PASSWORD_SALT_COL + " CHAR(32) NOT NULL, "
                + ROLE_COL + " VARCHAR(9) NOT NULL, "
                + PHOTO_COL + " VARCHAR(50)) ";

        db.execSQL(query);
    }

    public void addNewUser(String name, String email, String password, String role){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        byte[] salt = PasswordHandler.getNewSalt();
        byte[] hash = PasswordHandler.hash(password.toCharArray(), salt);

        values.put(NAME_COL, name);
        values.put(EMAIL_COL, email);
        values.put(PASSWORD_HASH_COL, hash);
        values.put(PASSWORD_SALT_COL, salt);
        values.put(ROLE_COL, role);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void addNewUser(String name, String email, String password, String role, String photo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        byte[] salt = PasswordHandler.getNewSalt();
        byte[] hash = PasswordHandler.hash(password.toCharArray(), salt);

        values.put(NAME_COL, name);
        values.put(EMAIL_COL, email);
        values.put(PASSWORD_HASH_COL, hash);
        values.put(PASSWORD_SALT_COL, salt);
        values.put(ROLE_COL, role);
        values.put(PHOTO_COL, photo);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }
}
