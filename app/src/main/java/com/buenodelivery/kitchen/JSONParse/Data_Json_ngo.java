package com.buenodelivery.kitchen.JSONParse;

import android.util.Log;

import com.buenodelivery.kitchen.Ngo_bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by intel on 12/16/2016.
 */

public class Data_Json_ngo {

    private String json;
    private String param1;
    private String param2;
    public JSONObject Json;

    private String ngo_id;
    private String ngo_name;
    private String ngo_desc;
    private String ngo_donation_amount;
    private String ngo_image_url;

    //for ngo data will in array

    public Data_Json_ngo(JSONObject Json){
        this.Json=Json;
    }

    public void parseJSON() {
        JSONObject ngo_data = null;
        try {

           // ngo_data = Json.getJSONObject("");

            JSONArray ngo_data_array=Json.getJSONArray("ngos");

            for(int i=0;i<ngo_data_array.length();i++)
            {
                JSONObject jb = (JSONObject) ngo_data_array.get(i);
                ngo_id = jb.getString("id");
                ngo_name = jb.getString("name");
                ngo_desc = jb.getString("description");
                ngo_donation_amount=jb.getString("default_donation_amount");
                ngo_image_url=jb.getString("display_image_url");

                Ngo_bean ngo_beans = new Ngo_bean();

                ngo_beans.setNgo_id(ngo_id);
                ngo_beans.setNgo_name(ngo_name);
                ngo_beans.setNgo_desc(ngo_desc);
                ngo_beans.setNgo_donation_amount(ngo_donation_amount);
                ngo_beans.setNgo_image_url(ngo_image_url);
            }

            Log.d("addy","ngo set");

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("addy_exception",e.getMessage());
        }
    }
}
