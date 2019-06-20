package com.picmob.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.picmob.AppCustomView.CustomTextViewBold;
import com.picmob.AppCustomView.UiHelper;
import com.picmob.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeliveryOptionActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvTitle)
    CustomTextViewBold mTitle;

    @BindView(R.id.Iv_cart)
    ImageView Iv_cart;

    @BindView(R.id.txt_counter)
    CustomTextViewBold txt_counter;

    @BindView(R.id.lyt_parcel)
    LinearLayout lyt_parcel;

    @BindView(R.id.lyt_courier)
    LinearLayout lyt_courier;

    @BindView(R.id.lyt_pasts)
    LinearLayout lyt_pasts;

    @BindView(R.id.btn_next)
    Button btn_next;

    String option = "Parcel";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_delivery_option);
        ButterKnife.bind(this);
        setActionBar();
    }

    private void setActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_icon_drawable);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mTitle.setText(R.string.delivery);
        Iv_cart.setVisibility(View.GONE);
        txt_counter.setVisibility(View.GONE);
        lyt_pasts.setOnClickListener(this);
        lyt_courier.setOnClickListener(this);
        lyt_parcel.setOnClickListener(this);
        btn_next.setOnClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                UiHelper.hideSoftKeyboard1(DeliveryOptionActivity.this);
                //  onBackPressed();
                Intent intent = new Intent(DeliveryOptionActivity.this, YourOrderActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NewApi")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.lyt_parcel:
                option = "Parcel";
                lyt_parcel.setBackground(getResources().getDrawable(R.drawable.custom_border_curverd_pink));
                lyt_courier.setBackground(getResources().getDrawable(R.drawable.edittext_border));
                lyt_pasts.setBackground(getResources().getDrawable(R.drawable.edittext_border));
                break;

            case R.id.lyt_courier:
                option = "Courier";
                lyt_courier.setBackground(getResources().getDrawable(R.drawable.custom_border_curverd_pink));
                lyt_parcel.setBackground(getResources().getDrawable(R.drawable.edittext_border));
                lyt_pasts.setBackground(getResources().getDrawable(R.drawable.edittext_border));
                break;

            case R.id.lyt_pasts:
                option = "Pasts";
                lyt_pasts.setBackground(getResources().getDrawable(R.drawable.custom_border_curverd_pink));
                lyt_courier.setBackground(getResources().getDrawable(R.drawable.edittext_border));
                lyt_parcel.setBackground(getResources().getDrawable(R.drawable.edittext_border));

                break;

            case R.id.btn_next:
                Intent intent = new Intent(this, DeleiveryActivity.class);
                intent.putExtra("total_amount", getIntent().getStringExtra("total_amount"));
                intent.putExtra("option", option);
//                intent.putExtra("width",getIntent().getStringExtra("width"));
//                intent.putExtra("height",getIntent().getStringExtra("height"));
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        UiHelper.hideSoftKeyboard1(DeliveryOptionActivity.this);
        Intent intent = new Intent(DeliveryOptionActivity.this, YourOrderActivity.class);
//        intent.putExtra("width",getIntent().getStringExtra("width"));
//        intent.putExtra("height",getIntent().getStringExtra("height"));
        startActivity(intent);
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}
