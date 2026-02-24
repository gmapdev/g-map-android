package com.ibigroup.mobile.otp.android.customize;

import android.view.View;

import androidx.annotation.NonNull;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView;

public class CustomizeMarkerView extends MarkerView {
    private LatLng markerViewLatLng;
    private View markerView;

    /**
     * Create a MarkerView
     *
     * @param latLng latitude-longitude pair
     * @param view   an Android SDK View
     */
    public CustomizeMarkerView(@NonNull LatLng latLng, @NonNull View view) {
        super(latLng, view);

        markerViewLatLng = latLng;
        markerView = view;
    }

    public View getView() {
        return markerView;
    }

    public void setView(View view) {
        this.markerView = view;
    }

    public LatLng getMarkerViewLatLng() {
        return markerViewLatLng;
    }

    public void setMarkerViewLatLng(@NonNull LatLng latLng) {
        markerViewLatLng = latLng;
    }
}
