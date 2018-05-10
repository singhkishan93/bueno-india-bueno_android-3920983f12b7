package com.buenodelivery.kitchen.activities.extras;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.buenodelivery.kitchen.R;
import com.buenodelivery.kitchen.core.BaseActivity;
import com.buenodelivery.kitchen.models.response.LoyalityResponseModel;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class LoyalityActivity extends BaseActivity {
    @Bind(R.id.loyalty_text)
    TextView loyaityTextView;

    private Subscription loyalitySubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loyality2);
        enableBackButton();
        ButterKnife.bind(this);
        loadData();
    }

    private void loadData() {
        loyalitySubscription = restService.getLoyalityData(preferenceManager.getMobileNumber())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<LoyalityResponseModel>() {
                    @Override
                    public void call(LoyalityResponseModel loyalityResponseModel) {
                        if (loyalityResponseModel != null) {
                            showData(loyalityResponseModel.membership);
                        } else {
                            showData("NA");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        showData("NA");
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (loyalitySubscription != null) loyalitySubscription.unsubscribe();
    }

    private void showData(String membership) {
        StringBuilder buffer = new StringBuilder();
        switch(membership)
        {
            case "PLATINUM":
                buffer.append(getString(R.string.loyalty_program_info_text_platinum));
                break;
            case "GOLD":
                buffer.append(getString(R.string.loyalty_program_info_text_gold));
                break;
            case "DIAMOND":
                buffer.append(getString(R.string.loyalty_program_info_text_diamond));
                break;
            default:
                buffer.append(getString(R.string.loyalty_program_info_text_silver));

        }
        buffer.append(getString(R.string.loyalty_program_info_text_footer));
        loyaityTextView.setText(Html.fromHtml(buffer.toString()));
    }
}
