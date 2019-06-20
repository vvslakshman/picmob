package com.picmob.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.picmob.Activity.OrderingActivity;
import com.picmob.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class InfoHelpAdapter extends RecyclerView.Adapter<InfoHelpAdapter.ViewHolder> {
    Context mContext;
    JSONArray jsonArray = new JSONArray();

    public InfoHelpAdapter(Context mContext, JSONArray jsonArray) {
        this.mContext = mContext;
        this.jsonArray = jsonArray;
    }


    @NonNull
    @Override
    public InfoHelpAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_help_row, parent, false);
        return new InfoHelpAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoHelpAdapter.ViewHolder holder, final int position) {
        try {
            final JSONObject jsonObject = new JSONObject(String.valueOf(jsonArray.getJSONObject(position)));

            holder.btn_ordering.setText(jsonObject.getString("topic_name"));
            holder.btn_ordering.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(mContext, OrderingActivity.class);
                    try {
                        intent.putExtra("id",jsonArray.getJSONObject(position).getString("info_id"));
                        intent.putExtra("title",jsonArray.getJSONObject(position).getString("topic_name"));
                        mContext.startActivity(intent);
                        ((Activity) mContext).overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
                    } catch (JSONException e) {
                        e.printStackTrace();
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        Button btn_ordering;


        public ViewHolder(View itemView) {
            super(itemView);

            btn_ordering = itemView.findViewById(R.id.btn_ordering);


        }
    }


}
