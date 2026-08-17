package com.nibm.hndse261coursework_techfix.activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.nibm.hndse261coursework_techfix.R;
import com.nibm.hndse261coursework_techfix.database.DatabaseHelper;

public class RepairsActivity extends AppCompatActivity {

    Button btnBack;
    LinearLayout repairsContainer;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repairs);
        btnBack = findViewById(R.id.btn_back);
        repairsContainer = findViewById(R.id.repairs_container);
        databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.getReadableDatabase();
        loadCurrentRepairs();
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadCurrentRepairs() {
        repairsContainer.removeAllViews();
        Cursor cursor = null;

        try {

            cursor = databaseHelper.getCurrentAppointments(db);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int appointmentId = cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    "appointment_id"
                            )
                    );
                    String customerName = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "customer_name"
                            )
                    );
                    String branchName = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "branch_name"
                            )
                    );
                    String serviceName = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "service_name"
                            )
                    );
                    String technicianName = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "technician_name"
                            )
                    );
                    String deviceModel = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "device_model"
                            )
                    );
                    String problemDescription = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "problem_description"
                            )
                    );
                    String appointmentDate = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "appointment_date"
                            )
                    );
                    String status = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "status"
                            )
                    );
                    addRepairEntry(
                            appointmentId,
                            customerName,
                            branchName,
                            serviceName,
                            technicianName,
                            deviceModel,
                            problemDescription,
                            appointmentDate,
                            status
                    );
                } while (cursor.moveToNext());
            } else {
                TextView noRepairs = new TextView(this);

                noRepairs.setText(
                        "There are currently no repair orders."
                );
                noRepairs.setTextSize(20);
                noRepairs.setGravity(Gravity.CENTER);

                noRepairs.setPadding(
                        10,
                        30,
                        10,
                        30
                );
                repairsContainer.addView(noRepairs);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void addRepairEntry(
            int appointmentId,
            String customerName,
            String branchName,
            String serviceName,
            String technicianName,
            String deviceModel,
            String problemDescription,
            String appointmentDate,
            String status) {

        LinearLayout repairLayout =
                new LinearLayout(this);

        repairLayout.setOrientation(
                LinearLayout.VERTICAL
        );

        repairLayout.setPadding(
                20,
                20,
                20,
                20
        );

        TextView repairDetails =
                new TextView(this);
        String technicianDisplay;
        if (technicianName == null ||
                technicianName.trim().isEmpty()) {

            technicianDisplay = "Not Assigned";
        } else {

            technicianDisplay = technicianName;
        }
        String problemDisplay;

        if (problemDescription == null ||
                problemDescription.trim().isEmpty()) {

            problemDisplay = "Not provided";
        } else {

            problemDisplay = problemDescription;
        }
        repairDetails.setText(
                "Repair Order #" + appointmentId +
                        "\nCustomer: " + customerName +
                        "\nBranch: " + branchName +
                        "\nService: " + serviceName +
                        "\nDevice: " + deviceModel +
                        "\nProblem: " + problemDisplay +
                        "\nTechnician: " + technicianDisplay +
                        "\nDate: " + appointmentDate +
                        "\nStatus: " + status
        );

        repairDetails.setTextSize(18);

        Button updateButton =
                new Button(this);

        updateButton.setText(
                "View / Update"
        );

        updateButton.setTextSize(18);

        repairLayout.addView(
                repairDetails
        );

        repairLayout.addView(
                updateButton
        );

        repairsContainer.addView(
                repairLayout
        );
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (db != null && db.isOpen()) {
            loadCurrentRepairs();
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