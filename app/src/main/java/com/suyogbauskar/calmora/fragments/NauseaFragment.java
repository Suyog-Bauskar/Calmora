package com.suyogbauskar.calmora.fragments;

import static java.time.temporal.TemporalAdjusters.next;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.suyogbauskar.calmora.HomeActivity;
import com.suyogbauskar.calmora.ImageContainer;
import com.suyogbauskar.calmora.QuestionsActivity;
import com.suyogbauskar.calmora.R;
import com.suyogbauskar.calmora.StartActivity;


public class NauseaFragment extends Fragment {
    private FloatingActionButton fab_next;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nausea, container, false);

        fab_next = view.findViewById(R.id.fab_next);

        fab_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ImageContainer.class));
            }
        });

        return view;
    }
}

