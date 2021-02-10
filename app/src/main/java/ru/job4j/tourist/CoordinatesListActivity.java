package ru.job4j.tourist;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.gms.maps.model.LatLng;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.job4j.tourist.data_base.CoordinatesDbHelper;

public class CoordinatesListActivity extends AppCompatActivity {
    private CoordinatesDbHelper helper;
    private RecyclerView recycler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinates_list);
        this.helper = new CoordinatesDbHelper(this);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        this.recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(llm);
        loadItems();
    }
    private void loadItems() {
        recycler.setAdapter(new CoordinatesAdapter(helper.getLocations()));
    }
    private class CoordinatesAdapter extends RecyclerView.Adapter<CoordinatesHolder> {
        private final List<LatLng> coordinates;
        public CoordinatesAdapter (List<LatLng> list) {
            this.coordinates = list;
        }
        @Override
        public int getItemViewType(int position) {
            return position;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @NonNull
        @Override
        public CoordinatesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.coordinate_modul, parent, false);
            return new CoordinatesHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull CoordinatesHolder holder, int position) {
            LatLng latLng = coordinates.get(position);
            TextView latitudeText = holder.itemView.findViewById(R.id.latitude_textView);
            TextView longitudeText = holder.itemView.findViewById(R.id.longitude_textView);
            View item = holder.itemView;
            double latitude = latLng.latitude;
            double longitude = latLng.longitude;
            latitudeText.setText(String.valueOf(latitude));
            longitudeText.setText(String.valueOf(longitude));
            holder.itemView.setId(position);
            itemPainter(item, position);
            holder.itemView.setOnClickListener(v -> onItemClick(latitude, longitude));
        }
        private void itemPainter(View view, int numb) {
            if (numb % 2 == 0) {
                view.setBackgroundColor(Color.parseColor("#CCDDCC"));
            }
        }
        private void onItemClick(double latitude, double longitude) {
            Intent intent = new Intent(CoordinatesListActivity.this,
                    MapsActivity.class);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            startActivity(intent);
        }
        @Override
        public int getItemCount() {
            return coordinates.size();
        }
    }
    private static class CoordinatesHolder extends RecyclerView.ViewHolder {
        public CoordinatesHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
