package com.csw.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.csw.popularmovies.Data.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

//Adapter for the RecyclerView
public class MovieImagesAdapter extends RecyclerView.Adapter<MovieImagesAdapter.MyViewHolder> {
    //List to hold the images
    private List<Movie> imageList;
    //Context to call the intent for the DetailActivity
    private Context mContext;

    MovieImagesAdapter(List<Movie> imageList) {
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the pic_layout into the recyclerView
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pic_layout,parent,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        //Get the image using the position and present it on the right place
        final Movie movie = imageList.get(position);
        if ((movie.getPosterPath() != null) && (!movie.getPosterPath().equals(""))){
        Picasso.with(holder.imageView.getContext()).load(buildImageUrl(movie.getPosterPath())).into(holder.imageView);
    }

    //Listen to image click
    holder.imageView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Pass the data to present in the DetailActivity
            Intent intent = new Intent(mContext, DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_IMAGE, buildImageUrl(movie.getPosterPath()));
            intent.putExtra(DetailActivity.EXTRA_TITLE,movie.getTitle());
            intent.putExtra(DetailActivity.EXTRA_OVERVIEW,movie.getOverview());
            intent.putExtra(DetailActivity.EXTRA_DATE,movie.getReleaseDate());
            intent.putExtra(DetailActivity.EXTRA_RATING,movie.getVoteAverage().toString());
            intent.putExtra(DetailActivity.EXTRA_ID,movie.getMovieId().toString());
            mContext.startActivity(intent);
        }
    });

    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;

        MyViewHolder(View itemView) {
            super(itemView);
            //Set the mContext into view context
            mContext = itemView.getContext();
            imageView=itemView.findViewById(R.id.imageView);
        }
    }


    //Build the image Url from the movie position
    private String buildImageUrl(String positionPath){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("image.tmdb.org")
                .appendPath("t")
                .appendPath("p")
                .appendPath("w185");
        return builder.appendEncodedPath(positionPath).build().toString();
    }
}
