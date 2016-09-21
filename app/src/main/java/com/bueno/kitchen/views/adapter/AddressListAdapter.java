package com.bueno.kitchen.views.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bueno.kitchen.R;
import com.bueno.kitchen.models.core.AddressModel;

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

    public AddressListAdapter(Context context, OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        this.addressModels = new ArrayList<>();
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            final AddressModel addressModel = addressModels.get(position);
            AddressViewHolder addressViewHolder = (AddressViewHolder) holder;
            addressViewHolder.bindUI(addressModel);
            addressViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.OnPickAddress(addressModel);
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
