package com.example.yh.data;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by yh on 2015/2/12.
 */
public class DynamicInfo {
    private String id;
    private String userId;
    private String imageUrl;
    private String userFace;
    private int supports;
    private String content;
    private String time;
    private String username;
    private String sex;
    private String attrs;
    private int fontSize;
    private String fontType;
    private int fontColor;
    private boolean haveSupports;

    public DynamicInfo() {
    }

    public DynamicInfo(String id,String userId, String imageUrl, int supports, String content, String time, String username, String sex, String attrs,String userFace,String fontType,int fontSize,int fontColor,boolean haveSupports) {
        this.id = id;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.supports = supports;
        this.content = content;
        this.time = time;
        this.username = username;
        this.sex = sex;
        this.attrs = attrs;
        this.userFace = userFace;
        this.fontColor = fontColor;
        this.fontSize = fontSize;
        this.fontType = fontType;
        this.haveSupports = haveSupports;
    }

    public boolean isHaveSupports() {
        return haveSupports;
    }

    public void setHaveSupports(boolean haveSupports) {
        this.haveSupports = haveSupports;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAttrs() {
        return attrs;
    }

    public void setAttrs(String attrs) {
        this.attrs = attrs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getSupports() {
        return supports;
    }

    public void setSupports(int supports) {
        this.supports = supports;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserFace() {
        return userFace;
    }

    public void setUserFace(String userFace) {
        this.userFace = userFace;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontType() {
        return fontType;
    }

    public void setFontType(String fontType) {
        this.fontType = fontType;
    }

    public int getFontColor() {
        return fontColor;
    }

    public void setFontColor(int fontColor) {
        this.fontColor = fontColor;
    }
}
