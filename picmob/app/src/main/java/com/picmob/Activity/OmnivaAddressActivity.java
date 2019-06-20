package com.picmob.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.picmob.Adapter.OmnivaAddressAdapter;
import com.picmob.Adapter.PlaceHistoryAdapter;
import com.picmob.AppCustomView.Constant;
import com.picmob.AppCustomView.ProgressDialog;
import com.picmob.AppCustomView.UiHelper;
import com.picmob.LocalStorage.DatabaseHandler;
import com.picmob.Models.AddressModel;
import com.picmob.Models.OmnivaAddressModel;
import com.picmob.Networking.BaseApplication;
import com.picmob.Networking.NetworkController;
import com.picmob.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class OmnivaAddressActivity extends BaseActivity implements PlaceHistoryAdapter.OnSelectAddress, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

//    @BindView(R.id.edt_search)
//    EditText edt_search;

    private static final String TAG = "CommonSearchActivity";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyD2h4Q1l4Q2ENW-l5ouOEW1uRFbZuAtP9c";
    @BindView(R.id.recycleview)
    RecyclerView recycleview;
    @BindView(R.id.btn_ok)
    Button btn_ok;
    @BindView(R.id.search_box)
    AutoCompleteTextView search_box;
    @BindView(R.id.placeRecycleview)
    RecyclerView placeRecycleview;
    OmnivaAddressAdapter omnivaAddressAdapter;
    OmnivaAddressModel omnivaAddressModel;
    ArrayList<OmnivaAddressModel> omnivaAddressModelArrayList = new ArrayList<>();
    String text;
    long delay = 1500;
    long last_text_edit = 0;
    Handler handler = new Handler();
    ArrayList<AddressModel> resultList = null;
    PlaceHistoryAdapter placeHistoryAdapter;
    String latitude, longitude, address, locationId;
    private Runnable input_finish_checker;
    private ArrayList<AddressModel> addressList = new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;
    private String locLatLng;

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            final Place myPlace = places.get(0);
            LatLng latlangObj = myPlace.getLatLng();
            latitude = String.valueOf(latlangObj.latitude);
            longitude = String.valueOf(latlangObj.longitude);
            Log.e("latitude:", "" + latitude);
            Log.v("longitude:", "" + longitude);
            new DownloadFilesTask().execute();
            recycleview.setVisibility(View.VISIBLE);
            placeRecycleview.setVisibility(View.GONE);
            Log.e(TAG, "Get Address: " + myPlace.getAddress());
            callApi(String.valueOf(myPlace.getAddress()));
//            Intent intent = new Intent();
//            intent.putExtra("text", address);
//            intent.putExtra("latitude", latitude);
//            intent.putExtra("longitude", longitude);
//            setResult(Activity.RESULT_OK, intent);
            //  CommonSearchActivity.this.finish();
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_omniva_address);
        ButterKnife.bind(this);


        placeRecycleview.setLayoutManager(new LinearLayoutManager(this));
        search_box.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));
        search_box.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UiHelper.hideSoftKeyboard1(OmnivaAddressActivity.this);
                AddressModel addressModel = resultList.get(position);
                address = addressModel.getAddress();
                locationId = addressModel.getLocationId();
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, locationId);
                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            }
        });

        recycleview.setVisibility(View.GONE);
        placeRecycleview.setVisibility(View.VISIBLE);

        mGoogleApiClient = new GoogleApiClient.Builder(this).
                addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();


