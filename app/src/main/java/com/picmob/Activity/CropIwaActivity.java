package com.picmob.Activity;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.picmob.AppCustomView.CustomTextViewBold;
import com.picmob.R;
import com.picmob.config.CropViewConfigurator;
import com.steelkiwi.cropiwa.AspectRatio;
import com.steelkiwi.cropiwa.CropIwaView;
import com.steelkiwi.cropiwa.config.ConfigChangeListener;
import com.steelkiwi.cropiwa.config.CropIwaSaveConfig;
import com.steelkiwi.cropiwa.config.InitialPosition;
import com.steelkiwi.cropiwa.image.CropIwaResultReceiver;
import com.yalantis.ucrop.UCrop;
import com.yarolegovich.mp.MaterialPreferenceScreen;

import java.io.ByteArrayOutputStream;
import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CropIwaActivity extends BaseActivity{

    @BindView(R.id.crop_view)
    CropIwaView cropIwaView;

    @BindView(R.id.btn_save)
    Button btn_save;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvTitle)
    TextView mTitle;

    float original_height,original_width,height_ratio,width_ratio,cg_height,cg_width;
    float after_height_ratio,after_width_ratio;
    private CropIwaSaveConfig.Builder saveConfig;
    private CropViewConfigurator configurator;
    private final String TAG="CropIwaActivity=> ";
    Uri destination;
    int imagewidth,imageheight;
    private Uri myUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cropiwa_layout);
        ButterKnife.bind(this);
        setActionBar();
        this.saveConfig = new CropIwaSaveConfig.Builder(destination);
        original_height=getIntent().getFloatExtra("original_height",original_height);
        original_width=getIntent().getFloatExtra("original_width",original_width);
        height_ratio=getIntent().getFloatExtra("height_ratio",height_ratio);
        width_ratio=getIntent().getFloatExtra("width_ratio",width_ratio);
        cg_height=getIntent().getFloatExtra("cg_height",cg_height);
        cg_width=getIntent().getFloatExtra("cg_width",cg_width);
        imagewidth=(int) getIntent().getFloatExtra("imagewidth",imagewidth);
        imageheight=(int) getIntent().getFloatExtra("imageheight",imageheight);

        Log.e(TAG, "image width and height: "+imagewidth+" , "+imageheight );
        Log.e(TAG, "cg width and height: "+cg_width+" , "+cg_height );

        configurator = new CropViewConfigurator(cropIwaView);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropIwaView.crop(configurator.getSelectedSaveConfig());
                finish();

            }
        });
        myUri = Uri.parse(getIntent().getStringExtra("image"));
          //Uri uri=   Uri.fromFile(new File(getIntent().getStringExtra("image")));
        //Log.e("TAG", "onCreate: "+uri );

       /* ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                (int)original_width,
                (int)original_height
        );
        cropIwaView.setLayoutParams(params);*/


       cropIwaView.setImageUri(myUri);
       cropIwaView.configureImage().addConfigChangeListener(new ConfigChangeListener() {
           @Override
           public void onConfigChanged() {
               Log.e(TAG, "onConfigChanged: ");

           }
       });







        /*//To scale image
        //Value is a float from 0.01f to 1
        cropIwaView.configureImage()
                .setScale(0.1f)
                .apply();*/



        //cropIwaView.setLayoutParams(new RelativeLayout.LayoutParams(200,200));

        if (original_height>original_width){

            float temp=height_ratio;
            height_ratio=width_ratio;
            width_ratio=temp;

        }else if (original_width==original_height){

            if (height_ratio>width_ratio){

                float temp=height_ratio;
                height_ratio=width_ratio;
                width_ratio=temp;

            }
        }

        Log.e(TAG, "onCreate: "+original_height+"width:"+original_width );

        if (original_height>original_width) {


            if (height_ratio == 10.0 && width_ratio == 15.0) {

                after_height_ratio=136.0f;
                after_width_ratio=86.0f;

            } else if (height_ratio == 15.0 && width_ratio == 10.0) {

                after_height_ratio=136.0f;
                after_width_ratio=86.0f;
            } else if (height_ratio == 20.0 && width_ratio == 30.0) {
                after_height_ratio=274.0f;
                after_width_ratio=174.0f;
            } else if (height_ratio == 30.0 && width_ratio == 20.0) {
                after_height_ratio=274.0f;
                after_width_ratio=174.0f;
            } else if (height_ratio == 15.0 && width_ratio == 20.0) {
                after_height_ratio=18.0f;
                after_width_ratio=13.0f;
            } else if (height_ratio == 20.0 && width_ratio == 15.0) {
                after_height_ratio=18.0f;
                after_width_ratio=13.0f;
            } else {
                after_height_ratio=15.0f;
                after_width_ratio=15.0f;
            }
        }
        else {
            if (height_ratio == 10.0 && width_ratio == 15.0) {
                after_height_ratio=86.0f;
                after_width_ratio=136.0f;
            } else if (height_ratio == 15.0 && width_ratio == 10.0) {
                after_height_ratio=86.0f;
                after_width_ratio=136.0f;
            } else if (height_ratio == 20.0 && width_ratio == 30.0) {
                after_height_ratio=174.0f;
                after_width_ratio=274.0f;
            } else if (height_ratio == 30.0 && width_ratio == 20.0) {
                after_height_ratio=174.0f;
                after_width_ratio=274.0f;
            } else if (height_ratio == 15.0 && width_ratio == 20.0) {
                after_height_ratio=13.0f;
                after_width_ratio=18.0f;
            } else if (height_ratio == 20.0 && width_ratio == 15.0) {
                after_height_ratio=13.0f;
                after_width_ratio=13.0f;
            } else {
                after_height_ratio=15.0f;
                after_width_ratio=15.0f;
            }
        }


        if (imagewidth>imageheight){
            Log.e(TAG, "width greater: "+imagewidth+" , "+imageheight );
            Log.e(TAG, "crop frame width and height: "+after_width_ratio+" , "+after_height_ratio );



            cropIwaView.configureOverlay()

                    .setAspectRatio(new AspectRatio((int)original_width,(int)original_height))
                    .setDynamicCrop(false)
                    .apply();




        }else {
            Log.e(TAG, "height greater: "+imagewidth+" , "+imageheight );
            Log.e(TAG, "crop frame width and height: "+after_width_ratio+" , "+after_height_ratio );



            cropIwaView.configureOverlay()
                    .setAspectRatio(new AspectRatio((int)original_height,(int)original_width))
                    .setDynamicCrop(false)
                    .apply();

        }


    }
    private void setActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_white_drawable);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mTitle.setText(R.string.crop);

    }


    private void getDropboxIMGSize(Uri uri){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);
        original_height = options.outHeight;
        original_width = options.outWidth;
        Log.e("TAG", "getDropboxIMGSize: "+original_width+" height"+original_height );

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

}
