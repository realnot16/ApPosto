package com.example.project.reservation;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.project.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Reservation implements Parcelable {

    public static class ReservationMetaData {
        //CAMPI DB ALTERVISTA tabella Booking
        public static String ID = "id_booking";
        public static String TIME_START = "time_start";
        public static String TIME_END = "time_end";
        public static String END_ADDRESS = "street";
        public static String BONUS = "bonus";
        public static String ID_PARKING = "parking_id";
        public static String ID_USER = "user_id";
        public static String AMOUNT = "amount";
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

    private String id_booking;
    private String time_start;
    private String time_end;
    private String address_end;
    private int bonus;
    private int parking_id;
    //private String user_id;
    private String amount;

    protected Reservation(Parcel in) {
        id_booking = in.readString();
        time_start = in.readString();
        time_end = in.readString();
        address_end=in.readString();
        bonus = in.readInt();
        parking_id = in.readInt();
        //user_id = in.readString();
        amount = in.readString();
    }



    @Override
    public int describeContents() {
        return 0;
    }

    public String getAddress_end() {
        return address_end;
    }

    public void setAddress_end(String address_end) {
        this.address_end = address_end;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id_booking);
        parcel.writeString(time_start);
        parcel.writeString(time_end);
        parcel.writeString(address_end);
        parcel.writeInt(bonus);
        parcel.writeInt(parking_id);
        //parcel.writeString(user_id);
        parcel.writeString(amount);
    }

    public void setId_booking(String id_booking) {
        this.id_booking = id_booking;
    }

    public void setTime_start(String time_start) {this.time_start= time_start; }

    public void setTime_end(String time_end) {
        this.time_end = time_end;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }


    public void setParking_id(int parking_id) {
        this.parking_id = parking_id;
    }

    /*public void setUser_id(String user_id) {
        this.user_id = user_id;
    }*/

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Reservation() {
    }

    private String getDataFormattata(String time_start) throws ParseException {
        Date temp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time_start);
        String result=new SimpleDateFormat("dd/MM/yyyy").format(temp);
        return result;
    }

    public String getId_booking() {
        return id_booking;
    }

    public String getTime_start() throws ParseException {
        Date temp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time_start);
        String result=new SimpleDateFormat("dd/MM/yyyy HH:mm").format(temp);
        return result;
    }

    public String getTime_end() throws ParseException {
        Date temp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time_end);
        String result=new SimpleDateFormat("dd/MM/yyyy HH:mm").format(temp);
        return result;
    }


    public int getBonus() {
        return bonus;
    }


    public int getParking_id() {
        return parking_id;
    }

/*    public String getUser_id() {
        return user_id;
    }*/

    public String getAmount() {
        return amount;
    }



}
