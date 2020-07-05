package com.example.project.userManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.ParametersAsync.ServerTask;
import com.example.project.R;
import com.example.project.walletManagement.PaymentActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.security.AuthProvider;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ShowProfile_TAG";
    private TextView name;
    private TextView birthdate;
    private TextView city;
    private TextView wallet;
    private TextView email;
    private TextView phone;
    private Button walletTopUp;

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
        walletTopUp= (Button) findViewById(R.id.button_ricarica);

        mAuth = FirebaseAuth.getInstance();


        //Mostra i dati dell'utente prendendoli dal db
        new LoadProfile().execute("https://smartparkingpolito.altervista.org/getProfile.php");

        //Collego il button per effettuare la ricarica all'activity paypal
        walletTopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), PaymentActivity.class);
                startActivity(intent);
            }
        });

    }


    //onClick su bottone Modifica - per modificare i campi
    public void editProfile(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("editProfile", profilo);
        intent.putExtra("editProfile", bundle);
        startActivity(intent);
    }


    //onClick su bottone Cambia password - per cambiare la password
    public void changePassword(View view) {

       //if(!GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD.equals(LoginActivity.credential.getSignInMethod())){
       //if(LoginActivity.credential.getSignInMethod().equals(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)) {
        ;
        //if(!GoogleAuthProvider.getCredential(GoogleSignIn.getLastSignedInAccount(this).getIdToken(), null).getSignInMethod().equals(GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD)){
           String emailAddress = mAuth.getCurrentUser().getEmail();

           if (!emailAddress.isEmpty()) {
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
           } else
               Toast.makeText(this, "Si è verificato un problema", Toast.LENGTH_SHORT).show();
       //}
    }


    public class LoadProfile extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                FirebaseUser user = mAuth.getCurrentUser();
                String params = "email=" + URLEncoder.encode(user.getEmail(), "UTF-8");
                JSONArray jArray = ServerTask.askToServer(params,strings[0]);

                for (int i = 0; i < jArray.length(); i++) {         //Ciclo di estrazione oggetti
                    JSONObject json_data = jArray.getJSONObject(i);
                    profilo = new Profilo();
                    profilo.setGoogleSignIn(json_data.getInt(Profilo.ProfiloMetaData.G_SIGNIN));
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
                if(profilo.getGoogleSignIn()==0) {
                    name.setText(profilo.getFirstname() + " " + profilo.getLastname());
                    city.setText(profilo.getCity());

                    try {
                        DateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = (Date) parser.parse(profilo.getBirthdate());
                        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        birthdate.setText(formatter.format(date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    email.setText(profilo.getEmail());
                    phone.setText(profilo.getPhone());
                    wallet.setText(String.valueOf(profilo.getWallet()));

                }else{  //Ha fatto l'accesso con Google per la prima volta: prima deve compilare i campi del profilo!
                    Intent i = new Intent(ProfileActivity.this, SignupActivity.class);
                    i.putExtra("isGSignIn", "GSignIn_yes");
                    i.putExtra("GSignin_wallet", ""+profilo.getWallet());
                    startActivity(i);
                    Toast.makeText(ProfileActivity.this, "Compila i campi per visualizzare il profilo!", Toast.LENGTH_LONG).show();
                }

            }
        }
    }



}
