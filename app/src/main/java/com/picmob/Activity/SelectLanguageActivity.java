package com.picmob.Activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.picmob.AppCustomView.UiHelper;
import com.picmob.Networking.BaseApplication;
import com.picmob.R;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectLanguageActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.lyt_english)
    LinearLayout lyt_english;

    @BindView(R.id.lyt_latvia)
    LinearLayout lyt_latvia;

    @BindView(R.id.lyt_estonia)
    LinearLayout lyt_estonia;

    @BindView(R.id.lyt_lithuania)
    LinearLayout lyt_lithuania;

    @BindView(R.id.lyt_russia)
    LinearLayout lyt_russia;

    @BindView(R.id.cb_english)
    CheckBox cb_english;

    @BindView(R.id.cb_estonia)
    CheckBox cb_estonia;

    @BindView(R.id.cb_latvia)
    CheckBox cb_latvia;

    @BindView(R.id.cb_lithuania)
    CheckBox cb_lithuania;

    @BindView(R.id.cb_russia)
    CheckBox cb_russia;

    @BindView(R.id.btn_confirm)
    Button btn_confirm;

    String checked="en";
    Locale myLocale;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);
        ButterKnife.bind(this);

        lyt_english.setOnClickListener(this);
        lyt_latvia.setOnClickListener(this);
        lyt_estonia.setOnClickListener(this);
        lyt_lithuania.setOnClickListener(this);
        lyt_russia.setOnClickListener(this);
        cb_english.setOnClickListener(this);
        cb_estonia.setOnClickListener(this);
        cb_russia.setOnClickListener(this);
        cb_lithuania.setOnClickListener(this);
        cb_latvia.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.lyt_english:
                cb_english.setChecked(true);
                cb_estonia.setChecked(false);
                cb_latvia.setChecked(false);
                cb_lithuania.setChecked(false);
                cb_russia.setChecked(false);
                checked="en";
                break;


            case R.id.lyt_latvia:
                cb_english.setChecked(false);
                cb_estonia.setChecked(false);
                cb_latvia.setChecked(true);
                cb_lithuania.setChecked(false);
                cb_russia.setChecked(false);
                checked="lv";
                break;


            case R.id.lyt_estonia:
                cb_english.setChecked(false);
                cb_estonia.setChecked(true);
                cb_latvia.setChecked(false);
                cb_lithuania.setChecked(false);
                cb_russia.setChecked(false);
                checked="et";
                break;

            case R.id.lyt_lithuania:
                cb_english.setChecked(false);
                cb_estonia.setChecked(false);
                cb_latvia.setChecked(false);
                cb_lithuania.setChecked(true);
                cb_russia.setChecked(false);
                checked="lt";
                break;

            case R.id.lyt_russia:
                cb_english.setChecked(false);
                cb_estonia.setChecked(false);
                cb_latvia.setChecked(false);
                cb_lithuania.setChecked(false);
                cb_russia.setChecked(true);
                checked="ru";
                break;

            case R.id.cb_english:
                cb_english.setChecked(true);
                cb_estonia.setChecked(false);
                cb_latvia.setChecked(false);
                cb_lithuania.setChecked(false);
                cb_russia.setChecked(false);
                checked="en";
                break;

            case R.id.cb_estonia:
                cb_english.setChecked(false);
                cb_estonia.setChecked(true);
                cb_latvia.setChecked(false);
                cb_lithuania.setChecked(false);
                cb_russia.setChecked(false);
                checked="et";
                break;


            case R.id.cb_latvia:
                cb_english.setChecked(false);
                cb_estonia.setChecked(false);
                cb_latvia.setChecked(true);
                cb_lithuania.setChecked(false);
                cb_russia.setChecked(false);
                checked="lv";
                break;


            case R.id.cb_lithuania:
                cb_english.setChecked(false);
                cb_estonia.setChecked(false);
                cb_latvia.setChecked(false);
                cb_lithuania.setChecked(true);
                cb_russia.setChecked(false);
                checked="lt";
                break;


            case R.id.cb_russia:
                cb_english.setChecked(false);
                cb_estonia.setChecked(false);
                cb_latvia.setChecked(false);
                cb_lithuania.setChecked(false);
                cb_russia.setChecked(true);
                checked="ru";
                break;

            case R.id.btn_confirm:
                  setLocale(checked);

//                Intent intent=new Intent(this,LoginActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);

        }
    }

    public void setLocale(String lang) {
        Log.e("TAG", "setLocale: "+lang );
        myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();

        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, LoginActivity.class);
        startActivity(refresh);
        finish();
        overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
        BaseApplication.getInstance().getSession().setLanguage(lang);

    }
}
