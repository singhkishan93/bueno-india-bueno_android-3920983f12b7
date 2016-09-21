package com.bueno.kitchen.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bueno.kitchen.core.BuenoApplication;
import com.bueno.kitchen.managers.DbManager;
import com.bueno.kitchen.managers.PreferenceManager;
import com.bueno.kitchen.models.core.AddressModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by navjot on 13/11/15.
 */
public class AddressOperations extends BaseOperations {

    private DbManager dbManager;
    @Inject
    PreferenceManager preferenceManager;

    public final static String CREATE_TABLE_QUERY = "CREATE TABLE "
            + AddressModel.TABLE_NAME + "("
            + AddressModel.COLUMN_ID + " INTEGER PRIMARY KEY,"
            + AddressModel.COLUMN_ADDRESS + " TEXT,"
            + AddressModel.COLUMN_NAME + " TEXT,"
            + AddressModel.COLUMN_LAT + " TEXT,"
            + AddressModel.COLUMN_LNG + " TEXT"
            + ")";

    @Inject
    public AddressOperations(DbManager dbManager) {
        this.dbManager = dbManager;
        BuenoApplication.getApp().getApplicationComponents().inject(this);
    }

    public Observable<AddressModel> addAddress(AddressModel addressModel) {
        return Observable.just(addressModel)
                .flatMap(new Func1<AddressModel, Observable<AddressModel>>() {
                    @Override
                    public Observable<AddressModel> call(final AddressModel addressModel) {
                        return Observable.create(new Observable.OnSubscribe<AddressModel>() {
                            @Override
                            public void call(Subscriber<? super AddressModel> subscriber) {
                                try {
                                    SQLiteDatabase db = dbManager.getWritableDatabase();

                                    ContentValues values = new ContentValues();
                                    if (addressModel._ID != null)
                                        values.put(AddressModel.COLUMN_ID, addressModel._ID);
                                    values.put(AddressModel.COLUMN_ADDRESS, addressModel.address);
                                    values.put(AddressModel.COLUMN_NAME, addressModel.name);
                                    values.put(AddressModel.COLUMN_LAT, addressModel.lat);
                                    values.put(AddressModel.COLUMN_LNG, addressModel.lng);

                                    // Inserting Row
                                    db.insertWithOnConflict(AddressModel.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                                    db.close();

                                    if (addressModel._ID == null) addressModel._ID = -1;

                                    subscriber.onNext(addressModel);
                                } catch (Exception e) {
                                    subscriber.onError(e);
                                }
                            }
                        });
                    }
                });

    }

    public Observable<List<AddressModel>> getAllAddress() {
        return Observable.create(new Observable.OnSubscribe<List<AddressModel>>() {
            @Override
            public void call(Subscriber<? super List<AddressModel>> subscriber) {
                try {
                    List<AddressModel> addressModels = new ArrayList<>();
                    String selectQuery = "SELECT  * FROM " + AddressModel.TABLE_NAME;

                    SQLiteDatabase db = dbManager.getWritableDatabase();
                    Cursor cursor = db.rawQuery(selectQuery, null);

                    if (cursor.moveToFirst()) {
                        do {
                            AddressModel addressModel = new AddressModel();
                            addressModel._ID = cursor.getInt(0);
                            addressModel.address = cursor.getString(1);
                            addressModel.name = cursor.getString(2);
                            addressModel.lat = cursor.getString(3);
                            addressModel.lng = cursor.getString(4);

                            if (addressModel.isValidAddress(preferenceManager.getLocality().getLocation()))
                                addressModels.add(addressModel);
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                    subscriber.onNext(addressModels);
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public void truncateTable() {
        dbManager.getWritableDatabase().delete(AddressModel.TABLE_NAME, null, null);
    }
}
