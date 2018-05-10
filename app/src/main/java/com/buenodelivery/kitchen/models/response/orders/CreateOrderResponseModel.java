package com.buenodelivery.kitchen.models.response.orders;

import com.buenodelivery.kitchen.models.core.BaseModel;
import com.google.gson.annotations.SerializedName;

/**
 * Created by navjot on 12/11/15.
 */
public class CreateOrderResponseModel extends BaseModel {
    @SerializedName("order_id")
    public String orderId;
}
