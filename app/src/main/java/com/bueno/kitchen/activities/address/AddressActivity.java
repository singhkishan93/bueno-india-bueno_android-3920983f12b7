package com.bueno.kitchen.activities.address;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bueno.kitchen.R;
import com.bueno.kitchen.core.BaseActivity;
import com.bueno.kitchen.core.BuenoApplication;
import com.bueno.kitchen.database.AddressOperations;
import com.bueno.kitchen.models.core.AddressModel;
import com.bueno.kitchen.models.core.LocalityModel;
import com.bueno.kitchen.utils.Config;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class AddressActivity extends BaseActivity {

    @Bind(R.id.name_edit_text)
    public EditText nameEditText;
    @Bind(R.id.address_edit_text)
    public EditText addressEditText;
    @Bind(R.id.name_input_layout)
    public TextInputLayout nameTextInputLayout;
    @Bind(R.id.address_input_layout)
    public TextInputLayout addressTextInputLayout;
    @Bind(R.id.add_address_button)
    public Button addAddressButton;
    @Bind(R.id.locality_edit_text)
    public EditText localityEditText;

    @Inject
    AddressOperations addressOperations;
    private AddressModel addressModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        ButterKnife.bind(this);
        BuenoApplication.getApp().getApplicationComponents().inject(this);
        enableBackButton();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        preFillAddress();
    }

    private void preFillAddress() {
        LocalityModel localityModel = preferenceManager.getLocality();
        if (localityModel != null) {
            localityEditText.setText(localityModel.geoAddress);
        }

        addressModel = getIntent().getParcelableExtra(Config.Properties.PROPERTY_ADDRESS);
        if (addressModel != null) {
            nameEditText.setText(addressModel.name);
            addressEditText.setText(addressModel.address);
            addAddressButton.setText("Save Address");
        } else {
            addAddressButton.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.add_address_button, R.id.checkout_button})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkout_button:
                saveAddress(true);
                break;
            case R.id.add_address_button:
                saveAddress(false);
                break;
        }
    }

    private void saveAddress(final boolean isCheckout) {
        if (isFormValid()) {
            if (addressModel == null)
                addressModel = new AddressModel();
            addressModel.address = addressEditText.getText().toString();
            addressModel.name = nameEditText.getText().toString();
            addressModel.lat = preferenceManager.getLocality().latitude;
            addressModel.lng = preferenceManager.getLocality().longitude;
            addressOperations.addAddress(addressModel)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<AddressModel>() {
                                   @Override
                                   public void call(AddressModel addressModel) {
                                       if (isCheckout) {
                                           Intent intent = new Intent();
                                           intent.putExtra(Config.Properties.PROPERTY_ADDRESS, addressModel);
                                           setResult(RESULT_OK, intent);
                                       } else
                                           setResult(RESULT_OK);
                                       finish();
                                   }
                               },
                            new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    utilitySingleton.ShowToast("Something went wrong. Please try again.");
                                }
                            });
        }
    }

    private boolean isFormValid() {
        nameTextInputLayout.setErrorEnabled(false);
        addressTextInputLayout.setErrorEnabled(false);
        if (!TextUtils.isEmpty(nameEditText.getText().toString())) {
            if (!TextUtils.isEmpty(addressEditText.getText().toString())) {
                return true;
            } else {
                addressTextInputLayout.setError("Address cannot be blank");
                addressTextInputLayout.setErrorEnabled(true);
            }
        } else {
            nameTextInputLayout.setError("Name cannot be blank");
            nameTextInputLayout.setErrorEnabled(true);
        }
        return false;
    }
}
