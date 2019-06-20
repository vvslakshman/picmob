package com.picmob.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import com.picmob.Models.OmnivaAddressModel;
import com.picmob.R;

import java.util.ArrayList;

public class OmnivaAddressAdapter extends RecyclerView.Adapter<OmnivaAddressAdapter.ViewHolder> {

    Context mContext;
    public ArrayList<OmnivaAddressModel> omnivaAddressModelArrayList = new ArrayList<>();

    public OmnivaAddressAdapter(ArrayList<OmnivaAddressModel> adapter_arraylist, Context mContext) {
        omnivaAddressModelArrayList=adapter_arraylist;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public OmnivaAddressAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.european_country_row, parent, false);
        return new OmnivaAddressAdapter.ViewHolder(view);
    }


    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull final OmnivaAddressAdapter.ViewHolder holder, final int position) {
        holder.checkBox.setChecked(omnivaAddressModelArrayList.get(position).isChecked);
        holder.checkBox.setText(omnivaAddressModelArrayList.get(position).country_name);
        holder.checkBox.setOnCheckedChangeListener(null);



        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i=0;i<omnivaAddressModelArrayList.size();i++){

                    omnivaAddressModelArrayList.get(i).isChecked=false;
                }
                if (holder.checkBox.isChecked()) {
                    omnivaAddressModelArrayList.get(position).setSelected(true);
                }
                else {
                    omnivaAddressModelArrayList.get(position).setSelected(false);
                }
                omnivaAddressModelArrayList.get(position).isChecked=holder.checkBox.isChecked();
              //  notifyItemRangeChanged(0,omnivaAddressModelArrayList.size());
                  notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return omnivaAddressModelArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
    //    LinearLayout mSnackView;
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
      //      mSnackView = (LinearLayout) itemView.findViewById(R.id.lyt);
            checkBox=(CheckBox)itemView.findViewById(R.id.cb_myorder);
        }
    }

}
