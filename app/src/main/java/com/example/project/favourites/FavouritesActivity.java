package com.example.project.favourites;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.project.R;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//VISUALIZZA LE PREFERENZE, PERMETTE DI ELIMINARNE UNA, DI ELIMINARLE TUTTE O DI MODIFICARE L'ETICHETTA
public class FavouritesActivity extends AppCompatActivity {

    private final static String FILE_PATH = "FavouritesMapFile.txt";
    private static final String TAG = "FavouritesActivity" ;
    private final static int DELETE_MENU_OPTION = 1;
    private final static int UPDATE_MENU_OPTION = 2;
    private static final int DELETE_ALL_MENU_OPTION = 3 ;
    private ListView list_view;
    private Favourite favToUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        initUI();
        list_view = (ListView) findViewById(android.R.id.list);

        //menu contestuale
        registerForContextMenu(list_view);

        //estraggo la mappa da file
        Map<String,Favourite> favMap= loadFavourite();
        Log.i(TAG, "Ho scaricato la mappa da file. Elementi contenuti:"+ favMap.size());

    }

    private void initUI() {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.favourite_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setHomeButtonEnabled(true);
        Drawable filterIcon = getDrawable(R.drawable.ic_more_vert_white_24dp);
        toolbar.setOverflowIcon(filterIcon);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.favourite_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                Log.i(TAG, "Back, redirect a Maps");
                finish();
                return true;
            case R.id.action_menu:
                deleteFile();
                saveMapOnFile(new HashMap<String, Favourite>());
                loadFavourite(); //automaticamente aggiorna la listview, contiene uploadListView()
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        int group = Menu.FIRST;
        //aggiungo le voci, associandole alle costanti che tengono traccia della scelta effettuata
        menu.add(group, UPDATE_MENU_OPTION, Menu.FIRST , R.string.favourite_update_option);
        menu.add(group, DELETE_MENU_OPTION, Menu.FIRST+ 1, R.string.favourite_delete_option);

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Log.i(TAG, "Posizione selezionata:"+ info.position);
        Favourite selectedItem= (Favourite) list_view.getItemAtPosition(info.position);
        Log.i(TAG, "Preferito elezionato:"+ selectedItem.toString());

        switch (item.getItemId()) {
            case DELETE_MENU_OPTION:
                //cancello item e aggiorno map su file
                deleteFavourite(selectedItem);
                //aggiorno listview
                loadFavourite();//automaticamente aggiorna la listview, contiene uploadListView()
                break;

            case UPDATE_MENU_OPTION:
                Log.i("update", "Ho cliccato modifica etichetta");
                //salvo Favourite da aggiornare
                favToUpdate= selectedItem;
                Log.i("update", "Ho salvato il preferito da aggiornare "+favToUpdate.toString());
                //dialog per inserire nuova etichetta
                AlertDialog dialog= createEd().create();
                dialog.show();
                break;
            default:

                return super.onContextItemSelected(item);

        }

        return true;

    }

    //METODO PER ESTRARRE LA MAPPA DA FILE
    public Map<String,Favourite> loadFavourite() {
        Log.i(TAG, "sono dentro load favourites.");

        Map<String,Favourite> result= new HashMap<String, Favourite>();
        try {
            FileInputStream fis = openFileInput(FILE_PATH);
            ObjectInputStream objectInput= new ObjectInputStream(fis);
            result = (HashMap<String, Favourite>) objectInput.readObject();
            objectInput.close();
            Log.i(TAG, "Ho estratto da file la mappa delle preferenze. size:"+result.size());

        } catch (EOFException eof) {
            System.out.println("Reached end of file");
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        updateListView(result);


        return result;
    }

    private void updateListView(Map<String, Favourite> map) {
        List <Favourite> favList= new ArrayList<>(map.values());
        //aggiorno List View
        if(!favList.isEmpty()){
            ArrayAdapter a= new ArrayAdapter(this, R.layout.favourites_row_layout, R.id.tv_favourite_label,favList);
            list_view.setAdapter(a);
        }else{
            Log.i(TAG, "Lista da caricare su ListView Vuota");
            ArrayAdapter a= new ArrayAdapter(this, R.layout.favourites_row_layout, R.id.tv_favourite_label,favList);
            list_view.setAdapter(a);
            Toast.makeText(this, R.string.favourite_not_found, Toast.LENGTH_LONG).show();
        }
    }


    //Salvare nuovo preferito su file
    public void saveFavourite(Favourite favourite) {
        //DA MODIFICARE, DEVO PRIMA OTTENERE LA MAPPA, VERIFICARE SE REGISTRATO UN INDIRIZZO PER LA LABEL INSERITA, POI AGGIUNGERE E AGGIORNARE
        //estraggo la mappa
        Map<String,Favourite> preferitiMap= new HashMap(loadFavourite());

        if(!preferitiMap.containsKey(favourite.getLabel())){//se non è salvato nessun indirizzo per quell'etichetta, la salvo
            preferitiMap.put(favourite.getLabel(),favourite);
            saveMapOnFile(preferitiMap);
        }else{//altrimenti
            Toast.makeText(this, R.string.favourite_repeated, Toast.LENGTH_LONG).show();
        }

    }
    //METODO PER SALVARE LA MAPPA SU FILE
    private void saveMapOnFile(Map<String, Favourite> preferiti) {
        Log.i(TAG, "sono dentro save map on file");
        try {
            FileOutputStream fos= openFileOutput(FILE_PATH, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(preferiti);
            oos.close();

            Log.i(TAG, "Ho caricato le preferenze");

        } catch (IOException e) {
            e.printStackTrace();
            //tvMessage.setText("Errore caricamento su file");
        }
    }

    public void deleteFavourite(Favourite toDelete) {

        //per eliminare, ottengo la mappa, la aggiorno e la ricarico su file
        Map<String,Favourite> preferiti= new HashMap(loadFavourite());
        if(preferiti.isEmpty()){
            Log.i(TAG,"Nessun luogo salvato fra i preferiti");
        }else{
            if(preferiti.containsKey(toDelete.getLabel())){
                preferiti.remove(toDelete.getLabel());
                //salvo il file
                saveMapOnFile(preferiti);
            }
        }

    }

    private void deleteFile() {
        File file = getFileStreamPath(FILE_PATH);
        boolean deleted = false;
        try {
            deleted = file.delete();
        } catch (SecurityException se) {
            se.printStackTrace();
            deleted= false;
        }

        Log.i(TAG, "Ho eliminato il file");
    }
//dialog per modifica etichetta
    private AlertDialog.Builder createEd() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_modify_fav_title);

        final EditText inputLabel = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        inputLabel.setLayoutParams(lp);
        builder.setView(inputLabel);

        builder.setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.i("update", "Ho cliccato INVIA");
                //salvo la nuova etichetta
                String labelNew= inputLabel.getText().toString();
                if(labelNew.trim().equals("") || labelNew.isEmpty()){
                    Log.i("update", "Etichetta vuota");
                    Toast.makeText(getApplicationContext(), R.string.label_empty, Toast.LENGTH_LONG).show();
                    return;
                }else {
                    Log.i("update", "La nuova etichetta è: " + labelNew);
                    //chiamo la funzione per aggiornare la mappa sul file
                    changeLabel(labelNew, favToUpdate);
                }

            }
        });
        builder.setNegativeButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        return builder;
    }
    //per aggiornare un'etichetta di un preferito già salvato,
    //passo la nuova etichetta e il vecchio oggetto
    private void changeLabel(String labelNew, Favourite toUpdate){
        Map<String, Favourite> map= loadFavourite();
        Favourite nuovo= new Favourite(labelNew, toUpdate.getLat(), toUpdate.getLon());
        if(map.containsKey(labelNew)){
            Log.i("update", "Ho inserito un'etichetta già presente");
            Toast.makeText(this, R.string.favourite_repeated, Toast.LENGTH_LONG).show();
            return;
        }else{
            Log.i("update", "Ho inserito un'etichetta corretta");
            Log.i("update", "Mappa non aggiornata: size "+map.size());
            //elimino vecchio da mappa
            map.remove(toUpdate.getLabel());
            Log.i("update", "Mappa meno vecchio elemento: size "+map.size());
            //aggiungo nuovo a mappa
            map.put(nuovo.getLabel(), nuovo);
            Log.i("update", "Mappa con nuovo elemento: size "+map.size());
            //carico mappa
            saveMapOnFile(map);
        }

        updateListView(loadFavourite());

    }
}
