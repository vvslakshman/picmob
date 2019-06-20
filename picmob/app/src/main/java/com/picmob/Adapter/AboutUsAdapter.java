package com.picmob.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.picmob.AppCustomView.CustomTextViewBold;
import com.picmob.AppCustomView.CustomTextViewNormal;
import com.picmob.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AboutUsAdapter extends RecyclerView.Adapter<AboutUsAdapter.ViewHolder> {
    Context mContext;
    JSONArray jsonArray = new JSONArray();
    String url;
    public AboutUsAdapter(Context mContext, JSONArray jsonArray,String url) {
        this.mContext = mContext;
        this.jsonArray = jsonArray;
        this.url=url;
    }


    @NonNull
    @Override
    public AboutUsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.about_us_row, parent, false);
        return new AboutUsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AboutUsAdapter.ViewHolder holder, final int position) {
        JSONArray subCategoryArray=new JSONArray();
        try {
            final JSONObject jsonObject = new JSONObject(String.valueOf(jsonArray.getJSONObject(position)));


            holder.txt_description.setText(jsonObject.getString("description"));
            Glide.with(mContext).
                    load(url+jsonArray.getJSONObject(position).getString("image"))
                    .into(holder.imageView);
            holder.txt_title.setText(jsonObject.getString("title"));

            if (jsonObject.getString("title").equalsIgnoreCase("Quality"))
                holder.txt_title.setTextColor(mContext.getResources().getColor(R.color.green));

            if (jsonObject.getString("title").equalsIgnoreCase(mContext.getResources().getString(R.string.services)))
                holder.txt_title.setTextColor(mContext.getResources().getColor(R.color.gray_text));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CustomTextViewNormal txt_description;
        ImageView imageView;
        CustomTextViewBold txt_title;
        public ViewHolder(View itemView) {
            super(itemView);
            txt_description=(CustomTextViewNormal)itemView.findViewById(R.id.txt_description);
            imageView=(ImageView)itemView.findViewById(R.id.image);
            txt_title=(CustomTextViewBold)itemView.findViewById(R.id.txt_title);
        }
    }



}