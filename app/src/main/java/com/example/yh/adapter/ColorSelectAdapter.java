package com.example.yh.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yh.data.Constants;
import com.example.yh.smartcycle.R;

import java.util.List;

/**
 * Created by yh on 2015/3/4.
 */
public class ColorSelectAdapter extends BaseAdapter {
    private Context context;
    private List<Integer> list;

    public ColorSelectAdapter(Context context) {
        this.context = context;
        list = Constants.colors;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_select_color_gridview, null);
            holder.textView = (TextView) convertView.findViewById(R.id.item_select_color_tv);
            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }
        holder.textView.setBackgroundColor(context.getResources().getColor(list.get(position)));
//        holder.textView.setBackgroundResource(R.drawable.select_font_selector);
        return convertView;
    }
    class Holder{
        TextView textView;
    }
}
