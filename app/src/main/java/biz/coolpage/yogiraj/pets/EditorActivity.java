package biz.coolpage.yogiraj.pets;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

//import biz.coolpage.yogiraj.pets.data.PetsContract;
//The more specific Way to handle Contaract class Constant is importing as below else above
import java.lang.ref.PhantomReference;
import java.nio.file.Path;

import biz.coolpage.yogiraj.pets.data.PetDbHelper;
import biz.coolpage.yogiraj.pets.data.PetsContract.PetEntry;//this is more specific way Now go and see genders

import static android.text.TextUtils.isEmpty;


public class EditorActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, View.OnTouchListener {


    private static final String LOG = "Message";
    private static final int PET_LOADER = 0;
    private EditText mNameEditText;
    private EditText mBreedEditText;
    private EditText mWeightEditText;
    private Spinner mGenderSpinner;
    private int mGender = 0;
    //private PetDbHelper mDbHelper;
    private Uri path;
    private int weight = 0;
    private String breedName = "Unknown Pet";
    private boolean mPetHasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        path = intent.getData();
        try {
            setTitle(path);
        } catch (Exception e) {
            Log.e(LOG, "Thread Exception Occurs Exception is " + e.getMessage());
        }


        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        mNameEditText.setOnTouchListener(this);
        mBreedEditText.setOnTouchListener(this);
        mWeightEditText.setOnTouchListener(this);
        mGenderSpinner.setOnTouchListener(this);

        //mDbHelper = new PetDbHelper(this);

        setupSpinner();
    }


    public void setTitle(Uri path) throws Exception {
        if (path == null) {
            setTitle("Add a Pet");
            invalidateOptionsMenu();//this will automatically invoked onPrepareOptionMenu method instead of 'onCreateOptionMenu'
        } else {
            setTitle("Edit Pet");
            Log.e(LOG, "Setting Title\nPath is: " + path);
            getSupportLoaderManager().initLoader(PET_LOADER, null, this);
            Log.e(LOG, "Initilize Loader Manager");


        }
    }


    private void setupSpinner() {

        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);


        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);


        mGenderSpinner.setAdapter(genderSpinnerAdapter);


        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetEntry.GENDER_FEMALE;
                        // mGender = PetsContract.PetEntry.GENDER_FEMALE; // Female
                    } else {
                        mGender = PetEntry.GENDER_UNKNOWN;
                        //For More consistant way import full path ie. import biz.coolpage.yogiraj.pets.data.PetsContract; to the new way is i.e: import biz.coolpage.yogiraj.pets.data.PetsContract.PetsEntry; // mGender = PetsContract.PetEntry.GENDER_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 0; // Unknown
            }
        });
    }

    private void savedata() {


       /* SQLiteDatabase database = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLOUMN_PET_NAME,mNameEditText.getText().toString().trim());
        values.put(PetEntry.COLOUMN_PET_BREED,mBreedEditText.getText().toString().trim());
        values.put(PetEntry.COLOUMN_PET_GENDER,mGender);
        int weight_breed = Integer.parseInt(mWeightEditText.getText().toString().trim());
        values.put(PetEntry.COLOUMN_PET_WEIGHT,weight_breed);
        long rowId = database.insert(PetEntry.TABLE_NAME, null, values);
        Toast.makeText(this,"Row id: "+rowId, Toast.LENGTH_SHORT).show();*/

        ContentValues values = new ContentValues();
        values.put(PetEntry.COLOUMN_PET_NAME, mNameEditText.getText().toString().trim());

        values.put(PetEntry.COLOUMN_PET_BREED, breedName);

        values.put(PetEntry.COLOUMN_PET_GENDER, mGender);

        values.put(PetEntry.COLOUMN_PET_WEIGHT, weight);


        if (path == null) {

            Uri use = getContentResolver().insert(PetEntry.CONTENT_URI, values);
        } else {
            int rows = getContentResolver().update(path, values, null, null);
            Toast.makeText(this, rows + " Rows get affected", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteData() {
        int rows;
        if (path == null) {
            return;
        } else {

            rows = getContentResolver().delete(path, null, null);
            Toast.makeText(this, rows + " Rows get deleted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (path == null) {
            MenuItem item = menu.findItem(R.id.action_delete);
            item.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                if (TextUtils.isEmpty(mNameEditText.getText().toString())) {
                    mNameEditText.setError("Enter Name of pet");
                } else {
                    if (!TextUtils.isEmpty(mBreedEditText.getText().toString())) {
                        breedName = mBreedEditText.getText().toString().trim();
                    }
                    if (mGender == PetEntry.GENDER_UNKNOWN) {
                        Toast.makeText(this, "Check Gender of Pet", Toast.LENGTH_SHORT).show();
                    } else {
                        String weightString = mWeightEditText.getText().toString();
                        if (TextUtils.isEmpty(weightString)) {
                            weight = 0;
                        } else {
                            weight = Integer.parseInt(mWeightEditText.getText().toString().trim());
                        }
                        savedata();
                        //Via calling finish method we return to parent activity ie. CatalogActivity
                        finish();
                    }

                }


                return true;

            case R.id.action_delete:
                deleteData();
                //Via calling finish method we return to parent activity ie. CatalogActivity
                finish();
                return true;

            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if (!mPetHasChanged) {
                    // Navigate back to parent activity (CatalogActivity)
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                showUnsavedChangeDialogBox();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(LOG, "Invoke Oncreate Loader");
        String[] projection = {
                PetEntry._ID,
                PetEntry.COLOUMN_PET_NAME,
                PetEntry.COLOUMN_PET_BREED,
                PetEntry.COLOUMN_PET_GENDER,
                PetEntry.COLOUMN_PET_WEIGHT
        };
        return new CursorLoader(this,
                path,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(LOG, "Invoke OnLoadFinished Loader");
        if (data == null || data.getCount() < 1) {
            return;
        }
        if (data.moveToFirst()) {
            int nameColumnIndex = data.getColumnIndex(PetEntry.COLOUMN_PET_NAME);
            int breedColumnIndex = data.getColumnIndex(PetEntry.COLOUMN_PET_BREED);
            int genderColumnIndex = data.getColumnIndex(PetEntry.COLOUMN_PET_GENDER);
            int weightColumnIndex = data.getColumnIndex(PetEntry.COLOUMN_PET_WEIGHT);

            mNameEditText.setText(data.getString(nameColumnIndex));
            mBreedEditText.setText(data.getString(breedColumnIndex));
            mWeightEditText.setText(String.valueOf(data.getLong(weightColumnIndex)));
            mGenderSpinner.setSelection(data.getInt(genderColumnIndex));


        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mNameEditText.setText("");
        mBreedEditText.setText("");
        mWeightEditText.setText("");
        mGenderSpinner.setSelection(0);

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mPetHasChanged = true;
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!mPetHasChanged) {
            super.onBackPressed();
            return;//because we don't write else so code will further execute therefore 'return' to stop the further code execuation if condition 'if' is true
        }
/*
        DialogInterface.OnClickListener discardButton = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //here user Clicked discard button so close current activity
                finish();
            }
        };*/

        showUnsavedChangeDialogBox();
    }

    private void showUnsavedChangeDialogBox() {
        //creating alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You really want to Discard changes?");
        builder.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("Keep", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //user pressed "Keep" button so just dismiss the dialog box
                // and continue editing

                dialog.dismiss();

            }
        });

        //invoking AlertDialog for displaying on display
        AlertDialog dialog = builder.create();
        dialog.show();


    }
}