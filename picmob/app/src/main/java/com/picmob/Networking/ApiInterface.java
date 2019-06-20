package com.picmob.Networking;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @POST("users/login")
    Call<JsonObject>login(@Body JsonObject jsonObject);

    @POST("register/social_login")
    Call<JsonObject>socialLogin(@Body JsonObject jsonObject);

    @GET("picture_sizes/picsize")
    Call<JsonObject> getPictureSize(@Header("token") String token, @Query("language_id")String language_id);

    @GET("picture_sizes/order_screen_slider/{language_id} ")
    Call<JsonObject> order_screen_slider(@Path("language_id") int language_id);

    @POST("contact_us/contact_us_form")
    Call<JsonObject> contacts(@Body JsonObject jsonObject);

    @POST("register/update_profile")
    Call<JsonObject>update(@Header("token") String token,@Body RequestBody requestBody);

    @POST("users/logout")
    Call<JsonObject> logout(@Header("token") String token);

    @GET("paper/paper_type")
    Call<JsonObject> papertype(@Header("token") String token ,@Query("language_id")String language_id);

    //for color images
    @POST("product/product_category")
    Call<JsonObject>productCategory(@Header("token") String token,@Body JsonObject jsonObject);

    //for slider images
    @POST("product/product_category_slider")
    Call<JsonObject>productCategorySlider(@Header("token") String token,@Body JsonObject jsonObject);

    //forSizes
    @POST("product/product_sizes")
    Call<JsonObject>product_sizes(@Header("token") String token,@Body JsonObject jsonObject);

    @POST("users/forgot_password")
    Call<JsonObject>forgot_password(@Body JsonObject jsonObject);

    @POST("users/verify_otp")
    Call<JsonObject>verify_otp(@Body JsonObject jsonObject);

    @POST("users/reset_password")
    Call<JsonObject>reset_password(@Body JsonObject jsonObject);

    @GET("product/product_list")
    Call<JsonObject> productList(@Header("token") String token,@Query("language_id")String language_id);

    @Multipart
    @POST("orders/upload_cart_image")
    Call<JsonObject>upload_image(@Part("id") RequestBody id, @Part("orderid") RequestBody orderid, @Part("quantity") RequestBody quantity,@Part("language_id") RequestBody language_id,@Part MultipartBody.Part file,@Part("height") RequestBody height,@Part("width") RequestBody width,@Part("Cropping_name_id") RequestBody paper_cropping);

    @POST("register/add_guest_user")
    Call<JsonObject>add_guest(@Body JsonObject jsonObject);

    @GET("country/country_list")
    Call<JsonObject> country_list(@Header("token") String token,@Query("language_id")String language_id);

    @POST("orders/submit_cart")
    Call<JsonObject>submit_cart(@Header("token") String token,@Body JsonObject jsonObject);

    @POST("register/register")
    Call<JsonObject>register(@Body JsonObject jsonObject);

    @POST("orders/my_order")
    Call<JsonObject> my_order(@Header("token") String token,@Body JsonObject jsonObject);

    @POST("card/add_card")
    Call<JsonObject>add_card(@Header("token") String token,@Body JsonObject jsonObject);

    @GET("card/my_card")
    Call<JsonObject> my_card(@Header("token") String token,@Query("language_id")String language_id);

    @POST("content/about_us")
    Call<JsonObject> getStaticContent(@Body JsonObject jsonObject);

    @GET("country/countries_shipping_price_list")
    Call<JsonObject> europe_country_list(@Header("token") String token,@Query("language_id")String language_id);

    @POST("orders/get_omniva_address")
    Call<JsonObject> get_omniva_address(@Body JsonObject jsonObject);

    @POST("content/get_info_faq")
    Call<JsonObject> get_info_faq(@Body JsonObject jsonObject);

    @POST("content/get_info_title")
    Call<JsonObject> get_info_title(@Body JsonObject jsonObject);
}
