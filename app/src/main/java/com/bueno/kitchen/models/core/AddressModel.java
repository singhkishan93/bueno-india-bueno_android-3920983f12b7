package com.bueno.kitchen.models.core;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.bueno.kitchen.utils.Config;
import com.bueno.kitchen.utils.UtilitySingleton;
import com.google.gson.Gson;

/**
 * Created by navjot on 13/11/15.
 */
public class AddressModel implements Parcelable {
    public String address;
    public String name;
    public Integer _ID;
    public String lat;
    public String lng;

    public static final String TABLE_NAME = "Address";
    public static final String COLUMN_ID = "_ID";
    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_LAT = "LAT";
    public static final String COLUMN_LNG = "LNG";
    public static final String COLUMN_ADDRESS = "ADDRESS";

    public AddressModel() {
    }

    protected AddressModel(Parcel in) {
        address = in.readString();
        name = in.readString();
        _ID = in.readInt();
        lat = in.readString();
        lng = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(name);
        dest.writeInt(_ID);
        dest.writeString(lat);
        dest.writeString(lng);
    }

    public boolean isValidAddress(Location myLocation) {
        return UtilitySingleton.calculateDistance(myLocation, lat, lng) <= Config.MAX_SAME_ADDRESS_DISTANCE;
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<AddressModel> CREATOR = new Parcelable.Creator<AddressModel>() {
        @Override
        public AddressModel createFromParcel(Parcel in) {
            return new AddressModel(in);
        }

        @Override
        public AddressModel[] newArray(int size) {
            return new AddressModel[size];
        }
    };

    public String getJsonString() {
        return new Gson().toJson(this);
    }
}
