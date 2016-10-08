package com.bueno.kitchen.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bueno.kitchen.R;
import com.bueno.kitchen.core.BaseActivity;
import com.bueno.kitchen.core.BuenoApplication;
import com.bueno.kitchen.database.OrderOperations;
import com.bueno.kitchen.managers.analytics.AnalyticsManager;
import com.bueno.kitchen.managers.analytics.SegmentManager;
import com.bueno.kitchen.managers.payment.core.PaymentManager;
import com.bueno.kitchen.models.core.AddressModel;
import com.bueno.kitchen.models.core.LocalityModel;
import com.bueno.kitchen.models.core.OrderModel;
import com.bueno.kitchen.models.core.PaymentDetailModel;
import com.bueno.kitchen.models.core.ProductModel;
import com.bueno.kitchen.models.response.CouponResponseModel;
import com.bueno.kitchen.models.response.LoyalityResponseModel;
import com.bueno.kitchen.models.response.ProductListResponseModel;
import com.bueno.kitchen.models.response.orders.CreateOrderResponseModel;
import com.bueno.kitchen.network.RestProcess;
import com.bueno.kitchen.utils.Config;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.google.gson.Gson;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class CheckoutActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener ,  PaymentResultListener {

    @Bind(R.id.subtotal_text)
    public TextView subTotalTextView;
    @Bind(R.id.discount_text)
    public TextView discountTextView;
    @Bind(R.id.vat_text)
    public TextView vatTextView;
    @Bind(R.id.total_amount_text)
    public TextView totalAmountTextView;
    @Bind(R.id.coupon_edit_text)
    public EditText couponEditText;
    @Bind(R.id.instruction_edit_text)
    public EditText instructionEditText;
    @Bind(R.id.coupon_input_layout)
    public TextInputLayout couponTextInputLayout;
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
    @Bind(R.id.apply_coupon_button)
    public TextView couponButton;
    @Bind(R.id.edit_address_button)
    public TextView changeAddressButton;
    @Bind(R.id.payment_radio_group)
    public RadioGroup paymentRadioGroup;
    @Bind(R.id.place_order_button)
    public Button placeOrderButton;
    @Bind(R.id.bueno_credit_container)
    public View creditContainerView;
    @Bind(R.id.credit_text)
    public TextView creditTextView;
    @Bind(R.id.checkbox_credits)
    public CheckBox checkBoxCredits;
    @Bind(R.id.coupon_text)
    public TextView couponTextView;
    @Bind(R.id.credit_container)
    public View creditContainer;
    @Bind(R.id.credit_text_1)
    public TextView creditTextView1;
    @Bind(R.id.script)
    public TextView script;

    @Inject
    OrderOperations orderOperations;

    private Map<String, CouponResponseModel.Item> couponProducts;
    private int couponCashback = 0;
    private String orderId;
    private String orderDate;
    private ArrayList<ProductModel> ordersList;
    private AddressModel addressModel;
    private double totalAmount;
    private double VAT;
    private double discount;
    private int creditsUsed;
    private double discountApplied;
    private String paymentResponse;
    private PaymentManager paymentManager;
    private Observable.OnSubscribe<CreateOrderResponseModel> placeOrder = new Observable.OnSubscribe<CreateOrderResponseModel>() {
        @Override
        public void call(Subscriber<? super CreateOrderResponseModel> subscriber) {

            boolean isCouponApplied = !TextUtils.isEmpty(couponEditText.getText().toString());
            boolean isOnlinePayment = isOnlinePayment() && !TextUtils.isEmpty(paymentResponse);

            //Remove zero quantity orders
            ArrayList<ProductModel> tempOrderList = new ArrayList<>();
            for (ProductModel productModel : ordersList) {
                if (productModel.selectedQuantity <= 0) {
                    tempOrderList.add(productModel);
                }
            }
            ordersList.removeAll(tempOrderList);

            //Create orders Map
            Map<String, String> ordersMap = new HashMap<>();
            for (int i = 0; i < ordersList.size(); i++) {
                ProductModel productModel = ordersList.get(i);
                ordersMap.put("orders[" + i + "][meal_id]", productModel.id);
                ordersMap.put("orders[" + i + "][qty]", productModel.selectedQuantity + "");
                ordersMap.put("orders[" + i + "][original_price]", productModel.originalPrice);
                ordersMap.put("orders[" + i + "][discount_price]", productModel.discountedPrice);
            }

            //Add coupons map
            if (isCouponApplied && couponProducts!=null && !couponProducts.isEmpty()) {
                Iterator couponIterator = couponProducts.entrySet().iterator();
                int i = ordersList.size();
                while (couponIterator.hasNext()) {
                    Map.Entry<String, CouponResponseModel.Item> entry = (Map.Entry<String, CouponResponseModel.Item>) couponIterator.next();
                    ordersMap.put("orders[" + i + "][meal_id]", entry.getKey());
                    ordersMap.put("orders[" + i + "][qty]", entry.getValue().quantity);
                    ordersMap.put("orders[" + i + "][original_price]", "0");
                    ordersMap.put("orders[" + i + "][discount_price]", "0");
                    i++;
                }
            }

            generateOrderId();
            String transactionStatus;
            JSONObject jObject;
            switch (getPaymentMode()) {
                case EBS:
                    try {
                        jObject = new JSONObject(paymentResponse);
                        transactionStatus = jObject.optString("TransactionId", null);
                    } catch (JSONException e) {
                        subscriber.onError(new Throwable(""));
                        return;
                    }
                    break;
                case PAYTM:
                    try {
                        jObject = new JSONObject(paymentResponse);
                        transactionStatus = jObject.optString("TXNID", null);
                    } catch (JSONException e) {
                        subscriber.onError(new Throwable(""));
                        return;
                    }
                    break;
                default:
                    transactionStatus = "NA";
            }

//            subscriber.onError(new Throwable(getString(R.string.error_text)));
            LocalityModel localityModel = preferenceManager.getLocality();
            CreateOrderResponseModel createOrderResponseModel;
            try {
                createOrderResponseModel = restService.createOrder("a5b5313df9625b6bc7b2273eae8abfa8",
                        "cc24028ca34c7050d00c3e53a0a4ca2c",
                        "android",
                        orderId,
                        addressModel.name,
                        preferenceManager.getEmail(),
                        preferenceManager.getMobileNumber(),
                        localityModel.getLocationCity(),
                        localityModel.city,
                        addressModel.address + ", " + localityModel.geoAddress,
                        String.valueOf(totalAmount),
                        VAT + "",
                        getPaymentModeInt(),
                        String.valueOf(discount),
                        instructionEditText.getText().toString(),
                        preferenceManager.getLocality().averageDeliveryTime,
                        orderDate,
                        isOnlinePayment ? transactionStatus : null,
                        isOnlinePayment ? paymentResponse : null,
                        isCouponApplied ? couponEditText.getText().toString() : null,
                        localityModel.latitude,
                        localityModel.longitude,
                        preferenceManager.getLoyalityModel() != null ? (checkBoxCredits.isChecked() ? creditsUsed : null) : null,
                        couponCashback,
                        ordersMap)
                        .execute()
                        .body();
                if (createOrderResponseModel.success == 1) {
                    LoyalityResponseModel loyalityResponseModel = restService.getLoyalityDataCall(preferenceManager.getMobileNumber())
                            .execute()
                            .body();
                    preferenceManager.saveLoyalityModel(loyalityResponseModel);
                    subscriber.onNext(createOrderResponseModel);
                } else {
                    subscriber.onError(new Throwable(TextUtils.isEmpty(createOrderResponseModel.errorMessage) ? null : createOrderResponseModel.errorMessage));
                }
            } catch (IOException e) {
                subscriber.onError(new Throwable(getString(R.string.error_text)));
            }
        }
    };

    private Func1<CreateOrderResponseModel, OrderModel> saveOrder = new Func1<CreateOrderResponseModel, OrderModel>() {
        @Override
        public OrderModel call(CreateOrderResponseModel createOrderResponseModel) {
            OrderModel orderModel = new OrderModel();

            orderModel.orderId = createOrderResponseModel.orderId;
            orderModel.productJson = new Gson().toJson(ordersList);
            orderModel.addressJson = addressModel.getJsonString();
            orderModel.discount = String.valueOf(discount);
            orderModel.vat = preferenceManager.getLocality().vat;
            orderModel.instruction = instructionEditText.getText().toString();
            orderModel.paymentMode = String.valueOf(getPaymentModeInt());
            orderModel.localityJson = preferenceManager.getLocality().getJsonString();

            return orderModel;
        }
    };

    private void generateOrderId() {
        if (TextUtils.isEmpty(orderId)) {
            Random rn = new Random();
            int random = rn.nextInt(10) + 1;
            Date currentDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
            orderId = sdf.format(currentDate) + "-" + random;
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            orderDate = sdf.format(currentDate);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        BuenoApplication.getApp().getApplicationComponents().inject(this);
        ButterKnife.bind(this);
        enableBackButton();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setUpScreen();
    }

    @Override
    protected void onPause() {
        super.onPause();
        preferenceManager.saveTempOrder(ordersList);
    }

    private void setUpScreen() {
        ordersList = preferenceManager.getTempOrder();
        setUpProducts();

        //Address
        changeAddressButton.setText("Change");
        addressModel = getIntent().getParcelableExtra(Config.Properties.PROPERTY_ADDRESS);
        if (addressModel != null) {
            addressNameTextView.setText(addressModel.name);
            addressTextView.setText(addressModel.address);
            locationTextView.setText(preferenceManager.getLocality().geoAddress);
            locationView.setVisibility(View.VISIBLE);
        }

        //Payment Modes
        paymentRadioGroup.setOnCheckedChangeListener(this);
        for (Map payment : preferenceManager.getConfiguration().paymentMethods) {
            Iterator iterator = payment.entrySet().iterator();
            if (iterator.hasNext()) {
                Map.Entry<String, String> map = (Map.Entry<String, String>) iterator.next();
                PaymentManager.PaymentModes paymentMode = PaymentManager.PaymentModes.getMode(map.getKey());

                if (paymentMode != null) {
                    RadioButton radioButton = new RadioButton(this);
                    radioButton.setText(map.getValue());
                    radioButton.setTag(paymentMode);

                    paymentRadioGroup.addView(radioButton);
                    if (paymentMode == PaymentManager.PaymentModes.COD)
                        radioButton.performClick();
                }
            }
        }
    }

    private void setUpProducts() {
        ordersListView.removeAllViews();
        if (ordersList != null) {
            for (ProductModel productModel : ordersList) {
                View convertView = getLayoutInflater().inflate(R.layout.cell_order, null);
                TextView priceTextView = (TextView) convertView.findViewById(R.id.item_price);
                priceTextView.setText(String.format("Rs. %.2f", productModel.getSubTotalPrice()));
                ((TextView) convertView.findViewById(R.id.item_description)).setText(productModel.mealName);
                View plusButton = convertView.findViewById(R.id.plus_button);
                View minusButton = convertView.findViewById(R.id.minus_button);
                TextView quantityTextView = (TextView) convertView.findViewById(R.id.quantity_text);

                quantityTextView.setText(String.valueOf(productModel.selectedQuantity));
                quantityTextView.setTag(productModel);

                convertView.findViewById(R.id.del_button).setOnClickListener(new DidPressDeleteButton(productModel, ordersListView, convertView));
                plusButton.setOnClickListener(new DidPressPlusButton(priceTextView, productModel, minusButton, quantityTextView));
                minusButton.setOnClickListener(new DidPressMinusButton(productModel, plusButton, quantityTextView, priceTextView));

                if (productModel.selectedQuantity >= Integer.valueOf(productModel.maxOrderQuantity)) {
                    plusButton.setVisibility(View.INVISIBLE);
                }
                if (productModel.selectedQuantity <= 0) {
                    minusButton.setVisibility(View.INVISIBLE);
                }

                ordersListView.addView(convertView);
            }
            updatePricing(0);
        }
    }

    private boolean isMinOrderAmountReached() {
        //        float amount = 0.0f;
//        if (ordersList != null) {
//
//            Toast.makeText(CheckoutActivity.this, "miniorder value = " + preferenceManager.getLocality().minOrderAmount, Toast.LENGTH_SHORT).show();
//            Toast.makeText(CheckoutActivity.this, "present value for booking = " + totalAmountTextView.getText().toString(), Toast.LENGTH_SHORT).show();
//
//
//            for (ProductModel productModel : ordersList) {
//                try {
//                    amount += (Float.valueOf(productModel.discountedPrice) * productModel.selectedQuantity);
//                } catch (Exception e) {
//                    continue;
//                }
//            }
//        }
//        Toast.makeText(CheckoutActivity.this, "amount that previously devloper was checking  = " + amount, Toast.LENGTH_SHORT).show();
//        if (Integer.valueOf(preferenceManager.getLocality().minOrderAmount) <= amount) {
//            return true;
//        } else {
//            utilitySingleton.ShowToast("You haven't reached the minimum order limit!!!");
//            return false;
//        }
        if (Float.valueOf(preferenceManager.getLocality().minOrderAmount) <= (Float.parseFloat(totalAmountTextView.getText().toString().replace("Rs.","").replace(" ","")) - Float.parseFloat(vatTextView.getText().toString().replace("Rs.","").replace(" ","")))) {
            return true;
        } else {
            utilitySingleton.ShowToast("You haven't reached the minimum order limit!!!");
            return false;
        }

    }

    private void updatePricing(double discount) {
        //Price Calculations
        double subTotal = 0;
        for (ProductModel productModel : ordersList) {
            subTotal += productModel.getSubTotalPrice();
        }
        totalAmount = subTotal;

        this.discount = discount;
        totalAmount -= discount;

        LoyalityResponseModel loyalityResponseModel = preferenceManager.getLoyalityModel();
        if (loyalityResponseModel != null && loyalityResponseModel.points > 0) {
            int maxCreditUsagePercent = preferenceManager.getConfiguration().maxCreditUsagePercent;
            int credits = (int) (totalAmount * maxCreditUsagePercent / 100);
            if (credits > (loyalityResponseModel.points))
                credits = loyalityResponseModel.points;
            if(credits>0) {
                creditTextView.setText(String.format("Use Bueno Rewards [Available: %d, max usable on this order: %d]",
                        loyalityResponseModel.points, credits));
                creditContainerView.setVisibility(View.VISIBLE);
                creditTextView1.setText(String.format("Rs. %.2f", (double) credits));
                creditsUsed = credits;

                if (checkBoxCredits.isChecked())
                    totalAmount -= credits;
            }
        }

        VAT = (preferenceManager.getLocality().getVat() * totalAmount) / 100;
        totalAmount += VAT;

        creditContainer.setVisibility(checkBoxCredits.isChecked() ? View.VISIBLE : View.GONE);
        subTotalTextView.setText(String.format("Rs. %.2f", Math.max(subTotal, 0)));
        totalAmountTextView.setText(String.format("Rs. %.2f", Math.max(totalAmount, 0)));
        discountTextView.setText(String.format("Rs. %.2f", discount));
        discountApplied = discount;
        vatTextView.setText(String.format("Rs. %.2f", Math.max(VAT, 0)));
    }

    private void disableCoupon() {
        if (!couponEditText.isEnabled()) {
            addressNameTextView.requestFocus();
            couponButton.setText("Apply Coupon");
            couponEditText.setText(null);
            couponEditText.setEnabled(true);
            couponTextView.setVisibility(View.GONE);
            couponTextView.setText(null);
            updatePricing(0);
            couponProducts = null;
            couponCashback = 0;
        }
    }

    @OnClick({R.id.apply_coupon_button,
            R.id.place_order_button,
            R.id.edit_address_button,
            R.id.bueno_credit_container})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bueno_credit_container:
                checkBoxCredits.setChecked(!checkBoxCredits.isChecked());
                updatePricing(discountApplied);
                break;
            case R.id.edit_address_button:
                Intent intent = new Intent();
                intent.putExtra(Config.Intents.INTENT_IS_FINISH, false);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.apply_coupon_button:
                if (couponEditText.isEnabled()) {
                    if (isMinOrderAmountReached() && !ordersList.isEmpty() && isValidCoupon()) {
                        StringBuilder orderIds = new StringBuilder();
                        StringBuilder qtyIds = new StringBuilder();
                        for (ProductModel productModel : ordersList) {
                            orderIds.append(productModel.id).append(",");
                            qtyIds.append(productModel.selectedQuantity).append(",");
                        }
                        restService.validateCoupon(orderIds.substring(0, orderIds.length() - 1),
                                couponEditText.getText().toString(),
                                qtyIds.substring(0, qtyIds.length() - 1),
                                preferenceManager.getEmail(),
                                preferenceManager.getMobileNumber(),
                                getPaymentModeInt(),
                                preferenceManager.getLocality().id,
                                preferenceManager.getLocality().getLocationCity())
                                .enqueue(new RestProcess<>(new RestProcess.RestCallback<CouponResponseModel>() {
                                    @Override
                                    public void success(CouponResponseModel couponResponseModel, Response<CouponResponseModel> response) {
                                        //Segment Analytics
                                        HashMap<String, String> properties = new HashMap<>();
                                        if (!TextUtils.isEmpty(couponEditText.getText().toString()))
                                            properties.put("coupon_code", couponEditText.getText().toString());
                                        properties.put("locality", preferenceManager.getLocality().id);
                                        JSONArray itemIdsArray = new JSONArray();
                                        for (ProductModel productModel : ordersList) {
                                            itemIdsArray.put(productModel.id);
                                        }
                                        properties.put("item_ids_array", itemIdsArray.toString());
                                        properties.put("pay_mode", getPaymentModeString());
                                        properties.put("api_response_string", response.raw().toString());
                                        properties.put("pay_amount", String.valueOf(totalAmount));
                                        properties.put("discount_amount", String.valueOf(discount));
                                        SegmentManager.with(CheckoutActivity.this)
                                                .setName("couponApply")
                                                .setProperties(properties)
                                                .build(SegmentManager.EventType.TRACK);

                                        couponProducts = couponResponseModel.items;
                                        couponCashback = (int) couponResponseModel.cashbackAmount;
                                        String addOnMessage = couponResponseModel.getAddOnMessage();
                                        couponTextView.setVisibility(TextUtils.isEmpty(addOnMessage) ? View.GONE : View.VISIBLE);
                                        couponTextView.setText(addOnMessage);

                                        couponEditText.setEnabled(false);
                                        couponButton.setText("Remove Coupon");
                                        updatePricing(couponResponseModel.discountAmount);
                                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CheckoutActivity.this, R.style.AppCompatAlertDialogStyle);
                                        alertDialog.setTitle(couponResponseModel.getDialogTitle())
                                                .setPositiveButton("Ok", null)
                                                .setCancelable(false)
                                                .setMessage(couponResponseModel.getDialogMessage())
                                                .show();
                                    }

                                    @Override
                                    public void failure(Throwable t) {
                                        disableCoupon();
                                        addressNameTextView.requestFocus();
                                        couponEditText.setText(null);
                                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CheckoutActivity.this, R.style.AppCompatAlertDialogStyle);
                                        alertDialog.setTitle("Failure")
                                                .setNegativeButton("Dismiss", null)
                                                .setCancelable(false)
                                                .setMessage(TextUtils.isEmpty(t.getMessage())
                                                        ? getString(R.string.error_text)
                                                        : t.getMessage())
                                                .show();
                                    }
                                }, this, true));
                    }
                } else {
                    disableCoupon();
                }
                break;
            case R.id.place_order_button:
                if (isMinOrderAmountReached()) {
                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setCancelable(false);
                    progressDialog.setTitle("Loading...");
                    progressDialog.show();
                    restService.getProductList(preferenceManager.getLocality().id)
                            .map(new Func1<ProductListResponseModel, Boolean>() {
                                @Override
                                public Boolean call(ProductListResponseModel productListResponseModel) {
                                    if (productListResponseModel != null && productListResponseModel.success == 1) {
                                        boolean isSuccess = true;
                                        for (ProductModel productModel : ordersList) {
                                            for (ProductModel productModelRaw : productListResponseModel.products) {
                                                if (productModelRaw.equals(productModel)
                                                        && Integer.valueOf(productModelRaw.maxOrderQuantity) < productModel.selectedQuantity) {
                                                    productModel.selectedQuantity = Integer.valueOf(productModelRaw.maxOrderQuantity);
                                                    isSuccess = false;
                                                }
                                            }
                                        }
                                        return isSuccess;
                                    }
                                    return false;
                                }
                            })
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<Boolean>() {
                                @Override
                                public void call(Boolean isSuccess) {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();

                                    if (isSuccess) {
                                        if(getPaymentModeInt() == 999){
                                            // here i am calling razor pay directly not bt payment manager
                                            razorPayExecution();
                                        }else {
                                            if (isOnlinePayment()) {
                                                initiateOnlinePayment();
                                            } else {
                                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(CheckoutActivity.this, R.style.AppCompatAlertDialogStyle);
                                                alertDialog.setMessage("Are you sure you want to place this order with Cash on Delivery?")
                                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                proceedPayment();
                                                            }
                                                        })
                                                        .setNegativeButton("No", null)
                                                        .show();
                                            }
                                        }
                                    } else {
                                        setUpProducts();
                                        disableCoupon();
                                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CheckoutActivity.this, R.style.AppCompatAlertDialogStyle);
                                        alertDialog.setMessage("Sorry, few products you selected are out of stock. Please review your cart again.")
                                                .setNeutralButton("Dismiss", null)
                                                .show();
                                    }
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                }
                            });
                }
                break;
        }
    }

    private boolean isValidCoupon() {
        couponTextInputLayout.setErrorEnabled(false);
        if (!TextUtils.isEmpty(couponEditText.getText().toString())) {
            return true;
        } else {
            couponTextInputLayout.setError("Coupon code is empty.");
            couponTextInputLayout.setErrorEnabled(true);
        }
        return false;
    }

    private void proceedPayment() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading...");
        progressDialog.show();
        Observable.create(placeOrder)
                .map(saveOrder)
                .flatMap(new Func1<OrderModel, Observable<OrderModel>>() {
                    @Override
                    public Observable<OrderModel> call(OrderModel orderModel) {
                        return orderOperations.addOrder(orderModel);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<OrderModel>() {
                    @Override
                    public void call(OrderModel orderModel) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                        //Google Analytics
                        for (ProductModel productModel : ordersList) {
                            AnalyticsManager.with(productModel)
                                    .setScreen("Checkout")
                                    .setAction(ProductAction.ACTION_CHECKOUT)
                                    .setQuantity(productModel.selectedQuantity)
                                    .setCoupon(couponEditText.getText().toString())
                                    .setOrderId(orderModel.orderId)
                                    .send();
                        }

                        //Segment Analytics
                        HashMap<String, String> properties = new HashMap<>();
                        if (!TextUtils.isEmpty(couponEditText.getText().toString()))
                            properties.put("coupon_code", couponEditText.getText().toString());
                        properties.put("locality", preferenceManager.getLocality().id);
                        JSONArray itemIdsArray = new JSONArray();
                        for (ProductModel productModel : ordersList) {
                            itemIdsArray.put(productModel.id);
                        }
                        properties.put("item_ids_array", itemIdsArray.toString());
                        properties.put("pay_mode", getPaymentModeString());
                        properties.put("api_response_string", "TODO");
                        properties.put("pay_amount", String.valueOf(totalAmount));
                        properties.put("discount_amount", String.valueOf(discount));
                        SegmentManager.with(CheckoutActivity.this)
                                .setName("order")
                                .setProperties(properties)
                                .build(SegmentManager.EventType.TRACK);


//                        ordersList = null;
                        preferenceManager.deleteTempOrder();
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CheckoutActivity.this, R.style.AppCompatAlertDialogStyle);
                        alertDialog.setTitle("Success")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                })
                                .setCancelable(false)
                                .setMessage("Order created successfully. Your order id is " + orderModel.orderId + ".")
                                .show();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        orderFailureMessage(throwable.getMessage());
                    }
                });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        disableCoupon();
        switch (getPaymentMode()) {
            case COD:
                placeOrderButton.setText(R.string.text_place_order);
                break;
            case EBS:
            case MOBIWIK:
            case PAYTM:
            case PAYU:
                placeOrderButton.setText("Proceed to Pay");
                break;
        }
    }

    private String getPaymentModeString() {
        return ((PaymentManager.PaymentModes) findViewById(paymentRadioGroup.getCheckedRadioButtonId()).getTag()).keyString;
    }

    private PaymentManager.PaymentModes getPaymentMode() {
        return ((PaymentManager.PaymentModes) findViewById(paymentRadioGroup.getCheckedRadioButtonId()).getTag());
    }

    private int getPaymentModeInt() {
        return ((PaymentManager.PaymentModes) findViewById(paymentRadioGroup.getCheckedRadioButtonId()).getTag()).key;
    }

    private boolean isOnlinePayment() {
        return getPaymentMode() != PaymentManager.PaymentModes.COD;
    }

    private void orderFailureMessage(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CheckoutActivity.this, R.style.AppCompatAlertDialogStyle);
        alertDialog.setTitle("Error")
                .setPositiveButton("Ok", null)
                .setCancelable(false)
                .setMessage(TextUtils.isEmpty(message) ? getString(R.string.error_text) : message)
                .show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        paymentManager.onActivityResult(0, 0, intent);
    }

    private void initiateOnlinePayment() {
        generateOrderId();

        PaymentDetailModel paymentDetailModel = new PaymentDetailModel(orderId,
                String.valueOf(totalAmount),
                addressModel.address);

        paymentManager = PaymentManager.with(this , CheckoutActivity.this)
                .setMode(getPaymentMode())
                .attachCallback(new PaymentManager.PaymentCallback() {
                    @Override
                    public void onSuccess(String params) {
                        paymentResponse = params;

                        //Track Payment
                        HashMap<String, String> properties = new HashMap<>();
                        properties.put("pay_mode", getPaymentModeString());
                        properties.put("gateway_response_string", params);
                        properties.put("amount", String.valueOf(totalAmount));
                        SegmentManager.with(CheckoutActivity.this)
                                .setName("onlinePayment")
                                .setProperties(properties)
                                .build(SegmentManager.EventType.TRACK);

                        proceedPayment();
                    }

                    @Override
                    public void onFailure(String message) {
                        orderFailureMessage(null);
                    }
                })
                .setConfig(paymentDetailModel)
                .initiatePayment();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        paymentManager.onActivityResult(requestCode, resultCode, data);
    }



    public class DidPressPlusButton implements View.OnClickListener {
        private TextView priceTextView;
        private TextView quantityTextView;
        private ProductModel productModel;
        private View minusButton;

        public DidPressPlusButton(TextView priceTextView, ProductModel productModel, View minusButton, TextView quantityTextView) {
            this.priceTextView = priceTextView;
            this.productModel = productModel;
            this.minusButton = minusButton;
            this.quantityTextView = quantityTextView;
        }

        @Override
        public void onClick(View v) {
            if (preferenceManager.getConfiguration().maxOrderItems > ProductModel.getQuantity(ordersList)) {
                disableCoupon();
                productModel.updateQuantity(1);
                quantityTextView.setText(String.valueOf(productModel.selectedQuantity));
                priceTextView.setText(String.format("Rs. %.2f", productModel.getSubTotalPrice()));

                if (productModel.selectedQuantity >= Integer.valueOf(productModel.maxOrderQuantity)) {
                    v.setVisibility(View.INVISIBLE);
                }
                if (productModel.selectedQuantity > 0) {
                    minusButton.setVisibility(View.VISIBLE);
                }

                updatePricing(discount);

                AnalyticsManager.with(productModel)
                        .setScreen("Checkout")
                        .setAction(ProductAction.ACTION_ADD)
                        .send();
                preferenceManager.saveTempOrder(ordersList);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutActivity.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("Error")
                        .setMessage(String.format(getString(R.string.max_order_string),
                                preferenceManager.getConfiguration().maxOrderItems,
                                preferenceManager.getConfiguration().contactUsPhone))
                        .setPositiveButton("Ok", null)
                        .show();
            }
        }
    }

    public class DidPressMinusButton implements View.OnClickListener {

        private TextView quantityTextView;
        private TextView priceTextView;
        private ProductModel productModel;
        private View plusButton;

        public DidPressMinusButton(ProductModel productModel, View plusButton, TextView quantityTextView, TextView priceTextView) {
            this.productModel = productModel;
            this.plusButton = plusButton;
            this.quantityTextView = quantityTextView;
            this.priceTextView = priceTextView;
        }

        @Override
        public void onClick(View v) {
            disableCoupon();
            productModel.updateQuantity(-1);
            quantityTextView.setText(String.valueOf(productModel.selectedQuantity));
            priceTextView.setText(String.format("Rs. %.2f", productModel.getSubTotalPrice()));

            if (productModel.selectedQuantity <= 0) {
                v.setVisibility(View.INVISIBLE);
            }
            if (productModel.selectedQuantity < Integer.valueOf(productModel.maxOrderQuantity)) {
                plusButton.setVisibility(View.VISIBLE);
            }

            updatePricing(discount);

            AnalyticsManager.with(productModel)
                    .setScreen("Checkout")
                    .setAction(ProductAction.ACTION_REMOVE)
                    .send();

            preferenceManager.saveTempOrder(ordersList);
        }
    }

    private class DidPressDeleteButton implements View.OnClickListener {
        private final ProductModel productModel;
        private final LinearLayout ordersListView;
        private final View convertView;

        public DidPressDeleteButton(ProductModel productModel, LinearLayout ordersListView, View convertView) {
            this.productModel = productModel;
            this.ordersListView = ordersListView;
            this.convertView = convertView;
        }

        @Override
        public void onClick(View v) {
            AnalyticsManager.with(productModel)
                    .setScreen("Checkout")
                    .setAction(ProductAction.ACTION_REMOVE)
                    .setQuantity(productModel.selectedQuantity)
                    .send();

            disableCoupon();
            ordersListView.removeView(convertView);
            ordersList.remove(productModel);

            updatePricing(discount);
            preferenceManager.saveTempOrder(ordersList);
        }
    }





