package com.picmob.Models;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Model_images {
    public ArrayList<Model_images> arrayList = null;
  public String imagePath,url;
  Model_images model_images;
    public boolean isSelected=false;



    public String getSingle_imagepath() {
        return imagePath;
    }

    public void setSingle_imagepath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
      this.isSelected = selected;
    }

    public void initializeModel(JSONArray responseObject){
        arrayList = new ArrayList<>();
        try {

            for (int i=0;i<responseObject.length();i++){
                model_images =new Model_images();
                JSONObject data = responseObject.getJSONObject(i);
                JSONObject imageObject=data.getJSONObject("images").getJSONObject("standard_resolution");

                model_images.url=imageObject.getString("url");
                model_images.isSelected=false;
                arrayList.add(model_images);
                Log.e("TAG", "Model url: "+model_images.url );
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


}