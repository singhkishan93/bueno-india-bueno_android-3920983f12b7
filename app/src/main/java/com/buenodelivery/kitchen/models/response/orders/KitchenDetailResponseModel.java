package com.buenodelivery.kitchen.models.response.orders;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

/**
 * Created by intel on 12/8/2016.
 */

public class KitchenDetailResponseModel {
    @SerializedName("delivery_charge")
    public String delivery_charge;

    public void print_log (String subject)
    {
        Log.d("addy_"+subject,delivery_charge+"");

    }

}
