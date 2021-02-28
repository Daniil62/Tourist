package ru.job4j.tourist.activitys;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import ru.job4j.tourist.R;
import ru.job4j.tourist.data_base.CoordinatesDbHelper;

public class PreviewActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private LatLng location;
    CoordinatesDbHelper helper;
    private int id;
    private boolean isItTrack;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        Intent intent = getIntent();
        this.helper = new CoordinatesDbHelper(this);
        this.id = intent.getIntExtra("id", -1);
        this.isItTrack = intent.getBooleanExtra("track", false);
        setMapFragment();
        setLocation();
        Button start = findViewById(R.id.start_button);
        start.setOnClickListener(this::onStartClick);
    }
    private void setLocation() {
        Intent intent = getIntent();
        this.location = new LatLng(intent.getDoubleExtra("latitude", 0.0),
                intent.getDoubleExtra("longitude", 0.0));
    }
    private void onStartClick(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        if (isItTrack) {
            intent.putExtra("id", id);
        } else {
            intent.putExtra("latitude", location.latitude);
            intent.putExtra("longitude", location.longitude);
        }
        intent.putExtra("purpose", true);
        startActivity(intent);
    }
    private void setMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.preview_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }
    private void setPolyline() {
        if (map != null && id > -1) {
            List<LatLng> list = helper.getTrackCoordinates(id + 1);
            Polyline line = map.addPolyline(
                    new PolylineOptions().clickable(true)
                            .addAll(list));
            line.setColor(Color.BLUE);
            line.setTag("B");
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(list.get(0), 15));
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        if (!isItTrack) {
            googleMap.addMarker(new MarkerOptions().position(location).title("location"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        } else {
            setPolyline();
        }
    }
}
