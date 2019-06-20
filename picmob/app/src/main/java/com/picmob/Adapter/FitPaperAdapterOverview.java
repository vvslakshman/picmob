package com.picmob.Adapter;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.picmob.Activity.EditPhotoActivity;
import com.picmob.Activity.PhotosOverviewActivity;
import com.picmob.AppCustomView.CustomTextViewNormal;
import com.picmob.AppCustomView.RoundRectMeasure;
import com.picmob.Models.CalculationModel;
import com.picmob.Models.PhotoOverviewModel;
import com.picmob.R;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

public class FitPaperAdapterOverview extends RecyclerView.Adapter<FitPaperAdapterOverview.ViewHolder> {


    Context mContext;
    long click_time=0;
    long delay=700;
    public ArrayList<PhotoOverviewModel> arrayList = new ArrayList<>();
    String paper_cropping_type;
    float original_height_ratio,original_width_ratio;
    float height_ratio,width_ratio;
    public int pos=-1;
    float original_width;
    float original_height;
    float cgheight=0;
    float cgwidth=0;
    float after_height=0;
    float after_width=0;
    float pos_x=0;
    float pos_y=0;
    private final String TAG="FitPaperAdapter=> ";

    ArrayList<CalculationModel> calculationModelArrayList;
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage";

    public FitPaperAdapterOverview(ArrayList<PhotoOverviewModel> arrayList,Context mContext,String type,float height,float width) {
        this.arrayList=arrayList;
        this.mContext = mContext;
        this.paper_cropping_type=type;
        this.height_ratio=height;
        this.width_ratio=width;
        this.original_height_ratio=height;
        this.original_width_ratio=width;
        calculationModelArrayList=new ArrayList<>();
        calculationModelArrayList.clear();

        Log.e("TAG", "width and height : "+width_ratio+" , "+height_ratio );
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fit_paper_row_one, parent, false);
        return new ViewHolder(view);
    }


    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        float tmp_ratio_height = height_ratio;
        float tmp_ratio_width = width_ratio;

        //   String images=arrayList.get(position).image;

        if (paper_cropping_type.equalsIgnoreCase("Fit Paper")) {
            holder.imageViewFitPaper.setVisibility(View.VISIBLE);
            holder.imageViewWhiteFrame.setVisibility(View.GONE);
            holder.imageViewFitPicture.setVisibility(View.GONE);
            holder.msnackView.setBackground(null);

            Log.e("TAG", "FITPAPER " );


            //  File image = new File(arrayList.get(position).image);
            //  Log.e("TAG", "on Image "+image );
            //  Bitmap bitmap=null;
//
//            if (image.toString().startsWith("https"))
//            {
//
//                //to convert image in URI
//                Uri myUri = Uri.parse(image.toString());
//                Log.e("TAG", "URI: "+myUri );
//
//                try {
//                    URL url = new URL(arrayList.get(position).image);
//                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                    connection.setDoInput(true);
//                    connection.connect();
//                    InputStream input = connection.getInputStream();
//                     bitmap = BitmapFactory.decodeStream(input);
//                } catch (IOException e) {
//                    e.printStackTrace();
//
//                }
//
//          }
//            else {

//            Bitmap   bitmap = BitmapFactory.decodeFile(new File(arrayList.get(position).image).getPath());
////            }
//            Log.e("TAG", "BITMAP: "+bitmap );
//            float original_width = bitmap.getWidth();
//            float original_height = bitmap.getHeight();
//            Log.e("TAG", "before calculation width: "+original_width +" height:"+original_height );


            float after_height=0;
            float after_width=0;
            float pos_x=0;
            float pos_y=0;

            getDropboxIMGSize(Uri.parse(arrayList.get(position).image));


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







            DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
            int device_width = displayMetrics.widthPixels;

            ConstraintLayout.LayoutParams params;

            if (height_ratio == 10.0 && width_ratio == 15.0) {

                if (after_height>after_width){

                    params = new ConstraintLayout.LayoutParams(
                            device_width /2 - 45,
                            (device_width / 2 -45)*2/3
                    );

                }else {

                    params = new ConstraintLayout.LayoutParams(
                            device_width /2 - 45,
                            (device_width / 2 -45)*2/3
                    );

                }
            }

            else if (height_ratio == 15.0 && width_ratio == 10.0) {

                if (after_height>after_width) {
                    params = new ConstraintLayout.LayoutParams(
                            device_width / 2 - 45,
                            (device_width / 2 - 45)*3/2
                    );
                }else {
                    params = new ConstraintLayout.LayoutParams(
                            device_width / 2 - 45,
                            (device_width / 2 - 45)*3/2
                    );
                }

            }
            else if (height_ratio == 20.0 && width_ratio == 30.0) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *2/3
                );

            }  else if (height_ratio == 30.0 && width_ratio == 20.0) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *3/2
                );



            }else if (height_ratio == 15.0 && width_ratio == 20.0) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *3/4
                );


            }else if (height_ratio == 20.0 && width_ratio == 15.0) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *4/3
                );


            }else {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45)
                );


            }
            params.setMargins(10, 10, 10, 10);


            holder.msnackView.setLayoutParams(params);


