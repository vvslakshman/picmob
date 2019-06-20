package com.picmob.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.picmob.Activity.DeleiveryActivity;
import com.picmob.AppCustomView.CustomTextViewNormal;
import com.picmob.Models.DeleiveryCountryModel;
import com.picmob.R;

import java.util.ArrayList;

public class DeleiveryCountryAdapter extends RecyclerView.Adapter<DeleiveryCountryAdapter.ViewHolder> {


    public ArrayList<DeleiveryCountryModel> deleiveryCountryModelArrayList = new ArrayList<>();
    public String country_name,country_id;
    Context mContext;
    String url;
     public  boolean selected=false;
    public DeleiveryCountryAdapter(ArrayList<DeleiveryCountryModel> frameColorModels, Context mContext, String url) {
        deleiveryCountryModelArrayList = frameColorModels;
        this.mContext = mContext;
        this.url = url;
    }

    @NonNull
    @Override
    public DeleiveryCountryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.deleivery_country_row, parent, false);
        return new DeleiveryCountryAdapter.ViewHolder(view);
    }


    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull final DeleiveryCountryAdapter.ViewHolder holder, final int position) {
        if (deleiveryCountryModelArrayList.get(position).isSelected) {
            holder.mSnackView.setBackground(mContext.getResources().getDrawable(R.drawable.custom_border_curverd_pink));
            holder.txt_papertype.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        }
        else {
            holder.mSnackView.setBackground(mContext.getResources().getDrawable(R.drawable.edittext_border));
            holder.txt_papertype.setTextColor(mContext.getResources().getColor(R.color.gray_text));
        }
        Glide.with(mContext).
                load(url + deleiveryCountryModelArrayList.get(position).image)
                .dontAnimate()
                .dontTransform()
                .error(R.drawable.selected_photo)
                .into(holder.Iv_flag);


        holder.txt_papertype.setText(deleiveryCountryModelArrayList.get(position).country_name);

        if (deleiveryCountryModelArrayList.get(position).isSelected) {
            country_name = deleiveryCountryModelArrayList.get(position).country_name;
            country_id=String.valueOf(deleiveryCountryModelArrayList.get(position).id);
        }

        //onclick
        holder.mSnackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected=true;

                if (deleiveryCountryModelArrayList.get(position).isSelected)
                    return;

                ((DeleiveryActivity)mContext).setTxt_price(deleiveryCountryModelArrayList.get(position).price);

                country_name = "";
                country_id="";
                if (deleiveryCountryModelArrayList.get(position).isSelected) {
                    country_name = deleiveryCountryModelArrayList.get(position).country_name;
                    country_id=String.valueOf(deleiveryCountryModelArrayList.get(position).id);
                }


                for (int i = 0; i < deleiveryCountryModelArrayList.size(); i++) {
                    if (i == position)
                        deleiveryCountryModelArrayList.get(i).isSelected = !deleiveryCountryModelArrayList.get(i).isSelected;
                    else
                        deleiveryCountryModelArrayList.get(i).isSelected = false;
                }


                if (deleiveryCountryModelArrayList.get(position).isSelected) {
                    holder.mSnackView.setBackground(mContext.getResources().getDrawable(R.drawable.custom_border_curverd_pink));
                    holder.txt_papertype.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                    country_name = deleiveryCountryModelArrayList.get(position).country_name;
                    country_id=String.valueOf(deleiveryCountryModelArrayList.get(position).id);
                    Log.e("DeliveryCountryAdapter", "COuntry name on CLick: " + country_name);
                    Log.e("DeliveryCountryAdapter", "COuntry ID on CLick: " + country_id);
                }else {
                    holder.mSnackView.setBackground(mContext.getResources().getDrawable(R.drawable.edittext_border));
                    holder.txt_papertype.setTextColor(mContext.getResources().getColor(R.color.gray_text));
                }
                    notifyItemRangeChanged(0, deleiveryCountryModelArrayList.size());

            }
        });


    }

    @Override
    public int getItemCount() {
        return deleiveryCountryModelArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        CustomTextViewNormal txt_papertype;
        LinearLayout mSnackView;
        ImageView Iv_flag;

        public ViewHolder(View itemView) {
            super(itemView);

            txt_papertype = (CustomTextViewNormal) itemView.findViewById(R.id.txt_papertype);
            mSnackView = (LinearLayout) itemView.findViewById(R.id.msnackView);
            Iv_flag = (ImageView) itemView.findViewById(R.id.Iv_flag);
        }
    }

}

