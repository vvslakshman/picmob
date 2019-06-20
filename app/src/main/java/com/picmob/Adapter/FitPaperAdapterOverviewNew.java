package com.picmob.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.picmob.Activity.EditPhotoActivity;
import com.picmob.Activity.PhotosOverviewActivity;
import com.picmob.AppCustomView.CustomTextViewNormal;
import com.picmob.Models.CalculationModel;
import com.picmob.Models.PhotoOverviewModel;
import com.picmob.R;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

public class FitPaperAdapterOverviewNew extends RecyclerView.Adapter<FitPaperAdapterOverviewNew.ViewHolder> {


    Context mContext;
    long click_time = 0;
    long delay = 700;
    public ArrayList<PhotoOverviewModel> arrayList = new ArrayList<>();
    String paper_cropping_type;
    float original_height_ratio, original_width_ratio;
    float height_ratio, width_ratio;
    public int pos = -1;
    float original_width;
    float original_height;
    float cgheight = 0;
    float cgwidth = 0;
    float after_height = 0;
    float after_width = 0;
    float pos_x = 0;
    float pos_y = 0;
    int finalHeight, finalWidth;
    private final String TAG = "FitPaperAdapter=> ";
    private float cellHeight,cellWidth;
    ArrayList<CalculationModel> calculationModelArrayList;
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage";
    int device_width;
    private String paper_cropping_id;

    public FitPaperAdapterOverviewNew(ArrayList<PhotoOverviewModel> arrayList, Context mContext, String type, float height, float width,String paper_cropping_id) {
        this.arrayList = arrayList;
        this.mContext = mContext;
        this.paper_cropping_type = type;
        this.width_ratio = width;
        this.height_ratio = height;
        this.paper_cropping_id = paper_cropping_id;
        calculationModelArrayList = new ArrayList<>();
        calculationModelArrayList.clear();
        Log.e(TAG, "width and height : " + width_ratio + " , " + height_ratio);

        DisplayMetrics displayMetrics = new DisplayMetrics();

        device_width  = mContext.getResources().getSystem().getDisplayMetrics().widthPixels;
        device_width=device_width-20;
        Log.e(TAG, "FitPaperAdapterOverviewNew: device_width: "+device_width  );

        //device_width=pxToDp(device_width);
       // Log.e(TAG, "FitPaperAdapterOverviewNew: device_width in dp"+device_width  );

    }

