package com.picmob.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
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
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.google.gson.JsonObject;
import com.picmob.AppCustomView.Constant;
import com.picmob.AppCustomView.CustomEditTextBold;
import com.picmob.AppCustomView.CustomTextViewNormal;
import com.picmob.AppCustomView.ProgressDialog;
import com.picmob.AppCustomView.UiHelper;
import com.picmob.Networking.BaseApplication;
import com.picmob.Networking.NetworkController;
import com.picmob.R;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class ContactsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ContactFragment";

    @BindView(R.id.editName)
    CustomEditTextBold editName;

    @BindView(R.id.editEmail)
    CustomEditTextBold editEmail;

    @BindView(R.id.editPhone)
    CustomEditTextBold editPhone;

    @BindView(R.id.editCategory)
    CustomEditTextBold editCategory;

    @BindView(R.id.editMessage)
    CustomEditTextBold editMessage;


    @BindView(R.id.txt_mail)
    CustomTextViewNormal txt_mail;

    @BindView(R.id.txt_url)
    CustomTextViewNormal txt_url;

    @BindView(R.id.txt_mob)
    CustomTextViewNormal txt_mob;

    @BindView(R.id.lyt_url)
    LinearLayout lyt_url;

    @BindView(R.id.lyt_mail)
    LinearLayout lyt_mail;

    @BindView(R.id.lyt_mob)
    LinearLayout lyt_mob;

    @BindView(R.id.btn_send)
    Button btn_send;

    @BindView(R.id.txt_underline)
    CustomTextViewNormal txt_underline;

    String url = "www.picmob.lv";
    String email = "info@picmob.lv";
    String phone = "+371 28673882";
    ProgressDialog progressDialog;

    String checked = "My order";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_contacts, container, false);
        ButterKnife.bind(this, view);
        //   txt_underline.setPaintFlags(txt_underline.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        lyt_url.setOnClickListener(this);
        lyt_mail.setOnClickListener(this);
        lyt_mob.setOnClickListener(this);
        btn_send.setOnClickListener(this);
        editCategory.setOnClickListener(this);
        return view;
    }

    public void callApi() {
        progressDialog = UiHelper.generateProgressDialog(getActivity(), false);
        progressDialog.show();
        JsonObject jsonObject = new JsonObject();
        int language_id = 1;
        String lang = BaseApplication.getInstance().getSession().getLanguage();
        if (lang.equalsIgnoreCase("en"))
            language_id = 1;
        else if (lang.equalsIgnoreCase("lv"))
            language_id = 2;
        else if (lang.equalsIgnoreCase("et"))
            language_id = 3;
        else if (lang.equalsIgnoreCase("lt"))
            language_id = 4;
        else if (lang.equalsIgnoreCase("ru"))
            language_id = 5;
        jsonObject.addProperty("language_id", language_id);
        jsonObject.addProperty("name", editName.getText().toString());
        jsonObject.addProperty("email", editEmail.getText().toString());
        jsonObject.addProperty("phone_number", editPhone.getText().toString());
        jsonObject.addProperty("case_category", editCategory.getText().toString());
        jsonObject.addProperty("message", editMessage.getText().toString());
        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().contacts(jsonObject);
        new NetworkController().post(getActivity(), call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                if (jsonObject != null) {
                    try {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        final JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {

                            UiHelper.showToast(getActivity(), jsonObject1.getString("message"));
                            editName.setText("");
                            editEmail.setText("");
                            editPhone.setText("");
                            editCategory.setText("");
                            editMessage.setText("");


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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.lyt_url:
                Log.e(TAG, "onClick: " + url);
                if (url.indexOf("http://") < 0) {
                    url = "http://" + url;
                }
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
                break;


            case R.id.lyt_mail:
                Intent emailIntent = new Intent(Intent.ACTION_SEND, Uri.parse(
                        "mailto:" + email));
                emailIntent.setType("text/plain");
                //  emailIntent.putExtra(Intent.EXTRA_EMAIL,email);
                startActivity(emailIntent);
                break;

            case R.id.lyt_mob:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                startActivity(intent);
                break;

            case R.id.editCategory:
                showCategoryDialog();
                break;

            case R.id.btn_send:
                validate();
                break;

        }
    }

    private void validate() {

        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String contact = editPhone.getText().toString().trim();
        String category = editCategory.getText().toString().trim();
        String message = editMessage.getText().toString().trim();


        if (name.isEmpty()) {
            UiHelper.showToast(getActivity(), getResources().getString(R.string.pls_enter_name));
        } else if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            UiHelper.showToast(getActivity(), getResources().getString(R.string.valid_msg_email));
        } else if (contact.isEmpty()) {
            UiHelper.showToast(getActivity(), getResources().getString(R.string.pls_enter_contact));
        } else if (category.isEmpty() || category.equalsIgnoreCase(getResources().getString(R.string.select_case_category))) {
            UiHelper.showToast(getActivity(), getResources().getString(R.string.pls_select_case_category));
        } else if (message.isEmpty()) {
            UiHelper.showToast(getActivity(), getResources().getString(R.string.pls_enter_message));
        } else
            callApi();

    }

    private void showCategoryDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        // Include dialog.xml file
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog.setContentView(R.layout.dialog_category);
        dialog.show();


        Button btn_yes = (Button) dialog.findViewById(R.id.btn_ok);
        LinearLayout lyt_myorder = (LinearLayout) dialog.findViewById(R.id.lyt_myorder);
        LinearLayout lyt_shipping = (LinearLayout) dialog.findViewById(R.id.lyt_shipping);
        LinearLayout lyt_returns = (LinearLayout) dialog.findViewById(R.id.lyt_returns);
        LinearLayout lyt_claims = (LinearLayout) dialog.findViewById(R.id.lyt_claims);
        LinearLayout lyt_special = (LinearLayout) dialog.findViewById(R.id.lyt_special);
        LinearLayout lyt_payment = (LinearLayout) dialog.findViewById(R.id.lyt_payment);
        LinearLayout lyt_product = (LinearLayout) dialog.findViewById(R.id.lyt_product);
        LinearLayout lyt_collab = (LinearLayout) dialog.findViewById(R.id.lyt_collab);
        LinearLayout lyt_other = (LinearLayout) dialog.findViewById(R.id.lyt_other);

        final CheckBox cb_myorder = (CheckBox) dialog.findViewById(R.id.cb_myorder);
        final CheckBox cb_shipping = (CheckBox) dialog.findViewById(R.id.cb_shipping);
        final CheckBox cb_returns = (CheckBox) dialog.findViewById(R.id.cb_returns);
        final CheckBox cb_claims = (CheckBox) dialog.findViewById(R.id.cb_claims);
        final CheckBox cb_special = (CheckBox) dialog.findViewById(R.id.cb_special);
        final CheckBox cb_payment = (CheckBox) dialog.findViewById(R.id.cb_payment);
        final CheckBox cb_product = (CheckBox) dialog.findViewById(R.id.cb_product);
        final CheckBox cb_collab = (CheckBox) dialog.findViewById(R.id.cb_collab);
        final CheckBox cb_other = (CheckBox) dialog.findViewById(R.id.cb_other);

        if (checked.equalsIgnoreCase("My order"))
            cb_myorder.setChecked(true);
        else if (checked.equalsIgnoreCase("Shipping/Delivery"))
            cb_shipping.setChecked(true);
        else if (checked.equalsIgnoreCase("Returns/Excahnge"))
            cb_returns.setChecked(true);
        else if (checked.equalsIgnoreCase("Claims"))
            cb_claims.setChecked(true);
        else if (checked.equalsIgnoreCase("Special offers/Discount"))
            cb_special.setChecked(true);
        else if (checked.equalsIgnoreCase("Payment"))
            cb_payment.setChecked(true);
        else if (checked.equalsIgnoreCase("Product questions"))
            cb_product.setChecked(true);
        else if (checked.equalsIgnoreCase("Collaborations/Press"))
            cb_collab.setChecked(true);
        else if (checked.equalsIgnoreCase("Other"))
            cb_other.setChecked(true);


        lyt_myorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_myorder.setChecked(true);
                cb_shipping.setChecked(false);
                cb_returns.setChecked(false);
                cb_claims.setChecked(false);
                cb_special.setChecked(false);
                cb_payment.setChecked(false);
                cb_product.setChecked(false);
                cb_collab.setChecked(false);
                cb_other.setChecked(false);
                checked = "My order";

            }
        });

        lyt_shipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_myorder.setChecked(false);
                cb_shipping.setChecked(true);
                cb_returns.setChecked(false);
                cb_claims.setChecked(false);
                cb_special.setChecked(false);
                cb_payment.setChecked(false);
                cb_product.setChecked(false);
                cb_collab.setChecked(false);
                cb_other.setChecked(false);
                checked = "Shipping/Delivery";
            }
        });


        lyt_returns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_myorder.setChecked(false);
                cb_shipping.setChecked(false);
                cb_returns.setChecked(true);
                cb_claims.setChecked(false);
                cb_special.setChecked(false);
                cb_payment.setChecked(false);
                cb_product.setChecked(false);
                cb_collab.setChecked(false);
                cb_other.setChecked(false);
                checked = "Returns/Excahnge";
            }
        });

        lyt_claims.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_myorder.setChecked(false);
                cb_shipping.setChecked(false);
                cb_returns.setChecked(false);
                cb_claims.setChecked(true);
                cb_special.setChecked(false);
                cb_payment.setChecked(false);
                cb_product.setChecked(false);
                cb_collab.setChecked(false);
                cb_other.setChecked(false);
                checked = "Claims";
            }
        });

        lyt_special.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_myorder.setChecked(false);
                cb_shipping.setChecked(false);
                cb_returns.setChecked(false);
                cb_claims.setChecked(false);
                cb_special.setChecked(true);
                cb_payment.setChecked(false);
                cb_product.setChecked(false);
                cb_collab.setChecked(false);
                cb_other.setChecked(false);
                checked = "Special offers/Discount";
            }
        });


        lyt_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_myorder.setChecked(false);
                cb_shipping.setChecked(false);
                cb_returns.setChecked(false);
                cb_claims.setChecked(false);
                cb_special.setChecked(false);
                cb_payment.setChecked(true);
                cb_product.setChecked(false);
                cb_collab.setChecked(false);
                cb_other.setChecked(false);
                checked = "Payment";
            }
        });

        lyt_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_myorder.setChecked(false);
                cb_shipping.setChecked(false);
                cb_returns.setChecked(false);
                cb_claims.setChecked(false);
                cb_special.setChecked(false);
                cb_payment.setChecked(false);
                cb_product.setChecked(true);
                cb_collab.setChecked(false);
                cb_other.setChecked(false);
                checked = "Product questions";
            }
        });

        lyt_collab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_myorder.setChecked(false);
                cb_shipping.setChecked(false);
                cb_returns.setChecked(false);
                cb_claims.setChecked(false);
                cb_special.setChecked(false);
                cb_payment.setChecked(false);
                cb_product.setChecked(false);
                cb_collab.setChecked(true);
                cb_other.setChecked(false);
                checked = "Collaborations/Press";
            }
        });

        lyt_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_myorder.setChecked(false);
                cb_shipping.setChecked(false);
                cb_returns.setChecked(false);
                cb_claims.setChecked(false);
                cb_special.setChecked(false);
                cb_payment.setChecked(false);
                cb_product.setChecked(false);
                cb_collab.setChecked(false);
                cb_other.setChecked(true);
                checked = "Other";
            }
        });

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // unlockCarApi(id);
                dialog.dismiss();
                editCategory.setText(checked);
            }
        });
    }

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
}
