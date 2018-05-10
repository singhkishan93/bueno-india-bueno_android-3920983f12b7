package com.buenodelivery.kitchen.managers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.buenodelivery.kitchen.database.AddressOperations;
import com.buenodelivery.kitchen.database.OrderOperations;
import com.buenodelivery.kitchen.models.core.AddressModel;
import com.buenodelivery.kitchen.models.core.OrderModel;

/**
 * Created by navjot on 9/11/15.
 */
public class DbManager extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "BuenoKitchen";

    public DbManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(AddressOperations.CREATE_TABLE_QUERY);
        sqLiteDatabase.execSQL(OrderOperations.CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AddressModel.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + OrderModel.TABLE_NAME);

        // Create tables again
        onCreate(sqLiteDatabase);
    }
}
