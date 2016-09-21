package com.bueno.kitchen.models.core;

import android.os.Parcel;
import android.os.Parcelable;

import com.bueno.kitchen.R;
import com.bueno.kitchen.managers.payment.core.PaymentManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by navjot on 9/11/15.
 */
public class OrderModel implements Parcelable {
    public static final String TABLE_NAME = "Orders";

    public static final String COLUMN_ID = "_ID";
    public static final String COLUMN_MAIN_ID = "MAIN_ID";
    public static final String COLUMN_PRODUCT_JSON = "PRODUCT_JSON";
    public static final String COLUMN_ADDRESS_JSON = "ADDRESS_JSON";
    public static final String COLUMN_VAT = "VAT";
    public static final String COLUMN_DISCOUNT = "DISCOUNT";
    public static final String COLUMN_INSTRUCTION = "INSTRUCTION";
    public static final String COLUMN_PAYMENT_MODE = "PAYMENT_MODE";
    public static final String COLUMN_LOCALITY_JSON = "LOCALITY_JSON";
    public String orderId;

    public String vat;
    public String discount;
    public List<ProductModel> productModels;
    public AddressModel addressModel;
    public LocalityModel localityModel;
    public String productJson;
    public String addressJson;
    public String instruction;
    public String paymentMode;
    public String localityJson;

    public void setProductJson(String productJson) {
        this.productJson = productJson;
        productModels = new Gson().fromJson(productJson, new TypeToken<ArrayList<ProductModel>>() {
        }.getType());
    }

    public void setAddressJson(String addressJson) {
        this.addressJson = addressJson;
        addressModel = new Gson().fromJson(addressJson, AddressModel.class);
    }

    public void setLocalityJson(String localityJson) {
        this.localityJson = localityJson;
        localityModel = new Gson().fromJson(localityJson, LocalityModel.class);
    }

    public OrderModel() {
    }

    protected OrderModel(Parcel in) {
        orderId = in.readString();
        vat = in.readString();
        discount = in.readString();
        if (in.readByte() == 0x01) {
            productModels = new ArrayList<>();
            in.readList(productModels, ProductModel.class.getClassLoader());
        } else {
            productModels = null;
        }
        addressModel = (AddressModel) in.readValue(AddressModel.class.getClassLoader());
        productJson = in.readString();
        addressJson = in.readString();
        instruction = in.readString();
        paymentMode = in.readString();
        localityJson = in.readString();
        localityModel = (LocalityModel) in.readValue(LocalityModel.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderId);
        dest.writeString(vat);
        dest.writeString(discount);
        if (productModels == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(productModels);
        }
        dest.writeValue(addressModel);
        dest.writeString(productJson);
        dest.writeString(addressJson);
        dest.writeString(instruction);
        dest.writeString(paymentMode);
        dest.writeString(localityJson);
        dest.writeValue(localityModel);
    }

    public int getPaymentMode() {
        return (paymentMode != null && paymentMode.equals(PaymentManager.PaymentModes.COD.keyString)) ?
                R.string.cash_delivery_text: R.string.online_payment_text;
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<OrderModel> CREATOR = new Parcelable.Creator<OrderModel>() {
        @Override
        public OrderModel createFromParcel(Parcel in) {
            return new OrderModel(in);
        }

        @Override
        public OrderModel[] newArray(int size) {
            return new OrderModel[size];
        }
    };
}
