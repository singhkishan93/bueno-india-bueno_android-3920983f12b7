package com.bueno.kitchen.views.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bueno.kitchen.R;
import com.bueno.kitchen.core.BuenoApplication;
import com.bueno.kitchen.models.core.CategoryModel;
import com.bueno.kitchen.models.core.ProductModel;
import com.bueno.kitchen.utils.UtilitySingleton;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by navjot on 5/11/15.
 */
public class CategoryGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_HEADER = 1;
    public static final int TYPE_CATEGORY = 2;

    private boolean isHeaderEnable;
    private final LayoutInflater layoutInflater;
    private final OnClickListener onClickListener;
    private final ArrayList<CategoryModel> categoryModels;
    private List<ProductModel> todaysSpecialList;
    private int imgSide;

    @Inject
    UtilitySingleton utilitySingleton;
    private Context context;
    private HeaderPagerAdapter headerPagerAdapter;

    public CategoryGridAdapter(Context context, OnClickListener onClickListener) {
        this.context = context;
        BuenoApplication.getApp().getActivityComponents().inject(this);
        this.onClickListener = onClickListener;
        this.categoryModels = new ArrayList<>();
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imgSide = (UtilitySingleton.getDisplayWidth(context) - utilitySingleton.dpToPx(context, 6)) / 2;
    }

    public void addCategories(ArrayList<CategoryModel> categoryModels) {
        this.categoryModels.clear();
        for (CategoryModel categoryModel : categoryModels){
            boolean anyActiveFound = false;
            for(ProductModel productModel : categoryModel.productModels){
                if(productModel.getProductStatus() == ProductModel.ProductStatus.Active){
                    anyActiveFound = true;
                }
            }
            if(anyActiveFound)
                this.categoryModels.add(categoryModel);
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_CATEGORY:
                ItemViewHolder itemViewHolder = new ItemViewHolder(layoutInflater.inflate(R.layout.cell_category, parent, false));
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) itemViewHolder.categoryImage.getLayoutParams();
                params.height = imgSide;
                return itemViewHolder;
            case TYPE_HEADER:
                return new HeaderViewHolder(layoutInflater.inflate(R.layout.layout_category_header, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            final CategoryModel categoryModel = categoryModels.get(position - (isHeaderEnable ? 1 : 0));
            itemViewHolder.bindUI(categoryModel);
            itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClickItem(categoryModel);
                }
            });
        } else if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerPagerAdapter = new HeaderPagerAdapter(context, todaysSpecialList);
            headerViewHolder.pagerMain.setAdapter(headerPagerAdapter);
            headerViewHolder.pagerIndicator.setViewPager(headerViewHolder.pagerMain);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (isHeaderEnable && position == 0) ? TYPE_HEADER : TYPE_CATEGORY;
    }

    @Override
    public int getItemCount() {
        return (isHeaderEnable ? 1 : 0) + categoryModels.size();
    }

    public void updateTodaySpecial(List<ProductModel> todaysSpecialList) {
        isHeaderEnable = !todaysSpecialList.isEmpty();
        if (isHeaderEnable) {
            this.todaysSpecialList = todaysSpecialList;
            if (headerPagerAdapter != null) headerPagerAdapter.notifyDataSetChanged();
            else notifyItemChanged(0);
        }
    }

    public interface OnClickListener {
        void onClickItem(CategoryModel categoryModel);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView categoryImage;
        private TextView nameTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            categoryImage = (ImageView) itemView.findViewById(R.id.category_image);
            nameTextView = (TextView) itemView.findViewById(R.id.category_name);
        }

        public void bindUI(final CategoryModel categoryModel) {
            nameTextView.setText(categoryModel.catName);
            Picasso.with(itemView.getContext())
                    .load(categoryModel.catImage)
                    .noFade()
                    .fit()
                    .centerCrop()
                    .into(categoryImage);
        }

    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.pager_main)
        public ViewPager pagerMain;
        @Bind(R.id.pager_indicator)
        public CirclePageIndicator pagerIndicator;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