    @NonNull
    @Override
    public FitPaperAdapterOverviewNew.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fit_paper_row_one_new, parent, false);

        return new FitPaperAdapterOverviewNew.ViewHolder(view);
    }


    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(final FitPaperAdapterOverviewNew.ViewHolder holder, final int position) {


        getDropboxIMGSize(Uri.parse(arrayList.get(position).image));
        holder.imageViewFitPaper.setVisibility(View.VISIBLE);





        if (original_width >= original_height) {
            if (width_ratio < height_ratio) {

                float width_r = width_ratio;
                width_ratio = height_ratio;
                height_ratio = width_r;

            }
        } else {
            if (width_ratio > height_ratio) {
                float width_r = width_ratio;
                width_ratio = height_ratio;
                height_ratio = width_r;
            }
        }



        if (original_width > original_height) {
            after_width = original_height * (width_ratio / height_ratio);
            after_height = original_height;

        } else if (original_height > original_width) {
            after_height = original_width * (height_ratio / width_ratio);
            after_width = original_width;
            Log.e("TAG", "height big Orginal position: " + original_width + original_height);

        } else {
            after_height = original_height;
            after_width = original_width;

            //PKJ
            after_height = original_width * (((height_ratio) / (width_ratio)));
            after_width = original_width;
        }


        if (original_width > original_height) {

            if (original_width > after_width) {
                pos_x = ((original_width - after_width) / 2);
                pos_y = 0;
            } else {
                after_height = original_width * (height_ratio / width_ratio); //height_ratio-width_ratio
                after_width = original_width;
                pos_x = 0;
                pos_y = ((original_height - after_height) / 2);
            }

            cgheight = after_height;
            cgwidth = after_width;

        } else if (original_height > original_width) {

            if (original_height > after_height) {
                pos_y = ((original_height - after_height) / 2);
                pos_x = 0;
            } else {
                after_width = original_height * (width_ratio / height_ratio);//width_ratio-height_ratio
                after_height = original_height;
                pos_y = 0;
                pos_x = ((original_width - after_width) / 2);

            }
            cgwidth = after_width;
            cgheight = after_height;
        } else {
            pos_x = 0;
            pos_y = 0;
            cgheight = original_height;
            cgwidth = original_width;


            if (original_height > after_height) {
                pos_y = (((original_height - after_height) / 2));
                pos_x = 0;

            } else {
                after_width = original_height * (((width_ratio) / (height_ratio)));//width_ratio-height_ratio
                after_height = original_height;
                pos_y = 0;
                pos_x = (((original_width - after_width) / 2));
            }

            cgwidth = (after_width);
            cgheight = (after_height);
        }

        Log.e("TAG", "original width : " + original_width + "original height :" + original_height + " cgHeight: " + cgheight + " cgWidth:" + cgwidth);
        Log.e("TAG", "after calculation FIT width: " + after_width + "height:" + after_height);
        Log.e("TAG", "position x:" + pos_x + " pos_y:" + pos_y);


     /*   DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int device_width = displayMetrics.widthPixels;*/

        ConstraintLayout.LayoutParams params;

        if (height_ratio == 10.0 && width_ratio == 15.0) {
            Log.e(TAG, "inside 15:10 " );

            Log.e(TAG, "position "+position+" after_height "+after_width+" after_height "+after_width   );

            if (after_height > after_width) {

                cellWidth=(device_width / 2) - 30;
                cellHeight= (float) ((cellWidth* 0.667));

                params = new ConstraintLayout.LayoutParams(
                        (int) cellWidth,
                        (int)cellHeight
                );

                Glide.with(mContext)
                        .load(arrayList.get(position).image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .override((int) cellWidth, (int) cellHeight)
                        .into(holder.imageViewFitPaper);


            } else {

                cellWidth=(device_width / 2) - 30;
                cellHeight= (float) ((cellWidth* 0.667));

                params = new ConstraintLayout.LayoutParams(
                        (int) cellWidth,
                        (int)cellHeight
                );

                Glide.with(mContext)
                        .load(arrayList.get(position).image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .override((int) cellWidth, (int) cellHeight)
                        .into(holder.imageViewFitPaper);

            }



        } else if (height_ratio == 15.0 && width_ratio == 10.0) {
            Log.e(TAG, "inside 10:15 " );

            if (after_height > after_width) {

                 cellWidth=(device_width/2-30);


                 cellHeight= (float) ((cellWidth*1.5));
                Log.e(TAG, "cell width and height: if "+cellWidth+" "+cellHeight );

                params = new ConstraintLayout.LayoutParams(
                        (int) cellWidth,
                        (int)cellHeight
                );

                Glide.with(mContext)
                        .load(arrayList.get(position).image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .override((int)cellWidth, (int) cellHeight)
                        .into(holder.imageViewFitPaper);

            } else {

                cellWidth=(device_width/2-30);
                cellHeight= (int) (cellWidth*1.5);
                Log.e(TAG, "cell width and height: else"+cellWidth+" "+cellHeight );

                params = new ConstraintLayout.LayoutParams(
                        (int)cellWidth,
                        (int)cellHeight
                );

                Glide.with(mContext)
                        .load(arrayList.get(position).image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .override((int) cellWidth, (int) cellHeight)
                        .into(holder.imageViewFitPaper);
            }

        } else if (height_ratio == 20.0 && width_ratio == 30.0) {

            Log.e(TAG, "inside 30:20 " );

            cellWidth=device_width / 2 - 30;
            cellHeight= (float) (cellWidth*0.66);

            params = new ConstraintLayout.LayoutParams(
                    (int)cellWidth,(int)cellHeight
            );

            Glide.with(mContext)
                    .load(arrayList.get(position).image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .override((int) cellWidth, (int) cellHeight)
                    .into(holder.imageViewFitPaper);

        } else if (height_ratio == 30.0 && width_ratio == 20.0) {
            Log.e(TAG, "inside 20:30 " );

            cellWidth=device_width / 2 - 30;
            cellHeight= (float) (cellWidth*1.5);

            params = new ConstraintLayout.LayoutParams(
                    (int)cellWidth,
                    (int)cellHeight
            );

            Glide.with(mContext)
                    .load(arrayList.get(position).image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .override((int) cellWidth, (int) cellHeight)
                    .into(holder.imageViewFitPaper);

        } else if (height_ratio == 15.0 && width_ratio == 20.0) {
            Log.e(TAG, "inside 20:15 " );

            cellWidth=device_width / 2 - 30;
            cellHeight= (float) (cellWidth*0.75);

            params = new ConstraintLayout.LayoutParams(
                    (int)cellWidth,
                    (int)cellHeight
            );

            Glide.with(mContext)
                    .load(arrayList.get(position).image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .override((int) cellWidth, (int)cellHeight)
                    .into(holder.imageViewFitPaper);

        } else if (height_ratio == 20.0 && width_ratio == 15.0) {
            Log.e(TAG, "inside 15:20 " );

            cellWidth=device_width / 2 - 30;
            cellHeight= (float) (cellWidth*1.33);
            Log.e(TAG, "cell width and height else if 15:20: "+cellWidth+" "+cellHeight );

            params = new ConstraintLayout.LayoutParams(
                    (int)cellWidth,
                    (int)cellHeight
            );
            Glide.with(mContext)
                    .load(arrayList.get(position).image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .override((int) cellWidth, (int) cellHeight)
                    .into(holder.imageViewFitPaper);

        } else {

            Log.e(TAG, "inside ---- " );

            cellWidth=device_width / 2 - 30;
            cellHeight=cellWidth;

            params = new ConstraintLayout.LayoutParams(
                    (int)cellWidth,
                    (int)cellHeight
            );

            Glide.with(mContext)
                    .load(arrayList.get(position).image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .override((int) cellWidth, (int)cellHeight)
                    .into(holder.imageViewFitPaper);
        }

        params.setMargins(0, 10, 0, 0);
        holder.msnackView.setLayoutParams(params);


        /*if (pos_x + cgwidth > original_width) {
            if (cgwidth > original_width) {

                Log.e(TAG, "onBindViewHolder: one");
                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                holder.imagecontainer.setLayoutParams(layoutParams);

                Glide.with(mContext)
                        .load(arrayList.get(position).image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .override((int) original_width, (int) cgheight)
                        .into(holder.imageViewFitPaper);

            } else {
                Log.e(TAG, "onBindViewHolder: two");
                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                holder.imagecontainer.setLayoutParams(layoutParams);

                Glide.with(mContext)
                        .load(arrayList.get(position).image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .override((int) cgwidth, (int) cgheight)
                        .into(holder.imageViewFitPaper);
            }
        } else if (pos_y + cgheight > original_height) {
            if (cgheight > original_height) {
                Log.e(TAG, "onBindViewHolder: three");
                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                holder.imagecontainer.setLayoutParams(layoutParams);

                Glide.with(mContext)
                        .load(arrayList.get(position).image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .override((int) cgwidth, (int) original_height)
                        .into(holder.imageViewFitPaper);


            } else {
                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                holder.imagecontainer.setLayoutParams(layoutParams);

                Log.e(TAG, "onBindViewHolder: fourth");
                Glide.with(mContext)
                        .load(arrayList.get(position).image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .override((int) cgwidth, (int) cgheight)
                        .into(holder.imageViewFitPaper);
            }

        } else {
            Log.e(TAG, "onBindViewHolder: fifth " + "cgwidth: " + cgwidth + " cgheight: " + cgheight);

            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            holder.imagecontainer.setLayoutParams(layoutParams);


            if (paper_cropping_type.equalsIgnoreCase("White Frame")) {
                Glide.with(mContext)
                        .load(arrayList.get(position).image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .override((int) device_width / 2 - 45, (int) (device_width / 2 - 45) * 3 / 2)
                        .into(holder.imageViewFitPaper);
            } else {
                Glide.with(mContext)
                        .load(arrayList.get(position).image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .override((int) cgwidth, (int) cgheight)
                        .into(holder.imageViewFitPaper);
            }


        }*/


        holder.imageViewFitPaper.setBackgroundColor(mContext.getResources().getColor(R.color.white));

        Log.e("TAG", "Height ratio: " + height_ratio + " width_ratio:" + width_ratio);

        //For Fit Paper
        if (paper_cropping_id.equalsIgnoreCase("2")) {
            Log.e(TAG, "inside Fit Paper ");
            holder.imageViewFitPaper.setAdjustViewBounds(true);
            /*holder.viewtop.setVisibility(View.GONE);
            holder.viewbottom.setVisibility(View.GONE);
            holder.viewright.setVisibility(View.GONE);
            holder.viewleft.setVisibility(View.GONE);*/

            if (original_width>original_height){
                holder.msnackView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
               // holder.imageViewFitPaper.setPadding(13,9,13,9);
            }else {
                holder.msnackView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
               // holder.imageViewFitPaper.setPadding(13,0,13,0);
            }

        }

        // For white frame
        if (paper_cropping_id.equalsIgnoreCase("1")) {
            Log.e(TAG, "inside white frame: ");
            Log.e(TAG, " original_width "+original_width +" original_height "+original_height );


            if (original_width>original_height){

                //holder.imagecontainer.setBackgroundColor(mContext.getResources().getColor(R.color.w));
                cellWidth=device_width / 2 - 30;

                cellHeight= (float) (cellWidth*0.6667);
                Log.e(TAG, "imagecontainer width and height : "+cellWidth+" , "+cellHeight );

                params = new ConstraintLayout.LayoutParams(
                        (int) cellWidth,
                        (int)cellHeight
                );
                holder.imageViewFitPaper.setVisibility(View.VISIBLE);

                holder.imagecontainer.setLayoutParams(params);


                holder.imageViewFitPaper.setAdjustViewBounds(false);
                holder.msnackView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                holder.imageViewFitPaper.setScaleType(ImageView.ScaleType.FIT_XY);
                holder.imageViewFitPaper.setPadding(21,21,21,21);

            }else if (original_height>original_width){
                holder.msnackView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                holder.imageViewFitPaper.setScaleType(ImageView.ScaleType.FIT_XY);
                holder.imageViewFitPaper.setPadding(24,21,24,21);
                /*FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams((int)cellWidth, (int)cellHeight);
                layoutParams.setMarginStart(10);
                layoutParams.setMarginEnd(10);
                holder.imagecontainer.setLayoutParams(layoutParams);*/
                //holder.imagecontainer.setPadding(20,0,20,0);

                holder.imageViewFitPaper.setAdjustViewBounds(false);
            }else {

                //holder.imagecontainer.setBackgroundColor(mContext.getResources().getColor(R.color.w));
                cellWidth=device_width / 2 - 30;

                cellHeight= (float) (cellWidth*0.6667);
                Log.e(TAG, "imagecontainer width and height : "+cellWidth+" , "+cellHeight );

                params = new ConstraintLayout.LayoutParams(
                        (int) cellWidth,
                        (int)cellHeight
                );
                holder.imageViewFitPaper.setVisibility(View.VISIBLE);

                holder.imagecontainer.setLayoutParams(params);


                holder.imageViewFitPaper.setAdjustViewBounds(false);
               holder.imageViewFitPaper.setScaleType(ImageView.ScaleType.FIT_XY);
                holder.msnackView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                holder.imageViewFitPaper.setPadding(21,21,21,21);
            }




           /* holder.viewtop.setVisibility(View.VISIBLE);
            holder.viewbottom.setVisibility(View.VISIBLE);
            holder.viewright.setVisibility(View.VISIBLE);
            holder.viewleft.setVisibility(View.VISIBLE);*/

           //holder.imageViewFitPaper.setPadding(8,7,8,7);
            //holder.imagecontainer.setPadding(8,7,8,7);





            /*android.view.ViewGroup.LayoutParams layoutParams = holder.viewright.getLayoutParams();
            layoutParams.width = 50;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            holder.viewright.setLayoutParams(layoutParams);*/





            //holder.imagecontainer.setPadding(7,0,7,0);

        }


        //For fit picture
        if (paper_cropping_id.equalsIgnoreCase("3")) {
            Log.e(TAG, "inside fit picture: ");
            holder.imageViewFitPaper.setAdjustViewBounds(true);



            if (original_height > original_width) {



                if (width_ratio == 10.0 && height_ratio == 15.0) {

                    float aspect_ratio = (float) original_width / (float) original_height;
                    double roundOffAspectRatio = Math.round(aspect_ratio * 100.0) / 100.0;
                    Log.e(TAG, "onBindViewHolder: aspect ratio " + aspect_ratio + " round of at 2 places " + roundOffAspectRatio);
                    //To check if image is already in ratio
                    if (roundOffAspectRatio < 0.70 && roundOffAspectRatio > 0.60) {
                        holder.msnackView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));


                    } else {

/*
                        if (original_width>original_height){
                            holder.msnackView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                            holder.imageViewFitPaper.setPadding(13,9,13,9);
                        }else {
                            holder.msnackView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                            holder.imageViewFitPaper.setPadding(13,0,13,0);
                        }*/



                        if (original_height>original_width){

                            holder.msnackView.setBackgroundColor(mContext.getResources().getColor(R.color.white));

                            Glide.with(mContext)
                                    .load(arrayList.get(position).image)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .centerCrop()
                                    .override((int) original_width, (int)original_height)
                                    .into(holder.imageViewFitPaper);

                        }else {



                            holder.imagecontainer.setBackgroundColor(mContext.getResources().getColor(R.color.white));


                            Glide.with(mContext)
                                    .load(arrayList.get(position).image)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .centerCrop()
                                    .override((int) original_width, (int)original_height)
                                    .into(holder.imageViewFitPaper);



                        }



                    }



                }


                if (width_ratio == 15.0 && height_ratio == 20.0) {

                    /*float aspect_ratio = (float) original_width / (float) original_height;
                    double roundOffAspectRatio = Math.round(aspect_ratio * 100.0) / 100.0;
                    Log.e(TAG, "onBindViewHolder: aspect ratio 3:4 " + aspect_ratio + " round of at 2 places " + roundOffAspectRatio);
                    //To check if image is already in ratio
                    if (roundOffAspectRatio < 0.70 && roundOffAspectRatio > 0.80) {
                        *//*holder.viewtop.setVisibility(View.GONE);
                        holder.viewbottom.setVisibility(View.GONE);
                        holder.viewright.setVisibility(View.GONE);
                        holder.viewleft.setVisibility(View.GONE);*//*
                    } else {
                       *//* holder.viewtop.setVisibility(View.GONE);
                        holder.viewbottom.setVisibility(View.GONE);
                        holder.viewright.setVisibility(View.VISIBLE);
                        holder.viewleft.setVisibility(View.VISIBLE);*//*
                        if (original_width>original_height){
                            holder.msnackView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                            holder.imageViewFitPaper.setPadding(13,9,13,9);
                        }else {
                            holder.msnackView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                            holder.imageViewFitPaper.setPadding(13,0,13,0);
                        }
                    }*/

                    holder.msnackView.setBackgroundColor(mContext.getResources().getColor(R.color.white));


                    if (original_height>original_width){

                    float aspect_ratio = (float) original_width / (float) original_height;
                    double roundOffAspectRatio = Math.round(aspect_ratio * 100.0) / 100.0;
                    Log.e(TAG, "onBindViewHolder: aspect ratio 3:4 " + aspect_ratio + " round of at 2 places " + roundOffAspectRatio);
                    //To check if image is already in ratio
                    if (roundOffAspectRatio < 0.70 && roundOffAspectRatio > 0.80) {
                        holder.msnackView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                    } else {

                        holder.msnackView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                        Glide.with(mContext)
                                .load(arrayList.get(position).image)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .centerCrop()
                                .override((int) original_width, (int)original_height)
                                .into(holder.imageViewFitPaper);

                        holder.msnackView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                    }


                    }
                }



                if (width_ratio == 20.0 && height_ratio == 30.0) {

                    float aspect_ratio = (float) original_width / (float) original_height;
                    double roundOffAspectRatio = Math.round(aspect_ratio * 100.0) / 100.0;
                    Log.e(TAG, "onBindViewHolder: aspect ratio 3:4 " + aspect_ratio + " round of at 2 places " + roundOffAspectRatio);
                    //To check if image is already in ratio
                    if (roundOffAspectRatio < 0.70 && roundOffAspectRatio > 0.60) {

                        holder.msnackView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                    } else {

                       /* if (original_width>original_height){
                            holder.msnackView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                            holder.imageViewFitPaper.setPadding(13,9,13,9);
                        }else {
                            holder.msnackView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                            holder.imageViewFitPaper.setPadding(13,0,13,0);
                        }*/
                        holder.msnackView.setBackgroundColor(mContext.getResources().getColor(R.color.white));


                        if (original_height>original_width){


                            Glide.with(mContext)
                                    .load(arrayList.get(position).image)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .centerCrop()
                                    .override((int) original_width, (int)original_height)
                                    .into(holder.imageViewFitPaper);

                            holder.msnackView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                        }
                    }

                }


            } else {

                Log.e(TAG, "width is greater----: "+cgwidth+" "+cgheight +" "+cellWidth+" "+cellHeight );


                if (cellWidth>cellHeight){

                    params = new ConstraintLayout.LayoutParams(
                            (int)cellWidth,
                            (int)cellHeight
                    );

                    holder.imagecontainer.setLayoutParams(params);
                    holder.imagecontainer.setBackgroundColor(mContext.getResources().getColor(R.color.white));

                    holder.msnackView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                }


                    //holder.msnackView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                    //holder.imageViewFitPaper.setPadding(13,9,13,9);

                Glide.with(mContext)
                        .load(arrayList.get(position).image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .override((int) original_width, (int)original_height)
                        .into(holder.imageViewFitPaper);

                    //holder.imageViewFitPaper.setPadding(0,13,0,13);

                /*holder.viewtop.setVisibility(View.VISIBLE);
                holder.viewbottom.setVisibility(View.VISIBLE);
                holder.viewright.setVisibility(View.GONE);
                holder.viewleft.setVisibility(View.GONE);*/
            }
        }


        //To set quantity of photo
        holder.txt_count.setText(arrayList.get(position).quantity);

        //To redirect on EditPhotoActivity
        holder.msnackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - click_time < delay)
                    return;
                click_time = SystemClock.elapsedRealtime();
                Intent intent = new Intent(mContext, EditPhotoActivity.class);
                intent.putExtra("image", arrayList.get(position).image);
                intent.putExtra("quantity", arrayList.get(position).quantity);
                intent.putExtra("papercropping", paper_cropping_type);
                intent.putExtra("paper_cropping_id", paper_cropping_id);
                intent.putExtra("height_ratio", height_ratio);
                intent.putExtra("width_ratio", width_ratio);
                intent.putExtra("after_width", after_width);
                intent.putExtra("after_height", after_height);
                intent.putExtra("cg_height", cgheight);
                intent.putExtra("cg_width", cgwidth);
                pos = position;
                Log.e("TAG", "Height_ratio:" + after_height + " width_ratio:" + after_width);
                ((PhotosOverviewActivity) mContext).startActivityForResult(intent, 5);

            }
        });


        //To delete image
        holder.Iv_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext, R.style.AlertDialog);
                builder.setTitle("");
                builder.setMessage(R.string.Are_u_sure_To_delete);
                builder.setPositiveButton(mContext.getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        arrayList.remove(position);
                        notifyDataSetChanged();

                        int currentQuantity = 1;
                        for (int i = 0; i < arrayList.size(); i++) {
                            currentQuantity = currentQuantity + Integer.parseInt(arrayList.get(i).quantity);
                        }
                        Log.e("FitPaperAdapter", "onActivityResult current: " + currentQuantity);
                        ((PhotosOverviewActivity) mContext).setQuantity((currentQuantity));

                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                android.support.v7.app.AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });




    }

    public int dpToPx(int dp) {
        float density = mContext.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public void newItem(String quantity, String edited_image, String papercropping) {
        Log.e("FitPaperAdapter", "edited byteArray: " + edited_image);
        arrayList.get(pos).image = edited_image;
        arrayList.get(pos).quantity = quantity;
        paper_cropping_type = papercropping;

        notifyDataSetChanged();
    }


    private void getDropboxIMGSize(Uri uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);
        original_height = options.outHeight;
        original_width = options.outWidth;
        Log.e("TAG", "getDropboxIMGSize: " + original_width + " height" + original_height);

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewFitPaper;
        ImageView viewtop, viewbottom, viewleft, viewright;
        RelativeLayout rlt_white;
        ConstraintLayout msnackView;
        ConstraintLayout imagecontainer;
        CustomTextViewNormal txt_count;
        ImageView Iv_cross;

        public ViewHolder(View itemView) {
            super(itemView);
            imageViewFitPaper = (ImageView) itemView.findViewById(R.id.imageViewFitPaper);

            msnackView = itemView.findViewById(R.id.msnackView);
            imagecontainer = itemView.findViewById(R.id.imagecontainer);
            rlt_white = (RelativeLayout) itemView.findViewById(R.id.rlt_white);
            txt_count = (CustomTextViewNormal) itemView.findViewById(R.id.txt_count);
            Iv_cross = (ImageView) itemView.findViewById(R.id.Iv_cross);
            viewtop = (ImageView) itemView.findViewById(R.id.viewtop);
            viewbottom = (ImageView) itemView.findViewById(R.id.viewbottom);
            viewleft = (ImageView) itemView.findViewById(R.id.viewleft);
            viewright = (ImageView) itemView.findViewById(R.id.viewright);

        }
    }




}
