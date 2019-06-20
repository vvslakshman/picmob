package com.picmob.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class PhotoAlbumModel implements Serializable {


    public String category_name,price;
    public int id = -1;
    public ArrayList<PhotoAlbumModel> arrayList = null;
    PhotoAlbumModel photoAlbumModel;
    public boolean isSelected;

    public void initializeModel(JSONObject responseObject){
        arrayList = new ArrayList<>();


        try {

            JSONObject data = responseObject.getJSONObject("data");

            JSONArray product_category_array=data.getJSONArray("product_category");
            for (int i=0;i<product_category_array.length();i++){
                photoAlbumModel =new PhotoAlbumModel();
                JSONObject dataObject = product_category_array.getJSONObject(i);
                photoAlbumModel.category_name= dataObject.getString("category_name");
                photoAlbumModel.price=dataObject.getString("price");
                photoAlbumModel.id = dataObject.getInt("id");
                if (i==0)
                    photoAlbumModel.isSelected=true;
                else
                    photoAlbumModel.isSelected=false;
                arrayList.add(photoAlbumModel);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}