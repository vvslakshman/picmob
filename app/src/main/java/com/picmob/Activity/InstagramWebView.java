package com.picmob.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.picmob.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InstagramWebView extends BaseActivity{

    private static final String TAG ="InstagramWebView" ;
    @BindView(R.id.webView)

    WebView webView;


    private static final String client_id = "37677046d01940ebb9c2e85f6f7efdd9";
    private static final String AUTHURL = "https://api.instagram.com/oauth/authorize/";
    private static final String TOKENURL = "https://api.instagram.com/oauth/access_token";
    public static final String APIURL = "https://api.instagram.com/v1";
    public static String CALLBACKURL = "https://www.xtreemsolution.com";
    String authURLString = AUTHURL + "?client_id=" + client_id + "&redirect_uri=" + CALLBACKURL + "&response_type=code&display=touch&scope=likes+comments+relationships";
    private static final String client_secret = "dc7b887ecf0b4359bd35cda409340bee";
    String tokenURLString = TOKENURL + "?client_id=" + client_id + "&client_secret=" + client_secret + "&redirect_uri=" + CALLBACKURL + "&grant_type=authorization_code";

    String request_token,id,username,accessTokenString,full_name,profile_picture,thumbnail_url;


    JSONObject dataObject=new JSONObject();
    JSONArray dataArray=new JSONArray();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instagram_webview);
        ButterKnife.bind(this);

        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(new AuthWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(authURLString);
    }

    private class AuthWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(CALLBACKURL)) {
                System.out.println(url);
                String parts[] = url.split("=");
                request_token = parts[1];  //This is your request token.
                Log.e(TAG, "shouldOverrideUrlLoading: "+request_token );

                new ddd().execute();
                return true;
            }
            return false;
        }
    }

    public   class ddd extends AsyncTask {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            Log.e(TAG, "onPreExecute: " );
            super.onPreExecute();
        }
        @Override
        protected Object doInBackground(Object[] objects) {
            Log.e(TAG, "doInBackground: " );
            try {
                URL url = new URL(tokenURLString);
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.setDoInput(true);
                httpsURLConnection.setDoOutput(true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpsURLConnection.getOutputStream());
                outputStreamWriter.write("client_id=" + client_id +
                        "&client_secret=" + client_secret +
                        "&grant_type=authorization_code" +
                        "&redirect_uri=" + CALLBACKURL +
                        "&code=" + request_token);

                outputStreamWriter.flush();
                String response = streamToString(httpsURLConnection.getInputStream());
                JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
                accessTokenString = jsonObject.getString("access_token"); //Here is your ACCESS TOKEN
                id = jsonObject.getJSONObject("user").getString("id");
                username = jsonObject.getJSONObject("user").getString("username"); //This is how you can get the user info. You can explore the JSON sent by Instagram as well to know what info you got in a response
                full_name=jsonObject.getJSONObject("user").getString("full_name");
                profile_picture=jsonObject.getJSONObject("user").getString("profile_picture");
                Log.e(TAG, "doInBackground: "+jsonObject.toString() );
//                JSONArray jsonArray = jsonObject.getJSONArray("data");
//                for (int i=0;i<jsonArray.length();i++) {
//                    JSONObject mainImageJsonObject = jsonArray.getJSONObject(i).getJSONObject("images").getJSONObject("low_resolution");
//                    String imageUrlString = mainImageJsonObject.getString("url");
//                    Log.e(TAG, "doInBackground: "+imageUrlString );
//                }



                URL example = new URL("https://api.instagram.com/v1/users/self/media/recent?access_token="
                        + accessTokenString);

                URLConnection tc = example.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        tc.getInputStream()));

                String line;
                while ((line = in.readLine()) != null) {
                    JSONObject ob = new JSONObject(line);

                     dataArray = ob.getJSONArray("data");

                    for (int i = 0; i < dataArray.length(); i++) {


                         dataObject = (JSONObject) dataArray.get(i);
                        Log.e(TAG, "for images: "+dataObject.toString() );
                        JSONObject jsonObject1=dataObject.getJSONObject("images").getJSONObject("thumbnail");
                         thumbnail_url=jsonObject1.getString("url");
                        Log.e(TAG, "thumbnail: "+thumbnail_url );
                      //  JSONObject nja = (JSONObject) jo.getJSONObject("photos");

                   //     JSONObject purl3 = (JSONObject) nja.getJSONObject("thumbnail");
                   //     Log.i(TAG, "images" + purl3.getString("url"));
                    }

                }



                Intent intent=new Intent();
                intent.putExtra("access_token",accessTokenString);
                intent.putExtra("id",id);
                intent.putExtra("username",username);
                intent.putExtra("full_name",full_name);
                intent.putExtra("profile_picture",profile_picture);
                intent.putExtra("thumbnail",dataArray.toString());
                setResult(Activity.RESULT_OK, intent);
                finish();
                Log.e(TAG, "doInBackground: "+username );
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            Log.e(TAG, "onPostExecute: " );
            super.onPostExecute(o);
        }
    }
    public static String streamToString(InputStream p_is)
    {
        try
        {
            BufferedReader m_br;
            StringBuffer m_outString = new StringBuffer();
            m_br = new BufferedReader(new InputStreamReader(p_is));
            String m_read = m_br.readLine();
            while(m_read != null)
            {
                m_outString.append(m_read);
                m_read =m_br.readLine();
            }
            return m_outString.toString();
        }
        catch (Exception p_ex)
        {
            p_ex.printStackTrace();
            Log.e(TAG, "streamToString: "+p_ex );
            return "";
        }
    }
}
