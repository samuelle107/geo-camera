package com.samuelle.geocamera.view;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.samuelle.geocamera.R;
import com.samuelle.geocamera.presenter.CameraPresenter;

public class CameraFragment extends Fragment {
    private TextureView textureView;
    private ImageButton imageCaptureButton;
    private CameraPresenter presenter;

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        presenter = new CameraPresenter(getActivity());

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.camera_layout, container, false);
        textureView = rootView.findViewById(R.id.view_finder);
        imageCaptureButton = rootView.findViewById(R.id.imageCaptureButton);

        startCamera();

        return rootView;
    }

    @SuppressLint("RestrictedApi")
    private void startCamera() {
        CameraX.unbindAll();

        // Display the preview to the textureView
        Preview preview = presenter.configurePreview(textureView);
        // Get an imageCapture object
        final ImageCapture imageCapture = presenter.configureImageCapture();

        // Take a picture when the capture button is clicked
        imageCaptureButton.setOnClickListener(v -> {
            presenter.takePicture(imageCapture);
        });

        CameraX.bindToLifecycle(this, preview, imageCapture);
    }
}
