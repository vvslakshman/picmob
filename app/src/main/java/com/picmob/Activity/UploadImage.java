package com.picmob.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.picmob.AppCustomView.Constant;
import com.picmob.AppCustomView.CustomTextViewBold;
import com.picmob.AppCustomView.UiHelper;
import com.picmob.Networking.BaseApplication;
import com.picmob.Networking.NetworkController;
import com.picmob.Networking.ProgressRequestBody;
import com.picmob.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

public class UploadImage extends BaseActivity implements ProgressRequestBody.UploadCallbacks {

    private static final String TAG = "UploadImage";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvTitle)
    CustomTextViewBold mTitle;

    @BindView(R.id.Iv_cart)
    ImageView Iv_cart;

    @BindView(R.id.txt_counter)
    CustomTextViewBold txt_counter;

    @BindView(R.id.progressBar)
    ProgressBar progressBars;

    @BindView(R.id.txtPercentage)
    TextView txtPercentage;

    @BindView(R.id.txtstatus)
    TextView txtstatus;

    JSONArray jsonArray = new JSONArray();
    String image;
    byte[] profile_img_EncodeByte = null;
    String responseString = "";
    long totalSize = 0;

    //  ProgressRequestBody fileBody;
    File file;

    int total_images = 0;
    int product_index = 0;
    int image_index = 0;
    int quantity = 1;
    int uploaded_images = 0;

    JSONArray imagesArray = new JSONArray();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_upload_image);
        ButterKnife.bind(this);
        setActionBar();

        String cartData = BaseApplication.getInstance().getSession().getCartData();
        Log.e(TAG, "onCreate: " + cartData.toString());
        try {
            if (cartData != null)
                jsonArray = new JSONArray(cartData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < jsonArray.length(); i++) {


            try {
                if (jsonArray.getJSONObject(i).has("images")) {
                    JSONArray jsonArray1 = jsonArray.getJSONObject(i).getJSONArray("images");
                    total_images = total_images + jsonArray1.length();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        uploadImage();

    }


    public void uploadImage() {
        if (total_images > 0) {
            txtstatus.setText(String.valueOf(uploaded_images) + "/" + String.valueOf(total_images));
            try {

                if (jsonArray.getJSONObject(product_index).has("images")) {
                    if (jsonArray.getJSONObject(product_index).getJSONArray("images").length() > 0) {

                        if (product_index == jsonArray.length())
                            return;

                        Log.e(TAG, "uploadImage:TOCHECK"+  jsonArray.getJSONObject(product_index).getString("paper_cropping_id"));
                        String cropping_id= jsonArray.getJSONObject(product_index).getString("paper_cropping_id");
                        String image = jsonArray.getJSONObject(product_index).getJSONArray("images").getJSONObject(image_index).getString("image");
                         quantity=jsonArray.getJSONObject(product_index).getJSONArray("images").getJSONObject(image_index).getInt("quantity");
                        convertImage(image);

                      //  quantity = jsonArray.getJSONObject(product_index).getInt("photos_quantity");
                        uploaded_images++;

                        callApi(product_index, image_index, quantity,cropping_id);

                        // new Upload(product_index, image_index, quantity).execute();
                    }
                } else {

                    if (product_index != jsonArray.length() - 1) {
                        product_index++;
                        image_index = 0;
                        uploadImage();
                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void setActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_icon_drawable);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        Iv_cart.setVisibility(View.GONE);
        txt_counter.setVisibility(View.GONE);
        mTitle.setText(R.string.photos_uploading);
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

    //convertimg string image uri into bytearray
    public void convertImage(String image) {

        Log.e(TAG, "convertImage: "+image );
        file = new File(image);
        Log.e(TAG, "convertImage file: "+file.getName());
    //    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
     //   Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
        //    Log.e(TAG, "onCreate bitmap: " + bitmap);
     //   ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
     //   profile_img_EncodeByte = byteArrayOutputStream.toByteArray();
        //   Log.e(TAG, "onCreate byte: " + profile_img_EncodeByte);
    }

    @Override
    public void onProgressUpdate(int percentage) {
        //    Log.e(TAG, "onProgressUpdate upr: " + percentage);
        progressBars.setVisibility(View.VISIBLE);
        progressBars.setProgress(percentage);
        txtPercentage.setText(String.valueOf(percentage + "%"));
    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {
        progressBars.setVisibility(View.VISIBLE);
        progressBars.setProgress(100);
    }


    public void callApi(final int order_id, int cart_id, int quantit,String cropping_id) {

        ProgressRequestBody fileBody = new ProgressRequestBody(file, UploadImage.this);
        Log.e(TAG, "callApi file: "+file );
        Log.e(TAG, "callApi name: "+file.getAbsolutePath() );
        MultipartBody.Part filePart;
        String fileName= String.valueOf(file);
        Log.e(TAG, "callApi: "+fileName );
//
//        if (fileName.startsWith("http")) {
//            filePart = MultipartBody.Part.createFormData("image_url", fileName, fileBody);
//        }else {
//            filePart = MultipartBody.Part.createFormData("cart_image", file.getName(), fileBody);
//        }

      //  MultipartBody.Builder builder = new MultipartBody.Builder();
    //    builder.setType(MultipartBody.FORM);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body;
        if (fileName.startsWith("http")) {
            body = MultipartBody.Part.createFormData("image_url", fileName,fileBody);
         //   builder.addFormDataPart("image_url",fileName);
        }else {
            body = MultipartBody.Part.createFormData("cart_image", file.getName(), fileBody);
         //   builder.addFormDataPart("cart_image",file.getName(),RequestBody.create(MediaType.parse("image/*"), fileName));
        }
//        MultipartBody requestBody = builder.build();

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

        RequestBody id = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(cart_id));
        RequestBody paper_cropping_id = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(cropping_id));
        RequestBody orderid = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(order_id));
        RequestBody quantity = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(quantit));
        RequestBody language =RequestBody.create(MediaType.parse("multipart/form-data"),String.valueOf(language_id));
        RequestBody height =RequestBody.create(MediaType.parse("multipart/form-data"),BaseApplication.getInstance().getSession().getHeight());
        final RequestBody width =RequestBody.create(MediaType.parse("multipart/form-data"),BaseApplication.getInstance().getSession().getWidth());
        Log.e(TAG, "callApi payload: "+fileName+" id:"+cart_id+" orderid:"+order_id+" quantrity:"+quantit );

        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().upload_image( id, orderid, quantity,language, body,height,width,paper_cropping_id);
        new NetworkController().post(UploadImage.this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                if (jsonObject != null) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {
                            //
                            if (image_index < jsonArray.getJSONObject(product_index).getJSONArray("images").length()-1) {
                                image_index++;
                            }
                            else{
                                if (product_index < jsonArray.length()-1) {
                                    product_index++;
                                    image_index = 0;
                                }
                            }
                            //

                            JSONObject jsonObject2 = jsonObject1.getJSONObject("data");
                            jsonObject2.put("product_index",order_id);
                            imagesArray.put(jsonObject2);

                            BaseApplication.getInstance().getSession().setImagesArray(imagesArray.toString());
                            Log.e(TAG, "images Array: " + imagesArray.toString());

                            responseString = jsonObject1.getString("status");


                            if (uploaded_images == total_images) {
                                Intent intent = new Intent(UploadImage.this, DeliveryOptionActivity.class);
                                intent.putExtra("total_amount", getIntent().getStringExtra("total_amount"));
                           //     intent.putExtra("width",getIntent().getStringExtra("width"));
                             //   intent.putExtra("height",getIntent().getStringExtra("height"));

                                startActivity(intent);
                                overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
                                return;
                            } else {
                                uploadImage();
                                return;
                            }
                            //  return;

                        } else {

                            Log.e(TAG, "Success: "+jsonObject1.getString("message") );
                            UiHelper.showToast(UploadImage.this, jsonObject1.getString("message"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void Error(String error) {
                Log.e(TAG, "Error: "+error );
                UiHelper.showToast(UploadImage.this, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    UiHelper.showToast(UploadImage.this, getResources().getString(R.string.pls_check_your_internet));
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });

    }


}
