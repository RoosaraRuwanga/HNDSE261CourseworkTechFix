package com.nibm.hndse261coursework_techfix.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nibm.hndse261coursework_techfix.R;
import com.nibm.hndse261coursework_techfix.database.DatabaseHelper;

public class RepairsActivity extends AppCompatActivity {

    Button btnAddRepair;
    Button btnBack;
    LinearLayout repairsContainer;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repairs);
        btnAddRepair = findViewById(R.id.btn_add_repair);
        btnBack = findViewById(R.id.btn_back);
        repairsContainer = findViewById(R.id.repairs_container);
        databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.getWritableDatabase();
        loadCurrentRepairs();
        btnAddRepair.setOnClickListener(v -> {
            Intent intent = new Intent(
                    RepairsActivity.this,
                    AddRepairActivity.class
            );
            startActivity(intent);
        });
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
                    byte[] imageBytes = cursor.getBlob(
                            cursor.getColumnIndexOrThrow(
                                    "sample_image"
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
                            imageBytes,
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
            byte[] imageBytes,
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
        repairLayout.addView(repairDetails);
        if (imageBytes != null) {
            Bitmap image = BitmapFactory.decodeByteArray(
                    imageBytes,
                    0,
                    imageBytes.length
            );
            ImageView repairImage =
                    new ImageView(this);
            repairImage.setImageBitmap(image);
            repairImage.setAdjustViewBounds(true);
            LinearLayout.LayoutParams imageParams =
                    new LinearLayout.LayoutParams(
                            400,
                            400
                    );
            imageParams.gravity = Gravity.CENTER;
            repairImage.setLayoutParams(imageParams);
            repairLayout.addView(repairImage);
        }
        LinearLayout buttonLayout =
                new LinearLayout(this);
        buttonLayout.setOrientation(
                LinearLayout.HORIZONTAL
        );
        buttonLayout.setGravity(
                Gravity.CENTER
        );
        Button editButton =
                new Button(this);
        editButton.setText("Edit");
        editButton.setTextSize(18);
        Button deleteButton =
                new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setTextSize(18);
        editButton.setOnClickListener(v -> {

            showEditPopup(
                    appointmentId,
                    deviceModel,
                    problemDescription,
                    appointmentDate,
                    status
            );
        });

        deleteButton.setOnClickListener(v -> {

            showDeleteConfirmation(
                    appointmentId
            );
        });

        buttonLayout.addView(editButton);
        buttonLayout.addView(deleteButton);

        repairLayout.addView(buttonLayout);

        repairsContainer.addView(repairLayout);
    }

    private void showEditPopup(
            int appointmentId,
            String deviceModel,
            String problemDescription,
            String appointmentDate,
            String status) {
        LinearLayout editLayout =
                new LinearLayout(this);
        editLayout.setOrientation(
                LinearLayout.VERTICAL
        );
        editLayout.setPadding(
                40,
                10,
                40,
                10
        );
        TextView deviceLabel =
                new TextView(this);
        deviceLabel.setText("Device Model");
        deviceLabel.setTextSize(16);
        EditText deviceEdit =
                new EditText(this);
        deviceEdit.setText(deviceModel);
        deviceEdit.setTextSize(16);
        TextView problemLabel =
                new TextView(this);
        problemLabel.setText("Problem Description");
        problemLabel.setTextSize(16);
        EditText problemEdit =
                new EditText(this);
        problemEdit.setText(problemDescription);
        problemEdit.setTextSize(16);
        TextView dateLabel =
                new TextView(this);
        dateLabel.setText("Appointment Date");
        dateLabel.setTextSize(16);
        EditText dateEdit =
                new EditText(this);
        dateEdit.setText(appointmentDate);
        dateEdit.setTextSize(16);
        TextView statusLabel =
                new TextView(this);
        statusLabel.setText("Status");
        statusLabel.setTextSize(16);
        EditText statusEdit =
                new EditText(this);
        statusEdit.setText(status);
        statusEdit.setTextSize(16);
        editLayout.addView(deviceLabel);
        editLayout.addView(deviceEdit);
        editLayout.addView(problemLabel);
        editLayout.addView(problemEdit);
        editLayout.addView(dateLabel);
        editLayout.addView(dateEdit);
        editLayout.addView(statusLabel);
        editLayout.addView(statusEdit);
        AlertDialog editDialog =
                new AlertDialog.Builder(this)
                        .setTitle(
                                "Edit Repair Order #" +
                                        appointmentId
                        )
                        .setView(editLayout)
                        .setPositiveButton(
                                "Update",
                                null
                        )
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .create();
        editDialog.setOnShowListener(dialog -> {
            Button updateButton =
                    editDialog.getButton(
                            AlertDialog.BUTTON_POSITIVE
                    );
            updateButton.setOnClickListener(v -> {
                String newDeviceModel =
                        deviceEdit.getText()
                                .toString()
                                .trim();
                String newProblemDescription =
                        problemEdit.getText()
                                .toString()
                                .trim();
                String newAppointmentDate =
                        dateEdit.getText()
                                .toString()
                                .trim();
                String newStatus =
                        statusEdit.getText()
                                .toString()
                                .trim();
                if (newDeviceModel.isEmpty() ||
                        newAppointmentDate.isEmpty() ||
                        newStatus.isEmpty()) {
                    Toast.makeText(
                            RepairsActivity.this,
                            "Please fill in all required fields.",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }
                databaseHelper.updateAppointment(
                        db,
                        appointmentId,
                        newDeviceModel,
                        newProblemDescription,
                        newAppointmentDate,
                        newStatus
                );
                Toast.makeText(
                        RepairsActivity.this,
                        "Repair order updated.",
                        Toast.LENGTH_SHORT
                ).show();
                editDialog.dismiss();

                loadCurrentRepairs();
            });
        });
        editDialog.show();
    }

    private void showDeleteConfirmation(
            int appointmentId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Repair Order")
                .setMessage(
                        "Are you sure you want to delete this repair order?"
                )
                .setPositiveButton(
                        "Yes",
                        (dialog, which) -> {

                            databaseHelper.deleteAppointment(
                                    db,
                                    appointmentId
                            );

                            Toast.makeText(
                                    RepairsActivity.this,
                                    "Repair order deleted.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            loadCurrentRepairs();
                        }
                )
                .setNegativeButton(
                        "No",
                        null
                )
                .show();
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