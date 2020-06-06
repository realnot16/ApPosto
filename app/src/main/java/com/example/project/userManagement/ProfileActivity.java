package com.example.project.userManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.ParametersAsync.ServerTask;
import com.example.project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ShowProfile_TAG";
    private static final int PROFILE_REQUEST_CODE = 1;
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
        setContentView(R.layout.user_management_profile);
        getWindow().setBackgroundDrawableResource(R.drawable.main_background_light);

        name = (TextView) findViewById(R.id.label_name_id);
        birthdate = (TextView) findViewById(R.id.label_birthdate);
        city = (TextView) findViewById(R.id.label_city);
        wallet = (TextView) findViewById(R.id.label_wallet);
        email = (TextView) findViewById(R.id.label_mail);
        phone = (TextView) findViewById(R.id.label_phone);

        mAuth = FirebaseAuth.getInstance();

        //Mostro i dati dell'utente prendendoli dal db
        new LoadProfile().execute("https://smartparkingpolito.altervista.org/getProfile.php");

    }


    //onClick su bottone Modifica - per modificare i campi
    public void editProfile(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("editProfile", profilo);
        intent.putExtra("editProfile", bundle);
        startActivityForResult(intent, PROFILE_REQUEST_CODE);
    }

    //onClick su bottone Ricarica - per ricarica wallet
    public void walletTopUp(View view) {
    }

    //onClick su bottone Cambia password - per cambiare la password
    public void changePassword(View view) {
        String emailAddress = mAuth.getCurrentUser().getEmail();

        if(!emailAddress.isEmpty()) {
            mAuth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.i(TAG, "Email sent.");
                                Toast.makeText(ProfileActivity.this, "Una mail è stata inviata al tuo indirizzo di posta.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else
            Toast.makeText(this, "Si è verificato un problema", Toast.LENGTH_SHORT).show();
    }


    private class LoadProfile extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                FirebaseUser user = mAuth.getCurrentUser();
                String params = "email=" + URLEncoder.encode(user.getEmail(), "UTF-8");
                JSONArray jArray = ServerTask.askToServer(params,strings[0]);

                for (int i = 0; i < jArray.length(); i++) {         //Ciclo di estrazione oggetti
                    JSONObject json_data = jArray.getJSONObject(i);
                    profilo = new Profilo();
                    profilo.setEmail(user.getEmail());
                    profilo.setId_user(user.getUid());
                    profilo.setFirstname(json_data.getString(Profilo.ProfiloMetaData.FIRSTNAME));
                    profilo.setLastname(json_data.getString(Profilo.ProfiloMetaData.LASTNAME));
                    profilo.setBirthdate(json_data.getString(Profilo.ProfiloMetaData.BIRTHDATE));
                    profilo.setCity(json_data.getString(Profilo.ProfiloMetaData.CITY));
                    profilo.setPhone(json_data.getString(Profilo.ProfiloMetaData.PHONE));
                    profilo.setWallet((float) json_data.getDouble(Profilo.ProfiloMetaData.WALLET));
                    profilo.setDeviceToken(json_data.getString(Profilo.ProfiloMetaData.DEVICE_TOKEN));
                }

            } catch (Exception e) {
                Log.e("log_tag", "Error " + e.toString());
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean==true) {
                name.setText(profilo.getFirstname() + " " + profilo.getLastname());
                city.setText(profilo.getCity());
                birthdate.setText(profilo.getBirthdate());
                email.setText(profilo.getEmail());
                phone.setText(profilo.getPhone());
                wallet.setText(String.valueOf(profilo.getWallet()));
            }
        }
    }



}
