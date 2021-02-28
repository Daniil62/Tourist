package ru.job4j.tourist.masters;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Location;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;
import ru.job4j.tourist.model.Place;
import ru.job4j.tourist.model.Track;

public class MapMaster extends AppCompatActivity {
    private static GoogleMap map;
    private static Location lastLocation;
    private static Location location;
    private static LatLng currentLatLng;
    private static LatLng purposeLatLng;
    private static float distance = 0;
    private String distanceToPurposeText = "--";
    private static double altitude = 0.0;
    private static boolean hasPurpose;
    private boolean isStartMarkerAdded = false;
    private static final List<Polyline> lines = new ArrayList<>();
    private static final List<Marker> markers = new ArrayList<>();
    private static final List<LatLng> trackPoints = new ArrayList<>();
    public MapMaster(GoogleMap m) {
        map = m;
    }
    public MapMaster() {}
    public void setLocation(Location loc) {
        location = loc;
    }
    public void setLatLng(LatLng ll) {
        currentLatLng = ll;
    }
    public static void setPurposeLatLng(LatLng purposeLatLng) {
        MapMaster.purposeLatLng = purposeLatLng;
    }
    public void setHasPurpose(boolean value) {
        hasPurpose = value;
    }
    public boolean checkPurpose() {
        return hasPurpose;
    }
    public void setStartMarkerAdded(boolean startMarkerAdded) {
        isStartMarkerAdded = startMarkerAdded;
    }
    public List<LatLng> getTrackPoints() {
        return trackPoints;
    }
    public static void onCurrentLocation() {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            Marker marker = map.addMarker(
                    new MarkerOptions().position(latLng).flat(true).title("Current location"));
            marker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
    }
    public void onLocation(double latitude, double longitude) {
        if (latitude != 0 && longitude != 0) {
            LatLng latLng = new LatLng(latitude, longitude);
            if (!isStartMarkerAdded) {
                map.addMarker(new MarkerOptions().position(latLng).title("location"));
                moveCamera();
                isStartMarkerAdded = true;
            }
        }
    }
    public void addPurposeMarker(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        Marker marker = map.addMarker(new MarkerOptions().position(latLng).title("location"));
        markers.add(marker);
    }
    public void removeMarker() {
        if (markers.size() != 0) {
            for (Marker marker : markers) {
                if (marker != null) {
                    marker.remove();
                }
            }
        }
    }
    public void setPolyline(List<LatLng> list) {
        Polyline line = map.addPolyline(
                new PolylineOptions().clickable(true).addAll(list));
        line.setColor(Color.BLUE);
        line.setTag("B");
    }
    public void setCurrentPolyline() {
   //     if (location.hasSpeed()) {
            if (map != null) {
                Polyline line = map.addPolyline(
                        new PolylineOptions().clickable(true).addAll(trackPoints));
                line.setColor(Color.RED);
                line.setTag("A");
            }
   //     }
    }
    public void setLineToPurpose() {
        PolylineOptions polylineOptions =
                new PolylineOptions().width(13).color(Color.GREEN).geodesic(true);
        removeLine();
        polylineOptions.add(currentLatLng, purposeLatLng);
        lines.add(map.addPolyline(polylineOptions));
    }
    public void removeLine() {
        if (lines.size() != 0) {
            for(Polyline pl : lines) {
                pl.remove();
            }
            lines.clear();
        }
    }
    public void moveCamera() {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
    }
    public Track getSavingTrack(String title) {
        return new Track(trackPoints, new Date().getTime(), title);
    }
    public Place getSavingPlace(String title) {
        return new Place(currentLatLng, title);
    }
    public void calculateCoveredDistance() {
        if (lastLocation != null &&
                (location.getLatitude() != 0.0 && location.getLongitude() != 0.0)) {
            distance += lastLocation.distanceTo(location);
        }
        if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
            lastLocation = location;
        }
    }
    @SuppressLint("DefaultLocale")
    public String getCoveredDistance() {
        return String.format("%.2f", distance / 1000);
    }
    @SuppressLint("DefaultLocale")
    public String getDistanceToPurpose() {
        if (hasPurpose) {
            Location purpose = new Location("");
            purpose.setLatitude(purposeLatLng.latitude);
            purpose.setLongitude(purposeLatLng.longitude);
            if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
                distanceToPurposeText = String.format("%.2f", location.distanceTo(purpose) / 1000);
            }
        }
        return distanceToPurposeText;
    }
    public float getSpeed() {
        return location.getSpeed();
    }
    @SuppressLint("DefaultLocale")
    public String getCurrentLatitudeString() {
        return String.format("%.6f", currentLatLng.latitude);
    }
    @SuppressLint("DefaultLocale")
    public String getCurrentLongitudeString() {
        return String.format("%.6f", currentLatLng.longitude);
    }
    @SuppressLint("DefaultLocale")
    public String getCurrentAltitudeString() {
        if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
            altitude = location.getAltitude();
        }
        return String.format("%.6f", altitude);
    }
}
