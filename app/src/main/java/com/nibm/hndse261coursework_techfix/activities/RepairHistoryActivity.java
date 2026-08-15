package com.nibm.hndse261coursework_techfix.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.nibm.hndse261coursework_techfix.R;

public class RepairHistoryActivity extends AppCompatActivity {

    TextView tvRepairHistory1;
    TextView tvRepairHistory2;
    TextView tvRepairHistory3;
    TextView tvRepairHistory4;

    Button btnRepairBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_repair_history);

        // ==========================
        // Layout Elements
        // ==========================

        tvRepairHistory1 =
                findViewById(R.id.tvRepairHistory1);

        tvRepairHistory2 =
                findViewById(R.id.tvRepairHistory2);

        tvRepairHistory3 =
                findViewById(R.id.tvRepairHistory3);

        tvRepairHistory4 =
                findViewById(R.id.tvRepairHistory4);

        btnRepairBack =
                findViewById(R.id.btnRepairBack);


        // ==========================
        // Temporary Information
        // ==========================

        tvRepairHistory1.setText(
                "Repair ID: --\n" +
                        "Service: --\n" +
                        "Status: --"
        );

        tvRepairHistory2.setText(
                "Repair ID: --\n" +
                        "Service: --\n" +
                        "Status: --"
        );

        tvRepairHistory3.setText(
                "Repair ID: --\n" +
                        "Service: --\n" +
                        "Status: --"
        );

        tvRepairHistory4.setText(
                "Repair ID: --\n" +
                        "Service: --\n" +
                        "Status: --"
        );


        // ==========================
        // Back Button
        // ==========================

        btnRepairBack.setOnClickListener(v -> {
            finish();
        });
    }
}