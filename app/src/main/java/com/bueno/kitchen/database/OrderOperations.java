package com.bueno.kitchen.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bueno.kitchen.managers.DbManager;
import com.bueno.kitchen.models.core.OrderModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by navjot on 9/11/15.
 */
public class OrderOperations extends BaseOperations {

    private DbManager dbManager;

    public final static String CREATE_TABLE_QUERY = "CREATE TABLE "
            + OrderModel.TABLE_NAME + "("
            + OrderModel.COLUMN_ID + " INTEGER PRIMARY KEY,"
            + OrderModel.COLUMN_MAIN_ID + " TEXT,"
            + OrderModel.COLUMN_PRODUCT_JSON + " TEXT,"
            + OrderModel.COLUMN_ADDRESS_JSON + " TEXT,"
            + OrderModel.COLUMN_VAT + " TEXT,"
            + OrderModel.COLUMN_DISCOUNT + " TEXT,"
            + OrderModel.COLUMN_INSTRUCTION + " TEXT,"
            + OrderModel.COLUMN_PAYMENT_MODE + " TEXT,"
            + OrderModel.COLUMN_LOCALITY_JSON + " TEXT"
            + ")";

    @Inject
    public OrderOperations(DbManager dbManager) {
        this.dbManager = dbManager;
    }

    public Observable<OrderModel> addOrder(final OrderModel orderModel) {
        return Observable.just(orderModel)
                .flatMap(new Func1<OrderModel, Observable<OrderModel>>() {
                    @Override
                    public Observable<OrderModel> call(final OrderModel orderModel) {
                        return Observable.create(new Observable.OnSubscribe<OrderModel>() {
                            @Override
                            public void call(Subscriber<? super OrderModel> subscriber) {
                                try {
                                    SQLiteDatabase db = dbManager.getWritableDatabase();

                                    ContentValues values = new ContentValues();
                                    values.put(OrderModel.COLUMN_MAIN_ID, orderModel.orderId);
                                    values.put(OrderModel.COLUMN_PRODUCT_JSON, orderModel.productJson);
                                    values.put(OrderModel.COLUMN_ADDRESS_JSON, orderModel.addressJson);
                                    values.put(OrderModel.COLUMN_VAT, orderModel.vat);
                                    values.put(OrderModel.COLUMN_DISCOUNT, orderModel.discount);
                                    values.put(OrderModel.COLUMN_INSTRUCTION, orderModel.instruction);
                                    values.put(OrderModel.COLUMN_PAYMENT_MODE, orderModel.paymentMode);
                                    values.put(OrderModel.COLUMN_LOCALITY_JSON, orderModel.localityJson);

                                    // Inserting Row
                                    db.insert(OrderModel.TABLE_NAME, null, values);
                                    db.close();
                                    subscriber.onNext(orderModel);
                                } catch (Exception e) {
                                    subscriber.onError(e);
                                }
                            }
                        });
                    }
                });

    }

    public Observable<List<OrderModel>> getAllOrders() {
        return Observable.create(new Observable.OnSubscribe<List<OrderModel>>() {
            @Override
            public void call(Subscriber<? super List<OrderModel>> subscriber) {
                try {
                    List<OrderModel> orderModels = new ArrayList<>();
                    String selectQuery = "SELECT  * FROM " + OrderModel.TABLE_NAME;

                    SQLiteDatabase db = dbManager.getWritableDatabase();
                    Cursor cursor = db.rawQuery(selectQuery, null);

                    if (cursor.moveToFirst()) {
                        do {
                            OrderModel orderModel = new OrderModel();
                            orderModel.orderId = cursor.getString(1);
                            orderModel.setProductJson(cursor.getString(2));
                            orderModel.setAddressJson(cursor.getString(3));
                            orderModel.vat = cursor.getString(4);
                            orderModel.discount = cursor.getString(5);
                            orderModel.instruction = cursor.getString(6);
                            orderModel.paymentMode = cursor.getString(7);
                            orderModel.setLocalityJson(cursor.getString(8));
                            orderModels.add(orderModel);
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                    subscriber.onNext(orderModels);
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public void truncateTable() {
        dbManager.getWritableDatabase().delete(OrderModel.TABLE_NAME, null, null);
    }
}
