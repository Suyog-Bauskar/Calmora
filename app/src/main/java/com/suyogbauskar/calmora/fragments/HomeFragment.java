package com.suyogbauskar.calmora.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.suyogbauskar.calmora.Adapter.ViewPagerAdapter;
import com.suyogbauskar.calmora.R;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;


public class HomeFragment extends Fragment {

    private ViewPager2 viewPager;
    private WormDotsIndicator dotsIndicator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        viewPager = view.findViewById(R.id.viewPager);
        dotsIndicator = view.findViewById(R.id.dotsIndicator);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Attach TabLayout with ViewPager2
        dotsIndicator.setViewPager2(viewPager);


        return view;
    }
}
