package com.buenodelivery.kitchen.dagger.modules;

import com.buenodelivery.kitchen.BuildConfig;
import com.buenodelivery.kitchen.network.services.GoogleService;
import com.buenodelivery.kitchen.network.services.JoolehService;
import com.buenodelivery.kitchen.network.services.OTPService;
import com.buenodelivery.kitchen.network.services.RestService;
import com.buenodelivery.kitchen.utils.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by navjot on 30/10/15.
 */
@Module
public class ApiModule {

    @Provides
    @Singleton
    public RestService provideService() {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();


        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.connectTimeout(30, TimeUnit.SECONDS);
        if (!BuildConfig.RELEASE) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);
        }

        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(builder.build())
                .build();
        return restAdapter.create(RestService.class);
    }



    @Provides
    @Singleton
    public OTPService provideOTPService() {
        return provideRetrofit(Config.Endpoints.URL_OTP_BASE).create(OTPService.class);
    }

    @Provides
    @Singleton
    public JoolehService provideJoolehService() {
        return provideRetrofit(Config.Endpoints.URL_JOOLEH_BASE).create(JoolehService.class);
    }

    @Provides
    @Singleton
    public GoogleService provideGoogleService() {
        return provideRetrofit(Config.Endpoints.URL_GOOGLE_BASE).create(GoogleService.class);
    }

    private Retrofit provideRetrofit(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

}
