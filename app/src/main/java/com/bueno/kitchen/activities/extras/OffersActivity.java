package com.bueno.kitchen.activities.extras;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bueno.kitchen.R;
import com.bueno.kitchen.views.adapter.OfferListAdapter;
import com.bueno.kitchen.core.BaseActivity;
import com.bueno.kitchen.managers.analytics.SegmentManager;
import com.bueno.kitchen.models.response.OffersResponseModel;
import com.bueno.kitchen.network.RestProcess;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Response;

public class OffersActivity extends BaseActivity implements OfferListAdapter.OfferListListener, SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.error_text)
    TextView errorTextView;
    @Bind(R.id.swipe_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.switcher)
    ViewFlipper switcher;
    private OfferListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);
        enableBackButton();
        ButterKnife.bind(this);

        SegmentManager.with(this)
                .setName("offerView")
                .build(SegmentManager.EventType.SCREEN);

        loadData();
    }

    private void loadData() {
        restService.getOffers()
                .enqueue(new RestProcess<>(new RestProcess.RestCallback<OffersResponseModel>() {
                    @Override
                    public void success(OffersResponseModel offersResponseModel, Response<OffersResponseModel> response) {
                        swipeRefreshLayout.setRefreshing(false);
                        if (!offersResponseModel.offers.isEmpty()) {
                            bindUI(offersResponseModel.offers);
                            switcher.setDisplayedChild(2);
                        } else {
                            errorTextView.setText("No offers found!!!\nPlease try again later.");
                            switcher.setDisplayedChild(1);
                        }
                    }

                    @Override
                    public void failure(Throwable t) {
                        swipeRefreshLayout.setRefreshing(false);
                        errorTextView.setText(R.string.other_error_text);
                        switcher.setDisplayedChild(1);
                    }
                }, this, false));
    }

    @OnClick(R.id.error_button)
    public void onClick(View v) {
        switcher.setDisplayedChild(0);
        loadData();
    }

    private void bindUI(List<Map<String, String>> offerModels) {
        if (adapter == null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new OfferListAdapter(this, offerModels, this);
            recyclerView.setAdapter(adapter);
            swipeRefreshLayout.setOnRefreshListener(this);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClickOffer(String offerCode) {
        utilitySingleton.copyToClipboard(offerCode);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        alertDialog.setMessage("Offer code copied to the clipboard!!!")
                .setPositiveButton("Dismiss", null)
                .show();
    }

    @Override
    public void onRefresh() {
        loadData();
    }
}
