package com.csw.popularmovies.Databse;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Toast;

/**
 * {@link ContentProvider} for Pets app.
 */
public class MoviesProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    private MoviesDbHelper mDbHelper;
    private static final int PETS = 100;
    private static final int PET_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_PETS, PETS);
        sUriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_PETS + "/#", PET_ID);
    }

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // TODO: Create and initialize a MoviesDbHelper object to gain access to the pets database.
        mDbHelper = new MoviesDbHelper(getContext());
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        //Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        //This cursor will hold the result of the query
        Cursor cursor = null;

        //Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                //For the PETS, query the pets table directly wth the given
                //projection, selection, selection arguments, and sort order.
                //The cursor could contain multiple rows of the pets table.
                cursor = database.query(MoviesContract.MoviesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PET_ID:
                //For the PET_ID code, extract out the ID from the URI.
                //For an example URI such as "content://com.example.android.pets/pets3",
                //the selection will be "_id=?" and the selection argument wil be a
                //String array containing the actual ID of 3 in this case.

                //For every "?" in the selection, we need to have an element in the selection
                //arguments that will fill in the "?". Since we have 1 question mark in the selection
                //we have 1 String in the selection arguments' String array.
                selection = MoviesContract.MoviesEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                //This will perform a query on the pets table where the _id equals 3 to return a
                //Cursor containing that row of the table.
                cursor = database.query(MoviesContract.MoviesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);

        }
        //Set notfication URI on the Cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }


    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database
     */
    private Uri insertPet(Uri uri, ContentValues values) {
        /**
         * Todo: Insert a new pet into the pets database table with the given ContentValues
         *
         * Once we know thw ID of the new row in the table,
         * return the new URI with the ID appended to the end of it
         */
        //Check the name is not null

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, values);


        if (id != -1) {
            Toast.makeText(getContext(), "Pet saved with id: " + id, Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getContext(), "Error saving pet " + id, Toast.LENGTH_SHORT).show();

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case PET_ID:
                selection = MoviesContract.MoviesEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArge) {
        // If the {@link MoviesEntry#COLUMN_PET_NAME} key is present,
        // check that the name value is not null.

        return 0;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted =0;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                //Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(MoviesContract.MoviesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PET_ID:
                //Delete a single row given by the ID in the URI
                selection = MoviesContract.MoviesEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(MoviesContract.MoviesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for :" + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return MoviesContract.MoviesEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return MoviesContract.MoviesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + "with match " + match);
        }
    }
}
