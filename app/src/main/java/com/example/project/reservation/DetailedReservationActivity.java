package com.example.project.reservation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
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

        try {
            init();
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
            tvStartAddressReservation.setText(reservation.getAddress_start());
            tvAmountReservation.setText(""+reservation.getAmount()+"€");
            if(reservation.getBonus()==1){
                tvBonusReservation.setText("Questa è una prenotazione bonus!");//mostra questo messaggio solo se bonus==1
            }

        } else { //altrimenti
            Log.i(TAG, "Non hai selezionato nessun utente");
        }

    }
}
