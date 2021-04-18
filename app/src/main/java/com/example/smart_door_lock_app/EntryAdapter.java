package com.example.smart_door_lock_app;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class EntryAdapter extends ArrayAdapter<HistoryEntry> {
    private Context mContext;
    private int mResource;

    public EntryAdapter(@NonNull Context context, int resource, @NonNull ArrayList<HistoryEntry> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        convertView = layoutInflater.inflate(mResource, parent, false);

        ImageView imageView = convertView.findViewById(R.id.imageIv);
        TextView nameTv = convertView.findViewById(R.id.nameTv);
        TextView timeTv = convertView.findViewById(R.id.timeTv);

        imageView.setImageResource(getItem(position).getImage());
        nameTv.setText(getItem(position).getName());
        timeTv.setText(getItem(position).getTime());

        return convertView;
    }
}