//            if (after_width>after_height) {
//
//                if (height_ratio == 10.0 && width_ratio == 15.0) {
//
//                    layoutParams.width = device_width / 2 - 33;
//                    layoutParams.height = (device_width / 2 - 33) * 2 / 3;
//                    holder.imageViewFitPaper.setLayoutParams(layoutParams);
//                }
//
//
//              else   if (height_ratio == 15.0 && width_ratio == 10.0) {
//                    layoutParams.width = device_width / 2 - 33;
//                    layoutParams.height = (device_width / 2 - 33) * 3 / 2;
//                    holder.imageViewFitPaper.setLayoutParams(layoutParams);
//                }
//                else if (height_ratio == 20.0 && width_ratio == 30.0) {
//
//                    layoutParams.width = device_width / 2 - 33;
//                    layoutParams.height = (device_width / 2 - 33) * 2 / 3;
//                    holder.imageViewFitPaper.setLayoutParams(layoutParams);
//                }
//
//                else if (height_ratio == 30.0 && width_ratio == 20.0) {
//
//                    layoutParams.width = device_width / 2 - 33;
//                    layoutParams.height = (device_width / 2 - 33) * 3 / 2;
//                    holder.imageViewFitPaper.setLayoutParams(layoutParams);
//                }
//                else if (height_ratio == 15.0 && width_ratio == 20.0) {
//
//                    layoutParams.width = device_width / 2 - 33;
//                    layoutParams.height = (device_width / 2 - 33) * 3 / 4;
//                    holder.imageViewFitPaper.setLayoutParams(layoutParams);
//                }
//                else if (height_ratio == 20.0 && width_ratio == 15.0) {
//
//                    layoutParams.width = device_width / 2 - 33;
//                    layoutParams.height = (device_width / 2 - 33) * 4 / 3;
//                    holder.imageViewFitPaper.setLayoutParams(layoutParams);
//                }
//                else {
//                    layoutParams.width = device_width / 2 - 33;
//                    layoutParams.height = (device_width / 2 - 33);
//
//                    holder.imageViewFitPaper.setLayoutParams(layoutParams);
//                }
//            } else  if (after_width < after_height) {
//
//                if (height_ratio == 10.0 && width_ratio == 15.0) {
//
//                    layoutParams.width = device_width / 2 - 33;
//                    layoutParams.height = (device_width / 2 - 33) * 2 / 3;
//                    holder.imageViewFitPaper.setLayoutParams(layoutParams);
//                }
//
//
//                else   if (height_ratio == 15.0 && width_ratio == 10.0) {
//                    layoutParams.width = device_width / 2 - 33;
//                    layoutParams.height = (device_width / 2 - 33) * 3 / 2;
//                    holder.imageViewFitPaper.setLayoutParams(layoutParams);
//                }
//                else if (height_ratio == 20.0 && width_ratio == 30.0) {
//
//                    layoutParams.width = device_width / 2 - 33;
//                    layoutParams.height = (device_width / 2 - 33) * 2 / 3;
//                    holder.imageViewFitPaper.setLayoutParams(layoutParams);
//                }
//
//                else if (height_ratio == 30.0 && width_ratio == 20.0) {
//
//                    layoutParams.width = device_width / 2 - 33;
//                    layoutParams.height = (device_width / 2 - 33) * 3 / 2;
//                    holder.imageViewFitPaper.setLayoutParams(layoutParams);
//                }
//                else if (height_ratio == 15.0 && width_ratio == 20.0) {
//
//                    layoutParams.width = device_width / 2 - 33;
//                    layoutParams.height = (device_width / 2 - 33) * 3 / 4;
//                    holder.imageViewFitPaper.setLayoutParams(layoutParams);
//                }
//                else if (height_ratio == 20.0 && width_ratio == 15.0) {
//
//                    layoutParams.width = device_width / 2 - 33;
//                    layoutParams.height = (device_width / 2 - 33) * 4 / 3;
//                    holder.imageViewFitPaper.setLayoutParams(layoutParams);
//                }
//                else {
//                    layoutParams.width = device_width / 2 - 33;
//                    layoutParams.height = (device_width / 2 - 33);
//
//                    holder.imageViewFitPaper.setLayoutParams(layoutParams);
//                }
//            }

            if (pos_x+cgwidth>original_width)
            {
                if (cgwidth>original_width){

                    Glide.with(mContext)
                            .load(arrayList.get(position).image)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop()
                            .override((int)original_width,(int)cgheight)
                            .into(holder.imageViewFitPaper);
                }
                else{

                    Glide.with(mContext)
                            .load(arrayList.get(position).image)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop()
                            .override((int)cgwidth,(int)cgheight)
                            .into(holder.imageViewFitPaper);
                }
            }

            else if (pos_y+cgheight>original_height)
            {
                if (cgheight>original_height){
                    Glide.with(mContext)
                            .load(arrayList.get(position).image)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop()
                            .override((int)cgwidth,(int)original_height)
                            .into(holder.imageViewFitPaper);


                }
                else{
                    Glide.with(mContext)
                            .load(arrayList.get(position).image)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop()
                            .override((int)cgwidth,(int)cgheight)
                            .into(holder.imageViewFitPaper);
                }

            }else{
                Glide.with(mContext)
                        .load(arrayList.get(position).image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .override((int)cgwidth,(int)cgheight)
                        .into(holder.imageViewFitPaper);
            }




            Log.e("TAG", "Height ratio: "+height_ratio +" width_ratio:"+width_ratio );
            holder.imageViewFitPaper.setBackground(null);




        }



        else if (paper_cropping_type.equalsIgnoreCase("White Frame")){
            holder.imageViewFitPaper.setVisibility(View.VISIBLE);
            holder.imageViewWhiteFrame.setVisibility(View.GONE);
            holder.imageViewFitPicture.setVisibility(View.GONE);

//            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//            Bitmap   bitmap = BitmapFactory.decodeFile(new File(arrayList.get(position).image).getPath());
//           }
//            Log.e("TAG", "BITMAP: "+bitmap );
//            float original_width = bitmap.getWidth();
//            float original_height = bitmap.getHeight();
//            Log.e("TAG", "before calculation width: "+original_width +" height:"+original_height );

            getDropboxIMGSize(Uri.parse(arrayList.get(position).image));

            float after_height=0;
            float after_width=0;
            float pos_x=0;
            float pos_y=0;

            if (height_ratio == 10.0 && width_ratio == 15.0) {
                height_ratio = 86;
                width_ratio = 136;
            } else if (height_ratio == 15.0 && width_ratio == 20.0 ){
                height_ratio = 13;
                width_ratio = 18;
            } else if (height_ratio == 20.0 && width_ratio == 30.0) {
                height_ratio = 174;
                width_ratio = 274;
            }else if (height_ratio==15.0 && width_ratio==15.0){

                height_ratio=15;
                width_ratio=15;
            }


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
                }else if (width_ratio==height_ratio){

                    width_ratio=height_ratio;
                    height_ratio=width_ratio;
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


           // cgwidth=0;
         //   cgheight=0;

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


            Log.e("TAG", "after calculation width White: "+after_width +"height:"+after_height );
            Log.e("TAG", "position x:"+pos_x +" pos_y:"+ pos_y );
            Log.e("TAG", "cgHeight: "+cgheight +" cgWidth:"+cgwidth );


            DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
            int device_width = displayMetrics.widthPixels;

//            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.imageViewWhiteFrame.getLayoutParams();
//            if (after_width>after_height) {
//                if (height_ratio == 10.0 && width_ratio == 15.0) {
//                    layoutParams.width = device_width / 2 - 33;
//                    layoutParams.height = (device_width / 2 - 33) *2/3 ;
////                    layoutParams.setMargins(6,6,6,6);
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }
//                else if (height_ratio == 15.0 && width_ratio == 10.0) {
//                    layoutParams.width = device_width / 2 - 33;
//                    layoutParams.height = (device_width / 2 - 33) *3/2 ;
////                    layoutParams.setMargins(6,6,6,6);
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }
//                else if (height_ratio == 20.0 && width_ratio == 30.0) {
//
//
//                    layoutParams.width = device_width / 2 - 85;
//                    layoutParams.height = (device_width / 2 - 85) * 2 / 3;
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }  else if (height_ratio == 30.0 && width_ratio == 20.0) {
//
//
//                    layoutParams.width = device_width / 2 - 85;
//                    layoutParams.height = (device_width / 2 - 85) * 3 / 2;
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }else if (height_ratio == 15.0 && width_ratio == 20.0) {
//                    layoutParams.width = device_width / 2 - 85;
//                    layoutParams.height = (device_width / 2 - 85) * 3 / 4;
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                } else if (height_ratio == 20.0 && width_ratio == 15.0) {
//
//
//                    layoutParams.width = device_width / 2 - 85;
//                    layoutParams.height = (device_width / 2 - 85) * 4 / 3;
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }else {
//                    layoutParams.width = device_width / 2 - 33;
//                    layoutParams.height = (device_width / 2 - 33);
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }
//
//
//
//            }else if (after_height>after_width){
//                if (height_ratio == 10.0 && width_ratio == 15.0) {
//                    layoutParams.width = device_width / 2 -33;
//                    layoutParams.height = (device_width / 2 - 33) * 2/3 ;
//               //     layoutParams.setMargins(6,6,6,6);
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }
//                else if (height_ratio == 15.0 && width_ratio == 10.0) {
//                    layoutParams.width = device_width / 2 - 33;
//                    layoutParams.height = (device_width / 2 - 33) * 3 / 2;
//              //      layoutParams.setMargins(6,6,6,6);
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }
//                else if (height_ratio == 20.0 && width_ratio == 30.0) {
//                    layoutParams.width = device_width / 2 - 85;
//                    layoutParams.height = (device_width / 2 - 85) * 2 / 3;
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }  else if (height_ratio == 30.0 && width_ratio == 20.0) {
//                    layoutParams.width = device_width / 2 - 85;
//                    layoutParams.height = (device_width / 2 - 85) * 3 / 2;
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }else if (height_ratio == 15.0 && width_ratio == 20.0) {
//                    layoutParams.width = device_width / 2 - 85;
//                    layoutParams.height = (device_width / 2 - 85) * 3 / 4;
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                } else if (height_ratio == 20.0 && width_ratio == 15.0) {
//                    layoutParams.width = device_width / 2 - 85;
//                    layoutParams.height = (device_width / 2 - 85) * 4 / 3;
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }else {
//                    layoutParams.width = device_width / 2 - 33;
//                    layoutParams.height = (device_width / 2 - 33);
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }
//            }
//            else {
//
//                Log.e("LAYOUT", "onBindViewHolder: image layout ELSE " );
//
//            }




