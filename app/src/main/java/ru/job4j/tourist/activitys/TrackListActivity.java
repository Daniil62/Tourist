package ru.job4j.tourist.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.job4j.tourist.R;
import ru.job4j.tourist.data_base.CoordinatesDbHelper;
import ru.job4j.tourist.model.Track;

public class TrackListActivity extends AppCompatActivity {
    private CoordinatesDbHelper helper;
    private RecyclerView recycler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list);
        this.helper = new CoordinatesDbHelper(this);
        this.recycler = findViewById(R.id.track_recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }
    @Override
    protected void onStart() {
        super.onStart();
        loadItems();
    }
    private void loadItems() {
        recycler.setAdapter(new TrackAdapter(helper.getAllTracks()));
    }
    private static class TrackHolder extends RecyclerView.ViewHolder {
         public TrackHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    private class TrackAdapter extends RecyclerView.Adapter<TrackHolder> {
        private final List<Track> tracks;
        private TrackAdapter(List<Track> tracks) {
            this.tracks = tracks;
        }
        @NonNull
        @Override
        public TrackHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.track_module, parent, false);
            return new TrackHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull TrackHolder holder, int position) {
            Track track = tracks.get(position);
            TextView trackInfo = holder.itemView.findViewById(R.id.track_info_textView);
            View view = holder.itemView;
            view.setId(position);
            setTextTrackInfo(trackInfo, track);
            itemPainter(holder.itemView, position);
            view.setOnClickListener(v -> onItemClick(view.getId(), track.getTitle()));
        }
        private void setTextTrackInfo(TextView trackInfo, Track track) {
            String info = track.getTitle();
            if (!info.equals("")) {
                trackInfo.setText(info);
            } else {
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy");
                trackInfo.setText(sd.format(new Date(track.getDate())));
            }
        }
        private void itemPainter(View view, int numb) {
            if (numb % 2 == 0) {
                view.setBackgroundColor(Color.parseColor("#CCDDCC"));
            }
        }
        private void onItemClick(int id, String title) {
            Intent intent = new Intent(
                    TrackListActivity.this, ObjectManagerActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("track_id", id);
            intent.putExtra("track", true);
            startActivity(intent);
        }
        @Override
        public int getItemCount() {
            return tracks.size();
        }
    }
}
