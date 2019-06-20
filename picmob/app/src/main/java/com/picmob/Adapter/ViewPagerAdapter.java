package com.picmob.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.picmob.Activity.GiftBoxActivity;
import com.picmob.Activity.PhotoAlbumActivity;
import com.picmob.Activity.PrintFrameActivity;
import com.picmob.Activity.ProductsActivity;
import com.picmob.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class ViewPagerAdapter  extends PagerAdapter {

    private ArrayList<Integer> images;
    private LayoutInflater inflater;
    private Context context;
    JSONArray jsonArray;
    String slider_url;
    public ViewPagerAdapter(Context context, JSONArray jsonArray,String slider_url) {
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
        View myImageLayout = inflater.inflate(R.layout.slide, view, false);
        final ImageView myImage = (ImageView) myImageLayout
                .findViewById(R.id.image);
       // Log.e("TAG", "instantiateItem: "+jsonArray.toString() );
        try {
            Glide.with(context).
                    load(slider_url+jsonArray.getJSONObject(position).getString("image"))
                    .dontAnimate()
                    .dontTransform()
                    .error(R.drawable.selected_photo)
                    .into(myImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        myImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (position==0){
//
//                    Intent intent=new Intent(context,PrintFrameActivity.class);
//                    try {
//                        intent.putExtra("product_id",jsonArray.getJSONObject(position).getString("product_id"));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    context.startActivity(intent);
//                    ((Activity) context ).overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
//
//
//                }
//
//                else if (position==1){
//
//                    Intent intent=new Intent(context,GiftBoxActivity.class);
//                    try {
//                        Log.e("ViewPagerAdapter", "onClick:productid "+ jsonArray.getJSONObject(position).getString("product_id"));
//                        intent.putExtra("product_id",jsonArray.getJSONObject(position).getString("product_id"));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    context.startActivity(intent);
//                    ((Activity) context ).overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
//
//                }
//
//                else if (position==2){
//
//                    Intent intent=new Intent(context,PhotoAlbumActivity.class);
//                    try {
//                        intent.putExtra("product_id",jsonArray.getJSONObject(position).getString("product_id"));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    context.startActivity(intent);
//                    ((Activity) context ).overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
//
//                }
//
//            }
//        });


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

        //  myImage.setImageResource(images);
        view.addView(myImageLayout, 0);
        return myImageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

}
