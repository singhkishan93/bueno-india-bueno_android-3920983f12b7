package com.bueno.kitchen.dagger.modules;

import com.bueno.kitchen.database.AddressOperations;
import com.bueno.kitchen.database.OrderOperations;
import com.bueno.kitchen.managers.DbManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by navjot on 9/11/15.
 */
@Module
public class DatabaseModule {

    @Provides
    @Singleton
    public OrderOperations provideOrderOperations(DbManager dbManager) {
        return new OrderOperations(dbManager);
    }

    @Provides
    @Singleton
    public AddressOperations provideAddressOperations(DbManager dbManager) {
        return new AddressOperations(dbManager);
    }

}
