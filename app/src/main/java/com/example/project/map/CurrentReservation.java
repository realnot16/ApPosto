package com.example.project.map;

public class CurrentReservation {

    public String id_booking;
    public Integer parking_id;
    public Integer successful;
    public String user_id;
    public String address_start;
    public Integer  bonus;

    public CurrentReservation(){}
    public CurrentReservation(String address_start, Integer bonus, String id_booking, Integer parking_id, Integer successful, String user_id) {
        this.address_start = address_start;
        this.bonus = bonus;
        this.id_booking = id_booking;
        this.parking_id = parking_id;
        this.successful = successful;
        this.user_id = user_id;
    }
}
