package com.picmob.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.picmob.Activity.PhotoAlbumActivity;
import com.picmob.AppCustomView.CustomTextViewNormal;

import com.picmob.Models.PhotoAlbumModel;
import com.picmob.R;

import java.util.ArrayList;

public class PhotoAlbumAdapter extends RecyclerView.Adapter<PhotoAlbumAdapter.ViewHolder> {


    Context mContext;
    public ArrayList<PhotoAlbumModel> photoAlbumModelArrayList = new ArrayList<>();
    public String category_name;
    public int photoAlbum_id=-1;
    public boolean categoryClicked=false;
    public PhotoAlbumAdapter(ArrayList<PhotoAlbumModel> frameColorModels,Context mContext) {
        photoAlbumModelArrayList=frameColorModels;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public PhotoAlbumAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frame_size_row, parent, false);
        return new PhotoAlbumAdapter.ViewHolder(view);
    }


    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull final PhotoAlbumAdapter.ViewHolder holder, final int position) {

        if (photoAlbumModelArrayList.get(position).isSelected)
            holder.txt_papertype.setBackground(mContext.getResources().getDrawable(R.drawable.custom_border_curverd_pink));
        else
            holder.txt_papertype.setBackground(mContext.getResources().getDrawable(R.drawable.edittext_border));



        ((PhotoAlbumActivity)mContext).setPrice(Float.valueOf(photoAlbumModelArrayList.get(position).price));
        holder.txt_papertype.setText(photoAlbumModelArrayList.get(position).category_name);

        if (photoAlbumModelArrayList.get(position).isSelected) {
            category_name = photoAlbumModelArrayList.get(position).category_name;
            photoAlbum_id=photoAlbumModelArrayList.get(position).id;
        }
        holder.mSnackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                categoryClicked=true;
                if (photoAlbumModelArrayList.get(position).isSelected)
                    return;


                category_name="";
                if (photoAlbumModelArrayList.get(position).isSelected) {
                    category_name = photoAlbumModelArrayList.get(position).category_name;
                    photoAlbum_id=photoAlbumModelArrayList.get(position).id;
                    Log.e("PhotoAlbumAdapter", "onClick: " + category_name);
                }


                for (int i=0;i<photoAlbumModelArrayList.size();i++) {
                    if (i==position)
                        photoAlbumModelArrayList.get(i).isSelected = !photoAlbumModelArrayList.get(i).isSelected;
                    else
                        photoAlbumModelArrayList.get(i).isSelected=false;
                }


                if (photoAlbumModelArrayList.get(position).isSelected)
                    holder.txt_papertype.setBackground(mContext.getResources().getDrawable(R.drawable.custom_border_curverd_pink));
                else
                    holder.txt_papertype.setBackground(mContext.getResources().getDrawable(R.drawable.edittext_border));
                notifyItemRangeChanged(0,photoAlbumModelArrayList.size());

                ((PhotoAlbumActivity)mContext).callApiForSlider(photoAlbumModelArrayList.get(position).id);
            }
        });


    }

    @Override
    public int getItemCount() {
        return photoAlbumModelArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        CustomTextViewNormal txt_papertype;
        RelativeLayout mSnackView;
        public ViewHolder(View itemView) {
            super(itemView);

            txt_papertype=(CustomTextViewNormal)itemView.findViewById(R.id.txt_papertype);
            mSnackView=(RelativeLayout)itemView.findViewById(R.id.msnackView);
        }
    }

}

