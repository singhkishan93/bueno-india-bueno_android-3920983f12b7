package com.buenodelivery.kitchen.network.services;

import com.buenodelivery.kitchen.models.OTPModel;
import com.buenodelivery.kitchen.utils.Config;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by bedi on 31/10/15.
 */
public interface OTPService {

    @GET(Config.Endpoints.URL_GET_OTP + "/{mode}/{phone_no}/{otp}")
    Call<OTPModel> sendOTP(@Path("mode") String otpMode,
                           @Path("phone_no") String phone,
                           @Path("otp") String otp);

}
