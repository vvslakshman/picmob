package com.picmob.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class OmnivaAddressModel implements Serializable {

    public String country_name="";
    public String ZIP="";
    public String id="";
    public String latitude="";
    public String longitude="";

    public boolean isChecked;
    private boolean isSelected;
    OmnivaAddressModel europeanCountryModel;
    public ArrayList<OmnivaAddressModel> europeanCountryModelArrayList = new ArrayList<>();

    public void initializeModel(JSONArray jsonArray){
        europeanCountryModelArrayList = new ArrayList<>();


        try {
         //   JSONArray dataArray = responseObject.getJSONArray("data");
            for (int i=0;i<jsonArray.length();i++){
                JSONObject dataObject = jsonArray.getJSONObject(i);
                europeanCountryModel =new OmnivaAddressModel();
                europeanCountryModel.country_name=dataObject.getString("NAME");
                europeanCountryModel.id=dataObject.getString("id");
                europeanCountryModel.ZIP=dataObject.getString("ZIP");
                europeanCountryModel.longitude=dataObject.getString("X_COORDINATE");
                europeanCountryModel.latitude=dataObject.getString("Y_COORDINATE");
                europeanCountryModel.isChecked=false;
                europeanCountryModelArrayList.add(europeanCountryModel);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    public boolean getSelected() {
        return isSelected;
    }
    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}

