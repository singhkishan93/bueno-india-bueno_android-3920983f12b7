package com.buenodelivery.kitchen.activities.utils;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.buenodelivery.kitchen.R;
import com.buenodelivery.kitchen.core.BaseActivity;
import com.buenodelivery.kitchen.utils.Config;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WebActivity extends BaseActivity {

    @Bind(R.id.web_view)
    public WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);
        enableBackButton();

        setTitle(getIntent().getStringExtra(Config.Intents.INTENT_TITLE));
        WebSettings websettings  = webView.getSettings();
        websettings.setJavaScriptEnabled(true);
        websettings.setDomStorageEnabled(true);
        websettings.setLoadsImagesAutomatically(true);
        webView.loadUrl(getIntent().getStringExtra(Config.Intents.INTENT_URL));
    }
}
