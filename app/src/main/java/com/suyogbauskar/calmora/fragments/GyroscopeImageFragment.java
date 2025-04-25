package com.suyogbauskar.calmora.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Base fragment class that provides gyroscope-based 3D effect functionality for images.
 * Height fragments will extend this class to get the 3D effect.
 */
public abstract class GyroscopeImageFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    protected ImageView imageView;
    private float[] rotationMatrix = new float[9];
    private float[] orientationValues = new float[3];
    
    // Parameters to control the 3D effect
    private float maxRotationAngle = 20.0f; // Maximum rotation angle in degrees - increased for more noticeable effect
    private float dampingFactor = 0.8f; // Reduces sensitivity
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Find the ImageView in the layout
        imageView = findImageView(view);
        
        // Set scale type to ensure the image fills the screen
        if (imageView != null) {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            // Scale image slightly larger to prevent edges from showing during rotation
            imageView.setScaleX(1.2f);
            imageView.setScaleY(1.2f);
        }
        
        // Initialize sensor manager and get gyroscope sensor
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            if (gyroscopeSensor == null) {
                // Fall back to accelerometer if gyroscope is not available
                gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            }
        }
    }
    
    /**
     * Find the ImageView to apply the 3D effect to.
     * Each subclass should implement this to return its specific ImageView.
     */
    protected abstract ImageView findImageView(View view);
    
    @Override
    public void onResume() {
        super.onResume();
        // Register sensor listener when fragment becomes active
        if (sensorManager != null && gyroscopeSensor != null) {
            sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister sensor listener when fragment is not visible
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (imageView == null) return;
        
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            // Convert rotation vector to orientation angles
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            SensorManager.getOrientation(rotationMatrix, orientationValues);
            
            // Get all three angles (azimuth, pitch, roll) - using azimuth for yaw
            float azimuthDegrees = (float) Math.toDegrees(orientationValues[0]) * dampingFactor;
            float pitchDegrees = (float) Math.toDegrees(orientationValues[1]) * dampingFactor;
            float rollDegrees = (float) Math.toDegrees(orientationValues[2]) * dampingFactor;
            
            // Limit rotation angles
            pitchDegrees = Math.max(Math.min(pitchDegrees, maxRotationAngle), -maxRotationAngle);
            rollDegrees = Math.max(Math.min(rollDegrees, maxRotationAngle), -maxRotationAngle);
            
            // Apply rotation to the image view (X=pitch/up-down, Y=roll/left-right, Z=azimuth/rotation)
            imageView.setRotationX(pitchDegrees);
            imageView.setRotationY(-rollDegrees);
            
            // Add parallax effect (slight movement)
            float translationX = rollDegrees * 1.5f;
            float translationY = pitchDegrees * 1.5f;
            imageView.setTranslationX(translationX);
            imageView.setTranslationY(translationY);
        }
        else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Simplified 3D effect using accelerometer data for all directions
            float x = event.values[0]; // left/right tilt
            float y = event.values[1]; // forward/backward tilt
            float z = event.values[2]; // gravitational force
            
            // Convert accelerometer values to rotation angles
            float rollDegrees = (x / SensorManager.GRAVITY_EARTH) * maxRotationAngle * dampingFactor;
            float pitchDegrees = (y / SensorManager.GRAVITY_EARTH) * maxRotationAngle * dampingFactor;
            
            // Apply rotation to the image view
            imageView.setRotationX(pitchDegrees);
            imageView.setRotationY(-rollDegrees);
            
            // Add parallax effect (slight movement)
            float translationX = rollDegrees * 1.5f;
            float translationY = pitchDegrees * 1.5f;
            imageView.setTranslationX(translationX);
            imageView.setTranslationY(translationY);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used in this implementation
    }
} 