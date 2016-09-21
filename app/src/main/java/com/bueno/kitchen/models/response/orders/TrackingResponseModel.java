package com.bueno.kitchen.models.response.orders;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by navjot on 8/1/16.
 */
public class TrackingResponseModel {
    public double latitude;
    public double longitude;

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }
}
