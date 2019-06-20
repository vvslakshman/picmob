package com.picmob.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.JsonObject;
import com.picmob.AppCustomView.Constant;
import com.picmob.AppCustomView.CustomTextViewBold;
import com.picmob.AppCustomView.CustomTextViewNormal;
import com.picmob.AppCustomView.ProgressDialog;
import com.picmob.AppCustomView.UiHelper;
import com.picmob.Fragments.AboutUsFragment;
import com.picmob.Fragments.ContactsFragment;
import com.picmob.Fragments.HomeFragment;
import com.picmob.Fragments.InfoHelpFragment;
import com.picmob.Fragments.MyOrdersFragment;
import com.picmob.Fragments.SettingsFragment;
import com.picmob.Networking.BaseApplication;
import com.picmob.Networking.NetworkController;
import com.picmob.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.toolbar1)
    Toolbar toolbar1;

    @BindView(R.id.tvTitle)
    CustomTextViewBold mTitle;

    @BindView(R.id.rlt_toolbar)
    RelativeLayout rlt_toolbar;

    @BindView(R.id.menu_icon)
    ImageView menu_icon_image;

    @BindView(R.id.tool)
    CoordinatorLayout mSnackView;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.layout_logout)
    LinearLayout layout_logout;

    @BindView(R.id.txt_counter)
    CustomTextViewBold txt_counter;

    @BindView(R.id.Iv_cart)
    ImageView Iv_cart;

    @BindView(R.id.lyt_language)
    LinearLayout lyt_language;

    @BindView(R.id.txt_language)
    CustomTextViewNormal txt_language;

    public static int navItemIndex = 0;
    public static String CURRENT_TAG = "";

    @BindView(R.id.language_flag)
    ImageView language_flag;

    @BindView(R.id.tv_logout)
    CustomTextViewNormal tv_logout;


    private String[] activityTitles;
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    private boolean isLogout = false;
    Locale myLocale;
    String checked = "";
    String selectedLanguage = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar1);
        mHandler = new Handler();
        activityTitles = getResources().getStringArray(R.array.ld_activityScreenTitles);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);
//            mi.setActionView(R.layout.menu_image);
//            //for aapplying a font to subMenu ...
//            SubMenu subMenu = mi.getSubMenu();
//            if (subMenu!=null && subMenu.size() >0 ) {
//                for (int j=0; j <subMenu.size();j++) {
//                    MenuItem subMenuItem = subMenu.getItem(j);
//                    UiHelper.applyFontToMenuItem(this,subMenuItem);
//                }
//            }
            //the method we have create in activity
            UiHelper.applyFontToMenuItem(this, mi);
        }
        navItemIndex = 0;
        setUpNavigationView();
        //setNavigationHeader();
        loadHomeFragment();

//        if (getIntent().hasExtra("Account")){
//            navItemIndex=0;
//            setUpNavigationView();
//            setNavigationHeader();
//            loadHomeFragment();}

