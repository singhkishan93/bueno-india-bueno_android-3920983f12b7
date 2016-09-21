package com.bueno.kitchen.managers.payment;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.bueno.kitchen.BuildConfig;
import com.bueno.kitchen.R;
import com.bueno.kitchen.managers.payment.core.PaymentManager;
import com.bueno.kitchen.models.core.PaymentDetailModel;
import com.bueno.kitchen.utils.Config;
import com.ebs.android.sdk.EBSPayment;
import com.ebs.android.sdk.PaymentRequest;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bedi on 20/03/16.
 */
public class EBSManager extends PaymentManager {

    private Context context;

    public EBSManager(Context context) {
        this.context = context;
    }

    @Override
    public void makePayment(PaymentDetailModel paymentDetailModel) {
        PaymentRequest.getInstance().setTransactionAmount(paymentDetailModel.amount);
        PaymentRequest.getInstance().setAccountId(Config.PaymentGateways.EBS_ACCOUNT_ID);
        PaymentRequest.getInstance().setSecureKey(Config.PaymentGateways.EBS_SECRET_KEY);
        PaymentRequest.getInstance().setReferenceNo(paymentDetailModel.orderId);
        PaymentRequest.getInstance().setBillingEmail(preferenceManager.getEmail());
        PaymentRequest.getInstance().setCurrency("INR");
        PaymentRequest.getInstance().setPage_id(Config.PaymentGateways.EBS_PAGE_ID);

        /** Optional */
        PaymentRequest.getInstance().setTransactionDescription(String.format("Order#%s", paymentDetailModel.orderId));

        /** Billing Details */
        PaymentRequest.getInstance().setBillingName(preferenceManager.getUserName());
        PaymentRequest.getInstance().setBillingAddress(paymentDetailModel.address);
        PaymentRequest.getInstance().setBillingCity(preferenceManager.getLocality().getLocationCity());
        PaymentRequest.getInstance().setBillingState("Haryana");
        PaymentRequest.getInstance().setBillingCountry("IND");
        PaymentRequest.getInstance().setBillingPostalCode("122001");
        PaymentRequest.getInstance().setBillingPhone(preferenceManager.getMobileNumber());

        /** Shipping Details */
        PaymentRequest.getInstance().setShippingName(preferenceManager.getUserName());
        PaymentRequest.getInstance().setShippingAddress(paymentDetailModel.address);
        PaymentRequest.getInstance().setShippingCity(preferenceManager.getLocality().getLocationCity());
        PaymentRequest.getInstance().setShippingState("Haryana");
        PaymentRequest.getInstance().setShippingCountry("IND");
        PaymentRequest.getInstance().setShippingPostalCode("122001");
        PaymentRequest.getInstance().setShippingEmail(preferenceManager.getEmail());
        PaymentRequest.getInstance().setShippingPhone(preferenceManager.getMobileNumber());

        PaymentRequest.getInstance().setFailuremessage("Payment Failure");
        PaymentRequest.getInstance().setLogEnabled(!BuildConfig.RELEASE ? "1" : "0");
        PaymentRequest.getInstance().setFailureid("0");

        ArrayList<HashMap<String, String>> customPostParameters = new ArrayList<>();
        HashMap<String, String> hashPostValues = new HashMap<>();
        hashPostValues.put("account_details", "saving");
        hashPostValues.put("merchant_type", "gold");
        customPostParameters.add(hashPostValues);
        PaymentRequest.getInstance().setCustomPostValues(customPostParameters);

        EBSPayment.getInstance().init(context,
                Config.PaymentGateways.EBS_ACCOUNT_ID,
                Config.PaymentGateways.EBS_SECRET_KEY,
                !BuildConfig.RELEASE ? com.ebs.android.sdk.Config.Mode.ENV_TEST : com.ebs.android.sdk.Config.Mode.ENV_LIVE,
                com.ebs.android.sdk.Config.Encryption.ALGORITHM_MD5,
                context.getString(R.string.host_name));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (paymentCallback != null && data != null) {
            String paymentResponse = data.getStringExtra("payment_id");
            if (!TextUtils.isEmpty(paymentResponse)) {
                paymentCallback.onSuccess(paymentResponse);
            } else {
                paymentCallback.onSuccess(null);
            }
        }
    }
}
