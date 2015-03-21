package com.example.yh.data;

import android.content.Context;

/**
 * Created by yh on 2015/2/12.
 */
public class User {

    private static final String TAG_SP = Constants.TAG_SP_USER;

    private String id;
    private String username;
    private String password;
    private String sex;
    private String attrs;
    private String birthday;
    private String telphone;
    private String description;
    private boolean login;
    private static User mUser = null;
    private static Context context;
    private String faceUrl;
    private String backgroundUrl;

    private User(){
        login = loadBoolean(Constants.TAG_SP_USER_LOGIN);
        if(login) {
            id = loadString(Constants.TAG_SP_USER_ID);
            username = loadString(Constants.TAG_SP_USER_USERNAME);
            sex = loadString(Constants.TAG_SP_USER_SEX);
            attrs = loadString(Constants.TAG_SP_USER_ATTRS);
            birthday = loadString(Constants.TAG_SP_USER_BIRTHDAY);
            telphone = loadString(Constants.TAG_SP_USER_TELPHONE);
            description = loadString(Constants.TAG_SP_USER_DESCRIPTION);
            if(loadString(Constants.TAG_SP_USER_FACEURL)!=null) {
                faceUrl = loadString(Constants.TAG_SP_USER_FACEURL);
            }
            if (loadString(Constants.TAG_SP_USER_BACKGROUNDURL) != null) {
                backgroundUrl = loadString(Constants.TAG_SP_USER_BACKGROUNDURL);
            }
        }
    }

    public void logout() {
        setDescription(null);
        setFaceUrl(null);
        setBackgroundUrl(null);
        setBirthday(null);
        setAttrs(null);
        setId(null);
        setLogin(false);
        setPassword(null);
        setSex(null);
        setTelphone(null);
        setUsername(null);
    }

    public static User getInstance(){
        if(mUser == null){
            mUser = new User();
        }
        return mUser;
    }

    public String getBackgroundUrl() {
        return backgroundUrl;
    }

    public void setBackgroundUrl(String backgroundUrl) {
        this.backgroundUrl = backgroundUrl;
        save(Constants.TAG_SP_USER_BACKGROUNDURL,backgroundUrl);
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
        save(Constants.TAG_SP_USER_BIRTHDAY,birthday);
    }

    public String getTelphone() {
        return telphone;
    }

    public void setTelphone(String telphone) {
        this.telphone = telphone;
        save(Constants.TAG_SP_USER_TELPHONE, telphone);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        save(Constants.TAG_SP_USER_DESCRIPTION,description);
    }

    private String loadString(String tag){
        return context.getSharedPreferences(TAG_SP,Context.MODE_PRIVATE).getString(tag,"");
    }

    private boolean loadBoolean(String tag) {
        return context.getSharedPreferences(TAG_SP,Context.MODE_PRIVATE).getBoolean(tag,false);
    }

    private void save(String tag,String data) {
        context.getSharedPreferences(TAG_SP, Context.MODE_PRIVATE).edit().putString(tag, data).commit();
    }

    private void save(String tag,boolean login) {
        context.getSharedPreferences(TAG_SP, Context.MODE_PRIVATE).edit().putBoolean(tag, login).commit();
    }

    public String getFaceUrl() {
        return faceUrl;
    }

    public void setFaceUrl(String faceUrl) {
        this.faceUrl = faceUrl;
        save(Constants.TAG_SP_USER_FACEURL,faceUrl);
    }

    public static void init(Context _context){
        context = _context;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        save(Constants.TAG_SP_USER_USERNAME,username);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
        save(Constants.TAG_SP_USER_SEX,sex);
    }

    public String getAttrs() {
        return attrs;
    }

    public void setAttrs(String attrs) {
        this.attrs = attrs;
        save(Constants.TAG_SP_USER_ATTRS,attrs);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        save(Constants.TAG_SP_USER_ID,id);
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
        save(Constants.TAG_SP_USER_LOGIN,login);
    }
}
