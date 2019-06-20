package com.picmob.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonObject;
import com.picmob.Activity.Selected_Photos;
import com.picmob.Adapter.PictureSizeAdapter;
import com.picmob.Adapter.ViewPagerAdapter;
import com.picmob.AppCustomView.Constant;
import com.picmob.AppCustomView.ProgressDialog;
import com.picmob.AppCustomView.UiHelper;
import com.picmob.Networking.BaseApplication;
import com.picmob.Networking.NetworkController;
import com.picmob.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;

public class HomeFragment extends Fragment implements PictureSizeAdapter.ClickItemEvent {

    private static final String TAG = "Home Fragment";
    private static ViewPager mPager;
    private static int currentPage = 0;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    ProgressDialog progressDialog;
    PictureSizeAdapter pictureSizeAdapter;
    Timer swipeTimer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_home, container, false);
        ButterKnife.bind(this, view);
        getPagerImages();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        return view;
    }


    public void getPagerImages() {
        progressDialog = UiHelper.generateProgressDialog(getActivity(), false);
        progressDialog.show();

        int language_id=1;
        String lang=BaseApplication.getInstance().getSession().getLanguage();
        if (lang.equalsIgnoreCase("en"))
            language_id=1;
        else if (lang.equalsIgnoreCase("lv"))
            language_id=2;
        else if (lang.equalsIgnoreCase("et"))
            language_id=3;
        else if (lang.equalsIgnoreCase("lt"))
            language_id=4;
        else if (lang.equalsIgnoreCase("ru"))
            language_id=5;

        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("language_id",language_id);

        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().getPictureSize(BaseApplication.getInstance().getSession().getToken(),String.valueOf(language_id));
        new NetworkController().get(getActivity(), call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                if (jsonObject != null) {
                    try {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {

                            Log.e(TAG, "Success: ");
                            final JSONObject jsonObject2 = jsonObject1.getJSONObject("data");

                            JSONArray jsonArray = jsonObject2.getJSONArray("slider");
                            String slider_url = jsonObject2.getString("slider_url");
                            init(jsonArray, 0, slider_url);

                            JSONArray jsonArray1 = jsonObject2.getJSONArray("picture_sizes");

                            pictureSizeAdapter = new PictureSizeAdapter(getActivity(), jsonArray1, (PictureSizeAdapter.ClickItemEvent) HomeFragment.this);
                            recyclerView.setAdapter(pictureSizeAdapter);
                            pictureSizeAdapter.notifyDataSetChanged();

                        } else {
                            UiHelper.showToast(getActivity(), jsonObject1.getString("message"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void Error(String error) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                UiHelper.showToast(getActivity(), error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    UiHelper.showToast(getActivity(), getResources().getString(R.string.pls_check_your_internet));
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });

    }

    public void init(final JSONArray jsonArray, final int position, String url) {
        Log.e(TAG, "init: " + position);
        mPager = (ViewPager) getActivity().findViewById(R.id.pager);
        mPager.setAdapter(new ViewPagerAdapter(getActivity(), jsonArray, url));
        CircleIndicator indicator = (CircleIndicator) getActivity().findViewById(R.id.indicator);
        indicator.setViewPager(mPager);
        mPager.setCurrentItem(position);

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                mPager.setCurrentItem(currentPage, true);
                if (currentPage == jsonArray.length()) {
                    currentPage = 0;
                }
                else {
                    ++currentPage ;

                }

            }
        };

         swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 1500, 4500);
    }

    @Override
    public void onPause() {
//        swipeTimer.cancel();
  //      swipeTimer.purge();
        super.onPause();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                //  Intent nn = new Intent(getContext(), FilterActivity.class);
                //   startActivityForResult(nn,443);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClickItem(String id, String height, String width, String price, String position,String discount,String quantity) {
        Log.e(TAG, "onClickItem: " + price);
        Intent intent = new Intent(getActivity(), Selected_Photos.class);
        intent.putExtra("height", height);
        intent.putExtra("width", width);
        intent.putExtra("price", price);
        intent.putExtra("picture_size_id", id);
        intent.putExtra("discount", discount);
        intent.putExtra("quantity", quantity);

        Bundle bundle = this.getArguments();
        if (bundle!=null && !bundle.isEmpty()) {
            String newProduct = bundle.getString("newProduct");
            intent.putExtra("newProduct","yes");
            Log.e(TAG, "onClick: " + newProduct);
        }

        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

    }

}
