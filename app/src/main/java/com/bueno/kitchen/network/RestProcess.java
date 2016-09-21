package com.bueno.kitchen.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;

import com.bueno.kitchen.R;
import com.bueno.kitchen.models.core.BaseModel;
import com.bueno.kitchen.utils.UtilitySingleton;

import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by navjot on 30/10/15.
 */
public class RestProcess<T> implements Callback<T> {
    private final RestCallback<T> restCallback;

    public UtilitySingleton utilitySingleton;
    private ProgressDialog progressDialog;
    private Context context;

    public interface RestCallback<T> {
        void success(T model, Response<T> response);

        void failure(Throwable t);
    }

    public RestProcess(RestCallback<T> restCallback, Context context, boolean showDialog) {
        utilitySingleton = new UtilitySingleton(context);
        this.restCallback = restCallback;
        this.context = context;

        if (showDialog) {
            progressDialog = utilitySingleton.getDialog(context, true);
        }
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (progressDialog != null)
            progressDialog.dismiss();
        if (restCallback != null) {
            if (response.isSuccessful()) {
                if (response.body() instanceof BaseModel) {
                    BaseModel baseModel = (BaseModel) response.body();
                    if (baseModel.success == 1) {
                        restCallback.success(response.body(), response);
                    } else {
                        restCallback.failure(new Throwable(TextUtils.isEmpty(baseModel.errorMessage) ? null : baseModel.errorMessage));
                    }
                } else {
                    restCallback.success(response.body(), response);
                }
            } else {
                restCallback.failure(new Throwable(context.getString(R.string.error_text)));
            }
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (progressDialog != null)
            progressDialog.dismiss();
        if (t instanceof UnknownHostException) {
            utilitySingleton.ShowToast(context.getString(R.string.toast_network_not_available));
        }

        if (restCallback != null) {
            restCallback.failure(new Throwable(context.getString((t instanceof UnknownHostException) ? R.string.error_network : R.string.error_text)));
        }
    }
}
