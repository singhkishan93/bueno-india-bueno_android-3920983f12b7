package com.buenodelivery.kitchen.models.core;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.buenodelivery.kitchen.Required_beans;
import com.buenodelivery.kitchen.utils.UtilitySingleton;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by navjot on 5/11/15.
 */
public class LocalityModel implements Parcelable {
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<LocalityModel> CREATOR = new Parcelable.Creator<LocalityModel>() {
        @Override
        public LocalityModel createFromParcel(Parcel in) {
            return new LocalityModel(in);
        }

        @Override
        public LocalityModel[] newArray(int size) {
            return new LocalityModel[size];
        }
    };
    public String id;
    public String city;
    public String geoAddress;
    @SerializedName("avg_delivery_time")
    public String averageDeliveryTime;
    @SerializedName("min_order_amount")
    public String minOrderAmount;
    public String vat;
    public String latitude;
    public String longitude;
    public float distance;
    public String errorMessage;

    Required_beans req_beans= new Required_beans();



    private LocalityModel() {
    }

    protected LocalityModel(Parcel in) {
        id = in.readString();

        city = in.readString();
        averageDeliveryTime = in.readString();
        minOrderAmount = in.readString();
        vat = in.readString();
        geoAddress = in.readString();


    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(city);
        dest.writeString(averageDeliveryTime);
        dest.writeString(minOrderAmount);
        dest.writeString(vat);
        dest.writeString(geoAddress);

        req_beans.setLocality_id(Integer.parseInt(id));
    }

    public double getVat() {
        try {
            return Double.valueOf(vat);
        } catch (Exception e) {
            return 0;
        }
    }

    public String getJsonString() {
        return new Gson().toJson(this);
    }

    public static LocalityModel getModel(String jsonString) {
        return new Gson().fromJson(jsonString, LocalityModel.class);
    }

    @Override
    public String toString() {
        return city;
    }

    public void calculateDistance(Location myLocation) {
        distance = UtilitySingleton.calculateDistance(myLocation, latitude, longitude);
    }

    @Override
    public boolean equals(Object o) {
        if (o != null) {
            if (o instanceof LocalityModel) {
                LocalityModel mLocalityModel = (LocalityModel) o;
                return mLocalityModel.id.equalsIgnoreCase(id);
            }
        }
        return false;
    }

    public Location getLocation() {
        if (!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)) {
            Location location = new Location("mylocation");
            location.setLatitude(Double.valueOf(latitude));
            location.setLongitude(Double.valueOf(longitude));
            return location;
        }
        return null;
    }

    public String getLocationCity(){
        String[] addressSplitted = geoAddress.split(", ");
        if(addressSplitted.length > 0){
            return addressSplitted[addressSplitted.length-1];
        }
        return "";
    }

    public boolean isLocationError() {
        return !TextUtils.isEmpty(errorMessage);
    }

    private String getTrimmedAddress(String fullAddress) {
        try {
            String finalAddress = fullAddress;
            if (!TextUtils.isEmpty(fullAddress)) {
                String[] addressArray = fullAddress.split(",");
                if (addressArray.length > 1) {
                    finalAddress = fullAddress.substring(fullAddress.indexOf(',') + 2);

                    if (addressArray.length > 3) {
                        int i = UtilitySingleton.ordinalIndexOf(finalAddress, ',', addressArray.length - 4);
                        if (i >= 0)
                            finalAddress = finalAddress.substring(0, i);
                    }
                }
                return finalAddress;
            }
        } catch (Exception e) {
            return fullAddress;
        }
        return fullAddress;
    }

    public static Builder Builder() {
        return new Builder();
    }

    public static class Builder {
        private final LocalityModel localityModel;

        public Builder() {
            localityModel = new LocalityModel();
        }

        public Builder setName(String name) {
            localityModel.geoAddress = localityModel.getTrimmedAddress(name);
            return this;
        }

        public Builder setLatitude(String latitude) {
            localityModel.latitude = latitude;
            return this;
        }

        public Builder setLocationError(String errorMessage) {
            localityModel.errorMessage = errorMessage;
            return this;
        }

        public Builder setLongitude(String longitude) {
            localityModel.longitude = longitude;
            return this;
        }

        public LocalityModel build() {
            return localityModel;
        }
    }
}
