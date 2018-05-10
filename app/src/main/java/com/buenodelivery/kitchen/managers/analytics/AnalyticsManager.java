package com.buenodelivery.kitchen.managers.analytics;

import android.text.TextUtils;

import com.buenodelivery.kitchen.core.BuenoApplication;
import com.buenodelivery.kitchen.managers.PreferenceManager;
import com.buenodelivery.kitchen.models.core.ProductModel;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;

import javax.inject.Inject;

/**
 * Created by bedi on 20/03/16.
 */
public class AnalyticsManager {

    @Inject
    PreferenceManager preferenceManager;

    private String productActionString;
    private ProductModel productModel;
    private String screenName;
    private int quantity;
    private String coupon;
    private String orderId;

    private AnalyticsManager(ProductModel productModel) {
        BuenoApplication.getApp().getApplicationComponents().inject(this);
        this.productModel = productModel;
        quantity = 1;
    }

    public void send() {
        Product product = new Product()
                .setId(productModel.id)
                .setName(productModel.mealName)
                .setCategory(productModel.catName)
                .setQuantity(Integer.parseInt(productModel.quantity))
                .setPrice(Double.parseDouble(productModel.discountedPrice));

        String actionName = null;
        switch (productActionString) {
            case ProductAction.ACTION_ADD:
                actionName = "Add Product";
                break;
            case ProductAction.ACTION_DETAIL:
                actionName = "Product Detail";
                break;
            case ProductAction.ACTION_CHECKOUT:
                actionName = "Product Checkout";
                break;
            case ProductAction.ACTION_PURCHASE:
                actionName = "Product Purchase";
                break;
            case ProductAction.ACTION_REMOVE:
                actionName = "Delete Product";
                break;
        }

        if (!TextUtils.isEmpty(actionName)) {
            ProductAction productAction = new ProductAction(productActionString);
            if (!actionName.equalsIgnoreCase(ProductAction.ACTION_PURCHASE)) {
                productAction.setProductActionList(actionName);
            } else {
                productAction.setTransactionId(orderId)
                        .setTransactionAffiliation("VAT")
                        .setTransactionRevenue(productModel.getSubTotalPrice())
                        .setTransactionTax((preferenceManager.getLocality().getVat() * productModel.getSubTotalPrice()) / 100);

                if (!TextUtils.isEmpty(coupon))
                    productAction.setTransactionCouponCode(coupon);
            }
            HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
                    .addProduct(product)
                    .setProductAction(productAction);

            Tracker t = BuenoApplication.getApp().getDefaultTracker();
            t.setScreenName(screenName);
            t.send(builder.build());
        }
    }

    public static Builder with(ProductModel productModel) {
        return new Builder(productModel);
    }

    public static class Builder {

        private AnalyticsManager analyticsManager;

        public Builder(ProductModel productModel) {
            analyticsManager = new AnalyticsManager(productModel);
        }

        public Builder setAction(String productAction) {
            analyticsManager.productActionString = productAction;
            return this;
        }

        public Builder setScreen(String screenName) {
            analyticsManager.screenName = screenName;
            return this;
        }

        public Builder setQuantity(int quantity) {
            analyticsManager.quantity = quantity;
            return this;
        }

        public Builder setCoupon(String coupon) {
            analyticsManager.coupon = coupon;
            return this;
        }

        public Builder setOrderId(String orderId) {
            analyticsManager.orderId = orderId;
            return this;
        }

        public AnalyticsManager send() {
            analyticsManager.send();
            return analyticsManager;
        }
    }
}
