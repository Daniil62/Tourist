package ru.job4j.tourist.model;

import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;

public class Track {
    private List<LatLng> way = new ArrayList<>();
    private final long date;
    private String title;
    public Track(List<LatLng> way, long date, String title) {
        this.way = way;
        this.date = date;
        this.title = title;
    }
    public List<LatLng> getWay() {
        return way;
    }
    public void setWay(List<LatLng> way) {
        this.way = way;
    }
    public long getDate() {
        return date;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
}
