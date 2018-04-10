package com.csw.popularmovies;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


class ServiceGenerator {
    static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    static Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());
    static OkHttpClient.Builder httpClient=
            new OkHttpClient.Builder();
    static Retrofit retrofit =
            retrofitBuilder
            .client(httpClient.build())
            .build();
}
