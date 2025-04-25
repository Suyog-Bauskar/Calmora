package com.suyogbauskar.calmora.fragments;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.suyogbauskar.calmora.R;

/**
 * Fragment that plays a fear of heights video in fullscreen landscape mode.
 * The video will automatically play and loop continuously.
 */
public class HeightVideoFragment extends Fragment {

    private VideoView videoView;
    private int currentPosition = 0;
    private boolean isLandscapeSet = false;
    private boolean isVisible = false;

    public HeightVideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this fragment across configuration changes
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                          Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_height_video, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Set up the video view
        videoView = view.findViewById(R.id.height_video_player);
        
        // Set the video source from raw resources
        String videoPath = "android.resource://" + requireActivity().getPackageName() + "/" + R.raw.fear_of_height;
        videoView.setVideoURI(Uri.parse(videoPath));
        
        // Set up looping by listening for completion
        videoView.setOnCompletionListener(mp -> {
            // Restart the video when it ends
            videoView.start();
        });
        
        // Set up error listener
        videoView.setOnErrorListener((mp, what, extra) -> {
            // Handle errors gracefully
            return false;
        });
        
        // Set up prepared listener to ensure good playback
        videoView.setOnPreparedListener(mp -> {
            // Set looping directly if API level permits
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mp.setLooping(true);
            }
            
            // Set video to fill the screen while maintaining aspect ratio
            mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            
            // If we have a saved position, seek to it
            if (currentPosition > 0) {
                videoView.seekTo(currentPosition);
            }
            
            // Start playing automatically if visible
            if (isVisible) {
                videoView.start();
            }
        });
    }
    
    /**
     * For compatibility with ViewPager (and some versions of ViewPager2)
     */
    @Override
    @SuppressWarnings("deprecation")
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible = isVisibleToUser;
        
        if (isVisibleToUser) {
            // Force landscape orientation when this fragment becomes visible
            if (getActivity() != null) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                isLandscapeSet = true;
            }
            
            // Start video if prepared
            if (videoView != null && !videoView.isPlaying()) {
                videoView.start();
            }
        } else {
            // Reset to portrait when leaving this fragment
            if (getActivity() != null && isLandscapeSet) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                isLandscapeSet = false;
            }
            
            // Pause video
            if (videoView != null && videoView.isPlaying()) {
                currentPosition = videoView.getCurrentPosition();
                videoView.pause();
            }
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        isVisible = true;
        
        // Force landscape orientation for immersive video experience
        if (getActivity() != null) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            isLandscapeSet = true;
        }
        
        // Resume playback when fragment becomes visible again
        if (videoView != null) {
            if (currentPosition > 0) {
                videoView.seekTo(currentPosition);
            }
            videoView.start();
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        isVisible = false;
        
        // Save current position and pause video when fragment is not visible
        if (videoView != null && videoView.isPlaying()) {
            currentPosition = videoView.getCurrentPosition();
            videoView.pause();
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Save current position before destroying view
        if (videoView != null) {
            currentPosition = videoView.getCurrentPosition();
            videoView.stopPlayback();
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Reset orientation when leaving the fragment
        if (getActivity() != null && isLandscapeSet) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            isLandscapeSet = false;
        }
    }
} 