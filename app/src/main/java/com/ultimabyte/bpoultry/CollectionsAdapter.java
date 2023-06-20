package com.ultimabyte.bpoultry;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CollectionsAdapter extends RecyclerView.Adapter<CollectionViewHolder> {

    private List<Collection> mItems;


    public CollectionsAdapter(List<Collection> items) {
        this.mItems = items;
    }


    public void setItems(List<Collection> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_collection, parent, false);
        return new CollectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionViewHolder holder, int position) {
        Collection collection = mItems.get(position);
        holder.bind(collection);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
