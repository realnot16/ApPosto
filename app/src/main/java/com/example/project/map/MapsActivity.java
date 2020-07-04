package com.example.project.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.NotificationService;
import com.example.project.ParametersAsync.CloseReservationParamsAsync;
import com.example.project.ParametersAsync.LoadStationParamsAsync;
import com.example.project.ParametersAsync.OpenReservationParamsAsync;
import com.example.project.ParametersAsync.ServerTask;
import com.example.project.R;
import com.example.project.reservation.ReservationsActivity;
import com.example.project.userManagement.LoginActivity;
import com.example.project.userManagement.Profilo;
import com.example.project.userManagement.ProfileActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;


import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import javax.net.ssl.HttpsURLConnection;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "Mappa";

    //LAYOUT

    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private List<Marker> AllMarkers;
    private View markPanel;
    private SlidingUpPanelLayout panel;
    private ConstraintLayout filterLayout;
    private ConstraintLayout stationLayout;

    private Station station_selected;

    // Keys for storing map activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";


    //GEOLOCALIZZAZIONE
    private Location mLastKnownLocation;
    private final LatLng mDefaultLocation = new LatLng(45.070841, 7.668552);
    private static final int DEFAULT_ZOOM = 15;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted=false;




    //SCANNER
    private final static int  NOTIFICATION_REQUEST_CODE=3;
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
    //POPUP
    private String new_parking_rdrct;
    private AlertDialog popup;
    ConstraintLayout layoutReservation;

    //NAVIGATORE
    private LinkedList<LatLng> polylinesPoints;
    private Polyline polyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout_main);

        //SLIDER STAZIONI

       panel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
       panel.setPanelState(PanelState.HIDDEN);

       filterLayout = findViewById(R.id.panel_filter_layout_id);
       stationLayout = findViewById(R.id.panel_station_layout_id);



       //AUTENTICAZIONE
        mAuth = FirebaseAuth.getInstance();

        // SETTO LISTA MARKERS
        AllMarkers = new ArrayList<Marker>();

        // TOOLBAR E NAVIGATION DRAWER
        Toolbar toolbar = findViewById(R.id.toolbar);
        setToolbar(toolbar);

        // MAPPA : Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if (savedInstanceState != null) {  //Recupero informazioni sull'ultima posizione rilevata
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        createMap();


        // CONTROLLA CURRENT RESERVATION
        currentReservation=new CurrentReservation();// In realtà dovrebbe chiedere al server e se non c'è la crea, se c'è la setta
        String urlCurrRes="https://smartparkingpolito.altervista.org/GetCurrentReservation.php";
        new CheckCurrentReservation().execute(urlCurrRes);


        // MESSAGE BROADCAST RECEIVER per catturare i messaggi provenienti dal Notification Service
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (intent.hasExtra("id_parking")){
                        String id_parking = intent.getStringExtra("id_parking");
                        String distance = intent.getStringExtra("distance");
                        String address = intent.getStringExtra("address");
                        showRdrctPopup(id_parking,distance,address);
                        new_parking_rdrct=id_parking;
                        }
                        else showRdrctPopup(null,null,null);



                    }
                }, new IntentFilter(NotificationService.ACTION_MESSAGE_BROADCAST)
        );

        // BOTTONE PER QR CODE ACTIIVTY
        qrButton= findViewById(R.id.floatingQrButton);
        setQrButton(qrButton);
        //EVIDENZIA STATO PRENOTAZIONE IN CORSO
        layoutReservation=(ConstraintLayout) findViewById(R.id.linLayoutCurrRes);
        if(currentReservation.id_booking!=null){
            layoutReservation.setVisibility(View.VISIBLE);
        }


    }

    private void createMap() {
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
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

    //IMPOSTO LA TOOLBAR:---------------------------------------------------------------------

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
        setAutocomplete();

       /* MenuItem filter = findViewById(R.id.action_filter);
        filter.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                filterLayout.setVisibility(View.VISIBLE);
                stationLayout.setVisibility(View.GONE);
                panel.setPanelState(PanelState.EXPANDED);
                return false;
            }
        });*/
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
                    case R.id.show_profile_id:
                        if(mAuth.getCurrentUser()!=null) {
                            startActivity(new Intent(MapsActivity.this, ProfileActivity.class));
                            Log.i(TAG, "Ho cliccato su Profilo nel menu. User:" + mAuth.getCurrentUser().getEmail());
                        }else {
                            mAuth.signOut();
                            Toast.makeText(MapsActivity.this, "Non sei autenticato!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MapsActivity.this, LoginActivity.class));
                        }
                        return true;
                    case R.id.logout_profile_id:
                        mAuth.signOut();
                        startActivity(new Intent(MapsActivity.this, LoginActivity.class));
                        return true;
                    case R.id.show_reservations:
                        startActivity(new Intent(MapsActivity.this, ReservationsActivity.class));
                        return true;
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

    private void setAutocomplete(){
        //AUTOCOMPLETAMENTO INDIRIZZI

        String apiKey = getString(R.string.place_autocomplete_key);

        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        final AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        ImageView searchIcon = (ImageView)((LinearLayout)autocompleteFragment.getView()).getChildAt(0);
        searchIcon.setVisibility(View.GONE);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG,Place.Field.ID, Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId()+" - "+place.getLatLng());
                //LatLng newPosition = new LatLng(place.getLatLng().latitude,place.getLatLng().longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), DEFAULT_ZOOM));

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

    }


    //IMPLEMENTO I FILTRI
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map_filters_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                // User chose the "Settings" item, show the app settings UI...
                Log.i(TAG,"Apro Filtri");

                filterLayout.setVisibility(View.VISIBLE);
                stationLayout.setVisibility(View.GONE);
                panel.setPanelState(PanelState.EXPANDED);

                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }



    //IMPOSTO LA MAPPA + LOCALIZZAZIONE:---------------------------------------------------------------------
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG,"mappa Pronta");
        mMap = googleMap;
        setMap();

        //CARICO LE STAZIONI
        askStations();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.i(TAG,"markerClick");

                station_selected = (Station) marker.getTag();
                TextView stationId = findViewById(R.id.panel_station_id);
                TextView streetId = findViewById(R.id.panel_street_id);

                Log.i(TAG,"Apro marker");
                stationId.setText(station_selected.getId_parking().toString());
                streetId.setText(station_selected.getStreet());

                stationLayout.setVisibility(View.VISIBLE);
                filterLayout.setVisibility(View.GONE);
                panel.setPanelState(PanelState.EXPANDED);
                Log.i(TAG,"marker aperto");

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
        Toast.makeText(this, "estrazione finita",Toast.LENGTH_SHORT);





    }


    private void setMap() {
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        //Riposiziono il bottone di geolocalizzazione
        View locationButton = ((View) findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rlp.setMargins(0, 180, 180, 0);
        
        // Posizione di default
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));

        Places.initialize(getApplicationContext(),getResources().getString(R.string.google_maps_key));
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        //Acquisisco i permessi e  riposiziono la vista
        checkLocationPermission();
        setDeviceLocation();

        return;
    }

    //IMPOSTO POSIZIONE DISPOSITIVO
    private void setDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        Log.i(TAG, "GEOLOCALIZZAZIONE-3: Procedo ad impostare posizione device");
        try {
            if (mLocationPermissionGranted) {
                Log.i(TAG, "GEOLOCALIZZAZIONE-4: Permessi -->"+mLocationPermissionGranted);
                Log.i(TAG, "GEOLOCALIZZAZIONE-5: Imposto bottone geolocalizzazione");

                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        if(mLocationPermissionGranted)
                            askForGPS();
                        else
                            Toast.makeText(MapsActivity.this,"Permessi disattivati",Toast.LENGTH_SHORT);
                        return false;
                    }
                });

                Log.i(TAG, "GEOLOCALIZZAZIONE-6: Verifico attivazione GPS");
                askForGPS();

                Log.i(TAG, "GEOLOCALIZZAZIONE-7: Acquisisco ed imposto posizione");
                setMyLocation();

            }
            else{
                Log.i(TAG, "GEOLOCALIZZAZIONE-4: Permessi -->"+mLocationPermissionGranted+" , Imposto posizione default");
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    //ACQUISISCO POSIZIONE DISPOSITIVO
    private void setMyLocation(){
        Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
        locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    // Set the map's camera position to the current location of the device.
                    mLastKnownLocation = task.getResult();
                    Log.i(TAG, "GEOLOCALIZZAZIONE-7a: Task terminato, Posizione Acquisita --> "+mLastKnownLocation);
                    if (mLastKnownLocation != null) {
                        Log.i(TAG, "GEOLOCALIZZAZIONE-7b: Imposto posizione nella mappa");
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                    }
                    else{
                        Log.i(TAG, "GEOLOCALIZZAZIONE-7b: Posizione nulla, Imposto default");
                        Log.e(TAG, "Exception: %s", task.getException());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                    }

                } else {
                    Log.i(TAG, "GEOLOCALIZZAZIONE-7a: Task senza susccesso, Imposto default");
                    Log.e(TAG, "Exception: %s", task.getException());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));

                }
            }
        });
    }

    //VERIFICO GPS ATTIVO
    private void askForGPS(){

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Log.i(TAG, "GEOLOCALIZZAZIONE-6a: GPS attivo, si procede all'attivazione");
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Log.i(TAG, "GEOLOCALIZZAZIONE-6a: GPS non attivo, richiedo attivazione");
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(MapsActivity.this,
                                        LocationRequest.PRIORITY_HIGH_ACCURACY);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            }
        });

    }

    //RICHIESTA PERMESSI GEOLOCALIZZAZIONE
    private void checkLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        Log.i(TAG, "GEOLOCALIZZAZIONE-1: Verifico permessi");
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "GEOLOCALIZZAZIONE-2: permessi già accordati");
            mLocationPermissionGranted = true;
        } else {
            Log.i(TAG, "GEOLOCALIZZAZIONE-2: permessi NON accordati, richiedo...");
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    //BOTTONI FLOATING + PERMESSI+ BOTTONE PRENOTAZIONE --------------------------------------
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
                    Boolean canCloseReservation=false;
                    if(currentReservation.getId_booking()!=null){
                        canCloseReservation=true;
                    }
                    intent.putExtra("canCloseReservation",canCloseReservation);
                    startActivityForResult(intent,SCANNER_REQUEST_CODE);

                }
            }
        });
    }

    private void askStations(){
        LatLng currentPosition = mMap.getCameraPosition().target;
        String city="Torino";//fittizia
        LoadStationParamsAsync parametersAsync=new LoadStationParamsAsync(currentPosition.latitude,currentPosition.longitude,city);
        new LoadStations().execute(parametersAsync);
    }
    //Apre la reservation e nasconde panel
    public void onBookStation(View view) {
        if (currentReservation.getId_booking()==null){
        OpenReservationParamsAsync paramsAsync=new OpenReservationParamsAsync(mAuth.getUid(),station_selected.id_parking,"//FIttizia",0);
        new OpenReservation().execute(paramsAsync);
        panel.setPanelState(PanelState.HIDDEN);



        }
        else Toast.makeText(this,R.string.alreadybooked,Toast.LENGTH_LONG).show();


    }

    public void onRefreshClick(View view) {
        for (Marker mLocationMarker: AllMarkers) {
            mLocationMarker.remove();
        }
        AllMarkers.clear();
        askStations();
    }

    public void onCloseResButtonClick(View view) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.alertCloseReser))
                .setMessage(getString(R.string.alertCloseMessage))

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String id_user=mAuth.getUid();
                        Integer id_parking=currentReservation.parking_id;
                        String id_booking=currentReservation.id_booking;// currentReservation.getId(); recupera id_booking da istanza currentReservation
                        CloseReservationParamsAsync paramsAsync= new CloseReservationParamsAsync(id_user,id_parking,id_booking,1);
                        new CloseReservation().execute(paramsAsync);

                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    //TASK ASYNC:---------------------------------------------------------------------------------

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
                MarkerOptions markerOptions = new MarkerOptions().position(position)
                        .title("Stazione N "+stat.getId_parking().toString())
                        .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.map_ic_pin));
                Marker marker = mMap.addMarker(markerOptions);//BitmapDescriptorFactory.fromResource(R.drawable.map_pin)
                AllMarkers.add(marker);
                marker.setTag(stat);

                Log.i(TAG, "marker aggiunto");
            }
        }
    }

    //2- OPEN RESERVATION, permette di aprire una map_icona_panel_prenotazione
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
            Log.i(TAG, "dettagli map_icona_panel_prenotazione: "+bonus_string+" "+ id_parking_string+" "+ id_user_string+" "+ address_string);
            try {
                params = "id_user=" + URLEncoder.encode(id_user_string, "UTF-8")
                        +"&bonus=" +URLEncoder.encode(bonus_string, "UTF-8")
                        +"&address_start=" +URLEncoder.encode(address_string, "UTF-8")
                        +"&id_parking="+URLEncoder.encode(id_parking_string, "UTF-8");

                JSONArray jsonArray=ServerTask.askToServer(params,url);
                //gestisci JsonArray
                JSONObject jsonObjectId=jsonArray.getJSONObject(0);
                JSONObject jsonObjectControl=jsonArray.getJSONObject(1);// index 0 booking_id, index 1 control_status
                control=jsonObjectControl.getString("control");

                Log.i("cntr0",control);
                if (control.equals("OK")){   // non esegue l'if
                    currentReservation.id_booking=jsonObjectId.getString("booking_id");
                    currentReservation.parking_id=parametersAsyncs[0].id_parking;

                }
                 //avverti l'utente che il posto è stato occupato o non ha soldi

                //Mostrare apertura map_icona_panel_prenotazione
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
                    layoutReservation.setVisibility(View.VISIBLE);
                    //Percorso per NAVIGATORE
                    calcolaPercorso(new LatLng(station_selected.latitude, station_selected.longitude));
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

    //3- CLOSE RESERVATION, permette di chiudere una map_icona_panel_prenotazione
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
                    layoutReservation.setVisibility(View.INVISIBLE);
                    //Chiudi Navigatore (Cancella percorso)
                    removeRoute();
                    break;
                case "WRONG_PARKING":Toast.makeText(MapsActivity.this,getString(R.string.sorry),Toast.LENGTH_LONG).show();
                    break;
                case "ERROR":        Toast.makeText(MapsActivity.this,getString(R.string.error_close_res),Toast.LENGTH_LONG).show();
                    break;
            }

        }

    }


    //4-CHECK CURRENT RESERVATION, controlla se sul server è settata una map_icona_panel_prenotazione per l'user corrente, evita di perdere
    // la map_icona_panel_prenotazione se l'app viene chiusa
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
            if (currentReservation.id_booking!=null) return true;
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result){
            Log.i("icona",result.toString());
            if (result==true) layoutReservation.setVisibility(View.VISIBLE);

        }

    }

    // METODI CALLBACK:----------------------------------------------------------------------------

    //Callback method after startActivityForResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        switch (requestCode) {
            case SCANNER_REQUEST_CODE:  //GESTIONE EVENTI SCANNER
                if(resultCode==Activity.RESULT_OK){
                    Integer result = Integer.parseInt(data.getStringExtra("parking_code"));
                    Toast.makeText(MapsActivity.this, result.toString(), Toast.LENGTH_SHORT).show();
                    String id_user=mAuth.getUid();
                    Integer id_parking=result;
                    String id_booking=currentReservation.id_booking;// currentReservation.getId(); recupera id_booking da istanza currentReservation
                    CloseReservationParamsAsync paramsAsync= new CloseReservationParamsAsync(id_user,id_parking,id_booking,1);
                    new CloseReservation().execute(paramsAsync);
                }
                break;
            case PROFILE_REQUEST_CODE:  //GESTIONE DATI PROFILO
                if(resultCode==Activity.RESULT_OK){

                }
                break;
            case LocationRequest.PRIORITY_HIGH_ACCURACY:  //GESTIONE EVENTI LOCATION
                switch (resultCode) {
                    case Activity.RESULT_OK:    // BUG ANDROID, PROBLEMI CON HIGH ACCURACY
                        // All required changes were successfully made
                        Log.i(TAG, "GEOLOCALIZZAZIONE-5b: GPS attivato dall'utente");
                        setDeviceLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Log.i(TAG, "GEOLOCALIZZAZIONE-5b: Richiesta attivazione GPS respinta ");
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    //Callback dopo richiesta Permessi
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
            // VERIFICO PERMESSI ED IMPOSTO POSIZIONE
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
                Log.i(TAG,"GEOLOCALIZZAZIONE-2a: esito richiesta --> "+mLocationPermissionGranted);
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    //Converte svg in bitmap (per icone mappa)
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    //-----POP UP REDIRECT----------------------------------------------------------------------

    // Aggiorna e mostra il pannello di redirect se arriva la notifica
    private void    showRdrctPopup(@Nullable String id_parking,@Nullable String distance,@Nullable String address) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View customView;

        if (id_parking!=null){
            customView = layoutInflater.inflate(R.layout.map_redir_popup, null);
            TextView tv_panel_park= (TextView) customView.findViewById(R.id.tv_park_id);
            tv_panel_park.setText(id_parking);
            TextView tv_panel_dist= (TextView) customView.findViewById(R.id.tv_dist_id);
            tv_panel_dist.setText(distance+"m");
            TextView tv_panel_address= (TextView) customView.findViewById(R.id.tv_address_id);
            tv_panel_address.setText(address);
        }
        else customView = layoutInflater.inflate(R.layout.map_no_redir_popup, null);
        builder.setView(customView);
        builder.setCancelable(false);
        popup=builder.create();
        popup.show();

    }

    //METODI DEI BOTTONI DEL POPUP DI REINDIRIZZAMENTO
    public void onEndReservationForFree(View view){
        String id_user=mAuth.getUid();
        Integer id_parking=currentReservation.parking_id;
        String id_booking=currentReservation.id_booking;// currentReservation.getId(); recupera id_booking da istanza currentReservation
        CloseReservationParamsAsync paramsAsync= new CloseReservationParamsAsync(id_user,id_parking,id_booking,0);
        new CloseReservation().execute(paramsAsync);
        popup.cancel();

    }
    public void onRedirect(View view){
        String id_user=mAuth.getUid();
        Integer id_parking=currentReservation.parking_id;
        String id_booking=currentReservation.id_booking;
        CloseReservationParamsAsync paramsAsync= new CloseReservationParamsAsync(id_user,id_parking,id_booking,2);
        new CloseReservation().execute(paramsAsync);

        if (new_parking_rdrct!=null){
        Integer id_new_parking=Integer.valueOf(new_parking_rdrct);
        OpenReservationParamsAsync paramsAsyncOpen=new OpenReservationParamsAsync(mAuth.getUid(),id_new_parking,"//FIttizia",1);
        new OpenReservation().execute(paramsAsyncOpen);
        //TO DO:Chiama metodo per reindirizzare navigatore
        }
        popup.cancel();

    }

    //--------NAVIGATORE---------------------------------------
     /*
    CALCOLO PERCORSO
     */
    public void calcolaPercorso(LatLng destinazione){
        LatLng posizioneUtente = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
        polylinesPoints = new LinkedList<>();
        polylinesPoints.clear();
        polylinesPoints.add(posizioneUtente); //Parte dall'inizio

        new DownloadTask().execute("https://maps.googleapis.com/maps/api/directions/json?origin=" +
                posizioneUtente.latitude+","+posizioneUtente.longitude +
                "&destination=" +
                destinazione.latitude+","+destinazione.longitude +
                "&key=AIzaSyBdcgZSbXkUcPAdylZgfAuK347e7J093WE");
    }


    /*
    TASK ASINCRONO DI DOWNLOAD COORDINATE
     */
    private class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            URL url;
            HttpsURLConnection urlConnection;

            try {
                url = new URL(strings[0]);
                urlConnection = (HttpsURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while(data != -1){
                    char cur = (char) data;
                    result += cur;
                    data = reader.read();
                }

                Log.i("mylog", result);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String routes = jsonObject.getString("routes");
                JSONArray arrayRoutes = new JSONArray(routes);
                JSONObject primaRoute = arrayRoutes.getJSONObject(0);

                String legs = primaRoute.getString("legs");
                JSONArray arrayLegs = new JSONArray(legs);
                JSONObject primaLeg = arrayLegs.getJSONObject(0);

                String steps = primaLeg.getString("steps");
                JSONArray arraySteps = new JSONArray(steps);

                for(int i=0; i<arraySteps.length(); i++){
                    JSONObject step = arraySteps.getJSONObject(i);
                    String lat = step.getJSONObject("end_location").getString("lat");
                    String lon = step.getJSONObject("end_location").getString("lng");
                    Log.i("mylog", lat+" "+lon);

                    polylinesPoints.add(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon)));

                }

                drawPolylines();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void drawPolylines(){
        PolylineOptions plo = new PolylineOptions();
        for(LatLng point : polylinesPoints){
            plo.add(point);
            plo.color(Color.CYAN);
            plo.width(20);
        }

        polyline = mMap.addPolyline(plo);

    }

    private void removeRoute() {
        polyline.remove();
        polylinesPoints.clear();
    }

}
