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
import android.content.Intent;

public class ViewPartsActivity extends AppCompatActivity {
    Button btnAddPart;
    Button btnBack;
    LinearLayout partsContainer;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewparts);
        btnAddPart = findViewById(R.id.btn_addpart);
        btnBack = findViewById(R.id.btn_back);
        partsContainer = findViewById(R.id.repairs_container);
        databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.getWritableDatabase();
        loadParts();
        btnAddPart.setOnClickListener(v -> {
            Intent intent = new Intent(
                    ViewPartsActivity.this,
                    AddPartActivity.class
            );
            startActivity(intent);
        });
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadParts() {
        partsContainer.removeAllViews();
        Cursor cursor = null;
        try {
            cursor = databaseHelper.getAllParts(db);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int partId = cursor.getInt(
                            cursor.getColumnIndexOrThrow("part_id")
                    );
                    String serialNumber = cursor.getString(
                            cursor.getColumnIndexOrThrow("serial_number")
                    );
                    String partName = cursor.getString(
                            cursor.getColumnIndexOrThrow("part_name")
                    );
                    double price = cursor.getDouble(
                            cursor.getColumnIndexOrThrow("price")
                    );
                    addPartEntry(
                            partId,
                            serialNumber,
                            partName,
                            price
                    );
                } while (cursor.moveToNext());
            } else {
                TextView noParts = new TextView(this);
                noParts.setText(
                        "There are currently no parts."
                );
                noParts.setTextSize(20);
                noParts.setGravity(Gravity.CENTER);
                noParts.setPadding(10, 30, 10, 30);
                partsContainer.addView(noParts);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void addPartEntry(
            int partId,
            String serialNumber,
            String partName,
            double price) {
        LinearLayout partLayout = new LinearLayout(this);
        partLayout.setOrientation(
                LinearLayout.VERTICAL
        );
        partLayout.setPadding(
                20, 20, 20, 20
        );
        TextView partDetails = new TextView(this);
        partDetails.setText(
                "Serial Number: " + serialNumber +
                        "\nName: " + partName +
                        "\nPrice: Rs. " + String.format("%.2f", price)
        );
        partDetails.setTextSize(18);
        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setTextSize(18);
        deleteButton.setOnClickListener(v ->
                showDeleteConfirmation(partId)
        );
        partLayout.addView(partDetails);
        partLayout.addView(deleteButton);
        partsContainer.addView(partLayout);
    }

    private void showDeleteConfirmation(int partId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Part")
                .setMessage(
                        "Are you sure you want to delete this part?"
                )
                .setPositiveButton(
                        "Yes",
                        (dialog, which) -> {
                            databaseHelper.deletePart(
                                    db,
                                    partId
                            );
                            Toast.makeText(
                                    ViewPartsActivity.this,
                                    "Part deleted.",
                                    Toast.LENGTH_SHORT
                            ).show();
                            loadParts();
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
    @Override
    protected void onResume() {
        super.onResume();
        if (db != null && db.isOpen()) {
            loadParts();
        }
    }
}
