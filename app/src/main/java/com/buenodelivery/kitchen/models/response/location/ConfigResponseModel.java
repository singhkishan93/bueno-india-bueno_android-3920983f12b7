package com.buenodelivery.kitchen.models.response.location;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by navjot on 3/2/16.
 */
public class ConfigResponseModel {

    @SerializedName("max_travel_distance")
    public Integer maxTravelDistance;
    @SerializedName("max_distance_from_nearest_locality")
    public Integer maxDistanceFromNearestLocality;
    @SerializedName("max_travel_time")
    public Integer maxTravelTime;
    @SerializedName("min_android_version")
    public Long minAndroidVersion;
    @SerializedName("kitchen_locations")
    public List<KitchenLocation> kitchenLocations = new ArrayList<>();
    @SerializedName("is_ordering_closed")
    public boolean isOrderingClosed;
    @SerializedName("ordering_closed_message")
    public String orderingClosedMessage;
    @SerializedName("no_dishes_error_message")
    public String noDishesErrorMessage;
    @SerializedName("contact_us_phone")
    public String contactUsPhone;
    @SerializedName("contact_us_email")
    public String contactUsEmail;
    @SerializedName("payment_methods_android")
    public ArrayList<Map<String, String>> paymentMethods=new ArrayList<>();
    @SerializedName("max_order_items")
    public int maxOrderItems;
    @SerializedName("max_credit_usage_percent")
    public int maxCreditUsagePercent;

    public void print_log (String subject,String log_param)
    {
        Log.d("addy_"+subject,log_param+"");
    }

    public class KitchenLocation {


        public Double latitude;
        public Double longitude;
    }
}
