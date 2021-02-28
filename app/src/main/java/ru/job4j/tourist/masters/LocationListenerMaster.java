package ru.job4j.tourist.masters;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import ru.job4j.tourist.interfaces.LocationListenerInterface;

public class LocationListenerMaster implements LocationListener {
    private LocationListenerInterface locationListenerInterface;
    @Override
    public void onLocationChanged(Location location) {
        locationListenerInterface.onLocationChanged(location);
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onProviderDisabled(String provider) {
    }
    public void setListenerInterface(LocationListenerInterface locListenerInterface) {
        this.locationListenerInterface = locListenerInterface;
    }
}
