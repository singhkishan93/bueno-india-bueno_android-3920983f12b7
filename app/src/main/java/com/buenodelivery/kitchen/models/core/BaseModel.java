package com.buenodelivery.kitchen.models.core;

import com.google.gson.annotations.SerializedName;

/**
 * Created by navjot on 5/11/15.
 */
public class BaseModel {
    public int success;
    @SerializedName("error_message")
    public String errorMessage;
}
