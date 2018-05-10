package com.buenodelivery.kitchen.activities.extras;

import android.os.Bundle;

import com.buenodelivery.kitchen.R;
import com.buenodelivery.kitchen.core.BaseActivity;

public class AboutUsActivity extends BaseActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        enableBackButton();

    }
}
