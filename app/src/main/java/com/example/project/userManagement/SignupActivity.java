package com.example.project.userManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.ParametersAsync.ServerTask;
import com.example.project.R;
import com.example.project.map.MapsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private static int CONFIRM_CODE;
    private EditText mail;
    private EditText password;
    private EditText confpassword;
    private EditText firstname;
    private EditText lastname;
    private EditText birthdate;
    private EditText phone;
    private EditText city;
    private TextView accedi;
    private TextView testoAccedi;
    private Button bottone;
    private float tempWallet;
    DatePickerDialog picker;

    private FirebaseAuth mAuth;


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_management_signup_layout);
        getWindow().setBackgroundDrawableResource(R.drawable.main_background_light);

        //Inizializzazione
        initUI();

    }

    //Metodo di inizializzazione
    private void initUI() {
        mail = findViewById(R.id.signUp_emailField_id);
        password = findViewById(R.id.signUp_passwordField_id);
        confpassword = findViewById(R.id.signUp_confpwdField_id);
        firstname = findViewById(R.id.signUp_nameField_id);
        lastname = findViewById(R.id.signUp_surnameField_id);
        phone = findViewById(R.id.signUp_phoneField_id);
        birthdate = findViewById(R.id.signUp_dateField_id);
        city = findViewById(R.id.signUp_cityField_id);
        accedi = findViewById(R.id.signUp_accedi_id);
        testoAccedi = findViewById(R.id.signUp_yesAccount_id);
        bottone = findViewById(R.id.signUp_confirm_button);

        mAuth = FirebaseAuth.getInstance();

        Bundle profileBundle = getIntent().getBundleExtra("editProfile");
        if(profileBundle!=null){
            CONFIRM_CODE = 1;
            Profilo profilo = profileBundle.getParcelable("editProfile");
            firstname.setText(profilo.getFirstname());
            lastname.setText(profilo.getLastname());
            birthdate.setText(profilo.getBirthdate());
            phone.setText(profilo.getPhone());
            city.setText(profilo.getCity());
            mail.setText(profilo.getEmail());
            mail.setEnabled(false);
            password.setVisibility(View.GONE);
            confpassword.setVisibility(View.GONE);
            accedi.setVisibility(View.GONE);
            testoAccedi.setVisibility(View.GONE);
            bottone.setText(R.string.signUp_conferma_button_description_text);
            tempWallet = profilo.getWallet();
        }else
            CONFIRM_CODE=0;
    }

    private void updateUI(FirebaseUser currentUser) {
        Intent intent = new Intent(SignupActivity.this, MapsActivity.class);
        startActivity(intent);
    }

    //It takes in an email address and password, validates them and then creates a new user.
    public void signup(View view){
        Log.i(TAG, "Hai cliccato su Conferma!");


        if(CONFIRM_CODE==0) { //Conferma=Registrati

            String mailUser = mail.getText().toString().trim();
            String passwordUser = password.getText().toString().trim();
            String confirmPwdUser = confpassword.getText().toString().trim();

            if (validateUser(mailUser, passwordUser, confirmPwdUser)) {

                mAuth.createUserWithEmailAndPassword(mailUser, passwordUser)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    createUser();
                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignupActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }
                                // ...
                            }
                        });
            }

        }else{ //Conferma= Aggiorna dati


            CONFIRM_CODE = 0;
            Intent editedProfile = new Intent(this, ProfileActivity.class);
            Bundle profBundle = new Bundle();
            Profilo p = new Profilo();
            p.setId_user(mAuth.getCurrentUser().getUid());
            p.setEmail(mail.getText().toString().trim());
            p.setFirstname(firstname.getText().toString().trim());
            p.setLastname(lastname.getText().toString().trim());
            p.setPhone(phone.getText().toString().trim());
            //p.setBirthdate(Date.valueOf(birthdate.getText().toString()));
            p.setBirthdate(birthdate.getText().toString().trim());
            p.setCity(city.getText().toString().trim());
            p.setWallet(tempWallet);
            profBundle.putParcelable("editedUser", p);
            editedProfile.putExtra("editedUser", profBundle);

            //Aggiornamento su DB
            new UpdateUser().execute(p);

            //Mando indietro i dati modificati
            //setResult(Activity.RESULT_OK, editedProfile);
            //finish();
            startActivity(editedProfile);

        }
    }

    private void createUser() {
        final Profilo nuovoProfilo = new Profilo();
        nuovoProfilo.setEmail(mAuth.getCurrentUser().getEmail());
        nuovoProfilo.setId_user(mAuth.getCurrentUser().getUid());
        nuovoProfilo.setFirstname(firstname.getText().toString().trim());
        nuovoProfilo.setLastname(lastname.getText().toString().trim());
        nuovoProfilo.setBirthdate(birthdate.getText().toString().trim());
        nuovoProfilo.setCity(city.getText().toString().trim());
        nuovoProfilo.setPhone(phone.getText().toString().trim());
        nuovoProfilo.setWallet(0);
        new UploadUser().execute(nuovoProfilo);
    }


    //Validazione sintattica e semantica degli EditText
    private boolean validateUser(String mailUser, String passwordUser, String confirmPwdUser) {
            boolean valid = true;
        Pattern emailPattern = Pattern
                .compile("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher emailMatcher = emailPattern.matcher(mailUser);

        Pattern passwordPattern = Pattern
                .compile("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}");
        Matcher passMatcher = passwordPattern.matcher(passwordUser);

        if (TextUtils.isEmpty(firstname.getText().toString())) {
            firstname.setError("Campo obbligatorio!");
            valid = false;
        }
        if(TextUtils.isEmpty(lastname.getText().toString())) {
            lastname.setError("Campo obbligatorio!");
            valid = false;
        }
        if (TextUtils.isEmpty(birthdate.getText().toString())){
            birthdate.setError("Campo obbligatorio!");
            valid = false;
        }
        if (TextUtils.isEmpty(phone.getText().toString())){
            phone.setError("Campo obbligatorio!");
            valid = false;
        }
        if (TextUtils.isEmpty(city.getText().toString())){
            city.setError("Campo obbligatorio!");
            valid = false;
        }
        if (TextUtils.isEmpty(mailUser)) {
                mail.setError("Campo obbligatorio!");
                valid = false;
        }
        if(TextUtils.isEmpty(passwordUser)) {
                password.setError("Campo obbligatorio!");
                valid = false;
        }
        if (TextUtils.isEmpty(confirmPwdUser)){
                confpassword.setError("Campo obbligatorio!");
                valid = false;
        }

        //Se i campi obbligatori sono stati compilati:
        if(valid==true) {
            if (!emailMatcher.matches()) {
                valid = false;
                mail.setError("Ricontrolla la mail inserita");
            }
            if (!passMatcher.matches()) {
                valid = false;
                Toast.makeText(this, "La password deve contenere almeno 1 maiuscola, 1 minuscola e un numero, e deve essere di almeno 8 caratteri.", Toast.LENGTH_LONG).show();
                password.getText().clear();
                confpassword.getText().clear();
            }
            if (passwordUser.compareTo(confirmPwdUser) != 0) {
                confpassword.setError("Le password non coincidono!");
                valid = false;
            }
        }

            return valid;
    }

    public void goToLogin(View view){
        Log.i(TAG, "Hai cliccato su Accedi. Verrai reindirizzato alla pagina di Login.");

        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void showDatePickerDialog(View v) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        picker = new DatePickerDialog(SignupActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String birthdateString = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        birthdate.setText(birthdateString);
                    }
                }, year, month, day);
        picker.show();
    }


    //TASK PER CARICARE NUOVO UTENTE SUL DB
    private class UploadUser extends AsyncTask<Profilo, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Profilo... profili) {

            String url = "https://smartparkingpolito.altervista.org/CreateProfile.php";
            String params = null;
            Profilo p = profili[0];

            //Encoding parametri:

            try {
                params = "email=" + URLEncoder.encode(p.getEmail(), "UTF-8")
                        + "&firstname=" + URLEncoder.encode(p.getFirstname(), "UTF-8")
                        + "&lastname=" + URLEncoder.encode(p.getLastname(), "UTF-8")
                        + "&city=" + URLEncoder.encode(p.getCity(), "UTF-8")
                        + "&phone=" + URLEncoder.encode(p.getPhone(), "UTF-8")
                        + "&birthdate=" + URLEncoder.encode(p.getBirthdate(), "UTF-8")
                        + "&id_user=" + URLEncoder.encode(p.getId_user(), "UTF-8");

                JSONArray jsonArray=ServerTask.askToServer(params,url);
                //gestisci JsonArray
                JSONObject jsonObjectControl=jsonArray.getJSONObject(0);
                String control=jsonObjectControl.getString("control");
                Log.i("cntr0",control);
                if (control.equals("OK")){   // non esegue l'if
                    //UTENTE REGISTRATO CORRETTAMENTE
                    Log.i(TAG, "Utente registrato correttamente.");
                    return true;
                }

            }
            catch (UnsupportedEncodingException | JSONException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

    }

    //TASK PER AGGIORNARE UTENTE SU DB
    private class UpdateUser extends AsyncTask<Profilo, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Profilo... profili) {
            String url = "https://smartparkingpolito.altervista.org/UpdateProfile.php";
            String params = null;
            Profilo p = profili[0];

            //Encoding parametri:

            try {
                params = "firstname=" + URLEncoder.encode(p.getFirstname(), "UTF-8")
                        + "&lastname=" + URLEncoder.encode(p.getLastname(), "UTF-8")
                        + "&city=" + URLEncoder.encode(p.getCity(), "UTF-8")
                        + "&phone=" + URLEncoder.encode(p.getPhone(), "UTF-8")
                        + "&birthdate=" + URLEncoder.encode(p.getBirthdate(), "UTF-8")
                        + "&id_user=" + URLEncoder.encode(p.getId_user(), "UTF-8")
                        + "&wallet=" + URLEncoder.encode(String.valueOf(p.getWallet()), "UTF-8");

                JSONArray jsonArray=ServerTask.askToServer(params,url);
                //gestisci JsonArray
                JSONObject jsonObjectControl=jsonArray.getJSONObject(0);
                String control=jsonObjectControl.getString("control");
                Log.i("cntr0",control);
                if (control.equals("OK")){   // non esegue l'if
                    //UTENTE AGGIORNATO CORRETTAMENTE
                    Log.i(TAG, "Utente aggiornato correttamente.");
                    return true;
                }

            }
            catch (UnsupportedEncodingException | JSONException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }


}