//////////////   Razor Pay
    public void razorPayExecution (){
        proceedPayment_secondway("iiiiioooo");
//        generateOrderId();
//        PaymentDetailModel paymentDetailModel6 = new PaymentDetailModel(orderId,
//                String.valueOf(totalAmount),
//                addressModel.address);
//
//        final Checkout co = new Checkout();
//
//        try {
//            JSONObject options = new JSONObject();
//            options.put("name", "Beuno");
//            options.put("description", "Order Charges");
//            //You can omit the image option to fetch the image from dashboard
//            options.put("image", "http://bueno.kitchen/photo/web-page/1457416201unnamed.png");
//            options.put("currency", "INR");
//            if(String.valueOf(paymentDetailModel6.amount).contains(".")){
//                options.put("amount", ""+String.valueOf(paymentDetailModel6.amount).replace(".",""));
//            }else {
//                options.put("amount", ""+String.valueOf(paymentDetailModel6.amount) + "00");
//            }
//            options.put("amount", "100");
//
//            JSONObject preFill = new JSONObject();
//            preFill.put("email", "sm@razorpay.com");
//            preFill.put("contact", "9876543210");
//
//          //  options.put("prefill", preFill);
//
//            co.open(CheckoutActivity.this, options);
//        } catch (Exception e) {
//            Toast.makeText(CheckoutActivity.this, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
//                    .show();
//            e.printStackTrace();
//        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        Log.d("Onpayment_sucess ",""+s);
        proceedPayment_secondway(""+s);
    }

    @Override
    public void onPaymentError(int i, String s) {

        Log.d("Onpayment_error ", "" + s);
    }











    public void proceedPayment_secondway(String transaction_id){

        boolean isCouponApplied = !TextUtils.isEmpty(couponEditText.getText().toString());
        boolean isOnlinePayment = isOnlinePayment() && !TextUtils.isEmpty(paymentResponse);
        String transactionStatus = "NA";
        //Create orders Map
        Map<String, String> ordersMap = new HashMap<>();
        for (int i = 0; i < ordersList.size(); i++) {
            ProductModel productModel = ordersList.get(i);
            ordersMap.put("orders[" + i + "][meal_id]", productModel.id);
            ordersMap.put("orders[" + i + "][qty]", productModel.selectedQuantity + "");
            ordersMap.put("orders[" + i + "][original_price]", productModel.originalPrice);
            ordersMap.put("orders[" + i + "][discount_price]", productModel.discountedPrice);
        }

        //Add coupons map
        if (isCouponApplied && couponProducts!=null && !couponProducts.isEmpty()) {
            Iterator couponIterator = couponProducts.entrySet().iterator();
            int i = ordersList.size();
            while (couponIterator.hasNext()) {
                Map.Entry<String, CouponResponseModel.Item> entry = (Map.Entry<String, CouponResponseModel.Item>) couponIterator.next();
                ordersMap.put("orders[" + i + "][meal_id]", entry.getKey());
                ordersMap.put("orders[" + i + "][qty]", entry.getValue().quantity);
                ordersMap.put("orders[" + i + "][original_price]", "0");
                ordersMap.put("orders[" + i + "][discount_price]", "0");
                i++;
            }
        }






        Log.d("C* username * ", "a5b5313df9625b6bc7b2273eae8abfa8");
        Log.d("C* password * ", "cc24028ca34c7050d00c3e53a0a4ca2c");
        Log.d("C* tag*", "android");
        Log.d("C* order_no*", ""+orderId);
        Log.d("C* name *", "" + addressModel.name);
        Log.d("C* email*", "" + preferenceManager.getEmail());
        Log.d("C* mobile *", "" + preferenceManager.getMobileNumber());
        Log.d("C* city*", "" + preferenceManager.getLocality().getLocationCity());
        Log.d("C* locality*", "" + preferenceManager.getLocality().city);
        Log.d("C* address *", "" + addressModel.address + ", " + preferenceManager.getLocality().geoAddress);
        Log.d("C* order_amount *", "" + String.valueOf(totalAmount));
        Log.d("C* vat *", "" + VAT + "");
        Log.d("C* paymode *", "" + getPaymentModeInt());
        Log.d("C* coupon_amount *", "" + String.valueOf(discount));
        Log.d("C* instruction *", "" + instructionEditText.getText().toString());
        Log.d("C* order_minute *", "" + preferenceManager.getLocality().averageDeliveryTime);
        Log.d("C* order_date *", ""+orderDate );

        Log.d("C* transaction_id * ", ""+transaction_id);
        Log.d("C* transaction_details * ","need to understand code");
        Log.d("C* coupon_code * ", "need to understand code");
        Log.d("C* latitude * ", "" + preferenceManager.getLocality().latitude);
        Log.d("C* longitude * ", "" + preferenceManager.getLocality().longitude);
        Log.d("C* redeem_points * ", "need to understand code");
        Log.d("C* rewards * ", "" + couponCashback);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading...");
        progressDialog.show();

        AndroidNetworking.post("http://bueno.ennerzyy.com/webapi/index.php")
                .addBodyParameter("username", "a5b5313df9625b6bc7b2273eae8abfa8")
                .addBodyParameter("password", "cc24028ca34c7050d00c3e53a0a4ca2c")
                .addBodyParameter("tag", "android")
                .addBodyParameter("order_no", ""+orderId)
                .addBodyParameter("name", ""+addressModel.name)
                .addBodyParameter("email", ""+ preferenceManager.getEmail())
                .addBodyParameter("mobile", ""+ preferenceManager.getMobileNumber())
                .addBodyParameter("city", "" + preferenceManager.getLocality().getLocationCity())
                .addBodyParameter("locality", "" + preferenceManager.getLocality().city)
                .addBodyParameter("address", "" + addressModel.address + ", " + preferenceManager.getLocality().geoAddress)
                .addBodyParameter("order_amount", "" + String.valueOf(totalAmount))
                .addBodyParameter("vat", ""+VAT + "")
                .addBodyParameter("paymode", ""+getPaymentModeInt())
                .addBodyParameter("coupon_amount", ""+ String.valueOf(discount))
                .addBodyParameter("instruction", ""+instructionEditText.getText().toString())
                .addBodyParameter("order_minute", ""+preferenceManager.getLocality().averageDeliveryTime)
                .addBodyParameter("order_date", ""+orderDate)
                .addBodyParameter("transaction_id", ""+transaction_id)
                .addBodyParameter("transaction_details", ""+transaction_id)
                .addBodyParameter("coupon_code",""+couponEditText.getText().toString())
                .addBodyParameter("latitude", ""+preferenceManager.getLocality().latitude)
                .addBodyParameter("longitude", "" + preferenceManager.getLocality().longitude)
                .addBodyParameter("redeem_points", "0")
                .addBodyParameter("rewards", ""+couponCashback)
                .addUrlEncodeFormBodyParameter((HashMap<String, String>) ordersMap)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Toast.makeText(CheckoutActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                        Log.d("reponse after proceed payment ", "" + response);
                        script.setText(""+response);



                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                        //Google Analytics
                        for (ProductModel productModel : ordersList) {
                            AnalyticsManager.with(productModel)
                                    .setScreen("Checkout")
                                    .setAction(ProductAction.ACTION_CHECKOUT)
                                    .setQuantity(productModel.selectedQuantity)
                                    .setCoupon(couponEditText.getText().toString())
                                    .setOrderId(orderId)
                                    .send();
                        }

                        //Segment Analytics
                        HashMap<String, String> properties = new HashMap<>();
                        if (!TextUtils.isEmpty(couponEditText.getText().toString()))
                            properties.put("coupon_code", couponEditText.getText().toString());
                        properties.put("locality", preferenceManager.getLocality().id);
                        JSONArray itemIdsArray = new JSONArray();
                        for (ProductModel productModel : ordersList) {
                            itemIdsArray.put(productModel.id);
                        }


                        properties.put("item_ids_array", itemIdsArray.toString());
                        properties.put("pay_mode", getPaymentModeString());
                        properties.put("api_response_string", "TODO");
                        properties.put("pay_amount", String.valueOf(totalAmount));
                        properties.put("discount_amount", String.valueOf(discount));

                        SegmentManager.with(CheckoutActivity.this)
                                .setName("order")
                                .setProperties(properties)
                                .build(SegmentManager.EventType.TRACK);


//                        ordersList = null;
                        preferenceManager.deleteTempOrder();

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CheckoutActivity.this, R.style.AppCompatAlertDialogStyle);
                        alertDialog.setTitle("Success")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                })
                                .setCancelable(false)
                                .setMessage("Order created successfully. Your order id is " + orderId + ".")
                                .show();




                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.d("Place order error",""+error);
                        Toast.makeText(CheckoutActivity.this, "eroorrrrr =" + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }










}
