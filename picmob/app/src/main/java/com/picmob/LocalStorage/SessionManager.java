package com.picmob.LocalStorage;

import android.content.Context;
import android.content.SharedPreferences;
import com.picmob.AppCustomView.Constant;



public class SessionManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(Constant.PREFS, PRIVATE_MODE);
        editor = pref.edit();
    }
    // clear all shared preference data after logout
    public void clearSession() {
        editor.clear();
        editor.commit();

    }
    public Boolean isLoggedIn() {
        Boolean isLonged = pref.getBoolean(Constant.IS_LOGIN, false);
        return isLonged;

    }

    public void setIsLoggedIn() {
        editor.putBoolean(Constant.IS_LOGIN, true);
    }

    public void setDeviceToken(String data) {
        editor.putString(Constant.TOKEN, data);
        editor.commit();
    }

    public String getDeviceToken() {
        String flag = pref.getString(Constant.TOKEN, "");
        editor.commit();
        return flag;
    }
    public String getProfileData() {
        String flag = pref.getString(Constant.PROFILE_DATA, "");
        editor.commit();
        return flag;
    }

    public void setProfileData(String data) {
        editor.putString(Constant.PROFILE_DATA, data);
        editor.commit();
    }
    public void setToken(String data) {
        editor.putString("tokenn", data);
        editor.commit();
    }

    public String getToken() {
        String flag = pref.getString("tokenn", "");
        editor.commit();
        return flag;
    }

    public void setDeliveryLatitude(String address) {
        editor.putString(Constant.DELIVERY_LATITUDE, address);
        editor.commit();
    }

    public String getDeliveryLatitude() {
        String flag = pref.getString(Constant.DELIVERY_LATITUDE, "");
        editor.commit();
        return flag;
    }
    public void setDeliveryLongitude(String address) {
        editor.putString(Constant.DELIVERY_LONGITUDE, address);
        editor.commit();
    }

    public String getDeliveryLongitude() {
        String flag = pref.getString(Constant.DELIVERY_LONGITUDE, "");
        editor.commit();
        return flag;
    }

    public String getSelectedImages(){
        return pref.getString("images","");
    }

    public void setSelectedImages(String images){
        editor.putString("images",images);
        editor.commit();
    }

    public void setOkInfo(String bFilter){
        editor.putString("Ok",bFilter);
        editor.commit();
    }

    public String getOkInfo(){
        String flag=pref.getString("Ok","");
        return  flag;
    }

    public void setNevershowDialog(String bFilter){
        editor.putString("Never",bFilter);
        editor.commit();
    }

    public String getNeverShowDialog(){
        String flag=pref.getString("Never","");
        return  flag;
    }


    public void setAccessToken(String bFilter){
        editor.putString("access_token",bFilter);
        editor.commit();
    }

    public String getAccessToken(){
        String flag=pref.getString("access_token","");
        return  flag;
    }


    public String getCartData(){
        return pref.getString("cart","");
    }

    public void setCartData(String cart){
        editor.putString("cart",cart);
        editor.commit();
    }

    public String getImagesArray(){
        return pref.getString("imagesids","");
    }

    public void setImagesArray(String cart){
        editor.putString("imagesids",cart);
        editor.commit();
    }

    public void setLanguage(String language){
        editor.putString("language",language);
        editor.commit();
    }

    public String getLanguage(){
        String flag=pref.getString("language","");
        return  flag;
    }

    public void setWidth(String width){
        editor.putString("width",width);
        editor.commit();
    }

    public String getWidth(){
        String flag=pref.getString("width","");
        return  flag;
    }

    public void setHeight(String height){
        editor.putString("height",height);
        editor.commit();
    }

    public String getHeight(){
        String flag=pref.getString("height","");
        return  flag;
    }


}

