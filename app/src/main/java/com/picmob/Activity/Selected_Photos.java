package com.picmob.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.picmob.AppCustomView.CustomTextViewBold;
import com.picmob.AppCustomView.CustomTextViewNormal;
import com.picmob.AppCustomView.UiHelper;
import com.picmob.Fragments.FacebookFragment;
import com.picmob.Fragments.InstagramFragment;
import com.picmob.Fragments.MobFragment;
import com.picmob.Networking.BaseApplication;
import com.picmob.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Selected_Photos extends BaseActivity {
    private static final String TAG = "SelectedPhotos";
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvTitle)
    CustomTextViewNormal mTitle;

    @BindView(R.id.txt_counter)
    CustomTextViewBold txt_counter;

    @BindView(R.id.Iv_cart)
    ImageView Iv_cart;

    @BindView(R.id.tv_photos_count)
    CustomTextViewNormal tv_photos_count;


    @BindView(R.id.tabs)
    TabLayout tabLayout;

    ArrayList<String> arrayList;
    String height, width, price, picture_size_id,discount,quantity;

    boolean isNewProduct = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_selected_photos);
        ButterKnife.bind(this);
        setActionBar();
        createTabIcons();

        price = getIntent().getStringExtra("price");
        width = getIntent().getStringExtra("width");
        height = getIntent().getStringExtra("height");
        picture_size_id = getIntent().getStringExtra("picture_size_id");
        discount = getIntent().getStringExtra("discount");
        quantity = getIntent().getStringExtra("quantity");
        Log.e(TAG, "onCreate: " + getIntent().getStringExtra("price"));

        if (getIntent().hasExtra("newProduct")) {

            Log.e(TAG, "newProduct: " + getIntent().getStringExtra("newProduct"));
            if (getIntent().getStringExtra("newProduct").equalsIgnoreCase("yes"))
                isNewProduct = true;
            Log.e(TAG, "isNewProduct: " + isNewProduct);

        }



        Iv_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClick: " );
                Intent intent = new Intent(Selected_Photos.this, YourOrderActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        replaceFragment(new MobFragment());

    }




    @Override
    protected void onResume() {
        super.onResume();

        if (!BaseApplication.getInstance().getSession().getSelectedImages().equalsIgnoreCase("") && !BaseApplication.getInstance().getSession().getSelectedImages().isEmpty()) {
            String aa = BaseApplication.getInstance().getSession().getSelectedImages().toString();
            Log.e(TAG, "sesion : " + aa);
            try {
                JSONArray jsonArray = new JSONArray(aa);
                if (PhotosActivity.productId == "")
                    tv_photos_count.setText(String.valueOf(0));
                else {
                    //Find the pruduct by id
                    int index = Integer.parseInt(PhotosActivity.productId) - 1;
                    String count = String.valueOf(jsonArray.getJSONObject(index).getJSONArray("images_array").length());
                    tv_photos_count.setText(" "+count);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            tv_photos_count.setText(" "+"0");

        }


        String cartData=BaseApplication.getInstance().getSession().getCartData();
        if (!cartData.isEmpty()){
            try {
                JSONArray jsonArray = new JSONArray(cartData);
                if (jsonArray.length()>0)
                    txt_counter.setText(String.valueOf(jsonArray.length()));
                else
                    txt_counter.setVisibility(View.INVISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else
            txt_counter.setVisibility(View.INVISIBLE);


        // replaceFragment(new MobFragment());
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    replaceFragment(new MobFragment());
                    TextView text = (TextView) tab.getCustomView();
                    CustomTextViewNormal tabOne = (CustomTextViewNormal) LayoutInflater.from(Selected_Photos.this).inflate(R.layout.custom_tab, null);
                    Typeface face = Typeface.createFromAsset(getAssets(), "montserrat_semibold.otf");
                    text.setTypeface(face);
                    text.setTextColor(getResources().getColor(R.color.black));
                } else if (tab.getPosition() == 1) {
                    replaceFragment(new FacebookFragment());
                    TextView text = (TextView) tab.getCustomView();
                    CustomTextViewNormal tabOne = (CustomTextViewNormal) LayoutInflater.from(Selected_Photos.this).inflate(R.layout.custom_tab, null);
                    Typeface face = Typeface.createFromAsset(getAssets(), "montserrat_semibold.otf");
                    text.setTypeface(face);
                    text.setTextColor(getResources().getColor(R.color.black));
                } else {
                    replaceFragment(new InstagramFragment());
                    TextView text = (TextView) tab.getCustomView();
                    CustomTextViewNormal tabOne = (CustomTextViewNormal) LayoutInflater.from(Selected_Photos.this).inflate(R.layout.custom_tab, null);
                    Typeface face = Typeface.createFromAsset(getAssets(), "montserrat_semibold.otf");
                    text.setTypeface(face);
                    text.setTextColor(getResources().getColor(R.color.black));
                }
            }

            @SuppressLint("WrongConstant")
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView text = (TextView) tab.getCustomView();
                CustomTextViewNormal tabOne = (CustomTextViewNormal) LayoutInflater.from(Selected_Photos.this).inflate(R.layout.custom_tab, null);
                Typeface face = Typeface.createFromAsset(getAssets(), "montserrat_regular.otf");
                text.setTypeface(face);
                text.setTextColor(getResources().getColor(R.color.gray_text));
            }

            @SuppressLint("WrongConstant")
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
//                TextView text = (TextView) tab.getCustomView();
//                CustomTextViewNormal tabOne = (CustomTextViewNormal) LayoutInflater.from(Selected_Photos.this).inflate(R.layout.custom_tab, null);
//                Typeface face = Typeface.createFromAsset(getAssets(), "montserrat_bold.otf");
//                tabOne.setTypeface(face);
            }
        });

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.filter_menu, menu);
//        return true;
//    }

    private void replaceFragment(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString("price", getIntent().getStringExtra("price"));
        bundle.putString("width", getIntent().getStringExtra("width"));
        bundle.putString("height", getIntent().getStringExtra("height"));
        bundle.putString("discount", getIntent().getStringExtra("discount"));
        bundle.putString("quantity", getIntent().getStringExtra("quantity"));
        bundle.putBoolean("newProduct", isNewProduct);
        Log.e(TAG, "replaceFragment: " + isNewProduct);
        bundle.putString("picture_size_id", getIntent().getStringExtra("picture_size_id"));
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.framelayout, fragment);
        transaction.commit();
    }

    private void setActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_icon_drawable);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mTitle.setText(getResources().getString(R.string.selected_photos));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    private void createTabIcons() {
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.string_mob)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.string_facebook)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.string_instagram)));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        CustomTextViewNormal tabOne = (CustomTextViewNormal) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText(getResources().getString(R.string.string_mob));
        Typeface face = Typeface.createFromAsset(getAssets(), "montserrat_semibold.otf");
        tabOne.setTypeface(face);
        tabOne.setTextColor(getResources().getColor(R.color.black));
        //tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_dash26, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);


        CustomTextViewNormal tabTwo = (CustomTextViewNormal) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText(getResources().getString(R.string.string_facebook));
        Typeface face1 = Typeface.createFromAsset(getAssets(), "montserrat_regular.otf");
        tabTwo.setTypeface(face1);
        //tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_category, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        CustomTextViewNormal tabThree = (CustomTextViewNormal) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setText(getResources().getString(R.string.string_instagram));
        Typeface face2 = Typeface.createFromAsset(getAssets(), "montserrat_regular.otf");
        tabThree.setTypeface(face2);
        //tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_order, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);

    }

    public void setCount(int count) {
        int cc = 0;
        int dd = cc + count;
        tv_photos_count.setText(String.valueOf(dd));
    }

    public void setCountPlus() {
        int newcount = Integer.parseInt(tv_photos_count.getText().toString().trim()) + 1;
        tv_photos_count.setText(String.valueOf(newcount));
    }


    public void setCountMinus() {
        int newcount = Integer.parseInt(tv_photos_count.getText().toString().trim()) - 1;
        tv_photos_count.setText(String.valueOf(newcount));
    }




//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.e(TAG, "onActivityResult: " );
//        Fragment fragment=getSupportFragmentManager().findFragmentById(R.id.framelayout);
//        fragment.onActivityResult(requestCode, resultCode, data);
//    }
}
