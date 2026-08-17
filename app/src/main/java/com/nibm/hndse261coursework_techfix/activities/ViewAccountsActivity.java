package com.nibm.hndse261coursework_techfix.activities;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nibm.hndse261coursework_techfix.R;
import com.nibm.hndse261coursework_techfix.database.DatabaseHelper;

public class ViewAccountsActivity extends AppCompatActivity {

    EditText txtSearch;
    Button btnViewAccounts;
    Button btnBack;
    LinearLayout accountsContainer;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewaccounts);
        txtSearch = findViewById(R.id.txt_search);
        btnViewAccounts = findViewById(R.id.btn_viewaccounts);
        btnBack = findViewById(R.id.btn_back);
        accountsContainer = findViewById(R.id.accounts_container);
        databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.getWritableDatabase();
        btnViewAccounts.setOnClickListener(v ->
        {
            String searchText = txtSearch.getText().toString().trim();
            loadAccounts(searchText);
        });
        txtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after) {
            }
            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count) {
                loadAccounts(s.toString().trim());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnBack.setOnClickListener(v -> finish());
        loadAccounts("");
    }

    private void loadAccounts(String searchText) {

        accountsContainer.removeAllViews();
        Cursor cursor = null;
        try {

            if (searchText.isEmpty()) {
                cursor = databaseHelper.getAllUsers(db);
            } else {
                cursor = databaseHelper.searchUsers(db, searchText);
            }

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int userId = cursor.getInt(
                            cursor.getColumnIndexOrThrow("user_id")
                    );
                    String name = cursor.getString(
                            cursor.getColumnIndexOrThrow("name")
                    );
                    String email = cursor.getString(
                            cursor.getColumnIndexOrThrow("email")
                    );
                    String phone = cursor.getString(
                            cursor.getColumnIndexOrThrow("phone")
                    );
                    String type = cursor.getString(
                            cursor.getColumnIndexOrThrow("type")
                    );
                    addAccountEntry(
                            userId,
                            name,
                            email,
                            phone,
                            type
                    );
                } while (cursor.moveToNext());
            }
            else {
                TextView noAccounts = new TextView(this);

                noAccounts.setText("No accounts found.");
                noAccounts.setTextSize(20);
                noAccounts.setGravity(Gravity.CENTER);
                noAccounts.setPadding(10, 30, 10, 30);

                accountsContainer.addView(noAccounts);
            }
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void addAccountEntry(
            int userId,
            String name,
            String email,
            String phone,
            String type) {

        LinearLayout accountLayout = new LinearLayout(this);

        accountLayout.setOrientation(LinearLayout.VERTICAL);
        accountLayout.setPadding(20, 20, 20, 20);
        TextView accountDetails = new TextView(this);
        accountDetails.setText(
                "Name: " + name +
                        "\nEmail: " + email +
                        "\nPhone: " + phone +
                        "\nType: " + type
        );

        accountDetails.setTextSize(18);
        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setTextSize(18);
        deleteButton.setOnClickListener(v ->
        {
            showDeleteConfirmation(
                    userId,
                    name
            );
        });
        accountLayout.addView(accountDetails);
        accountLayout.addView(deleteButton);
        accountsContainer.addView(accountLayout);
    }

    private void showDeleteConfirmation(
            int userId,
            String accountName) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage(
                        "Are you sure you want to delete this entry?"
                )
                .setPositiveButton(
                        "Yes",
                        (dialog, which) -> deleteAccount(
                                userId,
                                accountName
                        )
                )
                .setNegativeButton(
                        "No",
                        null
                )
                .show();
    }
    private void deleteAccount(
            int userId,
            String accountName) {
        databaseHelper.deleteUser(
                db,
                userId
        );
        Toast.makeText(
                this,
                accountName + " deleted successfully.",
                Toast.LENGTH_SHORT
        ).show();
        String searchText =
                txtSearch.getText().toString().trim();
        loadAccounts(searchText);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}