package com.picmob.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.picmob.Activity.GiftBoxActivity;
import com.picmob.Activity.PhotoAlbumActivity;
import com.picmob.Activity.PhotosActivity;
import com.picmob.Activity.PhotosOverviewActivity;
import com.picmob.Activity.PrintFrameActivity;
import com.picmob.Activity.Selected_Photos;
import com.picmob.Activity.YourOrderActivity;
import com.picmob.AppCustomView.CustomTextViewBold;
import com.picmob.AppCustomView.CustomTextViewNormal;
import com.picmob.AppCustomView.RoundRectCornerImageView;
import com.picmob.Models.GiftBoxModel;
import com.picmob.Networking.BaseApplication;
import com.picmob.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class YourOrderAdapter  extends RecyclerView.Adapter<YourOrderAdapter.ViewHolder> {
    private static final String TAG ="YouroRDERAdapter" ;
    Context mContext;

    JSONArray jsonArray = new JSONArray();
    String giftbox_price="";
    int photos_price=0;
    String photoAlbum_price="";
    float total=0;
    long click_time=0;
    long delay=700;
    public YourOrderAdapter(Context mContext, JSONArray jsonArray ) {
        this.mContext = mContext;
        this.jsonArray = jsonArray;



        for (int i=0;i<jsonArray.length();i++){

          try {

              if (jsonArray.getJSONObject(i).has("price")) {
                String ppl=jsonArray.getJSONObject(i).getString("price");
                String qua=jsonArray.getJSONObject(i).getString("photos_quantity");
                float dis=Float.parseFloat(jsonArray.getJSONObject(i).getString("discount"));
                float qntytoCheck=Float.parseFloat(jsonArray.getJSONObject(i).getString("quantity"));

                if (Float.parseFloat(qua)>qntytoCheck) {
                    float total_price = (Float.parseFloat(ppl) * Float.parseFloat(qua)) * (dis / 100);
                    float ff = (Float.parseFloat(ppl) * Float.parseFloat(qua)) - total_price;
                    total = total + ff;
                }else {
                    total = total + Float.parseFloat(ppl)*Float.parseFloat(qua);
                }
              }
              else if  (jsonArray.getJSONObject(i).has("giftbox_price")) {
                    String pp=jsonArray.getJSONObject(i).getString("giftbox_price");
                    String[] separated = pp.split("€");
                  String qua=jsonArray.getJSONObject(i).getString("giftbox_quantity");
                     total = (total + Float.parseFloat(separated[1].trim()) * Float.parseFloat(qua) );
               }
              else if  (jsonArray.getJSONObject(i).has("photoAlbum_price")) {
                  String ppg=jsonArray.getJSONObject(i).getString("photoAlbum_price");
                  String[] separated = ppg.split("€");
                  String qua=jsonArray.getJSONObject(i).getString("photoAlbum_quantity");
                  total = (total + Float.parseFloat(separated[1].trim()) * Float.parseFloat(qua));
              }
              else if  (jsonArray.getJSONObject(i).has("frame_price")) {
                  String pp=jsonArray.getJSONObject(i).getString("frame_price");
                  String[] separated = pp.split("€");
                  String qua=jsonArray.getJSONObject(i).getString("frame_quantity");
                  total = (total + Float.parseFloat(separated[1].trim()) * Float.parseFloat(qua));
              }

             } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ((YourOrderActivity)mContext).setTotalPrice(total);
        //   total=ss+price;
        Log.e(TAG, "total_price: "+total );
//        }

    }


    @NonNull
    @Override
    public YourOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.your_order_row, parent, false);
        return new YourOrderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull YourOrderAdapter.ViewHolder holder, final int position) {
        try {

            Log.e(TAG, "onBindViewHolder: "+jsonArray.toString() );
            final JSONObject jsonObject = new JSONObject(String.valueOf(jsonArray.getJSONObject(position)));

//            if (jsonArray.length()>0){
//        for (int i=0;i<jsonArray.length();i++) {
//            JSONObject cartObject=jsonArray.getJSONObject(i);
//            if (cartObject.getString("type").equalsIgnoreCase(jsonArray.getJSONObject(position).getString("type")) && cartObject.getString("giftbox_category").equalsIgnoreCase(jsonArray.getJSONObject(position).getString("giftbox_category"))){
//
//                Log.e(TAG, "onBind Cart Object: "+cartObject.getString("type"));
//                holder.txt_prints.setText(cartObject.getString("giftbox_quantity") + jsonArray.getJSONObject(position).getString("giftbox_quantity"));
//
//            }
//        }
//
//            }
//
//            else {

                if (jsonObject.has("giftbox_price")) {
                    holder.txt_title.setText(R.string.giftbox);
                    holder.txt_size.setVisibility(View.GONE);
                    holder.btn_edit.setVisibility(View.VISIBLE);
                    holder.txt_prints.setText(jsonObject.getString("giftbox_quantity") + " Giftbox");
                    holder.txt_papertypeheading.setText(jsonObject.getString("giftbox_category"));
                    holder.txt_frametype.setVisibility(View.GONE);

                    float tt=0;
                    String pp=jsonObject.getString("giftbox_price");
                    String[] separated = pp.split("€");
                    tt = Float.parseFloat(separated[1].trim());

                   float price=tt * Float.parseFloat(jsonObject.getString("giftbox_quantity"));
                    holder.txt_price.setText("€ "+String.format("%.2f",price));
                    giftbox_price = "€"+String.valueOf(price);

                    holder.roundRectCornerImageView.setImageResource(R.drawable.giftbox_product);

                } else if (jsonObject.has("photoAlbum_price")) {
                    holder.txt_title.setText(R.string.photo_album);
                    holder.btn_edit.setVisibility(View.VISIBLE);
                    holder.txt_prints.setText(jsonObject.getString("photoAlbum_quantity") + " Photo Album");
                    holder.txt_papertypeheading.setText(jsonObject.getString("photoAlbum_category"));
                    holder.txt_frametype.setVisibility(View.GONE);
                    holder.txt_size.setVisibility(View.GONE);

                    float tt=0;
                    String pp=jsonObject.getString("photoAlbum_price");
                    String[] separated = pp.split("€");
                    tt = Float.parseFloat(separated[1].trim());


                    float price=tt * Float.parseFloat(jsonObject.getString("photoAlbum_quantity"));
                    holder.txt_price.setText("€ "+String.format("%.2f",price));
                    photoAlbum_price = "€"+String.valueOf(price) ;


                    holder.roundRectCornerImageView.setImageResource(R.drawable.photo_album);

                } else if (jsonObject.has("frame_size")) {
                    holder.txt_title.setText(R.string.print_frame);
                    holder.btn_edit.setVisibility(View.VISIBLE);
                    holder.txt_size.setVisibility(View.GONE);
                  //  holder.txt_size.setText(jsonObject.getString("frame_size"));
                    holder.txt_prints.setText(jsonObject.getString("frame_quantity") + " Print Frame");
                    holder.txt_papertypeheading.setText("Size "+jsonObject.getString("frame_size"));
                  //  holder.txt_papertype.setText(" " + "Print Frame");
                    holder.txt_frametype.setVisibility(View.GONE);
                    holder.txt_papertype.setVisibility(View.GONE);
                    holder.color_image.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .load(jsonObject.getString("color_image"))
                            .into(holder.color_image);

                    float tt=0;
                    String pp=jsonObject.getString("frame_price");
                    String[] separated = pp.split("€");
                    tt = Float.parseFloat(separated[1].trim());


                    float price=tt * Float.parseFloat(jsonObject.getString("frame_quantity"));
                    holder.txt_price.setText("€ "+String.format("%.2f",price));
                 //   p = "€"+String.valueOf(price) ;


                    holder.roundRectCornerImageView.setImageResource(R.drawable.print_frame);

                }


              else   if (jsonObject.has("paper_name")) {
                    holder.txt_title.setText(R.string.prints);
                    holder.txt_papertype.setText(" " + jsonObject.getString("paper_name"));
                    holder.txt_papercropping.setText(" " + jsonObject.getString("paper_cropping_type"));

                    if (jsonObject.getString("photos_quantity").equalsIgnoreCase("1"))
                    holder.txt_prints.setText(jsonObject.getString("photos_quantity") + " print");
                    else
                        holder.txt_prints.setText(jsonObject.getString("photos_quantity") + " prints");
                    holder.txt_size.setText(jsonObject.getString("height") + "x" + jsonObject.getString("width") + "cm");
                    holder.txt_papertypeheading.setVisibility(View.GONE);
                    holder.txt_frametype.setVisibility(View.GONE);
                    photos_price = jsonObject.getInt("price");

                    float price, quantity, total_price,discount;
                    price = Float.parseFloat(jsonObject.getString("price"));
                    quantity = Float.parseFloat(jsonObject.getString("photos_quantity"));
                    discount=Float.parseFloat(jsonObject.getString("discount"));
                    float qntytoCheck=Float.parseFloat(jsonObject.getString("quantity"));

                    if (quantity>qntytoCheck) {
                        float TotalPricetoshow=price * quantity;
                        total_price = (price * quantity) * (discount / 100);
                        float ff = (price * quantity) - total_price;
                        //   holder.txt_price.setText("€"+total_price);
                     //   holder.txt_price.setText("€ " + String.format(Locale.ENGLISH, "%.2f", ff));
                        holder.txt_price.setText("€ " + String.format(Locale.ENGLISH, "%.2f", TotalPricetoshow));
                        holder.txt_discount.setVisibility(View.VISIBLE);
                        holder.txt_discountprice.setVisibility(View.VISIBLE);
                        holder.txt_discount.setText("-"+discount+"%");
                        holder.txt_discountprice.setText("€ " + String.format(Locale.ENGLISH, "%.2f", ff));
                    }else {
                        total_price = price * quantity;
                        holder.txt_price.setText("€ " + String.format(Locale.ENGLISH, "%.2f", total_price));
                        holder.txt_discountprice.setVisibility(View.VISIBLE);
                        holder.txt_discountprice.setText("€ " + String.format(Locale.ENGLISH, "%.2f", total_price));
                    }

                    Glide.with(mContext).
                            load(jsonObject.getJSONArray("images").getJSONObject(0).getString("image"))
                            .into(holder.roundRectCornerImageView);

                }



            holder.btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   // try {
                       // delete(jsonArray.getJSONObject(position).getString("product_id_images"));
                    if (SystemClock.elapsedRealtime()-click_time < delay)
                        return;
                    click_time=SystemClock.elapsedRealtime();

                    if(!((YourOrderActivity)mContext).allowClick)
                       return;

                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext,R.style.AlertDialog);
                    builder.setTitle("");
                    builder.setMessage(R.string.Are_u_sure_To_delete);
                    builder.setPositiveButton(mContext.getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            try {
                                if (jsonArray.getJSONObject(position).has("product_id_images"))
                                    delete(jsonArray.getJSONObject(position).getString("product_id_images"),position);
                                else
                                    delete(position);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    android.support.v7.app.AlertDialog alertDialog = builder.create();
                    alertDialog.show();



                    //   } catch (JSONException e) {
                     //   e.printStackTrace();
                  //  }
                }
            });

            holder.btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime()-click_time < delay)
                        return;
                    click_time=SystemClock.elapsedRealtime();
                    if(!((YourOrderActivity)mContext).allowClick)
                        return;
                    try {
                        if (jsonArray.getJSONObject(position).has("giftbox_price")){
                                Intent intent=new Intent(mContext, GiftBoxActivity.class);
                                intent.putExtra("product_id",jsonArray.getJSONObject(position).getString("product_id"));
                                intent.putExtra("edit","yes");
                                intent.putExtra("quantity",jsonArray.getJSONObject(position).getString("giftbox_quantity"));
                                intent.putExtra("category",jsonArray.getJSONObject(position).getString("giftbox_category"));
                                intent.putExtra("giftbox_category_id",jsonArray.getJSONObject(position).getInt("giftbox_category_id"));
                                intent.putExtra("giftbox_price",jsonArray.getJSONObject(position).getString("giftbox_price"));
                                intent.putExtra("giftbox_id",jsonArray.getJSONObject(position).getString("giftbox_id"));
                                intent.putExtra("position",position);
                                mContext.startActivity(intent);
                        }
                        else if (jsonArray.getJSONObject(position).has("photoAlbum_price")){
                            Intent intent=new Intent(mContext, PhotoAlbumActivity.class);
                            intent.putExtra("product_id",jsonArray.getJSONObject(position).getString("product_id"));
                            intent.putExtra("edit","yes");
                            intent.putExtra("quantity",jsonArray.getJSONObject(position).getString("photoAlbum_quantity"));
                            intent.putExtra("category",jsonArray.getJSONObject(position).getString("photoAlbum_category"));
                            intent.putExtra("photoAlbum_category_id",jsonArray.getJSONObject(position).getInt("photoAlbum_category_id"));
                            intent.putExtra("photoAlbum_price",jsonArray.getJSONObject(position).getString("photoAlbum_price"));
                            intent.putExtra("photoAlbum_id",jsonArray.getJSONObject(position).getString("photoAlbum_id"));
                            intent.putExtra("position",position);
                            mContext.startActivity(intent);

                        }
                        else if (jsonArray.getJSONObject(position).has("frame_price")){

                            Intent intent=new Intent(mContext, PrintFrameActivity.class);
                            intent.putExtra("product_id",jsonArray.getJSONObject(position).getString("product_id"));
                            intent.putExtra("edit","yes");
                            intent.putExtra("quantity",jsonArray.getJSONObject(position).getString("frame_quantity"));
                            intent.putExtra("size_id",jsonArray.getJSONObject(position).getInt("size_id"));
                            intent.putExtra("color_id",jsonArray.getJSONObject(position).getInt("color_id"));
                            intent.putExtra("frame_price",jsonArray.getJSONObject(position).getString("frame_price"));
                            mContext.startActivity(intent);

                        }
                        else
                        editProduct(jsonArray.getJSONObject(position).getString("product_id_images"),position);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void editProduct(String product_id,int position) {

        PhotosActivity.productId=product_id;
        Intent intent=new Intent(mContext, Selected_Photos.class);
        try {
           // intent.putExtra("paper_name",jsonArray.getJSONObject(position).getString("paper_name"));
          //  intent.putExtra("paper_cropping_type",jsonArray.getJSONObject(position).getString("paper_cropping_type"));
            intent.putExtra("price",jsonArray.getJSONObject(position).getString("price"));
            intent.putExtra("width",jsonArray.getJSONObject(position).getString("width"));
            intent.putExtra("height",jsonArray.getJSONObject(position).getString("height"));
            intent.putExtra("discount",jsonArray.getJSONObject(position).getString("discount"));
            intent.putExtra("quantity",jsonArray.getJSONObject(position).getString("quantity"));
            intent.putExtra("picture_size_id",jsonArray.getJSONObject(position).getString("picture_size_id"));
            intent.putExtra("newProduct","no");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        mContext.startActivity(intent);


    }

    @SuppressLint("NewApi")
    private void delete(String product_id,int position) {

        String cartData=BaseApplication.getInstance().getSession().getCartData();
        JSONArray newjsonArray = null;
        try {
            newjsonArray = new JSONArray(cartData);

            if (newjsonArray != null) {
                newjsonArray.remove(position);
                Log.e("before", "array items: "+jsonArray.length());
                jsonArray=newjsonArray ;
                notifyDataSetChanged();
                BaseApplication.getInstance().getSession().setCartData(newjsonArray.toString());
                if (jsonArray.length()==0){
                    Log.e(TAG, "zero length: " );
                    ((YourOrderActivity)mContext).cartEmpty();
                }
                Log.e(TAG, "delete: "+BaseApplication.getInstance().getSession().getCartData());
                Log.e("after", "array items: "+jsonArray.length());
            }

            String images=BaseApplication.getInstance().getSession().getSelectedImages();
            JSONArray productArrray=new JSONArray(images);

            for (int i=0;i<productArrray.length();i++){
                if (product_id.equalsIgnoreCase(productArrray.getJSONObject(i).getString("id"))){
                    productArrray.remove(i);
                    BaseApplication.getInstance().getSession().setSelectedImages(productArrray.toString());
                    Log.e(TAG, "delete: "+productArrray.toString() );
                    break;

                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        float tt=0;
        for (int i=0;i<jsonArray.length();i++){

            try {

                if (jsonArray.getJSONObject(i).has("price")) {
                    String ppl = jsonArray.getJSONObject(i).getString("price");
                    String qua = jsonArray.getJSONObject(i).getString("photos_quantity");
                    float dis=Float.parseFloat(jsonArray.getJSONObject(i).getString("discount"));
                    float qntytoCheck=Float.parseFloat(jsonArray.getJSONObject(i).getString("quantity"));
                    if (Float.parseFloat(qua)>qntytoCheck) {
                        float total_price = (Float.parseFloat(ppl) * Float.parseFloat(qua)) * (dis / 100);
                        float ff = (Float.parseFloat(ppl) * Float.parseFloat(qua)) - total_price;
                        tt = tt + ff;
                    }else {
                        tt = tt + Float.parseFloat(ppl)*Float.parseFloat(qua);
                    }
                    //    tt = tt + Float.parseFloat(ppl) * Float.parseFloat(qua);
                }
                else if  (jsonArray.getJSONObject(i).has("giftbox_price")) {
                    String pp=jsonArray.getJSONObject(i).getString("giftbox_price");
                    String[] separated = pp.split("€");
                    tt = tt + Float.parseFloat(separated[1].trim());
                } else if  (jsonArray.getJSONObject(i).has("photoAlbum_price")) {
                    String ppg=jsonArray.getJSONObject(i).getString("photoAlbum_price");
                    String[] separated = ppg.split("€");
                    tt = tt + Float.parseFloat(separated[1].trim());
                }else if  (jsonArray.getJSONObject(i).has("frame_price")) {
                    String pp=jsonArray.getJSONObject(i).getString("frame_price");
                    String[] separated = pp.split("€");
                    tt = tt + Float.parseFloat(separated[1].trim());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ((YourOrderActivity)mContext).setTotalPrice(tt);


    }

    @SuppressLint("NewApi")
    private void delete(int position) {

        String cartData=BaseApplication.getInstance().getSession().getCartData();
        JSONArray newjsonArray = null;
        try {
            newjsonArray = new JSONArray(cartData);

            if (newjsonArray != null) {
                newjsonArray.remove(position);
                Log.e("before", "array items: "+jsonArray.length());
                Log.e(TAG, "JSONARRAY: "+jsonArray.toString() );
                Log.e(TAG, "NEW JSONARRAY: "+newjsonArray.toString() );

                jsonArray=newjsonArray ;
                notifyDataSetChanged();
                BaseApplication.getInstance().getSession().setCartData(newjsonArray.toString());
                if (jsonArray.length()==0){
                    Log.e(TAG, "zero length: " );
                    ((YourOrderActivity)mContext).cartEmpty();
                }
                Log.e(TAG, "delete: "+BaseApplication.getInstance().getSession().getCartData());
                Log.e("after", "array items: "+jsonArray.length());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
//        try {
//            newjsonArray = new JSONArray(cartData);
//            for (int i=0;i<newjsonArray.length();i++) {
//                if (position.equalsIgnoreCase(newjsonArray.getJSONObject(i).getString("product_id_images"))){
//                    newjsonArray.remove(i);
//                    jsonArray=newjsonArray;
//                    BaseApplication.getInstance().getSession().setCartData(newjsonArray.toString());
//                    Log.e("before", "array items: "+newjsonArray.length());
//
//                    if (jsonArray.length()==0){
//                        Log.e(TAG, "zero length: " );
//                        ((YourOrderActivity)mContext).cartEmpty();
//                    }
//                    Log.e(TAG, "delete: "+BaseApplication.getInstance().getSession().getCartData());
//
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }



        float tt=0;
        for (int i=0;i<jsonArray.length();i++){

            try {

                if (jsonArray.getJSONObject(i).has("price")) {
                    String ppl = jsonArray.getJSONObject(i).getString("price");
                    String qua = jsonArray.getJSONObject(i).getString("photos_quantity");
                    float dis=Float.parseFloat(jsonArray.getJSONObject(i).getString("discount"));
                    float qntytoCheck=Float.parseFloat(jsonArray.getJSONObject(i).getString("quantity"));
                    if (Float.parseFloat(qua)>qntytoCheck) {
                        float total_price = (Float.parseFloat(ppl) * Float.parseFloat(qua)) * (dis / 100);
                        float ff = (Float.parseFloat(ppl) * Float.parseFloat(qua)) - total_price;
                        tt = tt + ff;
                    }else {
                        tt = tt + Float.parseFloat(ppl)*Float.parseFloat(qua);
                    }
                //    tt = tt + Float.parseFloat(ppl) * Float.parseFloat(qua);
                }
                else if  (jsonArray.getJSONObject(i).has("giftbox_price")) {
                    String pp=jsonArray.getJSONObject(i).getString("giftbox_price");
                    String[] separated = pp.split("€");
                    String qua = jsonArray.getJSONObject(i).getString("giftbox_quantity");
                    tt = tt + Float.parseFloat(separated[1].trim()) * Float.parseFloat(qua);

                } else if  (jsonArray.getJSONObject(i).has("photoAlbum_price")) {
                    String ppg=jsonArray.getJSONObject(i).getString("photoAlbum_price");
                    String[] separated = ppg.split("€");
                    String qua = jsonArray.getJSONObject(i).getString("photoAlbum_quantity");
                    tt = tt + Float.parseFloat(separated[1].trim()) * Float.parseFloat(qua);

                }else if  (jsonArray.getJSONObject(i).has("frame_price")) {
                    String pp=jsonArray.getJSONObject(i).getString("frame_price");
                    String[] separated = pp.split("€");
                    String qua = jsonArray.getJSONObject(i).getString("frame_quantity");
                    tt = tt + Float.parseFloat(separated[1].trim()) * Float.parseFloat(qua);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.e(TAG, "delete: TOTALPRICE:"+tt );
        ((YourOrderActivity)mContext).setTotalPrice(tt);


    }


    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CustomTextViewNormal txt_papercropping,txt_papertype,txt_prints,txt_size,txt_papertypeheading,txt_frametype,txt_discount,txt_discountprice;
        CustomTextViewBold txt_price,txt_title;
        Button btn_edit,btn_delete;
        ImageView roundRectCornerImageView;
        ImageView color_image;
        public ViewHolder(View itemView) {
            super(itemView);

            txt_papercropping=(CustomTextViewNormal)itemView.findViewById(R.id.txt_papercropping);
            txt_papertype=(CustomTextViewNormal)itemView.findViewById(R.id.txt_papertype);
            txt_prints=(CustomTextViewNormal)itemView.findViewById(R.id.txt_prints);
            txt_size=(CustomTextViewNormal)itemView.findViewById(R.id.txt_size);
            txt_price=(CustomTextViewBold) itemView.findViewById(R.id.txt_price);
            btn_edit=(Button)itemView.findViewById(R.id.btn_edit);
            btn_delete=(Button)itemView.findViewById(R.id.btn_delete);
            txt_papertypeheading=(CustomTextViewNormal)itemView.findViewById(R.id.txt_papertypeheading);
            txt_frametype=(CustomTextViewNormal)itemView.findViewById(R.id.txt_frametype);
            txt_discount=(CustomTextViewNormal)itemView.findViewById(R.id.txt_discount);
            txt_discountprice=(CustomTextViewNormal)itemView.findViewById(R.id.txt_discountprice);
            txt_title=(CustomTextViewBold)itemView.findViewById(R.id.txt_title);
            roundRectCornerImageView=(ImageView) itemView.findViewById(R.id.image);
            color_image=itemView.findViewById(R.id.color_image);
        }
    }


}
