package com.buenodelivery.kitchen.activities.address;
//remove auto picker
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.buenodelivery.kitchen.R;
import com.buenodelivery.kitchen.Required_beans;
import com.buenodelivery.kitchen.JSONParse.JSONKitchenArea;
import com.buenodelivery.kitchen.views.adapter.AddressListAdapter;
import com.buenodelivery.kitchen.core.BaseActivity;
import com.buenodelivery.kitchen.core.BuenoApplication;
import com.buenodelivery.kitchen.database.AddressOperations;
import com.buenodelivery.kitchen.models.core.AddressModel;
import com.buenodelivery.kitchen.utils.Config;

import org.json.JSONObject;

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
    String pick_up_time;

    Required_beans req_beans = new Required_beans();

    private ProgressDialog dialog;

    TextView tv_heading;
    TextView tv_ok;
    TextView tv_2;

    //Dialog dialog_order_mode;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);
        BuenoApplication.getApp().getApplicationComponents().inject(this);
        ButterKnife.bind(this);
        req_beans.setCurrent_con(AddressListActivity.this);

        AsyncTaskRunner asyncTaskRunner=new AsyncTaskRunner();
        asyncTaskRunner.execute();

        subscriptions = new CompositeSubscription();
        updateAddressList(true);
        enableBackButton();

    }

    private void sendRequest_KitchenAreaJSON(){
        JsonObjectRequest JSONRequest = new JsonObjectRequest(Request.Method.GET,"http://www.bueno.kitchen/webapi/services.php?" +
                "service_type=get_area_kitchen&id="+req_beans.getLocality_id(),null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        show_KitchenAreaJSON(response);
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AddressListActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(JSONRequest);
    }

    private void show_KitchenAreaJSON(JSONObject json)
    {
        JSONKitchenArea JSONkitchenArea = new JSONKitchenArea(json);
        JSONkitchenArea.parseJson();


        //parseJSON should be protected
    }
    private class AsyncTaskRunner extends AsyncTask<String, Void, String> {
        private String resp;
        @Override
        protected String doInBackground(String... params) {
            Thread t = new Thread(new Runnable() {
                public void run()
                {
                    sendRequest_KitchenAreaJSON();
                }
            });
            t.start();
            Log.d("addy_thread","started");

            return resp;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //startActivity(new Intent(this, AddressListActivity.class));
            // execution of result of Long time consuming operation
            Log.d("addy_async", "ohoh" + req_beans.getPick_up_time());
            dialog.dismiss();

        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            dialog = new ProgressDialog(AddressListActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("Loading...");
            dialog.show();}

//        @Override
//        protected void onProgressUpdate(Void... values) {
//              super.onProgressUpdate(values);
//            if (req_beans.getPick_up_time()!=null)
//                dialog.show();
//        }
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
//        Intent intent = new Intent(this, CheckoutActivity.class);
//        intent.putExtra(Config.Properties.PROPERTY_ADDRESS, addressModel);
        //startActivityForResult(intent, REQUEST_CHECKOUT);
    }

    @Override
    public void OnEditAddress(AddressModel addressModel) {
        Intent intent = new Intent(this, AddressActivity.class);
        intent.putExtra(Config.Properties.PROPERTY_ADDRESS, addressModel);
        startActivityForResult(intent, REQUEST_EDIT_ADDRESS);
    }

    @Override
    public void VerifyOrderMode(final Intent it) {
        final Dialog dialog_order_mode  = new Dialog(AddressListActivity.this);

        dialog_order_mode.requestWindowFeature(Window.FEATURE_NO_TITLE); //remove action bar
        dialog_order_mode.setContentView(R.layout.choose_order);

        dialog_order_mode.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        dialog_order_mode.setContentView(R.layout.choose_order);
        tv_heading= (TextView)dialog_order_mode.findViewById(R.id.tv_dialog_message);
        tv_ok=(TextView)dialog_order_mode.findViewById(R.id.tv_ok);
        tv_2=(TextView)dialog_order_mode.findViewById(R.id.tv_2);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog_order_mode.getWindow().getAttributes());
        dialog_order_mode.getWindow().setAttributes(lp);

        pick_up_time=req_beans.getPick_up_time();

        if(pick_up_time==null) {
            pick_up_time = "-1";
        }

        Log.d("addy_pickup",pick_up_time+"");

        if(Integer.parseInt(pick_up_time)>0)
        {
            Log.d("addy_kitchen_area","inside if");

            tv_heading.setText("Choose your order mode..!");

            //((TextView) dialog_order_mode.findViewById(R.id.tv_dialog_message)).setText(req_beans.getClosed_oredering_message());
            tv_ok.setText("Pick Up");
            tv_2.setText("Delivery");

            dialog_order_mode.findViewById(R.id.tv_ok).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    req_beans.setOrder_mode_flag(1);

                    startActivityForResult(it, REQUEST_CHECKOUT);
                    //dialog_order_mode.dismiss();
                    //send parameter
                }
            });
            dialog_order_mode.findViewById(R.id.tv_2).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    req_beans.setOrder_mode_flag(0);

                    startActivityForResult(it, REQUEST_CHECKOUT);
                    //send parameter
                    dialog_order_mode.dismiss();
                }
            });

            dialog_order_mode.show();
        }
        else {if (pick_up_time=="-1")

            {
            Toast.makeText(getApplicationContext(),"Couldn't find location please try again",Toast.LENGTH_LONG).show();
            }
            else
            startActivityForResult(it, REQUEST_CHECKOUT);
        }
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

//public class MyCustomObject {
//    // Listener defined earlier
//    public interface MyCustomObjectListener {
//        public void onObjectReady(String title);
//
//        public void onDataLoaded(String data);
//    }
//
//    // Member variable was defined earlier
//    private MyCustomObjectListener listener;
//
//    // Constructor where listener events are ignored
//    public MyCustomObject() {
//        // set null or default listener or accept as argument to constructor
//        this.listener = null;
//        //loadDataAsync();
//    }
//}
