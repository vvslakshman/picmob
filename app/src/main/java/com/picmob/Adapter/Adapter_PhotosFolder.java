package com.picmob.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.picmob.AppCustomView.RoundRectCornerImageView;
import com.picmob.Models.Model_folders;
import com.picmob.R;

import java.util.ArrayList;

public class Adapter_PhotosFolder extends ArrayAdapter<Model_folders> {

    Context context;
    ViewHolder viewHolder;
    ArrayList<Model_folders> al_menu = new ArrayList<>();


    public Adapter_PhotosFolder(Context context, ArrayList<Model_folders> al_menu) {
        super(context, R.layout.adapter_photosfolder, al_menu);
        this.al_menu = al_menu;
        this.context = context;


    }

    @Override
    public int getCount() {

        Log.e("ADAPTER LIST SIZE", al_menu.size() + "");
        return al_menu.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        if (al_menu.size() > 0) {
            return al_menu.size();
        } else {
            return 1;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_photosfolder, parent, false);
            viewHolder.tv_foldern = (TextView) convertView.findViewById(R.id.tv_folder);
            viewHolder.tv_foldersize = (TextView) convertView.findViewById(R.id.tv_folder2);
            viewHolder.iv_image = (RoundRectCornerImageView) convertView.findViewById(R.id.iv_image);



            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tv_foldern.setText(al_menu.get(position).getStr_folder());
        viewHolder.tv_foldersize.setText(al_menu.get(position).getImageList().size()+"");



        if (al_menu.get(position).getStr_folder().equalsIgnoreCase("PicMob")){
            viewHolder.iv_image.setImageDrawable(context.getResources().getDrawable(R.mipmap.app_icon));
        }else {
            Glide.with(context).load(al_menu.get(position).getImageList().get(0).getSingle_imagepath())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(viewHolder.iv_image);
        }

        return convertView;

    }

    private static class ViewHolder {
        TextView tv_foldern, tv_foldersize;
        RoundRectCornerImageView iv_image;


    }


}
