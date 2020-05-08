package com.example.project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG_LOG= "DBHelper";

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //cancello tabella esistente
       /* db.delete("MyReservation", null, null);
        Log.i("TAG_LOG", "Ho cancellato la tabella dal DB");*/


        Log.i("TAG_LOG", "Inizio Creazione DB");
        //creo la stringa per creare il DB
        String sql="";
        sql += "CREATE TABLE IF NOT EXISTS \"MyReservation\" ("; //tabella in locale che contiene lo storico delle prenotazioni dell'utente
        sql += "	    \"_id\" TEXT PRIMARY KEY ,"; //id prenotazione
        sql += "	    \"time_start\" TEXT NOT NULL,";//ora e data inizio
        sql += "	    \"time_finish\" TEXT NOT NULL,";
        sql += "	    \"amount\" REAL NOT NULL,";//costo totale
        sql += "	    \"start_address\" TEXT NOT NULL,";//indirizzo partenza
        sql += "	    \"finish_address\" TEXT NOT NULL,";
        sql += "	    \"id_parking\" TEXT NOT NULL,";//id dello stallo
        sql += "	    \"user_mail\" TEXT NOT NULL";//id utente
        sql += ")";
        //eseguo la stringa
        db.execSQL(sql);

        popolaTabellaReservation(db);



    }

    private void popolaTabellaReservation(SQLiteDatabase db) {
        //inserisco un'istanza
        String sql_insert="";
        sql_insert += "INSERT INTO MyReservation ( _id, time_start, time_finish, amount, start_address, finish_address," +
                " id_parking, user_mail) ";
        sql_insert += "VALUES ('reserv125452','28/04/2020 11:00','28/04/2020 11:05','2.00', " +
                "'Corso Duca Degli Abruzzi', 'Porta Nuova', 'p001', 'ilmioiduser@gmail.com')";
        db.execSQL(sql_insert);

        //inserisco la seconda istanza
        String sql_insert2="";
        sql_insert2 += "INSERT INTO MyReservation ( _id, time_start, time_finish, amount, start_address, finish_address," +
                " id_parking, user_mail) ";
        sql_insert2 += "VALUES ('reserv125453','01/05/2020 20:00','01/05/2020 20:50','2.50', " +
                "'Corso Duca Degli Abruzzi', 'via Monginevro', 'p011', 'flavia@gmail.com')";
        db.execSQL(sql_insert2);

        //inserisco la terza istanza
        String sql_insert3="";
        sql_insert3 += "INSERT INTO MyReservation ( _id, time_start, time_finish, amount, start_address, finish_address," +
                " id_parking, user_mail) ";
        sql_insert3 += "VALUES ('reserv125454','01/05/2020 21:00','01/05/2020 22:50','2.50', " +
                "'via Monginevro', 'via Dante di Nanni', 'p014', 'flavia@gmail.com')";
        db.execSQL(sql_insert3);

        //inserisco la quarta istanza
        String sql_insert4="";
        sql_insert4 += "INSERT INTO MyReservation ( _id, time_start, time_finish, amount, start_address, finish_address," +
                " id_parking, user_mail) ";
        sql_insert4 += "VALUES ('reserv125455','01/05/2020 21:20','01/05/2020 22:50','2.50', " +
                "'via Monginevro', 'via Dante di Nanni', 'p015', 'flavia@gmail.com')";
        db.execSQL(sql_insert4);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {
        // TODO Auto-generated method stub

    }
}