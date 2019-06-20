package com.picmob.AppCustomView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.rengwuxian.materialedittext.MaterialEditText;


@SuppressLint("AppCompatCustomView")
public class CustomEditTextBold extends MaterialEditText {
    Context context;

    public CustomEditTextBold(Context context) {
        super(context);
        this.context = context;
      //  init(context);
    }

    public CustomEditTextBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
      //  init(context);
    }

    public CustomEditTextBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    //    init(context);

    }
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
//    public void init(Context context) {
//        Typeface face = Typeface.createFromAsset(context.getAssets(), "montserrat_bold.otf");
//        this.setTypeface(face);
//    }
}
