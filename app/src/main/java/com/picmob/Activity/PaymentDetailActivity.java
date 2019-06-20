package com.picmob.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.Card;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.CardBuilder;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.picmob.AppCustomView.Constant;
import com.picmob.AppCustomView.CustomTextViewBold;
import com.picmob.AppCustomView.CustomTextViewNormal;
import com.picmob.AppCustomView.ProgressDialog;
import com.picmob.AppCustomView.UiHelper;
import com.picmob.Networking.BaseApplication;
import com.picmob.Networking.NetworkController;
import com.picmob.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class PaymentDetailActivity extends BaseActivity {
    private static final String TAG = "PaymentDetailActvity";
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvTitle)
    CustomTextViewBold mTitle;

    @BindView(R.id.Iv_cart)
    ImageView Iv_cart;

    @BindView(R.id.txt_counter)
    CustomTextViewBold txt_counter;

    @BindView(R.id.btn_next)
    Button btn_next;

    @BindView(R.id.editCardno)
    EditText editCardno;

    @BindView(R.id.editCardname)
    EditText editCardname;

    @BindView(R.id.editCarddate)
    EditText editCarddate;

    @BindView(R.id.editCardcvv)
    EditText editCardcvv;

    @BindView(R.id.txt_totalamount)
    CustomTextViewBold txt_totalamount;

    @BindView(R.id.txt_cardtype)
    CustomTextViewNormal txt_cardtype;

    BraintreeFragment braintreeFragment;
    String payment_token;
    JSONArray cartArray;

    JSONArray jsonArray=new JSONArray();
    String paper_type_id,paper_cropping_id,picture_size_id,image_ids,isProduct,discount;
    String type,giftbox_Id,giftbox_Qty,album_id,album_qty,product_id,frame_size_id,frame_color_id,print_frame_qty;

    JsonArray cart_data = new JsonArray();

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_payment_details);
        ButterKnife.bind(this);
        setActionBar();

//        if (BaseApplication.getInstance().getSession().isLoggedIn())
//        callApi();
        progressDialog=UiHelper.generateProgressDialog(PaymentDetailActivity.this,false);

        try {
            braintreeFragment = BraintreeFragment.newInstance(PaymentDetailActivity.this, "sandbox_sbpx4w86_2wmdjknyzc7kr9d6");
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }


        braintreeFragment.addListener(new PaymentMethodNonceCreatedListener() {
            @Override
            public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
                payment_token = paymentMethodNonce.getNonce();
                progressDialog.dismiss();
                if (BaseApplication.getInstance().getSession().isLoggedIn())
                callApiForCart();
                else
                    callApiForCartForGuest();
                Log.e("TAG", "onPaymentMethodNonceCreated: " + payment_token);
            }
        });


            braintreeFragment.addListener(new BraintreeErrorListener() {
                @Override
                public void onError(Exception error) {
                    Log.e(TAG, "onError: "+error );
                }
            });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validate())
                    return;
                progressDialog.show();
                CardBuilder cardBuilder = new CardBuilder()
                        .cardNumber(editCardno.getText().toString())
                        .cvv(editCardcvv.getText().toString())
                        .firstName(editCardname.getText().toString())
                        .expirationDate(editCarddate.getText().toString());

                Card.tokenize(braintreeFragment, cardBuilder);


            }
        });

        txt_totalamount.setText(getIntent().getStringExtra("total_amount"));
        txt_cardtype.setText(getIntent().getStringExtra("payment_method"));
        setCartData();

        editCarddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();

                int y = c.get(Calendar.YEAR);
                int m = c.get(Calendar.MONTH);
                int d = c.get(Calendar.DAY_OF_MONTH);



                final String[] MONTH = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

                DatePickerDialog dp = new DatePickerDialog(PaymentDetailActivity.this, AlertDialog.THEME_HOLO_DARK,
                        new DatePickerDialog.OnDateSetListener() {


                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String erg = "";
                            //    erg = String.valueOf(dayOfMonth);
                                erg += String.valueOf(monthOfYear + 1);
                                erg += "/" + year;

                                 editCarddate.setText(erg);
                                view.findViewById(getResources().getIdentifier("day","id","android")).setVisibility(View.GONE);
                            }

                        }, y, m,d);

