package com.bueno.kitchen.activities.orders;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bueno.kitchen.R;
import com.bueno.kitchen.views.adapter.OrderListAdapter;
import com.bueno.kitchen.core.BaseActivity;
import com.bueno.kitchen.core.BuenoApplication;
import com.bueno.kitchen.database.OrderOperations;
import com.bueno.kitchen.models.core.OrderModel;
import com.bueno.kitchen.utils.Config;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class OrdersListActivity extends BaseActivity implements OrderListAdapter.OnClickListener {

    @Inject
    OrderOperations orderOperations;
    @Bind(R.id.order_list_recycler_view)
    RecyclerView orderListRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_list);
        ButterKnife.bind(this);
        BuenoApplication.getApp().getApplicationComponents().inject(this);
        enableBackButton();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setUpList();
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
        Intent intent = new Intent(this, OrderDetailActivity.class);
        intent.putExtra(Config.Properties.PROPERTY_ORDER, orderModel);
        startActivity(intent);
    }
}
