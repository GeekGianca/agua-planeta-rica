package com.example.agua_planeta_rica.view.main;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;

import com.example.agua_planeta_rica.R;
import com.example.agua_planeta_rica.databinding.ActivityMainBinding;
import com.example.agua_planeta_rica.databinding.ItemRequestLayoutBinding;
import com.example.agua_planeta_rica.model.Product;
import com.example.agua_planeta_rica.model.Request;
import com.example.agua_planeta_rica.util.Common;
import com.example.agua_planeta_rica.view.login.LoginActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.agua_planeta_rica.util.Common.CURRENT_USER;
import static com.example.agua_planeta_rica.util.Common.CURRENT_USER_MODEL;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private List<Product> productList = new ArrayList<>();
    private List<String> products = new ArrayList<>();
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private DatabaseReference mReferenceRequest;
    private AlertDialog mDialog;
    private ProgressDialog mProgress;
    private double latitude;
    private double longitude;
    private boolean isSettings = false;
    //Var for alert
    private ItemRequestLayoutBinding bindView;
    private Product selectedProduct;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        binding.toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_logout:
                    mAuth.signOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    return true;
                case R.id.action_pqr:
                    return true;
                default:
                    return false;
            }
        });
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    Common.INSTANCE.placeToPurchases();
                } else {
                    Common.INSTANCE.placeToAdapter();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        binding.fab.setOnClickListener(view -> {
            requestPermissions();
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Log.d(TAG, "Get current last location");
                    latitude = locationResult.getLastLocation().getLatitude();
                    longitude = locationResult.getLastLocation().getLongitude();
                }
            }
        };
    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    private void requestPermissions() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        startLocationUpdates();
                        createAlertRequest();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        if (permissionDeniedResponse.isPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).onSameThread().check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Permisos necesarios");
        builder.setMessage("Esta aplicación necesita permiso para usar esta función. Puede otorgarlos en la configuración de la aplicación.");
        builder.setPositiveButton("CONFIGURACIONES", (dialog, which) -> {
            dialog.cancel();
            openSettings();
            isSettings = true;
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> {
            dialog.cancel();
            binding.fab.setEnabled(false);
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void createAlertRequest() {
        AlertDialog.Builder mMaterial = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.item_request_layout, null, false);
        bindView = ItemRequestLayoutBinding.bind(mView);
        bindView.code.setText(UUID.randomUUID().toString());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item, products);
        bindView.typeRequest.setAdapter(adapter);
        bindView.quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    bindView.totalPrice.setText("");
                } else {
                    double value = Double.parseDouble(editable.toString());
                    double total = value * price();
                    bindView.totalPrice.setText(String.valueOf(total));
                }
            }
        });
        bindView.request.setOnClickListener(view -> {
            mProgress = new ProgressDialog(MainActivity.this);
            mProgress.setMessage("Registrando...");
            mProgress.setCanceledOnTouchOutside(false);
            mProgress.setCancelable(false);
            mProgress.show();

            Request request = new Request();
            request.setCode(bindView.code.getText().toString());
            request.setType(bindView.typeRequest.getText().toString());
            request.setQuantity(Integer.parseInt(bindView.quantity.getText().toString()));
            request.setTotalPrice(Double.parseDouble(bindView.totalPrice.getText().toString()));
            request.setDetail(bindView.detail.getText().toString());
            request.setUsername(CURRENT_USER.getUid());
            request.setUserPhone(CURRENT_USER_MODEL.getPhone());
            request.setLat(latitude);
            request.setLng(longitude);
            request.setState(Common.STATE_REQUEST);
            mReferenceRequest.child(request.getCode()).setValue(request)
                    .addOnCompleteListener(task -> {
                        mProgress.dismiss();
                        mProgress.cancel();
                        if (task.isSuccessful()) {
                            mDialog.dismiss();
                            mDialog.cancel();
                            Snackbar.make(view, "Solicitud registrada exitosamente.", Snackbar.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(e -> {
                mProgress.dismiss();
                mProgress.cancel();
                Toast.makeText(MainActivity.this, "Error al solicitar.", Toast.LENGTH_SHORT).show();
            });
        });
        mMaterial.setView(bindView.getRoot());
        mMaterial.create();
        mDialog = mMaterial.create();
        mDialog.show();
    }

    private double price() {
        double price = 0;
        for (Product p : productList) {
            if (p.getName().equals(bindView.typeRequest.getText().toString())) {
                selectedProduct = p;
                price = p.getPrice();
            }
        }
        return price;
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
        if (isSettings) {
            binding.fab.setEnabled(true);
            isSettings = false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        createLocationRequest();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("products");
        mReferenceRequest = mDatabase.getReference("requests");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                products.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Product mProduct = postSnapshot.getValue(Product.class);
                    productList.add(mProduct);
                    products.add(mProduct.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        });
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Log.d("Latitude", "disable");
    }
}