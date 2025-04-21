package com.suyogbauskar.calmora;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.suyogbauskar.calmora.Adapter.ViewPagerAdapter;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

public class BasicCourse extends AppCompatActivity {

    private ViewPager2 viewPager;
    private WormDotsIndicator dotsIndicator;

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

        viewPager.setSaveEnabled(false);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this); // or pass FragmentActivity context
        viewPager.setAdapter(adapter);

        dotsIndicator.setViewPager2(viewPager);
    }
}
