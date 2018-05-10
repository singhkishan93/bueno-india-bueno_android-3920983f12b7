package com.buenodelivery.kitchen.activities.extras;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.buenodelivery.kitchen.R;
import com.buenodelivery.kitchen.activities.utils.WebActivity;
import com.buenodelivery.kitchen.core.BaseActivity;
import com.buenodelivery.kitchen.utils.Config;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class FaqActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        ButterKnife.bind(this);
        enableBackButton();
    }

    @OnClick({R.id.faqs_button,
            R.id.privacy_policy_button,
            R.id.terms_button,
            R.id.refund_button})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.faqs_button:
                loadWebView(((TextView) v).getText().toString(), Config.URLs.URL_FAQ);
                break;
            case R.id.privacy_policy_button:
                loadWebView(((TextView) v).getText().toString(), Config.URLs.URL_PRIVACY);
                break;
            case R.id.terms_button:
                loadWebView(((TextView) v).getText().toString(), Config.URLs.URL_TERMS);
                break;
            case R.id.refund_button:
                loadWebView(((TextView) v).getText().toString(), Config.URLs.URL_REFUND);
                break;
        }
    }

    private void loadWebView(String title, String url) {
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra(Config.Intents.INTENT_URL, url);
        intent.putExtra(Config.Intents.INTENT_TITLE, title);
        startActivity(intent);
    }
}
