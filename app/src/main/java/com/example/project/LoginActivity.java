package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

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

    //When initializing your Activity, check to see if the user is currently signed in.
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

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

    private void getMyPreferences() {
        SharedPreferences prefs = getSharedPreferences(MY_SHARED_PREF, Context.MODE_PRIVATE);
        if(prefs.getBoolean(CBOX_DATA_KEY, false)){
            email.setText(prefs.getString(EMAIL_DATA_KEY, ""));
            password.setText(prefs.getString(PASSWORD_DATA_KEY, ""));
            ricordami.setChecked(prefs.getBoolean(CBOX_DATA_KEY, false));
        }
    }

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


    private void updateUI(FirebaseUser currentUser) {
        if(currentUser!=null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    //Metodo richiamato con il click su Login
    public void login(View view){
        Log.i(TAG, "Hai cliccato su Login!");
        final String mailUtente = email.getText().toString();
        final String passwordUtente = password.getText().toString();
        final boolean checkRicordami = ricordami.isChecked();

        if(!mailUtente.isEmpty() && !mailUtente.isEmpty()) {
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

    //Richiamato con click su Registrati
    public void goToSignup(View view){
        Log.i(TAG, "Hai cliccato su Registrati. Verrai reindirizzato alla pagina di registrazione.");

        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
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
}
