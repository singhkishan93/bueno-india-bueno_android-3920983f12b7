package com.buenodelivery.kitchen.models.core;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by navjot on 5/11/15.
 */
public class ProductModel implements Parcelable, Cloneable {

    public enum ProductStatus {
        Active("1"),
        Disable("2"),
        ComingSoon("3"),
        Unknown("-1");

        private String value;

        ProductStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @SerializedName("today_special")
    public boolean todaySpecial;
    @SerializedName("id")
    public String id;
    @SerializedName("cat_id")
    public String catId;
    @SerializedName("cat_name")
    public String catName;
    @SerializedName("ing_id")
    public String ingId;
    @SerializedName("meal_name")
    public String mealName;
    @SerializedName("serves")
    public String serves;
    @SerializedName("discounted_price")
    public String discountedPrice;
    @SerializedName("original_price")
    public String originalPrice;
    @SerializedName("image")
    public String image;
    @SerializedName("zoom_img")
    public String zoomImg;
    @SerializedName("description")
    public String description;
    @SerializedName("is_veg")
    public String isVeg;
    @SerializedName("spice_level")
    public String spiceLevel;
    @SerializedName("alias")
    public String alias;
    @SerializedName("morder")
    public String morder;
    @SerializedName("mstatus")
    public String mstatus;
    @SerializedName("created")
    public String created;
    @SerializedName("updated")
    public String updated;
    @SerializedName("strikethrough_price")
    public String strikethroughPrice;
    @SerializedName("hot_deals")
    public String hotDeals;
    @SerializedName("show_menu")
    public String showMenu;
    @SerializedName("quantity")
    public String quantity;
    @SerializedName("max_order_quantity")
    public String maxOrderQuantity;
    public int selectedQuantity;
    public boolean showMoreInfo;

    public double getSubTotalPrice() {
        try {
            double originalPrice = Double.valueOf(this.discountedPrice);
            return selectedQuantity * originalPrice;
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean isVegetarian() {
        return isVeg.equalsIgnoreCase("Vegetarian");
    }

    public void updateQuantity(int quantity) {
        this.selectedQuantity += quantity;
    }

    public void resetQuantity(int quantity) {
        this.selectedQuantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null) {
            if (o instanceof ProductModel) {
                ProductModel mProductModel = (ProductModel) o;
                return mProductModel.id.equalsIgnoreCase(this.id);
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    protected ProductModel(Parcel in) {
        id = in.readString();
        catId = in.readString();
        catName = in.readString();
        ingId = in.readString();
        mealName = in.readString();
        serves = in.readString();
        discountedPrice = in.readString();
        originalPrice = in.readString();
        image = in.readString();
        zoomImg = in.readString();
        description = in.readString();
        isVeg = in.readString();
        spiceLevel = in.readString();
        alias = in.readString();
        morder = in.readString();
        mstatus = in.readString();
        created = in.readString();
        updated = in.readString();
        strikethroughPrice = in.readString();
        hotDeals = in.readString();
        showMenu = in.readString();
        quantity = in.readString();
        maxOrderQuantity = in.readString();
        selectedQuantity = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(catId);
        dest.writeString(catName);
        dest.writeString(ingId);
        dest.writeString(mealName);
        dest.writeString(serves);
        dest.writeString(discountedPrice);
        dest.writeString(originalPrice);
        dest.writeString(image);
        dest.writeString(zoomImg);
        dest.writeString(description);
        dest.writeString(isVeg);
        dest.writeString(spiceLevel);
        dest.writeString(alias);
        dest.writeString(morder);
        dest.writeString(mstatus);
        dest.writeString(created);
        dest.writeString(updated);
        dest.writeString(strikethroughPrice);
        dest.writeString(hotDeals);
        dest.writeString(showMenu);
        dest.writeString(quantity);
        dest.writeString(maxOrderQuantity);
        dest.writeInt(selectedQuantity);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ProductModel> CREATOR = new Parcelable.Creator<ProductModel>() {
        @Override
        public ProductModel createFromParcel(Parcel in) {
            return new ProductModel(in);
        }

        @Override
        public ProductModel[] newArray(int size) {
            return new ProductModel[size];
        }
    };

    public CategoryModel getCategoryById(ArrayList<CategoryModel> categoryModels) {
        for (CategoryModel categoryModel : categoryModels) {
            if (catId.equalsIgnoreCase(categoryModel.catId)) {
                return categoryModel;
            }
        }
        return null;
    }

    public static int getQuantity(ArrayList<ProductModel> productModels) {
        if (productModels != null) {
            int quantity = 0;
            for (ProductModel productModel : productModels) {
                try {
                    quantity += productModel.selectedQuantity;
                } catch (Exception e) {
                }
            }
            return quantity;
        }
        return 0;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public ProductStatus getProductStatus() {
        if (mstatus.equalsIgnoreCase(ProductStatus.ComingSoon.getValue()))
            return ProductStatus.ComingSoon;
        else if (mstatus.equalsIgnoreCase(ProductStatus.Active.getValue())
                && Integer.valueOf(quantity) > 0)
            return ProductStatus.Active;
        else if (mstatus.equalsIgnoreCase(ProductStatus.Disable.getValue())
                || Integer.valueOf(quantity) <= 0)
            return ProductStatus.Disable;
        else return ProductStatus.Unknown;
    }
}
