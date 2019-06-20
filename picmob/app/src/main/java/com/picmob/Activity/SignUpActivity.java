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

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class SignUpActivity extends BaseActivity implements View.OnClickListener {

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

    @BindView(R.id.editPass)
    CustomEditTextBold editPass;

    @BindView(R.id.editConfirmPass)
    CustomEditTextBold editConfirmPass;

    @BindView(R.id.btn_signin)
    Button btn_signin;

    @BindView(R.id.Iv_cart)
    ImageView Iv_cart;

    @BindView(R.id.txt_counter)
    CustomTextViewBold txt_counter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_signup_activity);
        ButterKnife.bind(this);
        setActionBar();
    }


    private void setActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_icon_drawable);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mTitle.setText(getResources().getString(R.string.sign_up));
        btn_signin.setOnClickListener(this);
        Iv_cart.setVisibility(View.GONE);
        txt_counter.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_signin:
                validate();
                break;
        }
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

    public void validate() {
        boolean valid = true;
        String email = editEmail.getText().toString().trim();
        String fname = editfirst.getText().toString().trim();
        String lname = editLast.getText().toString().trim();
        String contact = editContact.getText().toString().trim();
        String password = editPass.getText().toString().trim();
        String confirmpass = editConfirmPass.getText().toString().trim();


        if (fname.isEmpty()) {
            //  editfirst.setError(getResources().getString(R.string.pls_enter_first_name));
            UiHelper.showToast(this, getResources().getString(R.string.pls_enter_first_name));
            valid = false;
        } else if (lname.isEmpty()) {
            //   editLast.setError(getResources().getString(R.string.pls_enter_lastname));
            UiHelper.showToast(this, getResources().getString(R.string.pls_enter_lastname));
            valid = false;
        } else if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            //    editEmail.setError(getResources().getString(R.string.valid_msg_email));
            UiHelper.showToast(this, getResources().getString(R.string.valid_msg_email));
            valid = false;
        } else if (contact.isEmpty()) {
//            editContact.setError(getResources().getString(R.string.pls_enter_contact));
            UiHelper.showToast(this, getResources().getString(R.string.pls_enter_contact));
            valid = false;
        }

//        else if (contact.length()<10){
//
//            UiHelper.showToast(this,getResources().getString(R.string.phone_number_must));
//        }

        else if (password.isEmpty()) {
            // editPass.setError(getResources().getString(R.string.pls_enter_password));
            UiHelper.showToast(this, getResources().getString(R.string.pls_enter_password));
            valid = false;
        } else if (password.length() < 6) {
            UiHelper.showToast(this, getResources().getString(R.string.password_must_be_minimum));
        } else if (confirmpass.isEmpty()) {
            //    editConfirmPass.setError(getResources().getString(R.string.pls_enter_new_pass));
            UiHelper.showToast(this, getResources().getString(R.string.pls_enter_new_pass));
            valid = false;
        } else if (!password.equalsIgnoreCase(confirmpass)) {
            //    editConfirmPass.setError(getResources().getString(R.string.new_password_and_confirm_notmatch));
            UiHelper.showToast(this, getResources().getString(R.string.new_password_and_confirm_notmatch));
            valid = false;
        } else {
            callRegisterEvent();

        }
    }

    private void callRegisterEvent() {
//        if (!validate()) {
//            return;
//        }
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("device_token", BaseApplication.getInstance().getSession().getDeviceToken());
        jsonObject.addProperty("device_type", Constant.DIVICE_TYPE);
        jsonObject.addProperty("email", editEmail.getText().toString().trim());
        jsonObject.addProperty("password", editPass.getText().toString().trim());
        jsonObject.addProperty("confirm_password", editConfirmPass.getText().toString().trim());
        jsonObject.addProperty("contact_number", editContact.getText().toString().trim());
        jsonObject.addProperty("first_name", editfirst.getText().toString().trim());
        jsonObject.addProperty("last_name", editLast.getText().toString().trim());
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
        Log.e("tag", "pare login" + jsonObject.toString());
        callToRegister(jsonObject);
    }

    private void callToRegister(JsonObject jsonObject) {
        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();
        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().register(jsonObject);
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
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                            Log.e("tagdata login", " " + jsonObject1.getJSONObject("data"));
                        } else {
                            UiHelper.showToast(SignUpActivity.this, jsonObject1.getString("message"));
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
                UiHelper.showToast(SignUpActivity.this, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    UiHelper.showToast(SignUpActivity.this, getResources().getString(R.string.pls_check_your_internet));
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });

    }


}
