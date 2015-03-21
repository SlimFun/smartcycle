package com.example.yh.data;

import com.example.yh.smartcycle.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yh on 2015/2/13.
 */
public class Constants {
    public static final String BASE_URL = "http://1.cyclesmart.sinaapp.com/servlet/";
    public static final String TAG_SP_USER_LOGIN = "login";
    public static final String TAG_SP_USER = "user";
    public static final String TAG_SP_USER_USERNAME = "username";
    public static final String TAG_SP_USER_SEX = "sex";
    public static final String TAG_SP_USER_ATTRS = "attrs";
    public static final String TAG_SP_USER_ID = "id";
    public static final String TAG_SP_USER_FACEURL = "faceUrl";
    public static final String TAG_SP_USER_BACKGROUNDURL = "backgroundurl";
    public static final String TAG_SP_USER_BIRTHDAY = "birthday";
    public static final String TAG_SP_USER_DESCRIPTION = "description";
    public static final String TAG_SP_USER_TELPHONE = "telphone";
    public static int ACTION_BAR_HEIGHT;
    public static String ATTRS;
    public static List<Integer> colors = new ArrayList<>();
    static {
        colors.add(R.color.color1);
        colors.add(R.color.color2);
        colors.add(R.color.color3);
        colors.add(R.color.color4);
        colors.add(R.color.color5);
        colors.add(R.color.color6);
        colors.add(R.color.color7);
        colors.add(R.color.color8);
    }
}
