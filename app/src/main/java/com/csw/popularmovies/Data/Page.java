package com.csw.popularmovies.Data;


import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Page {

    @SerializedName("results")
    @Expose
    private List<Movie> results = null;
    public List<Movie> getResults() {
        return results;
    }

}


