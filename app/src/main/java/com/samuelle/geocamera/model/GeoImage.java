package com.samuelle.geocamera.model;

import com.google.android.gms.maps.model.LatLng;

public class GeoImage {
    private String fileName;
    private String filePath;
    private LatLng latLng;
    private long timeStamp;

    public GeoImage(String fileName, String filePath, LatLng latLng, long timeStamp) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.latLng = latLng;
        this.timeStamp = timeStamp;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
