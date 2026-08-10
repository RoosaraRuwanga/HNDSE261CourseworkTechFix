package com.nibm.hndse261coursework_techfix.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nibm.hndse261coursework_techfix.R;
import com.nibm.hndse261coursework_techfix.database.DatabaseHelper;

public class RegisterActivity extends AppCompatActivity {

    EditText editEmail, editName, editAddress, editPhone, editPassword;
    Button btnRegister, btnBack;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // layout elements
        editEmail = findViewById(R.id.edit_email);
        editName = findViewById(R.id.edit_name);
        editAddress = findViewById(R.id.edit_address);
        editPhone = findViewById(R.id.edit_phone);
        editPassword = findViewById(R.id.edit_password);

        btnRegister = findViewById(R.id.btn_register);
        btnBack = findViewById(R.id.btn_back);

        databaseHelper = new DatabaseHelper(this);

        btnRegister.setOnClickListener(v -> registerUser());
        // btw i didnt know this till now, you can just use finish to go back to a previous activity :P
        btnBack.setOnClickListener(v -> finish());
    }

    private void registerUser() {

        String email = editEmail.getText().toString().trim();
        String name = editName.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        // Basic validation, just so we dont have empty fields...
        if (email.isEmpty() ||
                name.isEmpty() ||
                address.isEmpty() ||
                phone.isEmpty() ||
                password.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please fill in all fields!",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        // create customer account
        long result = databaseHelper.insertUser(
                db,
                name,
                email,
                phone,
                password,
                address,
                "Customer"
        );

        db.close();

        // Check if SQL failed, if so, it means the email is already shared perhaps?
        if (result == -1) {

            Toast.makeText(
                    this,
                    "Email is already registered, please use an unique email.",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            Toast.makeText(
                    this,
                    "Registration successful! Welcome to our service.",
                    Toast.LENGTH_SHORT
            ).show();

            // Return to Login
            Intent intent = new Intent(
                    RegisterActivity.this,
                    LoginActivity.class
            );

            startActivity(intent);
            finish();
        }
    }
}