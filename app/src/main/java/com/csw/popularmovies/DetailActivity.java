package com.csw.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
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
import com.csw.popularmovies.Adapters.MovieImagesAdapter;
import com.csw.popularmovies.Adapters.ReviewsAdapter;
import com.csw.popularmovies.Adapters.TrailersAdapter;
import com.csw.popularmovies.Data.Reviews.Reviews;
import com.csw.popularmovies.Data.Trailers.Trailers;
import com.csw.popularmovies.Databse.MoviesContract;
import com.squareup.picasso.Picasso;
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
    //RecyclerView the present the reviews
    RecyclerView reviewsRecyclerView;
    ImageButton favoriteButton;

    @Override
    protected void onStart() {
        super.onStart();
        //Load trailers and reviews list
        loadTrailers();
        loadReviews();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mFavorite = true;

        ImageView movieImage = findViewById(R.id.image_iv);
        favoriteButton = findViewById(R.id.favoriteButton);
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
        assert intent != null;
        mImageUrl = intent.getStringExtra(EXTRA_IMAGE);
        mTitle = intent.getStringExtra(EXTRA_TITLE);
        mOverview = intent.getStringExtra(EXTRA_OVERVIEW);
        mRating = intent.getStringExtra(EXTRA_RATING);
        mDate = intent.getStringExtra(EXTRA_DATE);
        mId = intent.getStringExtra(EXTRA_ID);

        //Check if the movie is favorite
        checkFavorite();

        populateUI();
        //Present the Image using Picasso library
        Picasso.with(this)
                .load(MovieImagesAdapter.buildImageUrl(mImageUrl))
                .into(movieImage);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If the movie not in the favorite list add it, if it is delete it
                if (mFavorite) {
                    deleteFavorite();
                } else {
                    insertAsFavorite();
                }
            }
        });

    }

    private void insertAsFavorite() {
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
        if (!mTitle.equals("")) {
            titleTV.setText(mTitle);
        } else {
            titleTV.setText(R.string.no_title_message);
        }

        //Set text for overview
        if (!mOverview.equals("")) {
            overviewTV.setText(mOverview);
        } else {
            overviewTV.setText(R.string.no_oveview_message);
        }

        //Set text for user rating
        if (!mRating.equals("")) {
            userRatingTV.setText(mRating);
        } else {
            userRatingTV.setText(R.string.rating_not_available_message);
        }

        //Set text for the release date
        if (!mDate.equals("")) {
            releaseDateTV.setText(mDate);
        } else {
            releaseDateTV.setText(R.string.date_unknown);
        }
    }

    //Loading the trailers list
    private void loadTrailers() {
        //Create the server call using Retrofit library
        GeneratorService generatorService = ServiceGenerator.retrofit
                .create(GeneratorService.class);
        retrofit2.Call<Trailers> call =
                generatorService.getTrailers(mId, getString(R.string.api_key));
        //Start the call, Async call
        call.enqueue(new Callback<Trailers>() {
            @Override
            public void onResponse(Call<Trailers> call, Response<Trailers> response) {
                if (response.body() == null) {
                    return;
                }
                //Set the movies into the Adapter
                TrailersAdapter movieImagesAdapter = new TrailersAdapter(response.body().getTrailers());
                trailersRecyclerView.setAdapter(movieImagesAdapter);
            }

            @Override
            public void onFailure(Call<Trailers> call, Throwable t) {
                Log.e("Error", "Trailers server error");
            }
        });

    }

    //Loading the reviews list
    private void loadReviews() {
        //Create the server call using Retrofit library
        GeneratorService generatorService = ServiceGenerator.retrofit
                .create(GeneratorService.class);
        retrofit2.Call<Reviews> call =
                generatorService.getReviews(mId, getString(R.string.api_key));
        //Start the call, Async call
        call.enqueue(new Callback<Reviews>() {
            @Override
            public void onResponse(@NonNull Call<Reviews> call, Response<Reviews> response) {
                if (response.body() == null) {
                    return;
                }
                //Set the reviews into the Adapter
                ReviewsAdapter movieImagesAdapter = new ReviewsAdapter(response.body().getResults());
                reviewsRecyclerView.setAdapter(movieImagesAdapter);
            }

            @Override
            public void onFailure(@NonNull Call<Reviews> call, Throwable t) {
                Log.e("Error", "Reviews server error");
            }
        });
    }

    //Check if the movie is already in favorite list and change the "favorite" photo if it does
    private void checkFavorite() {
        Cursor cursor = getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI, new String[]{MoviesContract.MoviesEntry.COLUMN_MOVIE_ID}
                , MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + "=?", new String[]{String.valueOf(mId)}, null);
        mFavorite = (cursor != null) && (cursor.getCount() > 0);
        if (mFavorite) {
            favoriteButton.setBackgroundResource(R.mipmap.ic_favorites);
        } else {
            favoriteButton.setBackgroundResource(R.mipmap.ic_favorites_empty);
        }
        assert cursor != null;
        cursor.close();
    }

    //Delete movie from favorite list
    private void deleteFavorite() {
        getContentResolver().delete(MoviesContract.MoviesEntry.CONTENT_URI,
                MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + "=?",
                new String[]{String.valueOf(mId)});
        Toast.makeText(getApplicationContext(), R.string.removed_from_favorites, Toast.LENGTH_SHORT).show();
        checkFavorite();
    }
}
