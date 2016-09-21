package com.bueno.kitchen.models.response.orders;

import com.bueno.kitchen.models.core.BaseModel;
import com.google.gson.annotations.SerializedName;

/**
 * Created by navjot on 12/11/15.
 */
public class CreateOrderResponseModel extends BaseModel {
    @SerializedName("order_id")
    public String orderId;
}
