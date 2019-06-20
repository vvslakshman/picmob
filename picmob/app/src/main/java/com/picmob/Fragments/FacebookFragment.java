package com.picmob.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.picmob.AppCustomView.UiHelper;
import com.picmob.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FacebookFragment extends Fragment implements FacebookCallback<LoginResult> {

    @BindView(R.id.btn_fb)
    Button button;
    private static final String TAG ="FacebookFragment" ;
    LoginManager loginManager;
    CallbackManager callbackManager;


    String firstName = "";
    String lastName = "";
    String email = "";
    String profileURL = "";
    String social_id = "";

    private ArrayList<String> alFBAlbum = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facebook, container, false);
        ButterKnife.bind(this, view);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebookLogin();
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getContext());
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        callbackManager = CallbackManager.Factory.create();
        loginManager = com.facebook.login.LoginManager.getInstance();
        loginManager.registerCallback(callbackManager, FacebookFragment.this);

    }

    //facebook
    private void facebookLogin() {
        loginManager.logInWithReadPermissions(this, Arrays.asList("email", "public_profile","user_photos"));
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

//            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
//                @Override
//                public void onCompleted(JSONObject object, GraphResponse response) {
//                    Log.e("bug", "facebook social_id=>" + social_id + " toeken" + loginResult.getAccessToken().getToken());
//                    Log.e("bug", "graph response=>" + object.toString());
//                    try {
//                        if (object.has("name")) {
//                            String[] name = object.getString("name").split(" ");
//                            firstName = name[0];
//                            lastName = name[1];
//                        }
//                        email = object.has("email") ? object.getString("email") : "";
//                        social_id = object.has("id") ? object.getString("id") : "";
//
//
//                        if (response.getError() == null) {
//                            JSONObject joMain = response.getJSONObject();
//                            Log.e(TAG, "onCompleted main: "+joMain.toString() );
//                            if (joMain.has("data")) {
//                                JSONArray jaData = joMain.optJSONArray("data"); //find JSONArray from JSONObject
//                                alFBAlbum = new ArrayList<>();
//                                for (int i = 0; i < jaData.length(); i++) {//find no. of album using jaData.length()
//                                    JSONObject joAlbum = jaData.getJSONObject(i); //convert perticular album into JSONObject
//                                //    GetFacebookImages(joAlbum.optString("id")); //find Album ID and get All Images from album
//                                    Log.e(TAG, "onCompleted Album: "+joAlbum.toString() );
//                                }
//                            }
//                        } else {
//                            Log.d("Test", response.getError().toString());
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//
//            Bundle parameters = new Bundle();
//            parameters.putString("fields", "id,first_name,name,last_name,email,gender,birthday");
//            request.setParameters(parameters);
//            request.executeAsync();
//        }


            new GraphRequest(
                    loginResult.getAccessToken(),  //your fb AccessToken
                    "/" + loginResult.getAccessToken().getUserId() + "/albums",//user id of login user
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            Log.e("TAG", "Facebook Albums: " + response.toString());
                            try {
                                if (response.getError() == null) {
                                    JSONObject joMain = response.getJSONObject(); //convert GraphResponse response to JSONObject
                                    Log.e(TAG, "onCompleted: " + joMain);
                                    if (joMain.has("data")) {
                                        JSONArray jaData = joMain.optJSONArray("data"); //find JSONArray from JSONObject
                                        alFBAlbum = new ArrayList<>();
                                        for (int i = 0; i < jaData.length(); i++) {//find no. of album using jaData.length()
                                            JSONObject joAlbum = jaData.getJSONObject(i); //convert perticular album into JSONObject
                                            //    GetFacebookImages(joAlbum.optString("id")); //find Album ID and get All Images from album
                                        }
                                    }
                                } else {
                                    Log.e   ("Test", response.getError().toString());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            ).executeAsync();
        }
    }

    @Override
    public void onCancel() {
        Log.e("bug", "facebook sign in connection cancel");
    }

    @Override
    public void onError(FacebookException error) {
        UiHelper.showToast(getActivity(), "An error occur during Facebook Login");
        Log.e("bug", "facebook sign in error" + error.toString());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);


    }
}
