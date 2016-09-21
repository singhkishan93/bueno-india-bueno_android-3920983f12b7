package com.bueno.kitchen.views;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.bueno.kitchen.R;
import com.bueno.kitchen.core.BuenoApplication;
import com.bueno.kitchen.managers.PreferenceManager;
import com.bueno.kitchen.managers.analytics.AnalyticsManager;
import com.bueno.kitchen.managers.analytics.SegmentManager;
import com.bueno.kitchen.models.core.ProductModel;
import com.bueno.kitchen.utils.UtilitySingleton;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.squareup.otto.Bus;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by bedi on 10/04/16.
 */
public class ProductCellView extends RelativeLayout {
    @Bind(R.id.horizontal_separator)
    public View horizontalSeparator;
    @Bind(R.id.product_image)
    public ImageView productImage;
    @Bind(R.id.product_name)
    public TextView nameTextView;
    @Bind(R.id.product_price)
    public TextView priceTextView;
    @Bind(R.id.product_discounted_price)
    public TextView discountedPriceView;
    @Bind(R.id.add_cart_button)
    public View addCartButton;
    @Bind(R.id.veg_view)
    public View vegView;
    @Bind(R.id.switcher_container)
    public ViewSwitcher containerViewSwitcher;
    @Bind(R.id.description_text)
    public TextView descriptionTextView;
    @Bind(R.id.spice_text)
    public TextView spiceTextView;
    @Bind(R.id.serves_text)
    public TextView serveTextView;
    @Bind(R.id.title_text)
    public TextView titleTextView;
    @Bind(R.id.minus_cart_button)
    public View minusCartButton;
    @Bind(R.id.cart_count_text)
    public TextView cartCountTextView;
    @Bind(R.id.cart_sub_container)
    public View cartSubContainer;
    @Bind(R.id.status_view_switcher)
    public ViewSwitcher statusViewSwitcher;
    @Bind(R.id.status_text)
    public TextView productStatusText;
    @Bind(R.id.special_text)
    public TextView specialText;

    @Inject
    Bus eventBus;
    @Inject
    PreferenceManager preferenceManager;
    @Inject
    UtilitySingleton utilitySingleton;
    private ArrayList<ProductModel> ordersList;

    public ProductCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_product_cell_view, this);
        ButterKnife.bind(this);
        BuenoApplication.getApp().getApplicationComponents().inject(this);
        ordersList = preferenceManager.getTempOrder();
    }

    public void bindUI(final ProductModel productModel) {
        if (!productModel.showMoreInfo) {
            containerViewSwitcher.setDisplayedChild(1);
            vegView.setBackgroundResource(productModel.isVegetarian() ? R.drawable.shape_veg : R.drawable.shape_non_veg);
            nameTextView.setText(productModel.mealName);
            priceTextView.setText(String.format("%s", productModel.originalPrice));
            discountedPriceView.setText(String.format(" %s", productModel.discountedPrice));
            if (!productModel.originalPrice.equals(productModel.discountedPrice)) {
                discountedPriceView.setVisibility(View.VISIBLE);
                priceTextView.setPaintFlags(priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }

            ProductModel.ProductStatus status = productModel.getProductStatus();
            boolean isTodaySpecial = productModel.todaySpecial;
            statusViewSwitcher.setVisibility(status == ProductModel.ProductStatus.Unknown ? View.INVISIBLE : View.VISIBLE);
            statusViewSwitcher.setDisplayedChild(status == ProductModel.ProductStatus.Active ? 0 : 1);
            switch (status) {
                case Disable:
                    productStatusText.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.out_of_stock_red));
                    productStatusText.setText("Out of Stock");
                    break;
                case ComingSoon:
                    productStatusText.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.coming_soon_green));
                    productStatusText.setText("Coming Soon");
                    break;
            }
            if(isTodaySpecial){
                specialText.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.coming_soon_green));
                specialText.setText("Today's Special");
            }
        } else {
            containerViewSwitcher.setDisplayedChild(0);
            titleTextView.setText(productModel.mealName);
            descriptionTextView.setText(productModel.description);
            if (TextUtils.isEmpty(productModel.spiceLevel)) {
                horizontalSeparator.setVisibility(View.GONE);
                spiceTextView.setVisibility(View.GONE);
            } else {
                horizontalSeparator.setVisibility(View.VISIBLE);
                spiceTextView.setVisibility(View.VISIBLE);
                spiceTextView.setText(String.format("Spice Level: %s", productModel.spiceLevel));
            }
            serveTextView.setText(String.format("Serves: %s", productModel.serves));
        }
        Picasso.with(getContext())
                .load(productModel.image)
                .noFade()
                .fit()
                .centerCrop()
                .into(productImage);

        updateItemCart(minusCartButton,
                cartCountTextView,
                productModel,
                cartSubContainer);
        addCartButton.setOnClickListener(new DidClickCart(minusCartButton,
                cartCountTextView,
                productModel,
                cartSubContainer));
        minusCartButton.setOnClickListener(new DidClickCart(minusCartButton,
                cartCountTextView,
                productModel,
                cartSubContainer));