//        ScrollView scroller = new ScrollView(this);
//        scroller.addView(tv_logout);
//        tv_logout.setText(getResources().getString(R.string.log_out));
//        tv_logout.setMovementMethod(new ScrollingMovementMethod());

        layout_logout.setOnClickListener(this);
        lyt_language.setOnClickListener(this);
        menu_icon_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawers();
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    return;
                } else

                    drawer.openDrawer(GravityCompat.START);
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
            }
        });
        toolbar1.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("onclick", "setnavigationonclick main activity");
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawers();
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    return;
                } else

                    drawer.openDrawer(GravityCompat.START);
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
            }
        });

        Iv_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, YourOrderActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        String cartData = BaseApplication.getInstance().getSession().getCartData();
        if (!cartData.isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(cartData);
                if (jsonArray.length() > 0)
                    txt_counter.setText(String.valueOf(jsonArray.length()));
                else
                    txt_counter.setVisibility(View.INVISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else
            txt_counter.setVisibility(View.INVISIBLE);

        selectedLanguage = BaseApplication.getInstance().getSession().getLanguage();
        if (!selectedLanguage.isEmpty() && !selectedLanguage.equalsIgnoreCase("")) {

            if (selectedLanguage.equalsIgnoreCase("en")) {
                txt_language.setText("English");
                language_flag.setImageResource(R.drawable.flag_english);

            } else if (selectedLanguage.equalsIgnoreCase("lv")) {
                txt_language.setText("Latvia");
                language_flag.setImageResource(R.drawable.flag_latvia);
            } else if (selectedLanguage.equalsIgnoreCase("lt")) {
                txt_language.setText("Lithuania");
                language_flag.setImageResource(R.drawable.flag_lithuania);
            } else if (selectedLanguage.equalsIgnoreCase("ru")) {
                txt_language.setText("Russia");
                language_flag.setImageResource(R.drawable.flag_russia);
            } else if (selectedLanguage.equalsIgnoreCase("et")) {
                txt_language.setText("Estonia");
                language_flag.setImageResource(R.drawable.flag_estonia);
            }
        } else {

            txt_language.setText("English");
            language_flag.setImageResource(R.drawable.flag_english);
        }

    }

    private void loadHomeFragment() {
        selectNavMenu();
        setToolbarTitle();
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments

                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                if (getIntent().hasExtra("newProduct")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("newProduct", "yes");
                    fragment.setArguments(bundle);
                }
                fragmentTransaction.commitAllowingStateLoss();
            }
        };
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }
        //Closing drawer on item click
        drawer.closeDrawers();
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private void setToolbarTitle() {
        if (navItemIndex == 0) {
            toolbar1.setVisibility(View.GONE);
            rlt_toolbar.setVisibility(View.VISIBLE);
            isLogout = false;
            CURRENT_TAG = Constant.POS_HOME;
            Log.e("TAG", "setToolbarTitle: ");
            mTitle.setText(getString(R.string.picmob));
            //     getSupportActionBar().setTitle(getString(R.string.picmob));
            return;
        }
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            navigationView.getMenu().getItem(i).setActionView(R.layout.menu_image);
        }
        navigationView.getMenu().getItem(navItemIndex).setActionView(R.layout.menu_image_selected);

    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Log.e("TAG", "onNavigationItemSelected: " + menuItem.getItemId());
                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {

                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.menu_pos_home:
                        toolbar1.setVisibility(View.GONE);
                        rlt_toolbar.setVisibility(View.VISIBLE);
                        isLogout = false;
                        navItemIndex = 0;
                        CURRENT_TAG = Constant.POS_HOME;
                        mTitle.setText(getString(R.string.picmob));
                        //  getSupportActionBar().setTitle(getString(R.string.picmob));
                        break;
                    case R.id.menu_pos_myorders:
                        rlt_toolbar.setVisibility(View.GONE);
                        toolbar1.setVisibility(View.VISIBLE);
                        isLogout = false;
                        navItemIndex = 1;
                        CURRENT_TAG = Constant.POS_MYORDERS;
                        getSupportActionBar().setTitle(getString(R.string.myorders));
                        break;
//                    case R.id.menu_pos_mycards:
//                        rlt_toolbar.setVisibility(View.GONE);
//                        toolbar1.setVisibility(View.VISIBLE);
//                        isLogout=false;
//                        navItemIndex = 2;
//                        CURRENT_TAG = Constant.POS_MYCARDS;
//                        getSupportActionBar().setTitle(getString(R.string.mycards));
//                        break;
                    case R.id.menu_pos_settings:
                        rlt_toolbar.setVisibility(View.GONE);
                        toolbar1.setVisibility(View.VISIBLE);
                        isLogout = false;
                        navItemIndex = 2;
                        CURRENT_TAG = Constant.POS_SETTINGS;
                        getSupportActionBar().setTitle(getString(R.string.account));
                        break;
                    case R.id.menu_pos_info:
                        rlt_toolbar.setVisibility(View.GONE);
                        toolbar1.setVisibility(View.VISIBLE);
                        isLogout = false;
                        navItemIndex = 3;
                        CURRENT_TAG = Constant.POS_SETTINGS;
                        getSupportActionBar().setTitle(getString(R.string.info_help));
                        break;

                    case R.id.menu_pos_contacts:
                        rlt_toolbar.setVisibility(View.GONE);
                        toolbar1.setVisibility(View.VISIBLE);
                        isLogout = false;
                        navItemIndex = 4;
                        CURRENT_TAG = Constant.POS_SETTINGS;
                        getSupportActionBar().setTitle(getString(R.string.contacts));
                        break;

                    case R.id.menu_pos_aboutus:
                        rlt_toolbar.setVisibility(View.GONE);
                        toolbar1.setVisibility(View.VISIBLE);
                        isLogout = false;
                        navItemIndex = 5;
                        CURRENT_TAG = Constant.POS_ABOUTUS;
                        getSupportActionBar().setTitle(getString(R.string.about_us));
                        break;


//                    case R.id.menu_pos_logout:
//                        isLogout=true;
//                        logout();
//                        break;
                    default:
                        navItemIndex = 0;

                }
                if (!isLogout) {
                    Log.e("TAG", "ISLOGOUT TRUE: ");
                    loadHomeFragment();
                }

                return true;
            }
        });
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar1, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager) MainActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager) MainActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);
            }
        };

        //Setting the actionbarToggle to drawer layout

        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.menuicon_drawable, this.getTheme());
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);

        actionBarDrawerToggle.setHomeAsUpIndicator(drawable);
        drawer.setDrawerListener(actionBarDrawerToggle);


        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e("tag", "onResume: ");
        setNavigationHeader();

    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {

            case 0:
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                MyOrdersFragment myOrdersFragment = new MyOrdersFragment();
                return myOrdersFragment;
//            case 2:
//                MyCardsFragment myCardsFragment = new MyCardsFragment();
//                return myCardsFragment;

            case 2:
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;

            case 3:
                InfoHelpFragment infoHelpFragment = new InfoHelpFragment();
                return infoHelpFragment;

            case 4:
                ContactsFragment contactsFragment = new ContactsFragment();
                return contactsFragment;

            case 5:
                AboutUsFragment aboutUsFragment = new AboutUsFragment();
                return aboutUsFragment;

            default:
                return new HomeFragment();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter_menu, menu);
        return true;
    }

    public void setNavigationHeader() {
        View view = navigationView.getHeaderView(0);
        Log.e("set navigation", "setNavigationHeader: ");
        try {
            JSONObject jsonObject = new JSONObject(BaseApplication.getInstance().getSession().getProfileData().toString());
            CustomTextViewBold name = (CustomTextViewBold) view.findViewById(R.id.name);
            name.setText(getString(R.string.hello) + " " + jsonObject.getString("first_name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = Constant.POS_HOME;
                loadHomeFragment();
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_logout:
                logout();
                break;

            case R.id.lyt_language:

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.dialog_language);


                LinearLayout lyt_english = (LinearLayout) dialog.findViewById(R.id.lyt_english);
                LinearLayout lyt_latvia = (LinearLayout) dialog.findViewById(R.id.lyt_latvia);
                LinearLayout lyt_estonia = (LinearLayout) dialog.findViewById(R.id.lyt_estonia);
                LinearLayout lyt_lithuania = (LinearLayout) dialog.findViewById(R.id.lyt_lithuania);
                LinearLayout lyt_russia = (LinearLayout) dialog.findViewById(R.id.lyt_russia);
                final CheckBox cb_russia = (CheckBox) dialog.findViewById(R.id.cb_russia);
                final CheckBox cb_lithuania = (CheckBox) dialog.findViewById(R.id.cb_lithuania);
                final CheckBox cb_estonia = (CheckBox) dialog.findViewById(R.id.cb_estonia);
                final CheckBox cb_latvia = (CheckBox) dialog.findViewById(R.id.cb_latvia);
                final CheckBox cb_english = (CheckBox) dialog.findViewById(R.id.cb_english);
                Button btn_confirm = (Button) dialog.findViewById(R.id.btn_confirm);


                if (!selectedLanguage.isEmpty() && !selectedLanguage.equalsIgnoreCase("")) {

                    if (selectedLanguage.equalsIgnoreCase("en")) {
                        cb_english.setChecked(true);
                    } else if (selectedLanguage.equalsIgnoreCase("lv")) {
                        cb_latvia.setChecked(true);
                    } else if (selectedLanguage.equalsIgnoreCase("lt")) {
                        cb_lithuania.setChecked(true);
                    } else if (selectedLanguage.equalsIgnoreCase("ru")) {
                        cb_russia.setChecked(true);
                    } else if (selectedLanguage.equalsIgnoreCase("et")) {
                        cb_estonia.setChecked(true);
                    }
                } else
                    cb_english.setChecked(true);


                lyt_english.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cb_english.setChecked(true);
                        cb_estonia.setChecked(false);
                        cb_latvia.setChecked(false);
                        cb_lithuania.setChecked(false);
                        cb_russia.setChecked(false);
                        checked = "en";
                        selectedLanguage = "English";
                    }
                });

                lyt_latvia.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cb_english.setChecked(false);
                        cb_estonia.setChecked(false);
                        cb_latvia.setChecked(true);
                        cb_lithuania.setChecked(false);
                        cb_russia.setChecked(false);
                        checked = "lv";
                        selectedLanguage = "Latvia";
                    }
                });

                lyt_estonia.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cb_english.setChecked(false);
                        cb_estonia.setChecked(true);
                        cb_latvia.setChecked(false);
                        cb_lithuania.setChecked(false);
                        cb_russia.setChecked(false);
                        checked = "et";
                        selectedLanguage = "Estonia";
                    }
                });

                lyt_lithuania.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cb_english.setChecked(false);
                        cb_estonia.setChecked(false);
                        cb_latvia.setChecked(false);
                        cb_lithuania.setChecked(true);
                        cb_russia.setChecked(false);
                        checked = "lt";
                        selectedLanguage = "Lithuania";
                    }
                });

                lyt_russia.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cb_english.setChecked(false);
                        cb_estonia.setChecked(false);
                        cb_latvia.setChecked(false);
                        cb_lithuania.setChecked(false);
                        cb_russia.setChecked(true);
                        checked = "ru";
                        selectedLanguage = "Russia";
                    }
                });

                cb_english.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cb_english.setChecked(true);
                        cb_estonia.setChecked(false);
                        cb_latvia.setChecked(false);
                        cb_lithuania.setChecked(false);
                        cb_russia.setChecked(false);
                        checked = "en";
                        selectedLanguage = "English";
                    }
                });

                cb_estonia.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cb_english.setChecked(false);
                        cb_estonia.setChecked(true);
                        cb_latvia.setChecked(false);
                        cb_lithuania.setChecked(false);
                        cb_russia.setChecked(false);
                        checked = "et";
                        selectedLanguage = "Estonia";
                    }
                });

                cb_latvia.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cb_english.setChecked(false);
                        cb_estonia.setChecked(false);
                        cb_latvia.setChecked(true);
                        cb_lithuania.setChecked(false);
                        cb_russia.setChecked(false);
                        checked = "lv";
                        selectedLanguage = "Latvia";
                    }
                });

                cb_lithuania.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cb_english.setChecked(false);
                        cb_estonia.setChecked(false);
                        cb_latvia.setChecked(false);
                        cb_lithuania.setChecked(true);
                        cb_russia.setChecked(false);
                        checked = "lt";
                        selectedLanguage = "Lithuania";
                    }
                });

                cb_russia.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cb_english.setChecked(false);
                        cb_estonia.setChecked(false);
                        cb_latvia.setChecked(false);
                        cb_lithuania.setChecked(false);
                        cb_russia.setChecked(true);
                        checked = "ru";
                        selectedLanguage = "Russia";
                    }
                });

                btn_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setLocale(checked);
                        dialog.dismiss();
                        txt_language.setText(selectedLanguage);

                    }
                });


                dialog.show();

                break;


        }
    }

    public void setLocale(String lang) {
        Log.e("TAG", "setLocale: " + lang);
        myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();

        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent intent = getIntent();
        finish();
        startActivity(intent);
        BaseApplication.getInstance().getSession().setLanguage(lang);

    }

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(R.string.Are_u_sure_to_logout);
        builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                callToLogout(dialog);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void callToLogout(final DialogInterface dialogInterface) {
        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();


        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().logout(BaseApplication.getInstance().getSession().getToken());
        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                if (jsonObject != null) {
                    progressDialog.dismiss();
                    try {
                        dialogInterface.dismiss();
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {
                            BaseApplication.getInstance().getSession().clearSession();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                        } else {
                            UiHelper.showToast(MainActivity.this, jsonObject1.getString("message"));
                            Log.e("else me", "Success: ");
                            if (jsonObject1.getString("error_code").equalsIgnoreCase("delete_user")) {
                                Log.e("if me", "Success: ");
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);

                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                    Log.e("TAG", "Success else: ");
            }

            @Override
            public void Error(String error) {
                Log.e("Tag", "error : " + error);

                if (progressDialog != null)
                    progressDialog.dismiss();
                UiHelper.showToast(MainActivity.this, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    UiHelper.showNetworkError(MainActivity.this, mSnackView);
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });

    }

}
