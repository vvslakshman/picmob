package com.picmob.AppCustomView;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;




public class CustomTextInputLayout extends TextInputLayout {
    Typeface font_type;
    Context context;

    public CustomTextInputLayout(Context context, String fontName) {
        super(context);
        this.context = context;
        // this.font = fontName;
    }

    public CustomTextInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context);
        // TODO Auto-generated constructor stub
    }

    public CustomTextInputLayout(Context context) {
        super(context);
        this.context = context;
        init(context);
        // TODO Auto-generated constructor stub
    }

    public CustomTextInputLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init(context);
        // TODO Auto-generated constructor stub
    }

    public void init(Context context) {
        Typeface face = Typeface.createFromAsset(context.getAssets(), "montserrat_regular.otf");
        this.setTypeface(face);
    }
}
