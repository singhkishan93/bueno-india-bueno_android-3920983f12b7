package com.buenodelivery.kitchen.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buenodelivery.kitchen.R;
import com.buenodelivery.kitchen.core.BuenoApplication;
import com.buenodelivery.kitchen.managers.PreferenceManager;
import com.buenodelivery.kitchen.models.core.ProductModel;
import com.buenodelivery.kitchen.utils.UtilitySingleton;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by navjot on 7/1/16.
 */
public class CheckoutShelfView extends RelativeLayout {

    @Bind(R.id.amount_text)
    public TextView amountTextView;
    @Bind(R.id.quantity_text)
    public TextView quantityTextView;
    @Bind(R.id.checkout_button)
    public TextView checkoutButton;

    private ArrayList<ProductModel> ordersList;
    private CheckoutShelfViewInteractionListener listener;

    @Inject
    public UtilitySingleton utilitySingleton;
    @Inject
    public PreferenceManager preferenceManager;
    @Inject
    public Bus eventBus;

    public interface CheckoutShelfViewInteractionListener {
        void performCheckout();
    }

    public CheckoutShelfView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_checkout_bottom_shelf, this);
        BuenoApplication.getApp().getActivityComponents().inject(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        eventBus.register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        eventBus.unregister(this);
    }

    @Subscribe
    public void updateCart(ArrayList<ProductModel> ordersList) {
        this.ordersList = ordersList;
        if (ordersList != null && !ordersList.isEmpty()) {
            float amount = 0.0f;
            int quantity = 0;
            for (ProductModel productModel : ordersList) {
                try {
                    quantity += productModel.selectedQuantity;
                    amount += (Float.valueOf(productModel.discountedPrice) * productModel.selectedQuantity);
                } catch (Exception e) {
                }
            }
            amountTextView.setText(String.format("Rs. %s", amount));
            quantityTextView.setText(String.format("Qty: %d", quantity));

            if (isMinOrderAmountReached()) {
                checkoutButton.setText("Checkout ->");
                checkoutButton.setAlpha(1.0f);
            } else {
                checkoutButton.setText(String.format("Min. Rs.%s", preferenceManager.getLocality().minOrderAmount));
                checkoutButton.setAlpha(0.5f);
            }

            setVisibility(View.VISIBLE);
        } else {
            setVisibility(View.GONE);
        }
    }

    public void attachListener(CheckoutShelfViewInteractionListener checkoutShelfViewInteractionListener) {
        this.listener = checkoutShelfViewInteractionListener;
        checkoutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && v.getAlpha() == 1.0f)
                    listener.performCheckout();
            }
        });
    }

    private boolean isMinOrderAmountReached() {
        float amount = 0.0f;
        for (ProductModel productModel : ordersList) {
            try {
                amount += (Float.valueOf(productModel.discountedPrice) * productModel.selectedQuantity);
            } catch (Exception e) {
                continue;
            }
        }
        return Integer.valueOf(preferenceManager.getLocality().minOrderAmount) <= amount;
    }
}
