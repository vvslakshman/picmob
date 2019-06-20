package com.picmob.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.JsonObject;
import com.picmob.Adapter.FrameColorAdapter;
import com.picmob.AppCustomView.Constant;
import com.picmob.AppCustomView.CustomTextViewBold;
import com.picmob.AppCustomView.ProgressDialog;
import com.picmob.AppCustomView.UiHelper;
import com.picmob.Models.FrameColorModel;
import com.picmob.Networking.BaseApplication;
import com.picmob.Networking.NetworkController;
import com.picmob.R;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class EmailIdActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvTitle)
    CustomTextViewBold mTitle;

    @BindView(R.id.ed_email)
    EditText ed_email;

    @BindView(R.id.btn_send)
    Button btn_send;

    @BindView(R.id.txt_counter)
    CustomTextViewBold txt_counter;

    @BindView(R.id.Iv_cart)
    ImageView Iv_cart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_enter_emailid);
        ButterKnife.bind(this);
        setActionBar();
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=ed_email.getText().toString();
                if (email.isEmpty()  ) {
                    UiHelper.showToast(EmailIdActivity.this,getResources().getString(R.string.pls_enter_email));
                }
                else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){

                    UiHelper.showToast(EmailIdActivity.this,getResources().getString(R.string.pls_enter_correct_email));
                }
                else {
                   callApi();
                }

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
        mTitle.setText(getResources().getString(R.string.send_otp));
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

    public void callApi() {
        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("email",ed_email.getText().toString());
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
        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().forgot_password(jsonObject);
        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                if (jsonObject != null) {
                    try {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        final JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {

                            UiHelper.showToast(EmailIdActivity.this,jsonObject1.getString("message"));
                            Intent intent=new Intent(EmailIdActivity.this,FillOTP.class);
                            intent.putExtra("email",ed_email.getText().toString());
                            startActivity(intent);

                        } else {
                            UiHelper.showToast(EmailIdActivity.this, jsonObject1.getString("message"));
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
                UiHelper.showToast(EmailIdActivity.this, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    UiHelper.showToast(EmailIdActivity.this, getResources().getString(R.string.pls_check_your_internet));
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });

    }
}
