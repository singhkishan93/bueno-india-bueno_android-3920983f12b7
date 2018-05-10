package com.buenodelivery.kitchen.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.buenodelivery.kitchen.R;
import com.buenodelivery.kitchen.managers.PreferenceManager;
import com.buenodelivery.kitchen.utils.Config;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;

public class OLAWebViewActivity extends Activity {






    WebView webView ;
//    public static String bill = "{\n" +
//            "\"command\":\"debit\",\n" +
//            "\"accessToken\":\"3u41p82uT1\",\n" +
//            "\"uniqueId\":\"BEN232\",\n" +
//            "\"comments\":\"Tying_to_check_payment\",\n" +
//            "\"udf\":\"BEN232\",\n" +
//            "\"hash\":\"4dcdb322f41601d323e903c7d95fb3815662a1f656fc1727b100755664ce3d2fb641a4d4262842f66eaa6242ab5d3843a49cd39c7e13d9460c9e420f1597d792\",\n" +
//            "\"returnUrl\":\"NA\",\n" +
//            "\"notificationUrl\":\"NA\",\n" +
//            "\"amount\":\"1.00\",\n" +
//            "\"currency\":\"INR\"\n" +
//            "}";



    @Inject
    public PreferenceManager prefrences_manager ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olaweb_view);
        webView = (WebView) findViewById(R.id.olawebview);

        Log.d("Json i am sending to web view ", "" + getIntent().getStringExtra(Config.PaymentGateways.Key_Merchent_Bill));
        invokeOlaMoneyWebViewFlow(getIntent().getStringExtra(Config.PaymentGateways.Key_Merchent_Bill), "");


    }


    public void invokeOlaMoneyWebViewFlow(String bill, String phoneNumber) {
        StringBuffer buffer = new StringBuffer("https://om.olacabs.com/olamoney/webview/index.html");
        //Change http://sandbox.olamoney.in    to     https://om.olacabs.com before you go live
        String base64_bill = Base64.encodeToString(bill.getBytes(), Base64.DEFAULT);
        buffer.append("?bill=" + base64_bill);
        buffer.append("&phone=" + URLEncoder.encode(phoneNumber == null ? "" : phoneNumber));
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(buffer.toString());
        webView.addJavascriptInterface(new OlaMoneyInteface() {
            @Override
            @android.webkit.JavascriptInterface
            public void onPaymentDone(String jsonResponse) {
                /*Control is returned to the native code
                  You can get the data in jsonResponse
                  */
                Log.d("response i am geting  after OLA ", "" + jsonResponse);
                Toast.makeText(OLAWebViewActivity.this, "response OLA " + jsonResponse, Toast.LENGTH_SHORT).show();
            }
        }, "OlaMoney");

    }





    interface OlaMoneyInteface {
        @android.webkit.JavascriptInterface
        void onPaymentDone(String jsonResponse);
    }



    public String get_SHA_512_SecurePassword(String passwordToHash, String   salt) throws UnsupportedEncodingException {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes("UTF-8"));
            byte[] bytes = md.digest(passwordToHash.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return generatedPassword;
    }






    class MyJavaScriptInterface {

        private Context ctx;
        MyJavaScriptInterface(Context ctx) {
            this.ctx = ctx;
        }

        @JavascriptInterface
        public void showHTML(String html) {
            System.out.println(html);
        }

    }




}
