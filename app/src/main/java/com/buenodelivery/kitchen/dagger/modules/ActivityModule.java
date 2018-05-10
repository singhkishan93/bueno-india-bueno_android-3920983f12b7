package com.buenodelivery.kitchen.dagger.modules;

import android.content.Context;

import com.buenodelivery.kitchen.dagger.components.PerActivity;
import com.buenodelivery.kitchen.utils.UtilitySingleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by navjot on 30/10/15.
 */
@Module
public class ActivityModule {

    private final Context context;

    public ActivityModule(Context context) {
        this.context = context;
    }

    @Provides
    @PerActivity
    Context provideContext() {
        return context;
    }

    @Provides
    @PerActivity
    public UtilitySingleton provideUtilitySingleton() {
        return new UtilitySingleton(context);
    }
}
