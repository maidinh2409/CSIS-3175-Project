package com.example.dory.userDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * A database handler class used to create and manage a database of user which
 * extends SQLiteOpenHelper.
 */
public class UserDBHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "dory.db";
    private static final int DB_VERSION = 2;
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
    private static final String RESET_OTP_COL = "passwordResetOtp";
    private static final String RESET_TIME_COL = "passwordResetTimestamp";
    final static String TABLE1 = "EVENT"; // Table Event
    final static String T1COL1 = "event_id";
    final static String T1COL2 = "organizer_id";
    final static String T1COL3 = "title";
    final static String T1COL4 = "description";
    final static String T1COL5 = "startDate";
    final static String T1COL6 = "endDate";
    final static String T1COL7 = "location";
    final static String T1COL8 = "capacity";
    final static String TABLE2 = "INVITATION"; // Table Invitation
    final static String T2COL1 = "invitation_id";
    final static String T2COL2 = "event_id";
    final static String T2COL3 = "attendee_id";
    final static String T2COL4 = "status";
    final static String T2COL5 = "creationTime";
    final static String T2COL6 = "responseTime";

    /**
     * Constructs the user database.
     *
     * @param context the context of the app
     */
    public UserDBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * Creates a user database upon initializing an object.
     *
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
                + CONTACT_COL + " VARCHAR(50), "
                + RESET_OTP_COL + " VARCHAR(6),"
                + RESET_TIME_COL + " BIGINT DEFAULT -1"
                + ")";

        db.execSQL(query);

        query = "CREATE TABLE " + TABLE1 +
                "(" + T1COL1 + " INTEGER PRIMARY KEY," +
                T1COL2 + " INTEGER," +
                T1COL3 + " TEXT," +
                T1COL4 + " TEXT," +
                T1COL5 + " TEXT," +
                T1COL6 + " TEXT," +
                T1COL7 + " TEXT," +
                T1COL8 + " INTEGER)";
        db.execSQL(query);

        query = "CREATE TABLE " + TABLE2 +
                "(" + T2COL1 + " INTEGER PRIMARY KEY," +
                T2COL2 + " INTEGER," +
                T2COL3 + " INTEGER," +
                T2COL4 + " TEXT," +
                T2COL5 + " TEXT," + // CHECK IF DATETIME WORKS
                T2COL6 + " TEXT)"; // Use local date to convert from string and vice versa
        db.execSQL(query);
    }

    /**
     * Adds a user to the database.
     *
     * @param user a User object containing the user details. Name, email, password
     *             and role are mandatory,
     *             while photo, organization and contact are optional (in which case
     *             they're null)
     * @return true if successful and false if fail
     * @throws IllegalArgumentException if the name, email, password, or role value
     *                                  of the User is null
     */
    public boolean addNewUser(User user) {
        if (user.getName() == null || user.getEmail() == null || user.getPassword() == null || user.getRole() == null) {
            return false;
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
        if (user.getProfilePhoto() == null) {
            values.put(PHOTO_COL, (String) null);
        } else {
            values.put(PHOTO_COL, user.getProfilePhoto().toString());
        }
        values.put(ORGANIZATION_COL, user.getOrganizationName());
        values.put(CONTACT_COL, user.getContactInfo());

        long r = db.insert(TABLE_NAME, null, values);
        db.close();
        if (r > 0)
            return true;
        return false;
    }

    /**
     * Fetches all users in the database.
     *
     * @return an ArrayList of UserHashed objects
     */
    public ArrayList<UserHashed> getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        ArrayList<UserHashed> userHashedArray = new ArrayList<>();

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                if (cursor.getString(6) == null) {
                    userHashedArray.add(new UserHashed(
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5),
                            null,
                            cursor.getString(7),
                            cursor.getString(8),
                            cursor.getInt(0)));
                } else {
                    userHashedArray.add(new UserHashed(
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5),
                            cursor.getString(6),
                            cursor.getString(7),
                            cursor.getString(8),
                            cursor.getInt(0)));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return userHashedArray;
    }

    /**
     * Fetches a user from the database based on their email.
     *
     * @param email the user's email
     * @return a UserHashed object containing the details of the fetched user or
     * null if none is found.
     */
    public UserHashed getUserFromEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + EMAIL_COL + "=?",
                new String[]{email});
        UserHashed userHashed = null;

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            if (cursor.getString(6) == null) {
                userHashed = new UserHashed(
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        null,
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getInt(0));
            } else {
                userHashed = new UserHashed(
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getInt(0));
            }
        }
        cursor.close();
        return userHashed;
    }

    /**
     * Returns true if a user with the specified email exists in the database and
     * false if not
     *
     * @param email the email of the user
     * @return true if a user with said email exists in the database, false if not
     */
    public boolean userExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + EMAIL_COL + "=?",
                new String[]{email});
        boolean exists;

        if (cursor.getCount() > 0) {
            exists = true;
        } else {
            exists = false;
        }
        cursor.close();
        return exists;
    }

    /**
     * Fetches a user from the database based on their user id.
     *
     * @param id the user id
     * @return a UserHashed object containing the details of the fetched user.
     */
    public UserHashed getUserFromId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + ID_COL + "=?",
                new String[]{Integer.toString(id)});
        UserHashed userHashed = null;

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            if (cursor.getString(6) == null) {
                userHashed = new UserHashed(
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        null,
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getInt(0));
            } else {
                userHashed = new UserHashed(
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getInt(0));
            }
        }
        cursor.close();
        return userHashed;
    }

    /**
     * Replaces the value of the designated email with the values in the User
     * parameter.
     * Any parameter that is not null will replace an existing value with a new one.
     * The email value is what's used to locate the row and thus cannot be changed
     * by this method.
     * This method also cannot change the password.
     *
     * @param user a User object containing the updated values.
     * @return true if successful and false if not
     * @throws IllegalArgumentException if the email value of the User object is
     *                                  null
     */
    public boolean updateUser(User user) {
        if (user.getEmail() == null || !userExists(user.getEmail())) {
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (user.getName() != null) {
            values.put(NAME_COL, user.getName());
        }
        if (user.getRole() != null) {
            values.put(ROLE_COL, user.getRole());
        }
        if (user.getProfilePhoto() != null) {
            values.put(PHOTO_COL, user.getProfilePhoto().toString());
        }
        if (user.getOrganizationName() != null) {
            values.put(ORGANIZATION_COL, user.getOrganizationName());
        }
        if (user.getContactInfo() != null) {
            values.put(CONTACT_COL, user.getContactInfo());
        }

        long r = db.update(TABLE_NAME, values, EMAIL_COL + "=?", new String[]{user.getEmail()});
        db.close();
        if (r > 0)
            return true;
        return false;
    }

    /**
     * Generates a new salt and hash based on the given password which then replaces
     * the existing
     * salt and hash of the user with the given email.
     *
     * @param email    the email of the user whose password will be changed
     * @param password the new password
     * @return true if successful and false if unsuccessful or if a user with the
     * given email cannot be found in the database
     */
    public boolean updateUserPassword(String email, String password) {
        if (email == null || !userExists(email)) {
            return false;
        }
        String salt = PasswordHandler.getNewSalt();
        String hash = PasswordHandler.hash(password, salt);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PASSWORD_SALT_COL, salt);
        values.put(PASSWORD_HASH_COL, hash);
        long r = db.update(TABLE_NAME, values, EMAIL_COL + "=?", new String[]{email});
        db.close();
        if (r > 0)
            return true;
        return false;
    }

    /**
     * Returns true of the password matches the hash in the database retrieved from
     * the email value.
     * Returns false otherwise
     *
     * @param email    the user's email to be verified
     * @param password the password to be authenticated
     * @return true if the email and password matches, false otherwise
     */
    public boolean validateUser(String email, String password) {
        if (email == null || !userExists(email)) {
            return false;
        }
        UserHashed userHashed = getUserFromEmail(email);
        if (userHashed == null)
            return false;
        String salt = userHashed.getSalt();
        String hash = userHashed.getHash();
        return PasswordHandler.validatePassword(password, salt, hash);
    }

    /**
     * Creates an OTP with a length of 6 characters, saves it along with the time of creation in the database
     * @param email the email address of the user whose password is being reset.
     * @return the OTP code if successful, NULL if unsuccessful.
     */
    public String generateOtp(String email){
        return generateOtp(email, 6);
    }

    /**
     * Creates an OTP with a custom length, saves it along with the time of creation in the database
     * @param email the email address of the user whose password is being reset.
     * @param length the length of the OTP, must be >= 1.
     * @return the OTP code if successful, NULL if unsuccessful.
     */
    public String generateOtp(String email, int length){
        if (email == null || !userExists(email) || length < 1) {
            return null;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String otp = PasswordHandler.generateOtp(length);

        values.put(RESET_OTP_COL, otp);
        values.put(RESET_TIME_COL, System.currentTimeMillis());

        long r = db.update(TABLE_NAME, values, EMAIL_COL + "=?", new String[]{email});
        db.close();
        if (r > 0)
            return otp;
        return null;
    }

    /**
     * Receive the OTP code from the database.
     * @param email the email of the user whose OTP code you want to receive.
     * @return the OTP code as a String, or null if the user has no OTP or doesn't exist.
     */
    public String getOtp(String email){
        if (email == null || !userExists(email)) {
            return null;
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + EMAIL_COL + "=?",
                new String[]{email});
        String otp = null;

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            otp = cursor.getString(9);
        }
        cursor.close();
        return otp;
    }

    /**
     * Receive the OTP creation time from the database.
     * @param email the email of the user whose OTP creation time you want to receive.
     * @return the creation time as long, or -1 if it doesn't exist.
     */
    public long getOtpTime(String email){
        if (email == null || !userExists(email)) {
            return -1L;
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + EMAIL_COL + "=?",
                new String[]{email});
        long otpTime = -1;

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            otpTime = cursor.getLong(10);
        }
        cursor.close();
        return otpTime;
    }

    /**
     * Removes the OTP code and OTP creation time entry from the database.
     * @param email the email of the user whose OTP code and time you want to delete.
     * @return true if successful or false if not.
     */
    public boolean removeOtp(String email){
        if (email == null || !userExists(email)) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(RESET_OTP_COL, (String) null);
        values.put(RESET_TIME_COL, -1L);

        long r = db.update(TABLE_NAME, values, EMAIL_COL + "=?", new String[]{email});
        db.close();
        if (r > 0)
            return true;
        return false;
    }

    /**
     * Validates an OTP code against the OTP entry in the database.
     * This method has a time limit of 1 hour, meaning it will always return false if the method
     * is called against an otp generated more than 1 hour ago.
     * If the validation is successful then this method will also erase the OTP entry in the database.
     * Therefore this method can only return true once for each OTP generated.
     * @param email the email of the user whose OTP code you want to validate.
     * @param otp the OTP code that will be compared to the database entry, case sensitive.
     * @return true if the given OTP matches the database entry and the time difference doesn't
     * exceed the given time limit, returns false in every other case.
     */
    public boolean validateOtp(String email, String otp){
        return validateOtp(email, otp, 1);
    }

    /**
     * Validates an OTP code against the OTP entry in the database.
     * This method can be given a custom time limit, meaning it will always return false if the method
     * is called against an otp generated more than "timeLimitInHours" hours ago.
     * If the validation is successful then this method will also erase the OTP entry in the database.
     * Therefore this method can only return true once for each OTP generated.
     * @param email the email of the user whose OTP code you want to validate.
     * @param otp the OTP code that will be compared to the database entry, case sensitive.
     * @param timeLimitInHours the time limit of the OTP in hours. For example,
     *                         if the given time limit is 24 then this function will fail the
     *                         validation if 24 hours has passed since the OTP was generated.
     * @return true if the given OTP matches the database entry and the time difference doesn't
     * exceed the given time limit, returns false in every other case.
     */
    public boolean validateOtp(String email, String otp, int timeLimitInHours){
        if (email == null || !userExists(email) || timeLimitInHours < 1 || getOtp(email) == null || getOtpTime(email) == -1) {
            return false;
        }
        long currentTime = System.currentTimeMillis();
        long timeDifference = timeLimitInHours * 60000L;
        if(currentTime - getOtpTime(email) > timeDifference || !otp.equals(getOtp(email))){
            return false;
        }
        removeOtp(email);
        return true;
    }

    /**
     * Upgrades the database
     *
     * @param db         the database
     * @param oldVersion the old version number
     * @param newVersion the new version number
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE1);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE2);
        onCreate(db);
    }

    /**
     * Adds a new event to the database.
     *
     * @param eveID The unique identifier for the event.
     * @param orgID The unique identifier for the organizer.
     * @param ti    The title of the event.
     * @param des   The description of the event.
     * @param sDate The start date of the event.
     * @param eDate The end date of the event.
     * @param loc   The location of the event.
     * @param cap   The capacity of the event.
     * @return {@code true} if the event was successfully added, otherwise
     * {@code false}.
     */
    public boolean addEvent(int eveID, int orgID, String ti, String des, String sDate, String eDate, String loc,
                            int cap) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        long r;
        try {
            values.put(T1COL1, eveID);
            values.put(T1COL2, orgID);
            values.put(T1COL3, ti);
            values.put(T1COL4, des);
            values.put(T1COL5, sDate);
            values.put(T1COL6, eDate);
            values.put(T1COL7, loc);
            values.put(T1COL8, cap);
            r = sqLiteDatabase.insert(TABLE1, null, values);
        } catch (SQLiteException e) {
            Log.e("UserDBHandler", "Error inserting event: " + e.getMessage());
            return false;
        }
        return r >= 0;
    }

    /**
     * Retrieves a list of events for a specific organizer.
     *
     * @param orgID The unique identifier of the organizer whose events are to be
     *              retrieved.
     * @return An {@code ArrayList<Event>} containing all events associated with the
     * given organizer.
     */
    public ArrayList<Event> viewEventsForOrganizer(String orgID) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<Event> EventArray = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE1 + " WHERE " + T1COL2 + " = " + orgID, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                EventArray.add(new Event(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getInt(7)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return EventArray;
    }


    /**
     * Updates an existing event in the database.
     *
     * @param eveID The unique identifier of the event to be updated.
     * @param orgID The unique identifier of the organizer.
     * @param ti    The updated title of the event.
     * @param des   The updated description of the event.
     * @param sDate The updated start date of the event.
     * @param eDate The updated end date of the event.
     * @param loc   The updated location of the event.
     * @param cap   The updated capacity of the event.
     * @return {@code true} if the event was successfully updated, otherwise
     * {@code false}.
     */
    public boolean updateEvent(int eveID, int orgID, String ti, String des, String
                                       sDate, String eDate, String loc,
                               int cap) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(T1COL2, orgID);
        values.put(T1COL3, ti);
        values.put(T1COL4, des);
        values.put(T1COL5, sDate);
        values.put(T1COL6, eDate);
        values.put(T1COL7, loc);
        values.put(T1COL8, cap);
        int u = sqLiteDatabase.update(TABLE1, values, "event_id=?",
                new String[]{Integer.toString(eveID)});
        if (u > 0)
            return true;
        else
            return false;
    }

    /**
     * Deletes an event from the database.
     *
     * @param eveID The unique identifier of the event to be deleted.
     * @return {@code true} if the event was successfully deleted, otherwise
     * {@code false}.
     */
    public boolean delEvent(int eveID) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        int d = sqLiteDatabase.delete(TABLE1, "event_id=?",
                new String[]{Integer.toString(eveID)});
        if (d > 0)
            return true;
        else
            return false;
    }

    /**
     * Adds a new invitation to the database.
     *
     * @param invID    The unique identifier for the invitation.
     * @param eveID    The unique identifier of the event associated with the
     *                 invitation.
     * @param atteID   The unique identifier of the attendee receiving the
     *                 invitation.
     * @param status   The status of the invitation (e.g., pending, accepted,
     *                 declined).
     * @param creaTime The creation timestamp of the invitation.
     * @return {@code true} if the invitation was successfully added, otherwise
     * {@code false}.
     */
    public boolean addInvitation(int invID, int eveID, int atteID, String status, String
            creaTime) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(T2COL1, invID);
        values.put(T2COL2, eveID);
        values.put(T2COL3, atteID);
        values.put(T2COL4, status);
        values.put(T2COL5, creaTime);
        // values.put(T2COL6,respTime); //Create a method to return this value to the
        // table
        long r = sqLiteDatabase.insert(TABLE2, null, values);
        if (r > 0)
            return true;
        else
            return false;
    }

    /**
     * Retrieves a list of invitations for a specific attendee.
     *
     * @param atteID The unique identifier of the attendee whose invitations are to
     *               be retrieved.
     * @return An {@code ArrayList<Invitation>} containing all invitations
     * associated with the given attendee.
     */
    public ArrayList<Invitation> viewInvitationsForAttendeeID(String atteID) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<Invitation> InvitationArray = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE2, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                InvitationArray.add(new Invitation(
                        cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return InvitationArray;
    }

    /**
     * Updates an existing invitation in the database.
     *
     * @param invID    The unique identifier of the invitation to be updated.
     * @param eveID    The unique identifier of the event associated with the
     *                 invitation.
     * @param atteID   The unique identifier of the attendee receiving the
     *                 invitation.
     * @param status   The updated status of the invitation (e.g., pending,
     *                 accepted, declined).
     * @param creaTime The updated creation timestamp of the invitation.
     * @param respTime The updated response timestamp of the invitation.
     * @return {@code true} if the invitation was successfully updated, otherwise
     * {@code false}.
     */
    public boolean updateInvitation(int invID, int eveID, int atteID, String status, String
            creaTime, String respTime) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(T2COL2, eveID);
        values.put(T2COL3, atteID);
        values.put(T2COL4, status);
        values.put(T2COL5, creaTime);
        values.put(T2COL6, respTime);
        // values.put(T2COL6,respTime); //Create a method to return this value to the
        // table
        int u = sqLiteDatabase.update(TABLE2, values, "invitation_id=?",
                new String[]{Integer.toString(invID)});
        if (u > 0)
            return true;
        else
            return false;
    }

    /**
     * Deletes an invitation from the database based on its unique identifier.
     *
     * @param invID The unique identifier of the invitation to be deleted.
     * @return {@code true} if the invitation was successfully deleted, otherwise
     * {@code false}.
     */
    public boolean delInvitation(int invID) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        int d = sqLiteDatabase.delete(TABLE2, "invitation_id=?",
                new String[]{Integer.toString(invID)});
        if (d > 0)
            return true;
        else
            return false;
    }

    /**
     * Retrieves a list of invitations for a specific event based on the given event ID.
     *
     * @param eventID The ID of the event for which invitations are to be retrieved.
     * @return An ArrayList of {@link Invitation} objects associated with the given event ID.
     */
    public ArrayList<Invitation> getEventByID(int eventID) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<Invitation> InvitationArray = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE2 + " WHERE " + T2COL2 + " = " + eventID, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                InvitationArray.add(new Invitation(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return InvitationArray;
    }

    /**
     * Retrieves a list of invitations for a specific attendee based on the given attendee ID.
     *
     * @param attendeeID The ID of the attendee whose event invitations are to be retrieved.
     * @return An ArrayList of {@link Invitation} objects associated with the given attendee ID.
     */
    public ArrayList<Invitation> getEventsForAttendee(int attendeeID) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<Invitation> InvitationArray = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE2 + " WHERE " + T2COL3 + " = " + attendeeID, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                InvitationArray.add(new Invitation(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return InvitationArray;
    }

    /**
     * Retrieves a list of accepted event invitations for a specific attendee based on the given attendee ID.
     *
     * @param attendeeID The ID of the attendee whose accepted event invitations are to be retrieved.
     * @return An ArrayList of {@link Invitation} objects that have been accepted by the attendee.
     */
    public ArrayList<Invitation> getAcceptedEventsForAttendee(int attendeeID) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<Invitation> InvitationArray = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE2 + " WHERE " + T2COL3 + " = " + attendeeID + " AND " + T2COL4 + " = 'ACCEPTED'", null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                InvitationArray.add(new Invitation(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return InvitationArray;
    }

    /**
     * Retrieves a list of past events for a specific attendee based on the given attendee ID.
     * A past event is determined by comparing the event's date and time with the current date and time.
     *
     * @param attendeeID The ID of the attendee whose past events are to be retrieved.
     * @return An ArrayList of {@link Invitation} objects for events that have already occurred.
     */
    public ArrayList<Invitation> getPastEventsForAttendee(int attendeeID) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<Invitation> InvitationArray = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime today = LocalDateTime.now();
        String formattedDateTime = today.format(formatter);
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE2 + " JOIN " + TABLE1 + " ON TABLE2.event_id = TABLE1.event_id" + " WHERE " + T2COL3 + " = " + attendeeID + " AND " + T1COL6 + " < '" + formattedDateTime + "'", null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                InvitationArray.add(new Invitation(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return InvitationArray;
    }

    /**
     * Retrieves a list of attendees who have accepted an invitation for a given event ID.
     *
     * @param eventID The ID of the event for which to retrieve accepted attendees.
     * @return An ArrayList of {@link Invitation} objects representing attendees who have accepted the invitation.
     */
    public ArrayList<Invitation> getAttendeesForEventID(int eventID) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<Invitation> InvitationArray = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE2 + " WHERE " + T2COL2 + " = " + eventID + " AND " + T2COL4 + " = 'ACCEPTED'", null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                InvitationArray.add(new Invitation(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return InvitationArray;
    }
}
