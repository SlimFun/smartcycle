package com.example.yh.data;

/**
 * Created by yh on 2015/2/28.
 */
public class Friend {
    private String id;
    private String username;
    private String attrs;
    private String sex;
    private String face;

    public Friend(String id, String username, String attrs, String sex, String face) {
        this.id = id;
        this.username = username;
        this.attrs = attrs;
        this.sex = sex;
        this.face = face;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAttrs() {
        return attrs;
    }

    public void setAttrs(String attrs) {
        this.attrs = attrs;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }
}
