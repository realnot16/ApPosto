package com.example.project.map;

//STATION, da implementare
//classe contente i dettagli di tutte le stazioni
//mancano alcuni dettagli, da recuperare dal db

public class Station {

    double latitude;
    double longitude;
    String city;
    String street;
    Integer id_parking;
    double cost_minute;

    public Station(double latitude, double longitude, String city, String street, Integer id_parking, double cost_minute) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.street = street;
        this.id_parking = id_parking;
        this.cost_minute = cost_minute;
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

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Integer getId_parking() {
        return id_parking;
    }

    public void setId_parking(Integer id_parking) {
        this.id_parking = id_parking;
    }

    public double getCost_minute() {
        return cost_minute;
    }

    public void setCost_minute(double cost_minute) {
        this.cost_minute = cost_minute;
    }
}