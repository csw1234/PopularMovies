package com.csw.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.csw.popularmovies.Data.Movies.Movie;
import com.csw.popularmovies.Data.Reviews.Review;
import com.csw.popularmovies.Data.Reviews.Reviews;
import com.csw.popularmovies.Data.Trailers.Trailer;
import com.csw.popularmovies.Data.Trailers.Trailers;
import com.csw.popularmovies.Databse.MoviesContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGE = "extra_image";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_OVERVIEW = "extra_overview";
    public static final String EXTRA_RATING = "extra_ratings";
    public static final String EXTRA_DATE = "extra_date";
    public static final String EXTRA_ID = "extra_id";

    private String mTitle;
    private String mOverview;
    private String mRating;
    private String mDate;
    private String mId;
    private Boolean mFavorite;
    private String mImageUrl;

    //RecyclerView the present the trailers
    RecyclerView trailersRecyclerView;
    RecyclerView reviewsRecyclerView;
    ImageButton faovirteButton;
    @Override
    protected void onStart() {
        super.onStart();
        loadTrailers();
        loadReviews();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mFavorite = true;

        ImageView movieImage = findViewById(R.id.image_iv);
        faovirteButton = findViewById(R.id.favoriteButton);
        reviewsRecyclerView = findViewById(R.id.recyclerviewReviews);

        trailersRecyclerView = findViewById(R.id.recyclerviewTrailers);
        //Set the RecyclerView
        LinearLayoutManager trailersLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        trailersRecyclerView.setLayoutManager(trailersLayoutManager);

        LinearLayoutManager reviewsLayoutManager = new LinearLayoutManager(this);
        reviewsRecyclerView.setLayoutManager(reviewsLayoutManager);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }
        //Get the data from the adapter
        mImageUrl = intent.getStringExtra(EXTRA_IMAGE);
         mTitle = intent.getStringExtra(EXTRA_TITLE);
        mOverview = intent.getStringExtra(EXTRA_OVERVIEW);
        mRating = intent.getStringExtra(EXTRA_RATING);
        mDate = intent.getStringExtra(EXTRA_DATE);
        mId = intent.getStringExtra(EXTRA_ID);

        checkFavorite();

        populateUI();
        //Present the Image using Picasso library
        Picasso.with(this)
                .load(MovieImagesAdapter.buildImageUrl(mImageUrl))
                .into(movieImage);
        faovirteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFavorite){
                    deleteFavorite();
                }else {
                    insertAsFavorite();
                }
            }
        });

    }
    private void insertAsFavorite(){
        //Create a new map of values, where column names are the keys
        ContentValues mNewValues = new ContentValues();
        mNewValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, mId);
        mNewValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_OVERVIEW, mOverview);
        mNewValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_RELEASE_DATE, mDate);
        mNewValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_TITLE, mTitle);
        mNewValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_VOTE_AVERAGE, mRating);
        mNewValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_FAVORED, !mFavorite);
        mNewValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER_PATH, mImageUrl);
        getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI, mNewValues);
        checkFavorite();
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    private void populateUI() {
        TextView titleTV = findViewById(R.id.title);
        TextView userRatingTV = findViewById(R.id.user_rating_tv);
        TextView overviewTV = findViewById(R.id.overview_tv);
        TextView releaseDateTV = findViewById(R.id.release_date_tv);

        //Set text for movie title
        if (!mTitle.equals("")){
                titleTV.setText(mTitle);
        }else{
            titleTV.setText(R.string.no_title_message);
        }

        //Set text for overview
        if (!mOverview.equals("")){
            overviewTV.setText(mOverview);
        }else{
            overviewTV.setText(R.string.no_oveview_message);
        }

        //Set text for user rating
        if (!mRating.equals("")){
            userRatingTV.setText(mRating);
        }else{
            userRatingTV.setText(R.string.rating_not_available_message);
        }

        //Set text for the release date
        if (!mDate.equals("")){
            releaseDateTV.setText(mDate);
        }else{
            releaseDateTV.setText(R.string.date_unknown);
        }
    }
    //Loading the trailers list
    private void loadTrailers(){
        //Create the server call using Retrofit library
        GeneratorService generatorService = ServiceGenerator.retrofit
                .create(GeneratorService.class);
        retrofit2.Call<Trailers> call =
                generatorService.getTrailers(mId, getString(R.string.api_key));
        //Start the call, Async call
        call.enqueue(new Callback<Trailers>() {
            @Override
            public void onResponse(Call<Trailers> call, Response<Trailers> response) {
                List<Trailer> trailersList =new ArrayList<>();

                if (response.body() == null){
                    return;
                }
                if (response.body().getTrailers() != null){
                    //Get all trailers
                    trailersList.addAll(response.body().getTrailers());

                }
                //Set the movies into the Adapter
                TrailersAdapter movieImagesAdapter = new TrailersAdapter(trailersList);
                trailersRecyclerView.setAdapter(movieImagesAdapter);

            }

            @Override
            public void onFailure(Call<Trailers> call, Throwable t) {
                Log.e("kk","error");
            }
        });

    }

    //Loading the trailers list
    private void loadReviews(){
        //Create the server call using Retrofit library
        GeneratorService generatorService = ServiceGenerator.retrofit
                .create(GeneratorService.class);
        retrofit2.Call<Reviews> call =
                generatorService.getReviews(mId, getString(R.string.api_key));
        //Start the call, Async call
        call.enqueue(new Callback<Reviews>() {
            @Override
            public void onResponse(Call<Reviews> call, Response<Reviews> response) {
                List<Review> trailersList =new ArrayList<>();

                if (response.body() == null){
                    return;
                }
                if (response.body().getResults() != null){
                    //Get all trailers
                    trailersList.addAll(response.body().getResults());

                }
                //Set the movies into the Adapter
                ReviewsAdapter movieImagesAdapter = new ReviewsAdapter(trailersList);
                reviewsRecyclerView.setAdapter(movieImagesAdapter);

            }

            @Override
            public void onFailure(Call<Reviews> call, Throwable t) {
                Log.e("kk","error");
            }
        });

    }

    private void checkFavorite(){
        Cursor cursor = getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI, new String[]{MoviesContract.MoviesEntry.COLUMN_MOVIE_ID }
        , MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + "=?", new String[]{String.valueOf(mId)}, null);
        mFavorite = (cursor != null) && (cursor.getCount() >0);
        if (mFavorite){
            faovirteButton.setBackgroundResource(R.mipmap.ic_favorites);
        }else {
            faovirteButton.setBackgroundResource(R.mipmap.ic_favorites_empty);
        }
    }

    private void deleteFavorite(){
        getContentResolver().delete(MoviesContract.MoviesEntry.CONTENT_URI,
                MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + "=?",
                new String[]{String.valueOf(mId)});
        Toast.makeText(getApplicationContext(), R.string.removed_from_favorites, Toast.LENGTH_SHORT).show();
        checkFavorite();
    }
}
