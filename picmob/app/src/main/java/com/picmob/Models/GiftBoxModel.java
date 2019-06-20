package com.picmob.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class GiftBoxModel implements Serializable {


    public String category_name,price;
    public int id = -1;
    public ArrayList<GiftBoxModel> arrayList = null;
    GiftBoxModel giftBoxModel;
    public boolean isSelected;

    public void initializeModel(JSONObject responseObject){
        arrayList = new ArrayList<>();
        try {
            JSONObject data = responseObject.getJSONObject("data");
            JSONArray product_category_array=data.getJSONArray("product_category");
            for (int i=0;i<product_category_array.length();i++){
                giftBoxModel =new GiftBoxModel();
                JSONObject dataObject = product_category_array.getJSONObject(i);
                giftBoxModel.category_name= dataObject.getString("category_name");
                giftBoxModel.price=dataObject.getString("price");
                giftBoxModel.id = dataObject.getInt("id");
                if (i==0)
                giftBoxModel.isSelected=true;
                else
                    giftBoxModel.isSelected=false;
                arrayList.add(giftBoxModel);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}