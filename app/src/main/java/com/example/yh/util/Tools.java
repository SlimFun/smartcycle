package com.example.yh.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yh on 2015/2/16.
 */
public class Tools {
    public static String bitmap2String(Bitmap bitmap) {
        if (bitmap != null) {
            String string = null;
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
            byte[] bytes = bStream.toByteArray();
            string = Base64.encodeToString(bytes, Base64.DEFAULT);
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(string);
            String result = m.replaceAll("");
            String resultUtf8 = "";
            try {
                resultUtf8 = URLEncoder.encode(result, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return resultUtf8;
        }else {
            return null;
        }
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

//    public static String formatTime(long time){
//        Calendar calendar = Calendar.getInstance();
//        long timeAgo = (System.currentTimeMillis() - time)/1000;
//        if(timeAgo < 60) {
//            return String.format("%s秒前", timeAgo);
//        }else if(timeAgo < 60*60) {
//            return String.format("%s分钟前", timeAgo/60);
//        }else if(timeAgo < 60*60*24){
//            return String.format("%s小时前",timeAgo/(60*60));
//        }else{
//            calendar.setTimeInMillis(time);
//            if(timeAgo < 60*60*24*365){
//                return String.format("%s月%s日%s点",calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DAY_OF_MONTH),calendar.get(Calendar.HOUR_OF_DAY));
//            }
//            return String.format("%s年%s月%s日",calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DAY_OF_MONTH));
//        }
//    }

    /**检查是否有网络*/
    private static boolean isNetworkAvailable(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        if (info != null) {
            return info.isAvailable();
        }
        return false;
    }

    /**检查是否有WiFi*/
    public static boolean isWifi(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        if (info != null) {
            return info.isAvailable();
        }
        return false;
    }

    /** 检查是否是移动网络 */
    public static boolean isMobile(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        if (info != null) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE)
                return true;
        }
        return false;
    }

    private static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    public static boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }else{
            return false;
        }
    }

    public static boolean isEmpty(String msg) {
        if (msg == null || msg.trim().equals("")) {
            return true;
        }else {
            return false;
        }
    }
}
