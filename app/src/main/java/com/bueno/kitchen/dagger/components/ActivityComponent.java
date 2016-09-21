package com.bueno.kitchen.dagger.components;

import com.bueno.kitchen.activities.MainActivity;
import com.bueno.kitchen.activities.prelogin.OTPActivity;
import com.bueno.kitchen.activities.prelogin.SplashActivity;
import com.bueno.kitchen.activities.utils.MapActivity;
import com.bueno.kitchen.views.adapter.CategoryGridAdapter;
import com.bueno.kitchen.core.BaseActivity;
import com.bueno.kitchen.dagger.modules.ActivityModule;
import com.bueno.kitchen.views.CheckoutShelfView;

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
