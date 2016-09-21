package com.bueno.kitchen.network.services;

import com.bueno.kitchen.models.response.CouponResponseModel;
import com.bueno.kitchen.models.response.LocalitiesResponseModel;
import com.bueno.kitchen.models.response.LoyalityResponseModel;
import com.bueno.kitchen.models.response.OffersResponseModel;
import com.bueno.kitchen.models.response.ProductListResponseModel;
import com.bueno.kitchen.models.response.location.ConfigResponseModel;
import com.bueno.kitchen.models.response.orders.CreateOrderResponseModel;
import com.bueno.kitchen.models.response.orders.OrderDetailResponseModel;
import com.bueno.kitchen.utils.Config;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by navjot on 30/10/15.
 */
public interface RestService {

    @GET(Config.Endpoints.URL_LOCALITIES)
    Call<LocalitiesResponseModel> getLocalities();

    @GET(Config.Endpoints.URL_PRODUCTS_LIST)
    Observable<ProductListResponseModel> getProductList(@Query("id") String localityId);

    @FormUrlEncoded
    @POST(Config.Endpoints.URL_CREATE_ORDER)
    Call<CreateOrderResponseModel> createOrder(@Field("username") String userName,
                                               @Field("password") String password,
                                               @Field("tag") String tag,
                                               @Field("order_no") String orderNumber,
                                               @Field("name") String name,
                                               @Field("email") String email,
                                               @Field("mobile") String mobile,
                                               @Field("city") String city,
                                               @Field("locality") String locality,
                                               @Field("address") String address,
                                               @Field("order_amount") String orderAmount,
                                               @Field("vat") String vat,
                                               @Field("paymode") int paymentMode,
                                               @Field("coupon_amount") String couponAmount,
                                               @Field("instruction") String instruction,
                                               @Field("order_minute") String orderMinute,
                                               @Field("order_date") String orderDate,
                                               @Field("transaction_id") String transactionId,
                                               @Field("transaction_details") String transactionDetails,
                                               @Field("coupon_code") String couponCode,
                                               @Field("latitude") String latitude,
                                               @Field("longitude") String longitude,
                                               @Field("redeem_points") Integer redeemPoints,
                                               @Field("rewards") Integer cashbackPoints,
                                               @FieldMap Map<String, String> ordersMap);

    @GET(Config.Endpoints.URL_VALIDATE_COUPON)
    Call<CouponResponseModel> validateCoupon(@Query("items") String itemIds,
                                             @Query("couponcode") String couponCode,
                                             @Query("qtys") String qtyIds,
                                             @Query("email") String email,
                                             @Query("mobile") String mobile,
                                             @Query("paymode") int paymentMode,
                                             @Query("locality") String locality,
                                             @Query("city") String city);

    @GET(Config.Endpoints.URL_ORDER_STATUS)
    Call<OrderDetailResponseModel> getOrderDetail(@Query("order_id") String orderId);

    @GET(Config.Endpoints.URL_CONFIG)
    Observable<ConfigResponseModel> getLocationConfig();

    @GET(Config.Endpoints.URL_OFFERS)
    Call<OffersResponseModel> getOffers();

    @GET(Config.Endpoints.URL_LOYALITY)
    Observable<LoyalityResponseModel> getLoyalityData(@Query("mobile") String mobile);

    @GET(Config.Endpoints.URL_LOYALITY)
    Call<LoyalityResponseModel> getLoyalityDataCall(@Query("mobile") String mobile);
}
