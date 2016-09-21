package com.bueno.kitchen.views.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bueno.kitchen.R;
import com.bueno.kitchen.models.core.OfferModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by navjot on 17/11/15.
 */
public class OfferListAdapter extends RecyclerView.Adapter<OfferListAdapter.OfferViewHolder> {

    private final LayoutInflater layoutInflater;
    private final OfferListListener offerListListener;
    private List<OfferModel> offerModels;

    public interface OfferListListener {
        void onClickOffer(String offerCode);
    }

    public OfferListAdapter(Context context,
                            List<Map<String, String>> offerModels,
                            OfferListListener offerListListener) {
        this.offerListListener = offerListListener;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.offerModels = new ArrayList<>();
        for (Map<String, String> map : offerModels) {
            Map.Entry<String, String> localMap = map.entrySet().iterator().next();
            this.offerModels.add(OfferModel.getBuilder()
                    .setOfferText(localMap.getValue())
                    .setOfferCode(localMap.getKey())
                    .build());
        }
    }

    @Override
    public OfferViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OfferViewHolder(layoutInflater.inflate(R.layout.cell_offer, parent, false));
    }

    @Override
    public void onBindViewHolder(OfferViewHolder holder, final int position) {
        final OfferModel offerModel = offerModels.get(position);
        holder.bindUI(offerModel);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offerListListener.onClickOffer(offerModel.offerCode);
            }
        });
    }

    @Override
    public int getItemCount() {
        return offerModels.size();
    }

    public static class OfferViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.offer_code_text)
        TextView offerCodeTextView;
        @Bind(R.id.offer_text)
        TextView offerTextView;

        public OfferViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindUI(OfferModel offerModel) {
            offerCodeTextView.setText(offerModel.offerCode);
            offerTextView.setText(offerModel.offerText);
        }
    }
}
