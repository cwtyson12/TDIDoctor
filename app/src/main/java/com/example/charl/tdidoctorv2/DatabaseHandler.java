package com.example.charl.tdidoctorv2;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Handles database management for application. Every vehicle speed
 * and engine RPM data reading is sent to the database, as well as every recorded trouble code
 * @author Charles Tyson
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    public DatabaseHandler(Context context) {
        super(context, "SpeedDatabase", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createSpeedTable = "create table SpeedTable(time INTEGER, speed DOUBLE)";
        String createRPMTable = "create table RPMTable(time INTEGER, RPM INTEGER)";
        String createTroubleCodesTable = "create table troubleCodesTable(time TEXT, codes TEXT)";
        db.execSQL(createSpeedTable);
        db.execSQL(createRPMTable);
        db.execSQL(createTroubleCodesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS SpeedTable");
        db.execSQL("DROP TABLE IF EXISTS RPMTable");
        db.execSQL("DROP TABLE IF EXISTS createTroubleCodesTable");
        onCreate(db);
    }

    /**
     * Inserts OBD-II trouble codes into the database
     * @param timeVal Date and time of trouble code retrieval
     * @param codes Formatted string of trouble codes
     */
    public void insertTroubleCodes(String timeVal, String codes){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues troubleCodesContentValues = new ContentValues();
        troubleCodesContentValues.put("time", timeVal);
        troubleCodesContentValues.put("codes", codes);
        db.insert("troubleCodesTable", null, troubleCodesContentValues);
    }

    /**
     * Inserts passed speed and RPM into proper tables with passed in date and time
     * @param TimeVal date and time of data retrieval
     * @param SpeedVal speed in MPH
     * @param RPMVal Engine RPMs
     */
    public void insertToDatabase(long TimeVal, double SpeedVal, int RPMVal){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues speedTableContentValues = new ContentValues();
        ContentValues rpmTableContentValues = new ContentValues();

        speedTableContentValues.put("time", TimeVal);
        speedTableContentValues.put("speed", SpeedVal);
        db.insert("SpeedTable", null, speedTableContentValues);

        rpmTableContentValues.put("time", TimeVal);
        rpmTableContentValues.put("RPM", RPMVal);
        db.insert("RPMTable", null, rpmTableContentValues);
    }

    /**
     * Drops all tables from the database
     * @param db Database to drop tables from
     */
    public void dropTables(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS SpeedTable");
        db.execSQL("DROP TABLE IF EXISTS RPMTable");
        db.execSQL("DROP TABLE IF EXISTS troubleCodesTable");
        onCreate(db);
    }
}
