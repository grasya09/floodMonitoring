package com.project.grace.floodmeterapp;

public class CrowdSource {

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
    float lat;
    float lon;

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

    public float getLat() { return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }
}
