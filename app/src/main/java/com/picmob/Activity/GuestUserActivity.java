package com.picmob.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.gson.JsonObject;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class GuestUserActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvTitle)
    CustomTextViewBold mTitle;

    @BindView(R.id.editfirst)
    CustomEditTextBold editfirst;

    @BindView(R.id.editLast)
    CustomEditTextBold editLast;

    @BindView(R.id.editEmail)
    CustomEditTextBold editEmail;

    @BindView(R.id.editContact)
    CustomEditTextBold editContact;

    @BindView(R.id.btn_signin)
    Button btn_signin;

    @BindView(R.id.txt_counter)
    CustomTextViewBold txt_counter;

    @BindView(R.id.Iv_cart)
    ImageView Iv_cart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_guest_user);
        ButterKnife.bind(this);
        setActionBar();
    }



    private void setActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_icon_drawable);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mTitle.setText(getResources().getString(R.string.guest_user));
        btn_signin.setOnClickListener(this);
        Iv_cart.setVisibility(View.GONE);
        txt_counter.setVisibility(View.GONE);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent=new Intent(this,DefaultActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public boolean validate() {
        boolean valid = true;
        String email = editEmail.getText().toString().trim();
        String fname = editfirst.getText().toString().trim();
        String lname = editLast.getText().toString().trim();
        String contact=editContact.getText().toString().trim();
        //   String password = mEditPass.getText().toString().trim();
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
          //  editEmail.setError(getResources().getString(R.string.valid_msg_email));
            UiHelper.showToast(this,getResources().getString(R.string.valid_msg_email));
            valid = false;
        } else {
            editEmail.setError(null);
        }
        if (fname.isEmpty()) {
         //   editfirst.setError(getResources().getString(R.string.pls_enter_first_name));
            UiHelper.showToast(this,getResources().getString(R.string.pls_enter_first_name));
            valid = false;
        } else
            editfirst.setError(null);
        if (lname.isEmpty()) {
          //  editLast.setError(getResources().getString(R.string.pls_enter_lastname));
            UiHelper.showToast(this,getResources().getString(R.string.pls_enter_lastname));
            valid = false;
        } else
            editLast.setError(null);

        if (contact.isEmpty()) {
          //  editContact.setError(getResources().getString(R.string.pls_enter_contact));
            UiHelper.showToast(this,getResources().getString(R.string.pls_enter_contact));
            valid = false;
        } else {
            editContact.setError(null);
        }

        return valid;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_signin:
                if (!validate()) {
                    return;
                }
                callLoginEvent();
                break;
        }
    }

    private void callLoginEvent() {


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("device_type", Constant.DIVICE_TYPE);
        jsonObject.addProperty("device_token", BaseApplication.getInstance().getSession().getDeviceToken());
        jsonObject.addProperty("first_name", editfirst.getText().toString().trim());
        jsonObject.addProperty("last_name", editLast.getText().toString().trim());
        jsonObject.addProperty("email", editEmail.getText().toString().trim());
        jsonObject.addProperty("contact_number", editContact.getText().toString().trim());
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

        Log.e("tag", "pare login" + jsonObject.toString());
        callToLogin(jsonObject);
    }

    private void callToLogin(JsonObject jsonObject) {
       final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();
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
                            BaseApplication.getInstance().getSession().setIsLoggedIn();
                            BaseApplication.getInstance().getSession().setToken(jsonObject1.getJSONObject("data").getString("token"));
                            BaseApplication.getInstance().getSession().setProfileData(String.valueOf(jsonObject1.getJSONObject("data")));

                                Intent intent=new Intent();


                            String cartData=BaseApplication.getInstance().getSession().getCartData();
                            try {
                                JSONArray jsonArray = new JSONArray(cartData);

                                for (int i=0;i<jsonArray.length();i++){
//                            jsonArray.getJSONObject(i).getJSONArray("images").length()==0 &&
                                    if (jsonArray.getJSONObject(i).has("images")&& jsonArray.getJSONObject(i).getJSONArray("images")!=null){
                                        intent=new Intent(GuestUserActivity.this,UploadImage.class);
                                    }  else
                                        intent=new Intent(GuestUserActivity.this,DeliveryOptionActivity.class);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            intent.putExtra("total_amount",getIntent().getStringExtra("total_amount"));
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                        } else {
                            UiHelper.showToast(GuestUserActivity.this, jsonObject1.getString("message"));
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
                UiHelper.showToast(GuestUserActivity.this, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    UiHelper.showToast(GuestUserActivity.this, getResources().getString(R.string.pls_check_your_internet));
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });

    }
}
