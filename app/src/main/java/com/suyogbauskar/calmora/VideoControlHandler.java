package com.suyogbauskar.calmora;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.VideoView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.suyogbauskar.calmora.fragments.HeightVideoFragment;
import com.suyogbauskar.calmora.fragments.SpaceVideoFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles video control operations based on Firebase field changes from doctor portal
 */
public class VideoControlHandler {
    
    private static final String TAG = "VideoControlHandler";
    private final FragmentActivity activity;
    private Fragment currentVideoFragment;
    private VideoView currentVideoView;
    private String currentVideoName;
    
    // Map video names to their corresponding fragments and raw resource IDs
    private Map<String, VideoInfo> videoMap;
    
    private static class VideoInfo {
        Class<? extends Fragment> fragmentClass;
        int rawResourceId;
        String layoutVideoViewId;
        
        VideoInfo(Class<? extends Fragment> fragmentClass, int rawResourceId, String layoutVideoViewId) {
            this.fragmentClass = fragmentClass;
            this.rawResourceId = rawResourceId;
            this.layoutVideoViewId = layoutVideoViewId;
        }
    }
    
    public VideoControlHandler(FragmentActivity activity) {
        this.activity = activity;
        initializeVideoMap();
    }
    
    /**
     * Initialize the mapping between video names and their corresponding fragments/resources
     */
    private void initializeVideoMap() {
        videoMap = new HashMap<>();
        
        // Map video names to fragments and resources (with and without file extensions)
        videoMap.put("acrophobia", new VideoInfo(HeightVideoFragment.class, R.raw.acrophobia, "height_video_player"));
        videoMap.put("acrophobia.mp4", new VideoInfo(HeightVideoFragment.class, R.raw.acrophobia, "height_video_player"));
        videoMap.put("height", new VideoInfo(HeightVideoFragment.class, R.raw.acrophobia, "height_video_player"));
        videoMap.put("heights", new VideoInfo(HeightVideoFragment.class, R.raw.acrophobia, "height_video_player"));
        
        videoMap.put("claustrophobia", new VideoInfo(SpaceVideoFragment.class, R.raw.claustrophobia, "space_video_player"));
        videoMap.put("claustrophobia.mp4", new VideoInfo(SpaceVideoFragment.class, R.raw.claustrophobia, "space_video_player"));
        videoMap.put("space", new VideoInfo(SpaceVideoFragment.class, R.raw.claustrophobia, "space_video_player"));
        videoMap.put("closed_spaces", new VideoInfo(SpaceVideoFragment.class, R.raw.claustrophobia, "space_video_player"));
        
        Log.d(TAG, "Initialized video map with " + videoMap.size() + " video types");
    }
    
    /**
     * Play a specific video based on the video name
     */
    public void playVideo(String videoName) {
        if (videoName == null || videoName.trim().isEmpty()) {
            Log.w(TAG, "Cannot play video: video name is null or empty");
            return;
        }
        
        videoName = videoName.toLowerCase().trim();
        Log.d(TAG, "Attempting to play video: " + videoName);
        
        VideoInfo videoInfo = videoMap.get(videoName);
        
        // If not found, try removing file extension
        if (videoInfo == null && videoName.contains(".")) {
            String nameWithoutExtension = videoName.substring(0, videoName.lastIndexOf('.'));
            videoInfo = videoMap.get(nameWithoutExtension);
            Log.d(TAG, "Trying without extension: " + nameWithoutExtension);
        }
        
        if (videoInfo == null) {
            Log.w(TAG, "Unknown video name: " + videoName);
            return;
        }
        
        try {
            // If we're already showing the same video, just resume it
            if (currentVideoFragment != null && videoName.equals(currentVideoName)) {
                resumeCurrentVideo();
                return;
            }
            
            // Load and show the appropriate video fragment
            loadVideoFragment(videoInfo, videoName);
            
        } catch (Exception e) {
            Log.e(TAG, "Error playing video: " + videoName, e);
        }
    }
    
