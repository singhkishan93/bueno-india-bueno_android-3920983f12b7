package com.buenodelivery.kitchen.network.services;

import com.buenodelivery.kitchen.models.response.orders.TrackingResponseModel;
import com.buenodelivery.kitchen.utils.Config;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by navjot on 8/1/16.
 */
public interface JoolehService {
    @GET(Config.Endpoints.URL_TRACK_ORDER)
    Call<TrackingResponseModel> getTackingDetails(@Query("oid") String orderId);
}
