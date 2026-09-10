package com.nibm.hndse261coursework_techfix.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.nibm.hndse261coursework_techfix.R;
import com.nibm.hndse261coursework_techfix.database.DatabaseHelper;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AddRepairActivity extends AppCompatActivity {
    Spinner spinnerUser;
    Spinner spinnerBranch;
    Spinner spinnerService;
    Spinner spinnerTechnician;
    Spinner spinnerStatus;
    EditText editDeviceModel;
    EditText editProblem;
    EditText editDate;
    Button btnTakePhoto;
    Button btnAddRepair;
    Button btnBack;
    ImageView imgRepair;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    ArrayList<Integer> userIds = new ArrayList<>();
    ArrayList<Integer> branchIds = new ArrayList<>();
    ArrayList<Integer> serviceIds = new ArrayList<>();
    ArrayList<Integer> technicianIds = new ArrayList<>();
    Bitmap repairImage;
    ActivityResultLauncher<String> cameraPermission;
    ActivityResultLauncher<Void> takePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_repair);
        spinnerUser = findViewById(R.id.spinner_user);
        spinnerBranch = findViewById(R.id.spinner_branch);
        spinnerService = findViewById(R.id.spinner_service);
        spinnerTechnician = findViewById(R.id.spinner_technician);
        spinnerStatus = findViewById(R.id.spinner_status);
        editDeviceModel = findViewById(R.id.edit_device_model);
        editProblem = findViewById(R.id.edit_problem);
        editDate = findViewById(R.id.edit_date);
        btnTakePhoto = findViewById(R.id.btn_take_photo);
        btnAddRepair = findViewById(R.id.btn_add_repair);
        btnBack = findViewById(R.id.btn_back);
        imgRepair = findViewById(R.id.img_repair);
        databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.getWritableDatabase();

        loadUsers();
        loadBranches();
        loadServices();
        loadTechnicians();
        loadStatuses();

        cameraPermission = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) {
                        takePhoto.launch(null);
                    } else {
                        Toast.makeText(
                                AddRepairActivity.this,
                                "Camera permission is required.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );

        takePhoto = registerForActivityResult(
                new ActivityResultContracts.TakePicturePreview(),
                bitmap -> {
                    if (bitmap != null) {
                        repairImage = bitmap;
                        imgRepair.setImageBitmap(bitmap);
                    }
                }
        );

        btnTakePhoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(
                    AddRepairActivity.this,
                    Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED) {
                takePhoto.launch(null);

            } else {
                cameraPermission.launch(Manifest.permission.CAMERA);
            }
        });
        btnAddRepair.setOnClickListener(v -> addRepair());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadUsers() {
        ArrayList<String> users = new ArrayList<>();
        userIds.clear();
        Cursor cursor = databaseHelper.getUsersForSpinner(db);
        while (cursor.moveToNext()) {
            userIds.add(
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow("user_id")
                    )
            );
            users.add(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("name")
                    )
            );
        }
        cursor.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                users
        );
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerUser.setAdapter(adapter);
    }

    private void loadBranches() {
        ArrayList<String> branches = new ArrayList<>();
        branchIds.clear();
        Cursor cursor = databaseHelper.getBranchesForSpinner(db);
        while (cursor.moveToNext()) {
            branchIds.add(
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow("branch_id")
                    )
            );
            branches.add(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("branch_name")
                    )
            );
        }
        cursor.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                branches
        );
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        spinnerBranch.setAdapter(adapter);
    }

    private void loadServices() {

        ArrayList<String> services = new ArrayList<>();
        serviceIds.clear();
        Cursor cursor = databaseHelper.getServicesForSpinner(db);
        while (cursor.moveToNext()) {
            serviceIds.add(
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow("service_id")
                    )
            );
            services.add(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("service_name")
                    )
            );
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                services
        );
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        spinnerService.setAdapter(adapter);
    }

    private void loadTechnicians() {
        ArrayList<String> technicians = new ArrayList<>();
        technicianIds.clear();
        technicians.add("Not Assigned");
        technicianIds.add(-1);
        Cursor cursor = databaseHelper.getTechniciansForSpinner(db);
        while (cursor.moveToNext()) {
            technicianIds.add(
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow("technician_id")
                    )
            );
            technicians.add(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("name")
                    )
            );
        }
        cursor.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                technicians
        );
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        spinnerTechnician.setAdapter(adapter);
    }

    private void loadStatuses() {
        ArrayList<String> statuses = new ArrayList<>();
        statuses.add("Pending");
        statuses.add("In Progress");
        statuses.add("Completed");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                statuses
        );
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        spinnerStatus.setAdapter(adapter);
    }

    private void addRepair() {
        if (userIds.size() == 0 ||
                branchIds.size() == 0 ||
                serviceIds.size() == 0) {
            Toast.makeText(
                    this,
                    "Please make sure customers, branches and services exist.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        String deviceModel = editDeviceModel
                .getText()
                .toString()
                .trim();
        String problem = editProblem
                .getText()
                .toString()
                .trim();
        String appointmentDate = editDate
                .getText()
                .toString()
                .trim();
        if (deviceModel.isEmpty() ||
                appointmentDate.isEmpty()) {
            Toast.makeText(
                    this,
                    "Please fill in the required fields.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        int userId = userIds.get(
                spinnerUser.getSelectedItemPosition()
        );
        int branchId = branchIds.get(
                spinnerBranch.getSelectedItemPosition()
        );
        int serviceId = serviceIds.get(
                spinnerService.getSelectedItemPosition()
        );
        int technicianPosition =
                spinnerTechnician.getSelectedItemPosition();
        Integer technicianId = null;
        if (technicianPosition > 0) {
            technicianId = technicianIds.get(technicianPosition);
        }
        String status =
                spinnerStatus.getSelectedItem().toString();

        String createdAt = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
        ).format(new Date());
        long appointmentId = databaseHelper.insertAppointment(
                db,
                userId,
                branchId,
                serviceId,
                technicianId,
                deviceModel,
                problem,
                appointmentDate,
                status,
                createdAt
        );
        if (appointmentId == -1) {

            Toast.makeText(
                    this,
                    "Failed to add repair order.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }
        if (repairImage != null) {

            ByteArrayOutputStream stream =
                    new ByteArrayOutputStream();

            repairImage.compress(
                    Bitmap.CompressFormat.JPEG,
                    80,
                    stream
            );
            byte[] imageBytes = stream.toByteArray();

            databaseHelper.updateServiceImage(
                    db,
                    serviceId,
                    imageBytes
            );
        }
        Toast.makeText(
                this,
                "Repair order added.",
                Toast.LENGTH_SHORT
        ).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
