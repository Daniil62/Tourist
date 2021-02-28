package ru.job4j.tourist.model;

import com.google.android.gms.maps.model.LatLng;

public class Place {
    private LatLng coordinates;
    private String title;
    public Place (LatLng coordinates, String title) {
        this.coordinates = coordinates;
        this.title = title;
    }
    public Place() {}
    public LatLng getCoordinates() {
        return coordinates;
    }
    public void setCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
}
