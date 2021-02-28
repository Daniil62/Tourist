package ru.job4j.tourist.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import ru.job4j.tourist.R;

public class MenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        LinearLayout pointManager = findViewById(R.id.point_manager_layout);
        LinearLayout trackManager = findViewById(R.id.track_manager_layout);
        LinearLayout landmarkManager = findViewById(R.id.landmark_manager_layout);
        LinearLayout searchPlace = findViewById(R.id.places_layout);
        pointManager.setOnClickListener(this::onPointManagerClick);
        searchPlace.setOnClickListener(this::onSearchPlaceClick);
        trackManager.setOnClickListener(this::onTrackManagerClick);
        landmarkManager.setOnClickListener(this::onLandmarksManagerClick);
    }
    private void onPointManagerClick(View view) {
        startActivity(new Intent(this, PlacesListActivity.class));
    }
    private void onSearchPlaceClick(View view) {
        startActivity(new Intent(this, PlacesActivity.class));
    }
    private void onTrackManagerClick(View view) {
        startActivity(new Intent(this, TrackListActivity.class));
    }
    private void onLandmarksManagerClick(View view) {
        Intent intent = new Intent(this, InstrumentationActivity.class);
        startActivity(intent);
    }
}
