package com.buenodelivery.kitchen.managers.payment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.buenodelivery.kitchen.activities.OLAWebViewActivity;
import com.buenodelivery.kitchen.managers.payment.core.PaymentManager;
import com.buenodelivery.kitchen.models.core.PaymentDetailModel;
import com.buenodelivery.kitchen.utils.Config;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

/**
 * Created by samir on 26/09/16.
 */
public class OlaMoneyPayManager extends PaymentManager {

    WebView webView;
    Context context ;
    Activity activity ;
    public OlaMoneyPayManager(Context context){
        this.context  = context ;
    }


    @Override
    public void makePayment(PaymentDetailModel paymentDetailModel) {

        invokeOlaMoney(paymentDetailModel);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(context, "sending to payment manager "+data  +" reqestcode = "+requestCode,  Toast.LENGTH_SHORT).show();
    }

    public void invokeOlaMoney(PaymentDetailModel paymentDetailModel) {
        try {
            Log.d("generating hashes ",""+get_SHA_512_hash("3u41p82uT1|BEN232|Tying_to_check_payment|BEN23|NA|NA|INR|1|NA|n7QRr98Ui4","n7QRr98Ui4"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            Intent intent = new Intent("com.olacabs.olamoney.pay");
            intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            intent.putExtra("bill", createMerchentBill(Config.PaymentGateways.OLA_Access_Token,paymentDetailModel.orderId , Config.PaymentGateways.OLA_Comment ,Config.PaymentGateways.OLA_UDF , Config.PaymentGateways.OLA_ReturnUrl , Config.PaymentGateways.OLA_Notify_URL , Config.PaymentGateways.OLA_Currency , ""+paymentDetailModel.amount , Config.PaymentGateways.OLA_CouponCode , Config.PaymentGateways.OLA_Access_Salt));
            intent.setPackage("com.olacabs.customer"); //Remember to change this to "com.olacabs.customer" before you go live
            activity.startActivityForResult(intent, Config.PaymentGateways.OLA_RESULT_CODE);
        } catch(ActivityNotFoundException e) {
            try {
//                context.startActivity(new Intent(context, OLAWebViewActivity.class).putExtra(""+Config.PaymentGateways.Key_Merchent_Bill , ""+createMerchentBill(Config.PaymentGateways.OLA_Access_Token,paymentDetailModel.orderId , Config.PaymentGateways.OLA_Comment ,Config.PaymentGateways.OLA_UDF , Config.PaymentGateways.OLA_ReturnUrl , Config.PaymentGateways.OLA_Notify_URL , Config.PaymentGateways.OLA_Currency , "1" , Config.PaymentGateways.OLA_CouponCode , Config.PaymentGateways.OLA_Access_Salt)));
                context.startActivity(new Intent(context, OLAWebViewActivity.class).putExtra(""+Config.PaymentGateways.Key_Merchent_Bill , ""+createMerchentBill("NA","NA" , Config.PaymentGateways.OLA_Comment+ Calendar.getInstance().getTime() ,Config.PaymentGateways.OLA_UDF+Calendar.getInstance().getTime() , "NA" , "NA" , Config.PaymentGateways.OLA_Currency , "0" , "NA" , Config.PaymentGateways.OLA_Access_Salt)));
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
                Log.d("OlaMoneyPayManager error occured while creating bill For Web View ", "" + e1);

            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.d("OlaMoneyPayManager error occured while creating bill ",""+e);
        }
    }



    private boolean check_olacabs() {

        PackageManager pm = context.getPackageManager();

        try {
            pm.getPackageInfo("com.olacabs.customer", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }


    }











    //  response after ola payment
//    {
//        "type":"debit",
//            "status":"success",
//            "transactionId":"bgi9-g8pf-v3jz",
//            "merchantBillId":"JLhq2ZxZX",
//            "amount":"50.00",
//            "hash":"5c8216201632af4811457f1bd6206da13ed128dedabab6f9b82b2992bc96b784d89463fd442825c79c8fc5d298118361bd85ec1213",
//            "timestamp":1439995769451,
//            "comments":"test",
//            "udf":"test"
//        "isCashbackAttempted":"true" (This will be returned only couponCode is passed)
//        "isCashbackSuccessful":"true" (This will be returned only couponCode is passed)
//    }



    public String  createMerchentBill(String access_token ,  String unique_id , String comment  ,String udf  , String returnurl  , String notifyurl   , String currency, String amount   , String OLacouponCode, String salt ) throws UnsupportedEncodingException {

        Log.d("Unique ID for script",""+unique_id);
         return  "{\n" +
                "\"command\":\"debit\",\n" +
                "\"accessToken\":\""+access_token+"\",\n" +
                "\"uniqueId\":\""+unique_id+"\",\n" +
                "\"comments\":\""+comment+"\",\n" +
                "\"udf\":\""+udf+"\",\n" +
//                "\"hash\":\" " + ""+get_SHA_512_hash(  ""+access_token+"|"+ unique_id +"|"+comment+ "|"+udf+ "|"+returnurl+ "|"+notifyurl+ "|"+currency+ "|"+amount+ "|"+OLacouponCode+ "|"+salt,salt) + "\",\n" +
                 "\"hash\":\" " + "NA" +"\",\n" +
                 "\"returnUrl\":\""+returnurl+"\",\n" +
                "\"notificationUrl\":\""+notifyurl+"\",\n" +
                "\"amount\":\""+amount+"\",\n" +
                "\"currency\":\""+currency+"\"\n" +
                "}";


    }


    public String get_SHA_512_hash(String passwordToHash , String   salt) throws UnsupportedEncodingException {
        String generatedhash = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes("UTF-8"));
            byte[] bytes = md.digest(passwordToHash.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedhash = sb.toString();
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return generatedhash;
    }







}
