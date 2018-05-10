package com.buenodelivery.kitchen.views.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.buenodelivery.kitchen.R;
import com.buenodelivery.kitchen.Required_beans;
import com.buenodelivery.kitchen.activities.CheckoutActivity;
import com.buenodelivery.kitchen.models.core.AddressModel;
import com.buenodelivery.kitchen.utils.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by navjot on 5/11/15.
 */

public class AddressListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int ITEM_VIEW_TYPE = 101;
    private final static int FOOTER_VIEW_TYPE = 102;

    private final LayoutInflater layoutInflater;
    private final OnClickListener onClickListener;
    private ArrayList<AddressModel> addressModels;
    AddressModel addressModel;
    Context mcontext;

    Required_beans req_beans = new Required_beans();

    public AddressListAdapter(Context context, OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        this.addressModels = new ArrayList<>();
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mcontext=context;
    }

    public void addAddress(List<AddressModel> addressModels) {
        this.addressModels.clear();
        this.addressModels.addAll(addressModels);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_VIEW_TYPE:
                return new AddressViewHolder(layoutInflater.inflate(R.layout.cell_address, parent, false));
            case FOOTER_VIEW_TYPE:
                return new RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.cell_add_address, parent, false)) {
                };
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AddressViewHolder) {
            addressModel = addressModels.get(position);
            AddressViewHolder addressViewHolder = (AddressViewHolder) holder;
            addressViewHolder.bindUI(addressModel);
            addressViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                //public Context mcontext;


                @Override
                public void onClick(View view) {
                    onClickListener.OnPickAddress(addressModel);
                    //Intent itc = new Intent(req_beans.getCurrent_con(), CheckoutActivity.class);
                    Intent itc = new Intent(req_beans.getCurrent_con(), CheckoutActivity.class);
                    itc.putExtra(Config.Properties.PROPERTY_ADDRESS, addressModel);
                    onClickListener.VerifyOrderMode(itc);

                    //(RecyclerViewActivity)mContext).onClickChangeActivity();
                }
            });
            addressViewHolder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.OnEditAddress(addressModel);
                }
            });
        } else {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.OnClickAddAddress();
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position >= addressModels.size() ? FOOTER_VIEW_TYPE : ITEM_VIEW_TYPE;
    }

    @Override
    public int getItemCount() {
        return addressModels.size() + 1;
    }

    public interface OnClickListener {
        void OnPickAddress(AddressModel addressModel);
        void OnEditAddress(AddressModel addressModel);
        void VerifyOrderMode(Intent it);
        void OnClickAddAddress();
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView;
        private TextView addressTextView;
        private View editButton;

        public AddressViewHolder(View itemView) {
            super(itemView);
            init();
        }

        private void init() {
            nameTextView = (TextView) itemView.findViewById(R.id.name_text);
            addressTextView = (TextView) itemView.findViewById(R.id.address_text);
            editButton = itemView.findViewById(R.id.edit_address_button);
            editButton.setVisibility(View.VISIBLE);
        }

        public void bindUI(final AddressModel addressModel) {
            nameTextView.setText(addressModel.name);
            addressTextView.setText(addressModel.address);
        }
    }
}
