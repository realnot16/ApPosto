package com.example.project.userManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.project.ParametersAsync.ServerTask;
import com.example.project.R;
import com.example.project.map.MapsActivity;
import com.example.project.walletManagement.PaymentActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.AlgorithmParameterGenerator;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 1;
    private static final String MY_SHARED_PREF = "login_prefs";
    private static final String CBOX_DATA_KEY = "remember_checkbox";
    private static final String EMAIL_DATA_KEY = "remember_mail";
    private static final String PASSWORD_DATA_KEY = "remember_password";
    private EditText email;
    private EditText password;
    private CheckBox ricordami;

    private FirebaseAuth mAuth;

    //Google sign-in
    GoogleSignInClient googleSignInClient;
    SignInButton button;
    private String token;

    //When initializing your Activity, check to see if the user is currently signed in.
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //mAuth.signOut();
        updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_management_login_layout);
        getWindow().setBackgroundDrawableResource(R.drawable.main_background_coloured);
        ImageView img = findViewById(R.id.login_logo_id);

        //RIMUOVO TESTO DAL GOOGLE BUTTON
        //TextView textView = (TextView) button.getChildAt(0);
        //textView.setText("Acc");

        //Inizializzazione
        initUI();
    }

    //Metodo di inizializzazione
    private void initUI() {
        email = (EditText) findViewById(R.id.login_emailField_id);
        password = (EditText) findViewById(R.id.login_passwordField_id);
        ricordami = (CheckBox) findViewById(R.id.login_ricordami_id);
        button = findViewById(R.id.login_accedi_google_id);

        getMyPreferences();

        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        button.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {
                                          signInWithGoogle();
                                      }
                                  });

    }


    //Aggiornamento interfaccia, dopo login
    private void updateUI(FirebaseUser currentUser) {
        if(currentUser!=null) {
            getToken();
            new WalletAmount().execute("https://smartparkingpolito.altervista.org/getWalletAmount.php");
        }
    }

    //-----LOGIN CON GOOGLE------------

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                getToken();
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            new UploadGoogleSignin().execute(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //Snackbar.make(mBinding.mainLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    //------LOGIN CON USERNAME E PASSWORD-----------
    //Metodo richiamato con il click su Login
    public void login(View view){
        Log.i(TAG, "Hai cliccato su Login!");
        final String mailUtente = email.getText().toString();
        final String passwordUtente = password.getText().toString();
        final boolean checkRicordami = ricordami.isChecked();

        if(!mailUtente.isEmpty() && !passwordUtente.isEmpty()) {
            mAuth.signInWithEmailAndPassword(mailUtente, passwordUtente)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                                savePreference(checkRicordami, mailUtente, passwordUtente);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }
                            // ...
                        }
                    });
        }else
            Toast.makeText(this, "Controlla i campi.", Toast.LENGTH_SHORT).show();

    }

    //---------GESTIONE PREFERENZE-------------
    private void savePreference(boolean checkRicordami, String mailUtente, String passwordUtente) {
        SharedPreferences prefs = this.getSharedPreferences(MY_SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor= prefs.edit();;
        if(checkRicordami) {
            prefsEditor.putBoolean(CBOX_DATA_KEY, checkRicordami);
            prefsEditor.putString(EMAIL_DATA_KEY, mailUtente);
            prefsEditor.putString(PASSWORD_DATA_KEY, passwordUtente);
            prefsEditor.commit();
        } else{
            prefsEditor.clear();
            prefsEditor.commit();
        }
    }

    private void getMyPreferences() {
        SharedPreferences prefs = getSharedPreferences(MY_SHARED_PREF, Context.MODE_PRIVATE);
        if(prefs.getBoolean(CBOX_DATA_KEY, false)){
            email.setText(prefs.getString(EMAIL_DATA_KEY, ""));
            password.setText(prefs.getString(PASSWORD_DATA_KEY, ""));
            ricordami.setChecked(prefs.getBoolean(CBOX_DATA_KEY, false));
        }
    }


    //Richiamato con click su Registrati
    public void goToSignup(View view){
        Log.i(TAG, "Hai cliccato su Registrati. Verrai reindirizzato alla pagina di registrazione.");

        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        intent.putExtra("From", "LoginActivity");
        startActivity(intent);
    }

    //Richiamato con click su Password dimenticata
    public void resetPasswordEmail(View view) {
        String emailAddress = email.getText().toString();

        if(!emailAddress.isEmpty()) {
            mAuth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.i(TAG, "Email sent.");
                                Toast.makeText(LoginActivity.this, "Una mail è stata inviata al tuo indirizzo di posta.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else
            Toast.makeText(this, "Compila il campo mail per poter ripristinare la password.", Toast.LENGTH_SHORT).show();

    }

    //Prendere il token del dispositivo alla registrazione o per aggiornarlo
    public void getToken(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(task.isSuccessful()){
                    token = task.getResult().getToken();
                    Log.i(TAG, "Token: "+token);
                }else{
                    Log.i(TAG, "Problema", task.getException());
                    return;
                }
            }

        });

    }

    //------TASK ASINCRONI------------
    //TASK PER CARICARE NUOVO UTENTE SUL DB
    private class UploadGoogleSignin extends AsyncTask<FirebaseUser, Void, Boolean> {

        @Override
        protected Boolean doInBackground(FirebaseUser... firebaseUsers) {

            String url = "https://smartparkingpolito.altervista.org/CreateProfile.php";
            String params = null;
            FirebaseUser user = firebaseUsers[0];

            //Encoding parametri:

            try {
                params = "email=" + URLEncoder.encode(user.getEmail(), "UTF-8")
                        + "&id_user=" + URLEncoder.encode(user.getUid(), "UTF-8")
                        + "&device_token=" + URLEncoder.encode(token, "UTF-8")
                        + "&googleSignIn=" + URLEncoder.encode("1", "UTF-8");

                JSONArray jsonArray= ServerTask.askToServer(params,url);
                //gestisci JsonArray
                JSONObject jsonObjectControl=jsonArray.getJSONObject(0);
                String control=jsonObjectControl.getString("control");
                Log.i("cntr0",control);
                if (control.equals("OK")){   // non esegue l'if
                    //UTENTE REGISTRATO CORRETTAMENTE
                    Log.i(TAG, "Utente Google salvato correttamente.");
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


    private class WalletAmount extends AsyncTask<String, Void, Boolean> {
        FirebaseUser user = mAuth.getCurrentUser();

        @Override
        protected Boolean doInBackground(String... strings) {

            String url= strings[0];
            //Encoding parametri:
            try {
                String param = "id_user="+ URLEncoder.encode(user.getUid(), "UTF-8");

                JSONArray ja= ServerTask.askToServer(param, url);
                Log.i(TAG, "JSONArray da php: "+ja.toString());

                JSONObject jsonObject= ja.getJSONObject(0);
                Log.i(TAG, "JSON Object da php: "+jsonObject.toString());

                if (jsonObject!= null){
                    double wallet = Double.valueOf(jsonObject.getString("wallet"));
                    Log.i(TAG, "Wallet: "+wallet);
                    if(wallet<=0){
                        startActivity(new Intent(LoginActivity.this, PaymentActivity.class));
                    }else{
                        startActivity(new Intent(LoginActivity.this, MapsActivity.class));
                    }
                    return false;
                }

            }
            catch (UnsupportedEncodingException | JSONException e) {
                e.printStackTrace();
                return false;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            new UpdateDeviceToken().execute("https://smartparkingpolito.altervista.org/UpdateDeviceToken.php");
        }
    }

    private class UpdateDeviceToken extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {

            String url = strings[0];
            FirebaseUser user = mAuth.getCurrentUser();
            //Encoding parametri:
            try {
                String params = "id_user=" + URLEncoder.encode(user.getUid(), "UTF-8")
                        + "&device_token=" + URLEncoder.encode(token, "UTF-8");

                JSONArray jsonArray= ServerTask.askToServer(params,url);
                //gestisci JsonArray
                JSONObject jsonObjectControl=jsonArray.getJSONObject(0);
                String control=jsonObjectControl.getString("control");
                Log.i("cntr0",control);
                if (control.equals("OK")){   // non esegue l'if
                    //UTENTE REGISTRATO CORRETTAMENTE
                    Log.i(TAG, "Device Token aggiornato correttamente");
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
