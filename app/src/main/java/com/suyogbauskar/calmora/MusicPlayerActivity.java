package com.suyogbauskar.calmora;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.suyogbauskar.calmora.POJOs.MusicItem;
import com.suyogbauskar.calmora.R;

import java.util.ArrayList;
import java.util.List;

public class MusicPlayerActivity extends AppCompatActivity {
    private TextView musicTitleText;
    private ImageButton playPauseButton, nextButton, previousButton;
    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    // The full list of songs in this category
    private List<MusicItem> musicItems;
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        // bind views...
        musicTitleText = findViewById(R.id.musicTitleText);
        playPauseButton = findViewById(R.id.btnPlayPause);
        nextButton = findViewById(R.id.btnNext);
        previousButton = findViewById(R.id.btnPrevious);
        seekBar = findViewById(R.id.seekBar);

        // get Intent data
        String category = getIntent().getStringExtra("category");
        String title = getIntent().getStringExtra("title");
        int resId = getIntent().getIntExtra("resId", -1);

        // build the list for this category
        musicItems = getMusicByCategory(category);

        // find which index was clicked
        for (int i = 0; i < musicItems.size(); i++) {
            if (musicItems.get(i).getTitle().equals(title)) {
                currentIndex = i;
                break;
            }
        }

        // initialize UI and player
        updateSong(musicItems.get(currentIndex));

        playPauseButton.setOnClickListener(v -> {
            if (isPlaying) pauseMusic();
            else playMusic();
        });

        nextButton.setOnClickListener(v -> playNextSong());
        previousButton.setOnClickListener(v -> playPreviousSong());
    }

    private List<MusicItem> getMusicByCategory(String category) {
        List<MusicItem> list = new ArrayList<>();
        switch (category) {
            case "Relax":
                list.add(new MusicItem("Ocean Breeze", R.raw.relax1));
                list.add(new MusicItem("Rain Drops", R.raw.relax2));
                break;
            case "Spiritual":
                list.add(new MusicItem("Chant Om", R.raw.spiritual1));
                list.add(new MusicItem("Temple Bells", R.raw.spiritual2));
                break;
            case "Energetic":
                list.add(new MusicItem("Upbeat Vibes", R.raw.energetic1));
                list.add(new MusicItem("Workout Pulse", R.raw.energetic2));
                break;
            case "Stress Free":
                list.add(new MusicItem("Mind Cleanse", R.raw.stress1));
                list.add(new MusicItem("Peace Flow", R.raw.stress2));
                break;
        }
        return list;
    }

    private void updateSong(MusicItem item) {
        // stop any existing playback
        stopMusic();
        musicTitleText.setText(item.getTitle());
        mediaPlayer = MediaPlayer.create(this, item.getResId());
        mediaPlayer.setOnCompletionListener(mp -> stopMusic());
        seekBar.setMax(mediaPlayer.getDuration());
        mediaPlayer.setOnPreparedListener(mp -> {
            // optionally start updating seekBar in a handler
        });
        isPlaying = false;
        playPauseButton.setImageResource(R.drawable.play_circle);
    }

    private void playMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            isPlaying = true;
            playPauseButton.setImageResource(R.drawable.pause_circle);
            // start seekBar updater...
        }
    }

    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            playPauseButton.setImageResource(R.drawable.play_circle);
        }
    }

    private void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
            playPauseButton.setImageResource(R.drawable.play_circle);
        }
    }

    private void playNextSong() {
        currentIndex = (currentIndex + 1) % musicItems.size();
        updateSong(musicItems.get(currentIndex));
        playMusic();
    }

    private void playPreviousSong() {
        currentIndex = (currentIndex - 1 + musicItems.size()) % musicItems.size();
        updateSong(musicItems.get(currentIndex));
        playMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMusic();
    }
}
