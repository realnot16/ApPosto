package com.example.project.ParametersAsync;

public class OpenReservationParamsAsync {
    public String id_user;
    public Integer id_parking;
    public String address_start;
    public Integer bonus; //booleano

    public OpenReservationParamsAsync(String id_user, Integer id_parking, String address_start, Integer bonus) {
        this.id_user = id_user;
        this.id_parking = id_parking;
        this.address_start = address_start;
        this.bonus = bonus;
    }
}
