package com.project.grace.floodmeterapp;

import android.graphics.Bitmap;

import java.io.Serializable;

public class CrowdSource{

    int crowdsource;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    String userID;
    String tag;
    String dateAdded;
    double lat;
    double lon;
    byte[] image;

    public CrowdSource(){

    }

    public CrowdSource(int crowdsource) {
        this.crowdsource = crowdsource;
    }

    public int getCrowdsource() {
        return crowdsource;
    }

    public void setCrowdsource(int crowdsource) {
        this.crowdsource = crowdsource;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public double getLat() { return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setImage(byte[] image){ this.image = image; }

    public byte[] getImage(){return this.image;}
}
