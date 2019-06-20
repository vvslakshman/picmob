package com.picmob.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.picmob.Adapter.PhotosAdapter;
import com.picmob.AppCustomView.CustomTextViewBold;
import com.picmob.AppCustomView.CustomTextViewNormal;
import com.picmob.AppCustomView.UiHelper;
import com.picmob.Fragments.MobFragment;
import com.picmob.Networking.BaseApplication;
import com.picmob.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotosActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "PhotosActivity";
    public static String productId = "";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tvTitle)
    CustomTextViewNormal mTitle;
    @BindView(R.id.txt_counter)
    CustomTextViewBold txt_counter;
    @BindView(R.id.Iv_cart)
    ImageView Iv_cart;
    @BindView(R.id.tv_foldername)
    CustomTextViewNormal tv_foldername;
    @BindView(R.id.tv_photos_count)
    CustomTextViewNormal tv_photos_count;
    @BindView(R.id.btn_selectAll)
    Button btn_selectAll;
    @BindView(R.id.btn_deselectAll)
    Button btn_deselectAll;
    @BindView(R.id.btn_next)
    Button btn_next;


    int int_position;

    String folder_name;

    PhotosAdapter adapter;

    String price, width, height, picture_size_id, discount, quantity;

    boolean isnewProduct = false;
    JSONArray jsonArray;
    long click_time = 0;
    long delay = 700;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_selectphotos_selectall);
        ButterKnife.bind(this);
        setActionBar();

        if (!BaseApplication.getInstance().getSession().getOkInfo().equalsIgnoreCase("Ok") && !BaseApplication.getInstance().getSession().getNeverShowDialog().equalsIgnoreCase("Never"))
            showInfoDialog();
        else if (BaseApplication.getInstance().getSession().getNeverShowDialog().equalsIgnoreCase("Never") && BaseApplication.getInstance().getSession().getOkInfo().equalsIgnoreCase("Ok")) {

        } else if (!BaseApplication.getInstance().getSession().getOkInfo().equalsIgnoreCase("Ok") && BaseApplication.getInstance().getSession().getNeverShowDialog().equalsIgnoreCase("Never")) {

        } else if (BaseApplication.getInstance().getSession().getOkInfo().equalsIgnoreCase("Ok") && !BaseApplication.getInstance().getSession().getNeverShowDialog().equalsIgnoreCase("Never")) {

        }


        picture_size_id = getIntent().getStringExtra("picture_size_id");
        price = getIntent().getStringExtra("price");
        width = getIntent().getStringExtra("width");
        height = getIntent().getStringExtra("height");
        discount = getIntent().getStringExtra("discount");
        quantity = getIntent().getStringExtra("quantity");


        isnewProduct = getIntent().getBooleanExtra("newProduct", false);
        Log.e(TAG, "onCreate:isnew " + isnewProduct);
        Log.e(TAG, "price: " + price);


        recyclerView = findViewById(R.id.gv_folder);
        int_position = getIntent().getIntExtra("value", 0);
        folder_name = getIntent().getStringExtra("foldername");
        tv_foldername.setText(folder_name);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        Log.e(TAG, "onResume adapter: ");

        Iv_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PhotosActivity.this, YourOrderActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        setPhotoCount();

        adapter = new PhotosAdapter(this, MobFragment.al_images.get(int_position).getImageList());
        recyclerView.setAdapter(adapter);

        String cartData = BaseApplication.getInstance().getSession().getCartData();
        if (!cartData.isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(cartData);
                if (jsonArray.length() > 0)
                    txt_counter.setText(String.valueOf(jsonArray.length()));
                else
                    txt_counter.setVisibility(View.INVISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else
            txt_counter.setVisibility(View.INVISIBLE);


    }

    private void setPhotoCount() {
        if (!BaseApplication.getInstance().getSession().getSelectedImages().equalsIgnoreCase("") && !BaseApplication.getInstance().getSession().getSelectedImages().isEmpty()) {
            String aa = BaseApplication.getInstance().getSession().getSelectedImages().toString();
            int counterMatching = 0;
            Log.e(TAG, "onResume: " + aa);
            try {
                jsonArray = new JSONArray(aa);
                if (productId == "") {
                    tv_photos_count.setText(String.valueOf(0));
                    Log.e(TAG, "if ID: " + productId);
                } else {
                    Log.e(TAG, "else ID: " + productId);
                    int index = Integer.parseInt(PhotosActivity.productId) - 1;
                    String count = String.valueOf(jsonArray.getJSONObject(index).getJSONArray("images_array").length());
                    tv_photos_count.setText(" " + count);

                    JSONArray imagseArray = jsonArray.getJSONObject(index).getJSONArray("images_array");
                    if (imagseArray.length() == 0) {
                        for (int j = 0; j < MobFragment.al_images.get(int_position).getImageList().size(); j++) {
                            MobFragment.al_images.get(int_position).getImageList().get(j).setSelected(false);
                        }
                    }
                    //SEt selected imgs
                    for (int j = 0; j < MobFragment.al_images.get(int_position).getImageList().size(); j++) {
                        for (int i = 0; i < imagseArray.length(); i++) {
                            if (imagseArray.getJSONObject(i).getString("image").equalsIgnoreCase(MobFragment.al_images.get(int_position).getImageList().get(j).getSingle_imagepath())) {
                                MobFragment.al_images.get(int_position).getImageList().get(j).setSelected(true);
                                counterMatching++;
                                break;
                            } else {
                                MobFragment.al_images.get(int_position).getImageList().get(j).setSelected(false);

                            }

                        }

                    }


                    if (counterMatching == MobFragment.al_images.get(int_position).getImageList().size()) {
                        btn_selectAll.setVisibility(View.GONE);
                        btn_deselectAll.setVisibility(View.VISIBLE);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            tv_photos_count.setText(" " + "0");
        }

        Log.e(TAG, "setPhotoCount bahar: ");
    }

    private void showInfoDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog_info_photos);
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        Button btn_ok = (Button) dialog.findViewById(R.id.btn_ok);
        final CheckBox checkBox_notshow = dialog.findViewById(R.id.checkob_notshow);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseApplication.getInstance().getSession().setOkInfo("Ok");
                if (checkBox_notshow.isChecked()) {
                    BaseApplication.getInstance().getSession().setNevershowDialog("Never");
                }
                dialog.dismiss();
            }
        });


    }


    private void setActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_icon_drawable);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mTitle.setText(R.string.selected_photos);
        btn_selectAll.setOnClickListener(this);
        btn_deselectAll.setOnClickListener(this);
        btn_next.setOnClickListener(this);
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

    public void setCountPlus() {
        int newcount = Integer.parseInt(tv_photos_count.getText().toString().trim()) + 1;
        tv_photos_count.setText(" " + String.valueOf(newcount));
    }


    public void setCountMinus() {
        int newcount = Integer.parseInt(tv_photos_count.getText().toString().trim()) - 1;
        tv_photos_count.setText(" " + String.valueOf(newcount));
    }

    public void setCount(int count) {
        tv_photos_count.setText(" " + String.valueOf(count));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_selectAll:
                if (adapter != null)
                    adapter.SelectAll();
                btn_selectAll.setVisibility(View.GONE);
                btn_deselectAll.setVisibility(View.VISIBLE);

                break;

            case R.id.btn_deselectAll:
                if (adapter != null)
                    adapter.DeSelectAll();
                btn_deselectAll.setVisibility(View.GONE);
                btn_selectAll.setVisibility(View.VISIBLE);
                break;

            case R.id.btn_next:
                if (SystemClock.elapsedRealtime() - click_time < delay)
                    return;
                click_time = SystemClock.elapsedRealtime();
                Intent intent = new Intent(this, SelectParametersActivity.class);
                ArrayList<String> selectedItems = adapter.getCheckedItems();
                Log.e("PhotosActivity", "onClick: " + selectedItems.size());
                JSONArray sessionImagesArray = new JSONArray();
                JSONArray imagesArray = new JSONArray();
                if (!BaseApplication.getInstance().getSession().getSelectedImages().equalsIgnoreCase("") && !BaseApplication.getInstance().getSession().getSelectedImages().isEmpty()) {

                    String aa = BaseApplication.getInstance().getSession().getSelectedImages();
                    int index;
                    if (productId != "") {
                        index = Integer.parseInt(productId) - 1;
                        try {
                            sessionImagesArray = new JSONArray(aa);
                            imagesArray = sessionImagesArray.getJSONObject(index).getJSONArray("images_array");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                    }
                }
                if (imagesArray.length() == 0 && selectedItems.size() == 0)
                    UiHelper.showToast(this, getString(R.string.pls_Select_at_least_one_picture));
                else {
                    saveToSession();
                    intent.putExtra("price", price);
                    intent.putExtra("width", width);
                    intent.putExtra("height", height);
                    intent.putExtra("picture_size_id", picture_size_id);
                    intent.putExtra("discount", discount);
                    intent.putExtra("quantity", quantity);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        saveToSession();
        super.onBackPressed();
    }

    public void saveToSession() {

        ArrayList<String> selectedItems = adapter.getCheckedItems();
        if (selectedItems.size() == 0)
            return;

        JSONArray sessionProductsArray = new JSONArray();
        JSONArray sessionImagesArray = new JSONArray();

        JSONObject productObject = null;

        String aa = BaseApplication.getInstance().getSession().getSelectedImages();

        Log.e(TAG, "aa string: " + aa);

        int productIndex = 0;

        if (!aa.isEmpty()) {
            try {
                sessionProductsArray = new JSONArray(aa);

                if (productId != "") {
                    //For edit
                    //Find the product in product array
                    int index = Integer.parseInt(productId) - 1;
                    productObject = sessionProductsArray.getJSONObject(index);
                    sessionImagesArray = sessionProductsArray.getJSONObject(index).getJSONArray("images_array");
                    productIndex = index;
                } else {

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JSONObject jsonObject = new JSONObject();
        boolean matched = false;

        if (sessionImagesArray.length() == 0) {
            Log.e(TAG, "Session array is empty ");


            for (int i = 0; i < selectedItems.size(); i++) {


                JSONObject jsonObject1 = new JSONObject();
                try {
                    jsonObject1.put("image", selectedItems.get(i));
                    jsonObject1.put("quantity", 1);
                    sessionImagesArray.put(jsonObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } else {
            Log.e(TAG, "Session array is not empty ");


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
                        JSONObject jsonObject1 = new JSONObject();
                        Log.e(TAG, "Match not found and adding new item ");
                        jsonObject1.put("image", selectedItems.get(i));
                        jsonObject1.put("quantity", 1);
                        sessionImagesArray.put(jsonObject1);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }


        try {

            if (productObject == null) {
                productObject = new JSONObject();
                productObject.put("id", sessionProductsArray.length() + 1);
                //its a first product so product id should be same
                productId = String.valueOf(sessionProductsArray.length() + 1);
                productIndex = Integer.parseInt(productId) - 1;
            }
            productObject.put("images_array", sessionImagesArray);
            sessionProductsArray.put(productIndex, productObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "SAving session data: " + sessionProductsArray.toString());
        BaseApplication.getInstance().getSession().setSelectedImages(sessionProductsArray.toString());
    }


    @SuppressLint("NewApi")
    public void deSelectedImage(String imagePath) {

        JSONArray sessionImagesArray = new JSONArray();
        JSONArray sessionProductArray = new JSONArray();
        String aa = BaseApplication.getInstance().getSession().getSelectedImages();

        if (!aa.isEmpty()) {
            try {
                sessionProductArray = new JSONArray(aa);

                if (productId.equalsIgnoreCase(""))
                    return;

                int index = Integer.parseInt(productId) - 1;

                sessionImagesArray = sessionProductArray.getJSONObject(index).getJSONArray("images_array");

                for (int i = 0; i < sessionImagesArray.length(); i++) {

                    if (sessionImagesArray.getJSONObject(i).getString("image").equalsIgnoreCase(imagePath)) {
                        sessionImagesArray.remove(i);
                        sessionProductArray.getJSONObject(index).put("images_array", sessionImagesArray);
                        BaseApplication.getInstance().getSession().setSelectedImages(sessionProductArray.toString());
                        break;

                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }


    @SuppressLint("NewApi")
    public void deSelectedAllImage(String imagePath) {

        JSONArray sessionImagesArray = new JSONArray();
        JSONArray sessionProductArray = new JSONArray();
        String aa = BaseApplication.getInstance().getSession().getSelectedImages();

        if (!aa.isEmpty()) {
            try {
                sessionProductArray = new JSONArray(aa);

                if (productId.equalsIgnoreCase(""))
                    return;

                int index = Integer.parseInt(productId) - 1;

                sessionImagesArray = sessionProductArray.getJSONObject(index).getJSONArray("images_array");
                for (int i = 0; i < sessionImagesArray.length(); i++) {

                    if (sessionImagesArray.getJSONObject(i).getString("image").equalsIgnoreCase(imagePath)) {
                        sessionImagesArray.remove(i);
                        sessionProductArray.getJSONObject(index).put("images_array", sessionImagesArray);
                        BaseApplication.getInstance().getSession().setSelectedImages(sessionProductArray.toString());
                        break;

                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }


    public void ShowDeselectButton() {
        btn_deselectAll.setVisibility(View.GONE);
        btn_selectAll.setVisibility(View.VISIBLE);
    }
}

