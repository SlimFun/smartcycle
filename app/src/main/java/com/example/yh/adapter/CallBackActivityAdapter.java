package com.example.yh.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.yh.activity.ChatActivity;
import com.example.yh.data.Comment;
import com.example.yh.data.Constants;
import com.example.yh.data.User;
import com.example.yh.netword.RequestManager;
import com.example.yh.smartcycle.R;
import com.example.yh.util.TimeUtils;
import com.example.yh.util.Tools;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by yh on 2015/2/24.
 */
public class CallBackActivityAdapter extends BaseAdapter {
    private static final String TAG = "CallBackActivityAdapter";
    private Context context;
    private List<Comment> mList;
    private LayoutInflater inflater;
    private LinearLayout mLlSubmit;
    private LinearLayout mLlBottom;
    private EditText mEtCallBack;
    private TextView mTvSubmit;
    private View rootView;

    private RequestQueue mQueue = RequestManager.getRequestQueue();
    private CallBackInterface mInterfaceImpl;

    public CallBackActivityAdapter(Context context,List<Comment> list,CallBackInterface interfaceImpl){
        this.context = context;
        this.mList = list;
        inflater =  LayoutInflater.from(context);
        this.mInterfaceImpl = interfaceImpl;
    }

    public interface CallBackInterface{
        /**
         * @param position : 所回复的评论position
         * @param list : 评论列表
         */
        void callBack(int position, List<Comment> list);
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        CallBackActivityAdapterHolder holder;
        if(convertView == null) {
            holder = new CallBackActivityAdapterHolder();
            convertView = inflater.inflate(R.layout.item_call_back_activity, null);
            rootView = inflater.inflate(R.layout.activity_call_back, null);
            holder.ivCallBack = (ImageView) convertView.findViewById(R.id.acti_call_back_item_callback);
            holder.tvAttrs = (TextView) convertView.findViewById(R.id.acti_call_back_item_attrs);
            holder.tvContent = (TextView) convertView.findViewById(R.id.acti_call_back_item_content);
            holder.tvFloors = (TextView) convertView.findViewById(R.id.acti_call_back_item_floors);
            holder.tvTime = (TextView) convertView.findViewById(R.id.acti_call_back_item_time);
            holder.llCallBackComment = (LinearLayout) convertView.findViewById(R.id.acti_call_back_item_ll_callback);
            holder.tvCallBackComment = (TextView) convertView.findViewById(R.id.acti_call_back_item_callback_comment);
            holder.tvUserName = (TextView) convertView.findViewById(R.id.acti_call_back_item_username);
            mLlBottom = (LinearLayout) rootView.findViewById(R.id.acti_call_back_ll_bottom);
            mLlSubmit = (LinearLayout) rootView.findViewById(R.id.acti_call_back_ll_bottom_submit);
            mEtCallBack = (EditText) rootView.findViewById(R.id.acti_call_back_et);
            mTvSubmit = (TextView) rootView.findViewById(R.id.acti_call_back_tv_submit);


            convertView.setTag(holder);
        }else{
            holder = (CallBackActivityAdapterHolder) convertView.getTag();
        }
        holder.tvUserName.setText(mList.get(position).getOwnerName());
        if(mList.get(position).getOwnerSex().equals("male")){
            holder.tvUserName.setBackgroundResource(R.drawable.male_user_shape);
        }else{
            holder.tvUserName.setBackgroundResource(R.drawable.female_shape);
        }
//        String time = Tools.formatTime(Long.parseLong(mList.get(position).getTime()));
        String time = TimeUtils.getDescriptionTimeFromTimestamp(Long.parseLong(mList.get(position).getTime()));
        holder.tvTime.setText(time);
        holder.tvFloors.setText((position + 1) + "楼");
        holder.tvContent.setText(mList.get(position).getContent());
        holder.tvAttrs.setText(mList.get(position).getOwnerAttrs());
//        Toast.makeText(context,"position:" +(position) + "===callbackCommentId:" + mList.get(position).getCallBackCommentId(),Toast.LENGTH_SHORT).show();
        if (!Tools.isEmpty(mList.get(position).getCallBackCommentId())) {
//            Toast.makeText(context,"position:" +(position) + ";callbackCommentId:" + mList.get(position).getCallBackCommentId(),Toast.LENGTH_SHORT).show();
            Log.e(TAG, "position:" +(position) + ";callbackCommentId:" + mList.get(position).getCallBackCommentId());
            holder.llCallBackComment.setVisibility(View.VISIBLE);
            String content = mList.get(position).getCallBackCommentContent();
            holder.tvCallBackComment.setText("回复"+mList.get(position).getCallBackFloor()+"楼:" + cropText(content));
        }else{
            holder.llCallBackComment.setVisibility(View.GONE);
        }
        holder.tvFloors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, ChatActivity.class);
                intent.putExtra("ownerId", mList.get(position).getOwnerId());
                context.startActivity(intent);
            }
        });
        holder.ivCallBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInterfaceImpl.callBack(position,mList);
            }
        });
        return convertView;
    }

    private String cropText(String content) {
        if(content.length() > 15){
            String result = content.substring(0,15);
            return result + "...";
        }else{
            return content;
        }
    }

    class CallBackActivityAdapterHolder{
        public TextView tvFloors;
        public TextView tvUserName;
        public ImageView ivCallBack;
        public TextView tvContent;
        public TextView tvAttrs;
        public TextView tvTime;
        private LinearLayout llCallBackComment;
        private TextView tvCallBackComment;
    }
}
