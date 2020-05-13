package com.example.project.reservation;


import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.project.R;

public class ReservationsActivity extends ListActivity {

    private DBManager dbm;
    private static final String MY_SHARED_PREF = "login_prefs";
    private static final String EMAIL_DATA_KEY = "remember_mail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservations);

        //Creo un oggetto DBManager-> prende il riferimento al db o lo crea
        //devo passare il Context ed il riferimento alla listview
        dbm= new DBManager(getApplicationContext());

        SharedPreferences prefs = getSharedPreferences(MY_SHARED_PREF,Context.MODE_PRIVATE);
        String mailUtente = prefs.getString(EMAIL_DATA_KEY, "No preferenze!");

        //chiamo il metodo che aggiorna la ListView, a cui devo passare la mail dell'utente
        dbm.updateListView(getListView(), mailUtente);
    }
}
