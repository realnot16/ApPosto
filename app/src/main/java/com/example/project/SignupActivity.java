package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private EditText mail;
    private EditText password;
    private EditText confpassword;

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
        setContentView(R.layout.signup);

        //Inizializzazione
        initUI();
    }

    //Metodo di inizializzazione
    private void initUI() {
        mail = findViewById(R.id.registrationEmail_id);
        password = findViewById(R.id.registrationPassword_id);
        confpassword = findViewById(R.id.registrationConfpassword_id);

        mAuth = FirebaseAuth.getInstance();
    }

    private void updateUI(FirebaseUser currentUser) {
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(intent);
    }

    //It takes in an email address and password, validates them and then creates a new user.
    public void signup(View view){
        Log.i(TAG, "Hai cliccato su Registrati!");
        String mailUser = mail.getText().toString();
        String passwordUser = password.getText().toString();
        String confirmPwdUser = confpassword.getText().toString();

        if(validateUser(mailUser, passwordUser, confirmPwdUser)) {

            mAuth.createUserWithEmailAndPassword(mailUser, passwordUser)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
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
        } else{
            Toast.makeText(SignupActivity.this, "Ricontrolla i campi!", Toast.LENGTH_SHORT).show();
        }
    }

    //Validazione sintattica e semantica degli EditText
    private boolean validateUser(String mailUser, String passwordUser, String confirmPwdUser) {
            boolean valid = true;

        if (TextUtils.isEmpty(mailUser)) {
                Log.i(TAG, "Mail non presente.");
                valid = false;
        } else if(TextUtils.isEmpty(passwordUser)) {
                Log.i(TAG, "Password non presente.");
                valid = false;
        } else if (TextUtils.isEmpty(confirmPwdUser)){
                Log.i(TAG, "Password di conferma non presente.");
                valid = false;
        } else if(passwordUser.compareTo(confirmPwdUser)!=0) {
                Log.i(TAG, "Le password non coincidono.");
                valid = false;
        }else {
            Pattern emailPattern = Pattern
                    .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
            Pattern passwordPattern = Pattern
                    .compile("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}");
            Matcher emailMatcher = emailPattern.matcher(mailUser);
            Matcher passMatcher = passwordPattern.matcher(passwordUser);
            if(!emailMatcher.matches() || !passMatcher.matches())
                valid = false;
        }

            return valid;
    }

    public void goToLogin(View view){
        Log.i(TAG, "Hai cliccato su Accedi. Verrai reindirizzato alla pagina di Login.");

        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
    }

}
