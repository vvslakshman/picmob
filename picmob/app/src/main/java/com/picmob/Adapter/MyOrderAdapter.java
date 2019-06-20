package com.picmob.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.picmob.AppCustomView.CustomTextViewBold;
import com.picmob.AppCustomView.CustomTextViewNormal;
import com.picmob.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.ViewHolder> {
    Context mContext;
    JSONArray jsonArray = new JSONArray();
    public MyOrderAdapter(Context mContext, JSONArray jsonArray) {
        this.mContext = mContext;
        this.jsonArray = jsonArray;
    }


    @NonNull
    @Override
    public MyOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_order_row, parent, false);
        return new MyOrderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrderAdapter.ViewHolder holder, final int position) {
        JSONArray subCategoryArray=new JSONArray();
        try {
            final JSONObject jsonObject = new JSONObject(String.valueOf(jsonArray.getJSONObject(position)));
            Log.e("MyOrderAdapter", "onBindViewHolder: " + jsonArray.toString());

            holder.txt_date.setText(jsonObject.getString("add_date"));
            if (jsonObject.getString("delivery_type").equalsIgnoreCase("Omniva Parcel Machine"))
            holder.txt_location.setText(jsonObject.getString("location"));
            else
            holder.txt_location.setText(jsonObject.getString("location")+","+jsonObject.getString("city")+","+jsonObject.getString("country")+","+jsonObject.getString("post_code"));
            holder.txt_payment.setText(jsonObject.getString("payment_method"));
            holder.txt_shipping.setText(" "+jsonObject.getString("delivery_type"));
            holder.txt_shipping_price.setText("€"+" "+jsonObject.getString("shipping_price"));

            if (jsonObject.getString("order_status").equalsIgnoreCase("Pending"))
            {
                holder.txt_status.setText(mContext.getResources().getString(R.string.pending));
                holder.txt_status.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
            }


            else if (jsonObject.getString("order_status").equalsIgnoreCase("Declined")) {
                holder.txt_status.setText(mContext.getResources().getString(R.string.declined));
                holder.txt_status.setTextColor(mContext.getResources().getColor(R.color.red));
            }
            else if(jsonObject.getString("order_status").equalsIgnoreCase("Processing")) {
                holder.txt_status.setText(mContext.getResources().getString(R.string.processing));
                holder.txt_status.setTextColor(mContext.getResources().getColor(R.color.green));
            }
            else if (jsonObject.getString("order_status").equalsIgnoreCase("Completed")) {
                holder.txt_status.setText(mContext.getResources().getString(R.string.completed));
                holder.txt_status.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
            }

            holder.txt_total.setText("€ " + jsonObject.getString("price"));

            subCategoryArray=jsonArray.getJSONObject(position).getJSONArray("cart_product_data");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.e("MyOrderADapter", "Array: "+subCategoryArray.toString() );
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        holder.recyclerView.setNestedScrollingEnabled(false);
        holder.recyclerView.setFocusable(false);

        My_order_detail_Adapter my_order_detail_adapter=new My_order_detail_Adapter(mContext,subCategoryArray);
        holder.recyclerView.setAdapter(my_order_detail_adapter);
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CustomTextViewBold txt_total;
        CustomTextViewNormal txt_date,txt_status,txt_payment,txt_shipping,txt_location,txt_shipping_price;
        RecyclerView recyclerView;

        public ViewHolder(View itemView) {
            super(itemView);

            txt_total = (CustomTextViewBold) itemView.findViewById(R.id.txt_total);
            txt_payment = (CustomTextViewNormal) itemView.findViewById(R.id.txt_payment);
            txt_date = (CustomTextViewNormal) itemView.findViewById(R.id.txt_date);
            txt_shipping_price = (CustomTextViewNormal) itemView.findViewById(R.id.txt_shipping_price);
            txt_status= (CustomTextViewNormal) itemView.findViewById(R.id.txt_status);
            txt_shipping = (CustomTextViewNormal) itemView.findViewById(R.id.txt_shipping);
            txt_location = (CustomTextViewNormal) itemView.findViewById(R.id.txt_location);
            recyclerView=(RecyclerView)itemView.findViewById(R.id.recyclerView);

        }
    }



}