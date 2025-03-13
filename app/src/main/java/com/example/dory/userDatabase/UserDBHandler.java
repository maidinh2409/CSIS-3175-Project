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

    public void addNewUser(User user){
        if(user.getName() == null || user.getEmail() == null || user.getPassword() == null || user.getRole() == null){
            throw new IllegalArgumentException("name, email, password, or role cannot be null");
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String salt = PasswordHandler.getNewSalt();
        String hash = PasswordHandler.hash(user.getPassword(), salt);

        values.put(NAME_COL, user.getName());
        values.put(EMAIL_COL, user.getEmail());
        values.put(PASSWORD_HASH_COL, hash);
        values.put(PASSWORD_SALT_COL, salt);
        values.put(ROLE_COL, user.getRole());
        values.put(PHOTO_COL, user.getProfilePhoto());
        values.put(ORGANIZATION_COL, user.getOrganizationName());
        values.put(CONTACT_COL, user.getContactInfo());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<UserHashed> getAllUsers(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorCourses = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        ArrayList<UserHashed> userHashedArray = new ArrayList<>();

        if(cursorCourses.moveToFirst()){
            do{
                userHashedArray.add(new UserHashed(
                        cursorCourses.getString(1),
                        cursorCourses.getString(2),
                        cursorCourses.getString(3),
                        cursorCourses.getString(4),
                        cursorCourses.getString(5),
                        cursorCourses.getString(6),
                        cursorCourses.getString(7),
                        cursorCourses.getString(8),
                        cursorCourses.getInt(0)
                ));
            } while (cursorCourses.moveToNext());
        }
        cursorCourses.close();
        return userHashedArray;
    }

    public UserHashed getUser(String email){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorCourses = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE email='" + email + "'", null);
        UserHashed userHashed = null;

        cursorCourses.moveToFirst();
        userHashed = new UserHashed(
                cursorCourses.getString(1),
                cursorCourses.getString(2),
                cursorCourses.getString(3),
                cursorCourses.getString(4),
                cursorCourses.getString(5),
                cursorCourses.getString(6),
                cursorCourses.getString(7),
                cursorCourses.getString(8),
                cursorCourses.getInt(0)
        );
        cursorCourses.close();
        return userHashed;
    }

    public void updateUser(User user){
        if(user.getName() == null || user.getEmail() == null || user.getPassword() == null || user.getRole() == null){
            throw new IllegalArgumentException("name, email, password, or role cannot be null");
        }
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME
                + "SET " + NAME_COL + " = " + user.getName()
                + ", " + ROLE_COL + " = " + user.getRole()
                + ", " + PHOTO_COL + " = " + user.getProfilePhoto()
                + ", " + ORGANIZATION_COL + " = " + user.getOrganizationName()
                + ", " + CONTACT_COL + " = " + user.getContactInfo();

        //check if password is changed and generate new salt and hash if it does
        if(!validateUser(user.getEmail(), user.getPassword())){
            String salt = PasswordHandler.getNewSalt();
            String hash = PasswordHandler.hash(user.getPassword(), salt);
            query += ", " + PASSWORD_SALT_COL + " = " + salt
             + ", " + PASSWORD_HASH_COL + " = " + hash;
        }
        query += " WHERE " + EMAIL_COL + " = " + user.getEmail() + ";";

        db.execSQL(query);
        db.close();
    }
    public boolean validateUser(String email, String password){
        UserHashed userHashed = getUser(email);
        String salt = userHashed.getSalt();
        String hash = userHashed.getHash();
        return PasswordHandler.validatePassword(password, salt, hash);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }
}
