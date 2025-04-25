package com.suyogbauskar.calmora;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.suyogbauskar.calmora.Adapter.PhobiaSpecificAdapter;
import com.suyogbauskar.calmora.utils.PhobiaFragmentManager;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

public class BasicCourse extends AppCompatActivity {

    private ViewPager2 viewPager;
    private WormDotsIndicator dotsIndicator;
    private FloatingActionButton panicButton;
    private String phobiaType;
    private int currentPosition = 0;
    private static final String KEY_CURRENT_POSITION = "current_position";
    private static final int HEIGHT_VIDEO_FRAGMENT_INDEX = 18; // The index of HeightVideoFragment
    private static final int SPACE_VIDEO_FRAGMENT_INDEX = 19; // The index of SpaceVideoFragment
    private boolean isVideoFragmentActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_basic_course);

        // Default to portrait for all other fragments
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the phobia type from intent
        phobiaType = getIntent().getStringExtra("phobiaType");
        if (phobiaType == null) {
            phobiaType = PhobiaFragmentManager.UNKNOWN_PHOBIA;
        }

        // Restore saved position if available
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(KEY_CURRENT_POSITION, 0);
        }

        viewPager = findViewById(R.id.viewPager);
        dotsIndicator = findViewById(R.id.dotsIndicator);
        panicButton = findViewById(R.id.panicButton);

        // This is key - we don't want the ViewPager to handle its own saving
        viewPager.setSaveEnabled(false);
        
        setupViewPager();
        
        // Set page change listener to save current position and manage orientation
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentPosition = position;
                
                // Check if we're on a video fragment
                int actualFragmentIndex = PhobiaFragmentManager.getFragmentIndicesForPhobia(phobiaType)[position];
                isVideoFragmentActive = (actualFragmentIndex == HEIGHT_VIDEO_FRAGMENT_INDEX || 
                                        actualFragmentIndex == SPACE_VIDEO_FRAGMENT_INDEX);
                
                // If not on video fragment, ensure portrait mode
                if (!isVideoFragmentActive) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                // Note: Video fragments will set their own orientation
            }
        });
        
        // Show a short tooltip when the button is long-pressed
        panicButton.setOnLongClickListener(v -> {
            Toast.makeText(this, "Press for immediate calming music", Toast.LENGTH_SHORT).show();
            return true;
        });
        
        // Set up panic button click listener
        panicButton.setOnClickListener(v -> {
            // Start the calming music
            startCalmingMusic();
        });
    }
    
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the current ViewPager position
        outState.putInt(KEY_CURRENT_POSITION, currentPosition);
    }
    
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Keep the same position when configuration changes (like orientation)
        if (viewPager != null) {
            viewPager.setCurrentItem(currentPosition, false);
        }
        
        // Force portrait for non-video fragments, even during config changes
        if (!isVideoFragmentActive && newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Check if we need to force portrait when resuming
        if (!isVideoFragmentActive) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
    
    /**
     * Set up the ViewPager2 with the appropriate adapter based on phobia type
     */
    private void setupViewPager() {
        // Get fragment indices based on phobia type
        int[] fragmentIndices = PhobiaFragmentManager.getFragmentIndicesForPhobia(phobiaType);
        
        // Create the adapter with only the relevant fragments
        PhobiaSpecificAdapter adapter = new PhobiaSpecificAdapter(this, fragmentIndices);
        viewPager.setAdapter(adapter);
        
        // Set to saved position
        viewPager.setCurrentItem(currentPosition, false);
        
        // Check if we're on video fragment
        if (currentPosition < fragmentIndices.length) {
            int actualFragmentIndex = fragmentIndices[currentPosition];
            isVideoFragmentActive = (actualFragmentIndex == HEIGHT_VIDEO_FRAGMENT_INDEX || 
                                    actualFragmentIndex == SPACE_VIDEO_FRAGMENT_INDEX);
        }
        
        // Set up dots indicator
        dotsIndicator.setViewPager2(viewPager);
        
        // Set the activity title based on phobia type
        setTitle(getPhobiaTitle(phobiaType));
    }
    
    /**
     * Get a display title based on phobia type
     */
    private String getPhobiaTitle(String phobiaType) {
        switch (phobiaType) {
            case PhobiaFragmentManager.ACROPHOBIA:
                return "Acrophobia Treatment";
            case PhobiaFragmentManager.CLAUSTROPHOBIA:
                return "Claustrophobia Treatment";
            default:
                return "Phobia Treatment";
        }
    }
    
    /**
     * Start the MusicPlayerActivity with calming music
     */
    private void startCalmingMusic() {
        Intent intent = new Intent(this, MusicPlayerActivity.class);
        // Pass the "Relax" category for calming music
        intent.putExtra("category", "Relax");
        // Start with "Ocean Breeze" which is a calming track
        intent.putExtra("title", "Ocean Breeze");
        intent.putExtra("resId", R.raw.relax1);
        startActivity(intent);
    }
}
