package com.picmob.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.picmob.AppCustomView.UiHelper;
import com.picmob.Networking.BaseApplication;
import com.picmob.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;


public class SplashActivity extends BaseActivity {
    private static int SPLASH_TIME_OUT = 3000;
    String language;
    Locale myLocale;
    Bitmap bitmap;
    private static final int REQUEST_PERMISSIONS = 100;
    Intent intent ;
    String action ;
    String type ;
    ImageView share;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        language=BaseApplication.getInstance().getSession().getLanguage();
        if (!language.isEmpty() && !language.equalsIgnoreCase(""))
        setLocale(language);
         intent = getIntent();
         action = intent.getAction();
         type = intent.getType();
        share=findViewById(R.id.share);
        Log.e("TAG", "INTENT:"+type );
        if (type==null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //         dropSessionData();
                    CheckUserIsLoginOrNot();
                    // This method will be executed once the timer is over
                    // Start your app main activity
                }
            }, SPLASH_TIME_OUT);

        }

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_PERMISSIONS);

            Log.e("TAG", "onCreateView:if " );
        } else {
            Log.e("DB", "PERMISSION GRANTED");
            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if (type.startsWith("image/")) {
                    share.setVisibility(View.VISIBLE);
                    handleSendImage(intent); // Handle single image being sent
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finishAffinity();
                        }
                    }, 3000);


                }
            } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
                if (type.startsWith("image/")) {
                    share.setVisibility(View.VISIBLE);
                    handleSendMultipleImages(intent); // Handle multiple images being sent
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finishAffinity();

                        }
                    }, 3000);
                }
            }
        }

        BaseApplication.getInstance().getSession().setOkInfo("");

    }
    private void CheckUserIsLoginOrNot() {
        Intent Intent ;
        if(BaseApplication.getInstance().getSession().isLoggedIn()){
            Intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(Intent);
            finish();
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        }
        else {

            if (BaseApplication.getInstance().getSession().getLanguage().equalsIgnoreCase("") || BaseApplication.getInstance().getSession().getLanguage()==null) {
                Intent = new Intent(SplashActivity.this, SelectLanguageActivity.class);
                startActivity(Intent);
                finish();
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }else {
                Intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(Intent);
                finish();
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

            }
        }
    }

    void dropSessionData(){

        BaseApplication.getInstance().getSession().setSelectedImages("");
    }
    public void setLocale(String lang) {
        Log.e("TAG", "setLocale: "+lang );
        myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();

        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);


    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (imageUri != null) {
            // Update UI to reflect image being shared
            Log.e("TAG", "handleSendImage: "+imageUri );

            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/PicMob");
            if (!myDir.exists()) {
                myDir.mkdirs();
            }
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            String fname = "Image-"+ n +".jpg";
            File file = new File (myDir, fname);
            if (file.exists ())
                file.delete ();
            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                final Uri contentUri = Uri.fromFile(file);
                scanIntent.setData(contentUri);
                sendBroadcast(scanIntent);
            } else {
                final Intent intentt = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
                sendBroadcast(intentt);
            }


        }
    }
    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);

        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
            Log.e("TAG", "handleSendImage: " + imageUris);

            for (int i = 0; i < imageUris.size(); i++) {

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUris.get(i));
                } catch (IOException e) {
                    e.printStackTrace();
                }


                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/PicMob");
                if (!myDir.exists()) {
                    myDir.mkdirs();
                }
                Random generator = new Random();
                int n = 10000;
                n = generator.nextInt(n);
                String fname = "Image-" + n + ".jpg";
                File file = new File(myDir, fname);
                if (file.exists())
                    file.delete();
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    final Uri contentUri = Uri.fromFile(file);
                    scanIntent.setData(contentUri);
                    sendBroadcast(scanIntent);
                } else {
                    final Intent intentt = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
                    sendBroadcast(intentt);
                }
            }
        }
    }


    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("TAG", "onRequestPermissionsResult: " + permissions);

        if (requestCode == REQUEST_PERMISSIONS) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (Intent.ACTION_SEND.equals(action) && type != null) {
                    if (type.startsWith("image/")) {
                        share.setVisibility(View.VISIBLE);
                        handleSendImage(intent); // Handle single image being sent
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finishAffinity();
                            }
                        }, 3000);
                    }
                } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
                    if (type.startsWith("image/")) {
                        share.setVisibility(View.VISIBLE);
                        handleSendMultipleImages(intent); // Handle multiple images being sent
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finishAffinity();
                            }
                        }, 3000);
                    }
                }

            } else {
                Toast.makeText(this, "The app was not allowed to read or write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
            }


        }

    }

}
