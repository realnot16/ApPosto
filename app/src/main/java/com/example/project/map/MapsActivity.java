package com.example.project.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.project.R;
import com.example.project.userManagement.Profilo;
import com.example.project.userManagement.ShowProfile;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.Date;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "Mappa";

    //MAPPA
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private PlacesClient mPlacesClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private final LatLng mDefaultLocation = new LatLng(45.070841, 7.668552);
    private static final int DEFAULT_ZOOM = 15;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private boolean permissionChoose=false;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";


    //SCANNER
    private final static int  SCANNER_REQUEST_CODE=2;
    private final static int  PROFILE_REQUEST_CODE=1;
    private final static int MY_CAMERA_REQUEST_CODE=100;
    FloatingActionButton qrButton;

    //AUTENTICAZIONE
    FirebaseAuth mAuth;
    FirebaseUser user;

    //VARIE
    private DrawerLayout drawerLayout;
    private Integer filter_destination_meter=1200;  //FILTRO STAZIONI CARICATE

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout_main);

        //AUTENTICAZIONE
        mAuth = FirebaseAuth.getInstance();


        // TOOLBAR E NAVIGATION DRAWER
        Toolbar toolbar = findViewById(R.id.toolbar);
        setToolbar(toolbar);


        // MAPPA : Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if (savedInstanceState != null) {  //Recupero informazioni sull'ultima posizione rilevata
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        Places.initialize(getApplicationContext(),getResources().getString(R.string.google_maps_key));
        mPlacesClient = Places.createClient(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Retrieve location and camera position from saved instance state.


        // BOTTONE PER QR CODE ACTIIVTY
        qrButton= findViewById(R.id.floatingQrButton);
        setQrButton(qrButton);

    }

    //When initializing your Activity, check to see if the user is currently signed in.
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        user = mAuth.getCurrentUser();
        //mAuth.signOut();
        //updateUI(currentUser);
    }

    //ACTIVITY IN PAUSA
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //salvo lo stato della mappa quando l'activity è in pausa
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    //IMPOSTO LA TOOLBAR
    private void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true); // for add back arrow in action bar
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.map_ic_menu_black_24dp);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("");
        setNavigationDrawer(toolbar);
        Drawable filterIcon = getDrawable(R.drawable.map_ic_filter_list_black_24dp);
        toolbar.setOverflowIcon(filterIcon);
    }

    //COLLEGO IL NAVIGATION LAYOUT ALLA TOOLBAR
    private void setNavigationDrawer(Toolbar toolbar){
        drawerLayout =  findViewById(R.id.drawer_layout);
        NavigationView navigationView =  findViewById(R.id.map_navigationDrawer_id);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        drawerLayout.closeDrawers();
                        return true;
                    case R.id.show_profile_id:
                        new LoadProfile().execute("http://smartparkingpolito.altervista.org/getProfile.php");
                        Log.i(TAG, "Ho cliccato su Profilo nel menu.");
                    default:
                        return true;
                }
            }
        });
        ActionBarDrawerToggle actionBarDrawerToggle =
                new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.open ,R.string.close ) {
                    @Override
                    public void onDrawerClosed(View drawerView) {
                        super.onDrawerClosed(drawerView);
                    }
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        super.onDrawerOpened(drawerView);
                    }
                };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

    }


    //Prendo i dati sul profilo dal DB
    private class LoadProfile extends AsyncTask<String,Void,Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]); //
                //preparazione della richiesta
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); //apertura connessione
                urlConnection.setReadTimeout(1000);
                urlConnection.setConnectTimeout(1500);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                String params = "email=" +URLEncoder.encode(user.getEmail(), "UTF-8");
                Log.i(TAG, "params= "+params);
                DataOutputStream dos = new DataOutputStream(urlConnection.getOutputStream());
                dos.writeBytes(params);
                dos.flush();
                dos.close();

                urlConnection.connect(); //connessione
                InputStream is = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.ISO_8859_1), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null)
                    sb.append(line + "\n");
                is.close();

                String result = sb.toString();
                Log.i("result", result);
                if(result==null){
                    Log.i("result", "Nessun risultato");
                }
                else {
                    JSONArray jArray = new JSONArray(result);

                    for (int i = 0; i < jArray.length(); i++) {         //Ciclo di estrazione oggetti
                        JSONObject json_data = jArray.getJSONObject(i);
                        Intent profileIntent = new Intent(MapsActivity.this, ShowProfile.class);
                        Bundle profileBundle = new Bundle();
                        Profilo profilo = new Profilo();
                        profilo.setEmail(user.getEmail());
                        profilo.setFirstname(json_data.getString(Profilo.ProfiloMetaData.FIRSTNAME));
                        profilo.setLastname(json_data.getString(Profilo.ProfiloMetaData.LASTNAME));
                        profilo.setBirthdate(Date.valueOf(json_data.getString(Profilo.ProfiloMetaData.BIRTHDATE)));
                        profilo.setCity(json_data.getString(Profilo.ProfiloMetaData.CITY));
                        profilo.setPhone(json_data.getString(Profilo.ProfiloMetaData.PHONE));
                        profilo.setWallet((float) json_data.getDouble(Profilo.ProfiloMetaData.WALLET));
                        profileBundle.putParcelable("User", profilo);
                        profileIntent.putExtra("User", profileBundle);
                        startActivityForResult(profileIntent, PROFILE_REQUEST_CODE);
                    }
                }


            } catch (Exception e) {
                Log.e("log_tag", "Error " + e.toString());
            }
            return true;
        }

    }


    //IMPLEMENTO I FILTRI
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map_filters_menu, menu);
        return true;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG,"mappa Pronta");
        mMap = googleMap;
        setMap();

        //CARICO LE STAZIONI

        double lat_start= 45.070841;//fittizie: SOSTITUIRE CON QUELLE DEL DISPOSITIVO
        double long_start=7.668552;//fittizie
        String city="Torino";
        ParametersAsync parametersAsync=new ParametersAsync(lat_start,long_start,city);
        new LoadStations().execute(parametersAsync);
        Log.i(TAG,"Stazioni caricate");
        Toast prova = Toast.makeText(this, "estrazione finita",Toast.LENGTH_SHORT);
        prova.show();


    }


    //IMPOSTO LA MAPPA
    private void setMap() {
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        // Posizione di default
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));

        //Acquisisco i permessi e  riposiziono la vista
        getLocationPermission();

        return;
    }

    //Acquisisco la posizione del dispositivo e riposiziono la vista
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        Log.i(TAG, "Acquisisco posizioneDevice");
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                            Log.i(TAG, "Posizione Acquisita");
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    //RICHIESTA PERMESSI GEOLOCALIZZAZIONE
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        Log.i(TAG,"Richiedo Permesso");
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    //IMPOSTO IL BOTTONE PER QR ACTIVITY
    private void setQrButton(View qrButton) {
        qrButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MapsActivity.this,   //controlla che il permesso sia garantito
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MapsActivity.this,   //richiesta di permesso all'utente
                            new String[]{Manifest.permission.CAMERA},MY_CAMERA_REQUEST_CODE);
                }
                else{
                    Intent intent=new Intent(MapsActivity.this, ScannerActivity.class);
                    startActivityForResult(intent,SCANNER_REQUEST_CODE);

                }
            }
        });
    }

    //LOAD STATION, da implementare
    //permette di acquisire tutte le stazioni nell'area visualizzata durante il primo accesso alla mappa
    private class LoadStations extends AsyncTask<ParametersAsync,Void,ArrayList<Station>> {
        @Override
        protected ArrayList<Station> doInBackground(ParametersAsync... parametersAsyncs) {

            try {
                URL url = new URL("https://smartparkingpolito.altervista.org/AvailableParking.php"); //
                //preparazione della richiesta
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection(); //apertura connessione
                urlConnection.setReadTimeout(1000);
                urlConnection.setConnectTimeout(1500);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                String lat_dest_string = String.valueOf(parametersAsyncs[0].latitude); //converto in stringhe i valori per inserirli nella richiesta
                String long_dest_string = String.valueOf(parametersAsyncs[0].longitude);
                String filter_string = String.valueOf(filter_destination_meter);

                String params = "lat_destination=" + URLEncoder.encode(lat_dest_string, "UTF-8")
                        + "&long_destination=" + URLEncoder.encode(long_dest_string, "UTF-8")
                        + "&city=" + URLEncoder.encode(parametersAsyncs[0].city, "UTF-8")//modificare:estrarre la città da preferenze
                        + "&dist_filter_meter=" + URLEncoder.encode(filter_string, "UTF-8");
                Log.i("param", params);
                DataOutputStream dos = new DataOutputStream(urlConnection.getOutputStream());
                dos.writeBytes(params);
                dos.flush();
                dos.close();

                urlConnection.connect(); //connessione
                InputStream is = urlConnection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.ISO_8859_1), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                String result = sb.toString();

                Log.i("result", result);
                if (result == null) {
                    String no_parking = getResources().getString(R.string.toast_no_parking);
                    Log.i("param1", no_parking);
                    Toast.makeText(getBaseContext(), no_parking, Toast.LENGTH_SHORT).show();
                } else {
                    JSONArray jArray = new JSONArray(result);

                    String outputString = "";
                    ArrayList<Station> station = new ArrayList<Station>();
                    for (int i = 0; i < jArray.length(); i++) {         //Ciclo di estrazione oggetti
                        JSONObject json_data = jArray.getJSONObject(i);
                        String latitudine = json_data.getString("latitude");
                        String longitudine = json_data.getString("longitude");
                        Log.i(TAG, latitudine+" "+longitudine);
                        double lat = Double.parseDouble(latitudine);
                        double lng = Double.parseDouble(longitudine);
                        Station stat = new Station(lat, lng);
                        station.add(stat);

                    }
                    return station;
                }


            } catch (Exception e) {
                Log.e("log_tag", "Error " + e.toString());
            }
            return null;
        }

        protected void onPostExecute(ArrayList<Station> stations) {
            Log.e(TAG, "PostExecute");
            for (int i = 0; i < stations.size(); i++) {         //Ciclo di estrazione oggetti
                Station stat = stations.get(i);
                LatLng position = new LatLng(stat.getLatitude(), stat.getLongitude());
                MarkerOptions marker = new MarkerOptions().position(position).title("Stazione N "+i);
                mMap.addMarker(marker);
                Log.i(TAG, "marker aggiunto");
            }
        }
    }

    //DBASYNCREQUEST, cambiare nome-> non esplicativa
    //raccoglie tutte le stazioni in un area di destinazione selezionata. si avvia in seguito alla ricerca
    //restituisce i marker o l'arraylist? direi array list
    private class DbAsyncRequest extends AsyncTask<ParametersAsync,Void,Boolean> {
        @Override
        protected Boolean doInBackground(ParametersAsync... parametersAsyncs) {

            try {

                URL url = new URL("http://smartparkingpolito.altervista.org/AvailableParking.php"); //
                //preparazione della richiesta
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); //apertura connessione
                urlConnection.setReadTimeout(1000);
                urlConnection.setConnectTimeout(1500);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                String lat_dest_string =String.valueOf(parametersAsyncs[0].latitude); //converto in stringhe i valori per inserirli nella richiesta
                String long_dest_string =String.valueOf(parametersAsyncs[0].longitude);
                String filter_string =String.valueOf(filter_destination_meter);


                String params = "lat_destination=" +URLEncoder.encode(lat_dest_string, "UTF-8")
                        +"&long_destination=" +URLEncoder.encode(long_dest_string, "UTF-8")
                        +"&city="+URLEncoder.encode(parametersAsyncs[0].city, "UTF-8")//modificare:estrarre la città da preferenze
                        +"&dist_filter_meter="+URLEncoder.encode(filter_string, "UTF-8");
                Log.i("param", params);
                DataOutputStream dos = new DataOutputStream(urlConnection.getOutputStream());
                dos.writeBytes(params);
                dos.flush();
                dos.close();


                urlConnection.connect(); //connessione
                InputStream is = urlConnection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();

                String result = sb.toString();
                Log.i("result", result);
                if(result==null){
                    String no_parking = getResources().getString(R.string.toast_no_parking);
                    Log.i("param1", no_parking);
                    Toast.makeText(getBaseContext(), no_parking,Toast.LENGTH_SHORT).show();


                }
                else {
                    JSONArray jArray = new JSONArray(result);

                    for (int i = 0; i < jArray.length(); i++) {         //Ciclo di estrazione oggetti
                        JSONObject json_data = jArray.getJSONObject(i);
                        String latitudine=json_data.getString("latitude");
                        Log.i("latitudine", latitudine);
                        String longitudine=json_data.getString("longitude");
                        Log.i("longitudine", longitudine);


                    }
                }


            } catch (Exception e) {
                Log.e("log_tag", "Error " + e.toString());
            }
            return null;
        }

    }


    //Callback method after startActivityForResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        //GESTIONE EVENTI SCANNER
        if(requestCode==SCANNER_REQUEST_CODE && resultCode==Activity.RESULT_OK) {
            String result = data.getStringExtra("parking_code");
            Toast.makeText(MapsActivity.this, result, Toast.LENGTH_SHORT).show();
        }
        //GESTIONE DATI PROFILO
        if(requestCode==PROFILE_REQUEST_CODE && resultCode==Activity.RESULT_OK){

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,@NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_CAMERA_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent=new Intent(MapsActivity.this,ScannerActivity.class);
                    startActivityForResult(intent,SCANNER_REQUEST_CODE);

                } else {
                    Toast.makeText(MapsActivity.this, R.string.permcameradenied,Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                mLocationPermissionGranted = false;
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
                Log.i(TAG,"Esito permessi geoL: "+mLocationPermissionGranted);

                Log.i(TAG,"Mi preparo: "+mLocationPermissionGranted); //Perché a runtime avviene prima di getLocalPermission?
                if (mLocationPermissionGranted) {
                    // Get the current location of the device and set the position of the map.
                    Log.i(TAG, "Imposto posizioneAcquisita");
                    getDeviceLocation();
                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                }
                else {
                    Log.i(TAG, "Imposto posizioneDefault, Peremssi non accordati");
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                    mMap.setMyLocationEnabled(false);
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    mLastKnownLocation = null;
                }

            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }




}
