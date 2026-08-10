package com.nibm.hndse261coursework_techfix.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TechFix.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE User (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "phone TEXT, " +
                "password TEXT NOT NULL, " +
                "address TEXT," +
                "type TEXT)");

        db.execSQL("CREATE TABLE Branch (" +
                "branch_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "branch_name TEXT NOT NULL, " +
                "address TEXT NOT NULL, " +
                "phone TEXT, " +
                "latitude REAL NOT NULL, " +
                "longitude REAL NOT NULL)");

        db.execSQL("CREATE TABLE DeviceCategory (" +
                "category_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "category_name TEXT NOT NULL, " +
                "description TEXT)");

        db.execSQL("CREATE TABLE RepairService (" +
                "service_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "category_id INTEGER NOT NULL, " +
                "service_name TEXT NOT NULL, " +
                "description TEXT, " +
                "price REAL NOT NULL, " +
                "estimated_days INTEGER, " +
                "sample_image BLOB, " +
                "FOREIGN KEY (category_id) REFERENCES DeviceCategory(category_id))");

        db.execSQL("CREATE TABLE Technician (" +
                "technician_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "branch_id INTEGER NOT NULL, " +
                "name TEXT NOT NULL, " +
                "phone TEXT, " +
                "specialisation TEXT, " +
                "availability TEXT, " +
                "FOREIGN KEY (branch_id) REFERENCES Branch(branch_id))");

        db.execSQL("CREATE TABLE Appointment (" +
                "appointment_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "branch_id INTEGER NOT NULL, " +
                "service_id INTEGER NOT NULL, " +
                "technician_id INTEGER, " +
                "device_model TEXT NOT NULL, " +
                "problem_description TEXT, " +
                "appointment_date TEXT NOT NULL, " +
                "status TEXT NOT NULL DEFAULT 'Pending', " +
                "created_at TEXT, " +
                "FOREIGN KEY (user_id) REFERENCES User(user_id), " +
                "FOREIGN KEY (branch_id) REFERENCES Branch(branch_id), " +
                "FOREIGN KEY (service_id) REFERENCES RepairService(service_id), " +
                "FOREIGN KEY (technician_id) REFERENCES Technician(technician_id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Delete all the tables if they exist...
        db.execSQL("DROP TABLE IF EXISTS RepairImage");
        db.execSQL("DROP TABLE IF EXISTS Appointment");
        db.execSQL("DROP TABLE IF EXISTS Technician");
        db.execSQL("DROP TABLE IF EXISTS RepairService");
        db.execSQL("DROP TABLE IF EXISTS DeviceCategory");
        db.execSQL("DROP TABLE IF EXISTS Branch");
        db.execSQL("DROP TABLE IF EXISTS User");
        //... and then just run the onCreate function again! idk why we weren't given this
        // approach in the classes, so easy... or maybe i missed it??
        onCreate(db);
    }
}