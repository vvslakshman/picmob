package com.picmob.AppCustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;


public class CustomTextViewNormal extends android.support.v7.widget.AppCompatTextView {

    Context context;

    public CustomTextViewNormal(Context context) {
        super(context);
        this.context = context;
      //  init(context);
    }

    public CustomTextViewNormal(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
     //   init(context);
    }

    public CustomTextViewNormal(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
   //     init(context);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

//    public void init(Context context) {
//        Typeface face = Typeface.createFromAsset(context.getAssets(), "montserrat_regular.otf");
//        this.setTypeface(face);
//    }
}
