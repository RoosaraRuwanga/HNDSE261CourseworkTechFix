package com.nibm.hndse261coursework_techfix.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.nibm.hndse261coursework_techfix.R;
import com.nibm.hndse261coursework_techfix.database.DatabaseHelper;

import java.util.ArrayList;

public class ServicesActivity extends AppCompatActivity {

    Spinner spinnerServices;
    Spinner spinnerBranch;

    ImageView imgService;

    TextView tvServiceTitle;
    TextView tvServiceDescription;

    Button btnRequestService;
    Button btnServicesBack;

    DatabaseHelper databaseHelper;

    // Store branch IDs separately from the displayed names
    ArrayList<Integer> branchIds = new ArrayList<>();
    ArrayList<String> branchNames = new ArrayList<>();

    // Store service IDs separately from the displayed names
    ArrayList<Integer> serviceIds = new ArrayList<>();
    ArrayList<String> serviceNames = new ArrayList<>();
    ArrayList<String> serviceDescriptions = new ArrayList<>();


    // ---------------------------------------------------------
    // LOCATION PERMISSION
    // ---------------------------------------------------------

    private final ActivityResultLauncher<String[]> locationPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(),
                    result -> {

                        Boolean fineLocation =
                                result.get(Manifest.permission.ACCESS_FINE_LOCATION);

                        Boolean coarseLocation =
                                result.get(Manifest.permission.ACCESS_COARSE_LOCATION);

                        if (Boolean.TRUE.equals(fineLocation)
                                || Boolean.TRUE.equals(coarseLocation)) {

                            findClosestBranch();

                        } else {

                            Toast.makeText(
                                    this,
                                    "Location permission is required to select the closest branch.",
                                    Toast.LENGTH_LONG
                            ).show();

                            spinnerBranch.setEnabled(false);
                        }
                    }
            );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_services);

        // -----------------------------------------------------
        // CONNECT XML
        // -----------------------------------------------------

        spinnerServices = findViewById(R.id.spinnerServices);
        spinnerBranch = findViewById(R.id.spinnerBranch);

        imgService = findViewById(R.id.imgService);

        tvServiceTitle = findViewById(R.id.tvServiceTitle);
        tvServiceDescription = findViewById(R.id.tvServiceDescription);

        btnRequestService = findViewById(R.id.btnRequestService);
        btnServicesBack = findViewById(R.id.btnServicesBack);

        databaseHelper = new DatabaseHelper(this);


        // -----------------------------------------------------
        // BACK BUTTON
        // -----------------------------------------------------

        btnServicesBack.setOnClickListener(v -> finish());


        // -----------------------------------------------------
        // LOAD SERVICES
        // -----------------------------------------------------

        loadServices();


        // -----------------------------------------------------
        // SERVICE SELECTION
        // -----------------------------------------------------

        spinnerServices.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            android.widget.AdapterView<?> parent,
                            android.view.View view,
                            int position,
                            long id) {

                        if (!serviceNames.isEmpty()
                                && position >= 0
                                && position < serviceNames.size()) {

                            tvServiceTitle.setText(
                                    serviceNames.get(position)
                            );

                            tvServiceDescription.setText(
                                    serviceDescriptions.get(position)
                            );
                        }
                    }

                    @Override
                    public void onNothingSelected(
                            android.widget.AdapterView<?> parent) {
                    }
                }
        );


        // -----------------------------------------------------
        // REQUEST LOCATION
        // -----------------------------------------------------

        checkLocationPermission();
    }


    // =========================================================
    // LOAD SERVICES FROM EXISTING DATABASE
    // =========================================================

    private void loadServices() {

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = null;

        try {

            cursor = db.rawQuery(
                    "SELECT service_id, service_name, description " +
                            "FROM RepairService",
                    null
            );

            serviceIds.clear();
            serviceNames.clear();
            serviceDescriptions.clear();

            while (cursor.moveToNext()) {

                int serviceId =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow("service_id")
                        );

                String serviceName =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("service_name")
                        );

                String description =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("description")
                        );

                serviceIds.add(serviceId);
                serviceNames.add(serviceName);

                if (description == null || description.isEmpty()) {
                    serviceDescriptions.add("No description available.");
                } else {
                    serviceDescriptions.add(description);
                }
            }

            ArrayAdapter<String> adapter =
                    new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_spinner_item,
                            serviceNames
                    );

            adapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
            );

            spinnerServices.setAdapter(adapter);

        } finally {

            if (cursor != null) {
                cursor.close();
            }

            db.close();
        }
    }


    // =========================================================
    // CHECK LOCATION PERMISSION
    // =========================================================

    private void checkLocationPermission() {

        boolean fineLocationGranted =
                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED;

        boolean coarseLocationGranted =
                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED;


        if (fineLocationGranted || coarseLocationGranted) {

            findClosestBranch();

        } else {

            locationPermissionLauncher.launch(
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }
            );
        }
    }


    // =========================================================
    // FIND CLOSEST BRANCH
    // =========================================================

    private void findClosestBranch() {

        LocationManager locationManager =
                (LocationManager) getSystemService(LOCATION_SERVICE);


        if (locationManager == null) {

            Toast.makeText(
                    this,
                    "Location service unavailable.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        // Check whether GPS is enabled

        boolean gpsEnabled = false;

        try {

            gpsEnabled =
                    locationManager.isProviderEnabled(
                            LocationManager.GPS_PROVIDER
                    );

        } catch (Exception e) {

            e.printStackTrace();
        }


        if (!gpsEnabled) {

            Toast.makeText(
                    this,
                    "Please enable GPS/Location on your phone.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        try {

            Location location = null;


            // -------------------------------------------------
            // TRY GPS LOCATION
            // -------------------------------------------------

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {

                location =
                        locationManager.getLastKnownLocation(
                                LocationManager.GPS_PROVIDER
                        );
            }


            // -------------------------------------------------
            // TRY NETWORK LOCATION IF GPS IS NULL
            // -------------------------------------------------

            if (location == null) {

                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED) {

                    location =
                            locationManager.getLastKnownLocation(
                                    LocationManager.NETWORK_PROVIDER
                            );
                }
            }


            // -------------------------------------------------
            // NO LOCATION
            // -------------------------------------------------

            if (location == null) {

                Toast.makeText(
                        this,
                        "Unable to get your current location.",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }


            double userLatitude =
                    location.getLatitude();

            double userLongitude =
                    location.getLongitude();


            // Find nearest branch

            findNearestBranch(
                    userLatitude,
                    userLongitude
            );


        } catch (SecurityException e) {

            Toast.makeText(
                    this,
                    "Location permission is required.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }


    // =========================================================
    // COMPARE USER LOCATION WITH ALL BRANCHES
    // =========================================================

    private void findNearestBranch(
            double userLatitude,
            double userLongitude) {

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = null;


        try {

            cursor = db.rawQuery(
                    "SELECT branch_id, branch_name, latitude, longitude " +
                            "FROM Branch",
                    null
            );


            int closestBranchPosition = -1;

            double shortestDistance =
                    Double.MAX_VALUE;


            branchIds.clear();
            branchNames.clear();


            while (cursor.moveToNext()) {

                int branchId =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "branch_id"
                                )
                        );

                String branchName =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "branch_name"
                                )
                        );

                double branchLatitude =
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow(
                                        "latitude"
                                )
                        );

                double branchLongitude =
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow(
                                        "longitude"
                                )
                        );


                branchIds.add(branchId);
                branchNames.add(branchName);


                // ---------------------------------------------
                // CALCULATE DISTANCE
                // ---------------------------------------------

                float[] distance = new float[1];

                Location.distanceBetween(
                        userLatitude,
                        userLongitude,
                        branchLatitude,
                        branchLongitude,
                        distance
                );


                double distanceInMeters =
                        distance[0];


                // ---------------------------------------------
                // CHECK IF THIS IS THE CLOSEST BRANCH
                // ---------------------------------------------

                if (distanceInMeters < shortestDistance) {

                    shortestDistance =
                            distanceInMeters;

                    closestBranchPosition =
                            branchNames.size() - 1;
                }
            }


            // -------------------------------------------------
            // SHOW BRANCHES IN SPINNER
            // -------------------------------------------------

            ArrayAdapter<String> adapter =
                    new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_spinner_item,
                            branchNames
                    );

            adapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
            );

            spinnerBranch.setAdapter(adapter);


            // -------------------------------------------------
            // AUTOMATICALLY SELECT CLOSEST BRANCH
            // -------------------------------------------------

            if (closestBranchPosition >= 0) {

                spinnerBranch.setSelection(
                        closestBranchPosition
                );

                String closestBranch =
                        branchNames.get(
                                closestBranchPosition
                        );

                double distanceKm =
                        shortestDistance / 1000.0;


                Toast.makeText(
                        this,
                        "Closest branch: "
                                + closestBranch
                                + " ("
                                + String.format(
                                "%.2f km",
                                distanceKm
                        )
                                + ")",
                        Toast.LENGTH_SHORT
                ).show();

            } else {

                Toast.makeText(
                        this,
                        "No branches found.",
                        Toast.LENGTH_LONG
                ).show();
            }


        } finally {

            if (cursor != null) {
                cursor.close();
            }

            db.close();
        }
    }


    // =========================================================
    // REQUEST SERVICE
    // =========================================================

    private void requestService() {

        if (spinnerServices.getSelectedItem() == null) {

            Toast.makeText(
                    this,
                    "Please select a service.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        if (spinnerBranch.getSelectedItem() == null) {

            Toast.makeText(
                    this,
                    "Unable to determine your closest branch.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        String selectedService =
                spinnerServices
                        .getSelectedItem()
                        .toString();

        String selectedBranch =
                spinnerBranch
                        .getSelectedItem()
                        .toString();


        Toast.makeText(
                this,
                "Service: "
                        + selectedService
                        + "\nBranch: "
                        + selectedBranch,
                Toast.LENGTH_LONG
        ).show();


        // We will connect this to Appointment
        // after the GPS and UI are working correctly.
    }
}