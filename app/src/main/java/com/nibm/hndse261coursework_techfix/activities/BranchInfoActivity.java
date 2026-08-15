package com.nibm.hndse261coursework_techfix.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.nibm.hndse261coursework_techfix.R;
import com.nibm.hndse261coursework_techfix.database.DatabaseHelper;

public class BranchInfoActivity extends AppCompatActivity {

    TextView tvClosestBranch;
    Button btnBranchBack;

    DatabaseHelper databaseHelper;

    // Permission request
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
                                    "Location permission is required to find the closest branch.",
                                    Toast.LENGTH_LONG
                            ).show();

                            tvClosestBranch.setText(
                                    "Location permission required"
                            );
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_branch_info);

        tvClosestBranch = findViewById(R.id.tvClosestBranch);
        btnBranchBack = findViewById(R.id.btnBranchBack);

        databaseHelper = new DatabaseHelper(this);

        // Back button
        btnBranchBack.setOnClickListener(v -> finish());

        // Start GPS process
        checkLocationPermission();
    }

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

    private void findClosestBranch() {

        LocationManager locationManager =
                (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager == null) {

            tvClosestBranch.setText(
                    "Location service unavailable"
            );

            return;
        }

        // Check if GPS/location services are enabled
        boolean gpsEnabled = false;

        try {
            gpsEnabled = locationManager.isProviderEnabled(
                    LocationManager.GPS_PROVIDER
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!gpsEnabled) {

            tvClosestBranch.setText(
                    "Please enable GPS"
            );

            Toast.makeText(
                    this,
                    "Please enable Location/GPS on your phone.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        try {

            Location location = null;

            // Get the most recent GPS location
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {

                location = locationManager.getLastKnownLocation(
                        LocationManager.GPS_PROVIDER
                );
            }

            // Try network location if GPS location isn't available
            if (location == null) {

                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED) {

                    location = locationManager.getLastKnownLocation(
                            LocationManager.NETWORK_PROVIDER
                    );
                }
            }

            if (location == null) {

                tvClosestBranch.setText(
                        "Unable to get your location"
                );

                Toast.makeText(
                        this,
                        "Please make sure Location is enabled and try again.",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

            double userLatitude = location.getLatitude();
            double userLongitude = location.getLongitude();

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

    private void findNearestBranch(
            double userLatitude,
            double userLongitude) {

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = null;

        try {

            cursor = db.rawQuery(
                    "SELECT branch_id, branch_name, address, phone, latitude, longitude " +
                            "FROM Branch",
                    null
            );

            String closestBranchName = null;
            double shortestDistance = Double.MAX_VALUE;

            while (cursor.moveToNext()) {

                String branchName =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("branch_name")
                        );

                double branchLatitude =
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow("latitude")
                        );

                double branchLongitude =
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow("longitude")
                        );

                float[] distance = new float[1];

                Location.distanceBetween(
                        userLatitude,
                        userLongitude,
                        branchLatitude,
                        branchLongitude,
                        distance
                );

                double distanceInMeters = distance[0];

                if (distanceInMeters < shortestDistance) {

                    shortestDistance = distanceInMeters;
                    closestBranchName = branchName;
                }
            }

            if (closestBranchName != null) {

                double distanceInKilometers =
                        shortestDistance / 1000.0;

                tvClosestBranch.setText(
                        closestBranchName
                                + "\n"
                                + String.format(
                                "%.2f km away",
                                distanceInKilometers
                        )
                );

            } else {

                tvClosestBranch.setText(
                        "No branches found"
                );
            }

        } finally {

            if (cursor != null) {
                cursor.close();
            }

            db.close();
        }
    }
}