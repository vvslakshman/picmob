package com.picmob.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.picmob.Activity.PhotosActivity;
import com.picmob.Activity.Selected_Photos;
import com.picmob.Activity.YourOrderActivity;
import com.picmob.Adapter.Adapter_PhotosFolder;
import com.picmob.AppCustomView.UiHelper;
import com.picmob.Models.Model_folders;
import com.picmob.Models.Model_images;
import com.picmob.R;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MobFragment extends Fragment {

    private static final String TAG ="MobFragemnt" ;
    @BindView(R.id.gv_folder)
    GridView gv_folder;


    public static ArrayList<Model_folders> al_images = new ArrayList<>();
    boolean boolean_folder;
   private Adapter_PhotosFolder obj_adapter;
    private static final int REQUEST_PERMISSIONS = 100;
    int column_index_data, column_index_folder_name;

    String price,width,height,picture_size_id,discount,quantity;
    boolean isnewProduct=false;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mob, container, false);
        ButterKnife.bind(this, view);

        Bundle bundle = this.getArguments();
        price = bundle.getString("price");
        width = bundle.getString("width");
        height = bundle.getString("height");
        discount = bundle.getString("discount");
        quantity = bundle.getString("quantity");
        picture_size_id=bundle.getString("picture_size_id");
        isnewProduct=bundle.getBoolean("newProduct");
        Log.e(TAG, "onCreate isNew "+isnewProduct );
        Log.e(TAG, "onItemClick: "+price );

        gv_folder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), PhotosActivity.class);
                intent.putExtra("value",i);
                intent.putExtra("price",price);
                intent.putExtra("width",width);
                intent.putExtra("height",height);
                intent.putExtra("discount",discount);
                intent.putExtra("quantity",quantity);
                intent.putExtra("picture_size_id",picture_size_id);
                intent.putExtra("newProduct",isnewProduct);
                for (int j = 0; j < al_images.size(); j++) {
                    //   Log.e(TAG, "onItemClick: "+al_images.get(i).getStr_folder());
                    intent.putExtra("foldername",al_images.get(i).getStr_folder());
                }
                //      Log.e(TAG, "folder ka name: "+column_index_data);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);

            }
        });

        if (ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_PERMISSIONS);

            Log.e(TAG, "onCreateView:if " );
        } else {
            Log.e("DB", "PERMISSION GRANTED");
            fn_imagespath();
        }


        Log.e(TAG, "onCreateView:view " );
        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public ArrayList<Model_folders> fn_imagespath() {

        Log.e(TAG,"Fn_ImagesPAth");
        al_images.clear();

        int int_position = 0;
        Uri uri;
        Cursor cursor;

        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = getActivity().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            //    Log.e("Column", absolutePathOfImage);
            //    Log.e("Folder", cursor.getString(column_index_folder_name));

            for (int i = 0; i < al_images.size(); i++) {
                if (al_images.get(i).getStr_folder().equals(cursor.getString(column_index_folder_name))) {
                    boolean_folder = true;
                    int_position = i;
                    break;
                } else {
                    boolean_folder = false;
                }
            }


            if (boolean_folder) {

                ArrayList<Model_images> folderList = new ArrayList<>();
                Model_images model_images=new Model_images();
                model_images.setSingle_imagepath(absolutePathOfImage);
                folderList.addAll(al_images.get(int_position).getImageList());
                folderList.add(model_images);
                al_images.get(int_position).setImageList(folderList);

            } else {
                ArrayList<Model_images> al_path = new ArrayList<>();
                Model_images model_images=new Model_images();
                model_images.setSingle_imagepath(absolutePathOfImage);
                al_path.add(model_images);
                Model_folders obj_model = new Model_folders();
                obj_model.setStr_folder(cursor.getString(column_index_folder_name));
                obj_model.setImageList(al_path);

                if (cursor.getString(column_index_folder_name).equalsIgnoreCase("PicMob")){
                    al_images.add(0,obj_model);
                }else
                al_images.add(obj_model);


            }


        }


//        for (int i = 0; i < al_images.size(); i++) {
//            Log.e("FOLDER", al_images.get(i).getStr_folder());
//            for (int j = 0; j < al_images.get(i).getImageList().size(); j++) {
//        //        Log.e("FILE", al_images.get(i).getImageList().get(j));
//            }
//        }
        obj_adapter = new Adapter_PhotosFolder(getActivity(),al_images);
        gv_folder.setAdapter(obj_adapter);
        return al_images;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(TAG, "onRequestPermissionsResult: " + permissions);

        if (requestCode == REQUEST_PERMISSIONS) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fn_imagespath();
                Log.e(TAG, "grant result: " + grantResults.length);
            } else {
                Toast.makeText(getActivity(), "The app was not allowed to read or write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
            }


        }

    }
}