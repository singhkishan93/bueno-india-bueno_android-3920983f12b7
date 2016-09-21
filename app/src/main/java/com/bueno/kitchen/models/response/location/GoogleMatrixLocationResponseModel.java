package com.bueno.kitchen.models.response.location;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bedi on 31/01/16.
 */
public class GoogleMatrixLocationResponseModel {
    @SerializedName("destination_addresses")
    public List<String> destinationAddresses = new ArrayList<>();
    @SerializedName("origin_addresses")
    public List<String> originAddresses = new ArrayList<>();
    public List<Row> rows = new ArrayList<>();
    public String status;

    public class Row {
        public List<Element> elements = new ArrayList<>();
    }

    public class Element {
        public Distance distance;
        public Duration duration;
        public String status;
    }

    public class Duration {
        public String text;
        public Double value;
    }

    public class Distance {
        public String text;
        public Double value;
    }

    public boolean isSuccess() {
        return status.equalsIgnoreCase("OK")
                && isNotEmpty()
                && rows != null
                && !rows.isEmpty()
                && rows.get(0).elements != null
                && !rows.get(0).elements.isEmpty()
                && rows.get(0).elements.get(0).status.equals("OK");
    }

    private boolean isNotEmpty() {
        return !destinationAddresses.isEmpty();
    }

    public Double getDistance() {
        Element element = rows.get(0).elements.get(0);
        return element.distance.value;
    }

    public double getDuration() {
        Element element = rows.get(0).elements.get(0);
        return element.duration.value;
    }
}
