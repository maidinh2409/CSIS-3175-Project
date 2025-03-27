package com.example.dory.userDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * A database handler class used to create and manage a database of user which extends SQLiteOpenHelper.
 */
public class UserDBHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "user.db";
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

    /**
     * Constructs the user database.
     * @param context the context of the app
     */
    public UserDBHandler(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * Creates a user database upon initializing an object.
     * @param db this SQLiteDatabase object
     */
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

    /**
     * Adds a user to the database.
     * @param user a User object containing the user details. Name, email, password and role are mandatory,
     *             while photo, organization and contact are optional (in which case they're null)
     * @return true if successful and false if fail
     * @throws IllegalArgumentException if the name, email, password, or role value of the User is null
     */
    public boolean addNewUser(User user){
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

        long r = db.insert(TABLE_NAME, null, values);
        db.close();
        if(r>0) return true;
        return false;
    }

    /**
     * Fetches all users in the database.
     * @return an ArrayList of UserHashed objects
     */
    public ArrayList<UserHashed> getAllUsers(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        ArrayList<UserHashed> userHashedArray = new ArrayList<>();

        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            do{
                userHashedArray.add(new UserHashed(
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getInt(0)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return userHashedArray;
    }

    /**
     * Fetches a user from the database based on their email.
     * @param email the user's email
     * @return a UserHashed object containing the details of the fetched user or null if none is found.
     */
    public UserHashed getUserFromEmail(String email){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +  EMAIL_COL + "=?", new String[]{email});
        UserHashed userHashed = null;

        if(cursor.getCount()>0) {
            cursor.moveToFirst();
            userHashed = new UserHashed(
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getInt(0)
            );
        }
        cursor.close();
        return userHashed;
    }

    /**
     * Returns true if a user with the specified email exists in the database and false if not
     * @param email the email of the user
     * @return true if a user with said email exists in the database, false if not
     */
    public boolean userExists(String email){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +  EMAIL_COL + "=?", new String[]{email});
        boolean exists;

        if(cursor.getCount()>0) {
            exists = true;
        }
        else{
            exists = false;
        }
        cursor.close();
        return exists;
    }

    /**
     * Fetches a user from the database based on their user id.
     * @param id the user id
     * @return a UserHashed object containing the details of the fetched user.
     */
    public UserHashed getUserFromId(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +  ID_COL + "=?", new String[]{Integer.toString(id)});
        UserHashed userHashed = null;

        if(cursor.getCount()>0) {
            cursor.moveToFirst();
            userHashed = new UserHashed(
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getInt(0)
            );
        }
        cursor.close();
        return userHashed;
    }

    /**
     * Replaces the value of the designated email with the values in the User parameter.
     * Any parameter that is not null will replace an existing value with a new one.
     * The email value is what's used to locate the row and thus cannot be changed by this method.
     * This method also cannot change the password.
     * @param user a User object containing the updated values.
     * @return true if successful and false if not
     * @throws IllegalArgumentException if the email value of the User object is null
     */
    public boolean updateUser(User user){
        if(user.getEmail() == null || !userExists(user.getEmail())){
            throw new IllegalArgumentException("User with that email cannot be found");
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if(user.getName() != null){
            values.put(NAME_COL, user.getName());
        }
        if(user.getRole() != null){
            values.put(ROLE_COL, user.getRole());
        }
        if(user.getProfilePhoto() != null){
            values.put(PHOTO_COL, user.getProfilePhoto());
        }
        if(user.getOrganizationName() != null){
            values.put(ORGANIZATION_COL, user.getOrganizationName());
        }
        if(user.getContactInfo() != null){
            values.put(CONTACT_COL, user.getContactInfo());
        }

        long r = db.update(TABLE_NAME, values, EMAIL_COL+"=?", new String[]{user.getEmail()});
        db.close();
        if(r>0) return true;
        return false;
    }

    /**
     * Generates a new salt and hash based on the given password which then replaces the existing
     * salt and hash of the user with the given email.
     * @param email the email of the user whose password will be changed
     * @param password the new password
     * @return true if successful and false if unsuccessful or if a user with the given email cannot be found in the database
     */
    public boolean updateUserPassword(String email, String password){
        if(email == null || !userExists(email)){
            return false;
        }
        String salt = PasswordHandler.getNewSalt();
        String hash = PasswordHandler.hash(password, salt);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PASSWORD_SALT_COL, salt);
        values.put(PASSWORD_HASH_COL, hash);
        long r = db.update(TABLE_NAME, values, EMAIL_COL+"=?", new String[]{email});
        db.close();
        if(r>0) return true;
        return false;
    }

    /**
     * Returns true of the password matches the hash in the database retrieved from the email value.
     * Returns false otherwise
     * @param email the user's email to be verified
     * @param password the password to be authenticated
     * @return true if the email and password matches, false otherwise
     */
    public boolean validateUser(String email, String password){
        if(email == null || !userExists(email){
            return false;
        }
        UserHashed userHashed = getUserFromEmail(email);
        if(userHashed == null) return false;
        String salt = userHashed.getSalt();
        String hash = userHashed.getHash();
        return PasswordHandler.validatePassword(password, salt, hash);
    }

    /**
     * Upgrades the database
     * @param db the database
     * @param oldVersion the old version number
     * @param newVersion the new version number
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }
}
