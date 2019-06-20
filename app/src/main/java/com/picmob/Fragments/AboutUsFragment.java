package com.picmob.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.JsonObject;
import com.picmob.Adapter.AboutUsAdapter;
import com.picmob.AppCustomView.Constant;
import com.picmob.AppCustomView.ProgressDialog;
import com.picmob.AppCustomView.UiHelper;
import com.picmob.Networking.BaseApplication;
import com.picmob.Networking.NetworkController;
import com.picmob.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class AboutUsFragment extends Fragment {
    private static final String TAG = "AboutUs";
//
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    AboutUsAdapter aboutUsAdapter;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menu_filter);
        menu.clear();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_my_orders, container, false);
        ButterKnife.bind(this, view);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
      //  webView.setWebViewClient(new MyBrowser());
        getData();
        return view;
    }

    public void getData() {
        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(getActivity(), false);
        progressDialog.show();
        JsonObject jsonObject=new JsonObject();
        int language_id=1;
        String lang= BaseApplication.getInstance().getSession().getLanguage();
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
        jsonObject.addProperty("language_id",language_id);
        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().getStaticContent(jsonObject);
        new NetworkController().post(getActivity(), call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                if (jsonObject != null) {
                    try {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {



//                        String des=jsonObject1.getString("description");
//                            webView.getSettings().setLoadsImagesAutomatically(true);
//                            webView.getSettings().setJavaScriptEnabled(true);
//                            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//                            webView.loadData(des, "text/html", "UTF-8");
                            final JSONArray jsonArray = jsonObject1.getJSONObject("data").getJSONArray("about_us");
                            String image_url=jsonObject1.getJSONObject("data").getString("image_url");
                            aboutUsAdapter = new AboutUsAdapter(getActivity(), jsonArray,image_url);
                            recyclerview.setAdapter(aboutUsAdapter);


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

}


