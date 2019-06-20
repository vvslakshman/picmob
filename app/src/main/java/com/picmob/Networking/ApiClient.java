package com.picmob.Networking;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.picmob.AppCustomView.Constant;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {

    static Retrofit mRetrofit = null;


   public static Retrofit getClient() {
        if (mRetrofit == null) {

            OkHttpClient.Builder client = new OkHttpClient.Builder()
                    .readTimeout(90, TimeUnit.SECONDS)
                    .connectTimeout(90, TimeUnit.SECONDS);

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            client.addInterceptor(loggingInterceptor);

            mRetrofit = new Retrofit.Builder()
                    .baseUrl(Constant.BASE_URL)
                    .client(client.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return mRetrofit;
    }
}
