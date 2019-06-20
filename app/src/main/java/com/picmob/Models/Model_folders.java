package com.picmob.Models;

import java.util.ArrayList;

public class Model_folders {
    String str_folder;
    ArrayList<Model_images> imageList=new ArrayList();
    public boolean isSelected=false;

    public String getStr_folder() {
        return str_folder;
    }

    public void setStr_folder(String str_folder) {
        this.str_folder = str_folder;
    }

    public ArrayList<Model_images> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<Model_images> imageList) {
        this.imageList = imageList;
    }
}
