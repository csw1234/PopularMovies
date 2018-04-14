package com.csw.popularmovies.Databse;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by alonz on 06/10/2017.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "shelter.db";

    private static final int DATABASE_VERSION = 1;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_PET_TABLE = "CREATE TABLE " + MoviesContract.MoviesEntry.TABLE_NAME+"("
                + BaseColumns._ID + " INTEGER NOT NULL PRIMARY KEY,"
                + MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL,"
                + MoviesContract.MoviesEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL,"
                + MoviesContract.MoviesEntry.COLUMN_MOVIE_OVERVIEW + " TEXT,"
                + MoviesContract.MoviesEntry.COLUMN_MOVIE_VOTE_AVERAGE + " REAL,"
                + MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT,"
                + MoviesContract.MoviesEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT,"
                + MoviesContract.MoviesEntry.COLUMN_MOVIE_FAVORED + " INTEGER NOT NULL DEFAULT 0,"
                + "UNIQUE (" + MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE)";
        sqLiteDatabase.execSQL(SQL_CREATE_PET_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
