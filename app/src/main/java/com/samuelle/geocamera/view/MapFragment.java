package com.samuelle.geocamera.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.samuelle.geocamera.R;
import com.samuelle.geocamera.presenter.MapPresenter;

public class MapFragment extends Fragment {
    private GoogleMap googleMap;
    private MapPresenter presenter;

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        presenter = new MapPresenter(getActivity());
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.map_layout, container, false);
        // Get the map fragment after inflating the view
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_view);

        // This is called whenever the map is ready
        // It will initialize the map, animate the camera, add the markers, and add a custom info window
        mapFragment.getMapAsync(googleMap -> {
            this.googleMap = googleMap;
            this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(presenter.getCurrentPosition(), 15));

            presenter.addMarkers(googleMap);
            googleMap.setOnInfoWindowClickListener(marker -> presenter.openImageActivity(marker));
        });

        return rootView;
    }
}