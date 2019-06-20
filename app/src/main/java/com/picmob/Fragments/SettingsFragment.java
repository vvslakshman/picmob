package com.picmob.Fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.picmob.Activity.CommonSearchActivity;
import com.picmob.Activity.EuropeanCountryActivity;
import com.picmob.Activity.MainActivity;
import com.picmob.Adapter.PictureSizeAdapter;
import com.picmob.AppCustomView.Constant;
import com.picmob.AppCustomView.CustomEditTextBold;
import com.picmob.AppCustomView.GPSTracker;
import com.picmob.AppCustomView.ImagePicker;
import com.picmob.AppCustomView.ProgressDialog;
import com.picmob.AppCustomView.UiHelper;
import com.picmob.Networking.BaseApplication;
import com.picmob.Networking.NetworkController;
import com.picmob.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.editfirstname)
    CustomEditTextBold editfirstname;

    @BindView(R.id.editLastName)
    CustomEditTextBold editLastName;

    @BindView(R.id.editEmail)
    CustomEditTextBold editEmail;

    @BindView(R.id.editPhone)
    CustomEditTextBold editPhone;

    @BindView(R.id.editCountry)
    CustomEditTextBold editCountry;

    @BindView(R.id.editCity)
    CustomEditTextBold editCity;

    @BindView(R.id.editAddress)
    CustomEditTextBold editAddress;

    @BindView(R.id.editPostCode)
    CustomEditTextBold editPostCode;

    @BindView(R.id.btn_save)
    Button btn_save;

    @BindView(R.id.profile)
    CircularImageView profile;

    private static final String TAG ="Setting Fragment" ;
    private Double latitude;
    private Double longitude;
    private GoogleMap googleMap;
    ArrayList<String> permissionToAsk=new ArrayList<>();
    String country_id;
   //for profile image
    public final static String [] permissionForProfile = new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int requestcode_Camera_Permission = 11;
    private static final int PICK_IMAGE_ID = 10;
    Bitmap bm;
    byte[] profile_img_EncodeByte = null;
    float shipping_price=0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.layout_settings,container,false);
        ButterKnife.bind(this,view);
        setData();
        btn_save.setOnClickListener(this);
        editCountry.setOnClickListener(this);
        profile.setOnClickListener(this);
        return view;
    }

    private void setData() {
        try {
            Log.e(TAG, "setData: "+ BaseApplication.getInstance().getSession().getProfileData().toString());
            JSONObject jsonObject=new JSONObject(BaseApplication.getInstance().getSession().getProfileData());
            if (!jsonObject.optString("first_name").equalsIgnoreCase("") && jsonObject.has("first_name"))
            editfirstname.setText(jsonObject.getString("first_name"));

            if (!jsonObject.optString("last_name").equalsIgnoreCase("") && jsonObject.has("last_name"))
            editLastName.setText(jsonObject.getString("last_name"));

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
                country_id=jsonObject.getString("country_id");
            }

            if (!jsonObject.optString("post_code").equalsIgnoreCase("") && jsonObject.has("post_code"))
                editPostCode.setText(jsonObject.getString("post_code"));

         //   if (!jsonObject.optString("latitude").equalsIgnoreCase("") && jsonObject.has("latitude"))
         //   latitude=jsonObject.getDouble("latitude");

          //  if (!jsonObject.optString("longitude").equalsIgnoreCase("") && jsonObject.has("longitude"))
          //  longitude=jsonObject.getDouble("longitude");

//            if (!jsonObject.optString("image_url").equalsIgnoreCase("") && jsonObject.has("image_url"))
//            Glide.with(this).
//                    load(jsonObject.getString("image_url")).
//                    error(R.drawable.dummy)
//            .centerCrop()
//            .dontTransform()
//            .dontAnimate()
//                    .into(profile);
//
//            if (!jsonObject.optString("profile_image").equalsIgnoreCase("") && jsonObject.has("profile_image"))
//                Glide.with(this).
//                        load(jsonObject.getString("profile_image")).
//                        error(R.drawable.dummy)
//                        .into(profile);

            if (!jsonObject.optString("shipping_price").equalsIgnoreCase("") && jsonObject.has("shipping_price"))
                shipping_price=jsonObject.getInt("shipping_price");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public boolean validate() {
        boolean valid = true;
        String email = editEmail.getText().toString().trim();
        String fname=editfirstname.getText().toString().trim();
        String lname=editLastName.getText().toString().trim();
        String phone=editPhone.getText().toString().trim();
        String country=editCountry.getText().toString().trim();
        String city=editCity.getText().toString().trim();
        String address=editAddress.getText().toString().trim();
        String postcode=editPostCode.getText().toString().trim();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError(getResources().getString(R.string.valid_msg_email));
            valid = false;
        } else {
            editEmail.setError(null);
        }

        if (fname.isEmpty()) {
            editfirstname.setError(getResources().getString(R.string.pls_enter_first_name));
            valid = false;
        }else
            editfirstname.setError(null);

        if (lname.isEmpty()) {
            editLastName.setError(getResources().getString(R.string.pls_enter_lastname));
            valid = false;
        }else
            editLastName.setError(null);

//        if (phone.isEmpty()) {
//            editPhone.setError(getResources().getString(R.string.pls_enter_contact));
//            valid = false;
//        } else {
//            editPhone.setError(null);
//        }

        if (country.isEmpty()) {
            editCountry.setError(getResources().getString(R.string.pls_enter_country));
            valid = false;
        }else
            editCountry.setError(null);

        if (city.isEmpty()) {
            editCity.setError(getResources().getString(R.string.pls_enter_city));
            valid = false;
        }else
            editCity.setError(null);

        if (address.isEmpty()) {
            editAddress.setError(getResources().getString(R.string.pls_enter_address));
            valid = false;
        }else
            editAddress.setError(null);
//
//        if (postcode.isEmpty()) {
//            editPostCode.setError(getResources().getString(R.string.pls_enter_postcode));
//            valid = false;
//        }else
//            editPostCode.setError(null);
        return valid;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_save:
                if (!validate())
                    return;
                callApi();
                break;

             case R.id.editCountry:
                //showCountryDialog();
                Intent intent=new Intent(getActivity(),EuropeanCountryActivity.class);
                if (country_id!=null )
                    intent.putExtra("country_id",country_id);
                startActivityForResult(intent,255);
                break;

            case R.id.profile:
              //  getProfilePic();
                break;
        }
    }

    private void getProfilePic() {

        permissionToAsk.clear();
        for (String s : permissionForProfile) {
            if (ContextCompat.checkSelfPermission(getActivity(), s) == PackageManager.PERMISSION_DENIED)
                permissionToAsk.add(s);
        }
        if (!permissionToAsk.isEmpty())
            ActivityCompat.requestPermissions(getActivity(), permissionToAsk.toArray(new String[permissionToAsk.size()]), requestcode_Camera_Permission);
        else
        {
            Intent chooseImageIntent = ImagePicker.getPickImageIntent(getActivity());
            startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);}
    }

    private void callApi() {
        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(getActivity(), false);
         progressDialog.show();


//        Bitmap bitmap = ((BitmapDrawable) profile.getDrawable()).getBitmap();
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//      //  if (profile_img_EncodeByte!=null)
//        profile_img_EncodeByte = byteArrayOutputStream.toByteArray();



        MultipartBody.Builder builder = new MultipartBody.Builder();

        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("first_name",editfirstname.getText().toString());
        builder.addFormDataPart("last_name",editLastName.getText().toString());
        builder.addFormDataPart("email",editEmail.getText().toString());
        builder.addFormDataPart("contact_number",editPhone.getText().toString());
        builder.addFormDataPart("address",editAddress.getText().toString());
        builder.addFormDataPart("post_code",editPostCode.getText().toString());
        builder.addFormDataPart("city",editCity.getText().toString());
        builder.addFormDataPart("country",editCountry.getText().toString());
        builder.addFormDataPart("shipping_price",String.valueOf(shipping_price));
        builder.addFormDataPart("country_id",String.valueOf(country_id));
    //    builder.addFormDataPart("profile_image", "Profile_image.jpeg", RequestBody.create(MediaType.parse("image/jpeg"),profile_img_EncodeByte ));

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
        builder.addFormDataPart("language_id", String.valueOf(language_id));

        Log.e("profile byte ", "" + builder.toString());

        final RequestBody requestBody = builder.build();


//        JsonObject jsonObject=new JsonObject();
//        jsonObject.addProperty("first_name",editfirstname.getText().toString());
//        jsonObject.addProperty("last_name",editLastName.getText().toString());
//        jsonObject.addProperty("email",editEmail.getText().toString());
//        jsonObject.addProperty("contact_number",editPhone.getText().toString());
//      //  jsonObject.addProperty("latitude",latitude);
//     //   jsonObject.addProperty("longitude",longitude);
//        jsonObject.addProperty("address",editAddress.getText().toString());
//        jsonObject.addProperty("post_code",editPostCode.getText().toString());
//        jsonObject.addProperty("city",editCity.getText().toString());
//        jsonObject.addProperty("country",editCountry.getText().toString());


        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().update(BaseApplication.getInstance().getSession().getToken(), requestBody);
        new NetworkController().post(getActivity(), call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {

                progressDialog.dismiss();
                if (jsonObject != null) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {
                            Log.e(TAG, "Success DATA: "+jsonObject1.getJSONObject("data") );
                            BaseApplication.getInstance().getSession().setProfileData(String.valueOf((jsonObject1.getJSONObject("data").toString())));
                            ((MainActivity)getActivity()).setNavigationHeader();
                             UiHelper.showToast(getActivity(),getString(R.string.profile_updated));

//                            HomeFragment fragment =new HomeFragment();
//                            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//                            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
//                            fragmentTransaction.replace(R.id.frame, fragment, "");
//                            if (getIntent().hasExtra("newProduct")){
//                                Bundle bundle = new Bundle();
//                                bundle.putString("newProduct","yes");
//                                fragment.setArguments(bundle);
//                            }
//                            fragmentTransaction.commitAllowingStateLoss();

                        } else {
                            UiHelper.showToast(getActivity(), jsonObject1.getString("message"));
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
                UiHelper.showToast(getActivity(),"Error! Please try again");
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    UiHelper.showToast(getActivity(),getString(R.string.pls_check_your_internet));
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

            if (requestCode == PICK_IMAGE_ID) {
                Log.e("EditActivity", "onActivityResult: " + data);

                bm = ImagePicker.getImageFromResult(getActivity(), resultCode, data);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                profile_img_EncodeByte = baos.toByteArray();
                byte[] bitmapdata = baos.toByteArray();
                String image_str = Base64.encodeToString(bitmapdata, Base64.DEFAULT);
                byte[] encodeByte = Base64.decode(image_str, Base64.DEFAULT);

                bm = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                profile.setImageBitmap(bm);

//            if (bm != null) {
//                bm = Bitmap.createScaledBitmap(bm, 300, 300, true);
//                profile_img.setImageBitmap(bm);
//                Log.e("EditActivity", "onActivityResult bm: "+bm );
//              //  updateUserProfileImage(BaseApplication.getInstance().getSession().getID(),bm);
//            }
            }

            if (requestCode==255){
                if (data.getExtras() != null) {
                Bundle bundle1 = data.getExtras();
                if (bundle1.getString("country_name")!=null) {
                    editCountry.setText(bundle1.getString("country_name"));
                    shipping_price = Float.parseFloat(bundle1.getString("price"));
                    country_id = bundle1.getString("id");
                }

                }
            }

        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menu_filter);
        menu.clear();
    }


    private void askPermissions(){
        permissionToAsk.clear();
        for (String s : Constant.askForLocationPermission)
        {
            if (ContextCompat.checkSelfPermission(getActivity(), s) == PackageManager.PERMISSION_DENIED){
                permissionToAsk.add(s);
                Log.e("tag", "if askPermissions: " );
            }

        }
        if (!permissionToAsk.isEmpty()) {
            Log.e("tag", "if empty askPermissions: ");
            ActivityCompat.requestPermissions(getActivity(), permissionToAsk.toArray(new String[permissionToAsk.size()]), Constant.requestcodeForPermission);
        } else{
            Log.e("tag", "else access askPermissions: " );
            accessPermission();
        }
    }
    private void accessPermission() {
        GPSTracker gpsTracker=new GPSTracker(getActivity());
        if(gpsTracker.canGetLocation()){
            Log.e("tag", "if can get locationaccessPermission: " );
            Intent intent = new Intent(getActivity(), CommonSearchActivity.class);
            startActivityForResult(intent, 10001);
            getActivity(). overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        }
        else {
            Log.e("tag", "else cannot get locationaccessPermission: " );

            showDialog();

        }
    }
    private void showDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
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
                Intent intent = new Intent(getActivity(), CommonSearchActivity.class);
                startActivityForResult(intent, 10001);
                getActivity(). overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

            }
        });
        android.support.v7.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionToAsk.clear();
        if (requestCode==Constant.requestcodeForPermission){
            boolean allGranted=true;
            for (int i=0;i<grantResults.length;i++){
                if (grantResults[i]==PackageManager.PERMISSION_DENIED) {
                    Log.e("tag", "Permission denied onRequestPermissionsResult: " );
                    allGranted = false;

                }
            }
            if (allGranted){
                Log.e("tag", "All grantednRequestPermissionsResult: " );
                accessPermission();
            }
            else {
                Intent intent = new Intent(getActivity(), CommonSearchActivity.class);
                startActivityForResult(intent, 10001);
                getActivity(). overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        }
    }

}
