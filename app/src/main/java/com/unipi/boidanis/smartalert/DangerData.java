
package com.unipi.boidanis.smartalert;

import android.location.Location;
import android.widget.ImageView;


import androidx.annotation.Nullable;

import java.util.Date;
import java.util.stream.Stream;

public class DangerData {
    private String dangerType,description;
    private Date date;
    private Double longtitude,lat;
    private String imageUrl;
    private Boolean approved;
    private String key;
    private int number;
    public DangerData(){}
    public String getDangerType() {
        return dangerType;
    }

    public void setDangerType(String dangerType) {
        this.dangerType = dangerType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public DangerData(String key, String dangerType, String description, Double longtitude, Double lat, Date date,@Nullable String imageUrl, Boolean approved,int number) {
        this.key=key;
        this.dangerType=dangerType;
        this.description=description;
        this.longtitude=longtitude;
        this.lat=lat;
        this.date=date;
        this.imageUrl=imageUrl;
        this.approved=approved;
        this.number=number;
    }
}
