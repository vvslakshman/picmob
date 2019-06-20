package com.picmob.Models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class PaperTypeModel implements Serializable {

   public String papername,imageView,paper_type_name,paper_type_detail;
  public   String image_url,afterselect_iamge;
    public boolean isSelectedPaper;
    public boolean isSelectedCropping;
    public int papertypeid = -1;
    public int papercroppingid = -1;
    public ArrayList<PaperTypeModel> arrayList1 = null;
    public ArrayList<PaperTypeModel> arrayList2 = null;
    PaperTypeModel paperTypeModel;


    public void initializeModel(JSONObject responseObject){
        arrayList1 = new ArrayList<>();
        arrayList2=new ArrayList<>();

        try {

            JSONObject data = responseObject.getJSONObject("data");
             image_url=data.getString("paper_cropping_url");

            JSONArray paper_type_array=data.getJSONArray("paper_type");
            for (int i=0;i<paper_type_array.length();i++){
                paperTypeModel =new PaperTypeModel();
                JSONObject dataObject = paper_type_array.getJSONObject(i);
                paperTypeModel.papername= dataObject.getString("paper_name");
                paperTypeModel.papertypeid = dataObject.getInt("id");
                paperTypeModel.isSelectedPaper=false;
                arrayList1.add(paperTypeModel);
            }

            JSONArray paper_cropping_array=data.getJSONArray("paper_cropping");
            Log.e("TAG", "initializeModel: "+paper_cropping_array.length() );
            for (int i=0;i<paper_cropping_array.length();i++){
                paperTypeModel =new PaperTypeModel();
                JSONObject dataObject = paper_cropping_array.getJSONObject(i);
                paperTypeModel.papercroppingid = dataObject.getInt("id");
                paperTypeModel.imageView=dataObject.getString("image");
                paperTypeModel.paper_type_name=dataObject.getString("cropping_name");
                paperTypeModel.paper_type_detail=dataObject.getString("description");
                paperTypeModel.afterselect_iamge=dataObject.getString("after_select_image");
                paperTypeModel.isSelectedCropping=false;
                arrayList2.add(paperTypeModel);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
