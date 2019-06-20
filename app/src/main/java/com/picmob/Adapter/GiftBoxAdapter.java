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

import com.picmob.Activity.GiftBoxActivity;
import com.picmob.AppCustomView.CustomTextViewNormal;
import com.picmob.Models.GiftBoxModel;
import com.picmob.R;

import java.util.ArrayList;

public class GiftBoxAdapter extends RecyclerView.Adapter<GiftBoxAdapter.ViewHolder> {


    public ArrayList<GiftBoxModel> giftBoxModelArrayList = new ArrayList<>();
    public String category_name;
    public int giftbox_id = -1;
    Context mContext;
    public boolean categoryClicked=false;

    public GiftBoxAdapter(ArrayList<GiftBoxModel> frameColorModels, Context mContext) {
        giftBoxModelArrayList = frameColorModels;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public GiftBoxAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frame_size_row, parent, false);
        return new GiftBoxAdapter.ViewHolder(view);
    }


    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull final GiftBoxAdapter.ViewHolder holder, final int position) {
        //selection acc to position
        Log.e("GiftBoxAdapter", "onBindViewHolder: "+giftBoxModelArrayList.get(position).category_name );
        if (giftBoxModelArrayList.get(position).isSelected)
            holder.txt_papertype.setBackground(mContext.getResources().getDrawable(R.drawable.custom_border_curverd_pink));
        else
            holder.txt_papertype.setBackground(mContext.getResources().getDrawable(R.drawable.edittext_border));
        //setting price for position 0
        ((GiftBoxActivity) mContext).setPrice(Float.valueOf(giftBoxModelArrayList.get(0).price));

        holder.txt_papertype.setText(giftBoxModelArrayList.get(position).category_name);

        if (giftBoxModelArrayList.get(position).isSelected) {
            category_name = giftBoxModelArrayList.get(position).category_name;
            giftbox_id = giftBoxModelArrayList.get(position).id;
        }
        //onclick
        holder.mSnackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                categoryClicked=true;
                if (giftBoxModelArrayList.get(position).isSelected)
                    return;

                category_name = "";

                if (giftBoxModelArrayList.get(position).isSelected) {
                    category_name = giftBoxModelArrayList.get(position).category_name;
                    giftbox_id = giftBoxModelArrayList.get(position).id;
                    Log.e("GiftBoxAdapter", "onClick: " + category_name);

                }


                for (int i = 0; i < giftBoxModelArrayList.size(); i++) {
                    if (i == position)
                        giftBoxModelArrayList.get(i).isSelected = !giftBoxModelArrayList.get(i).isSelected;
                    else
                        giftBoxModelArrayList.get(i).isSelected = false;
                }


                if (giftBoxModelArrayList.get(position).isSelected)
                    holder.txt_papertype.setBackground(mContext.getResources().getDrawable(R.drawable.custom_border_curverd_pink));
                else
                    holder.txt_papertype.setBackground(mContext.getResources().getDrawable(R.drawable.edittext_border));
                notifyItemRangeChanged(0, giftBoxModelArrayList.size());

                ((GiftBoxActivity) mContext).callApiForSlider(giftBoxModelArrayList.get(position).id);
                //  ((GiftBoxActivity)mContext).setPrice(Float.valueOf(giftBoxModelArrayList.get(position).price));
            }
        });


    }

    @Override
    public int getItemCount() {
        return giftBoxModelArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        CustomTextViewNormal txt_papertype;
        RelativeLayout mSnackView;

        public ViewHolder(View itemView) {
            super(itemView);

            txt_papertype = (CustomTextViewNormal) itemView.findViewById(R.id.txt_papertype);
            mSnackView = (RelativeLayout) itemView.findViewById(R.id.msnackView);
        }
    }

}
