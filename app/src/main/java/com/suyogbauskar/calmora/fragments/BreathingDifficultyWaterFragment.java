package com.suyogbauskar.calmora.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.suyogbauskar.calmora.R;

/**
 * A simple {@link Fragment} subclass.
 * Fragment for displaying breathing difficulty symptoms for hydrophobia.
 */
public class BreathingDifficultyWaterFragment extends Fragment {

    public BreathingDifficultyWaterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_breathing_difficulty_water, container, false);
    }
}
