package com.example.terminal_control;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Controllerdb extends SQLiteOpenHelper {


    private static final String DATABASE_NAME="DBTerminals";
    public Controllerdb(Context applicationcontext) {
        super(applicationcontext, DATABASE_NAME, null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //create table to insert data
        String query;
        query = "CREATE TABLE IF NOT EXISTS tblTerminals(Id INTEGER PRIMARY KEY AUTOINCREMENT,IPAddress VARCHAR,TerNo INTEGER, CustNo INTEGER, PortNo INTEGER DEFAULT  4001,TerName VARCHAR);";
        db.execSQL(query);



    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query ;
        query = "DROP TABLE IF EXISTS tblTerminals";
        db.execSQL(query);
        onCreate(db);
    }
}