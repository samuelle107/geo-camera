package com.samuelle.geocamera.presenter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationManager;
import android.os.Environment;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.camera.core.AspectRatio;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.core.content.ContextCompat;

import java.io.File;

public class CameraPresenter {
    private Activity context;

    public CameraPresenter(Activity context) {
        this.context = context;
    }

    // Get the current location via the network provider
    private Location getCurrentLocation() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            return location;
        }

        return null;
    }

    // Callback for what will happen when the image is saved
    private ImageCapture.OnImageSavedListener getOnImageSavedListener() {
        return new ImageCapture.OnImageSavedListener() {
            @Override
            public void onImageSaved(File file) {
                String message = "Saved at" + file.getAbsolutePath();
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(
                    ImageCapture.ImageCaptureError imageCaptureError,
                    String errorMessage,
                    Throwable cause) {
                String message = "Capture failed : " + errorMessage;
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                if(cause != null){
                    cause.printStackTrace();
                }
            }
        };
    }

    // Update the texture view based on rotation
    private void updateTransform(TextureView textureView) {
        Matrix matrix = new Matrix();

        float cX = textureView.getMeasuredWidth() / 2f;
        float cY = textureView.getMeasuredHeight() / 2f;

        float rotationDgr;
        int rotation = (int) textureView.getRotation();

        switch (rotation) {
            case Surface.ROTATION_0:
                rotationDgr = 0f;
                break;
            case Surface.ROTATION_90:
                rotationDgr = 90f;
                break;
            case Surface.ROTATION_180:
                rotationDgr = 180f;
                break;
            case Surface.ROTATION_270:
                rotationDgr = 270f;
                break;
            default:
                return;
        }

        matrix.postRotate(rotationDgr, cX, cY);
        textureView.setTransform(matrix);
    }

    // Configure the preview for the camera
    public Preview configurePreview(TextureView textureView) {
        PreviewConfig previewConfig = new PreviewConfig
                .Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .build();
        Preview preview = new Preview(previewConfig);

        preview.setOnPreviewOutputUpdateListener(output -> {
            ViewGroup parent = (ViewGroup) textureView.getParent();
            parent.removeView(textureView);
            parent.addView(textureView, 0);

            textureView.setSurfaceTexture(output.getSurfaceTexture());
            updateTransform(textureView);
        });

        return preview;
    }

    // Configure the imageCapture
    public ImageCapture configureImageCapture() {
        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig
                .Builder()
                .setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                .setTargetRotation(
                        context
                        .getWindowManager()
                        .getDefaultDisplay()
                        .getRotation())
                .build();

        ImageCapture imageCapture = new ImageCapture(imageCaptureConfig);

        return imageCapture;
    }

    // Called when the user captures an image
    // Save the image to DCIM/GEO_CAMERA for easy access
    @SuppressLint("RestrictedApi")
    public void takePicture(ImageCapture imageCapture) {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/Geo_Camera");
        path.mkdirs(); // Create a folder if /Geo_Camera does not exist

        File file = new File(path, "IMG_" + System.currentTimeMillis() + ".jpeg");
        ImageCapture.Metadata metadata = new ImageCapture.Metadata();
        // Add the current location to the metadata
        metadata.location = getCurrentLocation();

        imageCapture.takePicture(
                file,
                metadata,
                CameraXExecutors.mainThreadExecutor(),
                getOnImageSavedListener());
    }
}
