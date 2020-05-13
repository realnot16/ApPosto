package com.example.project.map;

//STATION, da implementare
//classe contente i dettagli di tutte le stazioni
//mancano alcuni dettagli, da recuperare dal db

public class Station {
    String latitude;
    String longitude;
    String city;
    String name;

    public Station() {
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
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