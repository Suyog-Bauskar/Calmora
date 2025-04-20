package com.suyogbauskar.calmora;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MusicCategoryScreen extends AppCompatActivity {
    private LinearLayout relaxCard, spiritualCard, energeticCard, stressFreeCard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_music_category_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        relaxCard = findViewById(R.id.relaxCard);
        spiritualCard = findViewById(R.id.spiritualCard);
        energeticCard = findViewById(R.id.energeticCard);
        stressFreeCard = findViewById(R.id.stressFreeCard);

        View.OnClickListener listener = v -> {
            String category = "";
            if (v.getId() == R.id.relaxCard) category = "Relax";
            if (v.getId() == R.id.spiritualCard) category = "Spiritual";
            if (v.getId() == R.id.energeticCard) category = "Energetic";
            if (v.getId() == R.id.stressFreeCard) category = "Stress Free";

            Intent intent = new Intent(this, MusicListActivity.class);
            intent.putExtra("category", category);
            startActivity(intent);
        };

        relaxCard.setOnClickListener(listener);
        spiritualCard.setOnClickListener(listener);
        energeticCard.setOnClickListener(listener);
        stressFreeCard.setOnClickListener(listener);
    }
}



