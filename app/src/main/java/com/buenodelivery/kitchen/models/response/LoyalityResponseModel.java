package com.buenodelivery.kitchen.models.response;

import com.buenodelivery.kitchen.models.core.BaseModel;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bedi on 09/03/16.
 */
public class LoyalityResponseModel extends BaseModel {
    @SerializedName("referral_code")
    public String referralCode;
    public String membership;
    public int points;
}
