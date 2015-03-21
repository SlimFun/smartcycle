package com.example.yh.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.yh.smartcycle.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yh on 2015/3/3.
 */
public class FontSelectAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> fontList;
    public FontSelectAdapter(Context context) {
        mContext = context;
        fontList = new ArrayList<>();
        fontList.add("fonts/font1.TTF");
        fontList.add("fonts/font2.ttf");
        fontList.add("fonts/font3.TTF");
        fontList.add("fonts/font4.ttf");
        fontList.add("");
    }

    @Override
    public int getCount() {
        return fontList.size();
    }

    @Override
    public Object getItem(int position) {
        return fontList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_select_font_gridview,null);
            holder = new Holder();
            holder.textView = (TextView) convertView.findViewById(R.id.item_select_font_textview);
            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }
        if (fontList.get(position) != null && !fontList.get(position).trim().equals("")) {
            Typeface tf = Typeface.createFromAsset(mContext.getAssets(),fontList.get(position));
            holder.textView.setTypeface(tf);
        }
        return convertView;
    }

    class Holder{
        TextView textView;
    }
}
