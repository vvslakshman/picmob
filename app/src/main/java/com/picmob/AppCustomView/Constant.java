package com.picmob.AppCustomView;

public class Constant {
    public static final String BASE_URL = "http://xsdemo.com/picmob/api/";
   // public static final String BASE_URL = "http://192.168.1.61/picmob/api/";
    public static final String POS_HOME = "POS_HOME";
    public static final String POS_MYORDERS = "POS_MYORDERS";
    public static final String POS_MYCARDS = "POS_MYCARDS";
    public static final String POS_SETTINGS = "POS_SETTINGS";
    public static final String POS_INFO = "POS_INFO";
    public static final String POS_CONTACTS = "POS_CONTACTS";
    public static final String POS_ABOUTUS = "POS_ABOUTUS";
    public static final String PREFS = "PrefSession";
    public static final String SUCCESS = "success";
    public static final String IS_LOGIN = "isLogin";
    public static final String TOKEN = "token";
    public static final String PROFILE_DATA = "profile_data";
    public static final String DIVICE_TYPE = "android";
    public static final String DELIVERY_LATITUDE = "delivery_latitude";
    public static final String DELIVERY_LONGITUDE = "delivery_longitude";
    public final static int requestcodeForPermission = 11;

    public final static String [] askForLocationPermission = new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
    };

}
