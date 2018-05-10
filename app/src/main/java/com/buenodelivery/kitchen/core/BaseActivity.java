package com.buenodelivery.kitchen.core;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.MenuItem;

import com.buenodelivery.kitchen.managers.PreferenceManager;
import com.buenodelivery.kitchen.network.services.RestService;
import com.buenodelivery.kitchen.utils.Config;
import com.buenodelivery.kitchen.utils.UtilitySingleton;
import com.buenodelivery.kitchen.views.font.TextHelpers;

import javax.inject.Inject;

/**
 * Created by navjot on 30/10/15.
 */
public class BaseActivity extends AppCompatActivity {

    @Inject
    public UtilitySingleton utilitySingleton;
    @Inject
    public Context appContext;
    @Inject
    public PreferenceManager preferenceManager;
    @Inject
    public RestService restService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BuenoApplication.getApp().getActivityComponents().inject(this);

        if (!TextUtils.isEmpty(getTitle())) {
            SpannableString title = TextHelpers.textAsCustomFont(Config.Fonts.FONT_ROBOTO_REGULAR,
                    getTitle().toString(), this);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(title);
            }
        }
    }

    public void setTitle(@NonNull String title) {
        SpannableString s = TextHelpers.textAsCustomFont(Config.Fonts.FONT_ROBOTO_REGULAR, title, this);
        setTitle(s);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void enableBackButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
    }

}
