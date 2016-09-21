package com.bueno.kitchen.models.response.location;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bedi on 21/02/16.
 */
public class GoogleGeoCodingResponseModel {
    @SerializedName("results")
    public List<Result> results = new ArrayList<Result>();
    @SerializedName("status")
    public String status;

    public class Bounds {

        @SerializedName("northeast")
        public Northeast northeast;
        @SerializedName("southwest")
        public Southwest southwest;

    }

    public class AddressComponent {

        @SerializedName("long_name")
        public String longName;
        @SerializedName("short_name")
        public String shortName;
        @SerializedName("types")
        public List<String> types = new ArrayList<String>();

    }

    public class Geometry {

        @SerializedName("bounds")
        public Bounds bounds;
        @SerializedName("location")
        public Location location;
        @SerializedName("location_type")
        public String locationType;
        @SerializedName("viewport")
        public Viewport viewport;

    }

    public class Location {

        @SerializedName("lat")
        public Double lat;
        @SerializedName("lng")
        public Double lng;

    }

    public class Northeast {

        @SerializedName("lat")
        public Double lat;
        @SerializedName("lng")
        public Double lng;

    }

    public class Northeast_ {

        @SerializedName("lat")
        public Double lat;
        @SerializedName("lng")
        public Double lng;

    }

    public class Result {

        @SerializedName("address_components")
        public List<AddressComponent> addressComponents = new ArrayList<AddressComponent>();
        @SerializedName("formatted_address")
        public String formattedAddress;
        @SerializedName("geometry")
        public Geometry geometry;
        @SerializedName("place_id")
        public String placeId;
        @SerializedName("types")
        public List<String> types = new ArrayList<String>();

    }

    public class Southwest {

        @SerializedName("lat")
        public Double lat;
        @SerializedName("lng")
        public Double lng;

    }

    public class Southwest_ {

        @SerializedName("lat")
        public Double lat;
        @SerializedName("lng")
        public Double lng;

    }

    public class Viewport {

        @SerializedName("northeast")
        public Northeast_ northeast;
        @SerializedName("southwest")
        public Southwest_ southwest;

    }

    public String calculateAddress() {
        String calculatedResult = null;
        if (status.equalsIgnoreCase("OK")) {
            for (Result result : results) {
                if (result.geometry != null && result.geometry.locationType.equalsIgnoreCase("ROOFTOP")) {
                    calculatedResult = joinAddress(result);
                    break;
                }
            }

            if (TextUtils.isEmpty(calculatedResult)) {
                for (Result result : results) {
                    if (result.geometry != null && result.geometry.locationType.equalsIgnoreCase("APPROXIMATE")) {
                        calculatedResult = joinAddress(result);
                        break;
                    }
                }
            }

            return calculatedResult;
        }
        return null;
    }

    private String joinAddress(Result result) {
        StringBuilder stringBuilder = new StringBuilder();
        for (AddressComponent addressComponent : result.addressComponents) {
            if (addressComponent.types.contains("sublocality_level_3")) {
                stringBuilder.append(addressComponent.longName)
                        .append(", ");
            } else if (addressComponent.types.contains("sublocality_level_2")) {
                stringBuilder.append(addressComponent.longName)
                        .append(", ");
            } else if (addressComponent.types.contains("sublocality_level_1")) {
                stringBuilder.append(addressComponent.longName)
                        .append(", ");
            } else if (addressComponent.types.contains("locality")) {
                stringBuilder.append(addressComponent.longName)
                        .append(", ");
            }
        }

        if (stringBuilder.toString().isEmpty()) {
            for (AddressComponent addressComponent : result.addressComponents) {
                stringBuilder.append(addressComponent.longName)
                        .append(", ");
            }
        }

        if (!stringBuilder.toString().isEmpty())
            return stringBuilder.toString().substring(0, stringBuilder.toString().length() - 2);
        return null;
    }
}