//        setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (previousPosition >= 0 && previousPosition != position) {
//                    productModels.get(previousPosition).showMoreInfo = false;
//                    notifyItemChanged(previousPosition);
//                }
//                productModels.get(position).showMoreInfo = !productModels.get(position).showMoreInfo;
//                notifyItemChanged(position);
//                previousPosition = position;
//
//                AnalyticsManager.with(productModel)
//                        .setScreen("ProductsList")
//                        .setAction(ProductAction.ACTION_DETAIL)
//                        .send();
//            }
//        });
    }

    private void updateItemCart(View minusCartButton,
                                TextView cartCountTextView,
                                ProductModel productModel,
                                View cartSubContainer) {
        if (productModel.selectedQuantity > 0) {
            minusCartButton.setVisibility(View.VISIBLE);
            cartCountTextView.setVisibility(View.VISIBLE);
            cartSubContainer.setBackgroundResource(R.drawable.shape_green_button_translucent);
            cartCountTextView.setText(String.valueOf(productModel.selectedQuantity));
        } else {
            minusCartButton.setVisibility(View.INVISIBLE);
            cartCountTextView.setVisibility(View.INVISIBLE);
            cartSubContainer.setBackground(null);

        }
    }

    public class DidClickCart implements View.OnClickListener {

        private View minusCartButton;
        private TextView cartCountTextView;
        private ProductModel productModel;
        private View cartSubContainer;

        public DidClickCart(View minusCartButton,
                            TextView cartCountTextView,
                            ProductModel productModel,
                            View cartSubContainer) {
            this.minusCartButton = minusCartButton;
            this.cartCountTextView = cartCountTextView;
            this.productModel = productModel;
            this.cartSubContainer = cartSubContainer;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.add_cart_button:

                    AnalyticsManager.with(productModel)
                            .setScreen("ProductsList")
                            .setAction(ProductAction.ACTION_ADD)
                            .send();


                    HashMap<String, String> properties = new HashMap<>();
                    properties.put("item_id", productModel.id);
                    properties.put("item_name", productModel.mealName);
                    properties.put("item_category", productModel.catName);
                    properties.put("item_category_id", productModel.catId);
                    properties.put("item_is_veg", productModel.isVeg);
                    properties.put("item_spice_level", productModel.spiceLevel);
                    SegmentManager.with(getContext())
                            .setName("addToCart")
                            .setProperties(properties)
                            .build(SegmentManager.EventType.TRACK);

                    try {
                        if (preferenceManager.getConfiguration().maxOrderItems > ProductModel.getQuantity(ordersList)) {
                            if (Integer.valueOf(productModel.maxOrderQuantity) >= 1) {
                                if (ordersList != null) {
                                    if (!ordersList.contains(productModel)) {
                                        productModel.resetQuantity(1);
                                        ordersList.add((ProductModel) productModel.clone());
                                    } else {
                                        ProductModel mProductModel = ordersList.get(ordersList.indexOf(productModel));
                                        if (mProductModel.selectedQuantity < Integer.valueOf(productModel.maxOrderQuantity)) {
                                            {
                                                mProductModel.updateQuantity(1);
                                                productModel.updateQuantity(1);
                                            }
                                        } else {
                                            utilitySingleton.ShowToast("Out of stock");
                                        }
                                    }
                                } else {
                                    preferenceManager.deleteTempOrder();
                                    ordersList = preferenceManager.getTempOrder();
//                                    ordersList = new ArrayList<>();
                                    productModel.resetQuantity(1);
                                    ordersList.add((ProductModel) productModel.clone());
                                }
                                eventBus.post(ordersList);
                            } else {
                                utilitySingleton.ShowToast("Out of stock");
                            }
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppCompatAlertDialogStyle);
                            builder.setTitle("Error")
                                    .setMessage(String.format(getContext().getString(R.string.max_order_string),
                                            preferenceManager.getConfiguration().maxOrderItems,
                                            preferenceManager.getConfiguration().contactUsPhone))
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }
                    } catch (Exception e) {
                        utilitySingleton.ShowToast("Something went wrong.");
                    }
                    break;
                case R.id.minus_cart_button:
                    if (ordersList != null && ordersList.contains(productModel)) {
                        AnalyticsManager.with(productModel)
                                .setScreen("ProductsList")
                                .setAction(ProductAction.ACTION_REMOVE)
                                .send();

                        ProductModel mProductModel = ordersList.get(ordersList.indexOf(productModel));
                        productModel.updateQuantity(-1);
                        mProductModel.updateQuantity(-1);
                        if (mProductModel.selectedQuantity <= 0) {
                            ordersList.remove(mProductModel);
                        }

                        eventBus.post(ordersList);
                    }
                    break;
            }

            preferenceManager.saveTempOrder(ordersList);
            updateItemCart(minusCartButton,
                    cartCountTextView,
                    productModel,
                    cartSubContainer);
        }
    }
}
