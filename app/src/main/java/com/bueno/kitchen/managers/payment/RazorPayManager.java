package com.bueno.kitchen.managers.payment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.bueno.kitchen.managers.payment.core.PaymentManager;
import com.bueno.kitchen.models.core.PaymentDetailModel;
import com.razorpay.Checkout;

import org.json.JSONObject;

/**
 * Created by samir on 26/09/16.
 */
public class RazorPayManager extends PaymentManager{

    Context context ;


    public RazorPayManager(Context context){
        this.context = context ;
    }

    @Override
    public void makePayment(PaymentDetailModel paymentDetailModel) {
        startPayment();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }





    public void startPayment(){
        /**
         * Replace with your public key
         */
        final String public_key = "rzp_live_ILgsfZCZoFIKMb";

        /**
         * You need to pass current activity in order to let razorpay create CheckoutActivity
         */
      //  final Activity activity = this;

        final Checkout co = new Checkout();
        co.setPublicKey(public_key);

        try{
            JSONObject options = new JSONObject("{" +
                    "description: 'Demoing Charges'," +
                    "image: 'https://rzp-mobile.s3.amazonaws.com/images/rzp.png'," +
                    "currency: 'INR'}"
            );

            options.put("amount", "100");
            options.put("name", "Razorpay Corp");
            options.put("prefill", new JSONObject("{email: 'sm@razorpay.com', contact: '9876543210'}"));

            co.open((Activity) context, options);

        } catch(Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * The name of the function has to be
     *   onPaymentSuccess
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    public void onPaymentSuccess(String razorpayPaymentID){
        try {
            Toast.makeText(context, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Log.e("com.merchant", e.getMessage(), e);
        }
    }

    /**
     * The name of the function has to be
     *   onPaymentError
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    public void onPaymentError(int code, String response){
        try {
            Toast.makeText(context, "Payment failed: " + Integer.toString(code) + " " + response, Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Log.e("com.merchant", e.getMessage(), e);
        }
    }





}
