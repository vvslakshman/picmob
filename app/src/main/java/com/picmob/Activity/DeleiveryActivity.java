package com.picmob.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.PayPalAccountNonce;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.braintreepayments.api.models.PostalAddress;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.picmob.Adapter.DeleiveryCountryAdapter;
import com.picmob.AppCustomView.Constant;
import com.picmob.AppCustomView.CustomTextViewBold;
import com.picmob.AppCustomView.CustomTextViewNormal;
import com.picmob.AppCustomView.GPSTracker;
import com.picmob.AppCustomView.ProgressDialog;
import com.picmob.AppCustomView.UiHelper;
import com.picmob.Models.DeleiveryCountryModel;
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

public class DeleiveryActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "DeliveryActivity";
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvTitle)
    CustomTextViewBold mTitle;

    @BindView(R.id.Iv_cart)
    ImageView Iv_cart;

    @BindView(R.id.txt_counter)
    CustomTextViewBold txt_counter;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.txt_omnivaBox)
    CustomTextViewBold txt_omnivaBox;

    @BindView(R.id.txt_omnivaCourier)
    CustomTextViewBold txt_omnivaCourier;

    @BindView(R.id.txt_omnivaBox_gray)
    CustomTextViewBold txt_omnivaBox_gray;

    @BindView(R.id.txt_omnivaCourier_pink)
    CustomTextViewBold txt_omnivaCourier_pink;

    @BindView(R.id.lyt_country)
    LinearLayout lyt_country;

    @BindView(R.id.btn_next)
    Button btn_next;

    @BindView(R.id.editFirstname)
    EditText editFirstname;

    @BindView(R.id.editLastname)
    EditText editLastname;


    @BindView(R.id.editPhone)
    EditText editPhone;

    @BindView(R.id.editEmail)
    EditText editEmail;

    @BindView(R.id.editCountry)
    EditText editCountry;

    @BindView(R.id.editAddress)
    EditText editAddress;

    @BindView(R.id.editAddressOmniva)
    EditText editAddressOmniva;

    @BindView(R.id.editCity)
    EditText editCity;

    @BindView(R.id.editPostCode)
    EditText editPostCode;

    @BindView(R.id.txt_price)
    CustomTextViewNormal txt_price;

    @BindView(R.id.txt_total)
    CustomTextViewNormal txt_total;

    @BindView(R.id.lyt_entercountry)
    LinearLayout lyt_entercountry;

    @BindView(R.id.lyt_city)
    LinearLayout lyt_city;

    @BindView(R.id.lyt_address)
    LinearLayout lyt_address;

    @BindView(R.id.lyt_addressOmniva)
    LinearLayout lyt_addressOmniva;

    @BindView(R.id.lyt_postcode)
    LinearLayout lyt_postcode;


    ArrayList<DeleiveryCountryModel> deleiveryCountryModelArrayList = new ArrayList<>();
    DeleiveryCountryModel deleiveryCountryModel;
    DeleiveryCountryAdapter deleiveryCountryAdapter;

    JSONArray jsonArray = new JSONArray();
    ArrayList<String> permissionToAsk = new ArrayList<>();
    String delivery_type = "Omniva Parcel Machine";
    String paper_type_id, paper_cropping_id, picture_size_id, image_ids, isProduct, discount;
    String type, giftbox_Id, giftbox_Qty, album_id, album_qty, product_id, frame_size_id, frame_color_id, print_frame_qty;
    String checked = "Card";
    JsonArray cart_data = new JsonArray();
    ProgressDialog progressDialog;
    String country_id, omniva_country_id, omniva_latitude, omniva_longitude;
    String token;
    float shipping_price;
    String delivery_country_name;
    private Double latitude;
    private Double longitude;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_deleivery);
        ButterKnife.bind(this);

        if (getIntent().hasExtra("images")) {

            Log.e(TAG, "onCreate: " + getIntent().getStringExtra("images"));
        }

        setActionBar();
        recyclerView.setLayoutManager(new LinearLayoutManager(DeleiveryActivity.this, LinearLayoutManager.HORIZONTAL, false));

        setData();
        progressDialog = UiHelper.generateProgressDialog(DeleiveryActivity.this, false);
        //   txt_total.setText(" "+getIntent().getStringExtra("total_amount"));
        if (getIntent().getStringExtra("option").equalsIgnoreCase("Parcel")) {
            callApiForCountryList();
            delivery_type = "Omniva Parcel Machine";
            mTitle.setText(R.string.omniva_parcel_machine);
            lyt_country.setVisibility(View.VISIBLE);
            lyt_entercountry.setVisibility(View.GONE);
            lyt_address.setVisibility(View.GONE);
            lyt_addressOmniva.setVisibility(View.VISIBLE);
            lyt_postcode.setVisibility(View.GONE);
            lyt_city.setVisibility(View.GONE);
        } else if (getIntent().getStringExtra("option").equalsIgnoreCase("Courier")) {
            callApiForCountryList();
            delivery_type = "Omniva Courier";
            mTitle.setText(R.string.omniva_courier);
            lyt_country.setVisibility(View.VISIBLE);
            lyt_entercountry.setVisibility(View.GONE);
        } else if (getIntent().getStringExtra("option").equalsIgnoreCase("Pasts")) {
            delivery_type = "Postal Services";
            mTitle.setText(R.string.postal_services_latvija);
            lyt_country.setVisibility(View.GONE);
            txt_price.setText("€" + " " + String.valueOf(shipping_price));
            txt_total.setText(getIntent().getStringExtra("total_amount"));

        }
