package com.example.saurabh.rxsearch;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.saurabh.rxsearch.data.SearchResponse;
import com.example.saurabh.rxsearch.rest.GitHubClient;
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final long MSG_UPDATE_DELAY = 300;
    SearchView searchView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    SearchRecyclerAdapter adapter;
    Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) item.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        subscription = RxSearchView.queryTextChanges(searchView).filter(new Func1<CharSequence, Boolean>() {
            @Override
            public Boolean call(CharSequence charSequence) {
                return charSequence.length() > 2;
            }
        }).debounce(MSG_UPDATE_DELAY, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .doOnNext(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        Snackbar.make(recyclerView, "Loading...", Snackbar.LENGTH_INDEFINITE).show();
                    }
                })
                .flatMap(new Func1<CharSequence, Observable<SearchResponse>>() {
                    @Override
                    public Observable<SearchResponse> call(CharSequence charSequence) {
                        Log.d(TAG, "call: " + charSequence);
                        return GitHubClient.getClient(MainActivity.this).getSearchResultsRx((String) charSequence);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<SearchResponse>() {
                    @Override
                    public void call(SearchResponse response) {
                        Snackbar.make(recyclerView, "Loaded! ", Snackbar.LENGTH_SHORT).show();
                    }
                })
                .subscribe(new Action1<SearchResponse>() {
                    @Override
                    public void call(SearchResponse searchResponse) {
                        setupRecyclerAdapter(searchResponse);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
        return true;
    }


    private void setupRecyclerAdapter(SearchResponse response) {
        if (adapter == null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new SearchRecyclerAdapter(this, response);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setSearchResponse(response);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
