package com.buenodelivery.kitchen.activities.prelogin;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.widget.ImageView;

import com.buenodelivery.kitchen.R;
import com.buenodelivery.kitchen.activities.MainActivity;
import com.buenodelivery.kitchen.core.BuenoApplication;
import com.buenodelivery.kitchen.managers.PreferenceManager;
import com.segment.analytics.Analytics;
import com.segment.analytics.Traits;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.intercom.android.sdk.Intercom;
import io.intercom.android.sdk.identity.Registration;

public class SplashActivity extends Activity {

    @Bind(R.id.splash_image)
    ImageView splashImageView;

    @Inject
    PreferenceManager preferenceManager;
    private Runnable splashRunnable;
    private Handler splashHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        BuenoApplication.getApp().getActivityComponents().inject(this);
        ButterKnife.bind(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 101);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 101);

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.GET_ACCOUNTS}, 101);


                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 101);


                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);

                            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                            }
                        }
                    }
                }
            }
        }

        if (preferenceManager.isLoggedIn()) {
            Intercom.client().registerIdentifiedUser(new Registration().withUserId(preferenceManager.getMobileNumber()));
            try {
                Analytics.with(this).identify(preferenceManager.getMobileNumber(),
                        new Traits().putEmail(preferenceManager.getEmail()).putPhone(preferenceManager.getMobileNumber())
                                .putValue("appVersion", getPackageManager().getPackageInfo(getPackageName(), 0).versionName), null);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        } else {
            Intercom.client().registerUnidentifiedUser();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Picasso.with(this)
                .load(R.drawable.splash_screen_android_2)
                .fit()
                .centerCrop()
                .into(splashImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        startTimer();
                    }

                    @Override
                    public void onError() {
                        startTimer();
                    }
                });
    }

    private void startTimer() {
        if (splashHandler == null)
            splashHandler = new Handler();
        if (splashRunnable == null)
            splashRunnable = new Runnable() {
                @Override
                public void run() {
                    loadLaunchActivity();
                }
            };
        splashHandler.postDelayed(splashRunnable, 3000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Picasso.with(this).cancelRequest(splashImageView);
        if (splashHandler != null && splashRunnable != null)
            splashHandler.removeCallbacks(splashRunnable);
    }

    private void loadLaunchActivity() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }
}