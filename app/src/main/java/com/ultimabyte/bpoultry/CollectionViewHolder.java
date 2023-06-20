package com.ultimabyte.bpoultry;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CollectionViewHolder extends RecyclerView.ViewHolder {

    public CollectionViewHolder(@NonNull View itemView) {
        super(itemView);
    }


    public void bind(Collection item) {
        ((TextView) itemView.findViewById(R.id.title)).setText(item.shop.name);
        ((TextView) itemView.findViewById(R.id.weight)).setText(item.collectionAmount + " KG");
        ((TextView) itemView.findViewById(R.id.time)).setText(item.createdAt);
    }
}
