package com.buenodelivery.kitchen.ParseJSON;

import android.util.Log;

import com.buenodelivery.kitchen.Required_beans;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by intel on 12/9/2016.
 */

public class data_JSON {
    private String json;
    private String param1;
    private String param2;
    public JSONObject Json;

    private String ngo_id;
    private String ngo_name;
    private String ngo_desc;
    private String ngo_donation_amount;
    private String ngo_image_url;

    private boolean bool_param1;

    Required_beans req_beans = new Required_beans();

    //for ngo data will in array

    public data_JSON(String json){
        this.json = json;
    }
    public data_JSON(JSONObject Json){
        this.Json=Json;
    }

    public void parseJSON() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);

            param1 = jsonObject.getString("is_ordering_closed");
            param2 = jsonObject.getString("ordering_closed_message");

            Boolean bool_param1 = Boolean.valueOf(param1);

            req_beans.setIs_ordering_closed(bool_param1);
            req_beans.setClosed_oredering_message(param2);


        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("addy_exception",e.toString());
        }
    }
}
