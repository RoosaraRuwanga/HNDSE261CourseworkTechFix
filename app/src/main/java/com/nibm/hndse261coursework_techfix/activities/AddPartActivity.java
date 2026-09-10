package com.nibm.hndse261coursework_techfix.activities;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nibm.hndse261coursework_techfix.R;
import com.nibm.hndse261coursework_techfix.database.DatabaseHelper;

public class AddPartActivity extends AppCompatActivity {

    EditText editSerial;
    EditText editName;
    EditText editPrice;
    Button btnAdd;
    Button btnBack;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_part);
        editSerial = findViewById(R.id.edit_serial);
        editName = findViewById(R.id.edit_name);
        editPrice = findViewById(R.id.edit_price);
        btnAdd = findViewById(R.id.btn_add);
        btnBack = findViewById(R.id.btn_back);
        databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.getWritableDatabase();
        btnAdd.setOnClickListener(v -> addPart());
        btnBack.setOnClickListener(v -> finish());
    }

    private void addPart() {
        String serialNumber =
                editSerial.getText().toString().trim();
        String partName =
                editName.getText().toString().trim();
        String priceText =
                editPrice.getText().toString().trim();
        if (serialNumber.isEmpty()) {
            editSerial.setError("Enter a serial number");
            editSerial.requestFocus();
            return;
        }
        if (partName.isEmpty()) {
            editName.setError("Enter a part name");
            editName.requestFocus();
            return;
        }
        if (priceText.isEmpty()) {
            editPrice.setError("Enter a price");
            editPrice.requestFocus();
            return;
        }
        try {
            double price =
                    Double.parseDouble(priceText);
            long result =
                    databaseHelper.insertPart(
                            db,
                            serialNumber,
                            partName,
                            price
                    );
            if (result == -1) {
                Toast.makeText(
                        this,
                        "Could not add part. Serial number may already exist.",
                        Toast.LENGTH_LONG
                ).show();
            } else {

                Toast.makeText(
                        this,
                        "Part added successfully.",
                        Toast.LENGTH_SHORT
                ).show();
                finish();
            }
        } catch (NumberFormatException e) {

            editPrice.setError("Enter a valid price");
            editPrice.requestFocus();
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