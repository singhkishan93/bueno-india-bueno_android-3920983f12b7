package com.bueno.kitchen.views.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bueno.kitchen.R;
import com.bueno.kitchen.models.core.OrderModel;

import java.util.List;

/**
 * Created by navjot on 17/11/15.
 */
public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderViewHolder> {

    private final LayoutInflater layoutInflater;
    private final OnClickListener onClickListener;
    private List<OrderModel> orderModels;

    public interface OnClickListener {
        void OnClickOrder(OrderModel orderModel);
    }

    public OrderListAdapter(Context context, OnClickListener onClickListener, List<OrderModel> orderModels) {
        this.onClickListener = onClickListener;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.orderModels = orderModels;
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OrderViewHolder(layoutInflater.inflate(R.layout.cell_order_list, parent, false));
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        final OrderModel orderModel = orderModels.get(position);
        holder.bindUI(orderModel);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.OnClickOrder(orderModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderModels.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        private TextView orderIdTextView;

        public OrderViewHolder(View itemView) {
            super(itemView);
            init();
        }

        private void init() {
            orderIdTextView = (TextView) itemView.findViewById(R.id.order_id_text);
        }

        public void bindUI(OrderModel orderModel) {
            orderIdTextView.setText(String.format("Order#%s", orderModel.orderId));
        }
    }

}
