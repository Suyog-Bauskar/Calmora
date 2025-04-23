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
import com.suyogbauskar.calmora.Adapter.ViewPagerAdapter;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

public class BasicCourse extends AppCompatActivity {

    private ViewPager2 viewPager;
    private WormDotsIndicator dotsIndicator;
    private FloatingActionButton panicButton;

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

        viewPager = findViewById(R.id.viewPager);
        dotsIndicator = findViewById(R.id.dotsIndicator);
        panicButton = findViewById(R.id.panicButton);

        viewPager.setSaveEnabled(false);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this); // or pass FragmentActivity context
        viewPager.setAdapter(adapter);

        dotsIndicator.setViewPager2(viewPager);
        
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
