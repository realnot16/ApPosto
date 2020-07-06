package com.example.project.favourites;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import androidx.annotation.NonNull;

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

//VISUALIZZA LE PREFERENZE, PERMETTE DI ELIMINARNE UNA O DI MODIFICARE L'ETICHETTA
public class FavouritesActivity extends ListActivity {

    private final static String FILE_PATH = "FavouritesMapFile.txt";
    private static final String TAG = "FavouritesActivity" ;
    private final static int DELETE_MENU_OPTION = 1;
    private final static int UPDATE_MENU_OPTION = 2;
    private static final int DELETE_ALL_MENU_OPTION = 3 ;
    private Favourite modifiedFavourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        //menu contestuale
        registerForContextMenu(getListView());

        //loadTestMapOnFile();
        //estraggo la mappa da file
        Map<String,Favourite> favMap= loadFavourite();
        Log.i(TAG, "Ho scaricato la mappa da file. Elementi contenuti:"+ favMap.size());

    }

    private void loadTestMapOnFile() {
        Map<String,Favourite> fasulla= new HashMap<>();
        fasulla.put("casa", new Favourite("casa", 12345, 12345));
        fasulla.put("lavoro", new Favourite("lavoro", 12346, 12346));

        saveMapOnFile(fasulla);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        int group = Menu.FIRST;
        //aggiungo le voci, associandole alle costanti che tengono traccia della scelta effettuata
        menu.add(group, UPDATE_MENU_OPTION, Menu.FIRST , R.string.favourite_update_option);
        menu.add(group, DELETE_MENU_OPTION, Menu.FIRST+ 1, R.string.favourite_delete_option);
        menu.add(group, DELETE_ALL_MENU_OPTION, Menu.FIRST+ 2, R.string.favourite_delete_all_option);

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Log.i(TAG, "Posizione selezionata:"+ info.position);
        Favourite selectedItem= (Favourite) getListView().getItemAtPosition(info.position);
        Log.i(TAG, "Preferito elezionato:"+ selectedItem.toString());

        switch (item.getItemId()) {
            case DELETE_MENU_OPTION:
                //cancello e aggiorno map su file
                deleteFavourite(selectedItem);
                //aggiorno listview
                loadFavourite();//automaticamente aggiorna la listview, contiene uploadListView()
                break;

            case UPDATE_MENU_OPTION:
                Log.i(TAG, "Update");
                //salvo coordinate
                modifiedFavourite= new Favourite("noninserita", selectedItem.getLat(), selectedItem.getLon());
                //elimino il vecchio
                deleteFavourite(selectedItem);
                //dialog per inserire nuova etichetta
                AlertDialog dialog= createEd().create();
                dialog.show();
                break;

            case DELETE_ALL_MENU_OPTION:
                deleteFile();
                saveMapOnFile(new HashMap<String, Favourite>());
                loadFavourite(); //automaticamente aggiorna la listview, contiene uploadListView()
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
            getListView().setAdapter(a);
        }else{
            Log.i(TAG, "Lista da caricare su ListView Vuota");
            ArrayAdapter a= new ArrayAdapter(this, R.layout.favourites_row_layout, R.id.tv_favourite_label,favList);
            getListView().setAdapter(a);
            Toast.makeText(this, R.string.favourite_not_found, Toast.LENGTH_LONG);
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
            Toast.makeText(this, R.string.favourite_repeated, Toast.LENGTH_LONG);
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
            //tvMessage.setText("Nessun luogo salvato fra i preferiti");
        }else{
            if(preferiti.containsKey(toDelete.getLabel())){
                preferiti.remove(toDelete.getLabel());
                //salvo il file
                saveMapOnFile(preferiti);
                //tvMessage.setText("Hai rimosso la preferenza "+etichetta);
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
//dialog peer modifica etichetta
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
                Log.i("dialog", "Label inserita: "+inputLabel.getText().toString());
                modifiedFavourite.setLabel(inputLabel.getText().toString());
                saveFavourite(modifiedFavourite);
                loadFavourite();
            }
        });
        builder.setNegativeButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        return builder;
    }
}
