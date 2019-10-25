package com.samuelle.geocamera.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Environment;
import android.text.format.DateUtils;

import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.samuelle.geocamera.model.GeoImage;
import com.samuelle.geocamera.view.CustomInfoWindowAdapter;
import com.samuelle.geocamera.view.ImageActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapPresenter {
    private Activity context;
    private HashMap<Marker, String> markerImages;
    private ArrayList<GeoImage> geoImages;

    public MapPresenter(Activity context) {
        this.context = context;
    }

    // Get the current location based on the network
    public LatLng getCurrentPosition() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            try {
                return new LatLng(location.getLatitude(), location.getLongitude());
            } catch (Exception err) {
                return new LatLng(0,0);
            }
        }

        return new LatLng(0, 0);
    }

    // Get the address by reverse-geocoding
    private Address getReverseGeoLocation(LatLng latLng) {
        List<Address> addresses;

        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());

            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            return addresses.get(0);
        } catch (Exception err) {
            err.printStackTrace();
        }

        return null;
    }

    // Get all images from the image directory
    private ArrayList<GeoImage> getImagesFromDirectory() {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/Geo_Camera").toString();
        File directory = new File(path);
        File[] files = directory.listFiles();

        ArrayList<GeoImage> geoImages = new ArrayList<>();

        for (File file : files) {
            try {
                ExifInterface exif = new ExifInterface(file.getAbsolutePath());

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
                Date date = simpleDateFormat.parse(exif.getAttribute(ExifInterface.TAG_DATETIME));

                geoImages.add(new GeoImage(
                        file.getName(),
                        file.getAbsolutePath(),
                        new LatLng(exif.getLatLong()[0], exif.getLatLong()[1]),
                        date.getTime()));
            } catch (Exception err) {
                err.printStackTrace();
            }
        }

        return geoImages;
    }

    // Add markers for every image
    public void addMarkers(GoogleMap googleMap) {
        markerImages = new HashMap<>();
        geoImages = getImagesFromDirectory();

        for (GeoImage geoImage: geoImages) {
            Address address = getReverseGeoLocation(geoImage.getLatLng());
            String cityState = address.getLocality() + ", " + address.getAdminArea();
            String relativeTime = (String) DateUtils.getRelativeTimeSpanString(geoImage.getTimeStamp());

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(geoImage.getLatLng())
                    .title(cityState)
                    .snippet(relativeTime);

            Marker marker = googleMap.addMarker(markerOptions);
            markerImages.put(marker, geoImage.getFilePath());
        }

        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(context, markerImages);
        googleMap.setInfoWindowAdapter(adapter);
    }

    // Open an image activity and pass the image path as an extra
    public void openImageActivity(Marker marker) {
        Intent intent = new Intent(context, ImageActivity.class);
        intent.putExtra("imagePath", markerImages.get(marker));
        context.startActivity(intent);
    }
}
