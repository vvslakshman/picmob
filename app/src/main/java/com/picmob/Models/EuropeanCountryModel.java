package com.picmob.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class EuropeanCountryModel implements Serializable {

  public String country_name="";
  public String price="";
  public String id="";
  public boolean isChecked;
    private boolean isSelected;
    EuropeanCountryModel europeanCountryModel;
    public ArrayList<EuropeanCountryModel> europeanCountryModelArrayList = new ArrayList<>();

    public void initializeModel(JSONObject responseObject){
        europeanCountryModelArrayList = new ArrayList<>();


        try {
            JSONArray dataArray = responseObject.getJSONArray("country");
                for (int i=0;i<dataArray.length();i++){
                JSONObject dataObject = dataArray.getJSONObject(i);
                europeanCountryModel =new EuropeanCountryModel();
                europeanCountryModel.country_name=dataObject.getString("country_name");
                europeanCountryModel.id=dataObject.getString("id");
                europeanCountryModel.price=dataObject.getString("shipping_price");
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
