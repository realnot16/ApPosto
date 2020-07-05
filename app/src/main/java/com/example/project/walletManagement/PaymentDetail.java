package com.example.project.walletManagement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.project.R;
import com.example.project.map.MapsActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentDetail extends AppCompatActivity {

    private static final String TAG = "PaymentDetail" ;
    private TextView tvId;
    private TextView tvAmount;
    private TextView tvStatus;
    private TextView tvCurrentWallet;
    private Button btGoToMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_detail);

        tvId= (TextView)findViewById(R.id.tvIdText);
        tvAmount= (TextView)findViewById(R.id.tvAmountText);
        tvStatus= (TextView)findViewById(R.id.tvStatusText);
        tvCurrentWallet= (TextView)findViewById(R.id.tvCurrentWalletText);
        btGoToMap= (Button)findViewById(R.id.bt_go_to_map);

        Intent intent= getIntent();

        //estraggo dagli extra dell'intent le info su payment detail
        try {
            JSONObject jsonObject= new JSONObject(intent.getStringExtra("PaymentDetail"));
            /*All'interno del json object:
             * state-> identifica lo stato del pagamento
             * id-> è l'id del pagamento */
            Log.i(TAG, jsonObject.toString());
            //tvId.setText(jsonObject);
            tvId.setText(jsonObject.getJSONObject("response").getString("id"));
            tvStatus.setText(jsonObject.getJSONObject("response").getString("state"));
            tvAmount.setText(intent.getStringExtra("PaymentAmount"));
            tvCurrentWallet.setText(intent.getStringExtra("NewWallet"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void goToMap(View view) {
        Intent intent= new Intent(PaymentDetail.this, MapsActivity.class);
        startActivity(intent);
    }
}
