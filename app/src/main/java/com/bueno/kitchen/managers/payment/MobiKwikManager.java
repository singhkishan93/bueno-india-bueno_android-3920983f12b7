package com.bueno.kitchen.managers.payment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.bueno.kitchen.BuildConfig;
import com.bueno.kitchen.managers.payment.core.PaymentManager;
import com.bueno.kitchen.models.core.PaymentDetailModel;
import com.bueno.kitchen.utils.Config;
import com.mobikwik.sdk.MobikwikSDK;
import com.mobikwik.sdk.lib.MKTransactionResponse;
import com.mobikwik.sdk.lib.Transaction;
import com.mobikwik.sdk.lib.TransactionConfiguration;
import com.mobikwik.sdk.lib.User;

/**
 * Created by bedi on 20/03/16.
 */
public class MobiKwikManager extends PaymentManager {

    private static final int REQUEST_TRANSACTION = 1001;

    private Context context;

    public MobiKwikManager(Context context) {
        this.context = context;
    }

    @Override
    public void makePayment(PaymentDetailModel paymentDetailModel) {
        TransactionConfiguration config = new TransactionConfiguration();
        config.setDebitWallet(true);
        config.setAllowMixedContent(true);
        config.setPgResponseUrl(Config.PaymentGateways.MOBIKWIK_URL_CHECKSUM_VERIFY);
        config.setChecksumUrl(Config.PaymentGateways.MOBIKWIK_URL_CHECKSUM_GENERATOR);
        config.setMerchantName(Config.PaymentGateways.MOBIKWIK_MERCHANT_NAME);
        config.setMbkId(Config.PaymentGateways.MOBIKWIK_MERCHANT_ID);
        config.setMode(BuildConfig.RELEASE ? "1" : "0");

        User user = new User(preferenceManager.getEmail(), preferenceManager.getMobileNumber());
        Transaction newTransaction = Transaction.Factory.newTransaction(user,
                paymentDetailModel.orderId,
                paymentDetailModel.amount);

        Intent mobiKwikIntent = new Intent(context, MobikwikSDK.class);
        mobiKwikIntent.putExtra(MobikwikSDK.EXTRA_TRANSACTION_CONFIG, config);
        mobiKwikIntent.putExtra(MobikwikSDK.EXTRA_TRANSACTION, newTransaction);
        ((Activity) context).startActivityForResult(mobiKwikIntent, REQUEST_TRANSACTION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TRANSACTION) {
            if (data != null) {
                MKTransactionResponse response = (MKTransactionResponse)
                        data.getSerializableExtra(MobikwikSDK.EXTRA_TRANSACTION_RESPONSE);
                if (response.statusCode.equalsIgnoreCase("0")) {
                    paymentCallback.onSuccess(response.statusMessage);
                } else {
                    paymentCallback.onFailure(response.statusMessage);
                }
            }
        }
    }

}
