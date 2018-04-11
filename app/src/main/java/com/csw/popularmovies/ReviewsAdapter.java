package com.csw.popularmovies;

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

import com.csw.popularmovies.Data.Reviews.Review;
import com.csw.popularmovies.Data.Trailers.Trailer;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.MyViewHolder> {
    //List to hold the images
    private List<Review> ReviewsList;
    //Context to call the intent for the DetailActivity
    private Context mContext;

    ReviewsAdapter(List<Review> ReviewsList) {
        this.ReviewsList = ReviewsList;
    }

    @NonNull
    @Override
    public ReviewsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the pic_layout into the trailersRecyclerView
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_recyler_cell,parent,false);

        return new ReviewsAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReviewsAdapter.MyViewHolder holder, final int position) {
        //Get the image using the position and present it on the right place
        final Review review = ReviewsList.get(position);
        holder.textView.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return ReviewsList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView textView;

        MyViewHolder(View itemView) {
            super(itemView);
            //Set the mContext into view context
            mContext = itemView.getContext();
            textView=itemView.findViewById(R.id.reviewText);
        }
    }
}
