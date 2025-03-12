package com.example.dory.userDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class UserDBHandler extends  SQLiteOpenHelper {
    private static final String DB_NAME = "userdb";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "user_table";
    private static final String ID_COL = "user_id";
    private static final String NAME_COL = "name";
    private static final String EMAIL_COL = "email";
    private static final String PASSWORD_SALT_COL = "password_salt";
    private static final String PASSWORD_HASH_COL = "password_hash";
    private static final String PHOTO_COL = "profilePhoto";
    private static final String ROLE_COL = "role";
    private static final String ORGANIZATION_COL = "organizationName";
    private static final String CONTACT_COL = "contactInfo";

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
                + PHOTO_COL + " VARCHAR(50), "
                + ORGANIZATION_COL + " VARCHAR(100), "
                + CONTACT_COL + " VARCHAR(50))";

        db.execSQL(query);
    }

    public void addNewUser(String name, String email, String password, String role){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String salt = PasswordHandler.getNewSalt();
        String hash = PasswordHandler.hash(password, salt);

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

        String salt = PasswordHandler.getNewSalt();
        String hash = PasswordHandler.hash(password, salt);

        values.put(NAME_COL, name);
        values.put(EMAIL_COL, email);
        values.put(PASSWORD_HASH_COL, hash);
        values.put(PASSWORD_SALT_COL, salt);
        values.put(ROLE_COL, role);
        values.put(PHOTO_COL, photo);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<User> getAllUsers(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorCourses = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        ArrayList<User> userArray = new ArrayList<>();

        if(cursorCourses.moveToFirst()){
            do{
                userArray.add(new User(
                        cursorCourses.getString(1),
                        cursorCourses.getString(2),
                        cursorCourses.getString(3),
                        cursorCourses.getString(4),
                        cursorCourses.getString(5),
                        cursorCourses.getString(6)
                ));
            } while (cursorCourses.moveToNext());
        }
        cursorCourses.close();
        return userArray;
    }

    public User getUser(String email){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorCourses = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE email='" + email + "'", null);
        User user = null;

        cursorCourses.moveToFirst();
        user = new User(
                cursorCourses.getString(1),
                cursorCourses.getString(2),
                cursorCourses.getString(3),
                cursorCourses.getString(4),
                cursorCourses.getString(5),
                cursorCourses.getString(6)
        );
        cursorCourses.close();
        return user;
    }

    public boolean validateUser(String email, String password){
        User user = getUser(email);
        String salt = user.getSalt();
        String hash = user.getHash();
        return PasswordHandler.validatePassword(password, salt, hash);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }
}
