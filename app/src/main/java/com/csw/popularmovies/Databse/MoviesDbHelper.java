package com.csw.popularmovies.Databse;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class MoviesDbHelper extends SQLiteOpenHelper {

    //Database name and version
    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    //Create database with Contract columns
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MoviesContract.MoviesEntry.TABLE_NAME + "("
                + BaseColumns._ID + " INTEGER NOT NULL PRIMARY KEY,"
                + MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL,"
                + MoviesContract.MoviesEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL,"
                + MoviesContract.MoviesEntry.COLUMN_MOVIE_OVERVIEW + " TEXT,"
                + MoviesContract.MoviesEntry.COLUMN_MOVIE_VOTE_AVERAGE + " REAL,"
                + MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT,"
                + MoviesContract.MoviesEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT,"
                + MoviesContract.MoviesEntry.COLUMN_MOVIE_FAVORED + " INTEGER NOT NULL DEFAULT 0,"
                + "UNIQUE (" + MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE)";
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
