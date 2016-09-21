package com.bueno.kitchen.activities.utils;

import android.os.Bundle;
import android.webkit.WebView;

import com.bueno.kitchen.R;
import com.bueno.kitchen.core.BaseActivity;
import com.bueno.kitchen.utils.Config;

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
        webView.loadUrl(getIntent().getStringExtra(Config.Intents.INTENT_URL));
    }
}