    /**
     * Pause the currently playing video
     */
    public void pauseVideo() {
        Log.d(TAG, "Pausing current video");
        
        // Try fragment-specific methods first
        if (currentVideoFragment instanceof HeightVideoFragment) {
            ((HeightVideoFragment) currentVideoFragment).pauseVideo();
            return;
        } else if (currentVideoFragment instanceof SpaceVideoFragment) {
            ((SpaceVideoFragment) currentVideoFragment).pauseVideo();
            return;
        }
        
        // Fallback to direct VideoView control
        if (currentVideoView != null) {
            try {
                if (currentVideoView.isPlaying()) {
                    currentVideoView.pause();
                    Log.d(TAG, "Video paused successfully");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error pausing video", e);
            }
        } else {
            // Try to find VideoView in the current fragment
            pauseVideoInFragment();
        }
    }
    
    /**
     * Reset the current video to the beginning
     */
    public void resetVideo() {
        Log.d(TAG, "Resetting current video");
        
        // Try fragment-specific methods first
        if (currentVideoFragment instanceof HeightVideoFragment) {
            ((HeightVideoFragment) currentVideoFragment).resetVideo();
            return;
        } else if (currentVideoFragment instanceof SpaceVideoFragment) {
            ((SpaceVideoFragment) currentVideoFragment).resetVideo();
            return;
        }
        
        // Fallback to direct VideoView control
        if (currentVideoView != null) {
            try {
                boolean wasPlaying = currentVideoView.isPlaying();
                currentVideoView.seekTo(0);
                
                if (wasPlaying) {
                    currentVideoView.start();
                }
                Log.d(TAG, "Video reset successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error resetting video", e);
            }
        } else {
            // Try to find VideoView in the current fragment and reset
            resetVideoInFragment();
        }
    }
    
    /**
     * Close and clear the current video
     */
    public void closeVideo() {
        Log.d(TAG, "Closing current video");
        
        try {
            // Stop video using fragment-specific methods first
            if (currentVideoFragment instanceof HeightVideoFragment) {
                ((HeightVideoFragment) currentVideoFragment).stopVideo();
            } else if (currentVideoFragment instanceof SpaceVideoFragment) {
                ((SpaceVideoFragment) currentVideoFragment).stopVideo();
            }
            
            // Stop and clear video view
            if (currentVideoView != null) {
                if (currentVideoView.isPlaying()) {
                    currentVideoView.stopPlayback();
                }
                currentVideoView.setVisibility(View.GONE);
                currentVideoView = null;
            }
            
            // Remove video fragment
            if (currentVideoFragment != null) {
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .remove(currentVideoFragment)
                        .commitAllowingStateLoss();
                currentVideoFragment = null;
            }
            
            // Clear current video name
            currentVideoName = null;
            
            Log.d(TAG, "Video closed successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error closing video", e);
        }
    }
    
    /**
     * Load and display a video fragment
     */
    private void loadVideoFragment(VideoInfo videoInfo, String videoName) {
        try {
            // Close any existing video first
            closeVideo();
            
            // Create new fragment instance
            Fragment fragment = videoInfo.fragmentClass.newInstance();
            
            // Set external control flag if fragment supports it
            if (fragment instanceof HeightVideoFragment) {
                ((HeightVideoFragment) fragment).setExternallyControlled(true);
            } else if (fragment instanceof SpaceVideoFragment) {
                ((SpaceVideoFragment) fragment).setExternallyControlled(true);
            }
            
            // Add fragment to the activity
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.frameLayout, fragment, "video_fragment")
                    .commit();
            
            currentVideoFragment = fragment;
            currentVideoName = videoName;
            
            // Post a runnable to find the VideoView and start playing after the fragment is created
            activity.runOnUiThread(() -> {
                activity.findViewById(android.R.id.content).post(() -> {
                    findAndSetupVideoView(videoInfo);
                    // Start playing the video immediately
                    playCurrentVideo();
                });
            });
            
            Log.d(TAG, "Video fragment loaded: " + videoInfo.fragmentClass.getSimpleName());
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading video fragment", e);
        }
    }
    
