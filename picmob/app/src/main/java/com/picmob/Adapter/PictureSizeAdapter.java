package com.picmob.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.picmob.AppCustomView.CustomTextViewBold;
import com.picmob.AppCustomView.CustomTextViewNormal;
import com.picmob.Networking.BaseApplication;
import com.picmob.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PictureSizeAdapter  extends RecyclerView.Adapter<PictureSizeAdapter.ViewHolder> {
    Context mContext;
    ClickItemEvent clickItemEvent;
    JSONArray jsonArray = new JSONArray();

    public PictureSizeAdapter(Context mContext, JSONArray jsonArray, ClickItemEvent clickItemEvent) {
        this.mContext = mContext;
        this.jsonArray = jsonArray;
        this.clickItemEvent=clickItemEvent;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_sizes_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        try {
            final JSONObject jsonObject = new JSONObject(String.valueOf(jsonArray.getJSONObject(position)));
            Log.e("PictureSizeAdapter", "onBindViewHolder: "+jsonArray.toString() );
            holder.txt_type.setText(jsonObject.getString("print_type"));

            int pp=jsonObject.getInt("discount");
            //int[] separated = pp.split(".");

            holder.txt_discount.setText(String.valueOf(pp)+"%");
            holder.txt_price.setText("€ "+jsonObject.getString("price"));
            holder.txt_quantity.setText(jsonObject.getString("quantity"));
            holder.txt_size.setText(jsonObject.getString("height")+"x"+jsonObject.getString("width")+"cm");
            Glide.with(mContext).
                    load(jsonArray.getJSONObject(position).getString("image"))
                    .into(holder.imageView);
            holder.linlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  //  Intent intent=new Intent(mContext, Selected_Photos.class);
                    try {
                        clickItemEvent.onClickItem(jsonArray.getJSONObject(position).getString("id"),jsonArray.getJSONObject(position).getString("height"),jsonArray.getJSONObject(position).getString("width"),jsonArray.getJSONObject(position).getString("price") ,String.valueOf(position),jsonArray.getJSONObject(position).getString("discount"),jsonArray.getJSONObject(position).getString("quantity"));
                        BaseApplication.getInstance().getSession().setWidth(jsonArray.getJSONObject(position).getString("width"));
                        BaseApplication.getInstance().getSession().setHeight(jsonArray.getJSONObject(position).getString("height"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                 //   mContext.startActivity(intent);
                  //  ((Activity) mContext ).overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        CustomTextViewBold txt_type,txt_price;
        CustomTextViewNormal txt_size;
        TextView txt_discount,txt_quantity;
        LinearLayout linlayout;
        CardView cardView;
        ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            txt_discount = (TextView) itemView.findViewById(R.id.txt_discount);
            txt_quantity = (TextView) itemView.findViewById(R.id.txt_quantity);
            txt_price= (CustomTextViewBold) itemView.findViewById(R.id.txt_price);
            txt_type= (CustomTextViewBold) itemView.findViewById(R.id.txt_type);
            txt_size=(CustomTextViewNormal)itemView.findViewById(R.id.txt_size);
            linlayout=(LinearLayout)itemView.findViewById(R.id.linlayout);
            cardView=(CardView)itemView.findViewById(R.id.cardview);
            imageView=(ImageView)itemView.findViewById(R.id.image);
        }
    }
    public interface ClickItemEvent{
        void onClickItem(String id,String height,String width,String price,String position,String discount,String quantity);
    }

}
