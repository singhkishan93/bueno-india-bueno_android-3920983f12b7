package com.bueno.kitchen.activities.extras;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bueno.kitchen.R;
import com.bueno.kitchen.core.BaseActivity;
import com.bueno.kitchen.models.response.LoyalityResponseModel;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ReferActivity extends BaseActivity {

    @Bind(R.id.referral_code_text)
    TextView referralCodeTextView;
    @Bind(R.id.switcher)
    ViewFlipper switcher;
    @Bind(R.id.error_text)
    TextView errorTextView;
    @Bind(R.id.referral_code_container)
    View referralCodeContainer;
    private Subscription loyalitySubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer);
        enableBackButton();
        ButterKnife.bind(this);
        loadData();
    }

    private void loadData() {
        ((TextView) findViewById(R.id.refer_info_text)).setText(Html.fromHtml(getString(R.string.refer_earn_info_text)));
        loyalitySubscription = restService.getLoyalityData(preferenceManager.getMobileNumber())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<LoyalityResponseModel>() {
                    @Override
                    public void call(LoyalityResponseModel loyalityResponseModel) {
                        if (loyalityResponseModel != null) {
                            showData(loyalityResponseModel);
                        } else {
                            errorTextView.setText(R.string.other_error_text);
                            switcher.setDisplayedChild(1);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        errorTextView.setText(R.string.other_error_text);
                        switcher.setDisplayedChild(1);
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (loyalitySubscription != null) loyalitySubscription.unsubscribe();
    }

    private void showData(LoyalityResponseModel loyalityResponseModel) {
        switcher.setDisplayedChild(2);
        if (!TextUtils.isEmpty(loyalityResponseModel.referralCode)) {
            referralCodeTextView.setText(loyalityResponseModel.referralCode);
            referralCodeContainer.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.error_button, R.id.referral_code_container})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.referral_code_container:
                String shareBody = getString(R.string.referral_code_subject);
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
//                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Your referral code is: " + referralCodeTextView.getText().toString());
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hi, I thoroughly enjoyed my experience with Bueno: World’s Kitchen. " +
                        "Global Menu, Gourmet Food created by 5 Star Chefs in their own state of the art kitchen. " +
                        "I am sure you will love it too. Get flat 100 Rs discount on your first order. " +
                        "Order on Bueno App using my code: " +referralCodeTextView.getText().toString() +
                        " between 9 AM to 4 AM. Thank Me Later!!"+
                        " Click on this link http://onelink.to/bueno to get started.");
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
                break;
            case R.id.error_button:
                switcher.setDisplayedChild(0);
                loadData();
                break;
        }
    }
}
