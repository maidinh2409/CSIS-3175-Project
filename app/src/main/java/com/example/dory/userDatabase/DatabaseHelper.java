package com.example.dory.userDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    final static String DATABASE_NAME = "StudentInfo.db";
    final static int DATABASE_VERSION = 2;
    final static String TABLE1 = "StudentTable";
    final static String TABLE2 = "ProvTable";
    final static String T2COL1 = "PId";
    final static String T2COL2 = "ProvName";
    final static String T1COL1 = "Id";
    final static String T1COL2 = "FName";
    final static String T1COL3 = "LName";
    final static String T1COL4 = "Cell";
    final static String T1COL5 = "PId";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE1 +
                "(" + T1COL1 + " INTEGER PRIMARY KEY," +
                T1COL2 + " TEXT," +
                T1COL3 + " TEXT," +
                T1COL4 + " TEXT," +
                T1COL5 + " TEXT)";
        db.execSQL(query);
        query = "CREATE TABLE " + TABLE2 +
                "(" + T2COL1 + " TEXT," +
                T2COL2 + " TEXT)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE1);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE2);
        onCreate(db);
    }

    public Cursor viewStudProv(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "select Id,FName,ProvName from StudentTable inner join ProvTable " +
                " on StudentTable.PId = ProvTable.PId";
        Cursor c = sqLiteDatabase.rawQuery(query,null);
        return c;
    }
    public boolean addProvRecord(String prid,String pName){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(T2COL1,prid);
        values.put(T2COL2,pName);
        long r = sqLiteDatabase.insert(TABLE2,null,values);
        if(r>0)
            return true;
        else
            return false;

    }

    public boolean updateRec(int id,String cell){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(T1COL4,cell);
        int u = sqLiteDatabase.update(TABLE1,values,"id=?",
                new String[]{Integer.toString(id)});
        if(u>0)
            return true;
        else
            return false;
    }

    public boolean delRec(int id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        /*int d = sqLiteDatabase.delete(TABLE1,"Id=?",
                new String[]{Integer.toString(id)});
        if(d>0)
            return true;
        else
            return false;*/
        String query = "DELETE FROM " + TABLE1 + " WHERE Id = " + id;
        sqLiteDatabase.execSQL(query);
        return true;
    }

    public Cursor viewRecord(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE1;
        Cursor c = sqLiteDatabase.rawQuery(query,null);

//        String id = "1";
//        String query = "SELECT * FROM " + TABLE1 + " WHERE Id =?";
//        Cursor c = sqLiteDatabase.rawQuery(query,new String[]{id});
        return c;
    }


    public boolean addRecord(String fn,String ln,String cell,String prid){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(T1COL2,fn);
        values.put(T1COL3,ln);
        values.put(T1COL4,cell);
        values.put(T1COL5,prid);
        long r = sqLiteDatabase.insert(TABLE1,null,values);
        if(r>0)
            return true;
        else
            return false;

    }
}
