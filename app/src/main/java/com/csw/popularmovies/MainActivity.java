package com.csw.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.csw.popularmovies.Adapters.MovieImagesAdapter;
import com.csw.popularmovies.Data.Movies.Page;
import com.csw.popularmovies.Data.Movies.Movie;
import com.csw.popularmovies.Databse.MoviesContract;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String sortBy = "sortBy";
    //RecyclerView the present the images
    RecyclerView recyclerView;
    //Hold the SortBy parameter inside SharedPreferences
    SharedPreferences sharedPreferences;
    //Favorites state (empty / or not)
    Boolean favoritesEmpty = true;
    Boolean favoritesAreShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerview);
        //Set the RecyclerView the show the images in a 2 columns grid
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(linearLayoutManager);
        sharedPreferences = getSharedPreferences(sortBy, Context.MODE_PRIVATE);
        //Load the images
        loadMovies();
    }

    //Update favorites list or switch to previous screen in case favorites are empty
    @Override
    protected void onResume() {
        super.onResume();
        if (favoritesAreShown) {
            loadFavoritesMovies();
        }
        if (favoritesEmpty) {
            loadMovies();
        }
    }

    //Loading the Movies
    private void loadMovies() {
        //Create the server call using Retrofit library
        GeneratorService generatorService = ServiceGenerator.retrofit
                .create(GeneratorService.class);
        //Starting the server call, if there's no parameter inside the SortBy parameter (SharedPreferences), the default is "popular movies"
        retrofit2.Call<Page> call =
                generatorService.getImage(sharedPreferences.getString(sortBy, "popular"), getString(R.string.api_key));
        //Start the call, Async call
        call.enqueue(new Callback<Page>() {
            @Override
            public void onResponse(@NonNull Call<Page> call, Response<Page> response) {
                if (response.body() == null) {
                    return;
                }

                //Set the movies into the Adapter
                MovieImagesAdapter movieImagesAdapter = new MovieImagesAdapter(response.body().getResults());
                recyclerView.setAdapter(movieImagesAdapter);
            }

            @Override
            public void onFailure(@NonNull Call<Page> call, @NonNull Throwable t) {

            }
        });
    }

    //Set the menu for the Settings(SortBy) icon
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Saving the selected setting into shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        switch (item.getItemId()) {
            case R.id.popular:
                editor.putString(sortBy, "popular");
                editor.apply();
                loadMovies();
                favoritesAreShown = false;
                return true;
            case R.id.rating:
                editor.putString(sortBy, "top_rated");
                editor.apply();
                loadMovies();
                favoritesAreShown = false;
                return true;
            case R.id.favorites:
                loadFavoritesMovies();
                favoritesAreShown = true;
                return true;
            default:
                editor.putString(sortBy, "popular");
                editor.apply();
                return true;
        }
    }

    //Load favorites list
    private void loadFavoritesMovies() {
        List<Movie> imageList = new ArrayList<>();
        //Query list from database
        Cursor cursor = getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            favoritesEmpty = false;
            do {
                Movie movie = new Movie(
                        cursor.getInt(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID)),
                        cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_TITLE)),
                        cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_OVERVIEW)),
                        cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER_PATH)),
                        cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_RELEASE_DATE)),
                        cursor.getInt(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_VOTE_AVERAGE))
                );
                imageList.add(movie);

            } while (cursor.moveToNext());
            //Set the movies into the Adapter
            MovieImagesAdapter movieImagesAdapter = new MovieImagesAdapter(imageList);
            recyclerView.setAdapter(movieImagesAdapter);
        } else {
            favoritesEmpty = true;
            Toast.makeText(getApplicationContext(), R.string.empty_favorites, Toast.LENGTH_SHORT).show();
        }
        if (cursor != null) {
            cursor.close();
        }
    }
}
