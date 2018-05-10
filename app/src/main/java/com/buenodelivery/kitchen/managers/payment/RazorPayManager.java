package com.buenodelivery.kitchen.managers.payment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.buenodelivery.kitchen.managers.payment.core.PaymentManager;
import com.buenodelivery.kitchen.models.core.PaymentDetailModel;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

/**
 * Created by samir on 26/09/16.
 */
public class RazorPayManager extends PaymentManager implements PaymentResultListener {

    Context context ;
    Activity activity ;
    private static final int REQUEST_TRANSACTION = 1009;



    public RazorPayManager(Context context , Activity activity){
        this.context = context ;
        this.activity = activity ;
    }

    @Override
    public void makePayment(PaymentDetailModel paymentDetailModel) {
        startPayment(paymentDetailModel);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        Log.d("code**",""+requestCode);
        Log.d("resltcode**",""+resultCode);
        Log.d("data**",""+data);

        Toast.makeText(context, "in onactivityresult() of razorpaymanager" , Toast.LENGTH_SHORT).show();
    }





    public void startPayment(PaymentDetailModel paymentDetailModel){
        Log.d("addy_razor_pay","inside start payment");
        Toast.makeText(context, "start with razor pay meyhod ", Toast.LENGTH_SHORT).show();


        final Checkout co = new Checkout();
        co.setPublicKey("rzp_live_qwB115wRJTGvSb");

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Bueno");
            options.put("description", "Order Charges");
            options.put("image", "http://www.bueno.kitchen/assets/images/blogo.png");
            options.put("currency", "INR");
            options.put("amount", "100");

            if(String.valueOf(paymentDetailModel.amount).contains(".")){
                options.put("amount", ""+String.valueOf(paymentDetailModel.amount).replace(".",""));
            }else {
                options.put("amount", ""+String.valueOf(paymentDetailModel.amount) + "00");
            }

            JSONObject preFill = new JSONObject();
            preFill.put("email", "samir@apporio.com");
            preFill.put("contact", "9650923089");
            options.put("prefill", preFill);
            co.open(activity, options);

            paymentCallback.onSuccess("starting the crab ");
        } catch (Exception e) {
            Log.d("addy_razor_pay",e.getMessage());
            Toast.makeText(context, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        if (paymentCallback != null)
            paymentCallback.onSuccess(s);
    }

    @Override
    public void onPaymentError(int i, String s) {
        if (paymentCallback != null)
            paymentCallback.onFailure(s);
    }
}
