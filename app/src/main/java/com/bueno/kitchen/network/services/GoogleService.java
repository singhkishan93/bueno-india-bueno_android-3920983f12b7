package com.bueno.kitchen.network.services;

import com.bueno.kitchen.models.response.location.GoogleGeoCodingResponseModel;
import com.bueno.kitchen.models.response.location.GoogleMatrixLocationResponseModel;
import com.bueno.kitchen.utils.Config;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by bedi on 31/10/15.
 */
public interface GoogleService {

    @GET(Config.Endpoints.URL_GOOGLE_MATRIX)
    Observable<GoogleMatrixLocationResponseModel> getDirections(@Query("origins") String origins,
                                                                @Query("destinations") String destinations,
                                                                @Query("key") String serverKey);

    @GET(Config.Endpoints.URL_GOOGLE_REVERSE_GEOCODING)
    Call<GoogleGeoCodingResponseModel> getAddress(@Query("latlng") String latlng,
                                                  @Query("key") String serverKey);
}
