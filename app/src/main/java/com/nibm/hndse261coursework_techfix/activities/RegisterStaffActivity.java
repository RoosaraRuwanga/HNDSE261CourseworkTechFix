package com.nibm.hndse261coursework_techfix.activities;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nibm.hndse261coursework_techfix.R;
import com.nibm.hndse261coursework_techfix.database.DatabaseHelper;
import android.database.Cursor;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import java.util.ArrayList;

public class RegisterStaffActivity extends AppCompatActivity {

    EditText editUsername;
    EditText editPassword;
    Button btnRegister;
    Button btnBack;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    CheckBox checkTechnician;
    Spinner spinnerBranch;
    EditText editSpecialisation;
    EditText editAvailability;
    ArrayList<Integer> branchIds = new ArrayList<>();

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
        checkTechnician = findViewById(R.id.check_technician);
        spinnerBranch = findViewById(R.id.spinner_branch);
        editSpecialisation = findViewById(R.id.edit_specialisation);
        editAvailability = findViewById(R.id.edit_availability);
        loadBranches();
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
        if (checkTechnician.isChecked()) {

            if (branchIds.size() == 0) {
                Toast.makeText(
                        this,
                        "Please add a branch first.",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }
        }
        try {

            long result = databaseHelper.insertUser(
                    db,
                    username,     // name
                    username,     // email
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

                if (checkTechnician.isChecked()) {
                    int branchId = branchIds.get(
                            spinnerBranch.getSelectedItemPosition()
                    );
                    String specialisation =
                            editSpecialisation.getText().toString().trim();
                    String availability =
                            editAvailability.getText().toString().trim();
                    databaseHelper.insertTechnician(
                            db,
                            branchId,
                            username,
                            "",
                            specialisation,
                            availability
                    );
                }
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

    private void loadBranches() {
        ArrayList<String> branches = new ArrayList<>();
        branchIds.clear();
        Cursor cursor = databaseHelper.getBranchesForSpinner(db);
        while (cursor.moveToNext()) {
            branchIds.add(
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow("branch_id")
                    )
            );
            branches.add(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("branch_name")
                    )
            );
        }
        cursor.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                branches
        );
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        spinnerBranch.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
