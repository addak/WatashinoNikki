package com.bignerdranch.watashinonikki;

import java.io.File;
import java.util.Date;
import java.util.UUID;

public class DiaryEntry {


    private UUID mId;
    private String mTitle;
    private Date mDate;
    private String mWeather;
    private String mContent;
    private String mImagePath;

    public DiaryEntry(){
        this(UUID.randomUUID());
    }

    public DiaryEntry(UUID uuid){
        mId = uuid;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getPhotoFilename(){
        return "IMG_" + getId().toString() + ".jpg";
    }

    public String getPhotoThumbnailFilename(){ return "IMG_" + getId().toString() + "_thumbnail.jpg"; }

    public String getWeather() {
        return mWeather;
    }

    public void setWeather(String weather) {
        mWeather = weather;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String imagePath) {
        mImagePath = imagePath;
    }
}
