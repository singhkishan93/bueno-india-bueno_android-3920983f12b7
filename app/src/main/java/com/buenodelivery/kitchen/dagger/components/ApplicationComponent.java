package com.buenodelivery.kitchen.dagger.components;

import com.buenodelivery.kitchen.activities.CheckoutActivity;
import com.buenodelivery.kitchen.activities.MainActivity;
import com.buenodelivery.kitchen.activities.address.AddressActivity;
import com.buenodelivery.kitchen.activities.address.AddressListActivity;
import com.buenodelivery.kitchen.activities.orders.OrdersListActivity;
import com.buenodelivery.kitchen.dagger.modules.ApiModule;
import com.buenodelivery.kitchen.dagger.modules.ApplicationModule;
import com.buenodelivery.kitchen.dagger.modules.DatabaseModule;
import com.buenodelivery.kitchen.database.AddressOperations;
import com.buenodelivery.kitchen.database.OrderOperations;
import com.buenodelivery.kitchen.managers.analytics.AnalyticsManager;
import com.buenodelivery.kitchen.managers.PreferenceManager;
import com.buenodelivery.kitchen.managers.analytics.SegmentManager;
import com.buenodelivery.kitchen.managers.payment.core.PaymentManager;
import com.buenodelivery.kitchen.network.services.GoogleService;
import com.buenodelivery.kitchen.network.services.JoolehService;
import com.buenodelivery.kitchen.network.services.OTPService;
import com.buenodelivery.kitchen.network.services.RestService;
import com.buenodelivery.kitchen.receivers.IncomingSms;
import com.buenodelivery.kitchen.views.ProductCellView;
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
