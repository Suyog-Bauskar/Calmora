package com.suyogbauskar.calmora.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suyogbauskar.calmora.MusicPlayerActivity;
import com.suyogbauskar.calmora.POJOs.MusicItem;
import com.suyogbauskar.calmora.R;

import java.util.List;

public class MusicListAdapter extends ArrayAdapter<MusicItem> {

    private Context context;
    private List<MusicItem> musicList;

    public MusicListAdapter(Context context, List<MusicItem> musicList) {
        super(context, 0, musicList);
        this.context = context;
        this.musicList = musicList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_music, parent, false);
        }

        MusicItem item = musicList.get(position);

        TextView title = convertView.findViewById(R.id.musicTitle);
        title.setText(item.getTitle());

        return convertView;
    }
}
