package com.bueno.kitchen.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import com.bueno.kitchen.core.BuenoApplication;
import com.squareup.otto.Bus;

import javax.inject.Inject;

/**
 * Created by navjot on 13/1/16.
 */
public class IncomingSms extends BroadcastReceiver {

    @Inject
    Bus eventBus;

    @Override
    public void onReceive(Context context, Intent intent) {
        BuenoApplication.getApp().getApplicationComponents().inject(this);
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (Object aPdusObj : pdusObj) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();
                    try {
                        if (senderNum.equals("VM-TFCTOR") && !TextUtils.isEmpty(message) && message.length() > 4) {
                            eventBus.post(message.substring(message.length() - 4));
                        }
                    } catch (Exception e) {
                    }

                }
            }

        } catch (Exception e) {

        }
    }
}
