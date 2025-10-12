package com.suyogbauskar.calmora.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.suyogbauskar.calmora.R;

/**
 * Fragment for displaying dog image 5 with gyroscope-based 3D effect.
 */
public class DogFiveFragment extends GyroscopeImageFragment {

    public DogFiveFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dog_five, container, false);
    }
    
    @Override
    protected ImageView findImageView(View view) {
        // Find and return the ImageView from the layout
        return view.findViewById(R.id.dog_image_five);
    }
}
