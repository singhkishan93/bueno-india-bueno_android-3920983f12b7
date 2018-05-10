package com.buenodelivery.kitchen.JSONParse;

/**
 * Created by intel on 12/19/2016.
 */

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.buenodelivery.kitchen.OrderHistoryBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by intel on 12/16/2016.
 */

public class JsonOrderHistory {

    private String order_number;
    private String order_status;
    private String order_date_time;
    private String order_subtotal;
    private String order_vat;
    private String order_service_tax;
    private String order_service_charge;
    private String order_delivery_charge;
    private String order_packaging_charge;
    private String order_discount;
    private String order_points_redeemed;
    private String order_donation_amount;
    private String order_total_amount;

    private String order_user_email_address;
    private String order_user_contact_number;
    private String order_user_full_name;
    private String order_is_pickup;
    private String order_user_address;
    private String order_user_location;
    private String order_user_address_kitchen;

    private String order_item_name;
    private String order_item_quantity;
    private String order_item_price;

    public JSONObject Json;

    OrderHistoryBean orderHistoryBean = new OrderHistoryBean();

    List<OrderHistoryBean> listdata = new ArrayList<OrderHistoryBean>();



    public JsonOrderHistory(JSONObject Json){
        this.Json=Json;
    }

    public void parseJSON() {
        try {

            JSONObject jSONObject=Json.getJSONObject("order");

            order_number=jSONObject.getString("order_no");
            order_status=jSONObject.getString("status");
            order_date_time=jSONObject.getString("created_at");
            order_subtotal=jSONObject.getString("order_amount");
            order_vat=jSONObject.getString("vat");
            order_service_tax=jSONObject.getString("service_tax");
            order_service_charge=jSONObject.getString("service_charge");
            order_delivery_charge=jSONObject.getString("delivery_charge");
            order_packaging_charge=jSONObject.getString("packaging_charge");
            order_discount=jSONObject.getString("discount");
            order_points_redeemed=jSONObject.getString("points_redeemed");
            order_donation_amount=jSONObject.getString("donation_amount");
            order_total_amount=jSONObject.getString("total_amount");
            order_user_full_name=jSONObject.getString("full_name");
            order_user_email_address=jSONObject.getString("email_address");
            order_user_contact_number=jSONObject.getString("contact_number");
            order_is_pickup=jSONObject.getString("is_pickup");
            order_user_address=jSONObject.getString("address");
            order_user_address_kitchen=jSONObject.getString("kitchen");

            orderHistoryBean.setOrder_number(order_number);
            orderHistoryBean.setOrder_status(order_status);
            orderHistoryBean.setOrder_date_time(order_date_time);
            orderHistoryBean.setOrder_subtotal(order_subtotal);
            orderHistoryBean.setOrder_vat(order_vat);
            orderHistoryBean.setOrder_service_tax(order_service_tax);
            orderHistoryBean.setOrder_service_charge(order_service_charge);
            orderHistoryBean.setOrder_delivery_charge(order_delivery_charge);
            orderHistoryBean.setOrder_packaging_charge(order_packaging_charge);
            orderHistoryBean.setOrder_discount(order_discount);
            orderHistoryBean.setOrder_points_redeemed(order_points_redeemed);
            orderHistoryBean.setOrder_donation_amount(order_donation_amount);
            orderHistoryBean.setOrder_total_amount(order_total_amount);
            orderHistoryBean.setOrder_user_full_name(order_user_full_name);
            orderHistoryBean.setOrder_user_email_address(order_user_email_address);
            orderHistoryBean.setOrder_user_contact_number(order_user_contact_number);
            orderHistoryBean.setOrder_is_pickup(order_is_pickup);
            orderHistoryBean.setOrder_user_address(order_user_address);
            orderHistoryBean.setOrder_user_address_kitchen(order_user_address_kitchen);

            Log.d("addy_OrderHistoryJson",orderHistoryBean.getOrder_service_charge()+"");

            JSONArray orderHistory_Items=jSONObject.getJSONArray("items");

            for(int i=0;i<orderHistory_Items.length();i++)
            {
                JSONObject jb = (JSONObject) orderHistory_Items.get(i);

                order_item_name = jb.getString("name");
                order_item_quantity = jb.getString("quantity");
                order_item_price=jb.getString("price");

                orderHistoryBean.setOrder_item_price(order_item_name);
                orderHistoryBean.setOrder_item_quantity(order_item_quantity);
                orderHistoryBean.setOrder_item_price(order_item_price);


                Log.d("addy_OrderHistoryJson",order_item_name);
                Log.d("addy_OrderHistoryJson",order_item_quantity);
                Log.d("addy_OrderHistoryJson",order_item_price);
                Log.d("addy_OrderHistoryJson","******************");

                listdata.add(orderHistoryBean);
            }
            orderHistoryBean.setItem_list(listdata);
            Log.d("addy_listdata","set listdata");


//            if (orderHistory_Items != null) {
//                for (int i=0;i<orderHistory_Items.length();i++){
//
//                    listdata.add(orderHistory_Items);
//
//                    orderHistoryBean.setOderItem(listdata);
//                }
//            }

            Log.d("addy_listdata",listdata.toString());

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("addy_exception",e.getMessage());
        }
    }
}

