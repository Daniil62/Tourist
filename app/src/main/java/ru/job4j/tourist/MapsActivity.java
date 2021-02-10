package ru.job4j.tourist;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import ru.job4j.tourist.data_base.CoordinatesDbHelper;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, LocationListenerInterface {
    private CoordinatesDbHelper helper;
    private Location location;
    private GoogleMap map;
    private double latitude;
    private double longitude;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Places.initialize(this, getString(R.string.google_maps_key));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent intent = getIntent();
        permissionMaster();
        this.helper = new CoordinatesDbHelper(this);
        this.latitude = intent.getDoubleExtra("latitude", 0.0);
        this.longitude = intent.getDoubleExtra("longitude", 0.0);
        Button current = findViewById(R.id.current_location_button);
        Button toList = findViewById(R.id.to_location_list_button);
        ImageButton toPlaces = findViewById(R.id.places_imageButton);
        current.setOnClickListener(v -> getCurrentLocation());
        toList.setOnClickListener(this::goToList);
        toPlaces.setOnClickListener(v ->
            startActivity(new Intent(this, PlacesActivity.class))
        );
    }
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
    private void setMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }
    private void permissionMaster() {
        String[] permissions = {COARSE_LOCATION, FINE_LOCATION};
        boolean locPermissions = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ActivityCompat.checkSelfPermission(this, COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
        if (!locPermissions) {
            ActivityCompat.requestPermissions(this, permissions, 14);
        } else {
            setMapFragment();
        }
    }
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 14) {
            setMapFragment();
        }
    }
    private void getCurrentLocation() {
        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            helper.loadLocation(latLng);
            MarkerOptions marker = new MarkerOptions().position(latLng).title("Current location");
            marker.flat(true);
            map.addMarker(marker);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
    }
    private void goToList(View view) {
        Intent intent = new Intent(MapsActivity.this, CoordinatesListActivity.class);
        startActivity(intent);
    }
    private void onLocation(GoogleMap map) {
        if (latitude != 0 && longitude != 0) {
            LatLng latLng = new LatLng(latitude, longitude);
            map.addMarker(new MarkerOptions().position(latLng).title("location"));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
    }
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListenerMaster locListener = new LocationListenerMaster();
        locListener.setListenerInterface(this);
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000, 5, locListener);
        onLocation(map);
    }
    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }
}
