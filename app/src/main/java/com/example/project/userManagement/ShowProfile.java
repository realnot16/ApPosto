package com.example.project.userManagement;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ShowProfile extends AppCompatActivity {

    private TextView name;
    private TextView birthdate;
    private TextView city;
    private TextView wallet;
    private TextView email;
    private TextView phone;

    FirebaseAuth mAuth;
    Profilo profilo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        name = (TextView) findViewById(R.id.label_name_id);
        birthdate = (TextView) findViewById(R.id.label_birthdate);
        city = (TextView) findViewById(R.id.label_city);
        wallet = (TextView) findViewById(R.id.label_wallet);
        email = (TextView) findViewById(R.id.label_mail);
        phone = (TextView) findViewById(R.id.label_phone);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        setData();

    }

    private void setData() {
        Bundle profileBundle = getIntent().getBundleExtra("User");

        //Controllo pancia dell'intent
        if (profileBundle != null) {
            //1)Ho dei dati: li visualizzo per modificarli
            profilo = profileBundle.getParcelable("User");
            name.setText(profilo.getFirstname()+" "+profilo.getLastname());
            city.setText(profilo.getCity());
            birthdate.setText(profilo.getBirthdate().toString());
            email.setText(profilo.getEmail());
            phone.setText(profilo.getPhone());
            wallet.setText(String.valueOf(profilo.getWallet()));
        } else {

        }
    }


    //onClick su bottone Modifica - per modificare i campi
    public void editProfile(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    //onClick su bottone Ricarica - per ricarica wallet
    public void walletTopUp(View view) {
    }

    //onClick su bottone Cambia password - per cambiare la password
    public void changePassword(View view) {

    }
}
