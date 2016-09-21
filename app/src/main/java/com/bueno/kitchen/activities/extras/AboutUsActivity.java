package com.bueno.kitchen.activities.extras;

import android.os.Bundle;

import com.bueno.kitchen.R;
import com.bueno.kitchen.core.BaseActivity;

public class AboutUsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        enableBackButton();
    }
}
