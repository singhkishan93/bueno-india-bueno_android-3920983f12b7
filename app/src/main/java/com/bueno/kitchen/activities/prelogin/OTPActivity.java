package com.bueno.kitchen.activities.prelogin;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bueno.kitchen.R;
import com.bueno.kitchen.core.BaseActivity;
import com.bueno.kitchen.core.BuenoApplication;
import com.bueno.kitchen.managers.analytics.SegmentManager;
import com.bueno.kitchen.models.OTPModel;
import com.bueno.kitchen.network.RestProcess;
import com.bueno.kitchen.network.services.OTPService;
import com.bueno.kitchen.utils.Config;
import com.segment.analytics.Analytics;
import com.segment.analytics.Traits;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Response;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class OTPActivity extends BaseActivity {

    private final static int MIN_OTP_TIME = 60;
    private static final int REQUEST_PERMISSION = 101;

    @Inject
    OTPService otpService;
    @Inject
    Bus eventBus;
    @Bind(R.id.otp_button)
    Button commonButton;
    @Bind(R.id.phone_text_input)
    TextInputLayout phoneInputLayout;
    @Bind(R.id.otp_text_input)
    TextInputLayout otpInputLayout;
    @Bind(R.id.phone_edit_text)
    EditText phoneEditText;
    @Bind(R.id.otp_edit_text)
    EditText otpEditText;
    @Bind(R.id.submit_container)
    View otpContainer;
    @Bind(R.id.timer_text)
    TextView timerTextView;
    @Bind(R.id.voice_otp_button)
    View voiceOtpView;
    @Bind(R.id.submit_button)
    View submitOtpView;
    private String otpString;
    private Subscription timerSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        BuenoApplication.getApp().getActivityComponents().inject(this);
        enableBackButton();
        ButterKnife.bind(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, REQUEST_PERMISSION);
        }
    }

    @OnClick({R.id.otp_button, R.id.submit_button, R.id.voice_otp_button})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_button:
                if (isValidOTP()) {
                    preferenceManager.saveLoginState(getIntent().getStringExtra(Config.Intents.INTENT_EMAIL),
                            phoneEditText.getText().toString(),
                            getIntent().getStringExtra(Config.Intents.INTENT_NAME),
                            getIntent().getStringExtra(Config.Intents.INTENT_IMAGE));

                    try {
                        Analytics.with(this).identify(preferenceManager.getMobileNumber(),
                                new Traits().putEmail(preferenceManager.getEmail()).putPhone(preferenceManager.getMobileNumber())
                                        .putValue("appVersion", getPackageManager().getPackageInfo(getPackageName(), 0).versionName), null);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    HashMap<String, String> properties = new HashMap();
                    properties.put("email", preferenceManager.getEmail());
                    properties.put("phone", preferenceManager.getMobileNumber());

                    SegmentManager.with(this)
                            .setName("login")
                            .setProperties(properties)
                            .build(SegmentManager.EventType.TRACK);

                    otpInputLayout.setErrorEnabled(false);
                    setResult(RESULT_OK);
                    finish();
                }
                break;
            case R.id.otp_button:
                if (isValidNumber()) {
                    otpString = utilitySingleton.generateRandomOTP();
                    otpService.sendOTP("SMS",
                            phoneEditText.getText().toString(),
                            otpString)
                            .enqueue(new RestProcess<>(new RestProcess.RestCallback<OTPModel>() {
                                @Override
                                public void success(OTPModel model, Response<OTPModel> response) {
                                    updateTimer(true);
                                }

                                @Override
                                public void failure(Throwable t) {
                                    utilitySingleton.ShowToast("Something went wrong, please try again!");
                                }
                            }, this, true));
                }
                break;
            case R.id.voice_otp_button:
                if (isValidNumber()) {
                    otpString = utilitySingleton.generateRandomOTP();
                    otpService.sendOTP("VOICE", phoneEditText.getText().toString(),
                            otpString)
                            .enqueue(new RestProcess<>(new RestProcess.RestCallback<OTPModel>() {
                                @Override
                                public void success(OTPModel model, Response<OTPModel> response) {
                                    updateTimer(true);
                                }

                                @Override
                                public void failure(Throwable t) {
                                    utilitySingleton.ShowToast("Something went wrong, please try again!");
                                }
                            }, this, true));
                }
                break;
        }
    }

    private boolean isValidNumber() {
        phoneInputLayout.setErrorEnabled(false);
        String phoneString = phoneEditText.getText().toString();
        if (phoneString.length() > 0) {
            if (phoneString.length() == 10) {
                return true;
            } else {
                phoneInputLayout.setError("Invalid phone number.");
            }
        } else {
            phoneInputLayout.setError("Phone number cannot be empty.");
        }
        phoneInputLayout.setErrorEnabled(true);
        phoneEditText.setEnabled(true);
        return false;
    }

    private boolean isValidOTP() {
        otpInputLayout.setErrorEnabled(false);
        String otpString = otpEditText.getText().toString();
        if (otpString.length() > 0) {
            if (otpString.length() == 4 && otpString.equalsIgnoreCase(this.otpString)) {
                return true;
            } else {
                otpInputLayout.setError("Invalid OTP.");
            }
        } else {
            otpInputLayout.setError("OTP cannot be empty.");
        }
        otpInputLayout.setErrorEnabled(true);
        return false;
    }

    private void updateTimer(boolean isStart) {
        if (isStart) {
            updateTimer(false);
            phoneEditText.setEnabled(false);
            phoneInputLayout.setErrorEnabled(false);
            otpContainer.setVisibility(View.VISIBLE);
            voiceOtpView.setVisibility(View.GONE);
            commonButton.setVisibility(View.GONE);
            otpEditText.requestFocus();
            timerTextView.setText(String.format("%d seconds left", MIN_OTP_TIME));
            timerSubscription = Observable.interval(1, TimeUnit.SECONDS).take(MIN_OTP_TIME + 1, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            long secLeft = (MIN_OTP_TIME - aLong - 1);
                            timerTextView.setText(String.format("%d seconds left", secLeft < 0 ? 0 : secLeft));
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {

                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            otpString = "";
                            phoneEditText.setEnabled(true);
                            otpContainer.setVisibility(View.GONE);
                            voiceOtpView.setVisibility(View.VISIBLE);
                            commonButton.setVisibility(View.VISIBLE);
                        }
                    });
        } else if (timerSubscription != null) {
            timerSubscription.unsubscribe();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateTimer(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        eventBus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        eventBus.unregister(this);
    }

    @Subscribe
    public void receiveOTPCode(String otpString) {
        if (!phoneEditText.isEnabled()) {
            otpEditText.setText(otpString);
            submitOtpView.performClick();
        }
    }
}
