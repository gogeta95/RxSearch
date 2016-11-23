package com.example.saurabh.rxsearch;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.saurabh.rxsearch.data.SearchResponse;
import com.example.saurabh.rxsearch.rest.GitHubClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int MSG_UPDATE_DATA = 1;
    public static final long MSG_UPDATE_DELAY = 300;
    SearchView searchView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    SearchRecyclerAdapter adapter;
    Subscription subscription;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_UPDATE_DATA) {
                    String searchText = (String) msg.obj;
                    Log.d(TAG, "handleMessage: " + searchText);
                    processSearchText(searchText);
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) item.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
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
    public boolean onQueryTextChange(String newText) {
        if (newText.length() > 2) {
            Message message = Message.obtain(mHandler, MSG_UPDATE_DATA, newText);
            mHandler.removeMessages(MSG_UPDATE_DATA);
            mHandler.sendMessageDelayed(message, MSG_UPDATE_DELAY);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    public void processSearchText(String newText) {
        subscription = GitHubClient.getClient(this).getSearchResultsRx(newText)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
    }
}
