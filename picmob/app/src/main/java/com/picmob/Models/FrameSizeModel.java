package com.picmob.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class FrameSizeModel implements Serializable {


    public String height,width,price="0";
    public int sizeid = -1;
    public ArrayList<FrameSizeModel> arrayList = null;
    FrameSizeModel frameSizeModel;
    public boolean isSelected;


    public void initializeModel(JSONObject responseObject){
        arrayList = new ArrayList<>();


        try {
            JSONArray dataArray = responseObject.getJSONArray("data");
            for (int i=0;i<dataArray.length();i++){
                frameSizeModel =new FrameSizeModel();
                JSONObject dataObject = dataArray.getJSONObject(i);
                frameSizeModel.height= dataObject.getString("height");
                frameSizeModel.sizeid = dataObject.getInt("size_color_id");
                frameSizeModel.width=dataObject.getString("width");
                frameSizeModel.price=dataObject.getString("price");
                if (i==0)
                    frameSizeModel.isSelected=true;
                else
                    frameSizeModel.isSelected=false;
                arrayList.add(frameSizeModel);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
