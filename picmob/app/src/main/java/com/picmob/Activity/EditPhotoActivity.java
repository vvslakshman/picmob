package com.picmob.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.picmob.AppCustomView.CustomTextViewBold;
import com.picmob.AppCustomView.CustomTextViewNormal;
import com.picmob.Networking.BaseApplication;
import com.picmob.R;
import com.picmob.config.CropGallery;
import com.steelkiwi.cropiwa.image.CropIwaResultReceiver;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditPhotoActivity extends BaseActivity implements View.OnClickListener,CropIwaResultReceiver.Listener {

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    private static final String TAG = "EditPhotoActivity=> ";
    private static final float MAX_ZOOM = (float) 12;
    private static final float MIN_ZOOM = 1;
    private static double DELTA_INDEX[] = {
            0, 0.01, 0.02, 0.04, 0.05, 0.06, 0.07, 0.08, 0.1, 0.11,
            0.12, 0.14, 0.15, 0.16, 0.17, 0.18, 0.20, 0.21, 0.22, 0.24,
            0.25, 0.27, 0.28, 0.30, 0.32, 0.34, 0.36, 0.38, 0.40, 0.42,
            0.44, 0.46, 0.48, 0.5, 0.53, 0.56, 0.59, 0.62, 0.65, 0.68,
            0.71, 0.74, 0.77, 0.80, 0.83, 0.86, 0.89, 0.92, 0.95, 0.98,
            1.0, 1.06, 1.12, 1.18, 1.24, 1.30, 1.36, 1.42, 1.48, 1.54,
            1.60, 1.66, 1.72, 1.78, 1.84, 1.90, 1.96, 2.0, 2.12, 2.25,
            2.37, 2.50, 2.62, 2.75, 2.87, 3.0, 3.2, 3.4, 3.6, 3.8,
            4.0, 4.3, 4.7, 4.9, 5.0, 5.5, 6.0, 6.5, 6.8, 7.0,
            7.3, 7.5, 7.8, 8.0, 8.4, 8.7, 9.0, 9.4, 9.6, 9.8,
            10.0
    };
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tvTitle)
    CustomTextViewBold mTitle;
    @BindView(R.id.img)
    ImageView imageView;
    @BindView(R.id.txt_less)
    CustomTextViewNormal txt_less;
    @BindView(R.id.txt_max)
    CustomTextViewNormal txt_max;
    @BindView(R.id.txt_quantity)
    CustomTextViewNormal txt_quantity;
    @BindView(R.id.sb_brightness)
    SeekBar sb_brightness;
    @BindView(R.id.sb_contrast)
    SeekBar sb_contrast;
    @BindView(R.id.sb_saturation)
    SeekBar sb_saturation;
    @BindView(R.id.Iv_refresh)
    ImageView Iv_refresh;
    @BindView(R.id.Iv_rotate)
    ImageView Iv_rotate;
    @BindView(R.id.btn_save)
    Button btn_save;
    @BindView(R.id.txt_counter)
    CustomTextViewBold txt_counter;
    @BindView(R.id.Iv_cart)
    ImageView Iv_cart;
    String image;
    int itemQuantity = 1;
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    int mode = NONE;
    int degree = 0;
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage";
    private Bitmap imgBitmap;
    float  original_height;
    float original_width,cgheight,cgwidth;
    Uri edited_image_uri;
    float height_ratio,width_ratio;
    private CropIwaResultReceiver cropResultReceiver;
    private String paper_cropping_id;
    private int imageAngle=0;

    //for brightness
    public static PorterDuffColorFilter setBrightness(int progress) {

        Log.e(TAG, "progress value: "+progress );
        if (progress==50){
            return new PorterDuffColorFilter(Color.argb(0, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);
        }
        else if (progress > 50) {
            int value = (int) (progress - 100) * 255 / 100;

            return new PorterDuffColorFilter(Color.argb(value, 255, 255, 255), PorterDuff.Mode.SRC_OVER);

        } else {
            int value = (int) (100 - progress) * 255 / 100;
            return new PorterDuffColorFilter(Color.argb(value, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);
        }
    }

    public static ColorFilter adjustBrightnesss(int value) {
        ColorMatrix cm = new ColorMatrix();

        adjustBrightness(cm, value);

        return new ColorMatrixColorFilter(cm);
    }

    public static void adjustBrightness(ColorMatrix cm, float value) {
        value = cleanValue(value, 100);
        if (value == 0) {
            return;
        }

        float[] mat = new float[]
                {
                        1, 0, 0, 0, value,
                        0, 1, 0, 0, value,
                        0, 0, 1, 0, value,
                        0, 0, 0, 1, 0,
                        0, 0, 0, 0, 1
                };
        cm.postConcat(new ColorMatrix(mat));
    }

    public static ColorFilter adjustContrast(float value) {
        ColorMatrix cm = new ColorMatrix();

        adjustContrasts(cm, value);

        //    setContrast(cm, value);

        return new ColorMatrixColorFilter(cm);
    }

    public static void adjustContrasts(ColorMatrix cm, float value) {
        value = (int) cleanValue(value, 100);
        if (value == 0) {
            return;
        }
        float x;
        if (value < 50) {
            x = 127 + value / 100 * 127;
        } else {
            x = value % 1;
            if (x == 0) {
                x = (float) DELTA_INDEX[(int) value];
            } else {
                //x = DELTA_INDEX[(p_val<<0)]; // this is how the IDE does it.
                x = (float) DELTA_INDEX[((int) value << 0)] * (1 - x) + (float) DELTA_INDEX[((int) value << 0) + 1] * x; // use linear interpolation for more granularity.
            }
            x = x * 127 + 127;
        }

        float[] mat = new float[]
                {
                        x / 127, 0, 0, 0, 0.5f * (127 - x),
                        0, x / 127, 0, 0, 0.5f * (127 - x),
                        0, 0, x / 127, 0, 0.5f * (127 - x),
                        0, 0, 0, 1, 0,
                        0, 0, 0, 0, 1
                };
        cm.postConcat(new ColorMatrix(mat));

    }

    private static void setContrast(ColorMatrix cm, float contrast) {
        float scale = contrast + 1.f;
        float translate = (-.5f * scale + .5f) * 255.f;
        cm.set(new float[]{
                scale, 0, 0, 0, translate,
                0, scale, 0, 0, translate,
                0, 0, scale, 0, translate,
                0, 0, 0, 1, 0});
    }

    public static ColorFilter adjustSaturations(int value) {
        ColorMatrix cm = new ColorMatrix();

        adjustSaturation(cm, value);

        return new ColorMatrixColorFilter(cm);
    }

    public static void adjustSaturation(ColorMatrix cm, float value) {
        value = cleanValue(value, 100);
        if (value == 50) {
            return;
        }

        float x = 1 + ((value > 0) ? 3 * value / 100 : value / 100);
        float lumR = 0.3086f;
        float lumG = 0.6094f;
        float lumB = 0.0820f;

        float[] mat = new float[]
                {
                        lumR * (1 - x) + x, lumG * (1 - x), lumB * (1 - x), 0, 0,
                        lumR * (1 - x), lumG * (1 - x) + x, lumB * (1 - x), 0, 0,
                        lumR * (1 - x), lumG * (1 - x), lumB * (1 - x) + x, 0, 0,
                        0, 0, 0, 1, 0,
                        0, 0, 0, 0, 1
                };
        cm.postConcat(new ColorMatrix(mat));
    }

    protected static float cleanValue(float p_val, float p_limit) {
        return Math.min(p_limit, Math.max(-p_limit, p_val));
    }

    public static Bitmap rotateImage(Bitmap sourceImage, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(sourceImage, 0, 0, sourceImage.getWidth(), sourceImage.getHeight(), matrix, true);
    }


   public ColorMatrixColorFilter getContrastBrightnessFilter(float contrast) {
       float scale = contrast + 1.f;
       float translate = (-.5f * scale + .5f) * 255.f;
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, translate,
                        0, contrast, 0, 0, translate,
                        0, 0, contrast, 0, translate,
                        0, 0, 0, 1, 0
                });
        return new ColorMatrixColorFilter(cm);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_edit_photo_activity);
        ButterKnife.bind(this);
        setActionBar();

        cropResultReceiver = new CropIwaResultReceiver();
        cropResultReceiver.setListener(this);
        cropResultReceiver.register(this);

        image = getIntent().getStringExtra("image");
        cgheight = getIntent().getFloatExtra("cg_height",0);
        cgwidth = getIntent().getFloatExtra("cg_width",0);
        height_ratio = getIntent().getFloatExtra("height_ratio",0);
        width_ratio = getIntent().getFloatExtra("width_ratio",0);

        Log.e(TAG, "EDIT: "+cgheight +"cgWidth:"+cgwidth );
        itemQuantity = Integer.parseInt(getIntent().getStringExtra("quantity"));
        txt_quantity.setText(String.valueOf(itemQuantity));

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeFile(new File(image).getPath());
      //  bitmap.compress( Bitmap.CompressFormat.PNG,100,stream);
   //     imageView.setImageBitmap(bitmap);


        paper_cropping_id = getIntent().getStringExtra("paper_cropping_id");



        if (paper_cropping_id.equalsIgnoreCase("2")){
            Iv_rotate.setVisibility(View.VISIBLE);
            setCropImage();
        //    imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, (int)cgwidth, (int)cgheight, false));
       //     imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        }else if (paper_cropping_id.equalsIgnoreCase("1")){
            Iv_rotate.setVisibility(View.VISIBLE);
            setCropImage();
//            imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, (int)cgwidth, (int)cgheight, false));
        }else {
            Iv_rotate.setVisibility(View.GONE);
            imageView.setImageBitmap(bitmap);
        }

        Log.e(TAG, "onCreate: " + bitmap);



        sb_brightness.setMax(99);
        sb_brightness.setProgress(50);
        sb_brightness.setKeyProgressIncrement(1);
        sb_brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                // imageView.setColorFilter(adjustBrightnesss(progress));
                imageView.setColorFilter(setBrightness(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sb_contrast.setMax(100);
        sb_contrast.setKeyProgressIncrement(1);
        sb_contrast.setProgress(50);
        sb_contrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //imageView.setColorFilter(adjustContrast(i));
                if (i!=50){
                    float contrast = (float) (i + 10) / 10;
                    imageView.setColorFilter(getContrastBrightnessFilter(contrast));
                }else {
                    imageView.setColorFilter(0);
                }

            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        sb_saturation.setMax(100);
        sb_saturation.setKeyProgressIncrement(1);
        sb_saturation.setProgress(50);

        //converting image to bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        imgBitmap = BitmapFactory.decodeFile(image, options);

        sb_saturation.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                imageView.setColorFilter(adjustSaturations(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Iv_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditPhotoActivity.this, YourOrderActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

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

        Iv_rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*Bitmap bmap=((BitmapDrawable)imageView.getDrawable()).getBitmap();
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                String path = MediaStore.Images.Media.insertImage(getContentResolver(), bmap, "Title", null);

                float imagewidth=bmap.getWidth();
                float imageheight=bmap.getHeight();
                Log.e(TAG, "onClick width and height: "+imagewidth+" , "+imageheight );
                Intent intent=new Intent(EditPhotoActivity.this,CropIwaActivity.class);
                intent.putExtra("image",Uri.parse(path).toString());
                intent.putExtra("original_height",original_height);
                intent.putExtra("original_width",original_width);
                intent.putExtra("height_ratio",height_ratio);
                intent.putExtra("width_ratio",width_ratio);
                intent.putExtra("cg_height",cgheight);
                intent.putExtra("cg_width",cgwidth);
                intent.putExtra("imagewidth",imagewidth);
                intent.putExtra("imageheight",imageheight);

                startActivity(intent);*/






                Bitmap bmap =((BitmapDrawable)imageView.getDrawable()).getBitmap();
                Bitmap bitmap = BitmapFactory.decodeFile(image);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);

           //     File file=new File();
                File orginal_image=new File(image);
                File bitmmm=new File(path);
                Log.e(TAG, "IMAGE: "+orginal_image );
                Log.e(TAG, "PATH: "+bitmmm );
                try {
//                //    Log.e("TAG", "IMAGE: "+convertFileToContentUri(EditPhotoActivity.this,file) );
//               //     convertFileToContentUri(EditPhotoActivity.this,file);
                    getDropboxIMGSize((bmap));
                    startCrop(Uri.parse(path));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setCropImage() {


        float after_height=0;
        float after_width=0;
        float pos_x=0;
        float pos_y=0;
        getDropboxIMGSizeUri(Uri.parse(image));


        if (original_width >= original_height) {
            if (width_ratio < height_ratio) {

                float width_r = width_ratio;
                width_ratio = height_ratio;
                height_ratio = width_r;

            }
        }else {
            if (width_ratio > height_ratio) {
                float width_r = width_ratio;
                width_ratio = height_ratio;
                height_ratio = width_r;
            }
        }




        if (original_width > original_height){
            after_width=original_height * (width_ratio/height_ratio);
            after_height=original_height;

        }else if (original_height > original_width){
            after_height = original_width * (height_ratio / width_ratio);
            after_width=original_width;
            Log.e("TAG", "height big Orginal position: "+original_width + original_height );

        }else {
            after_height=original_height;
            after_width=original_width;

            //PKJ
            after_height = original_width * (((height_ratio)/(width_ratio)));
            after_width=original_width;
        }


        //  float cgheight=0;
        //  float cgwidth=0;




        if (original_width> original_height){

            if (original_width>after_width) {
                pos_x = ((original_width - after_width) / 2);
                pos_y = 0;
            }
            else {
                after_height = original_width * (height_ratio / width_ratio); //height_ratio-width_ratio
                after_width = original_width;
                pos_x = 0;
                pos_y = ((original_height - after_height) / 2);
            }

            cgheight = after_height;
            cgwidth=after_width;

        } else if (original_height>original_width){

            if (original_height>after_height) {
                pos_y = ((original_height - after_height) / 2);
                pos_x = 0;
            }
            else {
                after_width = original_height * (width_ratio / height_ratio);//width_ratio-height_ratio
                after_height = original_height;
                pos_y = 0;
                pos_x = ((original_width - after_width) / 2);

            }
            cgwidth = after_width;
            cgheight=after_height;
        }else {
            pos_x=0;
            pos_y=0;
            cgheight=original_height;
            cgwidth=original_width;


            if (original_height>after_height) {
                pos_y = (((original_height - after_height) / 2));
                pos_x = 0;

            }
            else {
                after_width = original_height * (((width_ratio) / (height_ratio)));//width_ratio-height_ratio
                after_height = original_height;
                pos_y = 0;
                pos_x = (((original_width - after_width) / 2));
            }

            cgwidth = (after_width);
            cgheight=(after_height);
        }


        Log.e("TAG", "after calculation FIT width: "+after_width +"height:"+after_height );
        Log.e("TAG", "position x:"+pos_x +" pos_y:"+ pos_y );
        Log.e("TAG", "cgHeight: "+cgheight +" cgWidth:"+cgwidth );


        if (pos_x+cgwidth>original_width)
        {
            if (cgwidth>original_width){

                Glide.with(this)
                        .load(image)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .override((int)original_width,(int)cgheight)
                        .into(imageView);
            }
            else{

                Glide.with(this)
                        .load(image)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .override((int)cgwidth,(int)cgheight)
                        .into(imageView);
            }
        }

        else if (pos_y+cgheight>original_height)
        {
            if (cgheight>original_height){
                Glide.with(this)
                        .load(image)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .override((int)cgwidth,(int)original_height)
                        .into(imageView);


            }
            else{
                Glide.with(this)
                        .load(image)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .override((int)cgwidth,(int)cgheight)
                        .into(imageView);
            }

        }else{
            Glide.with(this)
                    .load(image)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .override((int)cgwidth,(int)cgheight)
                    .into(imageView);
        }


    }

    private void setActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_white_drawable);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mTitle.setText(R.string.edit);
        txt_max.setOnClickListener(this);
        txt_less.setOnClickListener(this);
        Iv_refresh.setOnClickListener(this);
        btn_save.setOnClickListener(this);
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
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.txt_max:
                if (itemQuantity >= 1)
                    itemQuantity++;
                txt_quantity.setText(String.valueOf(itemQuantity));
                break;

            case R.id.txt_less:
                if (itemQuantity > 1)
                    itemQuantity--;
                txt_quantity.setText(String.valueOf(itemQuantity));
                break;

            case R.id.Iv_refresh:
                if (imageAngle==360){
                    imageAngle=0;
                }
                imageAngle=imageAngle+90;



//                final RotateAnimation rotateAnim = new RotateAnimation(degree, 90,
//                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
//                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);
//
//                rotateAnim.setDuration(0);
//                rotateAnim.setFillAfter(true);
//                imageView.startAnimation(rotateAnim);
//                degree += 90;
//                imageView.setRotation(degree);
//                Bitmap bmap1 = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
//                imageView.setImageBitmap(bmap1);
//                Log.e(TAG, "onClick rotated: " + bmap1);

                degree=0;
                degree += 90;
                Bitmap bmap1 = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                Bitmap rotatedImage = rotateImage(bmap1, degree);
                imageView.setImageBitmap(rotatedImage);
                Log.e(TAG, "onClick rotated: " + rotatedImage);

                break;

            case R.id.btn_save:
                Intent intent = new Intent();
                intent.putExtra("quantity", txt_quantity.getText().toString());
              //  imageView.buildDrawingCache();

                Bitmap bmap = loadBitmapFromView(imageView);
            //    Bitmap bmap=((BitmapDrawable)imageView.getDrawable()).getBitmap();

              //  Bitmap bmap = imageView.getDrawingCache();



            //    Bitmap rotatedImages = rotateImage(bmap, degree);

                //Convert to byte array
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                byte[] byteArray = stream.toByteArray();

                //making folder
                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/PicMob");
                if (!myDir.exists()) {
                    myDir.mkdirs();
                }
                Random generator = new Random();
                int n = 10000;
                n = generator.nextInt(n);
                String fname = "Image-" + n + ".png";
                File file = new File(myDir, fname);
                if (file.exists())
                    file.delete();
                try {
                 //   FileInputStream inStream = new FileInputStream(new File(edited_image_uri.getPath()));
                    FileOutputStream out = new FileOutputStream(file);
                 //   FileChannel inChannel = inStream.getChannel();
                 //   FileChannel outChannel = out.getChannel();
                    bmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                 //   inChannel.transferTo(0, inChannel.size(), outChannel);
                 //   inStream.close();
                    out.flush();
                    out.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    final Uri contentUri = Uri.fromFile(file);
                    scanIntent.setData(contentUri);
                    sendBroadcast(scanIntent);
                } else {
                    final Intent intentt = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
                    sendBroadcast(intentt);
                }
                Log.e(TAG, "onClick:filePath " + file.getPath());
                intent.putExtra("image", file.getPath());
                intent.putExtra("papercropping",getIntent().getStringExtra("papercropping"));
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                break;
        }
    }

    private Bitmap loadBitmapFromView(ImageView v) {
        Log.e(TAG, "loadBitmapFromView: "+v );
        Bitmap bmap=((BitmapDrawable)v.getDrawable()).getBitmap();
        final int w = bmap.getWidth();
        final int h = bmap.getHeight();
        bmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        final Canvas c = new Canvas(bmap);
        v.layout(0, 0, w, h);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return bmap;
    }

    private void startCrop(@NonNull Uri uri) {
        String destinationFileName = SAMPLE_CROPPED_IMAGE_NAME+".png";

        String quantity=getIntent().getStringExtra("quantity");
        String paper_cropping_type=getIntent().getStringExtra("papercropping");
     //   float height_ratio=getIntent().getFloatExtra("height_ratio",0);
     //   float width_ratio=getIntent().getFloatExtra("width_ratio",0);

        float image_height=getIntent().getFloatExtra("after_height",0);
        float image_width=getIntent().getFloatExtra("after_width",0);
        Log.e(TAG, "startCrop Height: "+image_height+" Width:"+image_width );
        Log.e(TAG, "startCrop RATIO: "+height_ratio+" Width:"+width_ratio );


//        if (paper_cropping_type.equalsIgnoreCase("White Frame"))
//        {

        if (original_height>original_width) {


            if (height_ratio == 10.0 && width_ratio == 15.0) {

                UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)), 1.58f, quantity, paper_cropping_type,imageAngle);
                uCrop.start((EditPhotoActivity.this), 5);
            } else if (height_ratio == 15.0 && width_ratio == 10.0) {
                UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)), 0.632f, quantity, paper_cropping_type,imageAngle);
                uCrop.start((EditPhotoActivity.this), 5);
            } else if (height_ratio == 20.0 && width_ratio == 30.0) {
                UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)), 1.58f, quantity, paper_cropping_type,imageAngle);
                uCrop.start((EditPhotoActivity.this), 5);
            } else if (height_ratio == 30.0 && width_ratio == 20.0) {
                UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)), 0.635f, quantity, paper_cropping_type,imageAngle);
                uCrop.start((EditPhotoActivity.this), 5);
            } else if (height_ratio == 15.0 && width_ratio == 20.0) {
                UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)), 0.72f, quantity, paper_cropping_type,imageAngle);
                uCrop.start((EditPhotoActivity.this), 5);
            } else if (height_ratio == 20.0 && width_ratio == 15.0) {
                UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)), 0.72f, quantity, paper_cropping_type,imageAngle);
                uCrop.start((EditPhotoActivity.this), 5);
            } else {
                UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)), 1, quantity, paper_cropping_type,imageAngle);
                uCrop.start((EditPhotoActivity.this), 5);
            }
        }
        else {
            if (height_ratio == 10.0 && width_ratio == 15.0) {
                UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)), 1.58f, quantity, paper_cropping_type,imageAngle);
                uCrop.start((EditPhotoActivity.this), 5);
            } else if (height_ratio == 15.0 && width_ratio == 10.0) {
                UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)), 0.632f, quantity, paper_cropping_type,imageAngle);
                uCrop.start((EditPhotoActivity.this), 5);
            } else if (height_ratio == 20.0 && width_ratio == 30.0) {
                UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)), 1.57f, quantity, paper_cropping_type,imageAngle);
                uCrop.start((EditPhotoActivity.this), 5);
            } else if (height_ratio == 30.0 && width_ratio == 20.0) {
                UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)), 1.57f, quantity, paper_cropping_type,imageAngle);
                uCrop.start((EditPhotoActivity.this), 5);
            } else if (height_ratio == 15.0 && width_ratio == 20.0) {
                UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)), 1.384f, quantity, paper_cropping_type,imageAngle);
                uCrop.start((EditPhotoActivity.this), 5);
            } else if (height_ratio == 20.0 && width_ratio == 15.0) {
                UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)), 1.384f, quantity, paper_cropping_type,imageAngle);
                uCrop.start((EditPhotoActivity.this), 5);
            } else {
                UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)), 1, quantity, paper_cropping_type,imageAngle);
                uCrop.start((EditPhotoActivity.this), 5);
            }


        }
