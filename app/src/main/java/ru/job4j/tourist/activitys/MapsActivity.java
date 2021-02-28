package ru.job4j.tourist.activitys;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import ru.job4j.tourist.data_base.CoordinatesDbHelper;
import ru.job4j.tourist.interfaces.LocationListenerInterface;
import ru.job4j.tourist.R;
import ru.job4j.tourist.masters.ControlPanelMaster;
import ru.job4j.tourist.masters.LocationListenerMaster;
import ru.job4j.tourist.masters.MapMaster;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, LocationListenerInterface {
    private MapMaster master;
    private CoordinatesDbHelper helper;
    private boolean hasPurpose;
    private int id;
    private ControlPanelMaster controlPanelMaster;
    private ImageButton hideShowPanelButton;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Places.initialize(this, getString(R.string.google_maps_key));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        permissionMaster();
        Intent intent = getIntent();
        this.helper = new CoordinatesDbHelper(this);
        this.hasPurpose = intent.getBooleanExtra("purpose", false);
        this.id = intent.getIntExtra("id", -1);
        this.controlPanelMaster = findViewById(R.id.control_panel);
        controlPanelMaster.setEnabled(false);
        this.hideShowPanelButton = findViewById(R.id.hide_show_panel_imageButton);
        hideShowPanelButton.setImageResource(R.drawable.ic_show_control_panel_foreground);
        hideShowPanelButton.setOnClickListener(this::onHideShowButtonClick);
    }
    private void onHideShowButtonClick(View view) {
        if (!controlPanelMaster.isEnabled()) {
            controlPanelMaster.setVisibility(View.VISIBLE);
            controlPanelMaster.setEnabled(true);
            hideShowPanelButton.setImageResource(R.drawable.ic_hide_control_panel_foreground);
        } else {
            controlPanelMaster.setVisibility(View.GONE);
            controlPanelMaster.setEnabled(false);
            hideShowPanelButton.setImageResource(R.drawable.ic_show_control_panel_foreground);
        }
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        master = new MapMaster(googleMap);
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListenerMaster locListener = new LocationListenerMaster();
        locListener.setListenerInterface(this);
        if (ActivityCompat.checkSelfPermission(
                this, FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000, 1, locListener);
        setPurposeAttributes();
    }
    private void setPurposeAttributes() {
        if (hasPurpose) {
            if (id != -1) {
                master.setPolyline(helper.getTrackCoordinates(id + 1));
            } else {
                master.setStartMarkerAdded(false);
                Intent intent = getIntent();
                master.addPurposeMarker(
                        intent.getDoubleExtra("latitude", 0.0),
                        intent.getDoubleExtra("longitude", 0.0));
            }
            master.setHasPurpose(true);
        }
    }
    @Override
    public void onLocationChanged(Location loc) {
        master.setLocation(loc);
        updateSituation(loc.getLatitude(), loc.getLongitude());
    }
    private void updateSituation(Double latitude, double longitude) {
        if (latitude != 0.0 && longitude != 0.0) {
            LatLng latLng = new LatLng(latitude, longitude);
            master.setLatLng(latLng);
            master.getTrackPoints().add(latLng);
            master.calculateCoveredDistance();
            updatePurposeAttributes();
            master.onLocation(latitude, longitude);
        }
    }
    private void updatePurposeAttributes() {
        if (hasPurpose && master.checkPurpose()) {
            double latitude = getIntent().getDoubleExtra("latitude", 0.0);
            double longitude = getIntent().getDoubleExtra("longitude", 0.0);
            LatLng purposeLatLng = latitude == 0.0 && longitude == 0.0 ?
                    helper.getTrackCoordinates(id + 1).get(0)
                    : new LatLng(latitude, longitude);
            MapMaster.setPurposeLatLng(purposeLatLng);
            master.setLineToPurpose();
        } else {
            master.setCurrentPolyline();
            master.moveCamera();
        }
    }
}
