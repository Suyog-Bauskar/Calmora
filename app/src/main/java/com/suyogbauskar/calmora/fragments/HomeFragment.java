package com.suyogbauskar.calmora.fragments;

import com.suyogbauskar.calmora.Adapter.ViewPagerAdapter;
import com.suyogbauskar.calmora.BasicCourse;
import com.suyogbauskar.calmora.MusicCategoryScreen;
import com.suyogbauskar.calmora.R;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;
import com.suyogbauskar.calmora.utils.PhobiaFragmentManager;
import com.suyogbauskar.calmora.utils.ProgressDialog;
import com.suyogbauskar.calmora.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class HomeFragment extends Fragment {

    private Button startButton, btn_relaxation_start;
    private RelativeLayout dailyThoughtButton;
    private ImageView signOutButton;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;
    private List<ThoughtItem> dailyThoughts;
    private Random random;
    private int lastThoughtIndex = -1;

    // Enum for categorizing thought types
    private enum ThoughtType {
        MINDFULNESS,
        PEACE,
        GRATITUDE,
        NATURE
    }

    // Class to hold thought information
    private static class ThoughtItem {
        String message;
        ThoughtType type;

        ThoughtItem(String message, ThoughtType type) {
            this.message = message;
            this.type = type;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog();
        
        // Initialize the daily thoughts collection
        initializeDailyThoughts();
        
        // Initialize random for selecting thoughts
        random = new Random();

        startButton = view.findViewById(R.id.btn_basics_start);
        btn_relaxation_start = view.findViewById(R.id.btn_relaxation_start);
        signOutButton = view.findViewById(R.id.iv_signout);

        // Show sign out button only if user is logged in
        if (auth.getCurrentUser() != null) {
            signOutButton.setVisibility(View.VISIBLE);
        } else {
            signOutButton.setVisibility(View.GONE);
        }

        startButton.setOnClickListener(v -> {
            // Load the phobia-specific course content
            startPhobiaSpecificCourse();
        });

        btn_relaxation_start.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MusicCategoryScreen.class);
            startActivity(intent);
        });

        // Sign out button click listener
        signOutButton.setOnClickListener(v -> showSignOutConfirmationDialog());

        // Daily thought dialog button
        dailyThoughtButton = view.findViewById(R.id.btn_daily_thought);
        dailyThoughtButton.setOnClickListener(v -> showDailyThoughtDialog());

        return view;
    }

    /**
     * Show a confirmation dialog before signing out
     */
    private void showSignOutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Sign Out");
        builder.setMessage("Are you sure you want to sign out?");
        builder.setPositiveButton("Yes", (dialog, which) -> signOut());
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    /**
     * Sign out the current user and navigate to login screen
     */
    private void signOut() {
        progressDialog.show(requireContext());
        
        try {
            // Clear the dialog shown preference for this user
            String userId = auth.getCurrentUser().getUid();
            resetPhobiaDialogPreference(userId);
            
            // Sign out the user
            auth.signOut();
            
            // Navigate to login screen
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Sign out failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            progressDialog.hide();
        }
    }
    
    /**
     * Reset the phobia dialog shown preference for the user
     * @param userId The user ID to reset the preference for
     */
    private void resetPhobiaDialogPreference(String userId) {
        if (userId == null) return;
        
        // Get shared preferences
        final String PREFS_NAME = "CalmOraPrefs";
        final String KEY_DIALOG_SHOWN = "phobia_dialog_shown";
        String dialogShownKey = KEY_DIALOG_SHOWN + "_" + userId;
        
        // Clear the preference
        requireActivity().getSharedPreferences(PREFS_NAME, requireActivity().MODE_PRIVATE)
                .edit()
                .remove(dialogShownKey)
                .apply();
    }

    /**
     * Initialize a collection of daily thoughts organized by theme
     */
    private void initializeDailyThoughts() {
        dailyThoughts = new ArrayList<>();
        
        // Mindfulness thoughts
        dailyThoughts.add(new ThoughtItem("Mindfulness is the key to being present in your own life.", ThoughtType.MINDFULNESS));
        dailyThoughts.add(new ThoughtItem("Your breath is your anchor to the present moment.", ThoughtType.MINDFULNESS));
        dailyThoughts.add(new ThoughtItem("You are not your thoughts. You are the observer of your thoughts.", ThoughtType.MINDFULNESS));
        dailyThoughts.add(new ThoughtItem("The quieter you become, the more you can hear.", ThoughtType.MINDFULNESS));
        dailyThoughts.add(new ThoughtItem("Every moment is a fresh beginning. Start now.", ThoughtType.MINDFULNESS));
        
        // Peace thoughts
        dailyThoughts.add(new ThoughtItem("Peace comes from within. Do not seek it without.", ThoughtType.PEACE));
        dailyThoughts.add(new ThoughtItem("Your calm mind is the ultimate weapon against your challenges.", ThoughtType.PEACE));
        dailyThoughts.add(new ThoughtItem("When you own your breath, nobody can steal your peace.", ThoughtType.PEACE));
        dailyThoughts.add(new ThoughtItem("Today I choose calm over worry and peace over perfection.", ThoughtType.PEACE));
        dailyThoughts.add(new ThoughtItem("Where there is peace, there is power.", ThoughtType.PEACE));
        
        // Gratitude thoughts
        dailyThoughts.add(new ThoughtItem("Gratitude turns what we have into enough.", ThoughtType.GRATITUDE));
        dailyThoughts.add(new ThoughtItem("Happiness is not something ready-made. It comes from your own actions.", ThoughtType.GRATITUDE));
        dailyThoughts.add(new ThoughtItem("Believe in yourself, every day is a new beginning!", ThoughtType.GRATITUDE));
        
        // Nature thoughts
        dailyThoughts.add(new ThoughtItem("Nature does not hurry, yet everything is accomplished.", ThoughtType.NATURE));
        dailyThoughts.add(new ThoughtItem("The mind is like water, when it's turbulent it's difficult to see. When it's calm, everything becomes clear.", ThoughtType.NATURE));
    }

    /**
     * Get a random thought different from the last one shown
     */
    private ThoughtItem getRandomThought() {
        if (dailyThoughts.size() <= 1) {
            return dailyThoughts.get(0);
        }
        
        int newIndex;
        do {
            newIndex = random.nextInt(dailyThoughts.size());
        } while (newIndex == lastThoughtIndex);
        
        lastThoughtIndex = newIndex;
        return dailyThoughts.get(newIndex);
    }

    /**
     * Get appropriate drawable resource for the thought type
     */
    private int getImageResourceForThought(ThoughtType type) {
        switch (type) {
            case MINDFULNESS:
                return R.drawable.thoughtful_mind;
            case PEACE:
                return R.drawable.peaceful_scene;
            case GRATITUDE:
                return R.drawable.grateful_moments;
            case NATURE:
            default:
                return R.drawable.calm_nature;
        }
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
        ImageView imageView = dialog.findViewById(R.id.imageViewThought);
        Button buttonClose = dialog.findViewById(R.id.buttonClose);

        // Get a random thought
        ThoughtItem thought = getRandomThought();
        
        // Set message and appropriate image
        textViewMessage.setText(thought.message);
        
        // Set image based on thought type
        if (imageView != null) {
            imageView.setImageResource(getImageResourceForThought(thought.type));
        }

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

    /**
     * Start the BasicCourse activity with phobia-specific fragments
     */
    private void startPhobiaSpecificCourse() {
        // Get the user's phobia type from Firestore
        PhobiaFragmentManager.getUserPhobiaType(getContext(), phobiaType -> {
            Intent intent = new Intent(getActivity(), BasicCourse.class);
            intent.putExtra("phobiaType", phobiaType);
            startActivity(intent);
        });
    }
}
