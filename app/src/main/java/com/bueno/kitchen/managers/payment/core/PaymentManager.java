package com.bueno.kitchen.managers.payment.core;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.bueno.kitchen.core.BuenoApplication;
import com.bueno.kitchen.managers.PreferenceManager;
import com.bueno.kitchen.managers.payment.EBSManager;
import com.bueno.kitchen.managers.payment.MobiKwikManager;
import com.bueno.kitchen.managers.payment.PaytmManager;
import com.bueno.kitchen.models.core.PaymentDetailModel;

import javax.inject.Inject;

/**
 * Created by bedi on 20/03/16.
 */
public abstract class PaymentManager {
    public enum PaymentModes {
        COD("COD", 2),
        EBS("EBS", 7),
        MOBIWIK("Mobikwik", 3),
        PAYTM("PayTm", 5),
        PAYU("PayU", 1);

        public final String keyString;
        public final int key;

        PaymentModes(String keyString,
                     int key) {
            this.keyString = keyString;
            this.key = key;
        }

        public static PaymentModes getMode(String keyString) {
            if (!TextUtils.isEmpty(keyString)) {
                if (keyString.equalsIgnoreCase(COD.keyString)) return COD;
                else if (keyString.equalsIgnoreCase(EBS.keyString)) return EBS;
                else if (keyString.equalsIgnoreCase(MOBIWIK.keyString)) return MOBIWIK;
                else if (keyString.equalsIgnoreCase(PAYTM.keyString)) return PAYTM;
                else if (keyString.equalsIgnoreCase(PAYU.keyString)) return PAYU;
                else return null;
            }
            return null;
        }
    }

    @Inject
    public PreferenceManager preferenceManager;
    public PaymentCallback paymentCallback;

    protected PaymentManager() {
        BuenoApplication.getApp().getApplicationComponents().inject(this);
    }

    public interface PaymentCallback {
        void onSuccess(String params);

        void onFailure(String message);
    }

    public void attachCallback(PaymentCallback paymentCallback) {
        this.paymentCallback = paymentCallback;
    }

    public abstract void makePayment(PaymentDetailModel paymentDetailModel);

    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);

    public static Builder with(Context context) {
        return new Builder(context);
    }

    public static class Builder {

        private PaymentManager paymentManager;
        private Context context;
        private PaymentDetailModel paymentDetailModel;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMode(PaymentModes paymentModes) {
            switch (paymentModes) {
                case EBS:
                    paymentManager = new EBSManager(context);
                    break;
                case MOBIWIK:
                    paymentManager = new MobiKwikManager(context);
                    break;
                case PAYTM:
                    paymentManager = new PaytmManager(context);
                    break;
            }
            return this;
        }

        public Builder attachCallback(PaymentCallback paymentCallback) {
            if (paymentManager != null) {
                paymentManager.attachCallback(paymentCallback);
            }
            return this;
        }

        public Builder setConfig(PaymentDetailModel paymentDetailModel) {
            this.paymentDetailModel = paymentDetailModel;
            return this;
        }

        public PaymentManager initiatePayment() {
            paymentManager.makePayment(paymentDetailModel);
            return paymentManager;
        }
    }

}
