package com.bueno.kitchen.dagger.modules;

import android.content.Context;

import com.bueno.kitchen.core.BuenoApplication;
import com.bueno.kitchen.managers.DbManager;
import com.bueno.kitchen.managers.PreferenceManager;
import com.bueno.kitchen.utils.Config;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by navjot on 30/10/15.
 */
@Module
public class ApplicationModule {

    private final BuenoApplication app;

    public ApplicationModule(BuenoApplication app) {
        this.app = app;
    }

    @Singleton
    @Provides
    public PreferenceManager providePreferenceManager() {
        return new PreferenceManager(app.getSharedPreferences(Config.Properties.PROPERTY_PREF, Context.MODE_PRIVATE));
    }

    @Singleton
    @Provides
    public Context provideContext() {
        return app;
    }

    @Provides
    @Singleton
    public DbManager provideDBManager() {
        return new DbManager(app);
    }

    @Provides
    @Singleton
    public Bus provideEventBus() {
        return new Bus();
    }

}
