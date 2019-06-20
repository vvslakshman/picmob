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

import com.bumptech.glide.Glide;
import com.picmob.Activity.PrintFrameActivity;
import com.picmob.AppCustomView.RoundRectCornerImageView;
import com.picmob.Models.FrameColorModel;
import com.picmob.R;

import java.util.ArrayList;

public class FrameColorAdapter extends RecyclerView.Adapter<FrameColorAdapter.ViewHolder> {


    Context mContext;
    String url;
    public ArrayList<FrameColorModel> frameColorModelArrayList = new ArrayList<>();
    public String frame_color;
    public int frame_color_id=-1;
    public String color_image;
    public boolean categoryClicked=false;
    public FrameColorAdapter(ArrayList<FrameColorModel> frameColorModels,Context mContext,String url) {
        frameColorModelArrayList=frameColorModels;
        this.mContext = mContext;
        this.url=url;
    }

    @NonNull
    @Override
    public FrameColorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frame_color_row, parent, false);
        return new FrameColorAdapter.ViewHolder(view);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull final FrameColorAdapter.ViewHolder holder, final int position) {


        Glide.with(mContext).
                load(url+frameColorModelArrayList.get(position).image)
                .dontAnimate()
                .dontTransform()
                .into(holder.imageView);

            if (frameColorModelArrayList.get(position).isSelected)
                holder.mSnackView.setBackground(mContext.getResources().getDrawable(R.drawable.custom_border_curverd_pink));
            else
                holder.mSnackView.setBackground(mContext.getResources().getDrawable(R.drawable.edittext_border));

        if (frameColorModelArrayList.get(position).isSelected) {
            frame_color=frameColorModelArrayList.get(position).color;
            frame_color_id=frameColorModelArrayList.get(position).colorid;
            color_image=frameColorModelArrayList.get(position).image;
        }




        holder.mSnackView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                categoryClicked=true;
                if (frameColorModelArrayList.get(position).isSelected)
                    return;


                frame_color="";
                if (frameColorModelArrayList.get(position).isSelected) {
                    frame_color=frameColorModelArrayList.get(position).color;
                    Log.e("FrameColorAdapter", "onClick: "+frame_color );
                    frame_color_id=frameColorModelArrayList.get(position).colorid;
                    color_image=frameColorModelArrayList.get(position).image;
                }


                for (int i=0;i<frameColorModelArrayList.size();i++) {
                    if (i==position)
                    frameColorModelArrayList.get(i).isSelected = !frameColorModelArrayList.get(i).isSelected;
                    else
                    frameColorModelArrayList.get(i).isSelected=false;
                }


                        if (frameColorModelArrayList.get(position).isSelected)
                            holder.mSnackView.setBackground(mContext.getResources().getDrawable(R.drawable.custom_border_curverd_pink));
                        else
                            holder.mSnackView.setBackground(mContext.getResources().getDrawable(R.drawable.edittext_border));
                       notifyItemRangeChanged(0,frameColorModelArrayList.size());

               // ((PrintFrameActivity)mContext).callApiForSlider(frameColorModelArrayList.get(position).colorid);
                ((PrintFrameActivity)mContext).callApiForSize(frameColorModelArrayList.get(position).colorid);

            }
        });


    }

    @Override
    public int getItemCount() {
        return frameColorModelArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        RoundRectCornerImageView imageView;
        RelativeLayout mSnackView;
        public ViewHolder(View itemView) {
            super(itemView);

            imageView=(RoundRectCornerImageView) itemView.findViewById(R.id.image);
            mSnackView=(RelativeLayout)itemView.findViewById(R.id.msnackView);
        }
    }

}