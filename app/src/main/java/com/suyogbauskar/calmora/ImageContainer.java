package com.suyogbauskar.calmora;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.suyogbauskar.calmora.Adapter.ViewPagerAdapter2;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

public class ImageContainer extends AppCompatActivity {
    private ViewPager2 viewPager2;
    private WormDotsIndicator dotsIndicator1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_image_container);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewPager2 = findViewById(R.id.viewPager2);
        dotsIndicator1 = findViewById(R.id.dotsIndicator1);
        ViewPagerAdapter2 adapter = new ViewPagerAdapter2(this);
        viewPager2.setAdapter(adapter);
        dotsIndicator1.setViewPager2(viewPager2);

    }
}