package com.suyogbauskar.calmora;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.suyogbauskar.calmora.Adapter.PhobiaSpecificAdapter;
import com.suyogbauskar.calmora.Adapter.ViewPagerAdapter;
import com.suyogbauskar.calmora.utils.PhobiaFragmentManager;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

public class BasicCourse extends AppCompatActivity {

    private ViewPager2 viewPager;
    private WormDotsIndicator dotsIndicator;
    private FloatingActionButton panicButton;
    private String phobiaType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_basic_course);

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

        viewPager = findViewById(R.id.viewPager);
        dotsIndicator = findViewById(R.id.dotsIndicator);
        panicButton = findViewById(R.id.panicButton);

        viewPager.setSaveEnabled(false);

        setupViewPager();
        
        // Show a short tooltip when the button is long-pressed
        panicButton.setOnLongClickListener(v -> {
            Toast.makeText(this, "Press for immediate calming music", Toast.LENGTH_SHORT).show();
            return true;
        });
        
        // Set up panic button click listener
        panicButton.setOnClickListener(v -> {
            // Show a brief toast message
            Toast.makeText(this, "Starting calming music...", Toast.LENGTH_SHORT).show();
            // Start the calming music
            startCalmingMusic();
        });
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
