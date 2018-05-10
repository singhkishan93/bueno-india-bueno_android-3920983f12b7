package com.buenodelivery.kitchen.activities.orders;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.buenodelivery.kitchen.JSONParse.JsonOrderHistory;
import com.buenodelivery.kitchen.OrderHistoryBean;
import com.buenodelivery.kitchen.R;
import com.buenodelivery.kitchen.core.BaseActivity;
import com.buenodelivery.kitchen.core.BuenoApplication;
import com.buenodelivery.kitchen.database.OrderOperations;
import com.buenodelivery.kitchen.models.core.OrderModel;
import com.buenodelivery.kitchen.views.adapter.OrderListAdapter;

import org.json.JSONObject;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class OrdersListActivity extends BaseActivity implements OrderListAdapter.OnClickListener {

    ProgressDialog dialog;
    Thread t;
    AsyncTaskRunner asyncTaskRunner;

    @Inject
    OrderOperations orderOperations;
    @Bind(R.id.order_list_recycler_view)
    RecyclerView orderListRecyclerView;

    OrderHistoryBean orderHistoryBean = new OrderHistoryBean();

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_list);
        ButterKnife.bind(this);
        BuenoApplication.getApp().getApplicationComponents().inject(this);
        enableBackButton();
        asyncTaskRunner=new AsyncTaskRunner();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //setUpList();
    }

    private void setUpList() {
        orderListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderOperations.getAllOrders()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<OrderModel>>() {
                    @Override
                    public void call(List<OrderModel> orderModels) {
                        orderListRecyclerView.setAdapter(new OrderListAdapter(OrdersListActivity.this, OrdersListActivity.this, orderModels));
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        finish();
                    }
                });
    }

    @Override
    public void OnClickOrder(OrderModel orderModel) {
        dialog = new ProgressDialog(OrdersListActivity.this);

        Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {

                dialog.setCancelable(false);
                dialog.setTitle("Loading...");
                dialog.show();

                sendRequest_JSONOrderHistory();

            }
        });

//        while (orderHistoryBean.getOrder_service_charge()!=null)
//        {
//            dialog.show();
//        }
        Log.d("addy_thread",orderHistoryBean.getOrder_service_charge()+"");
        dialog.dismiss();

        //asyncTaskRunner.execute();
        intent = new Intent(OrdersListActivity.this, OrderDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    private class AsyncTaskRunner extends AsyncTask<Void, Void, String> {
        private String resp;

        @Override
        protected String doInBackground(Void... params) {
//            Thread t = new Thread(new Runnable() {
//                public void run()
//                {
                    sendRequest_JSONOrderHistory();
                    Log.d("addy_thread","started");
//
//                }
//            });
//            t.start();


            return resp;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            while (orderHistoryBean.getOrder_service_charge()!=null)
            {
                dialog.show();
            }

            Log.d("addy_thread",orderHistoryBean.getOrder_service_charge()+"");
            dialog.dismiss();
            Log.d("addy_thread dismiss",orderHistoryBean.getOrder_service_charge()+"");

            return;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            dialog = new ProgressDialog(OrdersListActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("Loading...");
            dialog.show();

            return;
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
                        Toast.makeText(OrdersListActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(JSONRequest);
    }

    private void show_JSONOrderHistory(JSONObject json)
    {
        JsonOrderHistory jsonOrderHistory = new JsonOrderHistory(json);
        jsonOrderHistory.parseJSON();
        //parseJSON should be protected
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            //dialog.dismiss();
            dialog = null;
        }
    }
}
