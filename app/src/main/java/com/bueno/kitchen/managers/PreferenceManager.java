package com.bueno.kitchen.managers;

import android.content.SharedPreferences;

import com.bueno.kitchen.models.core.LocalityModel;
import com.bueno.kitchen.models.core.ProductModel;
import com.bueno.kitchen.models.response.LoyalityResponseModel;
import com.bueno.kitchen.models.response.location.ConfigResponseModel;
import com.bueno.kitchen.utils.Config;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by navjot on 30/10/15.
 */
public class PreferenceManager {
    public SharedPreferences sharedPreferences;

    public PreferenceManager(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void saveLoginState(String email,
                               String phone,
                               String name,
                               String image) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Config.Properties.PROPERTY_IS_LOGGED_IN, true);
        editor.putString(Config.Properties.PROPERTY_EMAIL, email);
        editor.putString(Config.Properties.PROPERTY_PHONE, phone);
        editor.putString(Config.Properties.PROPERTY_NAME, name);
        editor.putString(Config.Properties.PROPERTY_IMAGE, image);
        editor.apply();
    }

    public void logOut() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Config.Properties.PROPERTY_IS_LOGGED_IN, false);
        editor.putString(Config.Properties.PROPERTY_EMAIL, null);
        editor.putString(Config.Properties.PROPERTY_IMAGE, null);
        editor.putString(Config.Properties.PROPERTY_NAME, null);
        editor.putString(Config.Properties.PROPERTY_PHONE, null);
        editor.putString(Config.Properties.PROPERTY_ORDER_LIST, null);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(Config.Properties.PROPERTY_IS_LOGGED_IN, false);
    }

    public boolean isWalkthroughSeen() {
        return sharedPreferences.getBoolean(Config.Properties.PROPERTY_WALKTHROUGH, false);
    }

    public void walkthroughSeen() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Config.Properties.PROPERTY_WALKTHROUGH, true);
        editor.commit();
    }

    public boolean isLocalityPicked() {
        return sharedPreferences.getBoolean(Config.Properties.PROPERTY_IS_LOCALITY_PICKED, false);
    }

    public void localityPicked(LocalityModel localityModel) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Config.Properties.PROPERTY_IS_LOCALITY_PICKED, localityModel != null);
        editor.putString(Config.Properties.PROPERTY_LOCALITY, localityModel != null ? localityModel.getJsonString() : null);
        editor.commit();
    }

    public LocalityModel getLocality() {
        String jsonString = sharedPreferences.getString(Config.Properties.PROPERTY_LOCALITY, null);
        if (jsonString != null) {
            return LocalityModel.getModel(jsonString);
        }
        return null;
    }

    private String getSavedString(String property) {
        return sharedPreferences.getString(property, null);
    }

    public String getEmail() {
        return getSavedString(Config.Properties.PROPERTY_EMAIL);
    }

    public String getUserName() {
        return getSavedString(Config.Properties.PROPERTY_NAME);
    }

    public String getUserImage() {
        return getSavedString(Config.Properties.PROPERTY_IMAGE);
    }

    public String getMobileNumber() {
        return getSavedString(Config.Properties.PROPERTY_PHONE);
    }

    public void saveTempOrder(ArrayList<ProductModel> orderList) {
        synchronized (lock) {
            if((orderList==null || orderList.size()==0) && tempOrderList!=null)
                tempOrderList.clear();
            String orderString = (orderList != null && !orderList.isEmpty()) ? new Gson().toJson(orderList) : null;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Config.Properties.PROPERTY_ORDER_LIST, orderString);
            editor.apply();
        }
    }

    private static ArrayList<ProductModel> tempOrderList;
    private final static Object lock = new Object();

    public ArrayList<ProductModel> getTempOrder() {
        synchronized (lock) {
            if(tempOrderList!=null)
                return tempOrderList;
            String ordersModelString = getSavedString(Config.Properties.PROPERTY_ORDER_LIST);
            if (ordersModelString != null) {
                tempOrderList = new Gson().fromJson(ordersModelString, new TypeToken<ArrayList<ProductModel>>() {
                }.getType());
            }
            else
                tempOrderList = new ArrayList<>();
        }
        return tempOrderList;
    }

    public void deleteTempOrder() {
        saveTempOrder(null);
    }

    public void saveConfiguration(ConfigResponseModel configResponseModel) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Config.Properties.PROPERTY_CONFIGURATION, configResponseModel != null ? new Gson().toJson(configResponseModel) : null);
        editor.commit();
    }

    public void saveLoyalityModel(LoyalityResponseModel loyalityResponseModel) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Config.Properties.PROPERTY_LOYALITY_RESPONSE, new Gson().toJson(loyalityResponseModel));
        editor.commit();
    }

    public LoyalityResponseModel getLoyalityModel() {
        String jsonString = sharedPreferences.getString(Config.Properties.PROPERTY_LOYALITY_RESPONSE, null);
        if (jsonString != null) {
            return new Gson().fromJson(jsonString, LoyalityResponseModel.class);
        }
        return null;
    }

    public ConfigResponseModel getConfiguration() {
        String jsonString = sharedPreferences.getString(Config.Properties.PROPERTY_CONFIGURATION, null);
        if (jsonString != null) {
            return new Gson().fromJson(jsonString, ConfigResponseModel.class);
        }
        return null;
    }

}
