package com.bueno.kitchen.network.services;

import com.bueno.kitchen.models.response.orders.TrackingResponseModel;
import com.bueno.kitchen.utils.Config;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by navjot on 8/1/16.
 */
public interface JoolehService {
    @GET(Config.Endpoints.URL_TRACK_ORDER)
    Call<TrackingResponseModel> getTackingDetails(@Query("oid") String orderId);
}
