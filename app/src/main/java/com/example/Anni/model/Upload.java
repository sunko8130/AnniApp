package com.example.Anni.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Upload implements Serializable {
    private String mContent;
    private String mImageUrl;
    private String mKey;
 
    public Upload() {
        //empty constructor needed
    }
 
    public Upload(String content, String imageUrl) {
        mContent = content;
        mImageUrl = imageUrl;
    }

    public String getmContent() {
        return mContent;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    public String getImageUrl() {
        return mImageUrl;
    }
 
    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    @Exclude
    public String getmKey() {
        return mKey;
    }

    @Exclude
    public void setmKey(String mKey) {
        this.mKey = mKey;
    }
}