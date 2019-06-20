package com.picmob.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.picmob.Activity.PhotosActivity;
import com.picmob.Activity.Selected_Photos;
import com.picmob.Fragments.InstagramFragment;
import com.picmob.Models.Model_images;
import com.picmob.R;

import java.util.ArrayList;

public class InstagramAdapter extends RecyclerView.Adapter<InstagramAdapter.MyViewHolder> {

    Context context;
    ArrayList<Model_images> al_menu = new ArrayList<>();
    int int_position;
    SparseBooleanArray mSparseBooleanArray;
    public int count=0;
    public InstagramAdapter(Context context, ArrayList<Model_images> al_menu) {
        this.al_menu = al_menu;
        this.context = context;
        this.int_position = int_position;
        mSparseBooleanArray = new SparseBooleanArray();

    }
    @Override
    public InstagramAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_gridview_layout, parent, false);

        return new InstagramAdapter.MyViewHolder(itemView);
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
            count=al_menu.size();
            ((PhotosActivity)context).setCount(count);
        }
        notifyDataSetChanged();

    }

    public  void DeSelectAll(){
        for(int i=0;i<al_menu.size();i++) {
            al_menu.get(i).setSelected(false);
            count=0;
            ((PhotosActivity)context).setCount(count);
        }
        notifyDataSetChanged();

    }

    public ArrayList<String> getCheckedItems() {
        ArrayList<String> mTempArry = new ArrayList<String>();

        for(int i=0;i<al_menu.size();i++) {
            if(al_menu.get(i).isSelected) {
                //     mTempArry.add(String.valueOf(al_menu.get(i)));
                mTempArry.add(String.valueOf(al_menu.get(i).url));
            }

        }

        return mTempArry;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public void onBindViewHolder(final InstagramAdapter.MyViewHolder viewHolder, final int position) {

        Glide.with(context).load( al_menu.get(position).url)
                .centerCrop()
                .into(viewHolder.iv_image);

        viewHolder.mcheckBox.setChecked(al_menu.get(position).isSelected);

        if (al_menu.get(position).isSelected) {
            viewHolder.iv_image.setColorFilter(Color.argb(80, 255, 255, 255), PorterDuff.Mode.MULTIPLY);
        }else {
            viewHolder.iv_image.setColorFilter(null);
        }

        viewHolder.mSnackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("TAG", "onClick: "+al_menu.get(position).isSelected );
                al_menu.get(position).isSelected=!al_menu.get(position).isSelected;
                viewHolder.mcheckBox.setChecked(al_menu.get(position).isSelected);

                if (al_menu.get(position).isSelected) {
                    viewHolder.iv_image.setColorFilter(Color.argb(80, 255, 255, 255), PorterDuff.Mode.MULTIPLY);
                    ((Selected_Photos)context).setCountPlus();
                }else {
                    viewHolder.iv_image.setColorFilter(null);
                    ((Selected_Photos)context).setCountMinus();
                    InstagramFragment.deSelectedImage(al_menu.get(position).getSingle_imagepath());
                }
              //  ((Selected_Photos)context).setCount(count);
                Log.e("TAG", "onClick: "+count );
            }
        });

        final Handler handel = new Handler();
        final Runnable run=new Runnable() {
            @Override
            public void run() {


                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                // Include dialog.xml file
                dialog.setContentView(R.layout.dialog_photoholded);
                dialog.show();
                dialog.setCanceledOnTouchOutside(false);
                Button btn_close=(Button)dialog.findViewById(R.id.btn_close);
                ImageView imageView=(ImageView)dialog.findViewById(R.id.iv_image);

                btn_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                Glide.with(context).load( al_menu.get(position).url)
                        .centerCrop()
                        .into(imageView);

            }
        };
        viewHolder.mSnackView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        handel.postDelayed(run, 1000/* OR the amount of time you want */);
                        break;

                    default:
                        handel.removeCallbacks(run);
                        break;
                }
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
