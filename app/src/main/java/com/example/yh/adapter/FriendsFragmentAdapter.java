package com.example.yh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.yh.data.Friend;
import com.example.yh.network.image.ImageCacheManager;
import com.example.yh.smartcycle.R;
import com.example.yh.view.NetworkCircleImageView;

import java.util.List;

/**
 * Created by yh on 2015/2/28.
 */
public class FriendsFragmentAdapter extends BaseAdapter {
    private List<Friend> mList;
    private Context mContext;
    public FriendsFragmentAdapter(List list, Context context) {
        mList = list;
        mContext = context;
    }
    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_fragment_friends_lv,null);
            holder.ivFace = (NetworkCircleImageView) convertView.findViewById(R.id.fragment_friends_iv_user_face);
            holder.tvContent = (TextView) convertView.findViewById(R.id.fragment_friends_tv_content);
            holder.tvTime = (TextView) convertView.findViewById(R.id.fragment_friends_tv_time);
            holder.tvUserName = (TextView) convertView.findViewById(R.id.fragment_friends_tv_username);
            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
            holder.ivFace.setImageUrl(mList.get(position).getFace(), ImageCacheManager.getInstance().getImageLoader());
            holder.tvUserName.setText(mList.get(position).getUsername());
//            holder.tvTime.setText(mList.get(position).get);
//            holder.tvContent.setText(mList.get(position));
        }
        return convertView;
    }

    private class Holder{
        public NetworkCircleImageView ivFace;
        private TextView tvUserName;
        private TextView tvContent;
        private TextView tvTime;
    }
}
