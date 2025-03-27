package com.example.week10app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    final static String DATABASE_NAME = "InvitesApp.db";
    final static int DATABASE_VERSION = 1;
    final static String TABLE1 = "EVENT"; //Table Event
    final static String T1COL1 = "event_id";
    final static String T1COL2 = "organizer_id";
    final static String T1COL3 = "title";
    final static String T1COL4 = "description";
    final static String T1COL5 = "startDate";
    final static String T1COL6 = "endDate";
    final static String T1COL7 = "location";
    final static String T1COL8 = "capacity";
    final static String TABLE2 = "INVITATION"; //Table Invitation
    final static String T2COL1 = "invitation_id";
    final static String T2COL2 = "event_id";
    final static String T2COL3 = "attendee_id";
    final static String T2COL4 = "status";
    final static String T2COL5 = "creationTime";
    final static String T2COL6 = "responseTime";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE1 +
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
                T2COL5 + " DATETIME," + //CHECK IF DATETIME WORKS
                T2COL6 + " DATETIME)"; //Use local date to convert from string and viceversa
        db.execSQL(query);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE1);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE2);
        onCreate(db);
    }
    public boolean addEvent(int eveID, int orgID, String ti, String des, String sDate, String eDate, String loc, int cap) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(T1COL1,eveID);
        values.put(T1COL2,orgID);
        values.put(T1COL3,ti);
        values.put(T1COL4,des);
        values.put(T1COL5,sDate);
        values.put(T1COL6,eDate);
        values.put(T1COL7,loc);
        values.put(T1COL8,cap);
        long r = sqLiteDatabase.insert(TABLE1,null,values);
        if(r>0)
            return true;
        else
            return false;
    }
    public Cursor viewEventsForOrganizer(String orgID) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "SELECT event_id, title, description, startDate, endDate, location, capacity FROM EVENT" +
                " WHERE EVENT.organizer_id = " + orgID;
        Cursor c = sqLiteDatabase.rawQuery(query,null);
        return c;
    }
    public boolean updateEvent(int eveID, int orgID, String ti, String des, String sDate, String eDate, String loc, int cap) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(T1COL2,orgID);
        values.put(T1COL3,ti);
        values.put(T1COL4,des);
        values.put(T1COL5,sDate);
        values.put(T1COL6,eDate);
        values.put(T1COL7,loc);
        values.put(T1COL8,cap);
        int u = sqLiteDatabase.update(TABLE1,values,"event_id=?",
                new String[]{Integer.toString(eveID)});
        if(u>0)
            return true;
        else
            return false;
    }
    public boolean delEvent(int eveID) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        int d = sqLiteDatabase.delete(TABLE1,"event_id=?",
                new String[]{Integer.toString(eveID)});
        if(d>0)
            return true;
        else
            return false;
    }
    public boolean addInvitation(int invID, int eveID, int atteID, String status, String creaTime) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(T2COL1,invID);
        values.put(T2COL2,eveID);
        values.put(T2COL3,atteID);
        values.put(T2COL4,status);
        values.put(T2COL5,creaTime);
        //values.put(T2COL6,respTime); //Create a method to return this value to the table
        long r = sqLiteDatabase.insert(TABLE2,null,values);
        if(r>0)
            return true;
        else
            return false;
    }
    public Cursor viewInvitationsForAttendeeID(String atteID) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "SELECT invitation_id, event_id, status, creationTime, responseTime FROM INVITATION" +
                " WHERE INVITATION.attendee_id = " + atteID;
        Cursor c = sqLiteDatabase.rawQuery(query,null);
        return c;
    }
    public boolean updateInvitation(int invID, int eveID, int atteID, String status, String creaTime, String respTime) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(T2COL2,eveID);
        values.put(T2COL3,atteID);
        values.put(T2COL4,status);
        values.put(T2COL5,creaTime);
        values.put(T2COL6,respTime);
        //values.put(T2COL6,respTime); //Create a method to return this value to the table
        int u = sqLiteDatabase.update(TABLE2,values,"invitation_id=?",
                new String[]{Integer.toString(invID)});
        if(u>0)
            return true;
        else
            return false;
    }
    public boolean delInvitation(int invID) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        int d = sqLiteDatabase.delete(TABLE2,"invitation_id=?",
                new String[]{Integer.toString(invID)});
        if(d>0)
            return true;
        else
            return false;
    }
}
