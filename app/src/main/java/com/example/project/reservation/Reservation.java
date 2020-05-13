package com.example.project.reservation;

class Reservation {

    //Dati sui campi della tabella prenotazioni SQLite locale
    public static class ReservationMetaData {
        public static String ID = "_id";
        public static String TIME_START = "time_start";
        public static String TIME_FINISH = "time_finish";
        public static String AMOUNT = "amount";
        public static String START_ADDRESS = "start_address";
        public static String FINISH_ADDRESS = "finish_address";
        public static String ID_PARKING = "id_parking";
        public static String USER_MAIL = "user_mail";
        public static String[] COLUMNS = new String[] { ID, TIME_START,TIME_FINISH, AMOUNT,
        START_ADDRESS, FINISH_ADDRESS, ID_PARKING, USER_MAIL};
    }
}
