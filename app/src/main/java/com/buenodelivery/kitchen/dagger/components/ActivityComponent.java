package com.buenodelivery.kitchen.dagger.components;

import com.buenodelivery.kitchen.activities.MainActivity;
import com.buenodelivery.kitchen.activities.prelogin.OTPActivity;
import com.buenodelivery.kitchen.activities.prelogin.SplashActivity;
import com.buenodelivery.kitchen.activities.utils.MapActivity;
import com.buenodelivery.kitchen.views.adapter.CategoryGridAdapter;
import com.buenodelivery.kitchen.core.BaseActivity;
import com.buenodelivery.kitchen.dagger.modules.ActivityModule;
import com.buenodelivery.kitchen.views.CheckoutShelfView;

import dagger.Component;

/**
 * Created by navjot on 30/10/15.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(BaseActivity baseActivity);

    void inject(MainActivity mainActivity);

    void inject(OTPActivity activity);

    void inject(SplashActivity activity);

    void inject(CheckoutShelfView checkoutShelfView);

    void inject(CategoryGridAdapter categoryGridAdapter);

    void inject(MapActivity mapActivity);
}
