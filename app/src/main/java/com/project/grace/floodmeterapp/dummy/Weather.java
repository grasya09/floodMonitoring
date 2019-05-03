package com.project.grace.floodmeterapp.dummy;

import android.graphics.Bitmap;

public class Weather {

    private int image;
    private String condition;
    private String description;

    public Weather(int image, String condition, String description){
        this.image = image;
        this.condition = condition;
        this.description = description;
    }
    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
