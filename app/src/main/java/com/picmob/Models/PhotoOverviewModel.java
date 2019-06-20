package com.picmob.Models;



import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.Serializable;
import java.util.ArrayList;

public class PhotoOverviewModel implements Serializable {


    public String quantity="1";
    public String image="";
    PhotoOverviewModel photoOverviewModel;
    public ArrayList<PhotoOverviewModel> photoOverviewModelArrayList = null;


    public void initializeModel(JSONArray arrayList){

        photoOverviewModelArrayList = new ArrayList<>();
        Log.e("TAG", "initializeModel: "+arrayList );

        for (int i=0;i<arrayList.length();i++) {
            photoOverviewModel = new PhotoOverviewModel();
            try {
                photoOverviewModel.image = String.valueOf(arrayList.getJSONObject(i).getString("image"));

                if (arrayList.getJSONObject(i).has("quantity"))
            photoOverviewModel.quantity =arrayList.getJSONObject(i).getString("quantity");
                else
                    photoOverviewModel.quantity=quantity;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            photoOverviewModelArrayList.add(photoOverviewModel);
        }

    }
}
