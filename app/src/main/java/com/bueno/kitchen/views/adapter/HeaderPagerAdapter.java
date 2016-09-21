package com.bueno.kitchen.views.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bueno.kitchen.managers.analytics.AnalyticsManager;
import com.bueno.kitchen.models.core.ProductModel;
import com.bueno.kitchen.views.ProductCellView;
import com.google.android.gms.analytics.ecommerce.ProductAction;

import java.util.List;

public class HeaderPagerAdapter extends PagerAdapter {

    private Context context;
    private List<ProductModel> productModels;

    public HeaderPagerAdapter(Context context,
                              List<ProductModel> productModels) {
        this.context = context;
        this.productModels = productModels;
    }

    @Override
    public int getCount() {
        return productModels.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ProductCellView row = new ProductCellView(context, null);
        final ProductModel productModel = productModels.get(position);
        row.bindUI(productModels.get(position));

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < productModels.size(); i++) {
                    productModels.get(i).showMoreInfo = position == i && !productModels.get(i).showMoreInfo;
                }

                notifyDataSetChanged();
                AnalyticsManager.with(productModel)
                        .setScreen("ProductsList")
                        .setAction(ProductAction.ACTION_DETAIL)
                        .send();
            }
        });

        container.addView(row, 0);
        row.setTag(position);
        return row;
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}