package com.example.agua_planeta_rica.view.delivery;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.agua_planeta_rica.R;
import com.example.agua_planeta_rica.databinding.ActivityMapsBinding;
import com.example.agua_planeta_rica.model.Request;
import com.example.agua_planeta_rica.util.Common;
import com.example.agua_planeta_rica.util.FetchURL;
import com.example.agua_planeta_rica.util.LocationServiceBcst;
import com.example.agua_planeta_rica.util.TaskLoadedCallback;
import com.example.agua_planeta_rica.view.main.MainActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback {
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private AlertDialog dialog;
    private static MapsActivity instance;
    private PolylineOptions polylineOptions;
    private DatabaseReference mReference;

    public static MapsActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Paper.init(this);
        instance = this;
        binding.openGoogleMaps.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude() + "&daddr=" + Common.DELIVERY_NOW.getLat() + "," + Common.DELIVERY_NOW.getLng()));
            startActivity(intent);
        });
        binding.done.setOnClickListener(v -> {
            Map<String, Object> childUpdateFarm = new HashMap<>();
            Request req = Common.DELIVERY_NOW;
            req.setState("Entregado");
            childUpdateFarm.put("/requests/" + Common.DELIVERY_NOW.getCode(), req);
            mReference.updateChildren(childUpdateFarm);
            Toast.makeText(MapsActivity.this, "Entrega exitosa.", Toast.LENGTH_SHORT).show();
            finish();
        });

        binding.deny.setOnClickListener(v -> {
            Map<String, Object> childUpdateFarm = new HashMap<>();
            Request req = Common.DELIVERY_NOW;
            req.setState("Cancelado");
            childUpdateFarm.put("/requests/" + Common.DELIVERY_NOW.getCode(), req);
            mReference.updateChildren(childUpdateFarm);
            Toast.makeText(MapsActivity.this, "Producto cancelado.", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void checkAndDisplayPermissions() {
        Log.d(TAG, "Displaying permissions");
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            fetchLastLocation();
                        } else {
                            Log.d(TAG, "Deny Permissions");
                            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MapsActivity.this);
                            View mView = LayoutInflater.from(MapsActivity.this).inflate(R.layout.alerts_errors, null, false);
                            TextView titleInfoTxt = mView.findViewById(R.id.text_error);
                            titleInfoTxt.setText("Si rechazas el permiso de ubicación, no podrás visualizar las estaciones en el mapa.\n\nHabilita los permisos de ubicación en tu menú de configuración.");
                            TextView button_text = mView.findViewById(R.id.title_buttom);
                            button_text.setText("Habilitar permisos");
                            button_text.setOnClickListener(v -> {
                                dialog.dismiss();
                                Paper.book().write("isSettingEnable", "true");
                                Intent settingsPermissions = new Intent();
                                settingsPermissions.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                settingsPermissions.setData(uri);
                                startActivity(settingsPermissions);
                            });
                            builder.setView(mView);
                            dialog = builder.create();
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        Log.d(TAG, "onPermissionRationaleShouldBeShown");
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    @SuppressLint("MissingPermission")
    private void fetchLastLocation() {
        Log.d(TAG, "Fetching location");
        buildLocationRequest();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(location -> {
            try {
                if (location != null) {
                    getLastLocation(location);
                } else {
                    Log.d(TAG, "Location is null");
                    fusedLocationProviderClient.getLastLocation()
                            .addOnSuccessListener(this, location1 -> {
                                if (location1 != null) {
                                    getLastLocation(location1);
                                } else {
                                    Log.d(TAG, "Location null, retrieve location.");
                                    checkAndDisplayPermissions();
                                    SystemClock.sleep(1200);
                                }
                            });
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        });
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, LocationServiceBcst.class);
        intent.setAction(LocationServiceBcst.ACTION_PROCESS_UPDATE);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void updateLastLocation(Location location) {
        getLastLocation(location);
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(60000);
        locationRequest.setFastestInterval(30000);
        locationRequest.setSmallestDisplacement(9f);
    }

    private void getLastLocation(Location location) {
        Log.d(TAG, "Fetch and display map");
        currentLocation = location;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        // Add a marker in Sydney and move the camera
        LatLng current = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLng(current));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, 13), 10, null);
        String urlDriving = getUrl(current, new LatLng(Common.DELIVERY_NOW.getLat(), Common.DELIVERY_NOW.getLng()));
        Log.d(TAG, urlDriving);
        new FetchURL(this, this).execute(urlDriving, "walking");
        Toast.makeText(this, "Analizando la ruta mas cercana.", Toast.LENGTH_SHORT).show();
        double lat = Common.DELIVERY_NOW.getLat();
        double longt = Common.DELIVERY_NOW.getLng();
        mMap.addMarker(createMarker(lat, longt, Common.DELIVERY_NOW.getUsername(), R.drawable.ic_marker));
    }

    private MarkerOptions createMarker(double latitude, double longitude, String title, int resource) {
        return new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .icon(bitmapDescriptorFromVector(this, resource));
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_marker);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            boolean isSettingEnable = Boolean.parseBoolean(Paper.book().read("isSettingEnable"));
            if (isSettingEnable) {
                if (dialog != null) {
                    while (dialog.isShowing()) {
                        dialog.dismiss();
                        dialog.cancel();
                    }
                }
                checkAndDisplayPermissions();
                Paper.book().delete("isSettingEnable");
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkAndDisplayPermissions();
        mReference = FirebaseDatabase.getInstance().getReference();
    }

    private String getUrl(LatLng current, LatLng destinat) {
        String origin = "origin=" + current.latitude + "," + current.longitude;
        String destination = "destination=" + destinat.latitude + "," + destinat.longitude;
        //mode
        String mode = "mode=" + "walking";
        String parameters = origin + "&" + destination + "&" + mode;
        String output = "json";
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=AIzaSyBeKKULlSoRuuajoWgdjBDYtTkUyOax98o";
    }

    @Override
    public void onTaskDone(Object... values) {
        polylineOptions = (PolylineOptions) values[0];
    }
}