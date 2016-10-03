package com.bueno.kitchen.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bueno.kitchen.R;

import java.net.URLEncoder;

public class OLAWebViewActivity extends Activity {

    WebView webView ;
    public static String bill = "{\n" +
            "\"command\":\"debit\",\n" +
            "\"accessToken\":\"ola_access_token\",\n" +
            "\"uniqueId\":\"TXN1232434343555523\",\n" +
            "\"comments\":\"optional\",\n" +
            "\"udf\":\"optional\",\n" +
            "\"hash\":\"3ce32cf5dd8ad19909fa944dbb67e82260db059590546fad676ba859654477447080ef7a6262b91be36ce5e3260d525d977da632957b9822499134dd96a076c1\",\n" +
            "\"returnUrl\":\"http://myweb.com/myreturn_url.php\",\n" +
            "\"notificationUrl\":\"http://myweb.com/mynotify_url.php\",\n" +
            "\"amount\":\"0.00\",\n" +
            "\"currency\":\"INR\"\n" +
            "}";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olaweb_view);
        webView = (WebView) findViewById(R.id.olawebview);

        Log.d("Json i am sending to web view ", "" + bill);
        invokeOlaMoneyWebViewFlow(bill, "9650923089");

    }


    public void invokeOlaMoneyWebViewFlow(String bill, String phoneNumber) {
        StringBuffer buffer = new StringBuffer("https://om.olacabs.com/olamoney/webview/index.html");
        //Change http://sandbox.olamoney.in    to     https://om.olacabs.com before you go live
        String base64_bill = Base64.encodeToString(bill.getBytes(), Base64.DEFAULT);
        buffer.append("?bill="+base64_bill);
        buffer.append("&phone="+ URLEncoder.encode(phoneNumber == null ? "" : phoneNumber));
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(new OlaMoneyInteface() {
            @Override
            @android.webkit.JavascriptInterface
            public void onPaymentDone(String jsonResponse) {
/*Control is returned to the native code
  You can get the data in jsonResponse
*/
            }
        }, "OlaMoney");
        webView.loadUrl(buffer.toString());
    }





    interface OlaMoneyInteface {
        @android.webkit.JavascriptInterface
        void onPaymentDone(String jsonResponse);
    }





}
