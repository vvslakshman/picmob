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
import com.picmob.AppCustomView.CustomTextViewBold;
import com.picmob.AppCustomView.CustomTextViewNormal;
import com.picmob.Models.PaperTypeModel;
import com.picmob.R;


import java.util.ArrayList;

public class PaperCroppingAdapter extends RecyclerView.Adapter<PaperCroppingAdapter.ViewHolder> {


    Context mContext;
    public ArrayList<PaperTypeModel> paperTypeModelArrayList = new ArrayList<>();

    String url;
    public String paper_cropping;
    public int paper_cropping_id;

    public PaperCroppingAdapter(ArrayList<PaperTypeModel> paperTypeModels,Context mContext,String url) {
        this.mContext = mContext;
       paperTypeModelArrayList=paperTypeModels;
       this.url=url;
    }

    @NonNull
    @Override
    public PaperCroppingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.paper_cropping_row, parent, false);
        return new PaperCroppingAdapter.ViewHolder(view);
    }


    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull final PaperCroppingAdapter.ViewHolder holder, final int position) {


        if (paperTypeModelArrayList.get(position).isSelectedCropping) {

            Glide.with(mContext).
                    load(url+paperTypeModelArrayList.get(position).afterselect_iamge)
                    .into(holder.imageView);

            holder.imageView.setBackground(mContext.getResources().getDrawable(R.drawable.custom_border_curverd_pink));
        }
        else {
            Glide.with(mContext).
                    load(url+paperTypeModelArrayList.get(position).imageView)
                    .into(holder.imageView);
            holder.imageView.setBackground(mContext.getResources().getDrawable(R.drawable.edittext_border));

        }

            holder.txt_paper1.setText(paperTypeModelArrayList.get(position).paper_type_name);
            holder.txt_paperdetail.setText(paperTypeModelArrayList.get(position).paper_type_detail);



            holder.mSnackView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (paperTypeModelArrayList.get(position).isSelectedCropping)
                        return;

                    paper_cropping="";
                    if (!paperTypeModelArrayList.get(position).isSelectedCropping) {
                        paper_cropping = paperTypeModelArrayList.get(position).paper_type_name;
                        paper_cropping_id=paperTypeModelArrayList.get(position).papercroppingid;
                        Log.e("PaperCropping", "onClick: "+paper_cropping );
                    }

                    for (int i=0;i<paperTypeModelArrayList.size();i++) {
                        if (i==position)
                            paperTypeModelArrayList.get(i).isSelectedCropping = !paperTypeModelArrayList.get(i).isSelectedCropping;
                        else
                            paperTypeModelArrayList.get(i).isSelectedCropping=false;
                    }

                    if (paperTypeModelArrayList.get(position).isSelectedCropping) {
                        Glide.with(mContext).
                                load(url+paperTypeModelArrayList.get(position).afterselect_iamge)
                                .into(holder.imageView);
                        holder.imageView.setBackground(mContext.getResources().getDrawable(R.drawable.custom_border_curverd_pink));
                    }
                    else {
                        Glide.with(mContext).
                                load(url+paperTypeModelArrayList.get(position).imageView)
                                .into(holder.imageView);
                        holder.imageView.setBackground(mContext.getResources().getDrawable(R.drawable.edittext_border));

                    }
                 //   notifyItemRangeChanged(0,paperTypeModelArrayList.size());
                        notifyDataSetChanged();
                }
            });



    }

    @Override
    public int getItemCount() {
        return paperTypeModelArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        CustomTextViewNormal txt_paperdetail;
        CustomTextViewBold txt_paper1;
        LinearLayout mSnackView;
        ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);

            txt_paperdetail=(CustomTextViewNormal)itemView.findViewById(R.id.txt_paper1d);
            txt_paper1=(CustomTextViewBold)itemView.findViewById(R.id.txt_paper1);
            mSnackView=(LinearLayout) itemView.findViewById(R.id.msnackView);
            imageView=(ImageView)itemView.findViewById(R.id.Iv_first);
        }
    }

}
