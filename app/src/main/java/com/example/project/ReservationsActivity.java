package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

public class ReservationsActivity extends ListActivity {

    private final static int DB_VERSION = 1;
    /*FROMS e TOS sono gli array che mi servono a matchare
    i nomi delle colonne delle tabelle con gli elementi della
    riga della listView*/
//posso utilizzare anche TeamMetaData.COLUMN
private String[] FROMS = new String[] {"id_reservation", "time_start", "amount", "finish_address" };
//Chi meglio della tabella sa dirmi come si chiamano le cue colonne?
//Mi faccio passare i dati dalla funzione TeamMetaData nella classe Team
private int[] TOS = new int[] { R.id.reservation_id_label, R.id.date_id_label,
        R.id.amount_id_label, R.id.address_id_label };
    private SQLiteDatabase db;
    private Cursor cursor;
    private CursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservations);


        db = dbHelper.getWritableDatabase();

        /*Per prima cosa, con una select recupero il database da
         * inserire nella lista*/
        String sql = "SELECT id_reservation, time_start, amount, finish_address FROM MyReservation";
        cursor = db.rawQuery(sql, null); //ottengo un cursor

        /*Per mettere il contenuto della tabella in una ListView
		 Uso un SimpleCursorAdapter che riceve this, il layout di riga,
		 il cursore, il tag SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
		 che ha la funzione di informare l'adapter quando cambia il contenuto*/
        adapter = new SimpleCursorAdapter(this, R.layout.reservation_row_layout, cursor, FROMS, TOS, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        getListView().setAdapter(adapter);









    }

    /*codice standard a cui passo this,
    nome del DB, null e costante per aggiornamenti. Nasce con due metodi: onCreate e onUpdate
    che vengono chiamati quando devo creare o aggiornare il db
    in questo caso usando il codice sql*/
    private final SQLiteOpenHelper dbHelper = new SQLiteOpenHelper(this,
            "RESERVATION_DB", null, DB_VERSION) {

        @Override
        public void onCreate(SQLiteDatabase db) {
           // Log.i(TAG_LOG, "Inizio Creazione DB");
            //creo la stringa per creare il DB
            String sql="";
            sql += "CREATE TABLE \"MyReservation\" ("; //tabella in locale che contiene lo storico delle prenotazioni dell'utente
            sql += "	    \"id_reservation\" TEXT PRIMARY KEY,"; //id prenotazione
            sql += "	    \"time_start\" TEXT NOT NULL,";//ora e data inizio
            sql += "	    \"time_finish\" TEXT NOT NULL,";
            sql += "	    \"amount\" REAL NOT NULL,";//costo totale
            sql += "	    \"start_address\" TEXT NOT NULL";//indirizzo partenza
            sql += "	    \"finish_address\" TEXT NOT NULL";
            sql += "	    \"id_parking\" TEXT NOT NULL";//id dello stallo
            sql += "	    \"id_user\" TEXT NOT NULL";//id utente
            sql += ")";


            //eseguo la stringa
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
           // Log.i(TAG_LOG, "Aggiornamento non implementato");
        }

    };

    }
