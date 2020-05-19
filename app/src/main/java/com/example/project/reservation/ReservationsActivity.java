package com.example.project.reservation;


import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

//import com.example.project.DetailedReservationActivity;
import com.example.project.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ReservationsActivity extends ListActivity {

    private static final String MY_SHARED_PREF = "login_prefs";
    private static final String EMAIL_DATA_KEY = "remember_mail";
    private static final String TAG= "ReservationActivity";
    private static final String URL_DB= "http://pmscflaviahosting.altervista.org/booking_php.php";
    private List<Reservation> storico= new ArrayList<Reservation>();
    private String mailUtente;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservations);
/*
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int pos, long l) {
                //qui scrivo cosa fare al click
                Intent intent= new Intent(getApplicationContext(), DetailedReservationActivity.class);
                Bundle reservationBundle =new Bundle();
                Reservation selected= (Reservation)adapter.getItemAtPosition(pos);
                Log.i(TAG, "HAI CLICCATO SULLA PRENOTAZIONE DI: "+selected.getUser_id());
                reservationBundle.putParcelable("reservation", selected);
                intent.putExtra("reservation", reservationBundle);
                startActivity(intent);
            }
        });*/

        //codice per recuperare utente da preferenze
        SharedPreferences prefs = getSharedPreferences(MY_SHARED_PREF, Context.MODE_PRIVATE);
        mailUtente = prefs.getString(EMAIL_DATA_KEY, "No preferenze!");
        Log.i(TAG, "Utente salvato nelle preferenze:"+mailUtente.toString());

        updateReservationList();

    }

    private void updateReservationList() {
        DownloadReservationTask drt= new DownloadReservationTask();
        drt.execute(URL_DB);
    }

    //Async Task
    private class DownloadReservationTask extends AsyncTask<String, Void, List<Reservation>> {

        @Override
        protected List<Reservation> doInBackground(String... urls) {//ATTIVITA' CRITICA
            return loadReservationsFromDB(urls[0]);
        }
/*
        @Override
        protected void onPostExecute(List<Reservation> storico) {//QUANDO FINSCO ATTIVITA' CRITICA

            ArrayAdapter a= new ArrayAdapter(getApplicationContext(), R.layout.reservation_simple_row_layout, R.id.textViewList, storico);
            getListView().setAdapter(a);
        }*/
    }

    private List<Reservation> loadReservationsFromDB(String urlstring) {
        URL url;
        storico.clear();
        try {
            url = new URL(urlstring);
            //connessione
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            //concateno al POST l'user_id per filtrare la tabella
            String params = "user_id=" + URLEncoder.encode("flavia@gmail.com", "UTF-8");
            //utente da preferenze
            //String params = "user_id=" + URLEncoder.encode(mailUtente.toString(), "UTF-8");
            DataOutputStream dos = new DataOutputStream(urlConnection.getOutputStream());
            dos.writeBytes(params);
            dos.flush();
            dos.close();

            urlConnection.connect();
            InputStream is = urlConnection.getInputStream();

            //4c)leggo i contenuti della pagina
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();

            String result = sb.toString();//il risultato è una stringa Json

            JSONArray jArray = new JSONArray(result); //decodifico la stringa Json
            //per decodificare uso la classe jsonArray-> collezione di oggetti JsonObject
            //su cui faccio un ciclo, ad ogni oggetto chiedo di darmi l'elemento corrispondente alle colonne della tabella

            String outputString = "";
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);
                Log.i(TAG, "\n ID_BOOKING =  " + json_data.getString("id_booking") +
                        "\n DATA INIZIO = " + json_data.getString("time_start") +
                        "\n DATA FINE = " + json_data.getString("time_end") +
                        "\n INDIRIZZO DI PARTENZA = " + json_data.getString("address_start") +
                        "\n UTENTE = " + json_data.getString("user_id"));

                //4d) ottenuto l'array di classe Json posso creare un oggetto Reservation e inserirlo nell'array
                Reservation temp= new Reservation();
                temp.setId_booking(json_data.getString("id_booking"));
                temp.setTime_start(json_data.getString("time_start"));
                temp.setTime_end(json_data.getString("time_end"));
                temp.setAddress_start(json_data.getString("address_start"));
                temp.setBonus(json_data.getInt("bonus"));
                temp.setSuccessful(json_data.getInt("successful"));
                temp.setParking_id(json_data.getInt("parking_id"));
                temp.setUser_id(json_data.getString("user_id"));
                temp.setAmount((float)json_data.getDouble("amount")); //per ora con un cast, poi si vede

                storico.add(temp);
                Log.i(TAG, "Utente inserito nell'array: "+ temp.toString());
            }

        } catch (Exception e) {
            Log.e(TAG, "Error*** " + e.toString());
        }
        return storico;

    }


}