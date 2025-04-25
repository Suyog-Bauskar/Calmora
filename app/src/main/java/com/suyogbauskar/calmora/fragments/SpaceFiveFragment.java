package com.suyogbauskar.calmora.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.suyogbauskar.calmora.R;

/**
 * Fragment for displaying c_5 image with gyroscope-based 3D effect.
 */
public class SpaceFiveFragment extends GyroscopeImageFragment {

    public SpaceFiveFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_space_five, container, false);
    }
    
    @Override
    protected ImageView findImageView(View view) {
        // Find and return the ImageView from the layout
        return view.findViewById(R.id.space_image_five);
    }
} 