package com.csw.popularmovies.Adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.csw.popularmovies.Data.Trailers.Trailer;
import com.csw.popularmovies.R;

import java.util.List;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.MyViewHolder> {
    //List to hold the images
    private List<Trailer> trailersList;
    //Context to call the intent for the DetailActivity
    private Context mContext;

    public TrailersAdapter(List<Trailer> trailersList) {
        this.trailersList = trailersList;
    }

    @NonNull
    @Override
    public TrailersAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the trailer_recyler_cell into the trailersRecyclerView
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_recyler_cell, parent, false);
        return new TrailersAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final TrailersAdapter.MyViewHolder holder, final int position) {
        //Get the text using the position and present it on the right place
        final Trailer trailer = trailersList.get(position);
        int trailerNumber = position + 1;
        holder.textView.setText(mContext.getString(R.string.trailer) + trailerNumber + "/" + trailersList.size());
        //Listen to image click
        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start the trailer video using app of choice
                startYoutubeVideo(mContext, trailer.getKey());
            }
        });
    }

    @Override
    public int getItemCount() {
        return trailersList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        Button playButton;

        MyViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            textView = itemView.findViewById(R.id.trailerText);
            playButton = itemView.findViewById(R.id.play_button);
        }
    }


    //Start the video using intent and youtube parser url
    private void startYoutubeVideo(Context context, String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }
}
