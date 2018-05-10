package com.buenodelivery.kitchen.JSONParse;

import android.util.Log;

import com.buenodelivery.kitchen.Required_beans;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by intel on 12/9/2016.
 */

public class JSONKitchenArea {

    private String unit_number;

    private String address_line1;

    private String address_line2;

    private String sub_area;

    private String deleviry_charge;

    private String packaging_charge;

    private String vat;

    private String service_tax;

    private String service_charge;

    private String pickup_time;

    private String first_half;

    public JSONObject Json;


    public JSONKitchenArea(JSONObject Json)
    {
        this.Json=Json;
    }

public void parseJson()
{
    Required_beans req_beans = new Required_beans();

    try {

        JSONObject kitchen = Json.getJSONObject("kitchen");

        Log.d("addy_json_Parse","--> "+kitchen);



        unit_number = kitchen.getString("unit_number");
        address_line1 = kitchen.getString("address_line1");
        address_line2 = kitchen.getString("address_line2");
        sub_area=kitchen.getString("sub_area");
        deleviry_charge=kitchen.getString("delivery_charge");
        packaging_charge=kitchen.getString("packaging_charge");
        vat=kitchen.getString("vat");
        service_tax=kitchen.getString("service_tax");
        service_charge=kitchen.getString("service_charge");
        pickup_time=kitchen.getString("pickup_time");

        req_beans.setUnit_number(unit_number);
        req_beans.setAddress_line1(address_line1);
        req_beans.setAddress_line2(address_line2);
        req_beans.setSub_area(sub_area);
        req_beans.setDelivery_charge(deleviry_charge);
        req_beans.setPackaging_charge(packaging_charge);
        req_beans.setVat(vat);
        req_beans.setService_tax(service_tax);
        req_beans.setService_charge(service_charge);
        req_beans.setPick_up_time(pickup_time);

    } catch (JSONException e) {
        e.printStackTrace();
        Log.d("addy_exception",e.toString());
    }

}
}
