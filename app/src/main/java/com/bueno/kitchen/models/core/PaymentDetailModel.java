package com.bueno.kitchen.models.core;

/**
 * Created by bedi on 20/03/16.
 */
public class PaymentDetailModel {

    public String orderId;
    public String amount;
    public String address;

    public PaymentDetailModel(String orderId, String amount, String address) {
        this.orderId = orderId;
        this.amount = String.format("%.2f", Double.valueOf(amount));
        this.address = address;
    }
}
