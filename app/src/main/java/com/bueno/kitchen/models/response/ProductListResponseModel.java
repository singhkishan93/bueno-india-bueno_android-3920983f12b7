package com.bueno.kitchen.models.response;

import com.bueno.kitchen.models.core.BaseModel;
import com.bueno.kitchen.models.core.ProductModel;

import java.util.ArrayList;

/**
 * Created by navjot on 5/11/15.
 */
public class ProductListResponseModel extends BaseModel {
    public ArrayList<ProductModel> products = new ArrayList<>();
}
