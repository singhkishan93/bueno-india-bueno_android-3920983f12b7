package com.buenodelivery.kitchen.views.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buenodelivery.kitchen.R;
import com.buenodelivery.kitchen.managers.analytics.AnalyticsManager;
import com.buenodelivery.kitchen.models.core.ProductModel;
import com.buenodelivery.kitchen.views.ProductCellView;
import com.google.android.gms.analytics.ecommerce.ProductAction;

import java.util.ArrayList;

/**
 * Created by navjot on 5/11/15.
 */
public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ItemViewHolder> {

    private final LayoutInflater layoutInflater;
    private final ArrayList<ProductModel> productModels;
    private int previousPosition;

    public ProductListAdapter(Context context, ArrayList<ProductModel> productModels) {
        this.productModels = productModels;
        previousPosition = -1;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(layoutInflater.inflate(R.layout.cell_product, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, final int position) {
        final ProductModel productModel = productModels.get(position);
        holder.productCellView.bindUI(productModel);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (previousPosition >= 0 && previousPosition != position) {
                    productModels.get(previousPosition).showMoreInfo = false;
                    notifyItemChanged(previousPosition);
                }
                productModels.get(position).showMoreInfo = !productModels.get(position).showMoreInfo;
                notifyItemChanged(position);
                previousPosition = position;

                AnalyticsManager.with(productModel)
                        .setScreen("ProductsList")
                        .setAction(ProductAction.ACTION_DETAIL)
                        .send();
            }
        });
    }

    @Override
    public int getItemCount() {
        return productModels.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public final ProductCellView productCellView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            productCellView = (ProductCellView) itemView.findViewById(R.id.product_cell_layout);
        }
    }

}
