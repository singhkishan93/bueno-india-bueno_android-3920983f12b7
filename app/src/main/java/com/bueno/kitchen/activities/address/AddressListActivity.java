package com.bueno.kitchen.activities.address;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bueno.kitchen.R;
import com.bueno.kitchen.activities.CheckoutActivity;
import com.bueno.kitchen.views.adapter.AddressListAdapter;
import com.bueno.kitchen.core.BaseActivity;
import com.bueno.kitchen.core.BuenoApplication;
import com.bueno.kitchen.database.AddressOperations;
import com.bueno.kitchen.models.core.AddressModel;
import com.bueno.kitchen.utils.Config;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class AddressListActivity extends BaseActivity implements AddressListAdapter.OnClickListener {

    private static final int REQUEST_EDIT_ADDRESS = 105;
    private final int REQUEST_CHECKOUT = 103;
    private final int REQUEST_ADD_ADDRESS = 104;

    @Bind(R.id.address_list_recycler_view)
    public RecyclerView addressRecyclerView;
    @Inject
    AddressOperations addressOperations;
    private AddressListAdapter mAdapter;
    private CompositeSubscription subscriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);
        BuenoApplication.getApp().getApplicationComponents().inject(this);
        ButterKnife.bind(this);
        subscriptions = new CompositeSubscription();
        updateAddressList(true);
        enableBackButton();
    }

    private void setUpAddressList() {
        addressRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new AddressListAdapter(this, this);
        addressRecyclerView.setAdapter(mAdapter);
        addressRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                if (parent.getChildLayoutPosition(view) == 0)
                    outRect.set(0, utilitySingleton.dpToPx(AddressListActivity.this, 16), 0, 0);
            }
        }, 0);
    }

    private void updateAddressList(final boolean showCheckout) {
        subscriptions.add(addressOperations.getAllAddress()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<AddressModel>>() {
                               @Override
                               public void call(List<AddressModel> addressModels) {
                                   if (!addressModels.isEmpty()) {   // the address list is not empty ,
                                       if (mAdapter == null) {
                                           setUpAddressList();
                                       }

                                       mAdapter.addAddress(addressModels);
                                       if (showCheckout && addressModels.size() == 1) {  // if there is only one address in database ,  it will pick it and set it over the checkout screen
                                           OnPickAddress(addressModels.get(0));
                                       }
                                   } else
                                       startActivityForResult(new Intent(AddressListActivity.this, AddressActivity.class),
                                               REQUEST_ADD_ADDRESS);
                               }
                           },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {

                            }
                        }));
    }

    @Override
    public void OnPickAddress(AddressModel addressModel) {
        Intent intent = new Intent(this, CheckoutActivity.class);
        intent.putExtra(Config.Properties.PROPERTY_ADDRESS, addressModel);
        startActivityForResult(intent, REQUEST_CHECKOUT);
    }

    @Override
    public void OnEditAddress(AddressModel addressModel) {
        Intent intent = new Intent(this, AddressActivity.class);
        intent.putExtra(Config.Properties.PROPERTY_ADDRESS, addressModel);
        startActivityForResult(intent, REQUEST_EDIT_ADDRESS);
    }

    @Override
    public void OnClickAddAddress() {
        startActivityForResult(new Intent(this, AddressActivity.class), REQUEST_ADD_ADDRESS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (subscriptions != null) {
            subscriptions.clear();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                switch (requestCode) {
                    case REQUEST_CHECKOUT:
                        if (data != null && !data.getBooleanExtra(Config.Intents.INTENT_IS_FINISH, true)) {
                            return;
                        }
                        setResult(RESULT_OK);
                        finish();
                        break;
                    case REQUEST_ADD_ADDRESS:
                    case REQUEST_EDIT_ADDRESS:
                        updateAddressList(false);
                        if (data != null) {
                            AddressModel addressModel = data.getParcelableExtra(Config.Properties.PROPERTY_ADDRESS);
                            if (addressModel != null) {
                                OnPickAddress(addressModel);
                            }
                        }
                        break;
                }
                break;
            case RESULT_CANCELED:
                switch (requestCode) {
                    case REQUEST_CHECKOUT:
                    case REQUEST_ADD_ADDRESS:
                        if (mAdapter == null || mAdapter.getItemCount() < 3) {
                            finish();
                        }
                        break;
                }
                break;
        }
    }
}
