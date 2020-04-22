package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private EditText mail;
    private EditText password;
    private EditText confpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        //Inizializzazione
        initUI();
    }

    //Metodo di inizializzazione
    private void initUI() {
        mail = findViewById(R.id.email_id);
        password = findViewById(R.id.password_id);
        confpassword = findViewById(R.id.confpassword_id);
    }

    public void signup(View view){
        Log.i("TAG", "Hai cliccato su Registrati!");

        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void goToLogin(View view){
        Log.i("TAG", "Hai cliccato su Accedi. Verrai reindirizzato alla pagina di Login.");

        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
    }

}
