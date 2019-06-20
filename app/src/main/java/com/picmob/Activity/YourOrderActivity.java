package com.picmob.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.JsonObject;
import com.picmob.Adapter.YourOrderAdapter;
import com.picmob.Adapter.YourOrderPagerAdapter;
import com.picmob.AppCustomView.Constant;
import com.picmob.AppCustomView.CustomEditTextBold;
import com.picmob.AppCustomView.CustomTextViewBold;
import com.picmob.AppCustomView.ProgressDialog;
import com.picmob.AppCustomView.UiHelper;
import com.picmob.Networking.BaseApplication;
import com.picmob.Networking.NetworkController;
import com.picmob.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;

public class YourOrderActivity extends BaseActivity implements View.OnClickListener {
    public static final int Time = 4500;
    private static final String TAG = "YOurOrderActivity";
    private static ViewPager mPager;
    private static int currentPage = 0;
    public boolean allowClick = true;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tvTitle)
    CustomTextViewBold mTitle;
    @BindView(R.id.Iv_cart)
    ImageView Iv_cart;
    @BindView(R.id.txt_counter)
    CustomTextViewBold txt_counter;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.btn_addProduct)
    Button btn_addProduct;
    @BindView(R.id.txt_total)
    CustomTextViewBold txt_total;
    @BindView(R.id.txt_empty)
    CustomTextViewBold txt_empty;
    @BindView(R.id.btn_checkout)
    Button btn_checkout;
    @BindView(R.id.dfgh)
    LinearLayout dfgh;
    @BindView(R.id.editCode)
    CustomEditTextBold editCode;
    YourOrderAdapter yourOrderAdapter;
    String paper_cropping_type, paper_name;
    int paper_type_id, paper_cropping_id;
    int photos_quantity;
    String price, width, height, picture_size_id, discount, quantity;
    //Giftbox
    int giftbox_id, photoAlbum_id, frame_color_id, frame_size_id, giftbox_category_id, photoAlbum_category_id;
    String giftbox_category_name, giftbox_price, giftbox_quantity;
    String photoAlbum_name, photoAlbum_price, photoAlbum_quantity;
    String frame_price, frame_quantity,frame_color_image;
    String product_id;

    long click_time = 0;
    long delay = 700;

    private Timer swipTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_your_order);
        ButterKnife.bind(this);
        setActionBar();
        setAdapter();

