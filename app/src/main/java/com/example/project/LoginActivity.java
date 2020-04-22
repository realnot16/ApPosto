package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
    public void login(View view){
        Log.i("TAG", "Hai cliccato su Login!");
        String mailUtente = email.getText().toString();
        String passwordUtente = password.getText().toString();

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);

    }

    public void goToSignup(View view){
        Log.i("TAG", "Hai cliccato su Registrati. Verrai reindirizzato alla pagina di registrazione.");

        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }


}
