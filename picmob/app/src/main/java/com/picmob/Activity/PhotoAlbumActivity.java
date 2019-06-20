package com.picmob.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.gson.JsonObject;
import com.picmob.Adapter.GiftBoxAdapter;
import com.picmob.Adapter.PhotoAlbumAdapter;
import com.picmob.Adapter.PrintFrameViewPagerAdapter;
import com.picmob.AppCustomView.Constant;
import com.picmob.AppCustomView.CustomTextViewBold;
import com.picmob.AppCustomView.CustomTextViewNormal;
import com.picmob.AppCustomView.ProgressDialog;
import com.picmob.AppCustomView.UiHelper;
import com.picmob.Models.GiftBoxModel;
import com.picmob.Models.PhotoAlbumModel;
import com.picmob.Networking.BaseApplication;
import com.picmob.Networking.NetworkController;
import com.picmob.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;

public class PhotoAlbumActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvTitle)
    CustomTextViewBold mTitle;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.txt_description)
    CustomTextViewBold txt_description;

    @BindView(R.id.txt_price)
    CustomTextViewBold txt_price;

    @BindView(R.id.txt_less)
    CustomTextViewNormal txt_less;

    @BindView(R.id.txt_max)
    CustomTextViewNormal txt_max;

    @BindView(R.id.txt_quantity)
    CustomTextViewNormal txt_quantity;

    @BindView(R.id.txt_selectAlbum)
    CustomTextViewBold txt_selectAlbum;

    @BindView(R.id.btn_addCart)
    Button btn_addCart;


    @BindView(R.id.txt_counter)
    CustomTextViewBold txt_counter;

    @BindView(R.id.Iv_cart)
    ImageView Iv_cart;

    Timer swipeTimer;

    private static ViewPager mPager;
    private static int currentPage = 0;
    int itemQuantity=1;

    PhotoAlbumModel photoAlbumModel;
    PhotoAlbumAdapter photoAlbumAdapter;
    ArrayList<PhotoAlbumModel> photoAlbumModelArrayList = new ArrayList<>();
    float original_price;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_gift_box);
        ButterKnife.bind(this);
        setActionBar();
        recyclerView.setLayoutManager(new LinearLayoutManager(PhotoAlbumActivity.this,LinearLayoutManager.HORIZONTAL,false));
       // callApiForSlider();
        callApiForCategory();

        txt_selectAlbum.setText(R.string.select_album);

        Iv_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PhotoAlbumActivity.this,YourOrderActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
            }
        });

        String cartData=BaseApplication.getInstance().getSession().getCartData();
        if (!cartData.isEmpty()){
            try {
                JSONArray jsonArray = new JSONArray(cartData);
                if (jsonArray.length()>0)
                    txt_counter.setText(String.valueOf(jsonArray.length()));
                else
                    txt_counter.setVisibility(View.INVISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else
            txt_counter.setVisibility(View.INVISIBLE);
    }

    private void setActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_icon_drawable);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mTitle.setText(R.string.photo_album);
        txt_max.setOnClickListener(this);
        txt_less.setOnClickListener(this);
        btn_addCart.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void callApiForSlider(int category_id) {
        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("product_id",getIntent().getStringExtra("product_id"));
        jsonObject.addProperty("is_frame","No");
        jsonObject.addProperty("category_id",category_id);
        int language_id=1;
        String lang=BaseApplication.getInstance().getSession().getLanguage();
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
        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().productCategorySlider(BaseApplication.getInstance().getSession().getToken(),jsonObject);
        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                if (jsonObject != null) {
                    try {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {


                            final JSONObject jsonObject2 = jsonObject1.getJSONObject("data");

                            JSONArray jsonArray = jsonObject2.getJSONArray("slider");
                            String image_url=jsonObject2.getString("image_url");
                            init(jsonArray, 0,image_url);

                            String price=jsonObject2.getString("price");
                            if (getIntent().hasExtra("edit") && photoAlbumAdapter.categoryClicked==false){

                                String pp=getIntent().getStringExtra("photoAlbum_price");
                                String[] separated = pp.split("€");
                                String qua=getIntent().getStringExtra("quantity");
                                float  total = Float.parseFloat(separated[1].trim()) * Float.parseFloat(qua);
                                original_price= Float.parseFloat(separated[1].trim());
                                itemQuantity= Integer.parseInt(qua);
                                txt_price.setText("€ "+String.valueOf(total));
                                txt_quantity.setText(getIntent().getStringExtra("quantity"));
                            }

                            else {
                                original_price= Float.parseFloat(price);
                                itemQuantity=1;
                                Log.e("TAG", "PRICE before float: "+price );
                                setPrice(Float.valueOf(price));
                            }



                        } else {
                            UiHelper.showToast(PhotoAlbumActivity.this, jsonObject1.getString("message"));
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
                UiHelper.showToast(PhotoAlbumActivity.this, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    UiHelper.showToast(PhotoAlbumActivity.this, getResources().getString(R.string.pls_check_your_internet));
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });

    }

    public void init(final JSONArray jsonArray, final int position,String url) {

        mPager = (ViewPager) this.findViewById(R.id.pager);
        mPager.setAdapter(new PrintFrameViewPagerAdapter(this, jsonArray,url));
        CircleIndicator indicator = (CircleIndicator) this.findViewById(R.id.indicator);
        indicator.setViewPager(mPager);
        mPager.setCurrentItem(position);


        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                mPager.setCurrentItem(currentPage, true);
                if (currentPage == jsonArray.length()) {
                    currentPage = 0;
                }
                else {
                    ++currentPage ;

                }

            }
        };

        swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 500, 4500);
    }


    public void callApiForCategory() {
        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("product_id",getIntent().getStringExtra("product_id"));
        int language_id=1;
        String lang=BaseApplication.getInstance().getSession().getLanguage();
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
      //  Log.e("TAG", "callApiForCategory: " +getIntent().getStringExtra("product_id"));
        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().productCategory(BaseApplication.getInstance().getSession().getToken(),jsonObject);
        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                if (jsonObject != null) {
                    try {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        final JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {

                            String product_desciption=jsonObject1.getJSONObject("data").getString("product_description");
                            txt_description.setText(product_desciption);
                            photoAlbumModel = new PhotoAlbumModel();
                            photoAlbumModel.initializeModel(jsonObject1);
                            photoAlbumModelArrayList = photoAlbumModel.arrayList;


                            if (getIntent().hasExtra("edit")){


                                for (int i=0;i<photoAlbumModelArrayList.size();i++){
                                    if (photoAlbumModelArrayList.get(i).category_name.equalsIgnoreCase(getIntent().getStringExtra("category"))){
                                        photoAlbumModelArrayList.get(i).isSelected=true;
                                    }
                                    else
                                        photoAlbumModelArrayList.get(i).isSelected=false;

                                }

                            }


                            if (getIntent().hasExtra("edit"))
                                callApiForSlider(getIntent().getIntExtra("photoAlbum_category_id",0));
                            else
                                callApiForSlider(photoAlbumModelArrayList.get(0).id);


                            photoAlbumAdapter=new PhotoAlbumAdapter(photoAlbumModelArrayList,PhotoAlbumActivity.this);
                            recyclerView.setAdapter(photoAlbumAdapter);


                        } else {
                            UiHelper.showToast(PhotoAlbumActivity.this, jsonObject1.getString("message"));
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
                UiHelper.showToast(PhotoAlbumActivity.this, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    UiHelper.showToast(PhotoAlbumActivity.this, getResources().getString(R.string.pls_check_your_internet));
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });

    }

    public void setPrice(Float price){
        txt_quantity.setText("1");
        original_price=price;
        txt_price.setText("€ "+String.format("%.2f", price));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.txt_max:
                if (itemQuantity>=1)
                    itemQuantity++;
                txt_quantity.setText(String.valueOf(itemQuantity));
                txt_price.setText("€ "+String.format("%.2f",original_price*itemQuantity));
                break;

            case R.id.txt_less:
                if (itemQuantity>1)
                    itemQuantity--;
                txt_quantity.setText(String.valueOf(itemQuantity));
                txt_price.setText("€ "+String.format("%.2f",original_price*itemQuantity));
                break;

            case R.id.btn_addCart:
                Intent intent=new Intent(this,YourOrderActivity.class);
//                intent.putExtra("isProduct","Yes");
                intent.putExtra("type","PhotoAlbum");
                intent.putExtra("quantity",txt_quantity.getText().toString());
                intent.putExtra("price","€"+String.valueOf(original_price));
                intent.putExtra("category",photoAlbumAdapter.category_name);
                intent.putExtra("photoAlbum_id",photoAlbumAdapter.photoAlbum_id);
                intent.putExtra("product_id",getIntent().getStringExtra("product_id"));
                intent.putExtra("photoAlbum_category_id",photoAlbumAdapter.photoAlbum_id);
                if (getIntent().hasExtra("edit")) {
                    intent.putExtra("editAlbum", "yes");
                    intent.putExtra("edit_photoAlbum_id",getIntent().getStringExtra("photoAlbum_id"));
                    intent.putExtra("position",getIntent().getIntExtra("position",0));
                }
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
                break;
        }
    }
}
