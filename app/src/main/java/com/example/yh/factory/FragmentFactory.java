package com.example.yh.factory;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;

import com.example.yh.fragment.FriendsFragment;
import com.example.yh.fragment.MainFragment;
import com.example.yh.fragment.MyZoneFragment;
import com.example.yh.fragment.RecordFragment;

import yalantis.com.sidemenu.interfaces.ScreenShotable;

/**
 * Created by yh on 2015/2/10.
 */
public class FragmentFactory implements ScreenShotable {

    private static final String TAG = "FragmentFactory";

    public static final String CLOSE = "Close";
    public static final String BUILDING = "Building";
    public static final String BOOK = "Book";
    public static final String PAINT = "Paint";
    public static final String CASE = "Case";
    public static final String SHOP = "Shop";
    public static final String PARTY = "Party";
    public static final String MOVIE = "Movie";

    @Override
    public void takeScreenShot() {

    }

    @Override
    public Bitmap getBitmap() {
        return null;
    }

    public static enum FragmentType{
        MAIN,MAP,FRIENDS,RECORD
    }

    public static Fragment getInstance(FragmentType type){
        switch(type){
            case MAIN:
                return new MainFragment();
            case MAP:
                return new MyZoneFragment();
            case FRIENDS:
                return new FriendsFragment();
            case RECORD:
                return new RecordFragment();
            default:
                return null;
        }
    }

}
