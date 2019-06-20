package com.picmob.AppCustomView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.picmob.R;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UiHelper {
    public static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    public static final String API_KEY ="AIzaSyADVeJgmXEu524zEylXQCB0qx1D-j58N90";

    public static void applyFontToMenuItem(Context context,MenuItem mi) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), "montserrat_regular.otf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    public static ProgressDialog generateProgressDialog(Context context, boolean cancelable) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(true);
        return progressDialog;

    }
    public static void showNetworkError(final Context context, View view) {
        Snackbar snackbar = Snackbar
                .make(view, R.string.pls_check_your_internet, Snackbar.LENGTH_LONG)
                .setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (UiHelper.isOnline(context)) {
                            Snackbar snackbar1 = Snackbar.make(view, "Connected!", Snackbar.LENGTH_SHORT);
                            snackbar1.show();
                        } else {
                            showNetworkError(context, view);
                        }
                    }
                });

        snackbar.show();
    }
    public static void showToast(Context activity, String msg) {

        try {
            Toast toast = Toast.makeText(activity, msg, Toast.LENGTH_SHORT);
            View view = toast.getView();
            TextView text = (TextView) view.findViewById(android.R.id.message);
            text.setTextColor(activity.getResources().getColor(android.R.color.white));
            text.setTextSize(16);
            text.setShadowLayer(5, 0, 2, 0);
            view.setBackgroundResource(R.drawable.toast_drawable);
            view.setPadding(35, 15, 35, 15);
            toast.setGravity(Gravity.CENTER, 0, 110);
            toast.show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void hideSoftKeyboard1(Activity activity) {
        final InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {
            if (activity.getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }



}
