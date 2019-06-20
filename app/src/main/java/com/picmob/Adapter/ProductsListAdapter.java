package com.picmob.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.picmob.Activity.GiftBoxActivity;
import com.picmob.Activity.PhotoAlbumActivity;
import com.picmob.Activity.PrintFrameActivity;
import com.picmob.AppCustomView.CustomTextViewBold;
import com.picmob.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductsListAdapter extends RecyclerView.Adapter<ProductsListAdapter.ViewHolder> {
    Context mContext;

    JSONArray jsonArray = new JSONArray();
    String url;

    public ProductsListAdapter(Context mContext, JSONArray jsonArray, String url) {
        this.mContext = mContext;
        this.jsonArray = jsonArray;
        this.url = url;
    }


    @NonNull
    @Override
    public ProductsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_list_row, parent, false);
        return new ProductsListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsListAdapter.ViewHolder holder, final int position) {
        try {
            final JSONObject jsonObject = new JSONObject(String.valueOf(jsonArray.getJSONObject(position)));
            holder.txt_type.setText(jsonObject.getString("product_name"));

            Glide.with(mContext).
                    load(url + jsonArray.getJSONObject(position).getString("image"))
                    .centerCrop()
                    .into(holder.image);


            holder.lyt_printframe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position==0){

                        Intent intent=new Intent(mContext,PrintFrameActivity.class);
                        try {
                            intent.putExtra("product_id",jsonArray.getJSONObject(position).getString("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mContext.startActivity(intent);
                        ((Activity) mContext ).overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);


                    }

                    else if (position==1){

                        Intent intent=new Intent(mContext,PhotoAlbumActivity.class);
                        try {
                            Log.e("ViewPagerAdapter", "onClick:productid "+ jsonArray.getJSONObject(position).getString("id"));
                            intent.putExtra("product_id",jsonArray.getJSONObject(position).getString("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mContext.startActivity(intent);
                        ((Activity) mContext ).overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);

                    }

                    else if (position==2){

                        Intent intent=new Intent(mContext,GiftBoxActivity.class);
                        try {
                            intent.putExtra("product_id",jsonArray.getJSONObject(position).getString("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mContext.startActivity(intent);
                        ((Activity) mContext ).overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);

                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    public interface ClickItemEvent {
        void onClickItem(String height, String width, String price, String position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CustomTextViewBold txt_type;
        ImageView image;
        LinearLayout lyt_printframe;

        public ViewHolder(View itemView) {
            super(itemView);

            txt_type = (CustomTextViewBold) itemView.findViewById(R.id.txt_type);
            image = (ImageView) itemView.findViewById(R.id.image);
            lyt_printframe = (LinearLayout) itemView.findViewById(R.id.lyt_printframe);
        }
    }

}
