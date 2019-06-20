package com.picmob.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.picmob.Adapter.FitPaperAdapterOverview;
import com.picmob.Adapter.FitPaperAdapterOverviewNew;
import com.picmob.AppCustomView.CustomTextViewBold;
import com.picmob.AppCustomView.CustomTextViewNormal;
import com.picmob.AppCustomView.UiHelper;
import com.picmob.Models.PhotoOverviewModel;
import com.picmob.Networking.BaseApplication;
import com.picmob.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotosOverviewActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "PhotosOverviewActivity";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvTitle)
    CustomTextViewBold mTitle;

    @BindView(R.id.txt_quantity)
    CustomTextViewNormal txt_quantity;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.btn_addPhotos)
    Button btn_addPhotos;

    @BindView(R.id.btn_next)
    Button btn_next;

    @BindView(R.id.txt_counter)
    CustomTextViewBold txt_counter;

    @BindView(R.id.Iv_cart)
    ImageView Iv_cart;

    FitPaperAdapterOverviewNew fitPaperAdapterOverview;
    ArrayList<PhotoOverviewModel> arrayListForSelectedImages;
    PhotoOverviewModel photoOverviewModel;
    String paper_cropping_type, paper_name;
    JSONArray jsonArray;
    int photos_quantity = 0;
    long click_time = 0;
    long delay = 700;
    String paper_cropping_id;
    private float ww,hh;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_photos_overview);
        ButterKnife.bind(this);
        setActionBar();

        JSONArray imagesArray = new JSONArray();
        String aa = BaseApplication.getInstance().getSession().getSelectedImages().toString();
        Log.e(TAG, "aa array: " + aa);
        Log.e(TAG, "aa array index: " + PhotosActivity.productId);
        try {
            jsonArray = new JSONArray(aa);


            if (PhotosActivity.productId.equalsIgnoreCase(""))
                return;

            int index = Integer.parseInt(PhotosActivity.productId) - 1;
            imagesArray = jsonArray.getJSONObject(index).getJSONArray("images_array");
            //   String count = String.valueOf(jsonArray.getJSONObject(index).getJSONArray("images_array").length());


        } catch (JSONException e) {
            e.printStackTrace();
        }


        for (int i=0;i<imagesArray.length();i++){

            try {

                if (imagesArray.getJSONObject(i).has("quantity"))
                photos_quantity=photos_quantity+Integer.parseInt(imagesArray.getJSONObject(i).getString("quantity"));
                else
                    photos_quantity=1;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
      //  photos_quantity = imagesArray.length();
        txt_quantity.setText(" "+String.valueOf(photos_quantity) + " pcs");


        photoOverviewModel = new PhotoOverviewModel();
        photoOverviewModel.initializeModel(imagesArray);
        arrayListForSelectedImages = photoOverviewModel.photoOverviewModelArrayList;

        paper_cropping_type = getIntent().getStringExtra("paper_cropping_type");
        paper_name = getIntent().getStringExtra("paper_name");
        paper_cropping_id = getIntent().getStringExtra("paper_cropping_id");
        Log.e(TAG, "paper_cropping_id"+paper_cropping_id );

        String width=getIntent().getStringExtra("width");
        String height=getIntent().getStringExtra("height");

         ww= Float.parseFloat(width);
         hh= Float.parseFloat(height);

        GridLayoutManager layoutManager = new GridLayoutManager( this,2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
       //recyclerView.addItemDecoration(new VerticalSpaceItemDecoration( 25));

        /*recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view); // item position
                int spanCount = 2;
                int spacing = 10;//spacing between views in grid

                if (position >= 0) {
                    int column = position % spanCount; // item column

                    outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                    outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                    if (position < spanCount) { // top edge
                        outRect.top = spacing;
                    }
                    outRect.bottom = spacing; // item bottom
                } else {
                    outRect.left = 0;
                    outRect.right = 0;
                    outRect.top = 0;
                    outRect.bottom = 0;
                }
            }
        });*/



        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        float density  = getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth  = outMetrics.widthPixels / density;
        Log.e(TAG, "onCreate: dpWidth: "+dpWidth );



        Iv_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PhotosOverviewActivity.this, YourOrderActivity.class);

                if (SystemClock.elapsedRealtime() - click_time < delay)
                    return;
                click_time = SystemClock.elapsedRealtime();

                intent.putExtra("price", getIntent().getStringExtra("price"));
                intent.putExtra("width", getIntent().getStringExtra("width"));
                intent.putExtra("height", getIntent().getStringExtra("height"));
                intent.putExtra("discount", getIntent().getStringExtra("discount"));
                intent.putExtra("quantity", getIntent().getStringExtra("quantity"));
                intent.putExtra("picture_size_id", getIntent().getStringExtra("picture_size_id"));


                JSONArray productsArray = new JSONArray();
                JSONArray imagesArray = new JSONArray();

                for (int j = 0; j < arrayListForSelectedImages.size(); j++) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("image", arrayListForSelectedImages.get(j).image);
                        jsonObject.put("quantity", arrayListForSelectedImages.get(j).quantity);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    imagesArray.put(jsonObject);
                }

                int index = Integer.parseInt(PhotosActivity.productId) - 1;
                String productData = BaseApplication.getInstance().getSession().getSelectedImages();
                if (!productData.isEmpty()) {

                    try {
                        productsArray = new JSONArray(productData);
                        productsArray.getJSONObject(index).put("images_array", imagesArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.e(TAG, "onClick Product Array: " + productsArray.toString());
                BaseApplication.getInstance().getSession().setSelectedImages(productsArray.toString());


                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        String cartData=BaseApplication.getInstance().getSession().getCartData();
        if (!cartData.isEmpty()){
            try {
                JSONArray jsonArray = new JSONArray(cartData);
                if (jsonArray.length()>0)
                    txt_counter.setText(String.valueOf(jsonArray.length()));
                else
                    txt_counter.setVisibility(View.INVISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else
            txt_counter.setVisibility(View.INVISIBLE);
    }


    private void setActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_icon_drawable);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mTitle.setText(R.string.photos_overview);
        btn_addPhotos.setOnClickListener(this);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        savetoSesion();


    }


    public void savetoSesion(){
        JSONArray productsArray = new JSONArray();
        JSONArray imagesArray = new JSONArray();

        for (int j = 0; j < arrayListForSelectedImages.size(); j++) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("image", arrayListForSelectedImages.get(j).image);
                jsonObject.put("quantity", arrayListForSelectedImages.get(j).quantity);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            imagesArray.put(jsonObject);
        }

        int index = Integer.parseInt(PhotosActivity.productId) - 1;
        String productData = BaseApplication.getInstance().getSession().getSelectedImages();
        if (!productData.isEmpty()) {

            try {
                productsArray = new JSONArray(productData);
                productsArray.getJSONObject(index).put("images_array", imagesArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.e(TAG, "onClick Product Array: " + productsArray.toString());
        BaseApplication.getInstance().getSession().setSelectedImages(productsArray.toString());

        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 5) {
                Bundle bundle = data.getExtras();

                if (bundle.getString("image") != null && bundle.getString("quantity") != null) {

                    String quantity = bundle.getString("quantity");

                    String edited_image = bundle.getString("image");
                    Log.e(TAG, "onActivi edited: " + bundle.getString("image"));



//                    if (quantity.equalsIgnoreCase("0")){
//                        Log.e(TAG, "onActivityResult: "+quantity );
//                        fitPaperAdapterOverview.deleteItem(quantity, edited_image);
//                        }
//                        else
//                            {
                        fitPaperAdapterOverview.newItem(quantity, edited_image,bundle.getString("papercropping"));
//                    }
                    int currentQuantity = 1;
                    for (int i = 0; i < arrayListForSelectedImages.size(); i++) {
                        currentQuantity = currentQuantity + Integer.parseInt(arrayListForSelectedImages.get(i).quantity);
                        Log.e(TAG, "onActivityResult current: " + currentQuantity);
                    }

                    // String[] separated = currentQuantity.split("pcs");
                    // Log.e(TAG, "onActivityResult: "+separated[0]);
                    //  int ss= Integer.parseInt(separated[0].trim());
                    //  int total= Integer.parseInt(currentQuantity) + Integer.parseInt(quantity);
                    //  Log.e(TAG, "onActivityResult total: "+total );
                    txt_quantity.setText(" "+String.valueOf(currentQuantity - 1) + " pcs");
                }

            }
        }
    }


    @SuppressLint("NewApi")
    public void setQuantity(int quantity){

        if (quantity<=1){

            if (BaseApplication.getInstance().getSession().isLoggedIn()){
                startActivity(new Intent(PhotosOverviewActivity.this,MainActivity.class));
                finishAffinity();
            }
            else {
                startActivity(new Intent(PhotosOverviewActivity.this,DefaultActivity.class));
                finishAffinity();

            }
            savetoSesion();

        }else {
            txt_quantity.setText(" "+String.valueOf(quantity - 1) + " pcs");
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_addPhotos:
                if (SystemClock.elapsedRealtime() - click_time < delay)
                    return;
                click_time = SystemClock.elapsedRealtime();
                Intent intent1 = new Intent(this, Selected_Photos.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent1.putExtra("price", getIntent().getStringExtra("price"));
                intent1.putExtra("width", getIntent().getStringExtra("width"));
                intent1.putExtra("height", getIntent().getStringExtra("height"));
                intent1.putExtra("discount", getIntent().getStringExtra("discount"));
                intent1.putExtra("quantity", getIntent().getStringExtra("quantity"));
                intent1.putExtra("picture_size_id", getIntent().getStringExtra("picture_size_id"));


                JSONArray productsArray = new JSONArray();
                JSONArray imagesArray = new JSONArray();

                for (int j = 0; j < arrayListForSelectedImages.size(); j++) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("image", arrayListForSelectedImages.get(j).image);
                        jsonObject.put("quantity", arrayListForSelectedImages.get(j).quantity);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    imagesArray.put(jsonObject);
                }

                int index = Integer.parseInt(PhotosActivity.productId) - 1;
                String productData = BaseApplication.getInstance().getSession().getSelectedImages();
                if (!productData.isEmpty()) {

                    try {
                        productsArray = new JSONArray(productData);
                        productsArray.getJSONObject(index).put("images_array", imagesArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.e(TAG, "onClick Product Array: " + productsArray.toString());
                BaseApplication.getInstance().getSession().setSelectedImages(productsArray.toString());

                startActivity(intent1);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                break;

            case R.id.btn_next:

                if (SystemClock.elapsedRealtime() - click_time < delay)
                    return;
                click_time = SystemClock.elapsedRealtime();
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this,R.style.AlertDialog);
                builder.setTitle("");
                builder.setMessage(R.string.pls_confirm_all_images);
                builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        Intent intent = new Intent(PhotosOverviewActivity.this, YourOrderActivity.class);
                        intent.putExtra("type", "photos");
                        intent.putExtra("paper_name", paper_name);
                        intent.putExtra("paper_cropping_type", paper_cropping_type);
                        intent.putExtra("paper_type_id", getIntent().getIntExtra("paper_type_id", 0));
                        intent.putExtra("paper_cropping_id", getIntent().getIntExtra("paper_cropping_id", 0));
                        intent.putExtra("price", getIntent().getStringExtra("price"));
                        intent.putExtra("width", getIntent().getStringExtra("width"));
                        intent.putExtra("height", getIntent().getStringExtra("height"));
                        intent.putExtra("discount", getIntent().getStringExtra("discount"));
                        intent.putExtra("quantity", getIntent().getStringExtra("quantity"));
                        intent.putExtra("picture_size_id", getIntent().getStringExtra("picture_size_id"));

                        String currentQuantity = txt_quantity.getText().toString();
                        String[] separated = currentQuantity.split("pcs");
                        int ss = Integer.parseInt(separated[0].trim());

                        if (ss==0)
                            UiHelper.showToast(PhotosOverviewActivity.this,getResources().getString(R.string.pls_Select_at_least_one_picture));
                        else {

                            intent.putExtra("photos_quantity", ss);


                            JSONArray productsArrays = new JSONArray();
                            JSONArray imagesArrays = new JSONArray();

                            for (int j = 0; j < arrayListForSelectedImages.size(); j++) {
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("image", arrayListForSelectedImages.get(j).image);
                                    jsonObject.put("quantity", arrayListForSelectedImages.get(j).quantity);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                imagesArrays.put(jsonObject);
                            }

                            int indexs = Integer.parseInt(PhotosActivity.productId) - 1;
                            String productDatas = BaseApplication.getInstance().getSession().getSelectedImages();
                            if (!productDatas.isEmpty()) {

                                try {
                                    productsArrays = new JSONArray(productDatas);
                                    productsArrays.getJSONObject(indexs).put("images_array", imagesArrays);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            Log.e(TAG, "onClick Product Array: " + productsArrays.toString());
                            BaseApplication.getInstance().getSession().setSelectedImages(productsArrays.toString());


                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                    }}
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                android.support.v7.app.AlertDialog alertDialog = builder.create();
                alertDialog.show();



                    break;
                }
        }

    @Override
    protected void onResume() {
        super.onResume();

        //To update the
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        fitPaperAdapterOverview = new FitPaperAdapterOverviewNew(arrayListForSelectedImages, this, paper_cropping_type,ww,hh,paper_cropping_id);
        recyclerView.setAdapter(fitPaperAdapterOverview);
    }
}

