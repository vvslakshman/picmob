package com.picmob.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.NonNull;
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
import com.picmob.Models.CalculationModel;
import com.picmob.Models.PhotoOverviewModel;
import com.picmob.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

public class PhotoOverviewAdapter extends RecyclerView.Adapter<PhotoOverviewAdapter.ViewHolder> {


    Context mContext;
    long click_time=0;
    long delay=700;
    public ArrayList<PhotoOverviewModel> arrayList = new ArrayList<>();
    String paper_cropping_type;
    float height_ratio,width_ratio;
    public int pos=-1;
    float original_width;
    float original_height;

    public PhotoOverviewAdapter(ArrayList<PhotoOverviewModel> arrayList,Context mContext,String type,float width,float height) {
        this.arrayList=arrayList;
        this.mContext = mContext;
        this.paper_cropping_type=type;
        this.width_ratio=width;
        this.height_ratio=height;
    }

    @NonNull
    @Override
    public PhotoOverviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fit_paper_row, parent, false);
        return new PhotoOverviewAdapter.ViewHolder(view);
    }


    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(final PhotoOverviewAdapter.ViewHolder holder, final int position) {


        Log.e("TAG", "height_ratio: "+height_ratio );

        //   String images=arrayList.get(position).image;

        if (paper_cropping_type.equalsIgnoreCase("Fit Paper")) {
            holder.imageViewFitPaper.setVisibility(View.VISIBLE);
            holder.imageViewWhiteFrame.setVisibility(View.GONE);
            holder.imageViewFitPicture.setVisibility(View.GONE);
            holder.msnackView.setBackground(null);


            float after_height=0;
            float after_width=0;
            float pos_x=0;
            float pos_y=0;
            getDropboxIMGSize(Uri.parse(arrayList.get(position).image));

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


            }


            float cgheight=0;
            float cgwidth=0;




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

            }

            DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
            int device_width = displayMetrics.widthPixels;


            android.view.ViewGroup.LayoutParams layoutParams = holder.imageViewFitPaper.getLayoutParams();
            if (height_ratio==10.0 && width_ratio==15.0){


                layoutParams.width = device_width/2-33;
                layoutParams.height = (device_width/2-33)*2/3;
                holder.imageViewFitPaper.setLayoutParams(layoutParams);
            }
            else if (height_ratio==20.0 && width_ratio==30.0){

                layoutParams.width = device_width/2-33;
                layoutParams.height = (device_width/2-33)*2/3;
                holder.imageViewFitPaper.setLayoutParams(layoutParams);
            }
            else if (height_ratio==15.0 && width_ratio==20.0){

                layoutParams.width = device_width/2-33;
                layoutParams.height = (device_width/2-33)*3/4;
                holder.imageViewFitPaper.setLayoutParams(layoutParams);
            }
            else {
                layoutParams.width = device_width/2-33;
                layoutParams.height = (device_width/2-33);
                holder.imageViewFitPaper.setLayoutParams(layoutParams);
            }

            RelativeLayout.LayoutParams params;
            if (height_ratio == 10.0 && width_ratio == 15.0) {
                params = new RelativeLayout.LayoutParams(
                        device_width /2 - 40,
                        (device_width / 2 -40) * 2/3
                );

            }else if (height_ratio == 20.0 && width_ratio == 30.0) {
                params = new RelativeLayout.LayoutParams(
                        device_width /2 - 40,
                        (device_width / 2 -40) * 2/3 + 10
                );


            }else if (height_ratio == 15.0 && width_ratio == 20.0) {
                params = new RelativeLayout.LayoutParams(
                        device_width /2 - 40,
                        (device_width / 2 -40) * 3/4 +10
                );

            }else {
                params = new RelativeLayout.LayoutParams(
                        device_width /2 - 40,
                        (device_width / 2 -40) - 22
                );

            }

            params.setMargins(20, 20, 0, 0);
            holder.msnackView.setLayoutParams(params);


            if (pos_x+cgwidth>original_width)
            {
                if (cgwidth>original_width){

                    Glide.with(mContext)
                            .load(arrayList.get(position).image)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .override((int)original_width,(int)cgheight)
                            .centerCrop()
                            .into(holder.imageViewFitPaper);
                }
                else{

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
                    Glide.with(mContext)
                            .load(arrayList.get(position).image)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .override((int)cgwidth,(int)original_height)
                            .centerCrop()
                            .into(holder.imageViewFitPaper);


                }
                else{
                    Glide.with(mContext)
                            .load(arrayList.get(position).image)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .override((int)cgwidth,(int)cgheight)
                            .centerCrop()
                            .into(holder.imageViewFitPaper);
                }

            }else{
                Glide.with(mContext)
                        .load(arrayList.get(position).image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .override((int)cgwidth,(int)cgheight)
                        .centerCrop()
                        .into(holder.imageViewFitPaper);
            }


        }
        else if (paper_cropping_type.equalsIgnoreCase("White Frame")){
            holder.imageViewFitPaper.setVisibility(View.GONE);
            holder.imageViewWhiteFrame.setVisibility(View.VISIBLE);

            Glide.with(mContext)
                    .load(arrayList.get(position).image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imageViewWhiteFrame);

            holder.imageViewWhiteFrame.requestLayout();
        }

        else {
            holder.imageViewFitPaper.setVisibility(View.GONE);
            holder.imageViewWhiteFrame.setVisibility(View.GONE);
            holder.imageViewFitPicture.setVisibility(View.VISIBLE);

            Glide.with(mContext)
                    .load(arrayList.get(position).image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imageViewFitPicture);

            holder.imageViewFitPicture.requestLayout();
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
                pos=position;
                ((PhotosOverviewActivity)mContext).startActivityForResult(intent,5);

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
                            Log.e("FitPaperAdapter", "onActivityResult current: " + currentQuantity);
                        }
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

    public void newItem(String quantity,String edited_image){
        arrayList.get(pos).image=edited_image;
        arrayList.get(pos).quantity=quantity;
        notifyDataSetChanged();
    }

    private void getDropboxIMGSize(Uri uri){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);
        original_height = options.outHeight;
        original_width = options.outWidth;
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
        ImageView imageViewWhiteFrame,imageViewFitPaper;
        ImageView imageViewFitPicture;
        RelativeLayout msnackView;
        CustomTextViewNormal txt_count;
        ImageView Iv_cross;
        public ViewHolder(View itemView) {
            super(itemView);
            imageViewFitPaper=(ImageView) itemView.findViewById(R.id.imageViewFitPaper);
            imageViewWhiteFrame=(ImageView)itemView.findViewById(R.id.imageViewWhiteFrame);
            imageViewFitPicture=(ImageView)itemView.findViewById(R.id.imageViewFitPicture);
            msnackView=(RelativeLayout)itemView.findViewById(R.id.msnackView);
            txt_count=(CustomTextViewNormal)itemView.findViewById(R.id.txt_count);
            Iv_cross=(ImageView)itemView.findViewById(R.id.Iv_cross);
        }
    }
}

