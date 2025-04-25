package com.suyogbauskar.calmora.fragments;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.suyogbauskar.calmora.MusicPlayerActivity;
import com.suyogbauskar.calmora.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FearSpaceImg#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FearSpaceImg extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FloatingActionButton panicButton;

    public FearSpaceImg() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FearSpaceImg.
     */
    // TODO: Rename and change types and number of parameters
    public static FearSpaceImg newInstance(String param1, String param2) {
        FearSpaceImg fragment = new FearSpaceImg();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fear_space_img, container, false);
        
        // Find and set up the panic button
        panicButton = view.findViewById(R.id.panicButton);
        
        // Show a tooltip on long press
        panicButton.setOnLongClickListener(v -> {
            Toast.makeText(getContext(), "Press for immediate calming music", Toast.LENGTH_SHORT).show();
            return true;
        });
        
        // Set click listener to start calming music
        panicButton.setOnClickListener(v -> {
            startCalmingMusic();
        });
        
        return view;
    }
    
    /**
     * Start the MusicPlayerActivity with calming music
     */
    private void startCalmingMusic() {
        Intent intent = new Intent(getActivity(), MusicPlayerActivity.class);
        // Pass the "Relax" category for calming music
        intent.putExtra("category", "Relax");
        // Start with "Ocean Breeze" which is a calming track
        intent.putExtra("title", "Ocean Breeze");
        intent.putExtra("resId", R.raw.relax1);
        startActivity(intent);
    }
}