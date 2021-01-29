package ru.job4j.tourist;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
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
        this.helper = new CoordinatesDbHelper(this);
        this.latitude = intent.getDoubleExtra("latitude", 0.0);
        this.longitude = intent.getDoubleExtra("longitude", 0.0);
        Button current = findViewById(R.id.current_location_button);
        Button toList = findViewById(R.id.to_location_list_button);
        current.setOnClickListener(v -> getCurrentLocation());
        toList.setOnClickListener(this::goToList);
        permissionMaster();
        getCurrentLocation();
    }
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
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
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 14) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
        }
    }
    private void getCurrentLocation() {
        if (location != null) {
            LatLng latLng;
            if (latitude == 0.0 && longitude == 0.0) {
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
            } else {
                latLng = new LatLng(latitude, longitude);
            }
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LocationListener loc = new LocationListener() {
            @Override
            public void onLocationChanged(Location lct) {
                location = lct;
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
            @Override
            public void onProviderEnabled(String provider) {
            }
            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, loc);
    }
}
