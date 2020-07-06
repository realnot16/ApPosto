package com.example.project.favourites;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class Favourite implements Serializable {
    private static final long serialVersionUID = 1234L;
    private String label;
    private double lat;
    private double lon;

    public Favourite(String label, double lat, double lon) {
        this.label = label;
        this.lat = lat;
        this.lon = lon;
    }

    public String getLabel() {
        return label;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
    @Override
    public String toString() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
