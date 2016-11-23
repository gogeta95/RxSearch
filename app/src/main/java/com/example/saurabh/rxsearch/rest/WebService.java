package com.example.saurabh.rxsearch.rest;


import com.example.saurabh.rxsearch.data.SearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by saurabh on 22/09/16.
 */

public interface WebService {
//    @GET("/search/repositories")
//    Observable<SearchResponse> getSearchResultsRx(@Query("q") String query);

    @GET("/search/repositories")
    Call<SearchResponse> getSearchResults(@Query("q") String query);
}
