package com.example.yh.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.yh.data.FaceText;
import com.example.yh.smartcycle.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yh on 2015/3/1.
 */
public class EmoteAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<FaceText> mDatas = new ArrayList<>();
    public EmoteAdapter(Context context, List<FaceText> datas) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        if (datas != null && datas.size() > 0) {
            mDatas = datas;
        }
    }
    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_face_text, null);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.emo_face_text);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        FaceText faceText = (FaceText) getItem(position);
        String key = faceText.text.substring(1);
        Drawable drawable = mContext.getResources().getDrawable(mContext.getResources().getIdentifier(key, "drawable", mContext.getPackageName()));
        holder.image.setImageDrawable(drawable);
        return convertView;
    }
    class ViewHolder{
        public ImageView image;
    }
}
