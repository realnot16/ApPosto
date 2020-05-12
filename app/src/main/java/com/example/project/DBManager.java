package com.example.project;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

class DBManager {


    private String[] FROMS = new String[] {"_id", "time_start", "amount", "finish_address" };
    private int[] TOS = new int[] { R.id.reservation_id_label, R.id.date_id_label,
            R.id.amount_id_label, R.id.address_id_label };
    private SQLiteDatabase db;
    private Cursor cursor;
    private CursorAdapter adapter;
    private Context context;
    private DBHelper dbHelper;
    private final static String DB_NAME= "RESERVATION_DB";
    private final static int DB_VERSION = 1;

    public DBManager(Context ctx) {
        this.context=ctx;
        dbHelper= new DBHelper(ctx, DB_NAME, null, DB_VERSION);
        db = dbHelper.getWritableDatabase();
    }

    public void updateListView(ListView lv, String mailUtente) {

      /*  //recupero mail utente dalle preferenze
        SharedPreferences prefs = getSharedPreferences(MY_SHARED_PREF,Context.MODE_PRIVATE);*/

        String sql = "SELECT _id, time_start, amount, finish_address FROM MyReservation "+
                "WHERE user_mail ='"+mailUtente+"'";
        cursor = db.rawQuery(sql, null);
        adapter = new SimpleCursorAdapter( context, R.layout.reservation_row_layout, cursor, FROMS, TOS, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        lv.setAdapter(adapter);
    }



}
