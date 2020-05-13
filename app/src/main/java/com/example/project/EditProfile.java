package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditProfile extends AppCompatActivity {

    private EditText email;
    private EditText password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        email = (EditText) findViewById(R.id.show_mail_id);
        password = (EditText) findViewById(R.id.show_password_id);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (!user.getEmail().isEmpty()) {
            email.setText(user.getEmail());
            password.setText("*********");
        }

    }

    public void editProfile(View view) {

        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
}
