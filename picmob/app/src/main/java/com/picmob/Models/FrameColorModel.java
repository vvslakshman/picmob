package com.picmob.Models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class FrameColorModel implements Serializable {

    public String product_description;
    public String color,image;
    public int colorid = -1;
    public ArrayList<FrameColorModel> arrayList = null;
    FrameColorModel frameColorModel;
    public boolean isSelected;
    public String url;
    public void initializeModel(JSONObject responseObject){
        arrayList = new ArrayList<>();


        try {

            JSONObject data = responseObject.getJSONObject("data");
            product_description=data.getString("product_description");
            url=data.getString("product_color_images_url");
            JSONArray product_colors_array=data.getJSONArray("product_colors");
            for (int i=0;i<product_colors_array.length();i++){
                frameColorModel =new FrameColorModel();
                JSONObject dataObject = product_colors_array.getJSONObject(i);
                frameColorModel.image= dataObject.getString("image");
                frameColorModel.colorid = dataObject.getInt("color_id");
                if (i==0)
                    frameColorModel.isSelected=true;
                else
                    frameColorModel.isSelected=false;
                arrayList.add(frameColorModel);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
