package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //Inizializzazione
        initUI();
    }

    //Metodo di inizializzazione
    private void initUI() {
        email = (EditText) findViewById(R.id.email_id);
        password = (EditText) findViewById(R.id.password_id);
    }

    //Metodo richiamato con il click su Login
    private void login(View view){
        Log.i("TAG", "Cliccato su login.");
        String mailUtente = email.getText().toString();
        String passwordUtente = password.getText().toString();


    }


}