//                dp.setTitle("Calender");
//                dp.setMessage("Select Your Expiry Date");
                ((ViewGroup) dp.getDatePicker()).findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
                dp.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
                dp.show();



            }

        });

    }




    public void setCartData(){

        JSONArray imagesIdArray=new JSONArray();
        String images_ids="";
        String imageIddata=BaseApplication.getInstance().getSession().getImagesArray();
        String[] words=new String[0];
        if (!imageIddata.isEmpty() && !imageIddata.equalsIgnoreCase("")) {

            try {
                imagesIdArray = new JSONArray(imageIddata);
                for (int j = 0; j < imagesIdArray.length(); j++) {

                    if (images_ids.equalsIgnoreCase("")){
                        images_ids =imagesIdArray.getJSONObject(j).getString("image_id");
                    }else
                        images_ids = images_ids +","+ imagesIdArray.getJSONObject(j).getString("image_id");

                    //words = images_ids.split(" ");
                    //     System.out.println(Arrays.toString(words));

                    Log.e(TAG, "setCartData:ids " + images_ids);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String cartDataSesion=BaseApplication.getInstance().getSession().getCartData();
        try {
            jsonArray = new JSONArray(cartDataSesion);
            Log.e("TAG", "jsonArray sesion: " + jsonArray.toString());



            for (int i = 0; i < jsonArray.length(); i++) {

                Log.e(TAG, "length: "+jsonArray.length() );
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
                        print_frame_qty=jsonArray.getJSONObject(i).getString("frame_quantity");
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
                    jsonObject.addProperty("discount", discount);
                    jsonObject.addProperty("image_ids", getUploadedImgIds(i));
                    cart_data.add(jsonObject);
                }

                if (isProduct.equalsIgnoreCase("Yes")) {
                    jsonObject.addProperty("is_product", isProduct);
                    jsonObject.addProperty("product_id", product_id);

                    if (type.equalsIgnoreCase("Giftbox"))
                    jsonObject.addProperty("type", "GiftCart");
                    else
                        jsonObject.addProperty("type",type);

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
        } catch(JSONException e){
            e.printStackTrace();

        }

    }

    private String getUploadedImgIds(int productIndex) {

        JSONArray imagesIdArray = new JSONArray();
        String images_ids = "";
        String imageIddata = BaseApplication.getInstance().getSession().getImagesArray();

        if (!imageIddata.isEmpty() && !imageIddata.equalsIgnoreCase("")) {
            try {

                imagesIdArray = new JSONArray(imageIddata);

                for(int x=0; x < imagesIdArray.length(); x++){

                    if(productIndex == imagesIdArray.getJSONObject(x).getInt("product_index")){

                        if(images_ids.equals(""))
                            images_ids = imagesIdArray.getJSONObject(x).getString("image_id");
                        else
                            images_ids = images_ids+","+imagesIdArray.getJSONObject(x).getString("image_id");

                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return images_ids;
    }


    private void callApiForCart() {

        Log.e(TAG, "callApiForCart: " );
        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("payment_method", getIntent().getStringExtra("payment_method"));
        jsonObject.addProperty("delivery_type", getIntent().getStringExtra("delivery_type"));
        jsonObject.addProperty("deliver_user_firstname", getIntent().getStringExtra("first_name"));
        jsonObject.addProperty("deliver_user_lastname", getIntent().getStringExtra("last_name"));
        jsonObject.addProperty("deliver_user_number", getIntent().getStringExtra("phone"));
        jsonObject.addProperty("deliver_user_email", getIntent().getStringExtra("email"));
        jsonObject.addProperty("shipping_price", getIntent().getFloatExtra("shipping_price",0));


        if (getIntent().getStringExtra("delivery_type").equalsIgnoreCase("Omniva Parcel Machine")){
            jsonObject.addProperty("location", getIntent().getStringExtra("location"));
            jsonObject.addProperty("deliver_country_id", getIntent().getStringExtra("deliver_country_id"));
            jsonObject.addProperty("latitude", getIntent().getStringExtra("latitude"));
            jsonObject.addProperty("longitude", getIntent().getStringExtra("longitude"));

        }else if (getIntent().getStringExtra("delivery_type").equalsIgnoreCase("Omniva Courier")){
            jsonObject.addProperty("location", getIntent().getStringExtra("location"));
            jsonObject.addProperty("city", getIntent().getStringExtra("city"));
            jsonObject.addProperty("country", getIntent().getStringExtra("country"));
            jsonObject.addProperty("post_code", getIntent().getStringExtra("post_code"));
            jsonObject.addProperty("deliver_country_id", getIntent().getStringExtra("deliver_country_id"));
        }else {

            jsonObject.addProperty("location", getIntent().getStringExtra("location"));
            jsonObject.addProperty("city", getIntent().getStringExtra("city"));
            jsonObject.addProperty("country", getIntent().getStringExtra("country"));
            jsonObject.addProperty("post_code", getIntent().getStringExtra("post_code"));
            jsonObject.addProperty("country_id", getIntent().getStringExtra("country_id"));

        }

         String pp = getIntent().getStringExtra("total_amount");
        String[] disss = pp.split("\\€");
        float current_price = Float.parseFloat(disss[1]);
        Log.e(TAG, "current_price: "+current_price );
        jsonObject.addProperty("total_amount", current_price);

        jsonObject.addProperty("payment_token", payment_token);

      //  String carts=BaseApplication.getInstance().getSession().getSubmitCartData();



        Bundle b = getIntent().getExtras();
        String Array=b.getString("Array");


        jsonObject.add("cart_data",cart_data);
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
                            Intent intent = new Intent(PaymentDetailActivity.this, OrderPlaced.class);
                            startActivity(intent);
                            finish();
                            //   final JSONObject jsonObject2 = jsonObject1.getJSONObject("data");


                        } else {
                            Log.e(TAG, "Success: "+jsonObject1.getString("message") );
                            UiHelper.showToast(PaymentDetailActivity.this, jsonObject1.getString("message"));
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
                Log.e(TAG, "Error: "+error );
                UiHelper.showToast(PaymentDetailActivity.this, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    UiHelper.showToast(PaymentDetailActivity.this, getResources().getString(R.string.pls_check_your_internet));
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });

    }


    private void callApiForCartForGuest() {

        Log.e(TAG, "callApiForCartForGuest: " );

        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("payment_method", getIntent().getStringExtra("payment_method"));
        jsonObject.addProperty("delivery_type", getIntent().getStringExtra("delivery_type"));
        jsonObject.addProperty("deliver_user_firstname", getIntent().getStringExtra("first_name"));
        jsonObject.addProperty("deliver_user_lastname", getIntent().getStringExtra("last_name"));
        jsonObject.addProperty("deliver_user_number", getIntent().getStringExtra("phone"));
        jsonObject.addProperty("deliver_user_email", getIntent().getStringExtra("email"));
        jsonObject.addProperty("shipping_price", getIntent().getFloatExtra("shipping_price",0));


        if (getIntent().getStringExtra("delivery_type").equalsIgnoreCase("Omniva Parcel Machine")){
            jsonObject.addProperty("location", getIntent().getStringExtra("location"));
            jsonObject.addProperty("deliver_country_id", getIntent().getStringExtra("deliver_country_id"));
            jsonObject.addProperty("latitude", getIntent().getStringExtra("latitude"));
            jsonObject.addProperty("longitude", getIntent().getStringExtra("longitude"));

        }else if (getIntent().getStringExtra("delivery_type").equalsIgnoreCase("Omniva Courier")){
            jsonObject.addProperty("location", getIntent().getStringExtra("location"));
            jsonObject.addProperty("city", getIntent().getStringExtra("city"));
            jsonObject.addProperty("country", getIntent().getStringExtra("country"));
            jsonObject.addProperty("post_code", getIntent().getStringExtra("post_code"));
            jsonObject.addProperty("deliver_country_id", getIntent().getStringExtra("deliver_country_id"));
        }else {

            jsonObject.addProperty("location", getIntent().getStringExtra("location"));
            jsonObject.addProperty("city", getIntent().getStringExtra("city"));
            jsonObject.addProperty("country", getIntent().getStringExtra("country"));
            jsonObject.addProperty("post_code", getIntent().getStringExtra("post_code"));
            jsonObject.addProperty("country_id", getIntent().getStringExtra("country_id"));

        }

        String pp = getIntent().getStringExtra("total_amount");
        String[] disss = pp.split("\\€");
        float current_price = Float.parseFloat(disss[1]);
        Log.e(TAG, "current_price: "+current_price );
        jsonObject.addProperty("total_amount", current_price);

        jsonObject.addProperty("payment_token", payment_token);

        //  String carts=BaseApplication.getInstance().getSession().getSubmitCartData();



        Bundle b = getIntent().getExtras();
        String Array=b.getString("Array");


        jsonObject.add("cart_data",cart_data);
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

        Log.e(TAG, "callApiForCart JSONOBJECT: " + jsonObject.toString());

        String token=getIntent().getStringExtra("token");

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
                            Intent intent = new Intent(PaymentDetailActivity.this, OrderPlaced.class);
                            startActivity(intent);
                            finish();
                            //   final JSONObject jsonObject2 = jsonObject1.getJSONObject("data");


                        } else {
                            Log.e(TAG, "Success: "+jsonObject1.getString("message") );
                            UiHelper.showToast(PaymentDetailActivity.this, jsonObject1.getString("message"));
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
                Log.e(TAG, "Error: "+error );
                UiHelper.showToast(PaymentDetailActivity.this, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    UiHelper.showToast(PaymentDetailActivity.this, getResources().getString(R.string.pls_check_your_internet));
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });

    }


    private void setActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_icon_drawable);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        Iv_cart.setVisibility(View.GONE);
        txt_counter.setVisibility(View.GONE);
        mTitle.setText(R.string.payment_details);

    }

    public boolean validate() {
        boolean valid = true;
        String cardno = editCardno.getText().toString().trim();
        String name = editCardname.getText().toString().trim();
        String date = editCarddate.getText().toString().trim();
        String cvv = editCardcvv.getText().toString().trim();
        //   String password = mEditPass.getText().toString().trim();
        if (cardno.isEmpty()) {
            editCardno.setError(getString(R.string.pls_enter_cardno));
            valid = false;
        } else {
            editCardno.setError(null);
        }

        if (name.isEmpty()) {
            editCardname.setError(getString(R.string.pls_enter_name));
            valid = false;
        } else
            editCardname.setError(null);

        if (date.isEmpty()) {
            editCarddate.setError(getString(R.string.pls_enter_date));
            valid = false;
        } else {
            editCarddate.setError(null);
        }

        if (cvv.isEmpty()) {
            editCardcvv.setError(getString(R.string.pls_enter_cvv));
            valid = false;
        } else {
            editCardcvv.setError(null);
        }

        return valid;
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
        Intent intent=new Intent(this,DeleiveryActivity.class);
        intent.putExtra("option",getIntent().getStringExtra("option"));
        intent.putExtra("total_amount",getIntent().getStringExtra("total_amount"));
        startActivity(intent);
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        super.onBackPressed();

    }

    public void callApi() {
        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();

        JsonObject jsonObject=new JsonObject();
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

        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().my_card(BaseApplication.getInstance().getSession().getToken(),String.valueOf(language_id));
        new NetworkController().get(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                if (jsonObject != null) {
                    try {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {

                        JSONArray dataArray=jsonObject1.getJSONArray("data");
                            String cardno="",name="",date="";
                        if (dataArray.length()==0)
                            UiHelper.showToast(PaymentDetailActivity.this,"No Saved Card Found!");
                            else
                                 cardno=dataArray.getJSONObject(0).getString("card_number");
                            name=dataArray.getJSONObject(0).getString("card_holder_name");
                            date=dataArray.getJSONObject(0).getString("expiry_date");
                            editCardno.setText(cardno);
                            editCardname.setText(name);
                            editCarddate.setText(date);


                        } else {
                            UiHelper.showToast(PaymentDetailActivity.this, jsonObject1.getString("message"));
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
                UiHelper.showToast(PaymentDetailActivity.this, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    UiHelper.showToast(PaymentDetailActivity.this, getResources().getString(R.string.pls_check_your_internet));
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });

    }

}
