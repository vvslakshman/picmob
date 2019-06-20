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

public class MyCardAdapter extends RecyclerView.Adapter<MyCardAdapter.ViewHolder> {
    Context mContext;
    JSONArray jsonArray = new JSONArray();

    public MyCardAdapter(Context mContext, JSONArray jsonArray) {
        this.mContext = mContext;
        this.jsonArray = jsonArray;
    }


    @NonNull
    @Override
    public MyCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_order_row, parent, false);
        return new MyCardAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyCardAdapter.ViewHolder holder, final int position) {
        JSONArray subCategoryArray=new JSONArray();
        try {
            final JSONObject jsonObject = new JSONObject(String.valueOf(jsonArray.getJSONObject(position)));
            Log.e("MyOrderAdapter", "onBindViewHolder: " + jsonArray.toString());

            holder.txt_date.setText(jsonObject.getString("add_date"));
            holder.txt_location.setText(jsonObject.getString("location"));
            holder.txt_payment.setText(jsonObject.getString("payment_method"));
            holder.txt_shipping.setText(jsonObject.getString("delivery_type"));


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CustomTextViewBold txt_total;
        CustomTextViewNormal txt_date,txt_status,txt_payment,txt_shipping,txt_location;


        public ViewHolder(View itemView) {
            super(itemView);

            txt_total = (CustomTextViewBold) itemView.findViewById(R.id.txt_total);
            txt_payment = (CustomTextViewNormal) itemView.findViewById(R.id.txt_payment);
            txt_date = (CustomTextViewNormal) itemView.findViewById(R.id.txt_date);
            txt_status= (CustomTextViewNormal) itemView.findViewById(R.id.txt_status);
            txt_shipping = (CustomTextViewNormal) itemView.findViewById(R.id.txt_shipping);
            txt_location = (CustomTextViewNormal) itemView.findViewById(R.id.txt_location);


        }
    }



}