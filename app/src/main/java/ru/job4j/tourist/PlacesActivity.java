package ru.job4j.tourist;

import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import java.util.Arrays;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import ru.job4j.tourist.data_base.CoordinatesDbHelper;

public class PlacesActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private CoordinatesDbHelper helper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        this.helper = new CoordinatesDbHelper(this);
        Places.initialize(this, getString(R.string.google_maps_key));
        AutocompleteSupportFragment search = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        if (search != null) {
            search.setPlaceFields(Arrays.asList(
                    Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
            search.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    LatLng pos = place.getLatLng();
                    if (pos != null) {
                        helper.loadLocation(pos);
                        MarkerOptions markerOptions =
                                new MarkerOptions().position(pos).title("Hello Maps");
                        map.addMarker(markerOptions);
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));
                    }
                }
                @Override
                public void onError(@NonNull Status status) {
                    Log.i("onError", status.toString());
                }
            });
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }
}
