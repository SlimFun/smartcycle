package com.example.yh.adapter;

import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by yh on 2015/3/4.
 */
public class FontSelectPagerAdapter extends PagerAdapter {
    private List<View> list;
    private static final String TAG = "FontSeletPagerAdater";
    public FontSelectPagerAdapter(List list) {
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
//        if (container.getChildCount() != 0) {
//            container.removeAllViews();
//        }
//        container.removeAllViews();
        if (list.get(position).getParent() != null) {
            ((ViewGroup)list.get(position).getParent()).removeAllViews();
        }
        container.addView(list.get(position), 0);
        return list.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(list.get(position));
    }

}
