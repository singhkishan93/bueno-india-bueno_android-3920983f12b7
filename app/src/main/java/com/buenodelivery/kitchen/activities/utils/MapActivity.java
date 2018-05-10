package com.buenodelivery.kitchen.activities.utils;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.buenodelivery.kitchen.R;
import com.buenodelivery.kitchen.core.BaseActivity;
import com.buenodelivery.kitchen.core.BuenoApplication;
import com.buenodelivery.kitchen.models.response.orders.TrackingResponseModel;
import com.buenodelivery.kitchen.network.RestProcess;
import com.buenodelivery.kitchen.network.services.JoolehService;
import com.buenodelivery.kitchen.utils.Config;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import javax.inject.Inject;

import retrofit2.Response;

public class MapActivity extends BaseActivity implements OnMapReadyCallback {

    @Inject
    JoolehService joolehService;
    private GoogleMap googleMap;
    private LatLng latLng;
    private String orderId;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        orderId = getIntent().getStringExtra(Config.Intents.INTENT_ORDER_ID);
        if (!TextUtils.isEmpty(orderId)) {
            enableBackButton();
            BuenoApplication.getApp().getActivityComponents().inject(this);
            setUpMap();
            fetchLatLng();
        } else {
            finish();
        }
    }

    private void setUpMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void fetchLatLng() {
        joolehService.getTackingDetails(orderId)
                .enqueue(new RestProcess<>(new RestProcess.RestCallback<TrackingResponseModel>() {
                    @Override
                    public void success(TrackingResponseModel trackingResponseModel, Response<TrackingResponseModel> response) {
                        if (trackingResponseModel != null) {
                            latLng = trackingResponseModel.getLatLng();
                            updateMarker();
                        }
                    }

                    @Override
                    public void failure(Throwable t) {

                    }
                }, this, false));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_map:
                fetchLatLng();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        updateMarker();
    }

    private void updateMarker() {
        if (latLng != null && googleMap != null) {
            googleMap.clear();
            googleMap.setTrafficEnabled(true);
            googleMap.addMarker(new MarkerOptions()
                    .position(latLng));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        }
    }
}
