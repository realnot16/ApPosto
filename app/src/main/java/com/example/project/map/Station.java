package com.example.project.map;

//STATION, da implementare
//classe contente i dettagli di tutte le stazioni
//mancano alcuni dettagli, da recuperare dal db

public class Station {
    double latitude;
    double longitude;
    String city;
    String name;

    public Station(double lat,double lng) {
        latitude = lat;
        longitude= lng;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}