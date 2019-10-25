package com.samuelle.geocamera.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.samuelle.geocamera.R;

import java.util.HashMap;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private Activity context;
    private HashMap<Marker, String> markerImages; // Used to pass non-marker data to the info window adapter

    public CustomInfoWindowAdapter(Activity context, HashMap<Marker, String> markerImages) {
        this.context = context;
        this.markerImages = markerImages;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        // Inflate the view
        View view = context
                .getLayoutInflater()
                .inflate(R.layout.custom_info_window, null);

        TextView infoHeader = view.findViewById(R.id.infoHeader);
        TextView infoTimeStamp = view.findViewById(R.id.infoTimeStamp);
        ImageView imageView = view.findViewById(R.id.infoPicture);

        // Set the info window content
        infoHeader.setText(marker.getTitle());
        infoHeader.setTypeface(null, Typeface.BOLD);
        infoHeader.setTextSize(15);
        infoTimeStamp.setText(marker.getSnippet());

        // Get the image from the hash table and display it
        Bitmap bitmap = BitmapFactory.decodeFile(markerImages.get(marker));
        imageView.setImageBitmap(bitmap);

        return view;
    }
}
