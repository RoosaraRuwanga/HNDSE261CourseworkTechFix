package com.nibm.hndse261coursework_techfix.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.nibm.hndse261coursework_techfix.R;

public class AccountsActivity extends AppCompatActivity {
    Button btnAddStaffAccount;
    Button btnViewAccounts;
    Button btnDeleteAccounts;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        btnAddStaffAccount = findViewById(R.id.btn_addstaffaccount);
        btnViewAccounts = findViewById(R.id.btn_viewaccounts);
        btnDeleteAccounts = findViewById(R.id.btn_deleteaccounts);
        btnBack = findViewById(R.id.btn_back);

        btnAddStaffAccount.setOnClickListener(v -> {

            Intent intent = new Intent(
                    AccountsActivity.this,
                    RegisterStaffActivity.class
            );
            startActivity(intent);
        });

        btnViewAccounts.setOnClickListener(v -> {

            Intent intent = new Intent(
                    AccountsActivity.this,
                    ViewAccountsActivity.class
            );
            startActivity(intent);
        });

        btnDeleteAccounts.setOnClickListener(v -> {

            Intent intent = new Intent(
                    AccountsActivity.this,
                    DeleteAccountsActivity.class
            );
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> finish());
    }
}