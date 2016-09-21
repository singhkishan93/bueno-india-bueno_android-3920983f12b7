package com.bueno.kitchen.models.response;

import com.bueno.kitchen.models.core.BaseModel;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by bedi on 14/11/15.
 */
public class CouponResponseModel extends BaseModel {
    @SerializedName("discount_amt")
    public double discountAmount;
    @SerializedName("discount_type")
    public String discountType;
    public String description;
    @SerializedName("cashback_amt")
    public double cashbackAmount;
    public Map<String, Item> items = new HashMap<>();

    public class Item {
        public String name;
        public String quantity;
    }

    public String getAddOnMessage() {
        Item item = null;
        if (items!=null && !items.isEmpty()) {
            Iterator itemsIterator = items.entrySet().iterator();
            Map.Entry<String, Item> entry = (Map.Entry<String, Item>) itemsIterator.next();
            item = entry.getValue();
        }

        switch (discountType) {
            case "credit":
                return String.format("%s Bueno rewards would be added to your account within 24 hours of order delivery.", cashbackAmount);
            case "freebie":
            case "meal":
                if (item != null) {
                    return String.format("Complimentary gift with your order: %s %s", item.quantity, item.name);
                }
                break;
            case "hybrid":
                StringBuilder response = new StringBuilder();
                if (item != null) {
                    response.append(String.format("Complimentary gift with your order: %s %s. ", item.quantity, item.name));
                }

                if (cashbackAmount > 0) {
                    response.append(String.format("%s Bueno rewards would be added to your account within 24 hours of order delivery.",
                            cashbackAmount));
                }

                if(response.length()>0)
                    return response.toString();
                return null;
            default:
                return null;
        }
        return null;
    }

    public String getDialogMessage() {
        Item item = null;
        if (items!=null && !items.isEmpty()) {
            Iterator itemsIterator = items.entrySet().iterator();
            Map.Entry<String, Item> entry = (Map.Entry<String, Item>) itemsIterator.next();
            item = entry.getValue();
        }

        switch (discountType) {
            case "cash":
                return String.format("Cash discount of Rs. %.2f applied to your order.", discountAmount);
            case "credit":
                return String.format("%s Bueno rewards would be added to your account within 24 hours of order delivery.", cashbackAmount);
            case "freebie":
                if (item != null) {
                    return String.format("You would receive %s %s along with your order.", item.quantity, item.name);
                }
                break;
            case "meal":
                if (item != null) {
                    return String.format("You would receive %s %s along with your order.", item.quantity, item.name);
                }
                break;
            case "hybrid":
                if (discountAmount > 0 && cashbackAmount > 0 && item != null) {
                    return String.format("Cash discount of Rs. %.2f applied to your order, %s Bueno rewards would be added to your account within 24 hours of order delivery and You would receive %s %s along with your order.",
                            discountAmount, cashbackAmount, item.quantity, item.name);
                } else if (discountAmount > 0 && cashbackAmount > 0 && item == null) {
                    return String.format("Cash discount of Rs. %.2f applied to your order and %s Bueno rewards would be added to your account within 24 hours of order delivery.",
                            discountAmount, cashbackAmount);
                } else if (discountAmount == 0 && cashbackAmount > 0 && item != null) {
                    return String.format("%s Bueno rewards would be added to your account within 24 hours of order delivery and You would receive %s %s along with your order.",
                            cashbackAmount, item.quantity, item.name);
                } else if (discountAmount > 0 && cashbackAmount == 0 && item != null) {
                    return String.format("Cash discount of Rs. %.2f applied to your order and You would receive %s %s along with your order.",
                            discountAmount, item.quantity, item.name);
                } else if (discountAmount > 0) {
                    return String.format("Cash discount of Rs. %.2f applied to your order.",
                            discountAmount);
                } else if (cashbackAmount > 0) {
                    return String.format("%s Bueno rewards would be added to your account within 24 hours of order delivery.",
                            cashbackAmount);
                } else if (item != null) {
                    return String.format("You would receive %s %s along with your order.",
                            item.quantity, item.name);
                }
                break;
        }
        return null;
    }

    public String getDialogTitle() {
        switch (discountType) {
            case "cash":
                return "Well Done!";
            case "credit":
                return "Great Job!";
            case "freebie":
                return "Great Job!";
            case "meal":
                return "Great Job!";
            case "hybrid":
                return "Well Done!";
            default:
                return "Well Done!";
        }
    }
}
