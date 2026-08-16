package com.nibm.hndse261coursework_techfix.activities;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nibm.hndse261coursework_techfix.R;
import com.nibm.hndse261coursework_techfix.database.DatabaseHelper;

public class RegisterStaffActivity extends AppCompatActivity {

    EditText editUsername;
    EditText editPassword;
    Button btnRegister;
    Button btnBack;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerstaff);
        editUsername = findViewById(R.id.edit_username);
        editPassword = findViewById(R.id.edit_password);
        btnRegister = findViewById(R.id.btn_register);
        btnBack = findViewById(R.id.btn_back);
        databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.getWritableDatabase();
        btnRegister.setOnClickListener(v -> registerStaff());
        btnBack.setOnClickListener(v -> finish());
    }

    private void registerStaff() {

        String username = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (username.isEmpty()) {
            editUsername.setError("Enter a username");
            editUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editPassword.setError("Enter a password");
            editPassword.requestFocus();
            return;
        }
        try {

            long result = databaseHelper.insertUser(
                    db,
                    username,     // name
                    username,     // email - used by current login system
                    "",           // phone
                    password,
                    "",           // address
                    "Staff"
            );

            if (result == -1) {

                Toast.makeText(
                        this,
                        "Could not create account. Username may already exist.",
                        Toast.LENGTH_LONG
                ).show();

            } else {

                Toast.makeText(
                        this,
                        "Staff account created successfully",
                        Toast.LENGTH_SHORT
                ).show();

                finish();
            }
        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Error creating account: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
