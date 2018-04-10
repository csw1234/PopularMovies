package com.csw.popularmovies;

import com.csw.popularmovies.Data.Page;
import com.csw.popularmovies.Data.Trailers;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


//Set the Path and Query for the service
public interface GeneratorService {
    @GET("{sortBy}")
    Call<Page> getImage(@Path("sortBy") String sortBy, @Query("api_key") String key);

    @GET("{id}/videos")
    Call<Trailers> getTrailers(@Path("id") String id, @Query("api_key") String key);
}
