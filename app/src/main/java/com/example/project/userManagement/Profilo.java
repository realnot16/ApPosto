package com.example.project.userManagement;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class Profilo implements Parcelable {

    public static class ProfiloMetaData{
        public static String ID_USER = "id_user";
        public static String FIRSTNAME = "firstname";
        public static String LASTNAME = "lastname";
        public static String EMAIL = "email";
        public static String PHONE = "phone";
        public static String BIRTHDATE = "birthdate";
        public static String CITY = "city";
        public static String WALLET = "wallet";
        public static String TABLE_NAME = "User";
        public static String DEVICE_TOKEN = "device_token";
        public static String[] COLUMNS = new String[] { ID_USER, FIRSTNAME, LASTNAME, EMAIL, PHONE, BIRTHDATE, CITY, WALLET, DEVICE_TOKEN };
    }

    public static final Creator<Profilo> CREATOR = new Creator<Profilo>() {
        @Override
        public Profilo createFromParcel(Parcel in) {
            return new Profilo(in);
        }

        @Override
        public Profilo[] newArray(int size) {
            return new Profilo[size];
        }
    };

    private String id_user;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String birthdate;
    private String city;
    private float wallet;
    private String deviceToken;

    private Profilo(Parcel in) {
        id_user = in.readString();
        firstname = in.readString();
        lastname = in.readString();
        email = in.readString();
        phone = in.readString();
        birthdate = in.readString();
        //birthdate = new Date(in.readLong());
        city = in.readString();
        wallet = in.readFloat();
        deviceToken = in.readString();
    }

    public Profilo(){}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id_user);
        parcel.writeString(firstname);
        parcel.writeString(lastname);
        parcel.writeString(email);
        parcel.writeString(phone);
        parcel.writeString(birthdate);
        //parcel.writeLong(birthdate.getTime());
        parcel.writeString(city);
        parcel.writeFloat(wallet);
        parcel.writeString(deviceToken);
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthdate() {
        //SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
        //return f.format(birthdate);
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public float getWallet() {
        return wallet;
    }

    public void setWallet(float wallet) {
        this.wallet = wallet;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    @NonNull
    @Override
    public String toString() {
        return this.firstname;
    }
}
