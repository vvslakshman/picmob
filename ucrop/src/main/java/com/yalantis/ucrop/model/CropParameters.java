package com.yalantis.ucrop.model;

import android.graphics.Bitmap;
import android.graphics.ColorFilter;

/**
 * Created by Oleksii Shliama [https://github.com/shliama] on 6/21/16.
 */
public class CropParameters {

    private int mMaxResultImageSizeX, mMaxResultImageSizeY;

    private Bitmap.CompressFormat mCompressFormat;
    private int mCompressQuality;
    private String mImageInputPath, mImageOutputPath;
    private ExifInfo mExifInfo;
    private ColorFilter mColorFilter;

    public CropParameters(int maxResultImageSizeX, int maxResultImageSizeY,
                          Bitmap.CompressFormat compressFormat, int compressQuality,
                          String imageInputPath, String imageOutputPath, ExifInfo exifInfo, ColorFilter colorFilter) {
        mMaxResultImageSizeX = maxResultImageSizeX;
        mMaxResultImageSizeY = maxResultImageSizeY;
        mCompressFormat = compressFormat;
        mCompressQuality = compressQuality;
        mImageInputPath = imageInputPath;
        mImageOutputPath = imageOutputPath;
        mExifInfo = exifInfo;
        mColorFilter=colorFilter;
    }

    public int getMaxResultImageSizeX() {
        return mMaxResultImageSizeX;
    }

    public int getMaxResultImageSizeY() {
        return mMaxResultImageSizeY;
    }

    public Bitmap.CompressFormat getCompressFormat() {
        return mCompressFormat;
    }

    public int getCompressQuality() {
        return mCompressQuality;
    }

    public String getImageInputPath() {
        return mImageInputPath;
    }

    public String getImageOutputPath() {
        return mImageOutputPath;
    }

    public ExifInfo getExifInfo() {
        return mExifInfo;
    }

    public ColorFilter getmColorFilter() {
        return mColorFilter;
    }
}
