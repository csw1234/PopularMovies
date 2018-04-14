package com.csw.popularmovies.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csw.popularmovies.Data.Reviews.Review;
import com.csw.popularmovies.R;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.MyViewHolder> {
    //List to hold the reviews
    private List<Review> ReviewsList;

    public ReviewsAdapter(List<Review> ReviewsList) {
        this.ReviewsList = ReviewsList;
    }

    @NonNull
    @Override
    public ReviewsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the review_recyler_cell into the reviewsRecyclerView
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_recyler_cell, parent, false);
        return new ReviewsAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReviewsAdapter.MyViewHolder holder, final int position) {
        final Review review = ReviewsList.get(position);
        holder.textView.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return ReviewsList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.reviewText);
        }
    }
}
