package com.example.saurabh.rxsearch;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.example.saurabh.rxsearch.data.SearchResponse;
import com.example.saurabh.rxsearch.rest.GitHubClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    SearchView searchView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    SearchRecyclerAdapter adapter;

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
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private void setupRecyclerAdapter(SearchResponse response){
        if (adapter==null){
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter= new SearchRecyclerAdapter(this,response);
            recyclerView.setAdapter(adapter);
        }
        else {
            adapter.setSearchResponse(response);
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() < 2)
            return false;
        else {
            GitHubClient.getClient(this).getSearchResults(newText).enqueue(new Callback<SearchResponse>() {
                @Override
                public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        setupRecyclerAdapter(response.body());
                    }
                }

                @Override
                public void onFailure(Call<SearchResponse> call, Throwable t) {
                    t.printStackTrace();
                }
            });
            return true;
        }
    }
}
