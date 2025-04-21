package com.suyogbauskar.calmora.fragments;

import com.suyogbauskar.calmora.Adapter.ViewPagerAdapter;
import com.suyogbauskar.calmora.BasicCourse;
import com.suyogbauskar.calmora.MusicCategoryScreen;
import com.suyogbauskar.calmora.R;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

public class HomeFragment extends Fragment {

    private Button startButton , btn_relaxation_start;
    private RelativeLayout dailyThoughtButton; // Add this if using separate button to trigger dialog

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        startButton = view.findViewById(R.id.btn_basics_start);
        btn_relaxation_start = view.findViewById(R.id.btn_relaxation_start);

        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BasicCourse.class);
            startActivity(intent);
        });

        btn_relaxation_start.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MusicCategoryScreen.class);
            startActivity(intent);
        });

        // Optional: button to show daily thought dialog
        dailyThoughtButton = view.findViewById(R.id.btn_daily_thought); // Make sure this button exists in XML
        dailyThoughtButton.setOnClickListener(v -> showDailyThoughtDialog());

        return view;
    }

    private void showDailyThoughtDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_daily_thought);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Animate dialog appearance
        View dialogView = dialog.findViewById(R.id.dialogLayout);
        Animation enterAnim = AnimationUtils.loadAnimation(getContext(), R.anim.dialog_enter);
        dialogView.startAnimation(enterAnim);

        // Views
        TextView textViewTitle = dialog.findViewById(R.id.textViewTitle);
        TextView textViewMessage = dialog.findViewById(R.id.textViewMessage);
        Button buttonClose = dialog.findViewById(R.id.buttonClose);

        textViewMessage.setText("Believe in yourself, every day is a new beginning!");

        buttonClose.setOnClickListener(v -> {
            Animation exitAnim = AnimationUtils.loadAnimation(getContext(), R.anim.dialog_exit);
            dialogView.startAnimation(exitAnim);

            exitAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override public void onAnimationStart(Animation animation) {}
                @Override public void onAnimationRepeat(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }
}
