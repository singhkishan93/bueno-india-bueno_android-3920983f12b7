package com.buenodelivery.kitchen.activities.orders;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.buenodelivery.kitchen.JSONParse.JsonOrderHistory;
import com.buenodelivery.kitchen.OrderHistoryBean;
import com.buenodelivery.kitchen.R;
import com.buenodelivery.kitchen.activities.utils.MapActivity;
import com.buenodelivery.kitchen.core.BaseActivity;
import com.buenodelivery.kitchen.models.core.AddressModel;
import com.buenodelivery.kitchen.models.core.OrderModel;
import com.buenodelivery.kitchen.models.core.ProductModel;
import com.buenodelivery.kitchen.models.response.orders.OrderDetailResponseModel;
import com.buenodelivery.kitchen.network.RestProcess;
import com.buenodelivery.kitchen.utils.Config;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Response;

public class OrderDetailActivity extends BaseActivity {

    private ProgressDialog dialog;

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


    OrderHistoryBean orderHistoryBean = new OrderHistoryBean();
    AsyncTaskRunner asyncTaskRunner=new AsyncTaskRunner();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);



        orderModel = getIntent().getParcelableExtra(Config.Properties.PROPERTY_ORDER);
        if (orderModel == null) {
            finish();
            return;
        }

        ButterKnife.bind(this);

        //asyncTaskRunner.execute();

        fetchOrderStatus();
        bindUI();

        enableBackButton();
    }

//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//
//    }
    private class AsyncTaskRunner extends AsyncTask<String, Void, String> {
        private String resp;
        @Override
        protected String doInBackground(String... params) {
            Thread t = new Thread(new Runnable() {
                public void run()
                {

                    sendRequest_JSONOrderHistory();
                }
            });
            t.start();
            while (orderHistoryBean.getItem_list()!=null)
            {
                dialog.show();
            }
            Log.d("addy_thread","started");

            return resp;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("addy_thread"," dialog ended");

            dialog.dismiss();
            bindUI();
//            else
//
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            dialog = new ProgressDialog(OrderDetailActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("Loading...");
            dialog.show();
        }

//        @Override
//        protected void onProgressUpdate(Void... values) {
//              super.onProgressUpdate(values);
//            if (req_beans.getPick_up_time()!=null)
//                dialog.show();
//        }
    }

    private void sendRequest_JSONOrderHistory(){
        JsonObjectRequest JSONRequest = new JsonObjectRequest(Request.Method.GET,"http://www.bueno.kitchen/webapi/services.php?service_type=get_order_by_id&id=92889",null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response_ngo) {
                        show_JSONOrderHistory(response_ngo);
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(OrderDetailActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(JSONRequest);
    }

    private void show_JSONOrderHistory(JSONObject json)
    {
        JsonOrderHistory  jsonOrderHistory = new JsonOrderHistory(json);
        jsonOrderHistory.parseJSON();
        //parseJSON should be protected
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
        //setTitle("Order#" + orderModel.orderId);
        ordersListView.removeAllViews();
        int size=orderHistoryBean.getItem_list().size();
        for (int i=0;i<size;i++) {
            View convertView = getLayoutInflater().inflate(R.layout.cell_order_history, null);
            ((TextView) convertView.findViewById(R.id.item_price)).setText(String.format("Rs. %.2f", Float.parseFloat(orderHistoryBean.getOrder_item_price())));
            ((TextView) convertView.findViewById(R.id.item_description)).setText(String.format("%s X %s", orderHistoryBean.getOrder_item_name(), orderHistoryBean.getOrder_item_quantity()));
            ordersListView.addView(convertView);

            Log.d("addy)bean.getitem",Float.parseFloat(orderHistoryBean.getOrder_item_price())+"");
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
