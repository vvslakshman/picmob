package com.picmob.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.JsonObject;
import com.picmob.Activity.PaymentDetailActivity;
import com.picmob.Adapter.MyOrderAdapter;
import com.picmob.AppCustomView.Constant;
import com.picmob.AppCustomView.ProgressDialog;
import com.picmob.AppCustomView.UiHelper;
import com.picmob.Networking.BaseApplication;
import com.picmob.Networking.NetworkController;
import com.picmob.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class MyCardsFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.btn_next)
    Button btn_next;

    @BindView(R.id.editCardno)
    EditText editCardno;

    @BindView(R.id.editCardname)
    EditText editCardname;

    @BindView(R.id.editCarddate)
    EditText editCarddate;

    @BindView(R.id.editCardcvv)
    EditText editCardcvv;

    @BindView(R.id.layout_visa)
    LinearLayout layout_visa;

    @BindView(R.id.layout_master)
    LinearLayout layout_master;

    @BindView(R.id.layout_maestro)
    LinearLayout layout_maestro;

    int card_type_id = 1;
    String card_id="";
    JSONArray dataArray=new JSONArray();
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
        View view = inflater.inflate(R.layout.layout_my_cards, container, false);
        ButterKnife.bind(this, view);

        callApiForData();
        layout_visa.setOnClickListener(this);
        layout_maestro.setOnClickListener(this);
        layout_master.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        editCarddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();

                int y = c.get(Calendar.YEAR);
                int m = c.get(Calendar.MONTH);
                int d = c.get(Calendar.DAY_OF_MONTH);


                final String[] MONTH = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

                DatePickerDialog dp = new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_DARK,
                        new DatePickerDialog.OnDateSetListener() {


                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String erg = "";
                                //    erg = String.valueOf(dayOfMonth);
                                erg += String.valueOf(monthOfYear + 1);
                                erg += "/" + year;

                                editCarddate.setText(erg);
                                view.findViewById(getResources().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
                            }

                        }, y, m, d);

//                dp.setTitle("Calender");
//                dp.setMessage("Select Your Expiry Date");
                ((ViewGroup) dp.getDatePicker()).findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
                dp.show();


            }

        });

        return view;
    }


    public boolean validate() {
        boolean valid = true;
        String cardno = editCardno.getText().toString().trim();
        String name = editCardname.getText().toString().trim();
        String date = editCarddate.getText().toString().trim();
        String cvv = editCardcvv.getText().toString().trim();
        //   String password = mEditPass.getText().toString().trim();
        if (cardno.isEmpty()) {
            editCardno.setError(getString(R.string.pls_enter_cardno));
            valid = false;
        } else {
            editCardno.setError(null);
        }

        if (name.isEmpty()) {
            editCardname.setError(getString(R.string.pls_enter_name));
            valid = false;
        } else
            editCardname.setError(null);

        if (date.isEmpty()) {
            editCarddate.setError(getString(R.string.pls_enter_date));
            valid = false;
        } else {
            editCarddate.setError(null);
        }

        if (cvv.isEmpty()) {
            editCardcvv.setError(getString(R.string.pls_enter_cvv));
            valid = false;
        } else {
            editCardcvv.setError(null);
        }

        return valid;
    }

    public void callApiForData() {
        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(getActivity(), false);
        progressDialog.show();

        JsonObject jsonObject=new JsonObject();
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
        jsonObject.addProperty("language_id",language_id);
        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().my_card(BaseApplication.getInstance().getSession().getToken(),String.valueOf(language_id));
        new NetworkController().get(getActivity(), call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                if (jsonObject != null) {
                    try {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {


                             dataArray=jsonObject1.getJSONArray("data");
                            String cardno="",name="",date="";
                            if (dataArray.length()==0)
                                UiHelper.showToast(getActivity(),"No Saved Card Found!");
                            else
                                cardno=dataArray.getJSONObject(0).getString("card_number");
                            name=dataArray.getJSONObject(0).getString("card_holder_name");
                            date=dataArray.getJSONObject(0).getString("expiry_date");
                             card_id=dataArray.getJSONObject(0).getString("id");
                            editCardno.setText(cardno);
                            editCardname.setText(name);
                            editCarddate.setText(date);

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



    @SuppressLint("NewApi")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_visa:
                layout_visa.setBackground(getResources().getDrawable(R.drawable.custom_border_curverd_pink));
                layout_master.setBackground(getResources().getDrawable(R.drawable.edittext_border));
                layout_maestro.setBackground(getResources().getDrawable(R.drawable.edittext_border));
                card_type_id = 1;
                break;

            case R.id.layout_master:
                layout_visa.setBackground(getResources().getDrawable(R.drawable.edittext_border));
                layout_master.setBackground(getResources().getDrawable(R.drawable.custom_border_curverd_pink));
                layout_maestro.setBackground(getResources().getDrawable(R.drawable.edittext_border));
                card_type_id = 2;
                break;

            case R.id.layout_maestro:
                layout_visa.setBackground(getResources().getDrawable(R.drawable.edittext_border));
                layout_master.setBackground(getResources().getDrawable(R.drawable.edittext_border));
                layout_maestro.setBackground(getResources().getDrawable(R.drawable.custom_border_curverd_pink));
                card_type_id = 3;
                break;

            case R.id.btn_next:
                if (!validate())
                    return;
                callApi();
                break;

        }
    }

    public void callApi() {
        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(getActivity(), false);
        progressDialog.show();
        JsonObject jsonObject = new JsonObject();
        if (dataArray.length()>0)
            jsonObject.addProperty("id",card_id);
        jsonObject.addProperty("card_type_id", card_type_id);
        jsonObject.addProperty("card_number", editCardno.getText().toString());
        jsonObject.addProperty("card_holder_name", editCardname.getText().toString());
        jsonObject.addProperty("expiry_date", editCarddate.getText().toString());
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
        jsonObject.addProperty("language_id",language_id);
        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().add_card(BaseApplication.getInstance().getSession().getToken(), jsonObject);
        new NetworkController().post(getActivity(), call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                if (jsonObject != null) {
                    try {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {

                            UiHelper.showToast(getActivity(), jsonObject1.getString("message"));
//                            editCardno.setText("");
//                            editCardname.setText("");
//                            editCarddate.setText("");
//                            editCardcvv.setText("");


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

