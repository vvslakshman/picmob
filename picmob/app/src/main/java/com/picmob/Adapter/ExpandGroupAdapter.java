package com.picmob.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.widget.TextView;


import com.picmob.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ExpandGroupAdapter extends RecyclerView.Adapter<ExpandGroupAdapter.ViewHolder> {
    Context mContext;
    JSONArray jsonArray;

    public ExpandGroupAdapter(Context mContext, JSONArray jsonArray) {
        this.jsonArray = jsonArray;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public ExpandGroupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_grouplist_expandable, parent, false);
        return new ExpandGroupAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ExpandGroupAdapter.ViewHolder holder, final int position) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject = jsonArray.getJSONObject(position);
            holder.txt_question.setText(Html.fromHtml(jsonObject.getString("question").substring(0,1).toUpperCase()+ jsonObject.getString("question").substring(1).toLowerCase()));
            holder.txt_answer.setVisibility(View.GONE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.txt_question.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {

                if (holder.txt_answer.getVisibility() == View.VISIBLE) {
                    holder.txt_question.setTextColor(mContext.getResources().getColor(R.color.gray_text));
                    holder.txt_answer.setVisibility(View.GONE);
                }
                else {
                    holder.txt_question.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                    holder.txt_answer.setVisibility(View.VISIBLE);
                    try {
                        holder.txt_answer.setText( Html.fromHtml(jsonArray.getJSONObject(position).getString("answer").replace("\n","<br />"), Html.FROM_HTML_MODE_LEGACY));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });




    }


    @Override
    public int getItemCount() {

        return jsonArray.length();
    }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView txt_question,txt_answer;


        public ViewHolder(View itemView) {
            super(itemView);
            txt_question =  itemView.findViewById(R.id.txt_question);
            txt_answer=(TextView)itemView.findViewById(R.id.txt_answer);

        }
    }


}

