package com.picmob.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.picmob.Activity.PrintFrameActivity;
import com.picmob.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class PrintFrameViewPagerAdapter extends PagerAdapter {

    private ArrayList<Integer> images;
    private LayoutInflater inflater;
    private Context context;
    JSONArray jsonArray;
    String slider_url;

    public PrintFrameViewPagerAdapter(Context context, JSONArray jsonArray,String slider_url) {
        this.context = context;
        this.jsonArray=jsonArray;
        inflater = LayoutInflater.from(context);
        this.slider_url=slider_url;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return jsonArray.length();
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        View myImageLayout = inflater.inflate(R.layout.printframe_viewpager_row, view, false);
        ImageView myImage = (ImageView) myImageLayout
                .findViewById(R.id.image);

        try {
            Glide.with(context).
                    load(slider_url+jsonArray.getJSONObject(position).getString("image"))
                    .into(myImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }



        //  myImage.setImageResource(images);
        view.addView(myImageLayout, 0);
        return myImageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

}
