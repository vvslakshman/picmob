package com.picmob.Activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
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

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class LoginActivity extends BaseActivity implements View.OnClickListener, FacebookCallback<LoginResult> {

    private static final String TAG = "LoginActivity";
    static String message;
    @BindView(R.id.btn_fb)
    Button btn_fb;
    @BindView(R.id.btn_signin)
    Button btn_signin;
    @BindView(R.id.btn_insta)
    Button btn_insta;
    @BindView(R.id.btn_guest)
    Button tv_guest;
    @BindView(R.id.editPass)
    CustomEditTextBold editPass;
    @BindView(R.id.editEmail)
    CustomEditTextBold editEmail;
    @BindView(R.id.txt_forgot)
    CustomTextViewBold txt_forgot;
    @BindView(R.id.txt_signup)
    CustomTextViewBold txt_signup;
    CallbackManager callbackManager;
    LoginManager loginManager;
    String firstName = "";
    String lastName = "";
    String email = "";
    String profileURL = "";
    String social_id = "";
    ProgressDialog progressDialog;
    String access_token, username, insta_id;

    public static void openLoginAcitivity(Context context, String message1) {
        message = message1;
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);
        btn_fb.setOnClickListener(this);
        btn_signin.setOnClickListener(this);
        tv_guest.setOnClickListener(this);
        btn_insta.setOnClickListener(this);
        txt_forgot.setOnClickListener(this);
        txt_signup.setOnClickListener(this);
        callbackManager = CallbackManager.Factory.create();
        loginManager = com.facebook.login.LoginManager.getInstance();
        loginManager.registerCallback(callbackManager, this);

        //checkForInstagram();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_fb:
                facebookLogin();
                break;

            case R.id.btn_signin:
                validate();

                break;
            case R.id.btn_guest:
                Intent intent1 = new Intent(this, DefaultActivity.class);
                startActivity(intent1);
                finish();
                break;

            case R.id.btn_insta:
                // signInWithInstagram();
                Intent intent = new Intent(this, InstagramWebView.class);
                startActivityForResult(intent, 215);
                break;

            case R.id.txt_forgot:
                Intent intent2 = new Intent(this, EmailIdActivity.class);
                startActivity(intent2);
                break;

            case R.id.txt_signup:
                Intent intent3 = new Intent(this, SignUpActivity.class);
                startActivity(intent3);
                break;

        }
    }

    //facebook
    private void facebookLogin() {
        loginManager.logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
    }

    @Override
    public void onSuccess(final LoginResult loginResult) {
        Log.e("bug", "facebook sign in success");

        if (loginResult != null) {

            Profile profile = Profile.getCurrentProfile();

            if (profile != null) {
                firstName = profile.getFirstName();
                lastName = profile.getLastName();
                social_id = profile.getId();
                profileURL = String.valueOf(profile.getProfilePictureUri(200, 200));
                Log.e(TAG, "social Id: " + profileURL);
            }

            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    Log.e("bug", "facebook social_id=>" + social_id + " toeken" + loginResult.getAccessToken().getToken());
                    Log.e("bug", "graph response=>" + object.toString());
                    try {
                        if (object.has("name")) {
                            String[] name = object.getString("name").split(" ");
                            firstName = name[0];
                            lastName = name[1];
                        }
                        email = object.has("email") ? object.getString("email") : "";
                        social_id = object.has("id") ? object.getString("id") : "";
//                        callApi
                        //  socialSignUp("facebook", loginResult.getAccessToken().getToken());
                        socialSignUp("facebook");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,first_name,name,last_name,email,gender,birthday");
            request.setParameters(parameters);
            request.executeAsync();
        }
    }

    @Override
    public void onCancel() {
        Log.e("bug", "facebook sign in connection cancel");
    }

    @Override
    public void onError(FacebookException error) {
        UiHelper.showToast(this, "An error occur during Facebook Login");
        Log.e("bug", "facebook sign in error" + error.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 215) {
                Bundle bundle = data.getExtras();
                if (bundle.getString("access_token") != null) {
                    Log.e(TAG, "onActivityResult: ");
                    access_token = bundle.getString("access_token");
                    BaseApplication.getInstance().getSession().setAccessToken(access_token);
                    username = bundle.getString("username");
                    social_id = bundle.getString("id");
                    firstName = bundle.getString("full_name");
                    profileURL = bundle.getString("profile_picture");
                    socialSignUp("instagram");
                }

            }
        }
    }

    public boolean validate() {
        boolean valid = true;
        String email = editEmail.getText().toString().trim();
        String pass = editPass.getText().toString().trim();
        //   String password = mEditPass.getText().toString().trim();
        if (email.isEmpty()) {
            //  editEmail.setError(getResources().getString(R.string.valid_msg_email));
            UiHelper.showToast(this, getResources().getString(R.string.pls_enter_email));
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            UiHelper.showToast(this, getResources().getString(R.string.pls_enter_correct_email));
            valid = false;
        } else if (pass.isEmpty()) {
            UiHelper.showToast(this, getResources().getString(R.string.pls_enter_password));
            valid = false;
        } else if (pass.length() < 6) {
            UiHelper.showToast(this, getResources().getString(R.string.password_must_be_minimum));
        } else {
            callLoginEvent();
        }


        return valid;
    }

    private void callLoginEvent() {
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

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("device_type", Constant.DIVICE_TYPE);
        jsonObject.addProperty("device_token", BaseApplication.getInstance().getSession().getDeviceToken());
        jsonObject.addProperty("email", editEmail.getText().toString().trim());
        jsonObject.addProperty("password", editPass.getText().toString().trim());
        jsonObject.addProperty("language_id", language_id);

        Log.e("tag", "pare login" + jsonObject.toString());
        callToLogin(jsonObject);
    }

    private void callToLogin(JsonObject jsonObject) {
        progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();
        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().login(jsonObject);
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
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                            Log.e("tagdata login", " " + jsonObject1.getJSONObject("data"));
                        } else {
                            UiHelper.showToast(LoginActivity.this, jsonObject1.getString("message"));
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
                UiHelper.showToast(LoginActivity.this, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    UiHelper.showToast(LoginActivity.this, getResources().getString(R.string.pls_check_your_internet));
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });

    }

    private void socialSignUp(String type) {
        progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("device_type", Constant.DIVICE_TYPE);
        jsonObject.addProperty("device_token", BaseApplication.getInstance().getSession().getDeviceToken());
        jsonObject.addProperty("first_name", firstName);
        jsonObject.addProperty("last_name", lastName);
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("email", email);
        jsonObject.addProperty("social_id", social_id);
        jsonObject.addProperty("account_type", type);
        jsonObject.addProperty("image_url", profileURL);

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

        Log.e(TAG, "socialSignUp: " + jsonObject.toString());
        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().socialLogin(jsonObject);
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
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            Log.e("tagdata login", " " + jsonObject1.getJSONObject("data"));
                        } else {
                            UiHelper.showToast(LoginActivity.this, jsonObject1.getString("message"));
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
                UiHelper.showToast(LoginActivity.this, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    UiHelper.showToast(LoginActivity.this, getResources().getString(R.string.pls_check_your_internet));
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });

    }


}
