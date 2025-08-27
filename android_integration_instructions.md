# Android App Integration Instructions for Doctor Portal Video Control

## Overview
This document provides instructions for integrating real-time Firebase listeners in your Android app to automatically respond to video control changes made by doctors through the web portal. The Android app should monitor specific Firestore fields and automatically play, stop, reset, or close videos based on doctor actions.

## Firebase Firestore Structure
Your Android app should monitor the following fields in the user's document:

### User Document Path: `Users/{userId}`
- `isPlaying` (Boolean) - Controls video playback state
- `videoName` (String) - Specifies which video file to play
- `closeVideo` (Boolean) - Triggers video closure and cleanup
- `isVideoReset` (Boolean) - Triggers video reset to beginning

## Implementation Requirements

### 1. Real-time Listener Setup
- Add Firebase Firestore real-time listeners on the home screen when the app opens and user is logged in
- Listen to the user's document: `Users/{currentUserId}`
- Monitor changes to the four key fields: `isPlaying`, `videoName`, `closeVideo`, `isVideoReset`

### 2. Video Control Logic

#### When `isPlaying` changes to `true`:
- Check the `videoName` field to determine which video to play
- Automatically start playing the specified video file
- Ensure video player is visible and properly initialized
- Handle cases where video file might not exist locally

#### When `isPlaying` changes to `false`:
- Pause/stop the currently playing video
- Keep the video player visible but in paused state
- Do NOT clear the video or reset position unless `closeVideo` is also true

#### When `closeVideo` changes to `true`:
- Stop any currently playing video
- Clear the video player view
- Reset video position to beginning
- Hide video player interface
- Set local video state to closed/cleared
- Reset `closeVideo` back to `false` in Firestore after processing

#### When `isVideoReset` changes to `true`:
- Seek the current video to position 0 (beginning)
- If video was playing, continue playing from the beginning
- If video was paused, keep it paused at the beginning
- Reset `isVideoReset` back to `false` in Firestore after processing

### 3. Video File Management
- Ensure your existing video files are accessible by filename
- Map the `videoName` field values to your local video file paths
- Handle cases where requested video doesn't exist locally
- Consider video file formats and compatibility

### 4. Error Handling
- Handle network connectivity issues gracefully
- Manage cases where Firestore listener disconnects
- Provide fallback behavior if video files are missing
- Log appropriate error messages for debugging

### 5. Performance Optimization
- Only attach listeners when user is logged in and on home screen
- Properly detach listeners when leaving home screen or app
- Avoid memory leaks with proper listener cleanup
- Consider battery optimization for real-time listening

## Implementation Steps

1. **Setup Firebase Listeners**
   - Initialize Firestore listeners on home screen
   - Create listener for user document changes
   - Handle listener attachment/detachment lifecycle

2. **Create Video Control Handler**
   - Build centralized handler for processing field changes
   - Implement logic for each control type (play, stop, reset, close)
   - Ensure proper state management

3. **Integrate with Existing Video System**
   - Connect new listeners to your existing video playback code
   - Ensure compatibility with manual video controls
   - Handle conflicts between manual and automatic control

## Expected Behavior Flow

1. User opens Android app home screen
2. App attaches real-time listeners to user's Firestore document
3. Doctor uses web portal to control user's video
4. Firestore fields update in real-time
5. Android app receives field changes through listeners
6. App automatically responds by controlling video playback
7. User sees video start/stop/reset/close based on doctor actions
8. Process continues until user leaves home screen or logs out
