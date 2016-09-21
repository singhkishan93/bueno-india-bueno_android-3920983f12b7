package com.bueno.kitchen.activities.products;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bueno.kitchen.R;
import com.bueno.kitchen.activities.address.AddressListActivity;
import com.bueno.kitchen.activities.prelogin.LoginActivity;
import com.bueno.kitchen.core.BaseActivity;
import com.bueno.kitchen.models.core.ProductModel;
import com.bueno.kitchen.utils.Config;
import com.bueno.kitchen.views.CheckoutShelfView;
import com.bueno.kitchen.views.adapter.ProductListAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ProductListActivity extends BaseActivity implements CheckoutShelfView.CheckoutShelfViewInteractionListener {
    private static final int REQUEST_ADDRESS = 101;
    @Bind(R.id.products_list_recycler_view)
    public RecyclerView productsRecyclerView;
    @Bind(R.id.checkout_container)
    public CheckoutShelfView checkoutContainer;
    private ArrayList<ProductModel> ordersList, productList;
    private ProductListAdapter mAdapter;
    private Func1<ArrayList<ProductModel>, ArrayList<ProductModel>> mapOrders = new Func1<ArrayList<ProductModel>, ArrayList<ProductModel>>() {
        @Override
        public ArrayList<ProductModel> call(ArrayList<ProductModel> productModels) {
            ArrayList<ProductModel> cartProductModels = preferenceManager.getTempOrder();

            for (ProductModel productModel : productModels) {
                productModel.selectedQuantity = (cartProductModels != null && cartProductModels.contains(productModel)) ? cartProductModels.get(cartProductModels.indexOf(productModel)).selectedQuantity : 0;
            }
            return productModels;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        ButterKnife.bind(this);
        enableBackButton();
        productList = getIntent().getParcelableArrayListExtra(Config.Intents.INTENT_PRODUCT_LIST);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpProductList();
        setUpCart();
    }

    private void setUpProductList() {
        if (productList != null) {
            Observable.just(productList)
                    .map(mapOrders)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<ArrayList<ProductModel>>() {
                                   @Override
                                   public void call(ArrayList<ProductModel> productModels) {
                                       if (mAdapter == null) {
                                           mAdapter = new ProductListAdapter(ProductListActivity.this, productList);
                                           productsRecyclerView.setLayoutManager(new LinearLayoutManager(ProductListActivity.this));
                                           productsRecyclerView.setAdapter(mAdapter);
                                           productsRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                                               @Override
                                               public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                                                   if (parent.getChildLayoutPosition(view) == 0)
                                                       outRect.set(0, utilitySingleton.dpToPx(ProductListActivity.this, 2), 0, 0);
                                               }
                                           }, 0);
                                       } else {
                                           mAdapter.notifyDataSetChanged();
                                       }
                                   }
                               },
                            new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    finish();
                                }
                            });
            setTitle(productList.get(0).catName);
        } else {
            finish();
        }
    }

    private void setUpCart() {
        ordersList = preferenceManager.getTempOrder();
        checkoutContainer.attachListener(this);
        checkoutContainer.updateCart(ordersList);
    }

    @Override
    public void performCheckout() {
        if (!preferenceManager.isLoggedIn()) {
            startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_ADDRESS);
        } else {
            startActivity(new Intent(this, AddressListActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ADDRESS:
                    startActivity(new Intent(this, AddressListActivity.class));
                    break;
            }
        }
    }
}
