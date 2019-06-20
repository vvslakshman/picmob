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


import com.picmob.AppCustomView.CustomTextViewNormal;
import com.picmob.Models.PaperTypeModel;
import com.picmob.R;

import java.util.ArrayList;


public class PaperTypeAdapter extends RecyclerView.Adapter<PaperTypeAdapter.ViewHolder> {


    Context mContext;
    public String paper_name="";
    public int paper_type_id=-1;
    public ArrayList<PaperTypeModel> paperTypeModelArrayList = new ArrayList<>();

    public PaperTypeAdapter(ArrayList<PaperTypeModel> paperTypeModels,Context mContext) {
        paperTypeModelArrayList=paperTypeModels;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public PaperTypeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.paper_type_row, parent, false);
        return new PaperTypeAdapter.ViewHolder(view);
    }


    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        if (paperTypeModelArrayList.get(position).isSelectedPaper) {
            holder.txt_papertype.setBackground(mContext.getResources().getDrawable(R.drawable.custom_border_curverd_pink));
            holder.txt_papertype.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        }
        else {
            holder.txt_papertype.setBackground(mContext.getResources().getDrawable(R.drawable.edittext_border));
            holder.txt_papertype.setTextColor(mContext.getResources().getColor(R.color.gray_text));
        }
            holder.txt_papertype.setText(paperTypeModelArrayList.get(position).papername);
            holder.mSnackView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (paperTypeModelArrayList.get(position).isSelectedPaper)
                        return;

                    paper_name="";
                    paper_type_id=-1;
                    if (!paperTypeModelArrayList.get(position).isSelectedPaper) {
                        paper_name = paperTypeModelArrayList.get(position).papername;
                        paper_type_id=paperTypeModelArrayList.get(position).papertypeid;
                    }


                    for (int i=0;i<paperTypeModelArrayList.size();i++) {
                        if (i==position)
                            paperTypeModelArrayList.get(i).isSelectedPaper = !paperTypeModelArrayList.get(i).isSelectedPaper;
                        else
                            paperTypeModelArrayList.get(i).isSelectedPaper=false;
                    }


                    if (paperTypeModelArrayList.get(position).isSelectedPaper) {
                        holder.txt_papertype.setBackground(mContext.getResources().getDrawable(R.drawable.custom_border_curverd_pink));
                        holder.txt_papertype.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                    }
                    else {
                        holder.txt_papertype.setBackground(mContext.getResources().getDrawable(R.drawable.edittext_border));
                        holder.txt_papertype.setTextColor(mContext.getResources().getColor(R.color.gray_text));
                    }
                    notifyItemRangeChanged(0,paperTypeModelArrayList.size());

                }
            });


    }

    @Override
    public int getItemCount() {
        return paperTypeModelArrayList.size();
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