package com.picmob.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.gson.JsonObject;
import com.picmob.Adapter.PaperCroppingAdapter;
import com.picmob.Adapter.PaperTypeAdapter;
import com.picmob.AppCustomView.Constant;
import com.picmob.AppCustomView.CustomTextViewBold;
import com.picmob.AppCustomView.ProgressDialog;
import com.picmob.AppCustomView.UiHelper;
import com.picmob.Models.PaperTypeModel;
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

public class SelectParametersActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SelectParameters";
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvTitle)
    CustomTextViewBold mTitle;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    @BindView(R.id.recyclerviewcropping)
    RecyclerView recyclerviewcropping;

    @BindView(R.id.btn_next)
    Button btn_next;

    @BindView(R.id.btn_addPhotos)
    Button btn_addPhotos;

    @BindView(R.id.txt_counter)
    CustomTextViewBold txt_counter;

    @BindView(R.id.Iv_cart)
    ImageView Iv_cart;

    PaperTypeAdapter paperTypeAdapter;
    PaperTypeModel paperTypeModel;
    PaperCroppingAdapter paperCroppingAdapter;

    ArrayList<PaperTypeModel> paperTypeModelArrayList1 = new ArrayList<>();
    ArrayList<PaperTypeModel> paperTypeModelArrayList2 = new ArrayList<>();

    ArrayList<String> arrayListForSelectedImages;
    String paper_name = "";
    int paper_id = -1;
    int paper_cropping_id = -1;
    String paper_cropping_type = "";

    long click_time = 0;
    long delay = 700;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_select_parameters);
        ButterKnife.bind(this);
        setActionBar();

        recyclerview.setLayoutManager(new LinearLayoutManager(SelectParametersActivity.this, LinearLayoutManager.HORIZONTAL, false));
        recyclerviewcropping.setLayoutManager(new LinearLayoutManager(SelectParametersActivity.this));
        callApi();

        if (getIntent().getBundleExtra("BUNDLE") != null && !getIntent().getBundleExtra("BUNDLE").isEmpty()) {
            Bundle extras = getIntent().getBundleExtra("BUNDLE");
            arrayListForSelectedImages = (ArrayList<String>) extras.getSerializable("ARRAYLIST");
            Log.e(TAG, "onCreate: " + arrayListForSelectedImages.toString());
        }

        Iv_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectParametersActivity.this, YourOrderActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });


        String cartData = BaseApplication.getInstance().getSession().getCartData();
        if (!cartData.isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(cartData);
                if (jsonArray.length() > 0)
                    txt_counter.setText(String.valueOf(jsonArray.length()));
                else
                    txt_counter.setVisibility(View.INVISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else
            txt_counter.setVisibility(View.INVISIBLE);
    }

    private void setActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_icon_drawable);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mTitle.setText(R.string.select_parameters);
        btn_next.setOnClickListener(this);
        btn_addPhotos.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    public void callApi() {
        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();
        JsonObject jsonObject = new JsonObject();
        int language_id = 1;
        String lang = BaseApplication.getInstance().getSession().getLanguage();
        if (lang.equalsIgnoreCase("en"))
            language_id = 1;
        else if (lang.equalsIgnoreCase("lv"))
            language_id = 2;
        else if (lang.equalsIgnoreCase("et"))
            language_id = 3;
        else if (lang.equalsIgnoreCase("lt"))
            language_id = 4;
        else if (lang.equalsIgnoreCase("ru"))
            language_id = 5;
        jsonObject.addProperty("language_id", language_id);
        Log.e("TAG", "callApi: " + BaseApplication.getInstance().getSession().getToken());
        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().papertype(BaseApplication.getInstance().getSession().getToken(), String.valueOf(language_id));
        new NetworkController().get(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                if (jsonObject != null) {
                    try {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        final JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {
                            Log.e("TAG", "Success: "+jsonObject);

                            String data = jsonObject1.getJSONObject("data").getString("paper_cropping_url");
                            paperTypeModel = new PaperTypeModel();
                            paperTypeModel.initializeModel(jsonObject1);
                            paperTypeModelArrayList1 = paperTypeModel.arrayList1;
                            paperTypeModelArrayList2 = paperTypeModel.arrayList2;

                            paperTypeAdapter = new PaperTypeAdapter(paperTypeModelArrayList1, SelectParametersActivity.this);
                            recyclerview.setAdapter(paperTypeAdapter);

                            paperCroppingAdapter = new PaperCroppingAdapter(paperTypeModelArrayList2, SelectParametersActivity.this, data);
                            recyclerviewcropping.setAdapter(paperCroppingAdapter);
                        } else {
                            UiHelper.showToast(SelectParametersActivity.this, jsonObject1.getString("message"));
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
                UiHelper.showToast(SelectParametersActivity.this, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    UiHelper.showToast(SelectParametersActivity.this, getResources().getString(R.string.pls_check_your_internet));
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                if (SystemClock.elapsedRealtime() - click_time < delay)
                    return;
                click_time = SystemClock.elapsedRealtime();
                Intent intent = new Intent(this, PhotosOverviewActivity.class);

//                Bundle args = new Bundle();
//                args.putSerializable("ARRAYLIST", (Serializable) arrayListForSelectedImages);
//                intent.putExtra("BUNDLE", args);

                if (paperTypeAdapter != null) {
                    paper_name = paperTypeAdapter.paper_name;
                    paper_id = paperTypeAdapter.paper_type_id;
                }
                if (paperCroppingAdapter != null) {
                    paper_cropping_type = paperCroppingAdapter.paper_cropping;
                    paper_cropping_id = paperCroppingAdapter.paper_cropping_id;
                }


                if (paper_name == null || paper_name.isEmpty())
                    UiHelper.showToast(this, getString(R.string.pls_select_any_one_paper));
                if (paper_cropping_type == null || paper_cropping_type.isEmpty())
                    UiHelper.showToast(this, getString(R.string.pls_select_any_one_cropping));

                if (paper_name != null && !paper_name.isEmpty() && paper_cropping_type != null && !paper_cropping_type.isEmpty()) {
                    intent.putExtra("paper_name", paper_name);
                    intent.putExtra("paper_cropping_type", paper_cropping_type);
                    intent.putExtra("paper_type_id", paper_id);

                    Log.e(TAG, "paper_cropping_id"+paper_cropping_id );

                    intent.putExtra("paper_cropping_id", String.valueOf(paper_cropping_id));
                    intent.putExtra("price", getIntent().getStringExtra("price"));
                    intent.putExtra("width", getIntent().getStringExtra("width"));
                    intent.putExtra("height", getIntent().getStringExtra("height"));
                    intent.putExtra("discount", getIntent().getStringExtra("discount"));
                    intent.putExtra("quantity", getIntent().getStringExtra("quantity"));
                    intent.putExtra("picture_size_id", getIntent().getStringExtra("picture_size_id"));
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    Log.e(TAG, "intent papername: " + paper_name + " cropping:" + paper_cropping_type);
                }
//                else if (paper_name.equalsIgnoreCase("")){
//
//                    UiHelper.showToast(this,"Please Select any one Paper");
//                }
//                else if (paper_cropping_type.equalsIgnoreCase("")){
//
//                    UiHelper.showToast(this, "Please Select any one Cropping");
//                }

                break;


            case R.id.btn_addPhotos:
                if (SystemClock.elapsedRealtime() - click_time < delay)
                    return;
                click_time = SystemClock.elapsedRealtime();
                Intent intent1 = new Intent(this, Selected_Photos.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent1.putExtra("price", getIntent().getStringExtra("price"));
                intent1.putExtra("width", getIntent().getStringExtra("width"));
                intent1.putExtra("height", getIntent().getStringExtra("height"));
                intent1.putExtra("discount", getIntent().getStringExtra("discount"));
                intent1.putExtra("quantity", getIntent().getStringExtra("quantity"));
                intent1.putExtra("picture_size_id", getIntent().getStringExtra("picture_size_id"));
                startActivity(intent1);
                break;
        }
    }

}