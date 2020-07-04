package com.example.project.map;

import android.icu.util.ValueIterator;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CurrentReservation {

    public String id_booking;
    public Integer parking_id;
    public Integer successful;
    public String address_start;
    public Integer  bonus;
    public Float  latitude;
    public Float longitude;


    public CurrentReservation(){}

    public CurrentReservation(String id_booking, Integer parking_id, Integer successful, String address_start, Integer bonus, Float latitude, Float longitude) {
        this.id_booking = id_booking;
        this.parking_id = parking_id;
        this.successful = successful;
        this.address_start = address_start;
        this.bonus = bonus;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public String getId_booking() {
        return id_booking;
    }

    public void setId_booking(String id_booking) {
        this.id_booking = id_booking;
    }

    public Integer getParking_id() {
        return parking_id;
    }

    public void setParking_id(Integer parking_id) {
        this.parking_id = parking_id;
    }

    public Integer getSuccessful() {
        return successful;
    }

    public void setSuccessful(Integer successful) {
        this.successful = successful;
    }

    public String getAddress_start() {
        return address_start;
    }

    public void setAddress_start(String address_start) {
        this.address_start = address_start;
    }

    public Integer getBonus() {
        return bonus;
    }

    public void setBonus(Integer bonus) {
        this.bonus = bonus;
    }

    public void reset(){

        this.id_booking = null;
        this.parking_id = null;
        this.successful = null;
        this.address_start = null;
        this.bonus = null;
        this.latitude = null;
        this.longitude = null;

    }

}
