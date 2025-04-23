package com.suyogbauskar.calmora;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.suyogbauskar.calmora.POJOs.MusicItem;
import com.suyogbauskar.calmora.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {
    private TextView musicTitleText, tvArtistName, tvCurrentTime, tvTotalDuration;
    private ImageButton playPauseButton, nextButton, previousButton;
    private ImageView ivSongImage;
    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private Handler handler = new Handler();
    private Runnable runnable;

    // The full list of songs in this category
    private List<MusicItem> musicItems;
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        // bind views...
        musicTitleText = findViewById(R.id.musicTitleText);
        tvArtistName = findViewById(R.id.tvArtistName);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvTotalDuration = findViewById(R.id.tvTotalDuration);
        playPauseButton = findViewById(R.id.btnPlayPause);
        nextButton = findViewById(R.id.btnNext);
        previousButton = findViewById(R.id.btnPrevious);
        seekBar = findViewById(R.id.seekBar);
        ivSongImage = findViewById(R.id.ivSongImage);

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
        
        // Auto-play when player opens
        playMusic();

        playPauseButton.setOnClickListener(v -> {
            if (isPlaying) pauseMusic();
            else playMusic();
        });

        nextButton.setOnClickListener(v -> playNextSong());
        previousButton.setOnClickListener(v -> playPreviousSong());

        // SeekBar listener
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                    tvCurrentTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Pause updates while user is dragging
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    handler.removeCallbacks(runnable);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Resume updates after user finishes dragging
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    updateSeekBar();
                }
            }
        });
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

    private String formatTime(int millis) {
        return String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) - 
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    private void updateSeekBar() {
        if (mediaPlayer != null) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            seekBar.setProgress(currentPosition);
            tvCurrentTime.setText(formatTime(currentPosition));
            
            // Update every 100ms
            runnable = () -> updateSeekBar();
            handler.postDelayed(runnable, 100);
        }
    }

    private void updateSong(MusicItem item) {
        // stop any existing playback
        stopMusic();
        
        // Update UI elements
        musicTitleText.setText(item.getTitle());
        tvArtistName.setText("Nature Sounds"); // Default artist
        
        // Initialize the MediaPlayer
        mediaPlayer = MediaPlayer.create(this, item.getResId());
        mediaPlayer.setOnCompletionListener(mp -> {
            playNextSong(); // Auto-play next song
        });
        
        // Set up seekbar and duration
        int duration = mediaPlayer.getDuration();
        seekBar.setMax(duration);
        tvTotalDuration.setText(formatTime(duration));
        tvCurrentTime.setText("00:00");
        
        // Reset UI state
        isPlaying = false;
        playPauseButton.setImageResource(R.drawable.play_circle);
    }

    private void playMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            isPlaying = true;
            playPauseButton.setImageResource(R.drawable.pause_circle);
            updateSeekBar(); // Start updating seekBar
        }
    }

    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            playPauseButton.setImageResource(R.drawable.play_circle);
            handler.removeCallbacks(runnable); // Stop updating seekBar
        }
    }

    private void stopMusic() {
        if (mediaPlayer != null) {
            handler.removeCallbacks(runnable); // Stop updating seekBar
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
    protected void onPause() {
        super.onPause();
        pauseMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMusic();
    }
}
