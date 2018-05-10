package com.buenodelivery.kitchen.managers.payment;

import android.content.Context;
import android.content.Intent;

import com.buenodelivery.kitchen.managers.payment.core.PaymentManager;
import com.buenodelivery.kitchen.models.core.PaymentDetailModel;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Payu.PayuConstants;

/**
 * Created by bedi on 20/03/16.
 * <p/>
 * TODO OnHold
 */
public class PayUManager extends PaymentManager {

    private Context context;

    public PayUManager(Context context) {
        this.context = context;
    }

    @Override
    public void makePayment(PaymentDetailModel paymentDetailModel) {
        PaymentParams mPaymentParams = new PaymentParams();
        mPaymentParams.setKey("gtKFFx");
        mPaymentParams.setAmount(paymentDetailModel.amount);
        mPaymentParams.setProductInfo(null);
        mPaymentParams.setFirstName("");
        mPaymentParams.setEmail(preferenceManager.getEmail());
        mPaymentParams.setTxnId("");
        mPaymentParams.setSurl("https://payu.herokuapp.com/success");
        mPaymentParams.setFurl("https://payu.herokuapp.com/failure");

        mPaymentParams.setHash("your payment hash");

        PayuHashes payuHashes = new PayuHashes();
        Intent intent = new Intent();
        intent.putExtra(PayuConstants.ENV, PayuConstants.PRODUCTION_ENV);
        intent.putExtra(PayuConstants.PAYMENT_DEFAULT_PARAMS, mPaymentParams);
        intent.putExtra(PayuConstants.PAYU_HASHES, payuHashes);
        context.startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
