package com.bueno.kitchen.managers.payment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.bueno.kitchen.managers.payment.core.PaymentManager;
import com.bueno.kitchen.models.core.PaymentDetailModel;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

/**
 * Created by samir on 26/09/16.
 */
public class RazorPayManager extends PaymentManager implements PaymentResultListener {

    Context context ;
    Activity activity ;



    public RazorPayManager(Context context, Activity activity){
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


//        /**
//         * Replace with your public key
//         */
//        final String public_key  = "rzp_live_ILgsfZCZoFIKMb";     //   "rzp_live_ILgsfZCZoFIKMb";  // buENO6%#
//
//        /**
//         * You need to pass current activity in order to let razorpay create CheckoutActivity
//         */
//
//      //  final Activity activity = this;
//
//        final Checkout co = new Checkout();
//        co.setPublicKey(public_key);


//        try{
//            Toast.makeText(context, ""+paymentDetailModel.amount, Toast.LENGTH_SHORT).show();
//            JSONObject options = new JSONObject("{" +
//                    "description: 'Order Charges'," +
//                    "image: 'https://media.licdn.com/mpr/mpr/shrink_200_200/AAEAAQAAAAAAAAZ1AAAAJDYxNmExZDRiLWNlOGMtNDg2OS04ZGMxLWUxOGFiNzFlMTRmNA.png'," +
//                    "currency: 'INR'}"
//            );
//
//            if(paymentDetailModel.amount.contains(".")){
//                options.put("amount", "" + paymentDetailModel.amount.replace(".", "")) ;
//            }else {
//                options.put("amount", "" + paymentDetailModel.amount + "00");
//            }
//            options.put("name", "Beuno");
//            options.put("prefill", new JSONObject("{email: 'keshav@apporio.com', contact: '9876543210'}"));
////            options.put("theme", new JSONObject("{color: '#000}"));
//            co.open((Activity) context, options);
//        } catch(Exception e){
//            Toast.makeText(context,"getinbkvkeviehrv  "+ e.getMessage(), Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }
//
//






        /**
         * You need to pass current activity in order to let Razorpay create CheckoutActivity
         */

        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Razorpay Corp");
            options.put("description", "Demoing Charges");
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://rzp-mobile.s3.amazonaws.com/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", "100");

            JSONObject preFill = new JSONObject();
            preFill.put("email", "sm@razorpay.com");
            preFill.put("contact", "9876543210");

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }



    @Override
    public void onPaymentSuccess(String s) {
        try {
            Toast.makeText(context, "Payment Successfuldededede: " + s, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("eeeeeeee", "Exception in onPaymentSuccess", e);
        }
    }

    @Override
    public void onPaymentError(int i, String s) {
        try {
            Toast.makeText(context, "Payment hccfhbyhbfailed: " + i + "ssssssss " + s, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("pppppppp", "Exception in onPaymentError", e);
        }
    }






//
//    /**
//     * The name of the function has to be
//     *   onPaymentSuccess
//     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
//     */
//    public void onPaymentSuccess(String razorpayPaymentID){
//        Log.d("razorpaymentid**",""+razorpayPaymentID);
//        try {
//            Log.d("razorpaymentid**",""+razorpayPaymentID);
//            Toast.makeText(context, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
//        }
//        catch (Exception e){
//            Log.d("razorpaymentid**",""+razorpayPaymentID);
//            Log.e("com.merchant", e.getMessage(), e);
//        }
//
//
//    }
//
//
//
//
//    /**
//     * The name of the function has to be
//     *   onPaymentError
//     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
//     */
//    public void onPaymentError(int code, String response){
//
//        Toast.makeText(context, "code= "+code   + "response = "+response, Toast.LENGTH_SHORT).show();
//
//        Log.d("response**",""+response);
//
//
//        try {
//            Log.d("response**", "" + response);
//        }
//        catch (Exception e){
//            Log.d("response**",""+response);
//            Log.e("com.merchant", e.getMessage(), e);
//        }
//    }
//
//



}
