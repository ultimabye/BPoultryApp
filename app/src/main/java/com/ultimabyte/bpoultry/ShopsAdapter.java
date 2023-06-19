package com.ultimabyte.bpoultry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ultimabyte.bpoultry.data.Shop;

import java.util.List;

public class ShopsAdapter extends ArrayAdapter<Shop> {

    private static final String TAG = ShopsAdapter.class.getSimpleName();

    private List<Shop> mItems;

    public ShopsAdapter(Context context, List<Shop> items) {
        super(context, 0);
        mItems = items;
        setDropDownViewResource(R.layout.preset_item);
    }


    public void setShops(List<Shop> shops) {
        mItems.clear();
        mItems.addAll(shops);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }


    @Nullable
    @Override
    public Shop getItem(int position) {
        return mItems.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.preset_item, parent, false);
            convertView.setTag(new PresetHolder(convertView));
        }


        //convertView.setBackgroundResource(R.drawable.spinner_border);

        PresetHolder holder = (PresetHolder) convertView.getTag();
        holder.presetName.setText(mItems.get(position).name);
        return convertView;
    }


    @Override
    public View getDropDownView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.drop_down_item, parent, false);
            PresetHolder holder = new PresetHolder(convertView);
            convertView.setTag(holder);
        }

        PresetHolder holder = (PresetHolder) convertView.getTag();
        holder.presetName.setText(mItems.get(position).name);
        return convertView;
    }

    private class PresetHolder {
        TextView presetName;

        public PresetHolder(View view) {
            presetName = view.findViewById(R.id.title);
        }
    }
}
