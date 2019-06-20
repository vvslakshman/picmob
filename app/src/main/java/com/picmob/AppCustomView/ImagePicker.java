package com.picmob.AppCustomView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.picmob.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


public class ImagePicker {

    private static final int DEFAULT_MIN_WIDTH_QUALITY = 400;        // min pixels
    private static final String TAG = "ImagePicker";
    private static final String TEMP_IMAGE_NAME = "tempImage";

    public static int minWidthQuality = DEFAULT_MIN_WIDTH_QUALITY;


    public static Intent getPickImageIntent(Context context) {
        Log.e("bug","image picker image intent");
        Intent chooserIntent = null;
        List<Intent> intentList = new ArrayList<>();
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //    takePhotoIntent.putExtra("android.intent.extras.CAMERA_FACING",1);
        takePhotoIntent.putExtra("android.intent.extras.CAMERA_FACING", Camera.CameraInfo.CAMERA_FACING_FRONT);
        // takePhotoIntent.putExtra("return-data", true);
        //modified for oreo
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(context,"com.picmob.fileProvider",getTempFile(context)));

        //add for oreo
        takePhotoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        intentList = addIntentsToList(context, intentList, pickIntent);
        intentList = addIntentsToList(context, intentList, takePhotoIntent);

        if (intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1), context.getString(R.string.pick_image_intent_text));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }
        return chooserIntent;
    }

    private static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);

        }
        return list;
    }


    public static Bitmap getImageFromResult(Context context, int resultCode, Intent imageReturnedIntent) {

        Bitmap bm = null;
        File imageFile = getTempFile(context);

        if (resultCode == Activity.RESULT_OK) {
            Uri selectedImage;
            boolean isCamera = (imageReturnedIntent == null || imageReturnedIntent.getData() == null  || imageReturnedIntent.getData().toString().contains(imageFile.toString()));
            if (isCamera) {     /** CAMERA **/
//                selectedImage = Uri.fromFile(imageFile);
                //modified for oreo
                selectedImage = FileProvider.getUriForFile(context,"com.picmob.fileProvider",imageFile);
            }
            else {            /** ALBUM **/
                selectedImage = imageReturnedIntent.getData();
            }

            bm = getImageResized(context, selectedImage);
            int rotation = getRotation(context, selectedImage, isCamera);
            bm = rotate(bm, rotation);
        }
        return bm;
    }


    private static File getTempFile(Context context) {
        Log.e("bug","image picker get TempFile");
        File imageFile = new File(context.getExternalCacheDir(), TEMP_IMAGE_NAME);
        imageFile.getParentFile().mkdirs();
        return imageFile;
    }

    private static Bitmap decodeBitmap(Context context, Uri theUri, int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;

        Bitmap actuallyUsableBitmap = null;
        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = context.getContentResolver().openAssetFileDescriptor(theUri, "r");
            actuallyUsableBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return actuallyUsableBitmap;
    }

    /**
     * Resize to avoid using too much memory loading big images (e.g.: 2560*1920)
     **/
    private static Bitmap getImageResized(Context context, Uri selectedImage) {
        Bitmap bm = null;
        int[] sampleSizes = new int[]{5, 3, 2, 1};
        int i = 0;
        do {
            bm = decodeBitmap(context, selectedImage, sampleSizes[i]);
            i++;
        } while (bm.getWidth() < minWidthQuality && i < sampleSizes.length);

        int maxHeight = 816;
        int maxWidth = 612;
        float scale = Math.min(((float)maxHeight / bm.getWidth()), ((float)maxWidth / bm.getHeight()));

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);


        return bm;
    }


    private static int getRotation(Context context, Uri imageUri, boolean isCamera) {
        int rotation;
        if (isCamera) {
            rotation = getRotationFromCamera(context, imageUri);
        } else {
            rotation = getRotationFromGallery(context, imageUri);
        }
        Log.d(TAG, "Image rotation: " + rotation);
        return rotation;
    }

    private static int getRotationFromCamera(Context context, Uri imageFile) {
        int rotate = 0;
        try {

            context.getContentResolver().notifyChange(imageFile, null);
            ExifInterface exif = new ExifInterface(imageFile.getPath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public static int getRotationFromGallery(Context context, Uri imageUri) {
        int result = 0;
        String[] columns = {MediaStore.Images.Media.ORIENTATION};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(imageUri, columns, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int orientationColumnIndex = cursor.getColumnIndex(columns[0]);
                result = cursor.getInt(orientationColumnIndex);
            }
        } catch (Exception e) {
            //Do nothing
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }//End of try-catch block
        return result;
    }


    private static Bitmap rotate(Bitmap bm, int rotation) {
        if (rotation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            Bitmap bmOut = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            return bmOut;
        }
        return bm;
    }

}