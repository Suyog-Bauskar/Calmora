package com.suyogbauskar.calmora;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.suyogbauskar.calmora.Adapter.MusicListAdapter;
import com.suyogbauskar.calmora.POJOs.MusicItem;

import java.util.ArrayList;
import java.util.List;

public class MusicListActivity extends AppCompatActivity {

    private ListView musicListView;
    private TextView tvCategoryTitle;
    private List<MusicItem> musicItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_music_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        musicListView = findViewById(R.id.musicListView);
        tvCategoryTitle = findViewById(R.id.tvCategoryTitle);

        String category = getIntent().getStringExtra("category");
        tvCategoryTitle.setText(category + " Music");

        musicItems = new ArrayList<>();

        // Sample: Add static songs from raw/
        if ("Relax".equals(category)) {
            musicItems.add(new MusicItem("Ocean Breeze", R.raw.relax1));
            musicItems.add(new MusicItem("Rain Drops", R.raw.relax2));
        } else if ("Spiritual".equals(category)) {
            musicItems.add(new MusicItem("Divine Chant", R.raw.spiritual1));
            musicItems.add(new MusicItem("Temple Bells", R.raw.spiritual2));
        } else if ("Energetic".equals(category)) {
            musicItems.add(new MusicItem("Upbeat Vibes", R.raw.energetic1));
            musicItems.add(new MusicItem("Workout Pulse", R.raw.energetic2));
        } else if ("Stress Free".equals(category)) {
            musicItems.add(new MusicItem("Mind Cleanse", R.raw.stress1));
            musicItems.add(new MusicItem("Peace Flow", R.raw.stress2));
        }

        MusicListAdapter adapter = new MusicListAdapter(this, musicItems);
        musicListView.setAdapter(adapter);

        musicListView.setOnItemClickListener((parent, view, position, id) -> {
            MusicItem item = musicItems.get(position);
            Intent intent = new Intent(this, MusicPlayerActivity.class);
            intent.putExtra("category", category);       // the category string
            intent.putExtra("title", item.getTitle());
            intent.putExtra("resId", item.getResId());
            startActivity(intent);

        });
    }
}
