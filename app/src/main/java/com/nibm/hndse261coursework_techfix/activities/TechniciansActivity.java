package com.nibm.hndse261coursework_techfix.activities;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nibm.hndse261coursework_techfix.R;
import com.nibm.hndse261coursework_techfix.database.DatabaseHelper;

public class TechniciansActivity extends AppCompatActivity {
    Button btnBack;
    LinearLayout techniciansContainer;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewtechnicians);
        btnBack = findViewById(R.id.btn_back);
        techniciansContainer =
                findViewById(R.id.repairs_container);
        databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.getWritableDatabase();
        loadTechnicians();
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadTechnicians() {
        techniciansContainer.removeAllViews();
        Cursor cursor = null;
        try {
            cursor = databaseHelper.getAllTechnicians(db);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int technicianId = cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    "technician_id"
                            )
                    );
                    String branchName = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "branch_name"
                            )
                    );
                    String name = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "name"
                            )
                    );
                    String phone = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "phone"
                            )
                    );
                    String specialisation = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "specialisation"
                            )
                    );
                    String availability = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "availability"
                            )
                    );
                    addTechnicianEntry(
                            technicianId,
                            branchName,
                            name,
                            phone,
                            specialisation,
                            availability
                    );
                } while (cursor.moveToNext());
            } else {
                TextView noTechnicians =
                        new TextView(this);

                noTechnicians.setText(
                        "There are currently no technicians."
                );
                noTechnicians.setTextSize(20);
                noTechnicians.setGravity(
                        Gravity.CENTER
                );
                noTechnicians.setPadding(
                        10,
                        30,
                        10,
                        30
                );
                techniciansContainer.addView(
                        noTechnicians
                );
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void addTechnicianEntry(
            int technicianId,
            String branchName,
            String name,
            String phone,
            String specialisation,
            String availability) {
        LinearLayout technicianLayout =
                new LinearLayout(this);
        technicianLayout.setOrientation(
                LinearLayout.VERTICAL
        );
        technicianLayout.setPadding(
                20,
                20,
                20,
                20
        );
        TextView technicianDetails =
                new TextView(this);
        if (phone == null ||
                phone.trim().isEmpty()) {
            phone = "Not provided";
        }
        if (specialisation == null ||
                specialisation.trim().isEmpty()) {
            specialisation = "Not provided";
        }
        if (availability == null ||
                availability.trim().isEmpty()) {

            availability = "Not provided";
        }
        technicianDetails.setText(
                "Technician #" + technicianId +
                        "\nName: " + name +
                        "\nBranch: " + branchName +
                        "\nPhone: " + phone +
                        "\nSpecialisation: " + specialisation +
                        "\nAvailability: " + availability
        );
        technicianDetails.setTextSize(18);
        Button deleteButton =
                new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setTextSize(18);
        deleteButton.setOnClickListener(v -> {
            showDeleteConfirmation(
                    technicianId
            );
        });
        technicianLayout.addView(
                technicianDetails
        );
        technicianLayout.addView(
                deleteButton
        );
        techniciansContainer.addView(
                technicianLayout
        );
    }

    private void showDeleteConfirmation(
            int technicianId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Technician")
                .setMessage(
                        "Are you sure you want to delete this technician?"
                )
                .setPositiveButton(
                        "Yes",
                        (dialog, which) -> {
                            databaseHelper.deleteTechnician(
                                    db,
                                    technicianId
                            );
                            Toast.makeText(
                                    TechniciansActivity.this,
                                    "Technician deleted.",
                                    Toast.LENGTH_SHORT
                            ).show();
                            loadTechnicians();
                        }
                )
                .setNegativeButton(
                        "No",
                        null
                )
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
