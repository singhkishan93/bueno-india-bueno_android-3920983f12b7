package com.buenodelivery.kitchen.utils;

/**
 * Created by navjot on 30/10/15.
 */
public class Config {

    public static final String[] FACEBOOK_PERMISSIONS = {"public_profile", "email", "user_friends"};
    public static final int MAX_SAME_ADDRESS_DISTANCE = 50; //in meters

    public interface Endpoints {
        //Google
        String URL_GOOGLE_BASE = "https://maps.googleapis.com";
        String URL_GOOGLE_API = "/maps/api";
        String URL_GOOGLE_MATRIX = URL_GOOGLE_API + "/distancematrix/json";
        String URL_GOOGLE_REVERSE_GEOCODING = URL_GOOGLE_API + "/geocode/json";

        //OTP
        String URL_OTP_BASE = "https://2factor.in";
        String OTP_API_KEY = "5c601258-7efb-11e5-9a14-00163ef91450";
        String URL_GET_OTP = "/API/V1/" + OTP_API_KEY;
        String URL_JOOLEH_BASE = "https://jooleh.com";

        //App
        String URL_API = "/webapi";
        String URL_LOCALITIES = URL_API + "/services.php?service_type=localities";//area id
        String URL_PRODUCTS_LIST = URL_API + "/services.php?service_type=products_by_locality";
        String URL_CREATE_ORDER = URL_API + "/index.php";
        String URL_ORDER_STATUS = URL_API + "/order.php";
        String URL_VALIDATE_COUPON = URL_API + "/services.php?service_type=validate_coupon&src=android";
        String URL_TRACK_ORDER = "/api/v1/user/tracking";
        String URL_CONFIG =  URL_API + "/data.json";           //  "http://demo9621238.mockable.io/bueno"; // URL_API + "/data.json";     //"http://demo9621238.mockable.io/trialapi";   //URL_API + "/data.json";
        String URL_OFFERS = URL_API + "/offers.json";
        String URL_LOYALITY = URL_API + "/services.php?service_type=get_loyalty_points";
        public String DataJSON_url="http://www.bueno.kitchen/webapi/data.json";
    }

    public interface PaymentGateways {

        //EBS
        String EBS_SECRET_KEY = "f59c129cef206892a5c6268e40cf8de8";
        int EBS_ACCOUNT_ID = 17993;
        String EBS_PAGE_ID = "3252";

        //MobiKwik
        String MOBIKWIK_URL_CHECKSUM_GENERATOR = "http://www.bueno.kitchen/webapi/payment.php/3/checksum";
        String MOBIKWIK_URL_CHECKSUM_VERIFY = "http://www.bueno.kitchen/webapi/payment.php/3/verify";
        String MOBIKWIK_MERCHANT_ID = "MBK6465";
        String MOBIKWIK_MERCHANT_NAME = "Bueno";

        //Paytm
        String PAYTM_URL_CHECKSUM_GENERATOR = "http://www.bueno.kitchen/webapi/payment.php/5/checksum";
        String PAYTM_URL_CHECKSUM_VERIFY = "http://www.bueno.kitchen/webapi/payment.php/5/verify";
        String PAYTM_MERCHANT_ID = "buenok94734983487107";
        String PAYTM_MERCHANT_KEY = "5oMRUlC#@jOq9&W%";
        String PAYTM_WEBSITE = "Buenowap";

        //OLA MONEY
        String OLA_Access_Token  = "3u41p82uT1";
        String OLA_Access_Salt  = "n7QRr98Ui4";
        String OLA_Comment  = "paying_from_android_device";
        String OLA_UDF  = "UDF";
        String OLA_Currency  = "INR";
        String OLA_ReturnUrl  = "NA";
        String OLA_Notify_URL  = "NA";
        String OLA_CouponCode  = "NA";
        int OLA_RESULT_CODE = 1212 ;
        String Key_Merchent_Bill = "merchent_bill" ;

    }

    public interface Properties {
        String PROPERTY_IS_LOGGED_IN = "PROPERTY_IS_LOGGED_IN";
        String PROPERTY_EMAIL = "PROPERTY_EMAIL";
        String PROPERTY_PHONE = "PROPERTY_PHONE";
        String PROPERTY_NAME = "PROPERTY_NAME";
        String PROPERTY_IMAGE = "PROPERTY_IMAGE";
        String PROPERTY_IS_LOCALITY_PICKED = "PROPERTY_IS_LOCALITY_PICKED";
        String PROPERTY_LOCALITY = "PROPERTY_LOCALITY";
        String PROPERTY_ORDER_LIST = "PROPERTY_ORDER_LIST";
        String PROPERTY_ADDRESS = "PROPERTY_ADDRESS";
        String PROPERTY_ORDER = "PROPERTY_ORDER";
        String PROPERTY_PREF = "PREFERENCE_1";
        String PROPERTY_CONFIGURATION = "PROPERTY_CONFIGURATION";
        String PROPERTY_LOYALITY_RESPONSE = "PROPERTY_LOYALITY_RESPONSE";
        String PROPERTY_WALKTHROUGH = "PROPERTY_WALKTHROUGH";
    }

    public interface Intents {
        String INTENT_EMAIL = "INTENT_EMAIL";
        String INTENT_NAME = "INTENT_NAME";
        String INTENT_IMAGE = "INTENT_IMAGE";
        String INTENT_PRODUCT_LIST = "INTENT_PRODUCT_LIST";
        String INTENT_ORDER_ID = "INTENT_ORDER_ID";
        String INTENT_URL = "INTENT_URL";
        String INTENT_TITLE = "INTENT_TITLE";
        String INTENT_IS_FINISH = "INTENT_IS_FINISH";
    }

    public interface Fonts {
        String FONT_ROBOTO_REGULAR = "roboto_regular.ttf";
        String FONT_ROBOTO_BOLD = "roboto_bold.ttf";
    }

    public interface URLs {
        String URL_FAQ = "http://www.bueno.kitchen/pages/faqs";
        String URL_PRIVACY = "http://www.bueno.kitchen/pages/privacy_policy";
        String URL_TERMS = "http://www.bueno.kitchen/pages/terms_and_conditions";
        String URL_REFUND = "http://www.bueno.kitchen/pages/refund_and_cancellation";
    }

    public interface OrderElements{
        String KEY_ORDER_DATE = "order_date";
        String KEY_ORDER_ID = "order_time";
    }
}
