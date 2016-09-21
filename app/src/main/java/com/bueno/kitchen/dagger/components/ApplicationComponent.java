package com.bueno.kitchen.dagger.components;

import com.bueno.kitchen.activities.CheckoutActivity;
import com.bueno.kitchen.activities.MainActivity;
import com.bueno.kitchen.activities.address.AddressActivity;
import com.bueno.kitchen.activities.address.AddressListActivity;
import com.bueno.kitchen.activities.orders.OrdersListActivity;
import com.bueno.kitchen.dagger.modules.ApiModule;
import com.bueno.kitchen.dagger.modules.ApplicationModule;
import com.bueno.kitchen.dagger.modules.DatabaseModule;
import com.bueno.kitchen.database.AddressOperations;
import com.bueno.kitchen.database.OrderOperations;
import com.bueno.kitchen.managers.analytics.AnalyticsManager;
import com.bueno.kitchen.managers.PreferenceManager;
import com.bueno.kitchen.managers.analytics.SegmentManager;
import com.bueno.kitchen.managers.payment.core.PaymentManager;
import com.bueno.kitchen.network.services.GoogleService;
import com.bueno.kitchen.network.services.JoolehService;
import com.bueno.kitchen.network.services.OTPService;
import com.bueno.kitchen.network.services.RestService;
import com.bueno.kitchen.receivers.IncomingSms;
import com.bueno.kitchen.views.ProductCellView;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by navjot on 30/10/15.
 */
@Singleton
@Component(modules = {ApplicationModule.class, ApiModule.class, DatabaseModule.class})
public interface ApplicationComponent {
    void inject(PreferenceManager preferenceManager);

    PreferenceManager getPreferenceManager();

    RestService getRestService();

    OTPService getOTPService();

    JoolehService getJoolehService();

    GoogleService getGoogleService();

    AddressOperations getAddressOperations();

    OrderOperations getOrderOperations();

    Bus provideEventBus();

    void inject(AddressActivity activity);

    void inject(AddressListActivity addressListActivity);

    void inject(MainActivity mainActivity);

    void inject(CheckoutActivity checkoutActivity);

    void inject(OrdersListActivity ordersListActivity);

    void inject(IncomingSms incomingSms);

    void inject(AddressOperations addressOperations);

    void inject(PaymentManager paymentManager);

    void inject(AnalyticsManager analyticsManager);

    void inject(SegmentManager segmentManager);

    void inject(ProductCellView productCellView);
}
