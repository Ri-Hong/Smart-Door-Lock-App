package com.example.smart_door_lock_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.net.PortUnreachableException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DbHelper extends SQLiteOpenHelper {

    //User_Data
    public static final String USER_TABLE = "USER_TABLE";
    public static final String COLUMN_NAME = "USER";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_PIN = "PIN";


    //History
    private static final String HISTORY_TABLE = "HISTORY_TABLE";
    private static final String COLUMN_USER_NAME = "NAME";
    private static final String COLUMN_ACTION = "ACTION";
    private static final String COLUMN_TIME = "TIME";

    public DbHelper(@Nullable Context context) {
        super(context, "smartDoorLock.db", null, 1);
    }

    // this is called the first time a database is accessed. there should be code in here to create a new database.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + USER_TABLE + "(" + COLUMN_NAME + " TEXT, " + COLUMN_ID + " INTEGER, " + COLUMN_PIN + " INTEGER);";
        db.execSQL(createTableStatement);
        createTableStatement = "CREATE TABLE " + HISTORY_TABLE + "(" + COLUMN_USER_NAME + " TEXT, " + COLUMN_ACTION + " TEXT, " + COLUMN_TIME +  " TIMESTAMP);";
        db.execSQL(createTableStatement);
    }

    //this is called if the database version number changes. It prevents previous users apps from breaking.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addOne(String name, int id, int pin){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_ID, id);
        cv.put(COLUMN_PIN, pin);

        long insert = db.insert(USER_TABLE, null, cv);
        if (insert == -1){
            return false;
        }
        else{
            return true;
        }
    }

    public boolean addOneHistory(String name, String action ){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String currentDateAndTime = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        cv.put(COLUMN_USER_NAME, name);
        cv.put(COLUMN_ACTION, action);
        cv.put(COLUMN_TIME, currentDateAndTime);

        long insert = db.insert(HISTORY_TABLE, null, cv);
        if (insert == -1){
            return false;
        }
        else{
            return true;
        }
    }

    public int getSize(){ //gets the # of entries in the db
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, USER_TABLE);
        return numRows;
    }


    public List<ContentValues> getData()
    {
        List<ContentValues> returnList = new ArrayList<>();

        // Get Data from database
        String queryString = "SELECT * FROM " + USER_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery(queryString, null);

            if (cursor.moveToFirst()) {
                // Loop through cursor (results) and create new DataModel object for each row, then save to a list
                do {
                    String name = cursor.getString(0);
                    int id = cursor.getInt(1);
                    int pin = cursor.getInt(2);
                    ContentValues cv = new ContentValues();

                    cv.put(COLUMN_NAME, name);
                    cv.put(COLUMN_ID, id);
                    cv.put(COLUMN_PIN, pin);

                    returnList.add(cv);

                } while(cursor.moveToNext());
            }
            else {
                // No results fetched from DB
            }
        }
        catch (Exception e) {
            Log.d("Yejin", e.getMessage());
        }

        return returnList;
    }

    public List<ContentValues> getHistory()
    {
        List<ContentValues> returnList = new ArrayList<>();

        // Get Data from database
        String queryString = "SELECT * FROM " + HISTORY_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery(queryString, null);

            if (cursor.moveToFirst()) {
                // Loop through cursor (results) and create new DataModel object for each row, then save to a list
                do {
                    String name = cursor.getString(0);
                    String action = cursor.getString(1);
                    String time = cursor.getString(2);
                    ContentValues cv = new ContentValues();

                    cv.put(COLUMN_USER_NAME, name);
                    cv.put(COLUMN_ACTION, action);
                    cv.put(COLUMN_TIME, time);

                    returnList.add(cv);

                } while(cursor.moveToNext());
            }
            else {
                // No results fetched from DB
            }
        }
        catch (Exception e) {
            Log.d("Yejin", e.getMessage());
        }

        return returnList;
    }

    public void deleteTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        String clearDBQuery = "DELETE FROM "+ USER_TABLE;
        db.execSQL(clearDBQuery);
    }
}
