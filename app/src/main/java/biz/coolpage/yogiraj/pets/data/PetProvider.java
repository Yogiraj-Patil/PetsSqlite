package biz.coolpage.yogiraj.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by gaurav on 7/4/18.
 */

public class PetProvider extends ContentProvider {


    public static final String Log_E = PetProvider.class.getSimpleName();
    private static final int PETS = 100;
    private static final int PET_ID = 101;
    private static final int INSERT_PETS = 102;
    private static final String LOG = "Message";
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(PetsContract.CONTENT_AUTHORITY, PetsContract.PATH_PET, PETS);

        sUriMatcher.addURI(PetsContract.CONTENT_AUTHORITY, PetsContract.PATH_PET + "/#", PET_ID);

    }

    private PetDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new PetDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase mSQLiteDatabase = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {

            case PETS:

                cursor = mSQLiteDatabase.query(PetsContract.PetEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);

                break;

            case PET_ID:
                Log.d(LOG, "Reached To PET_ID query");

                selection = PetsContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri)/*Converts the last path segment to a long.
                 and the long conversion of the last segment or -1 if the path is empty  */)};
                cursor = mSQLiteDatabase.query(PetsContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot Query unknown URI " + uri);


        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }


    @Nullable
    @Override
    //The purpose of this method is to return a String that describes the type of the data stored at the input Uri. This String is known as the MIME type, which can also be referred to as content type.
    //One use case where this functionality is important is if you’re sending an intent with a URI set on the data field. The Android system will check the MIME type of that URI to determine which app component on the device can best handle your request.
    public String getType(@NonNull Uri uri) {

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetsContract.PetEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetsContract.PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " With match " + match);
        }
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        sanityCheck(values);

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, values);
            //above line is return there there for 'break' gives unreachable error  break;
            default:
                throw new IllegalArgumentException("Unknown Insert Query URI " + uri);
        }
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                getContext().getContentResolver().notifyChange(uri, null);
                return database.delete(PetsContract.PetEntry.TABLE_NAME, selection, selectionArgs);

            case PET_ID:
                selection = PetsContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                getContext().getContentResolver().notifyChange(uri, null);
                return database.delete(PetsContract.PetEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Could not Possible to perform Delete Operation " + uri);
        }
    }


    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        if (values.size() == 0) {
            //if no values to update then dont try to update
            return 0;
        }
        if (values.containsKey(PetsContract.PetEntry.COLOUMN_PET_NAME)) {
            String name = values.getAsString(PetsContract.PetEntry.COLOUMN_PET_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Name Requires");
            }
        }
        if (values.containsKey(PetsContract.PetEntry.COLOUMN_PET_GENDER)) {
            int gender = values.getAsInteger(PetsContract.PetEntry.COLOUMN_PET_GENDER);
            if (!PetsContract.PetEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("Requires valid Gender");
            }
        }
        if (values.containsKey(PetsContract.PetEntry.COLOUMN_PET_WEIGHT)) {
            int weight = values.getAsInteger(PetsContract.PetEntry.COLOUMN_PET_WEIGHT);
            if (weight < 0) {
                throw new IllegalArgumentException("Enter valid Weight");
            }
        }


        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                //update all similiar rows
                return updatePet(uri, values, selection, selectionArgs);
            case PET_ID:
                //update unique row
                selection = PetsContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                return updatePet(uri, values, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not Possible for " + uri);
        }
    }


    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase mSQLiteDatabase = mDbHelper.getWritableDatabase();
        int rows = mSQLiteDatabase.update(PetsContract.PetEntry.TABLE_NAME, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return rows;
    }


    private Uri insertPet(Uri uri, ContentValues contentValues) {
        SQLiteDatabase mSQLiteDatabase = mDbHelper.getWritableDatabase();
        long rowId = mSQLiteDatabase.insert(PetsContract.PetEntry.TABLE_NAME, null, contentValues);
        if (rowId == -1) {
            Log.e(Log_E, "Failed to Insert");
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, rowId);
    }


    private void sanityCheck(ContentValues values) {
        String name = values.getAsString(PetsContract.PetEntry.COLOUMN_PET_NAME);
        int gender = values.getAsInteger(PetsContract.PetEntry.COLOUMN_PET_GENDER);
        int weight = values.getAsInteger(PetsContract.PetEntry.COLOUMN_PET_WEIGHT);

        if (name == null) {
            //throw new IllegalArgumentException("Pet Requires Name");
        }
        if (weight < 0) {
            //throw new IllegalArgumentException("Enter Valid Wieght");
        }
        if (!PetsContract.PetEntry.isValidGender(gender)) {
            //throw new IllegalArgumentException("Select Valid Gender");
        }

    }
}
