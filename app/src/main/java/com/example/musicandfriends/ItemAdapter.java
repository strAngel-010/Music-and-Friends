package com.example.musicandfriends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ItemAdapter extends ArrayAdapter<ListItem> {
    public ItemAdapter(Context context, ListItem[] arr){
        super(context, R.layout.adapter_item, arr);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ListItem listItem = getItem(position);
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_item, null);
        }

        ((TextView)convertView.findViewById(R.id.textView)).setText(listItem.listItemText);

        return convertView;
    }
}
