package com.bueno.kitchen.models.core;

import java.util.ArrayList;

/**
 * Created by navjot on 7/1/16.
 */
public class CategoryModel {
    public String catId;
    public String catName;
    public String catImage;
    public ArrayList<ProductModel> productModels;

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof CategoryModel) {
            CategoryModel categoryModelNew = (CategoryModel) o;
            return categoryModelNew.catId.equals(catId);
        }
        return false;
    }
}
