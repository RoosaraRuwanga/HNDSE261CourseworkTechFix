package com.nibm.hndse261coursework_techfix.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
                "phone TEXT NOT NULL, " +
                "password TEXT NOT NULL, " +
                "address TEXT NOT NULL," +
                "type TEXT NOT NULL)");

        db.execSQL("CREATE TABLE Branch (" +
                "branch_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "branch_name TEXT NOT NULL, " +
                "address TEXT NOT NULL, " +
                "phone TEXT NOT NULL, " +
                "latitude REAL NOT NULL, " +
                "longitude REAL NOT NULL)");

        ContentValues branch1 = new ContentValues();
        branch1.put("branch_name", "Colombo Branch");
        branch1.put("address", "Colombo");
        branch1.put("phone", "0112345678");
        branch1.put("latitude", 6.9271);
        branch1.put("longitude", 79.8612);
        db.insert("Branch", null, branch1);

        ContentValues branch2 = new ContentValues();
        branch2.put("branch_name", "Wattala Branch");
        branch2.put("address", "Wattala");
        branch2.put("phone", "0812345678");
        branch2.put("latitude", 7.2906);
        branch2.put("longitude", 80.6337);
        db.insert("Branch", null, branch2);

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

        ContentValues service1 = new ContentValues();
        service1.put("category_id", 1);
        service1.put("service_name", "Screen Replacement");
        service1.put("description", "Replacement of damaged or cracked mobile phone screens");
        service1.put("price", 15000.00);
        service1.put("estimated_days", 2);
        db.insert("RepairService", null, service1);

        ContentValues service2 = new ContentValues();
        service2.put("category_id", 1);
        service2.put("service_name", "Battery Replacement");
        service2.put("description", "Replacement of faulty or worn-out mobile phone batteries");
        service2.put("price", 7500.00);
        service2.put("estimated_days", 1);
        db.insert("RepairService", null, service2);

        ContentValues service3 = new ContentValues();
        service3.put("category_id", 1);
        service3.put("service_name", "Charging Port Repair");
        service3.put("description", "Repair or replacement of damaged charging ports");
        service3.put("price", 6000.00);
        service3.put("estimated_days", 2);
        db.insert("RepairService", null, service3);

        ContentValues service4 = new ContentValues();
        service4.put("category_id", 2);
        service4.put("service_name", "Laptop Keyboard Replacement");
        service4.put("description", "Replacement of damaged laptop keyboard");
        service4.put("price", 12000.00);
        service4.put("estimated_days", 3);
        db.insert("RepairService", null, service4);

        ContentValues service5 = new ContentValues();
        service5.put("category_id", 2);
        service5.put("service_name", "Laptop Battery Replacement");
        service5.put("description", "Replacement of faulty laptop battery");
        service5.put("price", 18000.00);
        service5.put("estimated_days", 2);
        db.insert("RepairService", null, service5);

        ContentValues service6 = new ContentValues();
        service6.put("category_id", 3);
        service6.put("service_name", "Tablet Screen Replacement");
        service6.put("description", "Replacement of cracked or damaged tablet screen");
        service6.put("price", 14000.00);
        service6.put("estimated_days", 3);
        db.insert("RepairService", null, service6);

        db.execSQL("CREATE TABLE Technician (" +
                "technician_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER UNIQUE NOT NULL, " +
                "branch_id INTEGER NOT NULL, " +
                "name TEXT NOT NULL, " +
                "phone TEXT, " +
                "specialisation TEXT, " +
                "availability TEXT, " +
                "FOREIGN KEY (user_id) REFERENCES User(user_id), " +
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

        db.execSQL("CREATE TABLE Part (" +
                "part_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "serial_number TEXT UNIQUE NOT NULL, " +
                "part_name TEXT NOT NULL, " +
                "price REAL NOT NULL)");

        ContentValues adminAccount = new ContentValues();

        adminAccount.put("name", "admin");
        adminAccount.put("email", "admin");
        adminAccount.put("phone", "");
        adminAccount.put("password", "admin123");
        adminAccount.put("address", "");
        adminAccount.put("type", "Staff");

        db.insert("User", null, adminAccount);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Delete all the tables if they exist...
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

    // USER OPERATIONS
    // return type here is long cuz db.insert returns long. its for error checking
    public long insertUser(SQLiteDatabase db, String name, String email,
                           String phone, String password, String address,
                           String type) {

        ContentValues userRecord = new ContentValues();

        userRecord.put("name", name);
        userRecord.put("email", email);
        userRecord.put("phone", phone);
        userRecord.put("password", password);
        userRecord.put("address", address);
        userRecord.put("type", type);

        return db.insert("User", null, userRecord);
    }
    public void updateUser(SQLiteDatabase db, int userId,
                           String name, String email,
                           String phone, String address) {

        ContentValues userRecord = new ContentValues();

        userRecord.put("name", name);
        userRecord.put("email", email);
        userRecord.put("phone", phone);
        userRecord.put("address", address);

        db.update(
                "User",
                userRecord,
                "user_id = ?",
                new String[]{String.valueOf(userId)}
        );
    }
    public void deleteUser(SQLiteDatabase db, int userId) {

        db.delete(
                "User",
                "user_id = ?",
                new String[]{String.valueOf(userId)}
        );
    }
    public Cursor getAllUsers(SQLiteDatabase db) {

        return db.rawQuery(
                "SELECT * FROM User",
                null
        );
    }
    public Cursor searchUsers(SQLiteDatabase db, String searchText) {

        return db.rawQuery(
                "SELECT * FROM User WHERE name LIKE ? OR email LIKE ? OR phone LIKE ? OR type LIKE ?",
                new String[]{
                        "%" + searchText + "%",
                        "%" + searchText + "%",
                        "%" + searchText + "%",
                        "%" + searchText + "%"
                }
        );
    }
    public Cursor loginUser(SQLiteDatabase db, String email, String password) {

        return db.rawQuery(
                "SELECT * FROM User WHERE email = ? AND password = ?",
                new String[]{email, password}
        );
    }
    public Cursor getCurrentAppointments(SQLiteDatabase db) {
        return db.rawQuery(
                "SELECT " +
                        "Appointment.appointment_id, " +
                        "User.name AS customer_name, " +
                        "Branch.branch_name, " +
                        "RepairService.service_name, " +
                        "RepairService.sample_image, " +
                        "Technician.name AS technician_name, " +
                        "Appointment.device_model, " +
                        "Appointment.problem_description, " +
                        "Appointment.appointment_date, " +
                        "Appointment.status " +
                        "FROM Appointment " +
                        "INNER JOIN User " +
                        "ON Appointment.user_id = User.user_id " +
                        "INNER JOIN Branch " +
                        "ON Appointment.branch_id = Branch.branch_id " +
                        "INNER JOIN RepairService " +
                        "ON Appointment.service_id = RepairService.service_id " +
                        "LEFT JOIN Technician " +
                        "ON Appointment.technician_id = Technician.technician_id " +
                        "WHERE Appointment.status != 'Completed' " +
                        "ORDER BY Appointment.appointment_date ASC",
                null
        );
    }

    public void updateAppointment(
            SQLiteDatabase db,
            int appointmentId,
            String deviceModel,
            String problemDescription,
            String appointmentDate,
            String status) {
        ContentValues appointmentRecord = new ContentValues();
        appointmentRecord.put("device_model", deviceModel);
        appointmentRecord.put("problem_description", problemDescription);
        appointmentRecord.put("appointment_date", appointmentDate);
        appointmentRecord.put("status", status);
        db.update(
                "Appointment",
                appointmentRecord,
                "appointment_id = ?",
                new String[]{
                        String.valueOf(appointmentId)
                }
        );
    }

    public void deleteAppointment(
            SQLiteDatabase db,
            int appointmentId) {
        db.delete(
                "Appointment",
                "appointment_id = ?",
                new String[]{
                        String.valueOf(appointmentId)
                }
        );
    }

    public Cursor getUsersForSpinner(SQLiteDatabase db) {
        return db.rawQuery(
                "SELECT user_id, name FROM User",
                null
        );
    }

    public Cursor getBranchesForSpinner(SQLiteDatabase db) {
        return db.rawQuery(
                "SELECT branch_id, branch_name FROM Branch",
                null
        );
    }

    public Cursor getServicesForSpinner(SQLiteDatabase db) {
        return db.rawQuery(
                "SELECT service_id, service_name FROM RepairService",
                null
        );
    }

    public Cursor getTechniciansForSpinner(SQLiteDatabase db) {
        return db.rawQuery(
                "SELECT technician_id, name FROM Technician",
                null
        );
    }

    public long insertAppointment(
            SQLiteDatabase db,
            int userId,
            int branchId,
            int serviceId,
            Integer technicianId,
            String deviceModel,
            String problemDescription,
            String appointmentDate,
            String status,
            String createdAt) {
        ContentValues appointmentRecord = new ContentValues();
        appointmentRecord.put("user_id", userId);
        appointmentRecord.put("branch_id", branchId);
        appointmentRecord.put("service_id", serviceId);
        if (technicianId == null) {
            appointmentRecord.putNull("technician_id");
        } else {
            appointmentRecord.put("technician_id", technicianId);
        }
        appointmentRecord.put("device_model", deviceModel);
        appointmentRecord.put("problem_description", problemDescription);
        appointmentRecord.put("appointment_date", appointmentDate);
        appointmentRecord.put("status", status);
        appointmentRecord.put("created_at", createdAt);
        return db.insert("Appointment", null, appointmentRecord);
    }

    public void updateServiceImage(
            SQLiteDatabase db,
            int serviceId,
            byte[] image) {
        ContentValues serviceRecord = new ContentValues();
        serviceRecord.put("sample_image", image);
        db.update(
                "RepairService",
                serviceRecord,
                "service_id = ?",
                new String[]{
                        String.valueOf(serviceId)
                }
        );
    }

    public Cursor getAllTechnicians(SQLiteDatabase db) {
        return db.rawQuery(
                "SELECT Technician.technician_id, " +
                        "Technician.user_id, " +
                        "Technician.name, " +
                        "Technician.phone, " +
                        "Technician.specialisation, " +
                        "Technician.availability, " +
                        "Branch.branch_name " +
                        "FROM Technician " +
                        "INNER JOIN Branch " +
                        "ON Technician.branch_id = Branch.branch_id",
                null
        );
    }

    public void deleteTechnician(
            SQLiteDatabase db,
            int technicianId) {

        Cursor cursor = db.rawQuery(
                "SELECT user_id FROM Technician WHERE technician_id = ?",
                new String[]{
                        String.valueOf(technicianId)
                }
        );
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(
                    cursor.getColumnIndexOrThrow("user_id")
            );
        }
        cursor.close();
        ContentValues appointmentRecord =
                new ContentValues();
        appointmentRecord.putNull("technician_id");
        db.update(
                "Appointment",
                appointmentRecord,
                "technician_id = ?",
                new String[]{
                        String.valueOf(technicianId)
                }
        );
        db.delete(
                "Technician",
                "technician_id = ?",
                new String[]{
                        String.valueOf(technicianId)
                }
        );
        if (userId != -1) {
            db.delete(
                    "User",
                    "user_id = ?",
                    new String[]{
                            String.valueOf(userId)
                    }
            );
        }
    }

    public long insertTechnician(
            SQLiteDatabase db,
            int userId,
            int branchId,
            String name,
            String phone,
            String specialisation,
            String availability) {
        ContentValues technicianRecord =
                new ContentValues();
        technicianRecord.put("user_id", userId);
        technicianRecord.put("branch_id", branchId);
        technicianRecord.put("name", name);
        technicianRecord.put("phone", phone);
        technicianRecord.put("specialisation", specialisation);
        technicianRecord.put("availability", availability);
        return db.insert(
                "Technician",
                null,
                technicianRecord
        );
    }

    public Cursor getAllParts(SQLiteDatabase db) {
        return db.rawQuery(
                "SELECT * FROM Part",
                null
        );
    }

    public long insertPart(
            SQLiteDatabase db,
            String serialNumber,
            String partName,
            double price) {

        ContentValues partRecord = new ContentValues();

        partRecord.put("serial_number", serialNumber);
        partRecord.put("part_name", partName);
        partRecord.put("price", price);

        return db.insert(
                "Part",
                null,
                partRecord
        );
    }

    public void deletePart(
            SQLiteDatabase db,
            int partId) {
        db.delete(
                "Part",
                "part_id = ?",
                new String[]{String.valueOf(partId)}
        );
    }

}