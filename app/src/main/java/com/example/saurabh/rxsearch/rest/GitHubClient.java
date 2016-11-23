package com.example.saurabh.rxsearch.rest;

import android.content.Context;

import com.example.saurabh.rxsearch.R;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by saurabh on 22/09/16.
 */

public class GitHubClient {
    private static WebService webService;

    //private constructor to prevent object creation.
    private GitHubClient() {

    }

    public static WebService getClient(Context context) {
        if (webService == null) {
            Retrofit retrofit = new Retrofit.Builder()
//                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .baseUrl(context.getString(R.string.server_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            webService = retrofit.create(WebService.class);
        }
        return webService;
    }

}
