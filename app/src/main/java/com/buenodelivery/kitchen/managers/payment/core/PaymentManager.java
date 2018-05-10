package com.buenodelivery.kitchen.managers.payment.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.buenodelivery.kitchen.core.BuenoApplication;
import com.buenodelivery.kitchen.managers.PreferenceManager;
import com.buenodelivery.kitchen.managers.payment.EBSManager;
import com.buenodelivery.kitchen.managers.payment.MobiKwikManager;
import com.buenodelivery.kitchen.managers.payment.OlaMoneyPayManager;
import com.buenodelivery.kitchen.managers.payment.PayUManager;
import com.buenodelivery.kitchen.managers.payment.PaytmManager;
import com.buenodelivery.kitchen.managers.payment.RazorPayManager;
import com.buenodelivery.kitchen.models.core.PaymentDetailModel;

import javax.inject.Inject;

/**
 * Created by bedi on 20/03/16.
 */
public abstract class PaymentManager {
    public enum PaymentModes {

        PAYU("PayUMoney", 1),
        COD("COD", 2),
        MOBIWIK("Mobikwik", 3),
        PAYTM("PayTm", 5),
        OTHERS("Others",6),
        EBS("EBS", 7),
        RAZORPAY("Razorpay",8),
        OLAMONEY("OlaMoney",9);


        public final String keyString;
        public final int key;

        PaymentModes(String keyString, int key) {
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
                else if (keyString.equalsIgnoreCase(RAZORPAY.keyString)) return RAZORPAY;
                else if (keyString.equalsIgnoreCase(OLAMONEY.keyString)) return OLAMONEY;
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

    public static Builder with(Context context , Activity activity) {
        return new Builder(context , activity);
    }

    public static class Builder {

        private PaymentManager paymentManager;
        private Context context;
        private PaymentDetailModel paymentDetailModel;
        private Activity activity ;

        public Builder(Context context , Activity activity ) {
            this.context = context;
            this.activity = activity ;
        }

        public Builder setMode(PaymentModes paymentModes) {
            switch (paymentModes) {
                case EBS:
                    paymentManager = new EBSManager(context);
                    break;
                case PAYU:
                    paymentManager = new PayUManager(context);
                    break;
                case MOBIWIK:
                    paymentManager = new MobiKwikManager(context);
                    break;
                case PAYTM:
                    paymentManager = new PaytmManager(context);
                    break;
                case RAZORPAY:
                    paymentManager = new RazorPayManager(context , activity);
                    break;
                case OLAMONEY:
                    paymentManager = new OlaMoneyPayManager(context );
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
