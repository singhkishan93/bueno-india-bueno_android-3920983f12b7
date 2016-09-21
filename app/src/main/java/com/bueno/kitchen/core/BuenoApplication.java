package com.bueno.kitchen.core;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.bueno.kitchen.BuildConfig;
import com.bueno.kitchen.R;
import com.bueno.kitchen.dagger.components.ActivityComponent;
import com.bueno.kitchen.dagger.components.ApplicationComponent;
import com.bueno.kitchen.dagger.components.DaggerActivityComponent;
import com.bueno.kitchen.dagger.components.DaggerApplicationComponent;
import com.bueno.kitchen.dagger.modules.ActivityModule;
import com.bueno.kitchen.dagger.modules.ApplicationModule;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.segment.analytics.Analytics;
import com.segment.analytics.android.integrations.branch.BranchIntegration;
import com.segment.analytics.android.integrations.google.analytics.GoogleAnalyticsIntegration;

import io.fabric.sdk.android.Fabric;
import io.intercom.android.sdk.Intercom;

/**
 * Created by navjot on 30/10/15.
 */
public class BuenoApplication extends Application {

    private static BuenoApplication app;
    private ApplicationComponent applicationComponent;
    private ActivityComponent activityComponent;

    public static BuenoApplication getApp() {
        return app;
    }

    private Tracker mTracker;

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.app_tracker);
        }
        return mTracker;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Analytics analytics = new Analytics.Builder(this, getString(R.string.segment_write_key))
                .use(GoogleAnalyticsIntegration.FACTORY)
                .use(BranchIntegration.FACTORY)
                .build();
        Analytics.setSingletonInstance(analytics);

        if (BuildConfig.RELEASE)
            Fabric.with(this, new Crashlytics());

        Intercom.initialize(this, getString(R.string.intercom_key), getString(R.string.intercom_appid));

        BuenoApplication.app = this;
        applicationComponent = DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(this)).build();
        activityComponent = DaggerActivityComponent.builder().applicationComponent(applicationComponent).activityModule(new ActivityModule(this)).build();
    }

    public ApplicationComponent getApplicationComponents() {
        return applicationComponent;
    }

    public ActivityComponent getActivityComponents() {
        return activityComponent;
    }

}
