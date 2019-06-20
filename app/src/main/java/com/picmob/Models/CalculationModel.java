package com.picmob.Models;

public class CalculationModel {

 public float original_width;
    public   float original_height;
    public  float cgheight=0;
    public float cgwidth=0;
    public float after_height=0;
    public float after_width=0;
    public  float pos_x=0;
    public float pos_y=0;

    public CalculationModel(float original_width, float original_height, float cgheight, float cgwidth, float after_height, float after_width, float pos_x, float pos_y) {
        this.original_width = original_width;
        this.original_height = original_height;
        this.cgheight = cgheight;
        this.cgwidth = cgwidth;
        this.after_height = after_height;
        this.after_width = after_width;
        this.pos_x = pos_x;
        this.pos_y = pos_y;
    }

    public float getOriginal_width() {
        return original_width;
    }

    public void setOriginal_width(float original_width) {
        this.original_width = original_width;
    }

    public float getOriginal_height() {
        return original_height;
    }

    public void setOriginal_height(float original_height) {
        this.original_height = original_height;
    }

    public float getCgheight() {
        return cgheight;
    }

    public void setCgheight(float cgheight) {
        this.cgheight = cgheight;
    }

    public float getCgwidth() {
        return cgwidth;
    }

    public void setCgwidth(float cgwidth) {
        this.cgwidth = cgwidth;
    }

    public float getAfter_height() {
        return after_height;
    }

    public void setAfter_height(float after_height) {
        this.after_height = after_height;
    }

    public float getAfter_width() {
        return after_width;
    }

    public void setAfter_width(float after_width) {
        this.after_width = after_width;
    }

    public float getPos_x() {
        return pos_x;
    }

    public void setPos_x(float pos_x) {
        this.pos_x = pos_x;
    }

    public float getPos_y() {
        return pos_y;
    }

    public void setPos_y(float pos_y) {
        this.pos_y = pos_y;
    }
}
