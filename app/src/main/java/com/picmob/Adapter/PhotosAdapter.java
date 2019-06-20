package com.picmob.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.picmob.Activity.PhotosActivity;
import com.picmob.AppCustomView.UiHelper;
import com.picmob.Models.Model_images;
import com.picmob.Networking.BaseApplication;
import com.picmob.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.MyViewHolder> {

    Context context;
    ArrayList<Model_images> al_menu = new ArrayList<>();

    public PhotosAdapter(Context context, ArrayList<Model_images> al_menu) {
        this.al_menu = al_menu;
        this.context = context;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_gridview_layout, parent, false);

        return new MyViewHolder(itemView);
    }



    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return al_menu.size();
    }


    public  void SelectAll(){
        for(int i=0;i<al_menu.size();i++) {

            al_menu.get(i).setSelected(true);
            int count=al_menu.size();
            ((PhotosActivity)context).setCount(count);
        }
        notifyDataSetChanged();

    }

    @SuppressLint("NewApi")
    public  void DeSelectAll(){
        ArrayList<String> mTempArry = new ArrayList<String>();
        int index=0;

        for(int i=0;i<al_menu.size();i++) {

            if (al_menu.get(i).isSelected)
                mTempArry.add(String.valueOf(al_menu.get(i).getSingle_imagepath()));

            al_menu.get(i).setSelected(false);
        }
        int count=0;
        ((PhotosActivity)context).setCount(count);

        String aa= BaseApplication.getInstance().getSession().getSelectedImages();
        JSONArray sessionImagesArray = new JSONArray();
        JSONArray sessionProductArray = new JSONArray();
        if (!aa.isEmpty()) {
            try {
                sessionProductArray = new JSONArray(aa);
                Log.e("TAG", "DeSelectAll Product Array before: "+sessionProductArray.toString());
                Log.e("TAG", "DeSelectAll: Product ID "+PhotosActivity.productId );

                if (!PhotosActivity.productId.equalsIgnoreCase(""))
                    index = Integer.parseInt(PhotosActivity.productId)-1;
                else {

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            if (sessionProductArray.length()>0)
                sessionImagesArray = sessionProductArray.getJSONObject(index).getJSONArray("images_array");
            if (sessionImagesArray.length()>0) {
                Log.e("TAG", "DeSelectAll sessionImagesArray: " + sessionImagesArray.toString());
                for (int i = 0; i < sessionImagesArray.length(); i++) {

                    for (int j = 0; j < mTempArry.size(); j++) {
                        Log.e("TAG", "DeSelectAll mTemp: " + mTempArry.toString());
                        if (sessionImagesArray.getJSONObject(i).getString("image").equalsIgnoreCase(mTempArry.get(j))) {
                            sessionImagesArray.remove(i);
                        }
                    }

                }

                sessionProductArray.getJSONObject(index).put("images_array", sessionImagesArray);
                Log.e("TAG", "DeSelectAll product: " + sessionProductArray.toString());
                BaseApplication.getInstance().getSession().setSelectedImages(sessionProductArray.toString());
            }
            else {
                for(int i=0;i<al_menu.size();i++) {
                    al_menu.get(i).setSelected(false);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        }
        notifyDataSetChanged();

    }

    public ArrayList<String> getCheckedItems() {
        ArrayList<String> mTempArry = new ArrayList<String>();

        for(int i=0;i<al_menu.size();i++) {
            if(al_menu.get(i).isSelected) {
                mTempArry.add(String.valueOf(al_menu.get(i).getSingle_imagepath()));
            }
            Log.e("PHOTOSADAPTER", "getCheckedItems: "+mTempArry.toString() );
        }

        return mTempArry;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {

        Glide.with(context).load( al_menu.get(position).getSingle_imagepath())
                .centerCrop()
                .into(viewHolder.iv_image);

        Log.e("TAG", "onBindViewHolder: "+al_menu.get(position).isSelected );
        viewHolder.mcheckBox.setChecked(al_menu.get(position).isSelected);

        if (al_menu.get(position).isSelected) {
            viewHolder.iv_image.setColorFilter(Color.argb(80, 255, 255, 255), PorterDuff.Mode.MULTIPLY);
        }else {
            viewHolder.iv_image.setColorFilter(null);
        }

        viewHolder.mSnackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("TAG", "onCLick Selected: "+al_menu.get(position).isSelected);
                al_menu.get(position).isSelected=!al_menu.get(position).isSelected;
                viewHolder.mcheckBox.setChecked(al_menu.get(position).isSelected);

                if (al_menu.get(position).isSelected) {
                    viewHolder.iv_image.setColorFilter(Color.argb(80, 255, 255, 255), PorterDuff.Mode.MULTIPLY);
                    ((PhotosActivity)context).setCountPlus();
                }

                else {
                    viewHolder.iv_image.setColorFilter(null);
                    ((PhotosActivity)context).setCountMinus();
                    ((PhotosActivity)context).deSelectedImage(al_menu.get(position).getSingle_imagepath());
                }

                for(int i=0;i<al_menu.size();i++) {
                    if (!al_menu.get(i).isSelected())
                        ((PhotosActivity)context).ShowDeselectButton();
                }

            }
        });


        viewHolder.mSnackView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                // Include dialog.xml file
                dialog.setContentView(R.layout.dialog_photoholded);
                dialog.show();
                dialog.setCanceledOnTouchOutside(false);
                Button btn_close=(Button)dialog.findViewById(R.id.btn_close);
                ImageView imageView=(ImageView)dialog.findViewById(R.id.iv_image);


                DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
                int device_width = displayMetrics.widthPixels;

                android.view.ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                layoutParams.width = device_width-60;
                imageView.setLayoutParams(layoutParams);

                btn_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                Glide.with(context).load( al_menu.get(position).getSingle_imagepath())
                        .into(imageView);

                return false;
            }
        });


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public CheckBox mcheckBox;
        public ImageView iv_image;
        RelativeLayout mSnackView;
        public MyViewHolder(View view) {
            super(view);

            mcheckBox = (CheckBox) view.findViewById(R.id.checkBox);
            iv_image = (ImageView) view.findViewById(R.id.iv_image);
            mSnackView=(RelativeLayout)view.findViewById(R.id.mSnackView);
        }
    }


}