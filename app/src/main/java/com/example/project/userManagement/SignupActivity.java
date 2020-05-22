package com.example.project.userManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private EditText mail;
    private EditText password;
    private EditText confpassword;
    private EditText firstname;
    private EditText lastname;
    private EditText birthdate;
    private EditText phone;
    private EditText city;
    DatePickerDialog picker;
    private String birthdateString;

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
        setContentView(R.layout.signup_layout);

        //Inizializzazione
        initUI();
    }

    //Metodo di inizializzazione
    private void initUI() {
        mail = findViewById(R.id.changePwd_oldField_id);
        password = findViewById(R.id.changePwd_newField_id);
        confpassword = findViewById(R.id.changePwd_confNewField_id);
        firstname = findViewById(R.id.signUp_nameField_id);
        lastname = findViewById(R.id.signUp_surnameField_id);
        phone = findViewById(R.id.signUp_phoneField_id);
        birthdate = findViewById(R.id.signUp_dateField_id);
        city = findViewById(R.id.signUp_cityField_id);
        birthdateString = "";

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
        String nome = firstname.getText().toString();
        String cognome = lastname.getText().toString();
        String città = city.getText().toString();
        String telefono = phone.getText().toString();

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

            //uploadUser(nome, cognome, mailUser, telefono, data, città);
        } else{
            Toast.makeText(SignupActivity.this, "Ricontrolla i campi!", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadUser(String nome, String cognome, String mailUser, String telefono, Date data, String città) {

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
                    .compile("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@"
                            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
            Pattern passwordPattern = Pattern
                    .compile("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}");
            Matcher emailMatcher = emailPattern.matcher(mailUser);
            Matcher passMatcher = passwordPattern.matcher(passwordUser);
            if(!emailMatcher.matches() || !passMatcher.matches()) {
                valid = false;
                Toast.makeText(this, "La password deve contenere almeno 1 maiuscola, 1 minuscola e un numero, e deve essere di almeno 8 caratteri.", Toast.LENGTH_LONG).show();
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
                        birthdateString = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        birthdate.setText(birthdateString);
                    }
                }, year, month, day);
        picker.show();
    }


}
