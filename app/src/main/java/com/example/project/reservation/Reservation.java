package com.example.project.reservation;

import android.os.Parcel;
import android.os.Parcelable;

class Reservation implements Parcelable {

    private String id_booking;
    private String time_start;
    private String time_end;
    private String address_start;
    private int bonus;
    private int successful;
    private int parking_id;
    private String user_id;
    private float amount;

    protected Reservation(Parcel in) {
        id_booking = in.readString();
        time_start = in.readString();
        time_end = in.readString();
        address_start = in.readString();
        bonus = in.readInt();
        successful = in.readInt();
        parking_id = in.readInt();
        user_id = in.readString();
        amount = in.readFloat();
    }

    public static final Creator<Reservation> CREATOR = new Creator<Reservation>() {
        @Override
        public Reservation createFromParcel(Parcel in) {
            return new Reservation(in);
        }

        @Override
        public Reservation[] newArray(int size) {
            return new Reservation[size];
        }
    };

    @Override
    public String toString() {
        return "Prenotazione n°: " + id_booking + "\n"+
                "Data inizio: " + time_start + "\n"+
                "Partenza da: " + address_start + "\n" +
                "Importo pagato: " + amount ;
    }

    public void setId_booking(String id_booking) {
        this.id_booking = id_booking;
    }

    public void setTime_start(String time_start) {
        this.time_start = time_start;
    }

    public void setTime_end(String time_end) {
        this.time_end = time_end;
    }

    public void setAddress_start(String address_start) {
        this.address_start = address_start;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }

    public void setSuccessful(int successful) {
        this.successful = successful;
    }

    public void setParking_id(int parking_id) {
        this.parking_id = parking_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


    public Reservation(String id_booking, String time_start, String time_end, String address_start, int bonus, int successful, int parking_id, String user_id, float amount) {
        this.id_booking = id_booking;
        this.time_start = time_start;
        this.time_end = time_end;
        this.address_start = address_start;
        this.bonus = bonus;
        this.successful = successful;
        this.parking_id = parking_id;
        this.user_id = user_id;
        this.amount = amount;
    }

    public Reservation() {
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getId_booking() {
        return id_booking;
    }

    public String getTime_start() {
        return time_start;
    }

    public String getTime_end() {
        return time_end;
    }

    public String getAddress_start() {
        return address_start;
    }

    public int getBonus() {
        return bonus;
    }

    public int getSuccessful() {
        return successful;
    }

    public int getParking_id() {
        return parking_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public float getAmount() {
        return amount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id_booking);
        parcel.writeString(time_start);
        parcel.writeString(time_end);
        parcel.writeString(address_start);
        parcel.writeInt(bonus);
        parcel.writeInt(successful);
        parcel.writeInt(parking_id);
        parcel.writeString(user_id);
        parcel.writeFloat(amount);
    }

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
