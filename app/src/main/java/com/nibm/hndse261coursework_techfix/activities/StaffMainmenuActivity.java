package com.nibm.hndse261coursework_techfix.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.nibm.hndse261coursework_techfix.R;

public class StaffMainmenuActivity extends AppCompatActivity {

    Button btnViewAccounts;
    Button btnViewBranchInfo;
    Button btnTechAndParts;
    Button btnServices;
    Button btnRepairOrders;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staffmainmenu);

        btnViewAccounts = findViewById(R.id.btn_viewaccounts);
        btnViewBranchInfo = findViewById(R.id.btn_viewbranchinfo);
        btnTechAndParts = findViewById(R.id.btn_techandparts);
        btnServices = findViewById(R.id.btn_services);
        btnRepairOrders = findViewById(R.id.btn_repairorders);
        btnLogout = findViewById(R.id.btn_logout);

        btnViewAccounts.setOnClickListener(v -> {

            Intent intent = new Intent(
                    StaffMainmenuActivity.this,
                    AccountsActivity.class
            );

            startActivity(intent);
        });

        btnViewBranchInfo.setOnClickListener(v -> {

            Intent intent = new Intent(
                    StaffMainmenuActivity.this,
                    BranchInfoActivity.class
            );

            startActivity(intent);
        });

        btnTechAndParts.setOnClickListener(v -> {

            Intent intent = new Intent(
                    StaffMainmenuActivity.this,
                    TechAndPartsActivity.class
            );

            startActivity(intent);
        });

        btnServices.setOnClickListener(v -> {

            Intent intent = new Intent(
                    StaffMainmenuActivity.this,
                    ServicesActivity.class
            );

            startActivity(intent);
        });

        btnRepairOrders.setOnClickListener(v -> {

            Intent intent = new Intent(
                    StaffMainmenuActivity.this,
                    RepairsActivity.class
            );

            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {

            Intent intent = new Intent(
                    StaffMainmenuActivity.this,
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