//            RelativeLayout.LayoutParams paramsr;
//
//
//
//            if (height_ratio == 10.0 && width_ratio == 15.0) {
//                paramsr = new RelativeLayout.LayoutParams(
//                        device_width /2 - 40,
//                        (device_width / 2 -40) * 2/3
//                );
//
//
//            }
//
//            else if (height_ratio == 15.0 && width_ratio == 10.0) {
//                paramsr = new RelativeLayout.LayoutParams(
//                        device_width /2 - 40,
//                        (device_width / 2 -40) * 3/2
//                );
//
//
//            }
//            else if (height_ratio == 20.0 && width_ratio == 30.0) {
//                paramsr = new RelativeLayout.LayoutParams(
//                        device_width /2 - 40,
//                        (device_width / 2 -40) + 10
//                );
//
//            }  else if (height_ratio == 30.0 && width_ratio == 20.0) {
//                paramsr = new RelativeLayout.LayoutParams(
//                        device_width /2 - 40,
//                        (device_width / 2 -40)  + 10
//                );
//
//
//
//            }else if (height_ratio == 15.0 && width_ratio == 20.0) {
//                paramsr = new RelativeLayout.LayoutParams(
//                        device_width /2 - 40,
//                        (device_width / 2 -40)  +10
//                );
//
//
//            }else if (height_ratio == 20.0 && width_ratio == 15.0) {
//                paramsr = new RelativeLayout.LayoutParams(
//                        device_width /2 - 40,
//                        (device_width / 2 -40) +10
//                );
//
//
//            }else {
//                paramsr = new RelativeLayout.LayoutParams(
//                        device_width /2 - 40,
//                        (device_width / 2 -40) - 22
//                );
//
//
//            }
//
//            if (after_height>after_width){
//
//                params.setMargins(20,10,20,10);
//
//            }else if (after_width>after_height){
//                params.setMargins(10,20,10,20);
//            }
//
//
//            holder.rlt_white.setLayoutParams(paramsr);





            float hhratio=original_height_ratio;
            float wwratio=original_width_ratio;


            if (original_width >= original_height) {
                if (wwratio < hhratio) {

                    float width_r = wwratio;
                    wwratio = hhratio;
                    hhratio = width_r;

                }
            }else {
                if (wwratio > hhratio) {
                    float width_r = wwratio;
                    wwratio = hhratio;
                    hhratio = width_r;
                }
            }

            Log.e("HHRATIO", "onBindViewHolder: "+hhratio+"wwRatio:"+wwratio );
            ConstraintLayout.LayoutParams params;

            if (hhratio == 10.0 && wwratio == 15.0) {

                if (after_height>after_width){

                    params = new ConstraintLayout.LayoutParams(
                            device_width /2 - 45,
                            (device_width / 2 -45)*2/3
                    );

                }else {

                    params = new ConstraintLayout.LayoutParams(
                            device_width /2 - 45,
                            (device_width / 2 -45)*2/3
                    );
                }
            }

            else if (hhratio == 15.0 && wwratio == 10.0) {
                Log.e("INSIDE", "onBindViewHolder:15 10 " );

                if (after_height>after_width) {
                    params = new ConstraintLayout.LayoutParams(
                            device_width / 2 - 45,
                            (device_width / 2 - 45)*3/2
                    );
                }else {
                    params = new ConstraintLayout.LayoutParams(
                            device_width / 2 - 45,
                            (device_width / 2 - 45)*3/2
                    );


                }


            }
            else if (hhratio == 20.0 && wwratio == 30.0) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *2/3
                );

            }  else if (hhratio == 30.0 && wwratio == 20.0) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *3/2
                );



            }else if (hhratio == 15.0 && wwratio == 20.0) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *3/4
                );


            }else if (hhratio == 20.0 && wwratio == 15.0) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *4/3
                );


            }else if (hhratio == 86 && wwratio == 136) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *86/136
                );


            }else if (hhratio == 136 && wwratio == 86) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *136/86
                );


            }else if (hhratio == 13 && wwratio == 18) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *13/18
                );


            }else if (hhratio == 18 && wwratio == 13) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *18/13
                );


            }else if (hhratio == 174 && wwratio == 274) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *174/274
                );


            }else if (hhratio == 274 && wwratio == 174) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *274/174
                );


            }else {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45)
                );


            }
            Log.e("TAG", "Height ratio: "+height_ratio +" width_ratio:"+width_ratio );
            Log.e("CG", "CGHeight: "+cgheight +" CGWIDTH:"+cgwidth );


            params.setMargins(0, 10, 0, 0);
            holder.msnackView.setLayoutParams(params);
            //holder.imageViewWhiteFrame.setScaleType(ImageView.ScaleType.CENTER_CROP);


            if (pos_x+cgwidth>original_width)
            {
                if (cgwidth>original_width){

                    Log.e("CHECK", "onBindViewHolder: first1" );
                    Glide.with(mContext)
                            .load(arrayList.get(position).image)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .override((int)original_width,(int)cgheight)
                            .centerCrop()
                            .into(holder.imageViewFitPaper);
                }
                else{
                    Log.e("CHECK", "onBindViewHolder: first2" );
                    Glide.with(mContext)
                            .load(arrayList.get(position).image)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .override((int)cgwidth,(int)cgheight)
                            .centerCrop()
                            .into(holder.imageViewFitPaper);
                }
            }

            else if (pos_y+cgheight>original_height)
            {
                if (cgheight>original_height){
                    Log.e("CHECK", "onBindViewHolder: first32" );
                    Glide.with(mContext)
                            .load(arrayList.get(position).image)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .override((int)cgwidth,(int)original_height)
                            .centerCrop()
                            .into(holder.imageViewFitPaper);


                }
                else{
                    Log.e("CHECK", "onBindViewHolder: first3" );
                    Glide.with(mContext)
                            .load(arrayList.get(position).image)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .override((int)cgwidth,(int)cgheight)
                            .centerCrop()
                            .into(holder.imageViewFitPaper);
                }

            }else{

//                if (hhratio==15.0 && wwratio==15.0) {
//                    Log.e("CHECK", "onBindViewHolder: last");
//
////
////                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
////                    Bitmap bitmap = BitmapFactory.decodeFile(new File(arrayList.get(position).image).getPath());
////                      bitmap.compress( Bitmap.CompressFormat.PNG,100,stream);
////                         holder.imageViewFitPaper.setImageBitmap(bitmap);
////                        holder.imageViewFitPaper.setScaleType(ImageView.ScaleType.CENTER_CROP);
////
//                    Glide.with(mContext)
//                            .load(arrayList.get(position).image)
//                            .asBitmap()
//                            .centerCrop()
//                            .override((int) cgwidth, (int) cgheight)
//                            .into(holder.imageViewFitPaper);
//
//
//                }
//                else {
                    Glide.with(mContext)
                            .load(arrayList.get(position).image)
                            .asBitmap()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .override((int) cgwidth, (int) cgheight)
                            .centerCrop()
                            .into(holder.imageViewFitPaper);
//                }
            }


