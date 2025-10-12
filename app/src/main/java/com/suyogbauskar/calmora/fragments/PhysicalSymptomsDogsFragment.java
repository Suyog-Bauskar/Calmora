package com.suyogbauskar.calmora.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.suyogbauskar.calmora.R;

/**
 * A simple {@link Fragment} subclass.
 * Fragment for displaying physical symptoms for cynophobia.
 */
public class PhysicalSymptomsDogsFragment extends Fragment {

    public PhysicalSymptomsDogsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_physical_symptoms_dogs, container, false);
    }
}
