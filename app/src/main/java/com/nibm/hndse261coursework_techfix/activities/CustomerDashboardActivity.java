package com.nibm.hndse261coursework_techfix.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.nibm.hndse261coursework_techfix.R;

public class CustomerDashboardActivity extends AppCompatActivity {

    TextView tvWelcome;

    Button btnBranchInfo;
    Button btnServices;
    Button btnRepairHistory;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_customer_dashboard);

        // Connect Java to XML
        tvWelcome = findViewById(R.id.tvWelcome);

        btnBranchInfo = findViewById(R.id.btnBranchInfo);
        btnServices = findViewById(R.id.btnServices);
        btnRepairHistory = findViewById(R.id.btnRepairHistory);
        btnLogout = findViewById(R.id.btnLogout);


        // Get customer's name from LoginActivity
        String name = getIntent().getStringExtra("name");

        if (name != null && !name.isEmpty()) {
            tvWelcome.setText("Welcome, " + name + "!");
        } else {
            tvWelcome.setText("Welcome!");
        }


        // --------------------------------
        // Branch Information Button
        // --------------------------------

        btnBranchInfo.setOnClickListener(v -> {

            Intent intent = new Intent(
                    CustomerDashboardActivity.this,
                    BranchInfoActivity.class
            );

            startActivity(intent);

        });


        // --------------------------------
        // Services Button
        // --------------------------------

        btnServices.setOnClickListener(v -> {

            Intent intent = new Intent(
                    CustomerDashboardActivity.this,
                    ServicesActivity.class
            );

            startActivity(intent);

        });


        // --------------------------------
        // Repair History Button
        // --------------------------------

        btnRepairHistory.setOnClickListener(v -> {

            Intent intent = new Intent(
                    CustomerDashboardActivity.this,
                    RepairHistoryActivity.class
            );

            startActivity(intent);

        });


        // --------------------------------
        // Logout Button
        // --------------------------------

        btnLogout.setOnClickListener(v -> {

            Intent intent = new Intent(
                    CustomerDashboardActivity.this,
                    LoginActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);

            finish();

        });
    }
}