//            if (original_height>original_width)
//                holder.msnackView.setBackgroundColor(mContext.getResources().getColor(R.color.white));


            if (hhratio==15.0 && wwratio==15.0){
                Log.e("MATCH", "HEIGHT MATCH " );

                holder.imageViewFitPaper.setPadding(24,24,24,24);
                holder.imageViewFitPaper.setBackground(mContext.getResources().getDrawable(R.drawable.white_frame_bg_width));
            }
            else {
                holder.imageViewFitPaper.setPadding(16,16,16,16);
                holder.imageViewFitPaper.setBackground(mContext.getResources().getDrawable(R.drawable.white_frame_bg));
            }
        }


        //Paper fit picture start
        else {

            Log.e(TAG, "onBindViewHolder: "+ "fit picture selected" );


            holder.imageViewFitPaper.setVisibility(View.VISIBLE);
            holder.imageViewWhiteFrame.setVisibility(View.GONE);
            holder.imageViewFitPicture.setVisibility(View.GONE);

//            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//            Bitmap   bitmap = BitmapFactory.decodeFile(new File(arrayList.get(position).image).getPath());
////            }
//            Log.e("TAG", "BITMAP: "+bitmap );
//            float original_width = bitmap.getWidth();
//            float original_height = bitmap.getHeight();
//            Log.e("TAG", "before calculation width: "+original_width +" height:"+original_height );

            /*getDropboxIMGSize(Uri.parse(arrayList.get(position).image));

            float after_height = 0;
            float after_width = 0;
            float pos_x = 0;
            float pos_y = 0;


            if (height_ratio == 10.0 && width_ratio == 15.0) {
                height_ratio = 86;
                width_ratio = 136;
            } else if (height_ratio == 15.0 && width_ratio == 20.0 ){
                height_ratio = 13;
                width_ratio = 18;
            } else if (height_ratio == 20.0 && width_ratio == 30.0) {
                height_ratio = 174;
                width_ratio = 274;
            }else if (height_ratio==15.0 && width_ratio==15.0){

                height_ratio=15;
                width_ratio=15;
            }


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
                }else if (width_ratio==height_ratio){

                    width_ratio=height_ratio;
                    height_ratio=width_ratio;
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
            cgheight=0;
            cgwidth=0;


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


            Log.e("TAG", "after calculation width White: " + after_width + "height:" + after_height);
            Log.e("TAG", "position x:" + pos_x + " pos_y:" + pos_y);
            Log.e("TAG", "cgHeight: " + cgheight + " cgWidth:" + cgwidth);


            DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
            int device_width = displayMetrics.widthPixels;

//            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.imageViewWhiteFrame.getLayoutParams();
//            if (after_width>after_height) {
//                if (height_ratio == 10.0 && width_ratio == 15.0) {
//                    layoutParams.width = device_width / 2 - 33;
//                    layoutParams.height = (device_width / 2 - 33) *2/3 ;
////                    layoutParams.setMargins(6,6,6,6);
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }
//                else if (height_ratio == 15.0 && width_ratio == 10.0) {
//                    layoutParams.width = device_width / 2 - 33;
//                    layoutParams.height = (device_width / 2 - 33) *3/2 ;
////                    layoutParams.setMargins(6,6,6,6);
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }
//                else if (height_ratio == 20.0 && width_ratio == 30.0) {
//
//
//                    layoutParams.width = device_width / 2 - 85;
//                    layoutParams.height = (device_width / 2 - 85) * 2 / 3;
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }  else if (height_ratio == 30.0 && width_ratio == 20.0) {
//
//
//                    layoutParams.width = device_width / 2 - 85;
//                    layoutParams.height = (device_width / 2 - 85) * 3 / 2;
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }else if (height_ratio == 15.0 && width_ratio == 20.0) {
//                    layoutParams.width = device_width / 2 - 85;
//                    layoutParams.height = (device_width / 2 - 85) * 3 / 4;
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                } else if (height_ratio == 20.0 && width_ratio == 15.0) {
//
//
//                    layoutParams.width = device_width / 2 - 85;
//                    layoutParams.height = (device_width / 2 - 85) * 4 / 3;
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }else {
//                    layoutParams.width = device_width / 2 - 33;
//                    layoutParams.height = (device_width / 2 - 33);
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }
//
//
//
//            }else if (after_height>after_width){
//                if (height_ratio == 10.0 && width_ratio == 15.0) {
//                    layoutParams.width = device_width / 2 -33;
//                    layoutParams.height = (device_width / 2 - 33) * 2/3 ;
//               //     layoutParams.setMargins(6,6,6,6);
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }
//                else if (height_ratio == 15.0 && width_ratio == 10.0) {
//                    layoutParams.width = device_width / 2 - 33;
//                    layoutParams.height = (device_width / 2 - 33) * 3 / 2;
//              //      layoutParams.setMargins(6,6,6,6);
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }
//                else if (height_ratio == 20.0 && width_ratio == 30.0) {
//                    layoutParams.width = device_width / 2 - 85;
//                    layoutParams.height = (device_width / 2 - 85) * 2 / 3;
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }  else if (height_ratio == 30.0 && width_ratio == 20.0) {
//                    layoutParams.width = device_width / 2 - 85;
//                    layoutParams.height = (device_width / 2 - 85) * 3 / 2;
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }else if (height_ratio == 15.0 && width_ratio == 20.0) {
//                    layoutParams.width = device_width / 2 - 85;
//                    layoutParams.height = (device_width / 2 - 85) * 3 / 4;
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                } else if (height_ratio == 20.0 && width_ratio == 15.0) {
//                    layoutParams.width = device_width / 2 - 85;
//                    layoutParams.height = (device_width / 2 - 85) * 4 / 3;
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }else {
//                    layoutParams.width = device_width / 2 - 33;
//                    layoutParams.height = (device_width / 2 - 33);
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }
//            }
//            else {
//
//                Log.e("LAYOUT", "onBindViewHolder: image layout ELSE " );
//
//            }


//            RelativeLayout.LayoutParams paramsr;
//
//
//
//            if (height_ratio == 10.0 && width_ratio == 15.0) {
//                paramsr = new RelativeLayout.LayoutParams(
//                        device_width /2 - 40,
//                        (device_width / 2 -40) * 2/3
//                );
//
//
//            }
//
//            else if (height_ratio == 15.0 && width_ratio == 10.0) {
//                paramsr = new RelativeLayout.LayoutParams(
//                        device_width /2 - 40,
//                        (device_width / 2 -40) * 3/2
//                );
//
//
//            }
//            else if (height_ratio == 20.0 && width_ratio == 30.0) {
//                paramsr = new RelativeLayout.LayoutParams(
//                        device_width /2 - 40,
//                        (device_width / 2 -40) + 10
//                );
//
//            }  else if (height_ratio == 30.0 && width_ratio == 20.0) {
//                paramsr = new RelativeLayout.LayoutParams(
//                        device_width /2 - 40,
//                        (device_width / 2 -40)  + 10
//                );
//
//
//
//            }else if (height_ratio == 15.0 && width_ratio == 20.0) {
//                paramsr = new RelativeLayout.LayoutParams(
//                        device_width /2 - 40,
//                        (device_width / 2 -40)  +10
//                );
//
//
//            }else if (height_ratio == 20.0 && width_ratio == 15.0) {
//                paramsr = new RelativeLayout.LayoutParams(
//                        device_width /2 - 40,
//                        (device_width / 2 -40) +10
//                );
//
//
//            }else {
//                paramsr = new RelativeLayout.LayoutParams(
//                        device_width /2 - 40,
//                        (device_width / 2 -40) - 22
//                );
//
//
//            }
//
//            if (after_height>after_width){
//
//                params.setMargins(20,10,20,10);
//
//            }else if (after_width>after_height){
//                params.setMargins(10,20,10,20);
//            }
//
//
//            holder.rlt_white.setLayoutParams(paramsr);


            float hhratio=original_height_ratio;
            float wwratio=original_width_ratio;


            if (original_width >= original_height) {
                if (wwratio < hhratio) {

                    float width_r = wwratio;
                    wwratio = hhratio;
                    hhratio = width_r;

                }
            }else {
                if (wwratio > hhratio) {
                    float width_r = wwratio;
                    wwratio = hhratio;
                    hhratio = width_r;
                }
            }

            ConstraintLayout.LayoutParams params;

            if (hhratio == 10.0 && wwratio == 15.0) {

                if (after_height>after_width){

                    params = new ConstraintLayout.LayoutParams(
                            device_width /2 - 45,
                            (device_width / 2 -45)*2/3
                    );

                }else {

                    params = new ConstraintLayout.LayoutParams(
                            device_width /2 - 45,
                            (device_width / 2 -45)*2/3
                    );
                }
            }

            else if (hhratio == 15.0 && wwratio == 10.0) {
                Log.e("INSIDE", "onBindViewHolder:15 10 " );

                if (after_height>after_width) {
                    params = new ConstraintLayout.LayoutParams(
                            device_width / 2 - 45,
                            (device_width / 2 - 45)*3/2
                    );
                }else {
                    params = new ConstraintLayout.LayoutParams(
                            device_width / 2 - 45,
                            (device_width / 2 - 45)*3/2
                    );


                }


            }
            else if (hhratio == 20.0 && wwratio == 30.0) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *2/3
                );

            }  else if (hhratio == 30.0 && wwratio == 20.0) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *3/2
                );



            }else if (hhratio == 15.0 && wwratio == 20.0) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *3/4
                );


            }else if (hhratio == 20.0 && wwratio == 15.0) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *4/3
                );


            }else if (hhratio == 86 && wwratio == 136) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *86/136
                );


            }else if (hhratio == 136 && wwratio == 86) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *136/86
                );


            }else if (hhratio == 13 && wwratio == 18) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *13/18
                );


            }else if (hhratio == 18 && wwratio == 13) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *18/13
                );


            }else if (hhratio == 174 && wwratio == 274) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *174/274
                );


            }else if (hhratio == 274 && wwratio == 174) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *274/174
                );


            }else {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45)
                );


            }
            Log.e("TAG", "Height ratio: "+height_ratio +" width_ratio:"+width_ratio );

            params.setMargins(0, 10, 0, 0);
            holder.msnackView.setLayoutParams(params);
             // holder.msnackView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            //  holder.imageViewWhiteFrame.setScaleType(ImageView.ScaleType.CENTER_CROP);


            if (pos_x+cgwidth>original_width)
            {
                if (cgwidth>original_width){

                    Glide.with(mContext)
                            .load(arrayList.get(position).image)
                            .asBitmap()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .override((int)original_width,(int)original_height)
                            .into(holder.imageViewFitPaper);
                }
                else{

                    Glide.with(mContext)
                            .load(arrayList.get(position).image)
                            .asBitmap()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .override((int)cgwidth,(int)cgheight)
                            .into(holder.imageViewFitPaper);
                }
            }

            else if (pos_y+cgheight>original_height)
            {
                if (cgheight>original_height){
                    Glide.with(mContext)
                            .load(arrayList.get(position).image)
                            .asBitmap()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .override((int)cgwidth,(int)cgheight)
                            .into(holder.imageViewFitPaper);


                }
                else{
                    Glide.with(mContext)
                            .load(arrayList.get(position).image)
                            .asBitmap()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .override((int)cgwidth,(int)cgheight)
                            .into(holder.imageViewFitPaper);
                }

            }else{
                Glide.with(mContext)
                        .load(arrayList.get(position).image)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .override((int)cgwidth,(int)cgheight)
                        .into(holder.imageViewFitPaper);
            }


            if (original_height>original_width) {
                *//*original_height=original_height+20;

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        (int) original_width,
                        (int) original_height);
                holder.imagecontainer.setLayoutParams(layoutParams);
                holder.imagecontainer.setVisibility(View.VISIBLE);*//*
                  //holder.imageViewFitPaper.setPadding(0,5,0,5);
                 //holder.imageViewFitPaper.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                // holder.imageViewFitPaper.setPadding(20,0,20,0);
                holder.imageViewFitPaper.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                holder.imageViewFitPaper.setPadding(20,0,20,0);
                holder.msnackView.setBackgroundColor(mContext.getResources().getColor(R.color.darker_gray));
            }
            else {
                *//*original_width=original_width+5;
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        (int) original_width,
                        (int) original_height);
                holder.imagecontainer.setLayoutParams(layoutParams);
                holder.imagecontainer.setVisibility(View.VISIBLE);*//*
                //holder.imageViewFitPaper.setPadding(5,0,5,0);

                holder.imageViewFitPaper.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                holder.imageViewFitPaper.setPadding(0,20,0,20);
                holder.msnackView.setBackgroundColor(mContext.getResources().getColor(R.color.darker_gray));
              //   holder.imageViewFitPaper.setPadding(10,0,10,0);
             //   holder.imageViewFitPaper.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            }
            //if(original_height==original_height)
           // holder.imageViewFitPaper.setScaleType(ImageView.ScaleType.FIT_XY);
          //  holder.imageViewFitPaper.setBackgroundColor(mContext.getResources().getColor(R.color.white));

//            if (after_height > after_width) {
//                holder.imageViewFitPaper.setPadding(16, 0, 16, 0);
//            } else {
//                holder.imageViewFitPaper.setPadding(0, 16, 0, 16);
//            }*/



            getDropboxIMGSize(Uri.parse(arrayList.get(position).image));

            float after_height=0;
            float after_width=0;
            float pos_x=0;
            float pos_y=0;

            if (height_ratio == 10.0 && width_ratio == 15.0) {
                height_ratio = 86;
                width_ratio = 136;
            } else if (height_ratio == 15.0 && width_ratio == 20.0 ){
                height_ratio = 13;
                width_ratio = 18;
            } else if (height_ratio == 20.0 && width_ratio == 30.0) {
                height_ratio = 174;
                width_ratio = 274;
            }else if (height_ratio==15.0 && width_ratio==15.0){

                height_ratio=15;
                width_ratio=15;
            }


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
                }else if (width_ratio==height_ratio){

                    width_ratio=height_ratio;
                    height_ratio=width_ratio;
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


            // cgwidth=0;
            //   cgheight=0;

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


            Log.e("TAG", "after calculation width White: "+after_width +"height:"+after_height );
            Log.e("TAG", "position x:"+pos_x +" pos_y:"+ pos_y );
            Log.e("TAG", "cgHeight: "+cgheight +" cgWidth:"+cgwidth );


            DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
            int device_width = displayMetrics.widthPixels;

//            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.imageViewWhiteFrame.getLayoutParams();
//            if (after_width>after_height) {
//                if (height_ratio == 10.0 && width_ratio == 15.0) {
//                    layoutParams.width = device_width / 2 - 33;
//                    layoutParams.height = (device_width / 2 - 33) *2/3 ;
////                    layoutParams.setMargins(6,6,6,6);
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }
//                else if (height_ratio == 15.0 && width_ratio == 10.0) {
//                    layoutParams.width = device_width / 2 - 33;
//                    layoutParams.height = (device_width / 2 - 33) *3/2 ;
////                    layoutParams.setMargins(6,6,6,6);
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }
//                else if (height_ratio == 20.0 && width_ratio == 30.0) {
//
//
//                    layoutParams.width = device_width / 2 - 85;
//                    layoutParams.height = (device_width / 2 - 85) * 2 / 3;
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }  else if (height_ratio == 30.0 && width_ratio == 20.0) {
//
//
//                    layoutParams.width = device_width / 2 - 85;
//                    layoutParams.height = (device_width / 2 - 85) * 3 / 2;
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }else if (height_ratio == 15.0 && width_ratio == 20.0) {
//                    layoutParams.width = device_width / 2 - 85;
//                    layoutParams.height = (device_width / 2 - 85) * 3 / 4;
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                } else if (height_ratio == 20.0 && width_ratio == 15.0) {
//
//
//                    layoutParams.width = device_width / 2 - 85;
//                    layoutParams.height = (device_width / 2 - 85) * 4 / 3;
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }else {
//                    layoutParams.width = device_width / 2 - 33;
//                    layoutParams.height = (device_width / 2 - 33);
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }
//
//
//
//            }else if (after_height>after_width){
//                if (height_ratio == 10.0 && width_ratio == 15.0) {
//                    layoutParams.width = device_width / 2 -33;
//                    layoutParams.height = (device_width / 2 - 33) * 2/3 ;
//               //     layoutParams.setMargins(6,6,6,6);
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }
//                else if (height_ratio == 15.0 && width_ratio == 10.0) {
//                    layoutParams.width = device_width / 2 - 33;
//                    layoutParams.height = (device_width / 2 - 33) * 3 / 2;
//              //      layoutParams.setMargins(6,6,6,6);
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }
//                else if (height_ratio == 20.0 && width_ratio == 30.0) {
//                    layoutParams.width = device_width / 2 - 85;
//                    layoutParams.height = (device_width / 2 - 85) * 2 / 3;
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }  else if (height_ratio == 30.0 && width_ratio == 20.0) {
//                    layoutParams.width = device_width / 2 - 85;
//                    layoutParams.height = (device_width / 2 - 85) * 3 / 2;
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }else if (height_ratio == 15.0 && width_ratio == 20.0) {
//                    layoutParams.width = device_width / 2 - 85;
//                    layoutParams.height = (device_width / 2 - 85) * 3 / 4;
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                } else if (height_ratio == 20.0 && width_ratio == 15.0) {
//                    layoutParams.width = device_width / 2 - 85;
//                    layoutParams.height = (device_width / 2 - 85) * 4 / 3;
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }else {
//                    layoutParams.width = device_width / 2 - 33;
//                    layoutParams.height = (device_width / 2 - 33);
//                    holder.imageViewWhiteFrame.setLayoutParams(layoutParams);
//                }
//            }
//            else {
//
//                Log.e("LAYOUT", "onBindViewHolder: image layout ELSE " );
//
//            }




//            RelativeLayout.LayoutParams paramsr;
//
//
//
//            if (height_ratio == 10.0 && width_ratio == 15.0) {
//                paramsr = new RelativeLayout.LayoutParams(
//                        device_width /2 - 40,
//                        (device_width / 2 -40) * 2/3
//                );
//
//
//            }
//
//            else if (height_ratio == 15.0 && width_ratio == 10.0) {
//                paramsr = new RelativeLayout.LayoutParams(
//                        device_width /2 - 40,
//                        (device_width / 2 -40) * 3/2
//                );
//
//
//            }
//            else if (height_ratio == 20.0 && width_ratio == 30.0) {
//                paramsr = new RelativeLayout.LayoutParams(
//                        device_width /2 - 40,
//                        (device_width / 2 -40) + 10
//                );
//
//            }  else if (height_ratio == 30.0 && width_ratio == 20.0) {
//                paramsr = new RelativeLayout.LayoutParams(
//                        device_width /2 - 40,
//                        (device_width / 2 -40)  + 10
//                );
//
//
//
//            }else if (height_ratio == 15.0 && width_ratio == 20.0) {
//                paramsr = new RelativeLayout.LayoutParams(
//                        device_width /2 - 40,
//                        (device_width / 2 -40)  +10
//                );
//
//
//            }else if (height_ratio == 20.0 && width_ratio == 15.0) {
//                paramsr = new RelativeLayout.LayoutParams(
//                        device_width /2 - 40,
//                        (device_width / 2 -40) +10
//                );
//
//
//            }else {
//                paramsr = new RelativeLayout.LayoutParams(
//                        device_width /2 - 40,
//                        (device_width / 2 -40) - 22
//                );
//
//
//            }
//
//            if (after_height>after_width){
//
//                params.setMargins(20,10,20,10);
//
//            }else if (after_width>after_height){
//                params.setMargins(10,20,10,20);
//            }
//
//
//            holder.rlt_white.setLayoutParams(paramsr);





            float hhratio=original_height_ratio;
            float wwratio=original_width_ratio;


            if (original_width >= original_height) {
                if (wwratio < hhratio) {

                    float width_r = wwratio;
                    wwratio = hhratio;
                    hhratio = width_r;

                }
            }else {
                if (wwratio > hhratio) {
                    float width_r = wwratio;
                    wwratio = hhratio;
                    hhratio = width_r;
                }
            }

            Log.e("HHRATIO", "onBindViewHolder: "+hhratio+"wwRatio:"+wwratio );
            ConstraintLayout.LayoutParams params;

            if (hhratio == 10.0 && wwratio == 15.0) {

                if (after_height>after_width){

                    params = new ConstraintLayout.LayoutParams(
                            device_width /2 - 45,
                            (device_width / 2 -45)*2/3
                    );

                }else {

                    params = new ConstraintLayout.LayoutParams(
                            device_width /2 - 45,
                            (device_width / 2 -45)*2/3
                    );
                }
            }

            else if (hhratio == 15.0 && wwratio == 10.0) {
                Log.e("INSIDE", "onBindViewHolder:15 10 " );

                if (after_height>after_width) {
                    params = new ConstraintLayout.LayoutParams(
                            device_width / 2 - 45,
                            (device_width / 2 - 45)*3/2
                    );
                }else {
                    params = new ConstraintLayout.LayoutParams(
                            device_width / 2 - 45,
                            (device_width / 2 - 45)*3/2
                    );


                }


            }
            else if (hhratio == 20.0 && wwratio == 30.0) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *2/3
                );

            }  else if (hhratio == 30.0 && wwratio == 20.0) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *3/2
                );



            }else if (hhratio == 15.0 && wwratio == 20.0) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *3/4
                );


            }else if (hhratio == 20.0 && wwratio == 15.0) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *4/3
                );


            }else if (hhratio == 86 && wwratio == 136) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *86/136
                );


            }else if (hhratio == 136 && wwratio == 86) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *136/86
                );


            }else if (hhratio == 13 && wwratio == 18) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *13/18
                );


            }else if (hhratio == 18 && wwratio == 13) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *18/13
                );


            }else if (hhratio == 174 && wwratio == 274) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *174/274
                );


            }else if (hhratio == 274 && wwratio == 174) {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45) *274/174
                );


            }else {
                params = new ConstraintLayout.LayoutParams(
                        device_width /2 - 45,
                        (device_width / 2 -45)
                );


            }
            Log.e("TAG", "Height ratio: "+height_ratio +" width_ratio:"+width_ratio );
            Log.e("CG", "CGHeight: "+cgheight +" CGWIDTH:"+cgwidth );


            params.setMargins(0, 10, 0, 0);
            holder.msnackView.setLayoutParams(params);
            //holder.imageViewWhiteFrame.setScaleType(ImageView.ScaleType.CENTER_CROP);


            if (pos_x+cgwidth>original_width)
            {
                if (cgwidth>original_width){

                    Log.e("CHECK", "onBindViewHolder: first1" );
                    Glide.with(mContext)
                            .load(arrayList.get(position).image)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .override((int)original_width,(int)cgheight)
                            .centerCrop()
                            .into(holder.imageViewFitPaper);
                }
                else{
                    Log.e("CHECK", "onBindViewHolder: first2" );
                    Glide.with(mContext)
                            .load(arrayList.get(position).image)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .override((int)cgwidth,(int)cgheight)
                            .centerCrop()
                            .into(holder.imageViewFitPaper);
                }
            }

            else if (pos_y+cgheight>original_height)
            {
                if (cgheight>original_height){
                    Log.e("CHECK", "onBindViewHolder: first32" );
                    Glide.with(mContext)
                            .load(arrayList.get(position).image)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .override((int)cgwidth,(int)original_height)
                            .centerCrop()
                            .into(holder.imageViewFitPaper);

                }
                else{
                    Log.e("CHECK", "onBindViewHolder: first3" );
                    Glide.with(mContext)
                            .load(arrayList.get(position).image)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .override((int)cgwidth,(int)cgheight)
                            .centerCrop()
                            .into(holder.imageViewFitPaper);
                }

            }else{

//                if (hhratio==15.0 && wwratio==15.0) {
//                    Log.e("CHECK", "onBindViewHolder: last");
//
////
////                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
////                    Bitmap bitmap = BitmapFactory.decodeFile(new File(arrayList.get(position).image).getPath());
////                      bitmap.compress( Bitmap.CompressFormat.PNG,100,stream);
////                         holder.imageViewFitPaper.setImageBitmap(bitmap);
////                        holder.imageViewFitPaper.setScaleType(ImageView.ScaleType.CENTER_CROP);
////
//                    Glide.with(mContext)
//                            .load(arrayList.get(position).image)
//                            .asBitmap()
//                            .centerCrop()
//                            .override((int) cgwidth, (int) cgheight)
//                            .into(holder.imageViewFitPaper);
//
//
//                }
//                else {
                Glide.with(mContext)
                        .load(arrayList.get(position).image)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .override((int) cgwidth, (int) cgheight)
                        .centerCrop()
                        .into(holder.imageViewFitPaper);
//                }
            }


            if (original_height>original_width) {


                holder.imageViewFitPaper.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                //holder.imageViewFitPaper.setPadding(20,0,20,0);
                holder.msnackView.setBackgroundColor(mContext.getResources().getColor(R.color.darker_gray));
            }
            else {


                holder.imageViewFitPaper.setBackgroundColor(mContext.getResources().getColor(R.color.white));
               // holder.imageViewFitPaper.setPadding(0,20,0,20);
                holder.msnackView.setBackgroundColor(mContext.getResources().getColor(R.color.darker_gray));
                //   holder.imageViewFitPaper.setPadding(10,0,10,0);
                //   holder.imageViewFitPaper.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            }

        }


        holder.txt_count.setText(arrayList.get(position).quantity);

        holder.msnackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime()-click_time < delay)
                    return;
                click_time=SystemClock.elapsedRealtime();
                Intent intent=new Intent(mContext, EditPhotoActivity.class);
                intent.putExtra("image",arrayList.get(position).image);
                intent.putExtra("quantity",arrayList.get(position).quantity);
                intent.putExtra("papercropping",paper_cropping_type);
                intent.putExtra("height_ratio",original_height_ratio);
                intent.putExtra("width_ratio",original_width_ratio);
                intent.putExtra("after_width",after_width);
                intent.putExtra("after_height",after_height);
                intent.putExtra("cg_height",cgheight);
                intent.putExtra("cg_width",cgwidth);
                pos=position;
                Log.e("TAG", "Height_ratio:"+after_height+" width_ratio:"+after_width );
                ((PhotosOverviewActivity)mContext).startActivityForResult(intent,5);


