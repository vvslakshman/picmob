package com.picmob.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;

import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import com.picmob.AppCustomView.CustomTextViewBold;
import com.picmob.AppCustomView.CustomTextViewNormal;
import com.picmob.AppCustomView.RoundRectCornerImageView;
import com.picmob.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class My_order_detail_Adapter extends RecyclerView.Adapter<My_order_detail_Adapter.ViewHolder> {
    Context mContext;
    JSONArray jsonArray = new JSONArray();

    public My_order_detail_Adapter(Context mContext, JSONArray jsonArray) {
        this.mContext = mContext;
        this.jsonArray = jsonArray;

    }


    @NonNull
    @Override
    public My_order_detail_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_order_detail_row, parent, false);
        return new My_order_detail_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull My_order_detail_Adapter.ViewHolder holder, final int position) {
        try {
            final JSONObject jsonObject = new JSONObject(String.valueOf(jsonArray.getJSONObject(position)));

            Log.e("MyORdeDetailAdapter", "onBindViewHolder: "+jsonArray.toString() );

            if (jsonObject.getString("name").equalsIgnoreCase("Gift Envelope")) {
                holder.txt_size.setVisibility(View.GONE);
                holder.txt_prints.setText(jsonObject.getString("quantity") + " Gifts");
              //  holder.txt_papertypeheading.setText(" " + jsonObject.getString("giftbox_category"));
                holder.txt_papertypeheading.setVisibility(View.GONE);
                holder.txt_frametype.setVisibility(View.GONE);
                holder.txt_papercropping.setVisibility(View.GONE);
                holder.txt_papertype.setVisibility(View.GONE);
                String pp=jsonObject.getString("price");

                float price=Float.parseFloat(pp) * Float.parseFloat(jsonObject.getString("quantity"));
                holder.txt_price.setText("€ "+String.valueOf(price));



            } else if (jsonObject.getString("name").equalsIgnoreCase("Photo Album")) {
                holder.txt_prints.setText(jsonObject.getString("quantity") + " Albums");
              //  holder.txt_papertypeheading.setText(" " + jsonObject.getString("photoAlbum_category"));
                holder.txt_papertypeheading.setVisibility(View.GONE);
                holder.txt_frametype.setVisibility(View.GONE);
                holder.txt_papercropping.setVisibility(View.GONE);
                holder.txt_papertype.setVisibility(View.GONE);
                holder.txt_size.setVisibility(View.GONE);

                String pp=jsonObject.getString("price");

                float price=Float.parseFloat(pp) * Float.parseFloat(jsonObject.getString("quantity"));
                holder.txt_price.setText("€ "+String.valueOf(price));



            } else if (jsonObject.has("color_image")) {
                holder.txt_size.setText(jsonObject.getString("dimension"));
                holder.txt_prints.setText(jsonObject.getString("quantity") + " Frames");
                holder.txt_papertypeheading.setText("Color: ");
                holder.txt_papertype.setVisibility(View.GONE);
                holder.txt_frametype.setVisibility(View.GONE);
                holder.txt_papercropping.setVisibility(View.GONE);

                String pp=jsonObject.getString("price");
                float price=Float.parseFloat(pp) * Float.parseFloat(jsonObject.getString("quantity"));
                holder.txt_price.setText("€ "+String.valueOf(price));
                holder.image.setVisibility(View.VISIBLE);
                Glide.with(mContext).
                        load(jsonObject.getString("color_image"))
                        .into(holder.image);

                //   p = "€"+String.valueOf(price) ;

            }


            else   if (jsonObject.has("paper_cropping_name")) {
                holder.txt_papertype.setText(" " + jsonObject.getString("name"));
                holder.txt_papercropping.setText(" " + jsonObject.getString("paper_cropping_name"));
                holder.txt_prints.setText(jsonObject.getString("quantity") + " prints");
                holder.txt_size.setText(jsonObject.getString("dimension"));
                float price, quantity, total_price;
                price = Float.parseFloat(jsonObject.getString("price"));
                quantity = Float.parseFloat(jsonObject.getString("quantity"));
                total_price = price * quantity;
                //   holder.txt_price.setText("€"+total_price);
                holder.txt_price.setText("€ " + String.format(Locale.ENGLISH, "%.2f", total_price));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CustomTextViewNormal txt_papercropping,txt_papertype,txt_prints,txt_size,txt_papertypeheading,txt_frametype;
        CustomTextViewBold txt_price;
        RoundRectCornerImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            txt_papercropping=(CustomTextViewNormal)itemView.findViewById(R.id.txt_papercropping);
            txt_papertype=(CustomTextViewNormal)itemView.findViewById(R.id.txt_papertype);
            txt_prints=(CustomTextViewNormal)itemView.findViewById(R.id.txt_prints);
            txt_size=(CustomTextViewNormal)itemView.findViewById(R.id.txt_size);
            txt_price=(CustomTextViewBold) itemView.findViewById(R.id.txt_price);
            txt_papertypeheading=(CustomTextViewNormal)itemView.findViewById(R.id.txt_papertypeheading);
            txt_frametype=(CustomTextViewNormal)itemView.findViewById(R.id.txt_frametype);
            image=(RoundRectCornerImageView)itemView.findViewById(R.id.image);

        }
    }



}