package com.nibm.hndse261coursework_techfix.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nibm.hndse261coursework_techfix.R;
import com.nibm.hndse261coursework_techfix.database.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    EditText editEmail, editPassword;
    Button btnLogin, btnRegister;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // layout elements
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);

        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);

        databaseHelper = new DatabaseHelper(this);

        btnLogin.setOnClickListener(v -> loginUser());
        btnRegister.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    RegisterActivity.class
            );

            startActivity(intent);
        });
    }

    private void loginUser() {

        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        // Basic validation
        if (email.isEmpty() || password.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter all fields!",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = databaseHelper.loginUser(
                db,
                email,
                password
        );

        if (cursor.moveToFirst()) {

            int userId = cursor.getInt(
                    cursor.getColumnIndexOrThrow("user_id")
            );

            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow("name")
            );

            String type = cursor.getString(
                    cursor.getColumnIndexOrThrow("type")
            );

            cursor.close();
            db.close();


            if (type.equals("Customer")) {

                Intent intent = new Intent(
                        LoginActivity.this,
                        CustomerDashboardActivity.class
                );

                intent.putExtra("user_id", userId);
                intent.putExtra("name", name);

                startActivity(intent);
                finish();

            } else if (type.equals("Staff")) {

                Intent intent = new Intent(
                        LoginActivity.this,
                        CompanyDashboardActivity.class
                );

                intent.putExtra("user_id", userId);
                intent.putExtra("name", name);

                startActivity(intent);
                finish();

            } else {

                Toast.makeText(
                        this,
                        "Invalid account type",
                        Toast.LENGTH_SHORT
                ).show();
            }

        } else {

            cursor.close();
            db.close();

            Toast.makeText(
                    this,
                    "Invalid email or password",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}