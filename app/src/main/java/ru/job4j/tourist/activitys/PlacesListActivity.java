package ru.job4j.tourist.activitys;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.job4j.tourist.R;
import ru.job4j.tourist.data_base.CoordinatesDbHelper;
import ru.job4j.tourist.model.Place;

public class PlacesListActivity extends AppCompatActivity {
    private CoordinatesDbHelper helper;
    private RecyclerView recycler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinates_list);
        this.helper = new CoordinatesDbHelper(this);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        this.recycler = findViewById(R.id.coordinates_recycler);
        recycler.setLayoutManager(llm);
    }
    @Override
    protected void onStart() {
        super.onStart();
        loadItems();
    }
    private void loadItems() {
        recycler.setAdapter(new CoordinatesAdapter(helper.getPlaces()));
    }
    private class CoordinatesAdapter extends RecyclerView.Adapter<CoordinatesHolder> {
        private final List<Place> coordinates;
        public CoordinatesAdapter (List<Place> list) {
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
            View view = inflater.inflate(R.layout.coordinate_module, parent, false);
            return new CoordinatesHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull CoordinatesHolder holder, int position) {
            Place place = coordinates.get(position);
            TextView itemInfo = holder.itemView.findViewById(R.id.place_info_textView);
            View item = holder.itemView;
            setItemInfo(itemInfo, place);
            holder.itemView.setId(position);
            itemPainter(item, position);
            holder.itemView.setOnClickListener(v -> onItemClick(item.getId(), place));
        }
        private void setItemInfo(TextView textView, Place place) {
            String info = place.getTitle();
            if (info.equals("")) {
                info = place.getCoordinates().latitude + "\n" + place.getCoordinates().longitude;
            }
            textView.setText(info);
        }
        private void itemPainter(View view, int numb) {
            if (numb % 2 == 0) {
                view.setBackgroundColor(Color.parseColor("#CCDDCC"));
            }
        }
        private void onItemClick(int id, Place place) {
            Intent intent = new Intent(PlacesListActivity.this,
                    ObjectManagerActivity.class);
            intent.putExtra("title", place.getTitle());
            intent.putExtra("place_id", id);
            intent.putExtra("latitude", place.getCoordinates().latitude);
            intent.putExtra("longitude", place.getCoordinates().longitude);
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
