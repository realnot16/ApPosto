package com.example.project.reservation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.project.R;

import java.text.ParseException;

public class DetailedReservationActivity extends AppCompatActivity {

    private static final String TAG = "DetailedReservationActivity";
    private Reservation reservation;
    private TextView tvIdReservation;
    private TextView tvDateStartReservation;
    private TextView tvDateFinishReservation;
    private TextView tvStartAddressReservation;
    private TextView tvAmountReservation;
    private TextView tvBonusReservation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_reservation);
        getWindow().setBackgroundDrawableResource(R.drawable.main_background_not_coloured);

        initUI();

        try {
            init();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.detailed_reservation_tv);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setHomeButtonEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            Log.i(TAG, "Back, redirect a prenotazioni");
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void init() throws ParseException {

        tvIdReservation= (TextView) findViewById(R.id.reservation_id_label);
        tvDateStartReservation= (TextView) findViewById(R.id.date_id_label);
        tvDateFinishReservation= (TextView) findViewById(R.id.date_id_label2);
        tvStartAddressReservation= (TextView) findViewById(R.id.address_id_label);
        tvAmountReservation= (TextView) findViewById(R.id.amount_id_label);
        tvBonusReservation= (TextView) findViewById(R.id.tv_reservation_omaggio);

        Bundle resBundle = getIntent().getBundleExtra("reservation");

        //se ci sono
        if (resBundle != null) {//li estrae e li mette all'interno dei quattro campi
            reservation = (Reservation) resBundle.getParcelable("reservation");
            tvIdReservation.setText(reservation.getId_booking());
            tvDateStartReservation.setText(reservation.getTime_start());
            tvDateFinishReservation.setText(reservation.getTime_end());
            tvStartAddressReservation.setText(reservation.getAddress_end());
            tvAmountReservation.setText(""+reservation.getAmount()+"€");
            if(reservation.getBonus()==1){
                tvBonusReservation.setVisibility(View.VISIBLE);
                tvBonusReservation.setText(R.string.reservation_omaggio);//mostra questo messaggio solo se bonus==1
            }

        } else { //altrimenti
            Log.i(TAG, "Non hai selezionato nessun utente");
        }

    }
}
