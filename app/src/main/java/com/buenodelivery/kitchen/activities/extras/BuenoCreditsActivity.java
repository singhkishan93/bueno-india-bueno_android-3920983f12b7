package com.buenodelivery.kitchen.activities.extras;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.buenodelivery.kitchen.R;
import com.buenodelivery.kitchen.core.BaseActivity;
import com.buenodelivery.kitchen.models.response.LoyalityResponseModel;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class BuenoCreditsActivity extends BaseActivity {

    @Bind(R.id.credits_text)
    TextView creditsTextView;
    @Bind(R.id.switcher)
    ViewFlipper switcher;
    @Bind(R.id.error_text)
    TextView errorTextView;
    private Subscription loyalitySubscription;
    @Bind(R.id.credits_info_text)
    TextView creditsInfoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loyality);
        enableBackButton();
        ButterKnife.bind(this);
        loadData();
    }

    private void loadData() {
        creditsInfoTextView.setText(Html.fromHtml(getString(R.string.bueno_credits_info_text)));

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
        creditsTextView.setText(String.valueOf(loyalityResponseModel.points));
    }

    @OnClick({R.id.error_button})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.error_button:
                switcher.setDisplayedChild(0);
                loadData();
                break;
        }
    }
}
