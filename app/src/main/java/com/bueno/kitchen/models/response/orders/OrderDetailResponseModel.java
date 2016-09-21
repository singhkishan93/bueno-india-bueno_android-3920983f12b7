package com.bueno.kitchen.models.response.orders;

import com.google.gson.annotations.SerializedName;

/**
 * Created by navjot on 8/1/16.
 */
public class OrderDetailResponseModel {
    @SerializedName("order_status")
    public String orderStatus;
    @SerializedName("jooleh_order_no")
    public String joolehOrderNumber;
}
