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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.ParametersAsync.CloseReservationParamsAsync;
import com.example.project.ParametersAsync.LoadStationParamsAsync;
import com.example.project.ParametersAsync.OpenReservationParamsAsync;
import com.example.project.ParametersAsync.ServerTask;
import com.example.project.R;
import com.example.project.userManagement.Profilo;
import com.example.project.userManagement.ShowProfile;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.sql.Date;
import java.util.Arrays;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "Mappa";

    //MAPPA
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private PlacesClient mPlacesClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private View markPanel;
    private SlidingUpPanelLayout panel;
    private Station station_selected;

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

    //CURRENT RESERVATION INSTANCE
    private CurrentReservation currentReservation;

    //VARIE
    private DrawerLayout drawerLayout;
    private Integer filter_destination_meter=1200;  //FILTRO STAZIONI CARICATE

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout_main);

        //SLIDER STAZIONI

       panel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
       //panel.setPanelHeight(findViewById(R.id.floatingQrButton).getLayoutParams().height);
       panel.setPanelState(PanelState.HIDDEN);


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

        // CONTROLLA CURRENT RESERVATION
        String urlCurrRes="https://smartparkingpolito.altervista.org/GetCurrentReservation.php";
        new CheckCurrentReservation().execute(urlCurrRes);
        //AUTOCOMPLETAMENTO INDIRIZZI


        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });


        // BOTTONE PER QR CODE ACTIIVTY
        qrButton= findViewById(R.id.floatingQrButton);
        setQrButton(qrButton);
        //Genero una nuova Current Reservation;
        currentReservation=new CurrentReservation();// In realtà dovrebbe chiedere al server e se non c'è la crea, se c'è la setta

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
        LoadStationParamsAsync parametersAsync=new LoadStationParamsAsync(lat_start,long_start,city);
        new LoadStations().execute(parametersAsync);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.i(TAG,"markerClick");

                station_selected = (Station) marker.getTag();
                TextView stationId = findViewById(R.id.panel_station_id);
                TextView streetId = findViewById(R.id.panel_street_id);

                stationId.setText(station_selected.getId_parking().toString());
                streetId.setText(station_selected.getStreet());
                /*

                // Getting the position from the marker
                LatLng latLng = marker.getPosition();

                // Getting reference to the TextView to set latitude
                TextView tvLat = (TextView) findViewById(R.id.lati);

                // Getting reference to the TextView to set longitude
                TextView tvLng = (TextView) findViewById(R.id.longi);

                // Setting the latitude
                tvLat.setText("Latitude:" + latLng.latitude);

                // Setting the longitude
                tvLng.setText("Longitude:"+ latLng.longitude);*/

                panel.setPanelState(PanelState.EXPANDED);



                //panel.setAnchorPoint(findViewById(R.id.floatingQrButton).getHeight());
                //panel.setPanelState(PanelState.ANCHORED);
                return false;
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                panel.setPanelState(PanelState.HIDDEN);
            }
        });
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

    //IMPOSTO IL BOTTONE PER QR ACTIVITY + PERMISISON
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

    public void onBookStation(View view) {
        OpenReservationParamsAsync paramsAsync=new OpenReservationParamsAsync(mAuth.getUid(),station_selected.id_parking,"//FIttizia",0);
        new OpenReservation().execute(paramsAsync);
        panel.setPanelState(PanelState.HIDDEN);


    }


    //TASK ASYNC:

    //1- LOAD STATION, permette di acquisire tutte le stazioni nell'area visualizzata durante il primo accesso alla mappa
    private class LoadStations extends AsyncTask<LoadStationParamsAsync,Void,ArrayList<Station>> {
        @Override
        protected ArrayList<Station> doInBackground(LoadStationParamsAsync... parametersAsyncs) {

            String url="https://smartparkingpolito.altervista.org/AvailableParking.php";
            String params=null;
            ArrayList<Station> station = new ArrayList<Station>();

            //Encoding parametri:
            String lat_dest_string =String.valueOf(parametersAsyncs[0].latitude); //converto in stringhe i valori per inserirli nella richiesta
            String long_dest_string =String.valueOf(parametersAsyncs[0].longitude);

            try {
                params = "lat_destination=" +URLEncoder.encode(lat_dest_string, "UTF-8")
                        +"&long_destination=" +URLEncoder.encode(long_dest_string, "UTF-8")
                        +"&city="+URLEncoder.encode(parametersAsyncs[0].city, "UTF-8");

                    JSONArray jArray = ServerTask.askToServer(params,url);
                    for (int i = 0; i < jArray.length(); i++) {         //Ciclo di estrazione oggetti
                        JSONObject json_data = jArray.getJSONObject(i);
                        String latitudine = json_data.getString("latitude");
                        String longitudine = json_data.getString("longitude");
                        String city=json_data.getString("city");
                        String street=json_data.getString("street");
                        Integer id_parking=Integer.parseInt(json_data.getString("id_parking"));
                        double cost_minute = Double.parseDouble(json_data.getString("cost_minute"));
                        double lat = Double.parseDouble(latitudine);
                        double lng = Double.parseDouble(longitudine);
                        Station stat = new Station(lat, lng,city, street,id_parking,cost_minute);// usare dati scaricati
                        station.add(stat);

                    }



                }
                catch (Exception e) {
                Log.e("log_tag", "Error " + e.toString());
                }
                return station;
        }

        protected void onPostExecute(ArrayList<Station> stations) {
            Log.e(TAG, "PostExecute");
            for (int i = 0; i < stations.size(); i++) {         //Ciclo di estrazione oggetti
                Station stat = stations.get(i);
                LatLng position = new LatLng(stat.getLatitude(), stat.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions().position(position).title("Stazione N "+stat.getId_parking().toString());
                Marker marker = mMap.addMarker(markerOptions);
                marker.setTag(stat);

                Log.i(TAG, "marker aggiunto");
            }
        }
    }

    //2- OPEN RESERVATION, permette di aprire una prenotazione
    private class OpenReservation extends AsyncTask<OpenReservationParamsAsync,Void,String> {
        @Override
        protected String doInBackground(OpenReservationParamsAsync... parametersAsyncs) {
            String url="https://smartparkingpolito.altervista.org/OpenReservation.php";
            String params=null;
            String control=null;

            //Encoding parametri:
            String bonus_string=String.valueOf(parametersAsyncs[0].bonus);
            String id_parking_string=String.valueOf(parametersAsyncs[0].id_parking);
            String id_user_string=parametersAsyncs[0].id_user;
            String address_string=parametersAsyncs[0].address_start;
            try {
                params = "id_user=" + URLEncoder.encode(id_user_string, "UTF-8")
                        +"&bonus=" +URLEncoder.encode(bonus_string, "UTF-8")
                        +"&address_start=" +URLEncoder.encode(address_string, "UTF-8")
                        +"&id_parking="+URLEncoder.encode(id_parking_string, "UTF-8");

                JSONArray jsonArray=ServerTask.askToServer(params,url);
                //gestisci JsonArray
                JSONObject jsonObjectId=jsonArray.getJSONObject(0);
                JSONObject jsonObjectControl=jsonArray.getJSONObject(1);  // index 0 booking_id, index 1 control_status
                control=jsonObjectControl.getString("control");
                Log.i("cntr0",control);
                if (control.equals("OK")){   // non esegue l'if
                    currentReservation.id_booking=jsonObjectId.getString("booking_id");
                }
                 //avverti l'utente che il posto è stato occupato o non ha soldi

                //Mostrare apertura prenotazione
                //POP-UP CHE MOSTRA ALL'UTENTE IL RISULTATO;

            }
            catch (UnsupportedEncodingException | JSONException e) {
                e.printStackTrace();
            }
            return control;

        }
        @Override
        protected void onPostExecute(String result){
            switch (result){
                case "OK":
                    Toast.makeText(MapsActivity.this,getString(R.string.reserv_success),Toast.LENGTH_LONG).show();
                    break;
                case "OCCUPIED":
                    Toast.makeText(MapsActivity.this,getString(R.string.occupied),Toast.LENGTH_LONG).show();
                    break;
                case "ZERO_WALLET":
                    Toast.makeText(MapsActivity.this,getString(R.string.zero_wallet),Toast.LENGTH_LONG).show();
                    break;
                case "CONN_ERROR":
                    Toast.makeText(MapsActivity.this,getString(R.string.error_close_res),Toast.LENGTH_LONG).show();
                    break;
            }

        }

    }

    //3- CLOSE RESERVATION, permette di chiudere una prenotazione
    private class CloseReservation extends AsyncTask<CloseReservationParamsAsync,Void,String> {
        @Override
        protected String doInBackground(CloseReservationParamsAsync... parametersAsyncs) {

            String url="https://smartparkingpolito.altervista.org/CloseReservation.php";
            String params=null;
            String result=null;

            //Encoding parametri:
            String id_booking_string =parametersAsyncs[0].booking_id;
            String successful_string=String.valueOf(parametersAsyncs[0].successfull);
            String id_parking_string=String.valueOf(parametersAsyncs[0].id_parking);
            String id_user_string=parametersAsyncs[0].id_user;
            try {
                params = "id_user=" + URLEncoder.encode(id_user_string, "UTF-8")
                        +"&successful=" +URLEncoder.encode(successful_string, "UTF-8")
                        +"&id_booking=" +URLEncoder.encode(id_booking_string, "UTF-8")
                        +"&id_parking="+URLEncoder.encode(id_parking_string, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            JSONArray jsonArray=ServerTask.askToServer(params,url);
            if(jsonArray==null);
            try {
                result= jsonArray.getJSONObject(0).getString("result");
            }
            catch (JSONException e) {
                e.printStackTrace();
                result="ERROR";
            }

            return result;

        }
        @Override
        protected void onPostExecute(String result){
            switch (result){
                case "OK":          currentReservation.id_booking=null;
                    Toast.makeText(MapsActivity.this,getString(R.string.ok),Toast.LENGTH_LONG).show();
                    break;
                case "WRONG_PARKING":Toast.makeText(MapsActivity.this,getString(R.string.sorry),Toast.LENGTH_LONG).show();
                    break;
                case "ERROR":        Toast.makeText(MapsActivity.this,getString(R.string.error_close_res),Toast.LENGTH_LONG).show();
                    break;
            }

        }

    }

    //4-LOAD PROFILE,Prendo i dati sul profilo dal DB
    private class LoadProfile extends AsyncTask<String,Void,Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                String params = "email=" +URLEncoder.encode(user.getEmail(), "UTF-8");
                JSONArray jArray = ServerTask.askToServer(params,strings[0]);

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

            } catch (Exception e) {
                Log.e("log_tag", "Error " + e.toString());
            }
            return true;
        }

    }

    //5-CHECK CURRENT RESERVATION, controlla se sul server è settata una prenotazione per l'user corrente, evita di perdere
    // la prenotazione se l'app viene chiusa
    private class CheckCurrentReservation extends AsyncTask<String,Void,Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                String params = "id_user=" +URLEncoder.encode(mAuth.getUid(), "UTF-8");
                JSONArray jArray = ServerTask.askToServer(params,strings[0]);
                try {
                    JSONObject json_result = jArray.getJSONObject(1);
                    if (json_result.getString("result").equals("ok")){
                        JSONObject json_body = jArray.getJSONObject(0);
                        currentReservation.setId_booking(json_body.getString("id_booking"));
                        currentReservation.setAddress_start(json_body.getString("address_start"));
                        currentReservation.setParking_id(Integer.parseInt(json_body.getString("parking_id")));
                        currentReservation.setBonus(Integer.parseInt(json_body.getString("bonus")));
                        currentReservation.setSuccessful(Integer.parseInt(json_body.getString("successful")));
                    }

                }
                catch (JSONException ex) {
                    ex.printStackTrace();
                }

            } catch (Exception e) {
                Log.e("log_tag", "Error " + e.toString());
            }
            return true;
        }

    }


    //Callback method after startActivityForResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        //GESTIONE EVENTI SCANNER
        if(requestCode==SCANNER_REQUEST_CODE && resultCode==Activity.RESULT_OK) {
            Integer result = Integer.parseInt(data.getStringExtra("parking_code"));
            Toast.makeText(MapsActivity.this, result.toString(), Toast.LENGTH_SHORT).show();
            String id_user=mAuth.getUid();
            Integer id_parking=result;
            String id_booking=currentReservation.id_booking;// currentReservation.getId(); recupera id_booking da istanza currentReservation
            CloseReservationParamsAsync paramsAsync= new CloseReservationParamsAsync(id_user,id_parking,id_booking,1);
            new CloseReservation().execute(paramsAsync);
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
