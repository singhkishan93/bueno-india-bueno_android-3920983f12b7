package com.bueno.kitchen.managers.payment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bueno.kitchen.managers.payment.core.PaymentManager;
import com.bueno.kitchen.models.core.PaymentDetailModel;
import com.bueno.kitchen.utils.Config;
import com.paytm.pgsdk.PaytmMerchant;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by bedi on 20/03/16.
 */
public class PaytmManager extends PaymentManager {

    private Context context;

    public PaytmManager(Context context) {
        this.context = context;
    }

    @Override
    public void makePayment(PaymentDetailModel paymentDetailModel) {
        PaytmPGService Service = PaytmPGService.getProductionService();
        Map<String, String> paramMap = new HashMap<>();

        paramMap.put("REQUEST_TYPE", "DEFAULT");
        paramMap.put("ORDER_ID", paymentDetailModel.orderId);
        paramMap.put("MID", Config.PaymentGateways.PAYTM_MERCHANT_ID);
        paramMap.put("CUST_ID", preferenceManager.getMobileNumber());
        paramMap.put("CHANNEL_ID", "WAP");
//        paramMap.put("MERCHANT_KEY", Config.PaymentGateways.PAYTM_MERCHANT_KEY);
        paramMap.put("INDUSTRY_TYPE_ID", "Retail128");
        paramMap.put("WEBSITE", Config.PaymentGateways.PAYTM_WEBSITE);
        paramMap.put("TXN_AMOUNT", paymentDetailModel.amount);
        paramMap.put("THEME", "merchant");
        paramMap.put("EMAIL", preferenceManager.getEmail());
        paramMap.put("MOBILE_NO", preferenceManager.getMobileNumber());
        PaytmOrder Order = new PaytmOrder(paramMap);

        PaytmMerchant Merchant = new PaytmMerchant(Config.PaymentGateways.PAYTM_URL_CHECKSUM_GENERATOR,
                Config.PaymentGateways.PAYTM_URL_CHECKSUM_VERIFY);

        Service.initialize(Order, Merchant, null);

        Service.startPaymentTransaction(context, true, true,
                new PaytmPaymentTransactionCallback() {
                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        if (paymentCallback != null)
                            paymentCallback.onFailure(inErrorMessage);
                    }

                    @Override
                    public void onTransactionSuccess(Bundle inResponse) {
                        if (paymentCallback != null) {
                            JSONObject json = new JSONObject();
                            Set<String> keys = inResponse.keySet();
                            for (String key : keys) {
                                try {
                                     json.put(key, inResponse.get(key));
//                                    json.put(key, JSONObject.wrap(inResponse.get(key)));
                                } catch(JSONException e) {
                                    //Handle exception here
                                }
                            }
                            paymentCallback.onSuccess(json.toString());
                        }
                    }

                    @Override
                    public void onTransactionFailure(String inErrorMessage,
                                                     Bundle inResponse) {
                        if (paymentCallback != null)
                            paymentCallback.onFailure(inErrorMessage);
                    }

                    @Override
                    public void networkNotAvailable() {
                        if (paymentCallback != null)
                            paymentCallback.onFailure("networkNotAvailable");
                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                        if (paymentCallback != null)
                            paymentCallback.onFailure(inErrorMessage);
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode,
                                                      String inErrorMessage, String inFailingUrl) {
                        if (paymentCallback != null)
                            paymentCallback.onFailure(inErrorMessage);
                    }

                    @Override
                    public void onBackPressedCancelTransaction() {
                        if (paymentCallback != null)
                            paymentCallback.onFailure("onBackPressedCancelTransaction");
                    }

                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
