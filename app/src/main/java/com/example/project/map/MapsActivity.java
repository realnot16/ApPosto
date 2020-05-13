package com.example.project.map;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.project.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

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


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SearchView searchView;
    private PopupWindow popUp;
    private DrawerLayout drawerLayout;
    private String placeAutocompleteAPIkey;
    private Integer filter_destination_meter=1200;
    private final static int  REQUESTCODEFROMSCANNER=2;
    private final static int MY_CAMERA_REQUEST_CODE=100;
    FloatingActionButton qrButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
        getSupportActionBar().setTitle("Cerca Un Parcheggio"); // for set actionbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // for add back arrow in action bar
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.map_ic_menu_black_24dp);
        getSupportActionBar().setHomeButtonEnabled(true);*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout_main);
        // IMPOSTO LA TOOLBAR E LA COLLEGO AL NAVIGATION DRAWER
        Toolbar toolbar = findViewById(R.id.toolbar);
        SearchView searchView = findViewById(R.id.searchview_id);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // for add back arrow in action bar
        } catch (Exception e) {
            e.printStackTrace();
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.map_ic_menu_black_24dp);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("");
        setNavigationDrawer(toolbar);
        Drawable filterIcon = getDrawable(R.drawable.map_ic_filter_list_black_24dp);
        toolbar.setOverflowIcon(filterIcon);
        //searchView.setLayoutParams(new ActionBar.LayoutParams(Gravity.RIGHT));












        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        try {
            mapFragment.getMapAsync(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        qrButton= findViewById(R.id.floatingQrButton);
        qrButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MapsActivity.this,   //controlla che il permesso sia garantito
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MapsActivity.this,   //richiesta di permesso all'utente
                            new String[]{Manifest.permission.CAMERA},
                            MY_CAMERA_REQUEST_CODE);
                }
                else{
                    Intent intent=new Intent(MapsActivity.this, ScannerActivity.class);
                    startActivityForResult(intent,REQUESTCODEFROMSCANNER);

                }

            }
        });






/*
        //AUTOCOMPLETAMENTO SEARCH BAR
        placeAutocompleteAPIkey=getString(R.string.placeAutocomplete); //Initialize Places. For simplicity, the API key is hard-coded.
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), placeAutocompleteAPIkey);
        }
        PlacesClient placesClient = Places.createClient(this); // Create a new Places client instance.
*/
/*
        //SEARCH  BAR, da implementare
        // da impostare aggiungendo le destinazioni possibili
        FloatingSearchView fSearchView =(FloatingSearchView) findViewById(R.id.floating_search_view);
        popUp = new PopupWindow(this);

        fSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                //get suggestions based on newQuery
                //pass them on to the search view
                //fSearchView.swapSuggestions(newSuggestions);

                //impostare suggerimenti effettuando una richiesta a google per avere tutte le vie etc.
            }
        });

        fSearchView.setOnLeftMenuClickListener(new FloatingSearchView.OnLeftMenuClickListener() {
            @Override
            public void onMenuOpened() {
                View layout = mapFragment.getView();
                popUp.showAtLocation(layout, Gravity.BOTTOM,10,10);
                popUp.setContentView(findViewById(R.id.provaMenu));
            }

            @Override
            public void onMenuClosed() {
                popUp.dismiss();

            }
        });

*/
    }

    //COLLEGO IL NAVIGATION LAyoUT ALLA TOOLBAR
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map_filters_menu, menu);
        return true;
    }
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() ==android.R.id.home){
            Log.i("ActivityResult","CI SIAMO");
            LayoutInflater inflater = MapsActivity.this.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            View popView =
            PopupWindow popW = new PopupWindow();
           // showPopup(findViewById(R.id.searchview_id));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    public void showPopup(View v) {
        Log.i("ActivityResult","CI SIAMO2");
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        Log.i("ActivityResult","CI SIAMO3");
        inflater.inflate(R.menu.map_navigation_menu, popup.getMenu());
        Log.i("ActivityResult","CI SIAMO4");
        popup.show();
        Log.i("ActivityResult","CI SIAMO5");
    }*/



    private GoogleMap setMap(GoogleMap map) {
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        //AGGIUNGERE ALTRE MODIFICHE -> POSIZIONE INIZIALE, ZOOM INIZIALE


        double lat_start= 45.070841;//fittizie: SOSTITUIRE CON QUELLE DEL DISPOSITIVO
        double long_start=7.668552;//fittizie
        String city="Torino";
        ParametersAsync parametersAsync=new ParametersAsync(lat_start,long_start,city);
        new LoadStations().execute(parametersAsync);

        return mMap;
    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap = setMap(mMap);
        // Add a marker in Sydney and move the camera
        LatLng start = new LatLng(45.0781,7.6761);
        mMap.addMarker(new MarkerOptions().position(start).title("TOpark"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(start));

    }

    //STATION, da implementare
    //classe contente i dettagli di tutte le stazioni
    //mancano alcuni dettagli, da recuperare dal db
    private class Station {
        String latitude;
        String longitude;
        String city;
        String name;

        public Station() {
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    //LOAD STATION, da implementare
    //permette di acquisire tutte le stazioni nell'area visualizzata durante il primo accesso alla mappa
    private  class LoadStations extends AsyncTask<ParametersAsync,Void,Boolean> {
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
                String long_dest_string =String.valueOf(parametersAsyncs[1].longitude);
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

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.ISO_8859_1), 8);
                StringBuilder sb = new StringBuilder();
                String line;
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

                    String outputString = "";
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

                    String outputString = "";
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==REQUESTCODEFROMSCANNER && resultCode==Activity.RESULT_OK){
                String result=data.getStringExtra("parking_code");
            Toast.makeText(MapsActivity.this, result,Toast.LENGTH_SHORT).show();

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_CAMERA_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent=new Intent(MapsActivity.this,ScannerActivity.class);
                    startActivityForResult(intent,REQUESTCODEFROMSCANNER);

                } else {
                    Toast.makeText(MapsActivity.this, R.string.permcameradenied,Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }



}


//Search bar che fa partire la ricerca dalla tastiera (settando imeOption=activitysearch su xml)
//e che risponde all'evento QueryTextSubmit sparando il metodo DbRequest che comunica con api google
// per la ricerca delle coordinate relative alla ricerca e contatta il nostro DB in cloud per farsi ritornare
//un json di parcheggi idonei

        /*
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Aggiungere API google per ricavare coordinate della destinazione con la query
                double lat_dest= 45.070841;//fittizie: dovrebbero essere ritornate da google api
                double long_dest=7.668552;//fittizie
                String city="Torino";//fittizia
                ParametersAsync parametersAsync=new ParametersAsync(lat_dest,long_dest,city);
                new DbAsyncRequest().execute(parametersAsync);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        */