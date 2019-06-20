package com.picmob.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class DeleiveryCountryModel implements Serializable {


    public String country_name,image;
    public float price;
    public int id = -1;
    public ArrayList<DeleiveryCountryModel> arrayList = null;
    DeleiveryCountryModel deleiveryCountryModel;
    public boolean isSelected;

    public void initializeModel(JSONObject responseObject){
        arrayList = new ArrayList<>();
        try {
            JSONObject data = responseObject.getJSONObject("data");
            JSONArray product_category_array=data.getJSONArray("country");
            for (int i=0;i<product_category_array.length();i++){
                deleiveryCountryModel =new DeleiveryCountryModel();
                JSONObject dataObject = product_category_array.getJSONObject(i);
                deleiveryCountryModel.country_name= dataObject.getString("country_name");
                deleiveryCountryModel.image=dataObject.getString("image");
                deleiveryCountryModel.id = dataObject.getInt("id");
                deleiveryCountryModel.price = dataObject.getInt("shipping_price");
//                if (i==0)
//                    deleiveryCountryModel.isSelected=true;
//                else
                    deleiveryCountryModel.isSelected=false;
                arrayList.add(deleiveryCountryModel);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
