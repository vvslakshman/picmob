package com.picmob.AppCustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;


public class CustomAutoCompleteTextView extends android.support.v7.widget.AppCompatAutoCompleteTextView {
    Context context;

    public CustomAutoCompleteTextView(Context context) {
        super(context);
        this.context = context;
        init(context);
        //Typeface face= Typeface.createFromAsset(context.getAssets(), "Helvetica_Neue.ttf");
        //this.setTypeface(face);
    }

    public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context);
        //Typeface face=Typeface.createFromAsset(context.getAssets(), "Helvetica_Neue.ttf");
        //this.setTypeface(face);
    }

    public CustomAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init(context);
        //Typeface face=Typeface.createFromAsset(context.getAssets(), "Helvetica_Neue.ttf");
        //this.setTypeface(face);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


    }

    public void init(Context context) {
        Typeface face = Typeface.createFromAsset(context.getAssets(), "montserrat_bold.otf");
        this.setTypeface(face);
    }
}