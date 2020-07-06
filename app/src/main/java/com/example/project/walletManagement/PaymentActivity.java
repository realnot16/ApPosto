package com.example.project.walletManagement;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.project.ParametersAsync.ServerTask;
import com.example.project.R;
import com.example.project.map.MapsActivity;
import com.example.project.userManagement.LoginActivity;
import com.example.project.walletManagement.config.Config;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class PaymentActivity extends AppCompatActivity {

    private static final String TAG = "PaymentActivity" ;
    private RadioGroup rgPayment;
    private Button btPaynow;
    private TextView amountFromDb;
    private String amount;
    private String updatedWallet;
    private static final int PAYPAL_REQUEST_CODE = 123 ;
    private static PayPalConfiguration config= new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);
    private FirebaseAuth mAuth;
    private JSONObject paymentDetails= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        initUI();

        rgPayment= findViewById(R.id.rg_payment);
        btPaynow= findViewById(R.id.bt_paynow);
        amountFromDb = findViewById(R.id.tv_wallet_amount);
        mAuth= FirebaseAuth.getInstance();
        updatedWallet="";

        new WalletAmount().execute("https://smartparkingpolito.altervista.org/getWalletAmount.php");

        //avvio il servizio di Paypal (verificare prima di avere importato l'sdk di paypal)
        Intent intent= new Intent(this, PayPalService.class);
        //passo i dati sulla config
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
        //a questo punto paypal è in attesa di eventuali interazioni con il cliente, che avvengono tramite btnPayNow

    }

    //INIZIALIZZO ACTIIVTY
    private void initUI() {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.tv_activity_payment_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            Log.i(TAG, "Back, redirect a Maps");
            finish();
        }

        return super.onOptionsItemSelected(item);
    }



    private void processPayment() {
        /*Il costruttore di PayPalPayment si aspetta quattro parametri:
        1) quantità da spendere (BigDecimal)
        2) tipo di valuta
        3) eventuali messaggi
        4) motivo del pagamento*/
        PayPalPayment payPalPayment= new PayPalPayment(new BigDecimal(String.valueOf(amount)),
                "EUR",
                "Acquista beni",
                PayPalPayment.PAYMENT_INTENT_SALE);

        //creo un intent verso la  Payment Activity a cui passo la configurazione
        Intent intent= new Intent(this, com.paypal.android.sdk.payments.PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        //passo gli ExtraPayment
        intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYMENT, payPalPayment);

        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== PAYPAL_REQUEST_CODE){//controllo il requestCode
            if(resultCode== RESULT_OK){//controllo il resultCode

                PaymentConfirmation confirmation= data.getParcelableExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if(confirmation!= null){//controllo: se la risposta è diversa da null, estraggo i dettagli

                    paymentDetails= confirmation.toJSONObject();
                    Log.i(TAG, paymentDetails.toString());
                    try {//verifico se andato a buon fine
                        if(paymentDetails.getJSONObject("response").getString("state").equals("approved")){

                            Log.i(TAG, "Pagamento approvato, posso aggiornare il wallet");
                            new UpdateWallet().execute("https://smartparkingpolito.altervista.org/UpdateWallet.php");
                            //visualizzo dettagli del pagamento dopo aver eseguito l'operazione

                        }else{
                        Toast.makeText(this, "Errore: pagamento non andato a buon fine", Toast.LENGTH_LONG);
                    }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }else if(resultCode == Activity.RESULT_CANCELED)
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
        else if(resultCode == com.paypal.android.sdk.payments.PaymentActivity.RESULT_EXTRAS_INVALID)
            Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show();

    }

    //Attivazione listener sui bottoni
    private void setUpUI(){
        btPaynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int checkedId= rgPayment.getCheckedRadioButtonId();
                if(checkedId == -1){
                    Toast.makeText(getApplicationContext(), "Per effettuare una ricarica devi selezionare un importo!", Toast.LENGTH_LONG);
                }else if(checkedId == R.id.rb_5 ){
                    amount="5";
                }else if(checkedId == R.id.rb_15 ){
                    amount="15";
                }else if(checkedId == R.id.rb_10 ){
                    amount="10";
                }else if(checkedId == R.id.rb_20 ){
                    amount="20";
                }else if(checkedId == R.id.rb_50 ){
                    amount="50";
                }

                Log.i(TAG, "Importo selezionato: €"+amount );

                // a questo punto ho estratto l'importo, proseguo con la ricarica
                processPayment();

            }
        });
    }

    //------TASK ASINCRONI------
    //Lettura valore Wallet dal db per mostrarlo sul layout
    private class WalletAmount extends AsyncTask<String, Void, Boolean> {
        FirebaseUser user = mAuth.getCurrentUser();
        double wallet = -1.0;

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
                    wallet = Double.valueOf(jsonObject.getString("wallet"));
                    return true;
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
            //Al termine, viene settata l'interfaccia
            amountFromDb.setText(String.valueOf(wallet)+" €");
            setUpUI();
        }
    }

    //Aggiornamento wallet dopo ricarica
    private class UpdateWallet extends AsyncTask<String, Void, Boolean> {

        FirebaseUser user = mAuth.getCurrentUser();

        @Override
        protected Boolean doInBackground(String... strings) {

            String url= strings[0];
            //Encoding parametri:
            try {
                String param = "id_user="+ URLEncoder.encode(user.getUid(), "UTF-8")
                        + "&amount=" + URLEncoder.encode(amount, "UTF-8");
                /*String param = "id_user="+ URLEncoder.encode("JGiwCQtAwPVEd3J4Iw3ZNOYov4t1", "UTF-8")
                        + "&amount=" + URLEncoder.encode(amount, "UTF-8");*/

                JSONArray ja= ServerTask.askToServer(param, url);
                Log.i(TAG, "JSONArray da php: "+ja.toString());

                JSONObject jsonObject= ja.getJSONObject(0);

                Log.i(TAG, "JSON Object da php: "+jsonObject.toString());

                if (jsonObject!= null){
                    //UTENTE AGGIORNATO CORRETTAMENTE
                    updatedWallet= jsonObject.getString("wallet");
                    Log.i(TAG, "Wallet aggiornato correttamente. Nuovo credito residuo: "+updatedWallet);
                    return true;
                }

            }
            catch (UnsupportedEncodingException | JSONException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            //visualizzo i dettagli del pagamento e il nuovo importo
            Intent intent= new Intent(PaymentActivity.this, PaymentDetail.class);
            startActivity(intent.putExtra("PaymentDetail", paymentDetails.toString())//passo il dettaglio
                    .putExtra("PaymentAmount", amount)//passo l'amount (potrebbe non servire)
                    .putExtra("NewWallet", updatedWallet)
            );
        }
    }





    }