//                File file=new File(arrayList.get(position).image);
//
//
//
//                try {
//                    Log.e("TAG", "IMAGE: "+convertFileToContentUri(mContext,file) );
//                    convertFileToContentUri(mContext,file);
//                    startCrop(convertFileToContentUri(mContext,file),position);
//                    pos=position;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }


            }
        });

        holder.Iv_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext,R.style.AlertDialog);
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
                        ((PhotosOverviewActivity)mContext).setQuantity((currentQuantity));

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

    public void newItem(String quantity,String edited_image,String papercropping){
        Log.e("FitPaperAdapter", "edited byteArray: "+edited_image );
        arrayList.get(pos).image=edited_image;
        arrayList.get(pos).quantity=quantity;
        paper_cropping_type=papercropping;

        notifyDataSetChanged();
    }


    private void getDropboxIMGSize(Uri uri){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);
        original_height = options.outHeight;
        original_width = options.outWidth;
        Log.e("TAG", "getDropboxIMGSize: "+original_width+" height"+original_height );

    }

//    public Uri getImageUri(Context inContext, Bitmap inImage) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
//        return Uri.parse(path);
//    }

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
        ImageView imageViewWhiteFrame,imageViewFitPaper;
        ImageView imageViewFitPicture;
        RelativeLayout rlt_white;
        ConstraintLayout msnackView;
        RelativeLayout imagecontainer;
        CustomTextViewNormal txt_count;
        ImageView Iv_cross;
        public ViewHolder(View itemView) {
            super(itemView);
            imageViewFitPaper=(ImageView) itemView.findViewById(R.id.imageViewFitPaper);
            imageViewWhiteFrame=(ImageView)itemView.findViewById(R.id.imageViewWhiteFrame);
            imageViewFitPicture=(ImageView)itemView.findViewById(R.id.imageViewFitPicture);
            msnackView=itemView.findViewById(R.id.msnackView);
            imagecontainer=itemView.findViewById(R.id.imagecontainer);
            rlt_white=(RelativeLayout)itemView.findViewById(R.id.rlt_white);
            txt_count=(CustomTextViewNormal)itemView.findViewById(R.id.txt_count);
            Iv_cross=(ImageView)itemView.findViewById(R.id.Iv_cross);
        }
    }

    /*

    private void startCrop(@NonNull Uri uri,int position) {
        String destinationFileName = SAMPLE_CROPPED_IMAGE_NAME+".png";


        if (height_ratio==10.0 && width_ratio==15.0){
            UCrop uCrop =  UCrop.of(uri, Uri.fromFile(new File(mContext.getCacheDir(),destinationFileName)),1.5f,arrayList.get(position).quantity,paper_cropping_type);
            uCrop.start((PhotosOverviewActivity)mContext,5);
        }
        else if (height_ratio==20.0 && width_ratio==30.0){
            UCrop uCrop =  UCrop.of(uri, Uri.fromFile(new File(mContext.getCacheDir(),destinationFileName)),1.5f,arrayList.get(position).quantity,paper_cropping_type);
            uCrop.start((PhotosOverviewActivity)mContext,5);
        }
        else if (height_ratio==15.0 && width_ratio==20.0){
            UCrop uCrop =  UCrop.of(uri, Uri.fromFile(new File(mContext.getCacheDir(),destinationFileName)),1.33f,arrayList.get(position).quantity,paper_cropping_type);
            uCrop.start((PhotosOverviewActivity)mContext,5);
        }
        else {

            UCrop uCrop =  UCrop.of(uri, Uri.fromFile(new File(mContext.getCacheDir(),destinationFileName)),1,arrayList.get(position).quantity,paper_cropping_type);
            uCrop.start((PhotosOverviewActivity)mContext,5);
        }
    }

    */

    protected static Uri convertFileToContentUri(Context context, File file) throws Exception {

        //Uri localImageUri = Uri.fromFile(localImageFile); // Not suitable as it's not a content Uri

        ContentResolver cr = context.getContentResolver();
        String imagePath = file.getAbsolutePath();
        String imageName = null;
        String imageDescription = null;
        String uriString = MediaStore.Images.Media.insertImage(cr, imagePath, imageName, imageDescription);
        return Uri.parse(uriString);
    }
}