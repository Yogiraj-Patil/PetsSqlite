package biz.coolpage.yogiraj.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import biz.coolpage.yogiraj.pets.data.PetsContract.PetEntry;

/**
 * Created by Gaurav on 13-03-2018.
 */

public class PetDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = PetDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "shelter.db";
    private static final int DATABASE_VERSION = 2;

    //if constructor not set public it cant access in catalogActivity while in object of class creation
    public PetDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PETS_TABLE = "CREATE TABLE " + PetEntry.TABLE_NAME + " ( "
                + PetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PetEntry.COLOUMN_PET_NAME + " TEXT NOT NULL, "
                + PetEntry.COLOUMN_PET_BREED + " TEXT, "//can be null
                + PetEntry.COLOUMN_PET_GENDER + " INTEGER NOT NULL, "
                + PetEntry.COLOUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 1 );";

        db.execSQL(SQL_CREATE_PETS_TABLE);
    }


    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
