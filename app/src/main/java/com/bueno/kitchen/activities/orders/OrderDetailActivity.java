package com.bueno.kitchen.activities.orders;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bueno.kitchen.R;
import com.bueno.kitchen.activities.utils.MapActivity;
import com.bueno.kitchen.core.BaseActivity;
import com.bueno.kitchen.models.core.AddressModel;
import com.bueno.kitchen.models.core.OrderModel;
import com.bueno.kitchen.models.core.ProductModel;
import com.bueno.kitchen.models.response.orders.OrderDetailResponseModel;
import com.bueno.kitchen.network.RestProcess;
import com.bueno.kitchen.utils.Config;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Response;

public class OrderDetailActivity extends BaseActivity {

    private OrderModel orderModel;
    @Bind(R.id.subtotal_text)
    public TextView subTotalTextView;
    @Bind(R.id.discount_text)
    public TextView discountTextView;
    @Bind(R.id.vat_text)
    public TextView vatTextView;
    @Bind(R.id.total_amount_text)
    public TextView totalAmountTextView;
    @Bind(R.id.instruction_edit_text)
    public TextView instructionTextView;
    @Bind(R.id.order_list)
    public LinearLayout ordersListView;
    @Bind(R.id.name_text)
    public TextView addressNameTextView;
    @Bind(R.id.address_text)
    public TextView addressTextView;
    @Bind(R.id.location_text)
    public TextView locationTextView;
    @Bind(R.id.location_view)
    public View locationView;
    @Bind(R.id.instruction_container)
    public View instructionContainer;
    @Bind(R.id.order_status_text)
    public TextView orderStatusTextView;
    @Bind(R.id.track_order_button)
    public View trackOrderView;
    @Bind(R.id.order_status_container)
    public View orderStatusContainer;
    @Bind(R.id.edit_address_button)
    public View editAddressView;
    @Bind(R.id.payment_mode_text)
    public TextView paymentModeTextView;
    private OrderDetailResponseModel orderDetailResponseModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        orderModel = getIntent().getParcelableExtra(Config.Properties.PROPERTY_ORDER);
        if (orderModel == null) {
            finish();
            return;
        }
        enableBackButton();
        ButterKnife.bind(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        bindUI();
        fetchOrderStatus();
    }

    private void fetchOrderStatus() {
        restService.getOrderDetail(orderModel.orderId)
                .enqueue(new RestProcess<>(new RestProcess.RestCallback<OrderDetailResponseModel>() {
                    @Override
                    public void success(OrderDetailResponseModel orderDetailResponseModel, Response<OrderDetailResponseModel> response) {
                        if (orderDetailResponseModel != null && !TextUtils.isEmpty(orderDetailResponseModel.orderStatus)) {
                            OrderDetailActivity.this.orderDetailResponseModel = orderDetailResponseModel;
                            orderStatusContainer.setVisibility(View.VISIBLE);
                            orderStatusTextView.setText(orderDetailResponseModel.orderStatus);
                            trackOrderView.setVisibility(orderDetailResponseModel.orderStatus.equalsIgnoreCase("dispatched") ? View.VISIBLE : View.INVISIBLE);
                        }
                    }

                    @Override
                    public void failure(Throwable t) {

                    }
                }, this, false));
    }

    @OnClick(R.id.track_order_button)
    public void onClick(View v) {
        if (orderDetailResponseModel != null) {
            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra(Config.Intents.INTENT_ORDER_ID, orderDetailResponseModel.joolehOrderNumber);
            startActivity(intent);
        }
    }

    private void bindUI() {
        editAddressView.setVisibility(View.INVISIBLE);
        setTitle("Order#" + orderModel.orderId);
        ordersListView.removeAllViews();
        for (ProductModel productModel : orderModel.productModels) {
            View convertView = getLayoutInflater().inflate(R.layout.cell_order_history, null);
            ((TextView) convertView.findViewById(R.id.item_price)).setText(String.format("Rs. %.2f", productModel.getSubTotalPrice()));
            ((TextView) convertView.findViewById(R.id.item_description)).setText(String.format("%s X %s", productModel.selectedQuantity, productModel.mealName));
            ordersListView.addView(convertView);
        }

        if (!TextUtils.isEmpty(orderModel.instruction)) {
            instructionTextView.setText(orderModel.instruction);
        } else {
            instructionContainer.setVisibility(View.GONE);
        }

        //Address
        AddressModel addressModel = orderModel.addressModel;
        addressNameTextView.setText(addressModel.name);
        addressTextView.setText(addressModel.address);
        locationView.setVisibility(View.VISIBLE);
        locationTextView.setText(orderModel.localityModel.geoAddress);

        updatePricing(Double.valueOf(orderModel.discount));
        paymentModeTextView.setText(orderModel.getPaymentMode());
    }

    private void updatePricing(double discount) {
        //Price Calculations
        double subTotal = 0;
        for (ProductModel productModel : orderModel.productModels) {
            subTotal += productModel.getSubTotalPrice();
        }
        double totalAmount = subTotal;

        totalAmount -= discount;

        double VAT = (preferenceManager.getLocality().getVat() * totalAmount) / 100;
        totalAmount += VAT;

        subTotalTextView.setText(String.format("Rs. %.2f", Math.max(subTotal, 0)));
        totalAmountTextView.setText(String.format("Rs. %.2f", Math.max(totalAmount, 0)));
        discountTextView.setText(String.format("Rs. %.2f", discount));
        vatTextView.setText(String.format("Rs. %.2f", Math.max(VAT, 0)));
    }
}
