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
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import com.picmob.Models.EuropeanCountryModel;
import com.picmob.R;


import java.util.ArrayList;


public class EuropeanCountryAdapter extends RecyclerView.Adapter<EuropeanCountryAdapter.ViewHolder> {

    Context mContext;
    public ArrayList<EuropeanCountryModel> europeanCountryModelArrayList = new ArrayList<>();

    public EuropeanCountryAdapter(ArrayList<EuropeanCountryModel> adapter_arraylist, Context mContext) {
        europeanCountryModelArrayList=adapter_arraylist;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public EuropeanCountryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.european_country_row, parent, false);
        return new EuropeanCountryAdapter.ViewHolder(view);
    }


    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull final EuropeanCountryAdapter.ViewHolder holder, final int position) {

            holder.checkBox.setChecked(europeanCountryModelArrayList.get(position).isChecked);
            holder.checkBox.setText(europeanCountryModelArrayList.get(position).country_name);
            holder.checkBox.setOnCheckedChangeListener(null);



        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i=0;i<europeanCountryModelArrayList.size();i++){

                    europeanCountryModelArrayList.get(i).isChecked=false;
                }
                if (holder.checkBox.isChecked()) {
                    europeanCountryModelArrayList.get(position).setSelected(true);
                }
                else {
                    europeanCountryModelArrayList.get(position).setSelected(false);
                }
                europeanCountryModelArrayList.get(position).isChecked=holder.checkBox.isChecked();
                //notifyItemRangeChanged(0,europeanCountryModelArrayList.size());
                    notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return europeanCountryModelArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
     //   CustomTextViewNormal txt_name;
    //    LinearLayout mSnackView;
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);

      //      txt_name = (CustomTextViewNormal) itemView.findViewById(R.id.txt_name);
       //     mSnackView = (LinearLayout) itemView.findViewById(R.id.lyt);
            checkBox=(CheckBox)itemView.findViewById(R.id.cb_myorder);
        }
    }

}


