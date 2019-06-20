package com.picmob.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.CallbackManager;
import com.picmob.Activity.InstagramWebView;
import com.picmob.Activity.PhotosActivity;
import com.picmob.Activity.SelectParametersActivity;
import com.picmob.Adapter.InstagramAdapter;
import com.picmob.AppCustomView.UiHelper;
import com.picmob.Models.Model_images;
import com.picmob.Networking.BaseApplication;
import com.picmob.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.facebook.FacebookSdk.getApplicationContext;

public class InstagramFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.btn_insta)
    Button btn_insta;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.btn_next)
    Button btn_next;

    private static final String TAG ="InstaFRag" ;
    CallbackManager callbackManager;

    String access_token,username,insta_id;
    String firstName = "";
    String lastName = "";
    String email = "";
    String profileURL = "";
    String social_id = "";
    String thumbnail="";

    InstagramAdapter instagramAdapter;
    public ArrayList<Model_images> arrayList = new ArrayList<>();
    Model_images model_images;

    JSONArray dataArray=new JSONArray();


    String price,width,height,picture_size_id;
    boolean isnewProduct=false;

    private static final String client_id = "37677046d01940ebb9c2e85f6f7efdd9";
    private static final String AUTHURL = "https://api.instagram.com/oauth/authorize/";
    private static final String TOKENURL = "https://api.instagram.com/oauth/access_token";
    public static final String APIURL = "https://api.instagram.com/v1";
    public static String CALLBACKURL = "https://www.xtreemsolution.com";
    String authURLString = AUTHURL + "?client_id=" + client_id + "&redirect_uri=" + CALLBACKURL + "&response_type=code&display=touch&scope=likes+comments+relationships";
    private static final String client_secret = "dc7b887ecf0b4359bd35cda409340bee";
    String tokenURLString = TOKENURL + "?client_id=" + client_id + "&client_secret=" + client_secret + "&redirect_uri=" + CALLBACKURL + "&grant_type=authorization_code";



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_instagram, container, false);
        ButterKnife.bind(this, view);
        callbackManager = CallbackManager.Factory.create();

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        btn_insta.setOnClickListener(this);
        btn_next.setOnClickListener(this);

        Bundle bundle = this.getArguments();
        price = bundle.getString("price");
        width = bundle.getString("width");
        height = bundle.getString("height");
        picture_size_id=bundle.getString("picture_size_id");
        isnewProduct=bundle.getBoolean("newProduct");

        if (!BaseApplication.getInstance().getSession().getAccessToken().equalsIgnoreCase("")){

             access_token=BaseApplication.getInstance().getSession().getAccessToken();
            btn_insta.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            btn_next.setVisibility(View.VISIBLE);

            new ddd().execute();
        }
        else {

            btn_insta.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            btn_next.setVisibility(View.GONE);
        }



        return view;
    }

    public void call(){

        Intent intent=new Intent(getActivity(),InstagramWebView.class);
        startActivityForResult(intent,15);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 15) {
                Bundle bundle = data.getExtras();
                if (bundle.getString("access_token") != null) {
                    access_token = bundle.getString("access_token");
                    username=bundle.getString("username");
                    social_id=bundle.getString("id");
                    firstName=bundle.getString("full_name");
                    profileURL=bundle.getString("profile_picture");
                    thumbnail=bundle.getString("thumbnail");
                 //   Log.e(TAG, "thumbnails: "+thumbnail );
                    JSONArray jsonArray=new JSONArray();
                    try {
                         jsonArray=new JSONArray(thumbnail);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    BaseApplication.getInstance().getSession().setAccessToken(access_token);
                    btn_insta.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    btn_next.setVisibility(View.VISIBLE);

                    ArrayList<String> stringArrayList=new ArrayList<>();
                    stringArrayList.add(thumbnail);
                    Log.e(TAG, "array list: "+stringArrayList );




                    model_images = new Model_images();
                    model_images.initializeModel(jsonArray);
                    arrayList = model_images.arrayList;

                    instagramAdapter = new InstagramAdapter(getActivity(),arrayList);
                    recyclerView.setAdapter(instagramAdapter);

                }

            }
        }
    }


    public class ddd extends AsyncTask {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            Log.e(TAG, "onPreExecute: " );
            super.onPreExecute();
        }
        @Override
        protected Object doInBackground(Object[] objects) {
            Log.e(TAG, "doInBackground: " );

            try {
                URL example = new URL("https://api.instagram.com/v1/users/self/media/recent?access_token="
                        + access_token);

                URLConnection tc = example.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        tc.getInputStream()));

                String line;
                while ((line = in.readLine()) != null) {
                    JSONObject ob = new JSONObject(line);

                    dataArray = ob.getJSONArray("data");
                    Log.e(TAG, "doInBackground: "+dataArray.toString() );


                    model_images = new Model_images();
                    model_images.initializeModel(dataArray);
                    arrayList = model_images.arrayList;
                    Log.e(TAG, "doInBackground ArrayList: "+arrayList.toString());

                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            Log.e(TAG, "onPostExecute: " );
            instagramAdapter = new InstagramAdapter(getActivity(),arrayList);
            recyclerView.setAdapter(instagramAdapter);
            setPhotoCount();
            super.onPostExecute(o);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.btn_insta:
                call();
                break;

            case R.id.btn_next:
                Intent intent = new Intent(getActivity(), SelectParametersActivity.class);
                ArrayList<String> selectedItems = instagramAdapter.getCheckedItems();
                Log.e("PhotosActivity", "onClick: "+selectedItems.size());
                JSONArray sessionImagesArray=new JSONArray();
                //     JSONArray imagesArray=new JSONArray();
                if (!BaseApplication.getInstance().getSession().getSelectedImages().equalsIgnoreCase("") && !BaseApplication.getInstance().getSession().getSelectedImages().isEmpty()) {

                    String aa=BaseApplication.getInstance().getSession().getSelectedImages();
                    try {
                        sessionImagesArray=new JSONArray(aa);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (sessionImagesArray.length()==0 && selectedItems.size()==0)
                    UiHelper.showToast(getActivity(),"Please select at least one Picture");
                else {
                    saveToSession();
                    intent.putExtra("price",price);
                    intent.putExtra("width",width);
                    intent.putExtra("height",height);
                    intent.putExtra("picture_size_id",picture_size_id);
                    startActivity(intent);
                   getActivity(). overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
                }
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: " );

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
        setPhotoCount();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy: ");
        if (instagramAdapter!=null) {
            if (instagramAdapter.getCheckedItems() == null && instagramAdapter.getCheckedItems().size() == 0)
                return;
            else
                saveToSession();
        }
        super.onDestroy();
    }

    public void saveToSession(){
        ArrayList<String> selectedItems = instagramAdapter.getCheckedItems();

        if (selectedItems.size()==0)
            return;

        JSONArray sessionProductsArray = new JSONArray();
        JSONArray sessionImagesArray = new JSONArray();

        JSONObject productObject = null;

        String aa=BaseApplication.getInstance().getSession().getSelectedImages();

        Log.e(TAG, "aa string: "+aa );

        int productIndex = 0;

        if (!aa.isEmpty()) {
            try {
                sessionProductsArray = new JSONArray(aa);

                if(PhotosActivity.productId!=""){
                    //For edit
                    //Find the product in product array
//                  for (int x=0; x <sessionProductsArray.length(); x++){

//                      if(sessionProductsArray.getJSONObject(x).getString("id").equalsIgnoreCase(productId)){
                    int index = Integer.parseInt(PhotosActivity.productId)-1;
                    productObject = sessionProductsArray.getJSONObject(index);
                    sessionImagesArray = sessionProductsArray.getJSONObject(index).getJSONArray("images_array");
                    productIndex = index;
//                      }
//                  }

                }
                else {
                    //get Top array
//                  if (sessionProductsArray.length()>0){
//                      sessionImagesArray = sessionProductsArray.getJSONObject(0).getJSONArray("images_array");
//                      productId = "1";
//                  }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JSONObject jsonObject = new JSONObject();
        boolean matched=false;

        if (sessionImagesArray.length()==0  ) {
            Log.e(TAG, "Session array is empty " );


            for (int i = 0; i < selectedItems.size(); i++) {


                JSONObject jsonObject1 = new JSONObject();
                try {
                    jsonObject1.put("image", selectedItems.get(i));
                    sessionImagesArray.put(jsonObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
        else {
            Log.e(TAG, "Session array is not empty " );


            for (int i = 0; i < selectedItems.size(); i++) {
                matched = false;
                for (int j = 0; j < sessionImagesArray.length(); j++) {

                    try {

                        if (sessionImagesArray.getJSONObject(j).getString("image").equalsIgnoreCase(selectedItems.get(i))) {
                            matched = true;
                            Log.e(TAG, "Match found ");
                            break;
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                if (!matched) {
                    try {
                        Log.e(TAG, "Match not found and adding new item ");
                        jsonObject.put("image", selectedItems.get(i));
                        sessionImagesArray.put(jsonObject);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }



        try {

            if(productObject==null){
                productObject = new JSONObject();
                productObject.put("id",sessionProductsArray.length()+1);
                //its a first product so product id should be same
                PhotosActivity. productId = String.valueOf(sessionProductsArray.length()+1);
                productIndex = Integer.parseInt(PhotosActivity.productId)-1;
            }
            productObject.put("images_array",sessionImagesArray);
            sessionProductsArray.put(productIndex,productObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "SAving session data: "+sessionProductsArray.toString());
        BaseApplication.getInstance().getSession().setSelectedImages(sessionProductsArray.toString());
    }



    @SuppressLint("NewApi")
    public static void deSelectedImage(String imagePath){

        JSONArray sessionImagesArray = new JSONArray();
        JSONArray sessionProductArray = new JSONArray();
        String aa=BaseApplication.getInstance().getSession().getSelectedImages();

        if (!aa.isEmpty()) {
            try {
                sessionProductArray = new JSONArray(aa);

                int index = Integer.parseInt(PhotosActivity.productId)-1;

                sessionImagesArray = sessionProductArray.getJSONObject(index).getJSONArray("images_array");

                for (int i=0;i<sessionImagesArray.length();i++){

                    if (sessionImagesArray.getJSONObject(i).getString("image").equalsIgnoreCase(imagePath)){
                        sessionImagesArray.remove(i);
                        sessionProductArray.getJSONObject(index).put("images_array",sessionImagesArray);
                        BaseApplication.getInstance().getSession().setSelectedImages(sessionProductArray.toString());
                        break;

                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void setPhotoCount() {
        if (!BaseApplication.getInstance().getSession().getSelectedImages().equalsIgnoreCase("") && !BaseApplication.getInstance().getSession().getSelectedImages().isEmpty() && !isnewProduct) {
            String aa = BaseApplication.getInstance().getSession().getSelectedImages().toString();

            try {
            JSONArray    jsonArray=new JSONArray(aa);
                if(PhotosActivity.productId=="")
                {}

                else  {
                    int index = Integer.parseInt(PhotosActivity.productId) - 1;
                    Log.e(TAG, "arraylist ki size: "+arrayList.size() );
                    JSONArray imagseArray = jsonArray.getJSONObject(index).getJSONArray("images_array");
                    Log.e(TAG, "setPhotoCount imgesArry: "+imagseArray.toString() );
                    //SEt selected imgs

                    for (int i = 0; i < imagseArray.length(); i++) {
                        Log.e(TAG, "setPhotoCount arraylist: "+arrayList.size());
                        for (int j = 0; j <arrayList.size(); j++) {

                            if (imagseArray.getJSONObject(i).getString("image").equalsIgnoreCase(arrayList.get(j).url)) {
                                arrayList.get(j).setSelected(true);
                                break;
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }



}