//        getPagerImages();
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        setAdapter();
//        allowClick=true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPagerImages();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        allowClick = true;
    }

    @SuppressLint("NewApi")
    private void setAdapter() {
        //giftbox
        if (getIntent().hasExtra("type")) {
            if (getIntent().getStringExtra("type").equalsIgnoreCase("Giftbox")) {

                giftbox_category_name = getIntent().getStringExtra("category");
                giftbox_price = getIntent().getStringExtra("price");
                giftbox_quantity = getIntent().getStringExtra("quantity");
                giftbox_id = getIntent().getIntExtra("giftbox_id", 0);
                product_id = getIntent().getStringExtra("product_id");
                giftbox_category_id = getIntent().getIntExtra("giftbox_category_id", 0);
            } else if (getIntent().getStringExtra("type").equalsIgnoreCase("PhotoAlbum")) {
                photoAlbum_name = getIntent().getStringExtra("category");
                photoAlbum_price = getIntent().getStringExtra("price");
                photoAlbum_quantity = getIntent().getStringExtra("quantity");
                photoAlbum_id = getIntent().getIntExtra("photoAlbum_id", 0);
                product_id = getIntent().getStringExtra("product_id");
                photoAlbum_category_id = getIntent().getIntExtra("photoAlbum_category_id", 0);
            } else if (getIntent().getStringExtra("type").equalsIgnoreCase("photos")) {

                paper_cropping_type = getIntent().getStringExtra("paper_cropping_type");
                paper_name = getIntent().getStringExtra("paper_name");
                paper_type_id = getIntent().getIntExtra("paper_type_id", 0);
                paper_cropping_id = getIntent().getIntExtra("paper_cropping_id", 0);
                photos_quantity = getIntent().getIntExtra("photos_quantity", 0);
                price = getIntent().getStringExtra("price");
                width = getIntent().getStringExtra("width");
                height = getIntent().getStringExtra("height");
                discount = getIntent().getStringExtra("discount");
                quantity = getIntent().getStringExtra("quantity");
                Log.e(TAG, "DISCOUNT: " + getIntent().getStringExtra("discount"));
                picture_size_id = getIntent().getStringExtra("picture_size_id");
            } else if (getIntent().getStringExtra("type").equalsIgnoreCase("PrintFrame")) {

                frame_price = getIntent().getStringExtra("price");
                frame_quantity = getIntent().getStringExtra("quantity");
                width = getIntent().getStringExtra("frame_size");
                product_id = getIntent().getStringExtra("product_id");
                frame_color_image = getIntent().getStringExtra("color_image");
                frame_size_id = getIntent().getIntExtra("size_id", 0);
                frame_color_id = getIntent().getIntExtra("color_id", 0);
            }

        }
        Log.e(TAG, "price: " + price);

        JSONArray cartArray = new JSONArray();
        String cartData = BaseApplication.getInstance().getSession().getCartData();
        try {
            if (!cartData.isEmpty() && !cartData.equalsIgnoreCase(""))
                cartArray = new JSONArray(cartData);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JSONArray sessionProductArray = new JSONArray();
        String imagesData = BaseApplication.getInstance().getSession().getSelectedImages();
        try {
            if (!imagesData.isEmpty() && !imagesData.equalsIgnoreCase("")) {

                sessionProductArray = new JSONArray(imagesData.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int at = cartArray.length(); // "at" is a position where new product should be added in cartArray./


        JSONObject jsonObject = new JSONObject();
        Log.e(TAG, "cartArry length: " + cartArray.length());


        try {
            if (getIntent().hasExtra("type")) {
                if (getIntent().getStringExtra("type").equalsIgnoreCase("Giftbox")) {
                    jsonObject.put("is_product", "Yes");
                    jsonObject.put("type", "Giftbox");
                    jsonObject.put("giftbox_category", giftbox_category_name);
                    jsonObject.put("giftbox_price", giftbox_price);
                    jsonObject.put("giftbox_quantity", giftbox_quantity);
                    jsonObject.put("giftbox_id", giftbox_id);
                    jsonObject.put("product_id", product_id);
                    jsonObject.put("giftbox_category_id", giftbox_category_id);
                } else if (getIntent().getStringExtra("type").equalsIgnoreCase("PhotoAlbum")) {
                    jsonObject.put("is_product", "Yes");
                    jsonObject.put("type", "PhotoAlbum");
                    jsonObject.put("photoAlbum_category", photoAlbum_name);
                    jsonObject.put("photoAlbum_price", photoAlbum_price);
                    jsonObject.put("photoAlbum_quantity", photoAlbum_quantity);
                    jsonObject.put("photoAlbum_id", photoAlbum_id);
                    jsonObject.put("product_id", product_id);
                    jsonObject.put("photoAlbum_category_id", photoAlbum_category_id);
                } else if (getIntent().getStringExtra("type").equalsIgnoreCase("PrintFrame")) {
                    jsonObject.put("is_product", "Yes");
                    jsonObject.put("type", "PrintFrame");
                    jsonObject.put("frame_size", width);
                    jsonObject.put("frame_price", frame_price);
                    jsonObject.put("frame_quantity", frame_quantity);
                    jsonObject.put("product_id", product_id);
                    jsonObject.put("size_id", frame_size_id);
                    jsonObject.put("color_id", frame_color_id);
                    jsonObject.put("color_image", frame_color_image);
                } else if (getIntent().getStringExtra("type").equalsIgnoreCase("photos")) {
                    jsonObject.put("is_product", "No");
                    jsonObject.put("paper_name", paper_name);
                    jsonObject.put("paper_cropping_type", paper_cropping_type);
                    jsonObject.put("paper_type_id", paper_type_id);
                    jsonObject.put("paper_cropping_id", paper_cropping_id);
                    jsonObject.put("photos_quantity", photos_quantity);
                    jsonObject.put("price", price);
                    jsonObject.put("height", height);
                    jsonObject.put("width", width);
                    jsonObject.put("discount", discount);
                    jsonObject.put("quantity", quantity);
                    jsonObject.put("picture_size_id", picture_size_id);

                    if (PhotosActivity.productId != "") {
                        int index = Integer.parseInt(PhotosActivity.productId) - 1;
                        Log.e(TAG, "Product Array: " + sessionProductArray.toString());
                        JSONArray imagesArray = sessionProductArray.getJSONObject(index).getJSONArray("images_array");
                        jsonObject.put("product_id_images", sessionProductArray.getJSONObject(index).getString("id"));
                        jsonObject.put("images", imagesArray);

                        //find product if already exist
                        for (int x = 0; x < cartArray.length(); x++) {
                            //Check for same type product
                            //check for product (image) edit
                            if (cartArray.getJSONObject(x).getString("is_product").equalsIgnoreCase("no")) {
                                if (PhotosActivity.productId.equalsIgnoreCase(cartArray.getJSONObject(x).getString("product_id_images"))) {
                                    at = x;
                                    break;
                                }
                            }
                        }
                    }
                }


                //for edit product
                if (getIntent().hasExtra("editGiftbox")) {
                    for (int x = 0; x < cartArray.length(); x++) {

                        //Check for same type product
                        if (!getIntent().getStringExtra("type").equalsIgnoreCase("")) {
                            try {
                                if (cartArray.getJSONObject(x).has("giftbox_price")) {

                                    if (cartArray.getJSONObject(x).getString("giftbox_id").equalsIgnoreCase(String.valueOf(giftbox_id)) && cartArray.getJSONObject(x).getString("giftbox_category_id").equalsIgnoreCase(String.valueOf(giftbox_category_id))) {

                                        Log.e(TAG, "Same type of product in Edit if ");
                                        jsonObject = cartArray.getJSONObject(x);
                                        //  jsonObject.put("giftbox_quantity", jsonObject.getInt("giftbox_quantity") + Integer.parseInt(giftbox_quantity));


                                        Log.e(TAG, "cart Arry giftbox id: " + cartArray.getJSONObject(x).getString("giftbox_id"));
                                        Log.e(TAG, "Intent giftbox id " + String.valueOf(giftbox_id));


                                        if (cartArray.getJSONObject(x).getString("giftbox_id").equalsIgnoreCase(getIntent().getStringExtra("edit_giftboxId"))) {
                                            jsonObject.put("giftbox_quantity", giftbox_quantity);
                                            Log.e(TAG, "match giftbox id and product id ");

                                        } else {
                                            jsonObject.put("giftbox_quantity", jsonObject.getInt("giftbox_quantity") + Integer.parseInt(giftbox_quantity));
                                            Log.e(TAG, "does not match giftbox id ");
                                            int pos = getIntent().getIntExtra("position", 0);
                                            Log.e(TAG, "Position: " + pos);
                                            cartArray.remove(pos);

                                        }

                                        at = x;

                                        break;

                                    } else {
                                        Log.e(TAG, "Edited New Product ");
                                        Log.e(TAG, "else type: " + getIntent().getStringExtra("type"));
                                        Log.e(TAG, "setAdapter: " + giftbox_category_name);
                                        //       if (cartArray.getJSONObject(x).optString("giftbox_category").equalsIgnoreCase(giftbox_category_name)) {
                                        jsonObject = cartArray.getJSONObject(x);
                                        jsonObject.put("giftbox_quantity", Integer.parseInt(giftbox_quantity));
                                        jsonObject.put("is_product", "Yes");
                                        jsonObject.put("type", "Giftbox");
                                        jsonObject.put("giftbox_category", giftbox_category_name);
                                        jsonObject.put("giftbox_price", giftbox_price);
                                        jsonObject.put("giftbox_id", giftbox_id);
                                        jsonObject.put("product_id", product_id);
                                        jsonObject.put("giftbox_category_id", giftbox_category_id);
                                        at = x;
                                        break;
                                    }
                                    // }
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                } else if (getIntent().hasExtra("editAlbum")) {
                    for (int x = 0; x < cartArray.length(); x++) {

                        //Check for same type product
                        if (!getIntent().getStringExtra("type").equalsIgnoreCase("")) {
                            try {
                                if (cartArray.getJSONObject(x).has("photoAlbum_price")) {
                                    Log.e(TAG, "setAdap Photoname: " + photoAlbum_name);

                                    if (cartArray.getJSONObject(x).getString("photoAlbum_id").equalsIgnoreCase(String.valueOf(photoAlbum_id)) && cartArray.getJSONObject(x).getString("photoAlbum_category_id").equalsIgnoreCase(String.valueOf(photoAlbum_category_id))) {

                                        Log.e(TAG, "Same type of product in Edit if ");
                                        jsonObject = cartArray.getJSONObject(x);
                                        //           jsonObject.put("photoAlbum_quantity", jsonObject.getInt("photoAlbum_quantity") + Integer.parseInt(photoAlbum_quantity));


                                        Log.e(TAG, "cart Arry giftbox id: " + cartArray.getJSONObject(x).getString("photoAlbum_id"));
                                        Log.e(TAG, "Intent giftbox id " + String.valueOf(photoAlbum_id));


                                        if (cartArray.getJSONObject(x).getString("photoAlbum_id").equalsIgnoreCase(getIntent().getStringExtra("edit_photoAlbum_id"))) {
                                            Log.e(TAG, "match photoAlbum_id id  ");
                                            jsonObject.put("photoAlbum_quantity", photoAlbum_quantity);

                                        } else {
                                            jsonObject.put("photoAlbum_quantity", jsonObject.getInt("photoAlbum_quantity") + Integer.parseInt(photoAlbum_quantity));
                                            Log.e(TAG, "does not match photoAlbum id ");
                                            int pos = getIntent().getIntExtra("position", 0);
                                            Log.e(TAG, "Position: " + pos);
                                            cartArray.remove(pos);

                                        }

                                        at = x;

                                        break;

                                    } else {
                                        jsonObject = cartArray.getJSONObject(x);
                                        jsonObject.put("photoAlbum_quantity", Integer.parseInt(photoAlbum_quantity));
                                        jsonObject.put("is_product", "Yes");
                                        jsonObject.put("type", "PhotoAlbum");
                                        jsonObject.put("photoAlbum_category", photoAlbum_name);
                                        jsonObject.put("photoAlbum_price", photoAlbum_price);
                                        jsonObject.put("photoAlbum_id", photoAlbum_id);
                                        jsonObject.put("product_id", product_id);
                                        jsonObject.put("photoAlbum_category_id", photoAlbum_category_id);
                                        at = x;
                                        break;
                                    }
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                } else if (getIntent().hasExtra("editFrame")) {
                    for (int x = 0; x < cartArray.length(); x++) {

                        //Check for same type product
                        try {
                            if (cartArray.getJSONObject(x).has("frame_price")) {

                                if (cartArray.getJSONObject(x).optString("size_id").equalsIgnoreCase(String.valueOf(frame_size_id))) {

                                    if (cartArray.getJSONObject(x).optString("color_id").equalsIgnoreCase(String.valueOf(frame_color_id))) {
                                        jsonObject = cartArray.getJSONObject(x);
                                        jsonObject.put("frame_quantity", Integer.parseInt(frame_quantity));

//                                        if (cartArray.getJSONObject(x).getString("size_id").equalsIgnoreCase(String.valueOf(frame_size_id)) && cartArray.getJSONObject(x).getString("color_id").equalsIgnoreCase(String.valueOf(frame_color_id))){
//
//                                            Log.e(TAG, "match Frame id  ");
//                                            cartArray.remove(x);
//
//                                        }
                                        at = x;
                                        break;
                                    }

                                } else {
                                    jsonObject = cartArray.getJSONObject(x);
                                    jsonObject.put("frame_quantity", Integer.parseInt(frame_quantity));
                                    jsonObject.put("is_product", "Yes");
                                    jsonObject.put("type", "PrintFrame");
                                    jsonObject.put("frame_size", width);
                                    jsonObject.put("frame_price", frame_price);
                                    jsonObject.put("product_id", product_id);
                                    jsonObject.put("size_id", frame_size_id);
                                    jsonObject.put("color_id", frame_color_id);
                                    jsonObject.put("color_image", frame_color_image);
                                    at = x;
                                    break;
                                }
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                } else {
                    for (int x = 0; x < cartArray.length(); x++) {

                        //Check for same type product
                        if (!getIntent().getStringExtra("type").equalsIgnoreCase("")) {
                            if (cartArray.getJSONObject(x).optString("type").equalsIgnoreCase(getIntent().getStringExtra("type"))) {
                                Log.e(TAG, "else type: " + getIntent().getStringExtra("type"));
                                Log.e(TAG, "setAdapter: " + giftbox_category_name);
                                if (cartArray.getJSONObject(x).optString("giftbox_category").equalsIgnoreCase(giftbox_category_name)) {
                                    jsonObject = cartArray.getJSONObject(x);

                                    jsonObject.put("giftbox_quantity", jsonObject.getInt("giftbox_quantity") + Integer.parseInt(giftbox_quantity));
                                    at = x;
                                    break;
                                } else if (cartArray.getJSONObject(x).optString("photoAlbum_category").equalsIgnoreCase(photoAlbum_name)) {
                                    Log.e(TAG, "setAdap Photoname: " + photoAlbum_name);
                                    jsonObject = cartArray.getJSONObject(x);
                                    jsonObject.put("photoAlbum_quantity", jsonObject.getInt("photoAlbum_quantity") + Integer.parseInt(photoAlbum_quantity));
                                    at = x;
                                    break;
                                } else if (cartArray.getJSONObject(x).optString("size_id").equalsIgnoreCase(String.valueOf(frame_size_id))) {

                                    if (cartArray.getJSONObject(x).optString("color_id").equalsIgnoreCase(String.valueOf(frame_color_id))) {
                                        jsonObject = cartArray.getJSONObject(x);
                                        jsonObject.put("frame_quantity", jsonObject.getInt("frame_quantity") + Integer.parseInt(frame_quantity));
                                        at = x;
                                        break;
                                    }
                                }

                            }
                        }
                    }

                }


                cartArray.put(at, jsonObject);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        BaseApplication.getInstance().getSession().setCartData(cartArray.toString());
        Log.e(TAG, "setAdapter: " + cartArray.toString());
        if (cartArray.length() == 0)
            cartEmpty();
        Log.e(TAG, "setAdapter Images Array: " + sessionProductArray.toString());
        yourOrderAdapter = new YourOrderAdapter(this, cartArray);
        recyclerView.setAdapter(yourOrderAdapter);
        yourOrderAdapter.notifyDataSetChanged();


    }

    public void getPagerImages() {
        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();
       // String jsonObject = new JsonObject();
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
        //jsonObject.addProperty("language_id", language_id);

        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().order_screen_slider(language_id);
        new NetworkController().get(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                if (jsonObject != null) {
                    try {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {

                            Log.e(TAG, "Success: ");
                            final JSONObject jsonObject2 = jsonObject1.getJSONObject("data");

                          //  JSONArray jsonArray = jsonObject2.getJSONArray("slider");
                            String slider_url = jsonObject2.getString("slider_url");
                            init( 0, slider_url);


                        } else {
                            UiHelper.showToast(YourOrderActivity.this, jsonObject1.getString("message"));
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
                UiHelper.showToast(YourOrderActivity.this, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    UiHelper.showToast(YourOrderActivity.this, getResources().getString(R.string.pls_check_your_internet));
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });

    }

    public void init(final int position, String url) {
        Log.e(TAG, "init: " + position);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new YourOrderPagerAdapter(this, url));
       // CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
      //  indicator.setViewPager(mPager);
        mPager.setCurrentItem(position);


        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                mPager.setCurrentItem(currentPage, true);
//                if (currentPage == jsonArray.length()) {
//                    currentPage = 0;
//                } else {
//                    ++currentPage;
//
//                }

            }
        };

        swipTimer = new Timer();
        swipTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 500, 4500);
    }

    @Override
    public void onPause() {
        //  swipTimer.cancel();
        //  swipTimer.purge();
        super.onPause();

    }

    private void setActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_icon_drawable);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mTitle.setText(R.string.your_order);
        Iv_cart.setVisibility(View.GONE);
        txt_counter.setVisibility(View.GONE);
        btn_addProduct.setOnClickListener(this);
        btn_checkout.setOnClickListener(this);
        editCode.setOnClickListener(this);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_addProduct:
                if (SystemClock.elapsedRealtime() - click_time < delay)
                    return;
                click_time = SystemClock.elapsedRealtime();
                Intent intent = new Intent();
                if (BaseApplication.getInstance().getSession().isLoggedIn())
                    intent = new Intent(this, MainActivity.class);
                else
                    intent = new Intent(this, DefaultActivity.class);
                intent.putExtra("newProduct", "yes");
                PhotosActivity.productId = "";
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                break;

            case R.id.btn_checkout:
                if (SystemClock.elapsedRealtime() - click_time < delay)
                    return;
                click_time = SystemClock.elapsedRealtime();
                allowClick = false;
                Intent intent1 = new Intent();
                String cartData = BaseApplication.getInstance().getSession().getCartData();
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(cartData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //  if (jsonArray.length()==0)
                //    btn_checkout.setEnabled(false);

//                  if (!BaseApplication.getInstance().getSession().isLoggedIn()) {
//                     intent1 = new Intent(this, GuestUserActivity.class);
//                     intent1.putExtra("total_amount",txt_total.getText().toString());
//                    startActivity(intent1);
//                    overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
//                }
                //    else{
                try {
                    if (!cartData.isEmpty() && !cartData.equalsIgnoreCase("")) {


                        for (int i = 0; i < jsonArray.length(); i++) {
//                            jsonArray.getJSONObject(i).getJSONArray("images").length()==0 &&
                            if (jsonArray.getJSONObject(i).has("images")) {
                                intent1 = new Intent(this, UploadImage.class);
//                                        intent1.putExtra("width",width);
//                                        intent1.putExtra("height",height);
                                break;
                            } else
                                intent1 = new Intent(this, DeliveryOptionActivity.class);
                        }
                        intent1.putExtra("total_amount", txt_total.getText().toString());
                        Log.e(TAG, "onClick: " + txt_total.getText().toString());

                        startActivity(intent1);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


//                    String imagesData=BaseApplication.getInstance().getSession().getSelectedImages();
//
//                        if (!imagesData.isEmpty() && !imagesData.equalsIgnoreCase(""))
//                            intent1=new Intent(this,UploadImage.class);
//                       else
//                            intent1=new Intent(this,DeleiveryActivity.class);


                //   }
                break;

            case R.id.editCode:
                showCodeDialog();
                break;
        }
    }

    private void showCodeDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        // Include dialog.xml file
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog.setContentView(R.layout.dialog_code);
        dialog.show();


        Button btn_apply = (Button) dialog.findViewById(R.id.btn_ok);
    }

    public void setTotalPrice(float price) {
        //  txt_total.setText("€"+String.valueOf(price));
        txt_total.setText("€ " + String.format(Locale.ENGLISH, "%.2f", price));
    }

    public void cartEmpty() {
        btn_checkout.setEnabled(false);
        txt_empty.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        dfgh.setVisibility(View.GONE);
        editCode.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {

        PhotosActivity.productId = "";
        Intent intent = new Intent();
        if (BaseApplication.getInstance().getSession().isLoggedIn()) {
            intent = new Intent(this, MainActivity.class);
        } else
            intent = new Intent(this, DefaultActivity.class);
        intent.putExtra("newProduct", "yes");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        super.onBackPressed();
    }
}