//        input_finish_checker = new Runnable() {
//            public void run() {
//                if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
//                    text=edt_search.getText().toString();
//                    if (!text.equals("")){
//                        callApi(text);
//                    }
//
//                }
//            }
//        };
//
//        edt_search.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                handler.removeCallbacks(input_finish_checker);
//            }
//            @Override
//            public void afterTextChanged(final Editable editable) {
//                if (editable.length() > 0) {
//                    last_text_edit = System.currentTimeMillis();
//                    handler.postDelayed(input_finish_checker, delay);
//                } else {
//                    if (omnivaAddressAdapter!=null) {
//                        omnivaAddressAdapter.omnivaAddressModelArrayList.clear();
//                        omnivaAddressAdapter.notifyDataSetChanged();
//                    }
//                }
//
//            }
//        });


        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();

                if (omnivaAddressAdapter != null) {
                    for (int i = 0; i < omnivaAddressModelArrayList.size(); i++) {
                        if (omnivaAddressModelArrayList.get(i).isChecked) {
                            Log.e("TAG", "onClick: " + omnivaAddressModelArrayList.get(i).country_name);
                            intent.putExtra("country_name", omnivaAddressModelArrayList.get(i).country_name);
                            intent.putExtra("id", omnivaAddressModelArrayList.get(i).id);
                            intent.putExtra("latitude", omnivaAddressModelArrayList.get(i).latitude);
                            intent.putExtra("longitude", omnivaAddressModelArrayList.get(i).longitude);

                        }
                    }

                    setResult(RESULT_OK, intent);
                }
                finish();
            }
        });

    }


    private void callApi(String text) {
        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("address", text);
        jsonObject.addProperty("lat", latitude);
        jsonObject.addProperty("long", longitude);
        jsonObject.addProperty("country", getIntent().getStringExtra("selected_country"));

        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().get_omniva_address(jsonObject);
        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                if (jsonObject != null) {
                    try {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {


                            //   JSONObject jsonObject2=jsonObject1.getJSONObject("data");


                            JSONArray jsonArray = jsonObject1.getJSONArray("data");
                            omnivaAddressModel = new OmnivaAddressModel();
                            omnivaAddressModel.initializeModel(jsonArray);
                            omnivaAddressModelArrayList = omnivaAddressModel.europeanCountryModelArrayList;


                            if (getIntent().hasExtra("country_id")) {
                                for (int i = 0; i < omnivaAddressModelArrayList.size(); i++) {

                                    if (getIntent().getStringExtra("country_id").equalsIgnoreCase(String.valueOf(omnivaAddressModelArrayList.get(i).id))) {
                                        omnivaAddressModelArrayList.get(i).isChecked = true;
                                        Log.e("TAG", "NAME: " + omnivaAddressModelArrayList.get(i).country_name);
                                    }


                                }
                            }

                            omnivaAddressAdapter = new OmnivaAddressAdapter(omnivaAddressModelArrayList, OmnivaAddressActivity.this);
                            recycleview.setLayoutManager(new LinearLayoutManager(OmnivaAddressActivity.this));
                            recycleview.setAdapter(omnivaAddressAdapter);


                            if (jsonObject1.getJSONArray("data").length() < 1) {
                                UiHelper.showToast(OmnivaAddressActivity.this, jsonObject1.getString("message"));
                                omnivaAddressAdapter.omnivaAddressModelArrayList.clear();
                                omnivaAddressAdapter.notifyDataSetChanged();
                            }

                        } else if (jsonObject1.getString("status").equalsIgnoreCase("error")) {

                            //   if (jsonObject1.getString("message").equalsIgnoreCase("No suggestion found.")) {
                            UiHelper.showToast(OmnivaAddressActivity.this, jsonObject1.getString("message"));
                            if (omnivaAddressAdapter != null) {
                                omnivaAddressAdapter.omnivaAddressModelArrayList.clear();
                                omnivaAddressAdapter.notifyDataSetChanged();
                            }
                            // }

                        } else {
                            UiHelper.showToast(OmnivaAddressActivity.this, jsonObject1.getString("message"));
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
                UiHelper.showToast(OmnivaAddressActivity.this, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    UiHelper.showToast(OmnivaAddressActivity.this, "No Internet");
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void selectAddress(AddressModel address) {
//        Intent intent = new Intent();
//        intent.putExtra("text", address.getAddress());
        //intent.putExtra("latitude", address.getLatitude());
        //intent.putExtra("longitude", address.getLongitude());
        //setResult(Activity.RESULT_OK, intent);
        //  CommonSearchActivity.this.finish();

        latitude = address.getLatitude();
        longitude = address.getLongitude();
        recycleview.setVisibility(View.VISIBLE);
        placeRecycleview.setVisibility(View.GONE);
        callApi(address.getAddress());
        Log.e(TAG, "selectAddress: " + address.getLatitude());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        notifyAdapter();
    }

    @Override
    protected void onPause() {
        super.onPause();
        UiHelper.hideSoftKeyboard1(this);
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void notifyAdapter() {
        final DatabaseHandler db = new DatabaseHandler(this);
        List<AddressModel> addressModels = db.getAllAddress();
        for (AddressModel cn : addressModels) {
            AddressModel addressModel = new AddressModel();
            addressModel.setAddress(cn.getAddress());
            addressModel.setLatitude(cn.getLatitude());
            addressModel.setLongitude(cn.getLongitude());
            addressModel.setLocationId(cn.getLocationId());
            ;
            addressList.add(addressModel);
        }
        placeHistoryAdapter = new PlaceHistoryAdapter(this, addressList, OmnivaAddressActivity.this);
        placeRecycleview.setAdapter(placeHistoryAdapter);
    }

    public ArrayList autocomplete(String input) {
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();

        try {
            StringBuilder sb = new StringBuilder(UiHelper.PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + UiHelper.API_KEY);
            //sb.append("&types=(regions)");
            //      sb.append("&components=country:de|country:in|country:us");
            //  sb.append("&components=country:DE");
            //    sb.append("&components=geocode");

            if (getIntent().getStringExtra("selected_country").equalsIgnoreCase("Latvia"))
                sb.append("&components=country:LV");
            else if (getIntent().getStringExtra("selected_country").equalsIgnoreCase("Estonia"))
                sb.append("&components=country:EE");
            else
                sb.append("&components=country:LT");


            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            return resultList;
        } catch (IOException e) {
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            Log.e("place_data", "" + jsonObj.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                JSONObject jsonObject = predsJsonArray.getJSONObject(i);
                AddressModel addressModel = new AddressModel();
                addressModel.setAddress(jsonObject.getString("description"));
                addressModel.setLocationId(jsonObject.getString("place_id"));
                resultList.add(addressModel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    private class DownloadFilesTask extends AsyncTask<String, String, Boolean> {
        DatabaseHandler db;

        public DownloadFilesTask() {
            db = new DatabaseHandler(OmnivaAddressActivity.this);
        }

        @Override
        protected Boolean doInBackground(String... params) {

            boolean exist = false;

            List<AddressModel> addressModels = db.getAllAddress();
            for (AddressModel cn : addressModels) {
                Log.e("aa", "doInBackground" + cn.getLocationId().toString());
                Log.e("aa", "doInBackground" + locationId.toString());
                if (cn.getLocationId().equalsIgnoreCase(locationId)) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                db.addSAddress(new AddressModel(locationId, address, latitude, longitude));
            }
            return null;
        }
    }

    private class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
        private ArrayList<AddressModel> resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int searchadrsrow) {
            super(context, searchadrsrow);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int position) {
            //AddressModel addressModel=resultList.get(position).getAddress();
            return resultList.get(position).getAddress();
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        resultList = autocomplete(constraint.toString());
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }
}
