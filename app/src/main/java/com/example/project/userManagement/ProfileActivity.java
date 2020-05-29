package com.example.project.userManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

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

        setData();

    }

    private void setData() {
        Bundle profileBundle = getIntent().getBundleExtra("User");
        Bundle editBundle = getIntent().getBundleExtra("editedUser");

        //Controllo pancia dell'intent
        if (profileBundle != null || editBundle != null) {
            if(editBundle!=null) {
                profilo = editBundle.getParcelable("editedUser");
                profileBundle = null;
            }else {
                profilo = profileBundle.getParcelable("User");
            }
                name.setText(profilo.getFirstname()+" "+profilo.getLastname());
                city.setText(profilo.getCity());
                birthdate.setText(profilo.getBirthdate());
                email.setText(profilo.getEmail());
                phone.setText(profilo.getPhone());
                wallet.setText(String.valueOf(profilo.getWallet()));
        } else {
            //accesso non consentito direttamente su showprofile
        }
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

    /*
   //OnActivityResult
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode, resultCode, data);

       // check that it is the SecondActivity with an OK result
       if (requestCode == PROFILE_REQUEST_CODE) {
           if (resultCode == RESULT_OK) {

               Log.i(TAG, "OnActivityResult");
               Bundle editedUser = getIntent().getBundleExtra("editedUser");
               profilo = (Profilo) editedUser.getParcelable("editedUser");
               name.setText(profilo.getFirstname()+" "+profilo.getLastname());
               city.setText(profilo.getCity());
               birthdate.setText(profilo.getBirthdate());
               email.setText(profilo.getEmail());
               phone.setText(profilo.getPhone());
               wallet.setText(String.valueOf(profilo.getWallet()));


           }
       }
   }

     */


}
