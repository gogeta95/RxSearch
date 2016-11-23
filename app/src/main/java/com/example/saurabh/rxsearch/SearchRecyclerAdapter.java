package com.example.saurabh.rxsearch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.saurabh.rxsearch.data.Item;
import com.example.saurabh.rxsearch.data.SearchResponse;
import com.example.saurabh.rxsearch.util.AndroidUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by saurabh on 22/09/16.
 */

public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.SearchResultViewHolder> {

    private SearchResponse searchResponse;
    private LayoutInflater inflater;
    private Context context;

    public SearchRecyclerAdapter(@NonNull Context context, @NonNull SearchResponse searchResponse) {
        this.searchResponse = searchResponse;
        this.context=context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public SearchResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SearchResultViewHolder(inflater.inflate(R.layout.search_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final SearchResultViewHolder holder, int position) {
        final Item item = searchResponse.getItems().get(position);
        holder.repoName.setText(item.getName());
        holder.repoFullName.setText(item.getFullName());
        holder.searchItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtils.openURLinBrowser(context,item.getHtmlUrl());
            }
        });
    }

    public void setSearchResponse(SearchResponse searchResponse) {
        this.searchResponse = searchResponse;
        notifyDataSetChanged();
    }

    public void addItems(List<Item> items){
        int oldSize=searchResponse.getItems().size();
        searchResponse.getItems().addAll(items);
        notifyItemRangeInserted(oldSize,oldSize+items.size());
    }

    @Override
    public int getItemCount() {
        return searchResponse.getItems().size();
    }

    static class SearchResultViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.search_item)
        CardView searchItem;
        @BindView(R.id.repo_name)
        TextView repoName;
        @BindView(R.id.repo_full_name)
        TextView repoFullName;

        SearchResultViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