//    }
//    else {
//
//            if (original_height>original_width) {
//
//
//                if (height_ratio == 10.0 && width_ratio == 15.0) {
//                    UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)), 0.66f, quantity, paper_cropping_type);
//                    uCrop.start((EditPhotoActivity.this), 5);
//                } else if (height_ratio == 15.0 && width_ratio == 10.0) {
//                    UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)), 1.5f, quantity, paper_cropping_type);
//                    uCrop.start((EditPhotoActivity.this), 5);
//                } else if (height_ratio == 20.0 && width_ratio == 30.0) {
//                    UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)), 0.66f, quantity, paper_cropping_type);
//                    uCrop.start((EditPhotoActivity.this), 5);
//                } else if (height_ratio == 30.0 && width_ratio == 20.0) {
//                    UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)), 1.5f, quantity, paper_cropping_type);
//                    uCrop.start((EditPhotoActivity.this), 5);
//                } else if (height_ratio == 15.0 && width_ratio == 20.0) {
//                    UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)), 0.75f, quantity, paper_cropping_type);
//                    uCrop.start((EditPhotoActivity.this), 5);
//                } else if (height_ratio == 20.0 && width_ratio == 15.0) {
//                    UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)), 1.33f, quantity, paper_cropping_type);
//                    uCrop.start((EditPhotoActivity.this), 5);
//                } else {
//                    UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)), 1, quantity, paper_cropping_type);
//                    uCrop.start((EditPhotoActivity.this), 5);
//                }
//            }
//            else {
//                if (height_ratio == 10.0 && width_ratio == 15.0) {
//                    UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)), 0.66f, quantity, paper_cropping_type);
//                    uCrop.start((EditPhotoActivity.this), 5);
//                } else if (height_ratio == 15.0 && width_ratio == 10.0) {
//                    UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)), 1.5f, quantity, paper_cropping_type);
//                    uCrop.start((EditPhotoActivity.this), 5);
//                } else if (height_ratio == 20.0 && width_ratio == 30.0) {
//                    UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)), 0.66f, quantity, paper_cropping_type);
//                    uCrop.start((EditPhotoActivity.this), 5);
//                } else if (height_ratio == 30.0 && width_ratio == 20.0) {
//                    UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)), 1.5f, quantity, paper_cropping_type);
//                    uCrop.start((EditPhotoActivity.this), 5);
//                } else if (height_ratio == 15.0 && width_ratio == 20.0) {
//                    UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)), 0.75f, quantity, paper_cropping_type);
//                    uCrop.start((EditPhotoActivity.this), 5);
//                } else if (height_ratio == 20.0 && width_ratio == 15.0) {
//                    UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)), 1.33f, quantity, paper_cropping_type);
//                    uCrop.start((EditPhotoActivity.this), 5);
//                } else {
//                    UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)), 1, quantity, paper_cropping_type);
//                    uCrop.start((EditPhotoActivity.this), 5);
//                }
//
//
//            }
//
//        }
    }

    private float getCropFrameRatio(float width,float height) {
        float ratio;
        if (height < width){


            ratio =cgwidth/original_height;
            Log.e(TAG, "getCropFrameRatio: "+height +"WIDTH:"+width +"Ratio:"+width/height);

        }else {
            ratio=height/cgwidth;
            Log.e(TAG, "getCropFrameRatio:else "+height +"WIDTH:"+width +"Ratio:"+width/height);
            Log.e(TAG, "getCropFrameRatio:else " );
        }

        Log.e(TAG, "getCropFrameRatio ratio: "+ratio);

        return ratio;
    }

    protected static Uri convertFileToContentUri(Context context, File  file) throws Exception {

        //Uri localImageUri = Uri.fromFile(localImageFile); // Not suitable as it's not a content Uri

        ContentResolver cr = context.getContentResolver();
        String imagePath = file.getAbsolutePath();
        String imageName = null;
        String imageDescription = null;
        String uriString = MediaStore.Images.Media.insertImage(cr, imagePath, imageName, imageDescription);
        return Uri.parse(uriString);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 5) {
                Bundle bundle = data.getExtras();

                if (bundle!=null){
               //     BitmapFactory.Options options = new BitmapFactory.Options();
                     edited_image_uri = (Uri) bundle.get("uri");
              //      Bitmap bmp=     BitmapFactory.decodeFile(new File(edited_image.getPath()).getAbsolutePath(), options);
                    Log.e(TAG, "onActivityResult: "+edited_image_uri );
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeFile(new File(edited_image_uri.getPath()).getAbsolutePath(),bmOptions);
                    imageView.setImageBitmap(bitmap);

                }

            }
        }
    }


    public void getDropboxIMGSize(Bitmap uri){
        Log.e(TAG, "getDropboxIMGSize: "+uri );
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);
//        original_height = options.outHeight;
//        original_width = options.outWidth;



      //  BitmapFactory.Options bmOptions = new BitmapFactory.Options();
      //  Bitmap bitmap = BitmapFactory.decodeFile(uri.getAbsolutePath(),bmOptions);
        original_height=uri.getHeight();
        original_width=uri.getWidth();
        Log.e("TAG", "getDropboxIMGSize: "+original_width+" height"+original_height );
      //  bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);


    }

    private void getDropboxIMGSizeUri(Uri uri){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);
        original_height = options.outHeight;
        original_width = options.outWidth;
        Log.e("TAG", "getDropboxIMGSize: "+original_width+" height"+original_height );

    }

    @Override
    public void onCropSuccess(Uri croppedUri) {
        Log.e(TAG, "onCropSuccess: "+croppedUri );
        imageView.setImageURI(croppedUri);
    }

    @Override
    public void onCropFailed(Throwable e) {
        Log.e(TAG, "onCropfailed: " );
    }


}
