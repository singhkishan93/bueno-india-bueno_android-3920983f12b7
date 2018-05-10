package com.buenodelivery.kitchen;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by intel on 12/8/2016.
 */

public class Required_beans {


   static public int locality_id;

    static  public String delivery_charge;

    static  public String deleviry_charge_kitchen_area;

    static  public String packaging_charge;

    static  public String vat;

    static public String service_tax;

    static public String service_charge;

    static  public String pick_up_time;

    static public boolean is_ordering_closed;

    static public  String address_line1;

    static public  String address_line2;

    static public  String sub_area;

    static public  String closed_oredering_message;

    static private String unit_number;

    static private int order_mode_flag;

    static public Context current_con;

    static public  Intent current_intent;

    static public Intent getCurrent_intent() {
        return current_intent;
    }

    public static void setCurrent_intent(Intent current_intent) {
        Required_beans.current_intent = current_intent;
    }

    public static Context getCurrent_con() {
        return current_con;
    }

    public static void setCurrent_con(Context current_con) {
        Required_beans.current_con = current_con;
    }

    public int getOrder_mode_flag() {
        return order_mode_flag;
    }

    public void setOrder_mode_flag(int order_mode_flag) {
        this.order_mode_flag = order_mode_flag;

        Log.d("addy_flag",this.order_mode_flag+"");
    }
    public String getUnit_number() {
        return unit_number;
    }

    public void setUnit_number(String unit_number) {
        this.unit_number = unit_number;
    }


    public static String getAddress_line1() {
        return address_line1;
    }

    public static void setAddress_line1(String address_line1) {
        Required_beans.address_line1 = address_line1;
    }

    public static String getAddress_line2() {
        return address_line2;
    }

    public static void setAddress_line2(String address_line2) {
        Required_beans.address_line2 = address_line2;
    }

    public static String getSub_area() {
        return sub_area;
    }

    public static void setSub_area(String sub_area) {
        Required_beans.sub_area = sub_area;
    }

    public boolean is_ordering_closed() {
        return is_ordering_closed;
    }

    public void setIs_ordering_closed(boolean is_ordering_closed) {
        this.is_ordering_closed = is_ordering_closed;
    }

    public String getClosed_oredering_message() {
        return closed_oredering_message;
    }

    public void setClosed_oredering_message(String closed_oredering_message) {

        this.closed_oredering_message = closed_oredering_message;

    }

    public String getPick_up_time() {
        return pick_up_time;
    }

    public void setPick_up_time(String pick_up_time) {
        this.pick_up_time = pick_up_time;
    }

    public String getService_charge() {
        return service_charge;
    }

    public void setService_charge(String service_charge) {
        this.service_charge = service_charge;
    }

    public String getService_tax() {
        return service_tax;
    }

    public void setService_tax(String service_tax) {
        this.service_tax = service_tax;
    }

    public String getVat() {
        return vat;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }

    public String getPackaging_charge() {
        return packaging_charge;
    }

    public void setPackaging_charge(String packaging_charge) {
        this.packaging_charge = packaging_charge;
    }

    public String getDeleviry_charge_kitchen_area() {
        return deleviry_charge_kitchen_area;
    }

    public void setDeleviry_charge_kitchen_area(String deleviry_charge_kitchen_area) {
        this.deleviry_charge_kitchen_area = deleviry_charge_kitchen_area;
    }

    public String getDelivery_charge() {
        return delivery_charge;
    }

    public void setDelivery_charge(String delivery_charge) {
        this.delivery_charge = delivery_charge;
    }

    public int getLocality_id() {
        return locality_id;
    }

    public void setLocality_id(int locality_id) {
        this.locality_id = locality_id;
    }


}
