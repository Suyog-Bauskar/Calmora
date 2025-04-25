package com.suyogbauskar.calmora.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.suyogbauskar.calmora.R;

/**
 * Fragment for displaying h_4 image with gyroscope-based 3D effect.
 */
public class HeightFourFragment extends GyroscopeImageFragment {

    public HeightFourFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_height_four, container, false);
    }
    
    @Override
    protected ImageView findImageView(View view) {
        // Find and return the ImageView from the layout
        return view.findViewById(R.id.height_image_four);
    }
} 