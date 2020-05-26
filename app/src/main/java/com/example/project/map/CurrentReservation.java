package com.example.project.map;

public class CurrentReservation {

    public String id_booking;
    public Integer parking_id;
    public Integer successful;
    public String address_start;
    public Integer  bonus;

    public CurrentReservation(){}
    public CurrentReservation(String address_start, Integer bonus, String id_booking, Integer parking_id, Integer successful) {
        this.address_start = address_start;
        this.bonus = bonus;
        this.id_booking = id_booking;
        this.parking_id = parking_id;
        this.successful = successful;

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
}