    /**
     * Find and setup the VideoView in the current fragment
     */
    private void findAndSetupVideoView(VideoInfo videoInfo) {
        try {
            // Try to find the VideoView by ID
            int videoViewId = activity.getResources().getIdentifier(
                    videoInfo.layoutVideoViewId, "id", activity.getPackageName());
            
            if (videoViewId != 0) {
                View view = activity.findViewById(videoViewId);
                if (view instanceof VideoView) {
                    currentVideoView = (VideoView) view;
                    
                    // Setup video URI
                    String videoPath = "android.resource://" + activity.getPackageName() + "/" + videoInfo.rawResourceId;
                    currentVideoView.setVideoURI(Uri.parse(videoPath));
                    
                    // Start playing
                    currentVideoView.setOnPreparedListener(mp -> {
                        currentVideoView.start();
                        Log.d(TAG, "Video started playing");
                    });
                    
                    Log.d(TAG, "VideoView found and configured");
                } else {
                    Log.w(TAG, "Found view is not a VideoView: " + view);
                }
            } else {
                Log.w(TAG, "VideoView ID not found: " + videoInfo.layoutVideoViewId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error finding VideoView", e);
        }
    }
    
    /**
     * Resume the current video if it exists
     */
    private void resumeCurrentVideo() {
        // Try fragment-specific methods first
        if (currentVideoFragment instanceof HeightVideoFragment) {
            ((HeightVideoFragment) currentVideoFragment).playVideo();
            return;
        } else if (currentVideoFragment instanceof SpaceVideoFragment) {
            ((SpaceVideoFragment) currentVideoFragment).playVideo();
            return;
        }
        
        // Fallback to direct VideoView control
        if (currentVideoView != null) {
            try {
                if (!currentVideoView.isPlaying()) {
                    currentVideoView.start();
                    Log.d(TAG, "Current video resumed");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error resuming current video", e);
            }
        }
    }
    
    /**
     * Start playing the current video fragment
     */
    private void playCurrentVideo() {
        // Try fragment-specific methods first
        if (currentVideoFragment instanceof HeightVideoFragment) {
            ((HeightVideoFragment) currentVideoFragment).playVideo();
        } else if (currentVideoFragment instanceof SpaceVideoFragment) {
            ((SpaceVideoFragment) currentVideoFragment).playVideo();
        }
    }
    
    /**
     * Pause video in the current fragment by finding VideoView
     */
    private void pauseVideoInFragment() {
        try {
            // Try common VideoView IDs
            String[] videoViewIds = {"height_video_player", "space_video_player", "video_player"};
            
            for (String viewId : videoViewIds) {
                int id = activity.getResources().getIdentifier(viewId, "id", activity.getPackageName());
                if (id != 0) {
                    View view = activity.findViewById(id);
                    if (view instanceof VideoView) {
                        VideoView videoView = (VideoView) view;
                        if (videoView.isPlaying()) {
                            videoView.pause();
                            Log.d(TAG, "Video paused in fragment");
                            return;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error pausing video in fragment", e);
        }
    }
    
    /**
     * Reset video in the current fragment by finding VideoView
     */
    private void resetVideoInFragment() {
        try {
            // Try common VideoView IDs
            String[] videoViewIds = {"height_video_player", "space_video_player", "video_player"};
            
            for (String viewId : videoViewIds) {
                int id = activity.getResources().getIdentifier(viewId, "id", activity.getPackageName());
                if (id != 0) {
                    View view = activity.findViewById(id);
                    if (view instanceof VideoView) {
                        VideoView videoView = (VideoView) view;
                        boolean wasPlaying = videoView.isPlaying();
                        videoView.seekTo(0);
                        if (wasPlaying) {
                            videoView.start();
                        }
                        Log.d(TAG, "Video reset in fragment");
                        return;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error resetting video in fragment", e);
        }
    }
    
    /**
     * Get the current video name being played
     */
    public String getCurrentVideoName() {
        return currentVideoName;
    }
    
    /**
     * Check if a video is currently playing
     */
    public boolean isVideoPlaying() {
        // Try fragment-specific methods first
        if (currentVideoFragment instanceof HeightVideoFragment) {
            return ((HeightVideoFragment) currentVideoFragment).isVideoPlaying();
        } else if (currentVideoFragment instanceof SpaceVideoFragment) {
            return ((SpaceVideoFragment) currentVideoFragment).isVideoPlaying();
        }
        
        // Fallback to direct VideoView control
        if (currentVideoView != null) {
            try {
                return currentVideoView.isPlaying();
            } catch (Exception e) {
                Log.e(TAG, "Error checking if video is playing", e);
            }
        }
        return false;
    }
}