//        String pp = getIntent().getStringExtra("total_amount");
//        String[] disss = pp.split("\\€");
//        float current_price = Float.parseFloat(disss[1]);
//        txt_total.setText(String.valueOf(current_price));


    }


    private void setActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_icon_drawable);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        Iv_cart.setVisibility(View.GONE);
        txt_counter.setVisibility(View.GONE);

        txt_omnivaBox_gray.setOnClickListener(this);
        txt_omnivaCourier.setOnClickListener(this);
        editCountry.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        editAddressOmniva.setOnClickListener(this);
        setCartData();
    }


    private void setData() {
        try {
            if (BaseApplication.getInstance().getSession().getProfileData() != null && !BaseApplication.getInstance().getSession().getProfileData().equalsIgnoreCase("")) {
                JSONObject jsonObject = new JSONObject(BaseApplication.getInstance().getSession().getProfileData());
                if (!jsonObject.optString("first_name").equalsIgnoreCase("") && jsonObject.has("first_name"))
                    editFirstname.setText(jsonObject.getString("first_name"));

                if (!jsonObject.optString("last_name").equalsIgnoreCase("") && jsonObject.has("last_name"))
                    editLastname.setText(jsonObject.getString("last_name"));

                if (!jsonObject.optString("email").equalsIgnoreCase("") && jsonObject.has("email"))
                    editEmail.setText(jsonObject.getString("email"));

                if (!jsonObject.optString("contact_number").equalsIgnoreCase("") && jsonObject.has("contact_number"))
                    editPhone.setText(jsonObject.getString("contact_number"));

                if (!jsonObject.optString("address").equalsIgnoreCase("") && jsonObject.has("address"))
                    editAddress.setText(jsonObject.getString("address"));

                if (!jsonObject.optString("city").equalsIgnoreCase("") && jsonObject.has("city"))
                    editCity.setText(jsonObject.getString("city"));

                if (!jsonObject.optString("country").equalsIgnoreCase("") && jsonObject.has("country")) {
                    editCountry.setText(jsonObject.getString("country"));
                    country_id = jsonObject.getString("country_id");
                    delivery_country_name = jsonObject.getString("country");
                    Log.e(TAG, "setData: " + delivery_country_name);
                }

                if (!jsonObject.optString("post_code").equalsIgnoreCase("") && jsonObject.has("post_code"))
                    editPostCode.setText(jsonObject.getString("post_code"));


                if (!jsonObject.optString("shipping_price").equalsIgnoreCase("") && jsonObject.has("shipping_price"))
                    shipping_price = jsonObject.getInt("shipping_price");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void setCartData() {


        String cartDataSesion = BaseApplication.getInstance().getSession().getCartData();
        try {
            jsonArray = new JSONArray(cartDataSesion);
            Log.e("TAG", "jsonArray sesion: " + jsonArray.toString());


            for (int i = 0; i < jsonArray.length(); i++) {

                Log.e(TAG, "length: " + jsonArray.length());
                if (jsonArray.getJSONObject(i).getString("is_product").equalsIgnoreCase("No")) {
                    isProduct = jsonArray.getJSONObject(i).getString("is_product");
                    paper_type_id = jsonArray.getJSONObject(i).getString("paper_type_id");
                    paper_cropping_id = jsonArray.getJSONObject(i).getString("paper_cropping_id");
                    picture_size_id = jsonArray.getJSONObject(i).getString("picture_size_id");
                    discount = jsonArray.getJSONObject(i).getString("discount");
                }

                if (jsonArray.getJSONObject(i).getString("is_product").equalsIgnoreCase("Yes")) {
                    isProduct = jsonArray.getJSONObject(i).getString("is_product");
                    product_id = jsonArray.getJSONObject(i).getString("product_id");
                    type = jsonArray.getJSONObject(i).getString("type");

                    if (type.equalsIgnoreCase("Giftbox")) {
                        giftbox_Id = jsonArray.getJSONObject(i).getString("giftbox_id");
                        giftbox_Qty = jsonArray.getJSONObject(i).getString("giftbox_quantity");
                    }
                    if (type.equalsIgnoreCase("PhotoAlbum")) {

                        album_id = jsonArray.getJSONObject(i).getString("photoAlbum_id");
                        album_qty = jsonArray.getJSONObject(i).getString("photoAlbum_quantity");
                    }
                    if (type.equalsIgnoreCase("PrintFrame")) {
                        print_frame_qty = jsonArray.getJSONObject(i).getString("frame_quantity");
                        frame_size_id = jsonArray.getJSONObject(i).getString("size_id");
                        frame_color_id = jsonArray.getJSONObject(i).getString("color_id");
                    }

                }


                JsonObject jsonObject = new JsonObject();

                if (isProduct.equalsIgnoreCase("No")) {
                    jsonObject.addProperty("is_product", isProduct);
                    jsonObject.addProperty("paper_type_id", paper_type_id);
                    jsonObject.addProperty("paper_cropping_id", paper_cropping_id);
                    jsonObject.addProperty("picture_size_id", picture_size_id);
                    jsonObject.addProperty("image_ids", getUploadedImgIds(i));
                    jsonObject.addProperty("discount", discount);

                    cart_data.add(jsonObject);
                }

                if (isProduct.equalsIgnoreCase("Yes")) {
                    jsonObject.addProperty("is_product", isProduct);
                    jsonObject.addProperty("product_id", product_id);

                    if (type.equalsIgnoreCase("Giftbox"))
                        jsonObject.addProperty("type", "GiftCart");
                    else
                        jsonObject.addProperty("type", type);

                    if (type.equalsIgnoreCase("Giftbox")) {

                        jsonObject.addProperty("giftboxId", giftbox_Id);
                        jsonObject.addProperty("giftboxQty", giftbox_Qty);
                        cart_data.add(jsonObject);

                    }
                    if (type.equalsIgnoreCase("PhotoAlbum")) {

                        jsonObject.addProperty("albumId", album_id);
                        jsonObject.addProperty("albumQty", album_qty);
                        cart_data.add(jsonObject);
                    }
                    if (type.equalsIgnoreCase("PrintFrame")) {

                        jsonObject.addProperty("printFrameQty", print_frame_qty);
                        jsonObject.addProperty("size_id", frame_size_id);
                        jsonObject.addProperty("color_id", frame_color_id);
                        cart_data.add(jsonObject);
                    }

                }

                //   BaseApplication.getInstance().getSession().setSubmitCartData(cart_data.toString());
                Log.e(TAG, "setCartData: " + cart_data);

            }
        } catch (JSONException e) {
            e.printStackTrace();

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        progressDialog.dismiss();
    }


    private String getUploadedImgIds(int productIndex) {

        JSONArray imagesIdArray = new JSONArray();
        String images_ids = "";
        String imageIddata = BaseApplication.getInstance().getSession().getImagesArray();

        if (!imageIddata.isEmpty() && !imageIddata.equalsIgnoreCase("")) {
            try {

                imagesIdArray = new JSONArray(imageIddata);

                for (int x = 0; x < imagesIdArray.length(); x++) {

                    if (productIndex == imagesIdArray.getJSONObject(x).getInt("product_index")) {

                        if (images_ids.equals(""))
                            images_ids = imagesIdArray.getJSONObject(x).getString("image_id");
                        else
                            images_ids = images_ids + "," + imagesIdArray.getJSONObject(x).getString("image_id");

                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return images_ids;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                UiHelper.hideSoftKeyboard1(DeleiveryActivity.this);
                Intent intent = new Intent();
                intent.putExtra("width", getIntent().getStringExtra("width"));
                intent.putExtra("height", getIntent().getStringExtra("height"));
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void callApiForCountryList() {
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

        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().country_list(BaseApplication.getInstance().getSession().getToken(), String.valueOf(language_id));
        new NetworkController().get(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                if (jsonObject != null) {
                    try {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        final JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {

                            String url = jsonObject1.getJSONObject("data").getString("country_image_url");

                            deleiveryCountryModel = new DeleiveryCountryModel();
                            deleiveryCountryModel.initializeModel(jsonObject1);
                            deleiveryCountryModelArrayList = deleiveryCountryModel.arrayList;

                            deleiveryCountryAdapter = new DeleiveryCountryAdapter(deleiveryCountryModelArrayList, DeleiveryActivity.this, url);
                            recyclerView.setAdapter(deleiveryCountryAdapter);


                            Log.e(TAG, "Delivery COunrty name: " + delivery_country_name);
                            if (delivery_country_name != null && !delivery_country_name.equalsIgnoreCase("")) {

                                if (delivery_country_name.equalsIgnoreCase("Latvia") || delivery_country_name.equalsIgnoreCase("Lithuania") || delivery_country_name.equalsIgnoreCase("Estonia")) {
                                    for (int i = 0; i < deleiveryCountryModelArrayList.size(); i++) {
                                        if (deleiveryCountryModelArrayList.get(i).country_name.equalsIgnoreCase(delivery_country_name)) {
                                            deleiveryCountryModelArrayList.get(i).isSelected = true;
                                            deleiveryCountryModelArrayList.get(i).id = Integer.parseInt(country_id);
                                            setTxt_price(deleiveryCountryModelArrayList.get(i).price);
                                        } else {
                                            deleiveryCountryModelArrayList.get(i).isSelected = false;
                                        }

                                    }
                                    deleiveryCountryAdapter.selected = true;
                                } else {
                                    deleiveryCountryAdapter.selected = false;
                                }

                            }


                            //     txt_price.setText("€"+" "+String.valueOf(deleiveryCountryModelArrayList.get(0).price));
                            //       setTxt_price(deleiveryCountryModelArrayList.get(0).price);


                        } else {
                            UiHelper.showToast(DeleiveryActivity.this, jsonObject1.getString("message"));
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
                UiHelper.showToast(DeleiveryActivity.this, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    UiHelper.showToast(DeleiveryActivity.this, "No Internet");
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });

    }

    public void setTxt_price(float price) {

        txt_price.setText("€" + " " + String.valueOf(price));

        String pp = getIntent().getStringExtra("total_amount");
        String[] disss = pp.split("\\€");
        float current_price = Float.parseFloat(disss[1]);

        txt_total.setText("€" + " " + String.valueOf(current_price + price));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.txt_omnivaBox_gray:
                txt_omnivaBox.setVisibility(View.VISIBLE);
                txt_omnivaBox_gray.setVisibility(View.GONE);
                txt_omnivaCourier.setVisibility(View.VISIBLE);
                txt_omnivaCourier_pink.setVisibility(View.GONE);
                lyt_country.setVisibility(View.VISIBLE);
                delivery_type = "Omniva Parcel Machine";
                break;

            case R.id.txt_omnivaCourier:
                txt_omnivaBox.setVisibility(View.GONE);
                txt_omnivaBox_gray.setVisibility(View.VISIBLE);
                txt_omnivaCourier.setVisibility(View.GONE);
                txt_omnivaCourier_pink.setVisibility(View.VISIBLE);
                lyt_country.setVisibility(View.GONE);
                delivery_type = "Omniva Courier";
                break;
//
//            case R.id.editLocation:
//                askPermissions();
//                break;

            case R.id.btn_next:
                UiHelper.hideSoftKeyboard1(DeleiveryActivity.this);
                if (!validate())
                    return;
                if (BaseApplication.getInstance().getSession().isLoggedIn())
                    showPaymentDialog();

                else {

                    getTokenAPi();


                }

                break;

            case R.id.editCountry:
                //showCountryDialog();
                Intent intent = new Intent(this, EuropeanCountryActivity.class);
                if (country_id != null)
                    intent.putExtra("country_id", country_id);
                startActivityForResult(intent, 255);
                break;

            case R.id.editAddressOmniva:


                if (deleiveryCountryAdapter.selected == false) {
                    UiHelper.showToast(this, getResources().getString(R.string.pls_select_country_first));
                } else {
                    Intent intent1 = new Intent(DeleiveryActivity.this, OmnivaAddressActivity.class);
                    Log.e(TAG, "onClick:" + deleiveryCountryAdapter.country_name);
                    if (omniva_country_id != null)
                        intent1.putExtra("country_id", omniva_country_id);
                    intent1.putExtra("selected_country", deleiveryCountryAdapter.country_name);
                    startActivityForResult(intent1, 256);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);


                }
                break;
        }
    }

    private void getTokenAPi() {
        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("device_type", Constant.DIVICE_TYPE);
        jsonObject.addProperty("device_token", BaseApplication.getInstance().getSession().getDeviceToken());
        jsonObject.addProperty("first_name", editFirstname.getText().toString().trim());
        jsonObject.addProperty("last_name", editLastname.getText().toString().trim());
        jsonObject.addProperty("email", editEmail.getText().toString().trim());
        jsonObject.addProperty("contact_number", editPhone.getText().toString().trim());
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

        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().add_guest(jsonObject);
        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                if (jsonObject != null) {
                    try {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {
                            //  BaseApplication.getInstance().getSession().setIsLoggedIn();
                            //    BaseApplication.getInstance().getSession().setToken(jsonObject1.getJSONObject("data").getString("token"));
                            //  BaseApplication.getInstance().getSession().setProfileData(String.valueOf(jsonObject1.getJSONObject("data")));

                            token = jsonObject1.getJSONObject("data").getString("token");
                            showPaymentDialog();
//                            Intent intent=new Intent();
//
//
//                            String cartData=BaseApplication.getInstance().getSession().getCartData();
//                            try {
//                                JSONArray jsonArray = new JSONArray(cartData);
//
//                                for (int i=0;i<jsonArray.length();i++){
////                            jsonArray.getJSONObject(i).getJSONArray("images").length()==0 &&
//                                    if (jsonArray.getJSONObject(i).has("images")&& jsonArray.getJSONObject(i).getJSONArray("images")!=null){
//                                        intent=new Intent(DeleiveryActivity.this,UploadImage.class);
//                                    }  else
//                                        intent=new Intent(DeleiveryActivity.this,DeliveryOptionActivity.class);
//                                }
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                            intent.putExtra("total_amount",getIntent().getStringExtra("total_amount"));
//                            startActivity(intent);
//                            finish();
//                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                        } else {
                            UiHelper.showToast(DeleiveryActivity.this, jsonObject1.getString("message"));
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
                UiHelper.showToast(DeleiveryActivity.this, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    UiHelper.showToast(DeleiveryActivity.this, getResources().getString(R.string.pls_check_your_internet));
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });

    }


    public boolean validate() {
        boolean valid = true;
        String email = editEmail.getText().toString().trim();
        String Firstname = editFirstname.getText().toString().trim();
        String lastname = editLastname.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String country = editCountry.getText().toString().trim();
        String city = editCity.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String postcode = editPostCode.getText().toString().trim();
        String AddressOmniva = editAddressOmniva.getText().toString().trim();


        if (delivery_type.equalsIgnoreCase("Omniva Parcel Machine")) {
            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editEmail.setError(getResources().getString(R.string.valid_msg_email));
                valid = false;
            } else {
                editEmail.setError(null);
            }

            if (Firstname.isEmpty()) {
                editFirstname.setError(getResources().getString(R.string.pls_enter_first_name));
                valid = false;
            } else
                editFirstname.setError(null);


            if (lastname.isEmpty()) {
                editLastname.setError(getResources().getString(R.string.pls_enter_lastname));
                valid = false;
            } else
                editLastname.setError(null);


            if (phone.isEmpty()) {
                editPhone.setError(getResources().getString(R.string.pls_enter_contact));
                valid = false;
            } else {
                editPhone.setError(null);
            }


            if (deleiveryCountryAdapter.selected == false) {
                UiHelper.showToast(this, getResources().getString(R.string.pls_select_country));
                valid = false;
            } else {
                valid = true;
            }


            if (AddressOmniva.isEmpty()) {
                editAddressOmniva.setError(getResources().getString(R.string.pls_enter_address));
                valid = false;
            } else
                editAddressOmniva.setError(null);

            return valid;


        } else if (delivery_type.equalsIgnoreCase("Omniva Courier")) {
            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editEmail.setError(getResources().getString(R.string.valid_msg_email));
                valid = false;
            } else {
                editEmail.setError(null);
            }

            if (Firstname.isEmpty()) {
                editFirstname.setError(getResources().getString(R.string.pls_enter_first_name));
                valid = false;
            } else
                editFirstname.setError(null);


            if (lastname.isEmpty()) {
                editLastname.setError(getResources().getString(R.string.pls_enter_lastname));
                valid = false;
            } else
                editLastname.setError(null);


//           if (phone.isEmpty()) {
//               editPhone.setError(getResources().getString(R.string.pls_enter_contact));
//               valid = false;
//           } else {
//               editPhone.setError(null);
//           }

            if (deleiveryCountryAdapter.selected == false) {
                UiHelper.showToast(this, getResources().getString(R.string.pls_select_country));
                valid = false;
            } else {

                valid = true;
            }

            if (city.isEmpty()) {
                editCity.setError(getResources().getString(R.string.pls_enter_city));
                valid = false;
            } else
                editCity.setError(null);

            if (address.isEmpty()) {
                editAddress.setError(getResources().getString(R.string.pls_enter_address));
                valid = false;
            } else
                editAddress.setError(null);
//
//           if (postcode.isEmpty()) {
//               editPostCode.setError(getResources().getString(R.string.pls_enter_postcode));
//               valid = false;
//           }else
//               editPostCode.setError(null);
            return valid;

        } else {

            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editEmail.setError(getResources().getString(R.string.valid_msg_email));
                valid = false;
            } else {
                editEmail.setError(null);
            }

            if (Firstname.isEmpty()) {
                editFirstname.setError(getResources().getString(R.string.pls_enter_first_name));
                valid = false;
            } else
                editFirstname.setError(null);


            if (lastname.isEmpty()) {
                editLastname.setError(getResources().getString(R.string.pls_enter_lastname));
                valid = false;
            } else
                editLastname.setError(null);


//           if (phone.isEmpty()) {
//               editPhone.setError(getResources().getString(R.string.pls_enter_contact));
//               valid = false;
//           } else {
//               editPhone.setError(null);
//           }

            if (country.isEmpty()) {
                editCountry.setError(getResources().getString(R.string.pls_enter_country));
                valid = false;
            } else
                editCountry.setError(null);

            if (city.isEmpty()) {
                editCity.setError(getResources().getString(R.string.pls_enter_city));
                valid = false;
            } else
                editCity.setError(null);

            if (address.isEmpty()) {
                editAddress.setError(getResources().getString(R.string.pls_enter_address));
                valid = false;
            } else
                editAddress.setError(null);
//
//           if (postcode.isEmpty()) {
//               editPostCode.setError(getResources().getString(R.string.pls_enter_postcode));
//               valid = false;
//           }
//           else
//               editPostCode.setError(null);
            return valid;
        }
    }


    private void showPaymentDialog() {
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);

        View sheetView = this.getLayoutInflater().inflate(R.layout.dialog_payment_method, null);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();

        LinearLayout lyt_card = (LinearLayout) sheetView.findViewById(R.id.lyt_creditCard);
        LinearLayout lyt_paypal = (LinearLayout) sheetView.findViewById(R.id.lyt_paypal);
        final CheckBox cb_card = (CheckBox) sheetView.findViewById(R.id.cb_card);
        final CheckBox cb_paypal = (CheckBox) sheetView.findViewById(R.id.cb_paypal);
        Button btn_next = (Button) sheetView.findViewById(R.id.btn_next);
        lyt_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_card.setChecked(true);
                cb_paypal.setChecked(false);
                checked = "Card";
            }
        });

        lyt_paypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_card.setChecked(false);
                cb_paypal.setChecked(true);
                checked = "Paypal";
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checked.equalsIgnoreCase("Paypal")) {
                    BraintreeFragment braintreeFragment = null;

                    progressDialog.show();
                    try {
                        braintreeFragment = BraintreeFragment.newInstance(DeleiveryActivity.this, "sandbox_sbpx4w86_2wmdjknyzc7kr9d6");
                        PayPal.authorizeAccount(braintreeFragment);
                    } catch (InvalidArgumentException e) {
                        e.printStackTrace();
                    }


                    braintreeFragment.addListener(new PaymentMethodNonceCreatedListener() {
                        @Override
                        public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
                            PayPalAccountNonce paypalAccountNonce = (PayPalAccountNonce) paymentMethodNonce;
                            PostalAddress billingAddress = paypalAccountNonce.getBillingAddress();
                            String streetAddress = billingAddress.getStreetAddress();
                            String extendedAddress = billingAddress.getExtendedAddress();
                            String locality = billingAddress.getLocality();
                            String countryCodeAlpha2 = billingAddress.getCountryCodeAlpha2();
                            String postalCode = billingAddress.getPostalCode();
                            String region = billingAddress.getRegion();
                            String nonce = paymentMethodNonce.getNonce();

                            if (BaseApplication.getInstance().getSession().isLoggedIn())
                                callApiSubmitCart(nonce);
                            else
                                callApiSubmitCartForGuest(nonce);

                            Log.e(TAG, "onPaymentMethodNonceCreated: " + nonce);
                        }
                    });


                } else {

                    Intent intent = new Intent(DeleiveryActivity.this, PaymentDetailActivity.class);

                    intent.putExtra("first_name", editFirstname.getText().toString());
                    intent.putExtra("last_name", editLastname.getText().toString());
                    intent.putExtra("phone", editPhone.getText().toString());
                    intent.putExtra("email", editEmail.getText().toString());
                    float current_price;
                    if (txt_price.toString().equalsIgnoreCase("0")) {
                        current_price = 0;
                    } else {
                        String pp = txt_price.getText().toString();
                        String[] disss = pp.split("\\€");
                        current_price = Float.parseFloat(disss[1]);
                    }
                    intent.putExtra("shipping_price", current_price);

                    if (delivery_type.equalsIgnoreCase("Omniva Parcel Machine")) {
                        intent.putExtra("location", editAddressOmniva.getText().toString());
                        intent.putExtra("deliver_country_id", deleiveryCountryAdapter.country_id);
                        intent.putExtra("latitude", omniva_latitude);
                        intent.putExtra("longitude", omniva_longitude);


                    } else if (delivery_type.equalsIgnoreCase("Omniva Courier")) {
                        intent.putExtra("location", editAddress.getText().toString());
                        intent.putExtra("city", editCity.getText().toString());
                        intent.putExtra("deliver_country_id", deleiveryCountryAdapter.country_id);
                        intent.putExtra("country", deleiveryCountryAdapter.country_name);
                        intent.putExtra("post_code", editPostCode.getText().toString());
                    } else {

                        intent.putExtra("location", editAddress.getText().toString());
                        intent.putExtra("city", editCity.getText().toString());
                        intent.putExtra("country", editCountry.getText().toString());
                        intent.putExtra("post_code", editPostCode.getText().toString());
                        intent.putExtra("country_id", country_id);
                    }


                    intent.putExtra("delivery_type", delivery_type);
                    intent.putExtra("payment_method", checked);

                    intent.putExtra("total_amount", txt_total.getText().toString());
                    intent.putExtra("option", getIntent().getStringExtra("option"));
                    if (!BaseApplication.getInstance().getSession().isLoggedIn())
                        intent.putExtra("token", token);
                    Log.e(TAG, "onClick: " + getIntent().getStringExtra("total_amount"));


                    Bundle b = new Bundle();
                    b.putString("Array", cart_data.toString());
                    intent.putExtras(b);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                }
            }
        });
    }

    private void callApiSubmitCart(String payment_token) {

        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("payment_method", checked);
        jsonObject.addProperty("delivery_type", delivery_type);
        jsonObject.addProperty("deliver_user_firstname", editFirstname.getText().toString());
        jsonObject.addProperty("deliver_user_lastname", getIntent().getStringExtra("last_name"));
        jsonObject.addProperty("deliver_user_number", editPhone.getText().toString());
        jsonObject.addProperty("deliver_user_email", editEmail.getText().toString());

        if (delivery_type.equalsIgnoreCase("Omniva Parcel Machine")) {
            jsonObject.addProperty("location", editAddressOmniva.getText().toString());
            jsonObject.addProperty("deliver_country_id", deleiveryCountryAdapter.country_id);
            jsonObject.addProperty("latitude", omniva_latitude);
            jsonObject.addProperty("longitude", omniva_longitude);

        } else if (delivery_type.equalsIgnoreCase("Omniva Courier")) {
            jsonObject.addProperty("location", editAddress.getText().toString());
            jsonObject.addProperty("city", editCity.getText().toString());
            jsonObject.addProperty("country", deleiveryCountryAdapter.country_name);
            jsonObject.addProperty("post_code", editPostCode.getText().toString());
            jsonObject.addProperty("deliver_country_id", deleiveryCountryAdapter.country_id);
        } else {

            jsonObject.addProperty("location", editAddress.getText().toString());
            jsonObject.addProperty("city", editCity.getText().toString());
            jsonObject.addProperty("country", editCountry.getText().toString());
            jsonObject.addProperty("post_code", editPostCode.getText().toString());
            jsonObject.addProperty("country_id", country_id);
        }

        String pp = txt_total.getText().toString().trim();
        String[] disss = pp.split("\\€");
        float current_price = Float.parseFloat(disss[1]);
        Log.e(TAG, "current_price: " + current_price);
        float current_price1;
        if (txt_price.toString().equalsIgnoreCase("0")) {
            current_price1 = 0;
        } else {
            String pp1 = txt_price.getText().toString();
            String[] disss1 = pp1.split("\\€");
            current_price1 = Float.parseFloat(disss1[1]);
        }
        jsonObject.addProperty("shipping_price", current_price1);
        jsonObject.addProperty("total_amount", current_price);
        jsonObject.addProperty("payment_token", payment_token);
        jsonObject.add("cart_data", cart_data);

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

        Log.e(TAG, "callApiForCart JSONOBJECT: " + jsonObject.toString());

        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().submit_cart(BaseApplication.getInstance().getSession().getToken(), jsonObject);
        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                if (jsonObject != null) {
                    try {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {

                            Log.e(TAG, "Success: ");
                            BaseApplication.getInstance().getSession().setCartData("");
                            BaseApplication.getInstance().getSession().setSelectedImages("");
                            BaseApplication.getInstance().getSession().setImagesArray("");
                            BaseApplication.getInstance().getSession().setHeight("");
                            BaseApplication.getInstance().getSession().setWidth("");
                            //    BaseApplication.getInstance().getSession().setSubmitCartData("");
                            Intent intent = new Intent(DeleiveryActivity.this, OrderPlaced.class);
                            startActivity(intent);
                            finish();
                            //   final JSONObject jsonObject2 = jsonObject1.getJSONObject("data");


                        } else {
                            Log.e(TAG, "Success: " + jsonObject1.getString("message"));
                            UiHelper.showToast(DeleiveryActivity.this, jsonObject1.getString("message"));
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
                Log.e(TAG, "Error: " + error);
                UiHelper.showToast(DeleiveryActivity.this, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    UiHelper.showToast(DeleiveryActivity.this, "No Internet");
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });

    }

    private void callApiSubmitCartForGuest(String payment_token) {

        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("payment_method", checked);
        jsonObject.addProperty("delivery_type", delivery_type);
        jsonObject.addProperty("deliver_user_firstname", editFirstname.getText().toString());
        jsonObject.addProperty("deliver_user_lastname", getIntent().getStringExtra("last_name"));
        jsonObject.addProperty("deliver_user_number", editPhone.getText().toString());
        jsonObject.addProperty("deliver_user_email", editEmail.getText().toString());

        if (delivery_type.equalsIgnoreCase("Omniva Parcel Machine")) {
            jsonObject.addProperty("location", editAddressOmniva.getText().toString());
            jsonObject.addProperty("deliver_country_id", deleiveryCountryAdapter.country_id);
            jsonObject.addProperty("latitude", omniva_latitude);
            jsonObject.addProperty("longitude", omniva_longitude);

        } else if (delivery_type.equalsIgnoreCase("Omniva Courier")) {
            jsonObject.addProperty("location", editAddress.getText().toString());
            jsonObject.addProperty("city", editCity.getText().toString());
            jsonObject.addProperty("country", deleiveryCountryAdapter.country_name);
            jsonObject.addProperty("post_code", editPostCode.getText().toString());
            jsonObject.addProperty("deliver_country_id", deleiveryCountryAdapter.country_id);
        } else {

            jsonObject.addProperty("location", editAddress.getText().toString());
            jsonObject.addProperty("city", editCity.getText().toString());
            jsonObject.addProperty("country", editCountry.getText().toString());
            jsonObject.addProperty("post_code", editPostCode.getText().toString());
            jsonObject.addProperty("country_id", country_id);
        }

        String pp = txt_total.getText().toString().trim();
        String[] disss = pp.split("\\€");
        float current_price = Float.parseFloat(disss[1]);

        String pp1 = txt_price.getText().toString();
        String[] disss1 = pp1.split("\\€");
        float current_price1 = Float.parseFloat(disss1[1]);

        jsonObject.addProperty("shipping_price", current_price1);
        jsonObject.addProperty("total_amount", current_price);

        jsonObject.addProperty("payment_token", payment_token);
        jsonObject.add("cart_data", cart_data);
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
        Log.e(TAG, "callApiForCart JSONOBJECT: " + jsonObject.toString());

        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().submit_cart(token, jsonObject);
        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                if (jsonObject != null) {
                    try {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {

                            Log.e(TAG, "Success: ");
                            BaseApplication.getInstance().getSession().setCartData("");
                            BaseApplication.getInstance().getSession().setSelectedImages("");
                            BaseApplication.getInstance().getSession().setImagesArray("");
                            BaseApplication.getInstance().getSession().setHeight("");
                            BaseApplication.getInstance().getSession().setWidth("");
                            //    BaseApplication.getInstance().getSession().setSubmitCartData("");
                            Intent intent = new Intent(DeleiveryActivity.this, OrderPlaced.class);
                            startActivity(intent);
                            finish();
                            //   final JSONObject jsonObject2 = jsonObject1.getJSONObject("data");


                        } else {
                            Log.e(TAG, "Success: " + jsonObject1.getString("message"));
                            UiHelper.showToast(DeleiveryActivity.this, jsonObject1.getString("message"));
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
                Log.e(TAG, "Error: " + error);
                UiHelper.showToast(DeleiveryActivity.this, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    UiHelper.showToast(DeleiveryActivity.this, "No Internet");
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 10001) {
                Bundle bundle = data.getExtras();
                Log.e("tag", "text" + bundle.getString("text"));
                Log.e("tag", "latitude" + bundle.getString("latitude"));
                Log.e("tag", "longitude" + bundle.getString("longitude"));
                if (bundle.getString("latitude") != null && bundle.getString("longitude") != null) {
                    final String address = bundle.getString("text");
                    latitude = Double.valueOf(bundle.getString("latitude"));
                    longitude = Double.valueOf(bundle.getString("longitude"));
                    BaseApplication.getInstance().getSession().setDeliveryLatitude(String.valueOf(latitude));
                    BaseApplication.getInstance().getSession().setDeliveryLongitude(String.valueOf(longitude));
                    LatLng latLng = new LatLng(latitude, longitude);
//                    if (googleMap != null) {
//                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
//                    }
                    //   editLocation.setText(bundle.getString("text"));
                }
            }
            if (requestCode == 255) {
                if (data.getExtras() != null) {
                    Bundle bundle1 = data.getExtras();
                    if (bundle1.getString("country_name") != null) {
                        editCountry.setText(bundle1.getString("country_name"));

                        float shipping_price = 0;
                        shipping_price = Float.parseFloat(bundle1.getString("price"));
                        txt_price.setText(" €" + String.valueOf(shipping_price));

                        String pp = getIntent().getStringExtra("total_amount");
                        String[] separated = pp.split("€");
                        float old_price = Float.parseFloat(separated[1].trim());
                        float total_price = old_price + shipping_price;
                        txt_total.setText(" €" + String.valueOf(total_price));
                        country_id = bundle1.getString("id");

//                    String pp=jsonArray.getJSONObject(i).getString("giftbox_price");
//                    String[] separated = pp.split("€");
//                    String qua=jsonArray.getJSONObject(i).getString("giftbox_quantity");
//                    total = (total + Float.parseFloat(separated[1].trim()) * Float.parseFloat(qua) );
                    }
                }
            }

            if (requestCode == 256) {
                if (data.getExtras() != null) {
                    Bundle bundle1 = data.getExtras();
                    if (bundle1.getString("country_name") != null) {
                        editAddressOmniva.setText(bundle1.getString("country_name"));
                        omniva_country_id = bundle1.getString("id");
                        omniva_latitude = bundle1.getString("latitude");
                        omniva_longitude = bundle1.getString("longitude");
                    }
                }
            }
        }
    }

    private void askPermissions() {
        permissionToAsk.clear();
        for (String s : Constant.askForLocationPermission) {
            if (ContextCompat.checkSelfPermission(this, s) == PackageManager.PERMISSION_DENIED) {
                permissionToAsk.add(s);
                Log.e("tag", "if askPermissions: ");
            }

        }
        if (!permissionToAsk.isEmpty()) {
            Log.e("tag", "if empty askPermissions: ");
            ActivityCompat.requestPermissions(this, permissionToAsk.toArray(new String[permissionToAsk.size()]), Constant.requestcodeForPermission);
        } else {
            Log.e("tag", "else access askPermissions: ");
            Intent intent1 = new Intent(DeleiveryActivity.this, OmnivaAddressActivity.class);
            Log.e(TAG, "onClick:" + deleiveryCountryAdapter.country_name);
            if (omniva_country_id != null)
                intent1.putExtra("country_id", omniva_country_id);
            intent1.putExtra("selected_country", deleiveryCountryAdapter.country_name);
            startActivityForResult(intent1, 256);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        }
    }

    private void accessPermission() {
        GPSTracker gpsTracker = new GPSTracker(this);
        if (gpsTracker.canGetLocation()) {
            Log.e("tag", "if can get locationaccessPermission: ");
            Intent intent1 = new Intent(this, OmnivaAddressActivity.class);
            Log.e(TAG, "onClick:" + deleiveryCountryAdapter.country_name);
            if (omniva_country_id != null)
                intent1.putExtra("country_id", omniva_country_id);
            intent1.putExtra("selected_country", deleiveryCountryAdapter.country_name);
            startActivityForResult(intent1, 256);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        } else {
            Log.e("tag", "else cannot get locationaccessPermission: ");

            showDialog();

        }
    }

    private void showDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.gpsTitle));
        builder.setMessage(getString(R.string.gpsMessage));
        builder.setPositiveButton(getResources().getString(R.string.settingButton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);

            }
        });
        builder.setNegativeButton(getResources().getString(R.string.exitButton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //  BaseApplication.getInstance().getSession().setExit("Exit");
                dialog.dismiss();
                Intent intent1 = new Intent(DeleiveryActivity.this, OmnivaAddressActivity.class);
                Log.e(TAG, "onClick:" + deleiveryCountryAdapter.country_name);
                if (omniva_country_id != null)
                    intent1.putExtra("country_id", omniva_country_id);
                intent1.putExtra("selected_country", deleiveryCountryAdapter.country_name);
                startActivityForResult(intent1, 256);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

            }
        });
        android.support.v7.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionToAsk.clear();
        if (requestCode == Constant.requestcodeForPermission) {
            boolean allGranted = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    Log.e("tag", "Permission denied onRequestPermissionsResult: ");
                    allGranted = false;

                }
            }
            if (allGranted) {
                Log.e("tag", "All grantednRequestPermissionsResult: ");
                accessPermission();
            } else {
                Intent intent1 = new Intent(this, OmnivaAddressActivity.class);
                Log.e(TAG, "onClick:" + deleiveryCountryAdapter.country_name);
                if (omniva_country_id != null)
                    intent1.putExtra("country_id", omniva_country_id);
                intent1.putExtra("selected_country", deleiveryCountryAdapter.country_name);
                startActivityForResult(intent1, 256);
                this.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        }
    }


}
