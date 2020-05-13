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


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (!user.getEmail().isEmpty()) {
            email.setText(user.getEmail());
        }


    }

    public void editProfile(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
}
