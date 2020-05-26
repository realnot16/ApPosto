package com.example.project.ParametersAsync;

public class CloseReservationParamsAsync {
    public String id_user;
    public Integer id_parking;
    public String booking_id;
    public Integer successfull;

        public CloseReservationParamsAsync(String id_user, Integer id_parking, String booking_id, Integer successfull) {
            this.id_user = id_user;
            this.id_parking = id_parking;
            this.booking_id = booking_id;
            this.successfull = successfull;
        }
    }
