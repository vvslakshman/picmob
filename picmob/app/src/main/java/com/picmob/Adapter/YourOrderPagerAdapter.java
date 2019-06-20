package com.picmob.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.picmob.Activity.ProductsActivity;
import com.picmob.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class YourOrderPagerAdapter extends PagerAdapter {

    private ArrayList<Integer> images;
    private LayoutInflater inflater;
    private Context context;
  //  JSONArray jsonArray;
    String slider_url;

    public YourOrderPagerAdapter(Context context,String slider_url) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.slider_url=slider_url;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        View myImageLayout = inflater.inflate(R.layout.slide, view, false);
        final ImageView myImage = (ImageView) myImageLayout
                .findViewById(R.id.image);
        // Log.e("TAG", "instantiateItem: "+jsonArray.toString() );

            Glide.with(context).
                    load(slider_url)
                    .into(myImage);


        myImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             //   if (position==0){

                    Intent intent=new Intent(context,ProductsActivity.class);
                    context.startActivity(intent);
                    ((Activity) context ).overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);


             //   }


            }
        });
        view.addView(myImageLayout, 0);
        return myImageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

}
