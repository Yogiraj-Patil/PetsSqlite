package biz.coolpage.yogiraj.pets;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import biz.coolpage.yogiraj.pets.data.PetDbHelper;
import biz.coolpage.yogiraj.pets.data.PetsContract;
import biz.coolpage.yogiraj.pets.data.PetsContract.PetEntry;

public class CatalogActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    private static final int PET_LOADER = 1001;
    ListView listView;
    PetCursorAdapter mpetCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        listView = (ListView) findViewById(R.id.listview);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CatalogActivity.this, EditorActivity.class));
            }
        });

        View emptyView = findViewById(R.id.empty_View);
        listView.setEmptyView(emptyView);
        //mDbHelper = new PetDbHelper(this);
        // displayDatabaseInfo();

        mpetCursorAdapter = new PetCursorAdapter(this, null);
        listView.setAdapter(mpetCursorAdapter);
        //just kick of the Loader
        getLoaderManager().initLoader(PET_LOADER, null, this);
        listView.setOnItemClickListener(this);

    }


  /*  private void displayDatabaseInfo() {
        //PetDbHelper mDbHelper = new PetDbHelper(this);
        // SQLiteDatabase db = mDbHelper.getReadableDatabase(); //no need cause i am using now content provider //Cursor cursor = db.rawQuery("SELECT * FROM " + PetEntry.TABLE_NAME, null);
        String[] projection = {PetEntry._ID, PetEntry.COLOUMN_PET_BREED, PetEntry.COLOUMN_PET_NAME,
                PetEntry.COLOUMN_PET_GENDER, PetEntry.COLOUMN_PET_WEIGHT};
        Cursor cursor = db.query(PetEntry.TABLE_NAME, null , null, null, null, null, null);//no need using Content provider
        //For knowing the column index
        Cursor cursor = getContentResolver().query(PetEntry.CONTENT_URI, projection, null,
                null, null, null);
        PetCursorAdapter petCursorAdapter = new PetCursorAdapter(this,cursor);
        listView.setAdapter(petCursorAdapter);
         int idIndex = cursor.getColumnIndex(PetEntry._ID);
        int nameIndex = cursor.getColumnIndex(PetEntry.COLOUMN_PET_NAME);
        int breedIndex = cursor.getColumnIndex(PetEntry.COLOUMN_PET_BREED);
        int genderIndex = cursor.getColumnIndex(PetEntry.COLOUMN_PET_GENDER);
        int weightIndex = cursor.getColumnIndex(PetEntry.COLOUMN_PET_WEIGHT);
        while (cursor.moveToNext()) {
            textview.append(("\n" + cursor.getInt(idIndex) + " - "
                    + cursor.getString(nameIndex) + " - "
                    + cursor.getString(breedIndex) + " - "
                    + cursor.getInt(genderIndex) + " - "
                    + cursor.getLong(weightIndex)));}
        try {} finally {
            cursor.close();
        }
    }*/


    private void insertDummyPet() {
        //PetDbHelper mDbHelper = new PetDbHelper(this);    SQLiteDatabase database = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLOUMN_PET_NAME, "Toto");
        values.put(PetEntry.COLOUMN_PET_BREED, "Terrier");
        values.put(PetEntry.COLOUMN_PET_GENDER, 1);
        values.put(PetEntry.COLOUMN_PET_WEIGHT, 7);
        long rowId;                                                                                      //rowId = database.insert(PetEntry.TABLE_NAME, null, values);   Toast.makeText(this,"Row Id is: "+rowId,Toast.LENGTH_SHORT).show();
        Uri use = getContentResolver().insert(PetEntry.CONTENT_URI, values);

    }

/*
    //when user comes on this activity it need to refresh text on activity therefore onStart is called
    @Override
    protected void onStart() {
        super.onStart();
        //displayDatabaseInfo();
       //after loader no need onStart() method
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertDummyPet();
                //displayDatabaseInfo();
                return true;

            case R.id.action_delete_all_entries:
                deleteall();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                PetEntry._ID,
                PetEntry.COLOUMN_PET_NAME,
                PetEntry.COLOUMN_PET_BREED
        };
        //this CursorLoader execute ContentProvider query method on a background thread
        return new CursorLoader(this,     //passing parent activity context
                PetEntry.CONTENT_URI,            //passinfg uri
                projection,                      //colums to be selected
                null,                      //clause and down argument
                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mpetCursorAdapter.swapCursor(data);//this method call newView and BindView method on petCursorAdapter
        //means we just pass a data to petCursorAdapter
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mpetCursorAdapter.swapCursor(null); //it pervent memory leaks and close/clear adapter
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Uri currentPetUri = ContentUris.withAppendedId(PetEntry.CONTENT_URI, id);
        Intent intent = new Intent(this, EditorActivity.class);
        intent.setData(currentPetUri);
        startActivity(intent);

    }

    private void deleteall() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("You Really want to delete all data form Device");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Thread.sleep(700);
                } catch (Exception e) {
                }

                confirmDecision();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void confirmDecision() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete All Data");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getContentResolver().delete(PetEntry.CONTENT_URI, null, null);
            }
        });
        builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
