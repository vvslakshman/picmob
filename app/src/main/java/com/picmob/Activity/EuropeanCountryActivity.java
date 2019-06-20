package com.picmob.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.JsonObject;
import com.picmob.Adapter.EuropeanCountryAdapter;
import com.picmob.AppCustomView.Constant;
import com.picmob.AppCustomView.ProgressDialog;
import com.picmob.AppCustomView.UiHelper;
import com.picmob.Models.EuropeanCountryModel;

import com.picmob.Networking.BaseApplication;
import com.picmob.Networking.NetworkController;
import com.picmob.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class EuropeanCountryActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.btn_ok)
    Button btn_ok;

    EuropeanCountryAdapter europeanCountryAdapter;
    EuropeanCountryModel europeanCountryModel;
    ArrayList<EuropeanCountryModel> europeanCountryModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_country_list);
        ButterKnife.bind(this);
        APiforEuropCountriesList();

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                if (europeanCountryAdapter != null) {
                    for (int i = 0; i < europeanCountryModelArrayList.size(); i++) {
                        if (europeanCountryModelArrayList.get(i).isChecked) {
                            Log.e("TAG", "onClick: " + europeanCountryModelArrayList.get(i).country_name);
                            intent.putExtra("country_name", europeanCountryModelArrayList.get(i).country_name);
                            intent.putExtra("price", europeanCountryModelArrayList.get(i).price);
                            intent.putExtra("id", europeanCountryModelArrayList.get(i).id);
                        }
                    }

                    //     intent.putExtra("country_name",europeanCountryAdapter.europeanCountryModelArrayList.get();

                    setResult(RESULT_OK, intent);
                }
                finish();
            }
        });
    }

    private void APiforEuropCountriesList() {

        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();

        JsonObject jsonObject=new JsonObject();
        int language_id=1;
        String lang= BaseApplication.getInstance().getSession().getLanguage();
        if (lang.equalsIgnoreCase("en"))
            language_id=1;
        else if (lang.equalsIgnoreCase("lv"))
            language_id=2;
        else if (lang.equalsIgnoreCase("et"))
            language_id=3;
        else if (lang.equalsIgnoreCase("lt"))
            language_id=4;
        else if (lang.equalsIgnoreCase("ru"))
            language_id=5;
        jsonObject.addProperty("language_id",language_id);

        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().europe_country_list(BaseApplication.getInstance().getSession().getToken(),String.valueOf(language_id));
        new NetworkController().get(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                if (jsonObject != null) {
                    try {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        final JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {

                            JSONObject jsonObject2=jsonObject1.getJSONObject("data");



                            JSONArray jsonArray=jsonObject2.getJSONArray("country");
                            europeanCountryModel = new EuropeanCountryModel();
                            europeanCountryModel.initializeModel(jsonObject2);
                            europeanCountryModelArrayList=europeanCountryModel.europeanCountryModelArrayList;

                            if (getIntent().hasExtra("country_id")){
                            for (int i=0;i<europeanCountryModelArrayList.size();i++){

//                                for (int j=0;j<jsonArray.length();j++){

                                    //    Log.e("both ids", "initilize: "+jsonArray.getString(j) +" list id:- "+europeanCountryModelArrayList.get(i).id);
                                        if (getIntent().getStringExtra("country_id").equalsIgnoreCase(String.valueOf(europeanCountryModelArrayList.get(i).id))) {
                                            europeanCountryModelArrayList.get(i).isChecked = true;
                                            Log.e("TAG", "NAME: "+europeanCountryModelArrayList.get(i).country_name );
                                        }



                                }
                            }


                             europeanCountryAdapter = new EuropeanCountryAdapter(europeanCountryModelArrayList, EuropeanCountryActivity.this);
                            recyclerView.setLayoutManager(new LinearLayoutManager(EuropeanCountryActivity.this));
                            recyclerView.setAdapter(europeanCountryAdapter);


                        } else {
                            UiHelper.showToast(EuropeanCountryActivity.this, jsonObject1.getString("message"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void Error(String error) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                UiHelper.showToast(EuropeanCountryActivity.this, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    UiHelper.showToast(EuropeanCountryActivity.this, "No Internet");
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });


    }


}
