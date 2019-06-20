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

import com.picmob.Activity.PrintFrameActivity;
import com.picmob.AppCustomView.CustomTextViewNormal;
import com.picmob.Models.FrameSizeModel;
import com.picmob.R;

import java.util.ArrayList;

public class FrameSizeAdapter extends RecyclerView.Adapter<FrameSizeAdapter.ViewHolder> {


    Context mContext;
    public ArrayList<FrameSizeModel> frameSizeModelArrayList = new ArrayList<>();
    public String frame_size;
    public float frame_price=0;
    public int frame_size_id=-1;
    public boolean categoryClicked=false;
    public FrameSizeAdapter(ArrayList<FrameSizeModel> frameColorModels,Context mContext) {
        frameSizeModelArrayList=frameColorModels;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public FrameSizeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frame_size_row, parent, false);
        return new FrameSizeAdapter.ViewHolder(view);
    }


    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull final FrameSizeAdapter.ViewHolder holder, final int position) {

        if (frameSizeModelArrayList.get(position).isSelected)
            holder.txt_papertype.setBackground(mContext.getResources().getDrawable(R.drawable.custom_border_curverd_pink));
        else
            holder.txt_papertype.setBackground(mContext.getResources().getDrawable(R.drawable.edittext_border));


        holder.txt_papertype.setText(frameSizeModelArrayList.get(position).height+"x"+frameSizeModelArrayList.get(position).width+"cm");

        if (frameSizeModelArrayList.get(position).isSelected) {
            frame_size = frameSizeModelArrayList.get(position).height + "x" + frameSizeModelArrayList.get(position).width + "cm";
            frame_size_id=frameSizeModelArrayList.get(position).sizeid;
        }

        holder.mSnackView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {

                categoryClicked=true;
                if (frameSizeModelArrayList.get(position).isSelected)
                    return;
                frame_size="";

                for (int i=0;i<frameSizeModelArrayList.size();i++) {
                    if (i==position){
                        frameSizeModelArrayList.get(i).isSelected = !frameSizeModelArrayList.get(i).isSelected;
                        frame_size = frameSizeModelArrayList.get(position).height + "x" + frameSizeModelArrayList.get(position).width + "cm";
                        frame_size_id=frameSizeModelArrayList.get(position).sizeid;
                        Log.e("TAG", "onClick Price: "+frameSizeModelArrayList.get(position).price );
                        ((PrintFrameActivity)mContext).setPrice(Float.valueOf(frameSizeModelArrayList.get(position).price));
                    }
                    else
                        frameSizeModelArrayList.get(i).isSelected=false;
                }


                if (frameSizeModelArrayList.get(position).isSelected)
                    holder.txt_papertype.setBackground(mContext.getResources().getDrawable(R.drawable.custom_border_curverd_pink));
                else
                    holder.txt_papertype.setBackground(mContext.getResources().getDrawable(R.drawable.edittext_border));
                notifyItemRangeChanged(0,frameSizeModelArrayList.size());

               //((PrintFrameActivity)mContext).callApiForSizeClick(frameSizeModelArrayList.get(position).sizeid);

            }
        });


    }

    @Override
    public int getItemCount() {
        return frameSizeModelArrayList.size();
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