package com.example.android.pets.data;



import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.net.URI;

/**
 * {@link ContentProvider} for Pets app.
 */
public class PetProvider extends ContentProvider {
    /** Tag for the log messages */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();

    //Codes for the UriMatcher
    private static final int PETS = 100;
    private static final int PET_ID = 101;

    //Create a uriMatcher
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS,PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS+"/#",PET_ID);
    }


    /** Database helper object */
    PetDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new PetDbHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor=null;

        int match = sUriMatcher.match(uri);
        switch(match){
            case PETS:
                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection,selection,selectionArgs,
                        null,null,sortOrder);
                break;
            case PET_ID:
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection,selection,selectionArgs,
                        null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = sUriMatcher.match(uri);
        switch(match){
            case PETS:
                return insertPet(uri,values);
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
    }

    private Uri insertPet(Uri uri, ContentValues values) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //Data validation
        String name = values.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
        String breed = values.getAsString(PetContract.PetEntry.COLUMN_PET_BREED);
        Integer gender = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_GENDER);
        Integer weight = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT);

        if(name == null){
            throw new IllegalArgumentException("Pet requires a name");
        }
        if(breed == null){
            throw new IllegalArgumentException("Pet requires a breed");
        }
        if(gender == null || !PetContract.PetEntry.isValidGender(gender)){
            throw new IllegalArgumentException("Pet requires a valid gender");
        }
        if(weight != null && weight < 0){
            throw new IllegalArgumentException("Pet requires a positive weight");
        }

        long row = database.insert(PetContract.PetEntry.TABLE_NAME,null,values);

        if (row == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        return ContentUris.withAppendedId(uri,row);
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch(match){
            case PETS:
                return updatePet(uri,values, selection,selectionArgs);
            case PET_ID:
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{ String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for "+ uri);
        }
    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rows = 0;
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        String name = values.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
        String breed = values.getAsString(PetContract.PetEntry.COLUMN_PET_BREED);
        Integer gender = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_GENDER);
        Integer weight = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT);

        if(values.containsKey(PetContract.PetEntry.COLUMN_PET_NAME) && name == null){
            throw new IllegalArgumentException("Pet requires a name");
        }
        if(values.containsKey(PetContract.PetEntry.COLUMN_PET_BREED) && breed == null){
            throw new IllegalArgumentException("Pet requires a breed");
        }
        if(values.containsKey(PetContract.PetEntry.COLUMN_PET_BREED) && gender == null || !PetContract.PetEntry.isValidGender(gender)){
            throw new IllegalArgumentException("Pet requires a valid gender");
        }
        if(values.containsKey(PetContract.PetEntry.COLUMN_PET_WEIGHT) && weight != null && weight < 0){
            throw new IllegalArgumentException("Pet requires a positive weight");
        }

        rows = database.update(PetContract.PetEntry.TABLE_NAME, values, selection, selectionArgs);


        if (rows == -1) {
            Log.e(LOG_TAG, "Failed to update row for " + uri);
            return -1;
        }
        return rows;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        switch (match){
            case PETS:
                return database.delete(PetContract.PetEntry.TABLE_NAME,selection,selectionArgs);
            case PET_ID:
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                return database.delete(PetContract.PetEntry.TABLE_NAME,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

}
