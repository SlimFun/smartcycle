package com.example.yh.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.example.yh.activity.ActionDetailActivity;
import com.example.yh.activity.CallBackActivity;
import com.example.yh.data.Constants;
import com.example.yh.data.DynamicInfo;
import com.example.yh.data.User;
import com.example.yh.netword.RequestManager;
import com.example.yh.network.image.ImageCacheManager;
import com.example.yh.smartcycle.R;
import com.example.yh.util.TimeUtils;
import com.example.yh.util.Tools;
import com.example.yh.view.NetworkCircleImageView;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by yh on 2015/2/12.
 */
public class MainFragmentAdapter extends BaseAdapter implements View.OnClickListener{

    private static final String TAG = "MainFragmentAdapter";
    private LayoutInflater mInflater;
    private List<DynamicInfo> mList;
    private Context context;
    private final int SUC = 1;
    private final int FL = 0;
    private OnDynamicInfoItemClickListener itemClickListener;
    private RequestQueue mQueue = RequestManager.getRequestQueue();
    private User user;
    private final String updateSupportsUrl = Constants.BASE_URL + "UpdataSupportsServlet?infoId=%s&userId=%s";
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SUC) {
//                Bundle bundle = msg.getData();
//                int supprots = bundle.getInt("supports");
//                MainFragmentViewHolder holder = (MainFragmentViewHolder) msg.obj;
//                holder.supports.setText(supprots + "");
//                holder.ivSupports.setImageResource(R.drawable.supports_selected);
            }else{
                Toast.makeText(context,"服务器端出问题了",Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };

    public MainFragmentAdapter(Context context,List<DynamicInfo> infos,OnDynamicInfoItemClickListener itemClickListener) {
        User.init(context);
        user = User.getInstance();
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mList = infos;
        this.itemClickListener = itemClickListener;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position - 1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        long startTime = System.currentTimeMillis();
        final MainFragmentViewHolder holder;

        if(convertView == null){
            convertView = mInflater.inflate(R.layout.item_mainfragment_listview,null);
            holder = new MainFragmentViewHolder();
            holder.username = (TextView) convertView.findViewById(R.id.tv_item_username);
            holder.attrs = (TextView) convertView.findViewById(R.id.tv_item_attrs);
            holder.time = (TextView) convertView.findViewById(R.id.tv_item_time);
            holder.imageContent = (NetworkImageView) convertView.findViewById(R.id.iv_item_content);
            holder.textContent = (TextView) convertView.findViewById(R.id.tv_item_content);
            holder.userFace = (NetworkCircleImageView) convertView.findViewById(R.id.iv_item_face);
            holder.tsSupports = (TextSwitcher) convertView.findViewById(R.id.fragment_main_ts_support);
            holder.llCallBack = (LinearLayout) convertView.findViewById(R.id.ll_item_call_back);
            holder.llSendMessage = (LinearLayout) convertView.findViewById(R.id.ll_item_send_message);
            holder.llSupports = (LinearLayout) convertView.findViewById(R.id.ll_item_supports);
            holder.tvSupportsNum = (TextView) convertView.findViewById(R.id.fragment_main_textview_supports);
            holder.ivSendMessage = (ImageView) convertView.findViewById(R.id.iv_item_message);
            holder.ivSupports = (ImageView) convertView.findViewById(R.id.iv_item_supports);
            holder.tvMore = (TextView) convertView.findViewById(R.id.item_mainfragment_tv_more);
            convertView.setTag(holder);
        }else{
            holder = (MainFragmentViewHolder) convertView.getTag();
        }
        holder.username.setText(mList.get(position).getUsername());
        if(mList.get(position).getSex().equals("male")){
            holder.username.setBackgroundResource(R.drawable.male_user_shape);
            holder.ivSendMessage.setImageResource(R.drawable.message_male);
        }else{
            holder.username.setBackgroundResource(R.drawable.female_shape);
            holder.ivSendMessage.setImageResource(R.drawable.message_female);
        }
        holder.attrs.setText(mList.get(position).getAttrs());
        long time  = Long.parseLong(mList.get(position).getTime());
        holder.time.setText(TimeUtils.getDescriptionTimeFromTimestamp(time));
//        holder.tsSupports.setText(mList.get(position).getSupports() + "");
        holder.tvSupportsNum.setText(mList.get(position).getSupports() + "");
        holder.userFace.setErrorImageResId(R.drawable.img_error);
        holder.imageContent.setErrorImageResId(R.drawable.img_error);
        Log.e(TAG, "faceUrl : " + mList.get(position).getUserFace());
        holder.userFace.setImageUrl(mList.get(position).getUserFace(),ImageCacheManager.getInstance().getImageLoader());
        holder.imageContent.setImageUrl(mList.get(position).getImageUrl(), ImageCacheManager.getInstance().getImageLoader());
        holder.textContent.setText(mList.get(position).getContent());
//        String fontType = mList.get(position).getFontType();
//        if (fontType != null && !fontType.trim().equals("")) {
//            AssetManager mgr = context.getAssets();
//            Typeface tf = Typeface.createFromAsset(mgr, fontType);
//            holder.textContent.setTypeface(tf);
//        }
        if (mList.get(position).isHaveSupports()) {
            holder.ivSupports.setImageResource(R.drawable.supports_selected);
        }else{
            holder.ivSupports.setImageResource(R.drawable.supports_unselected);
        }
        holder.textContent.setTextColor(mList.get(position).getFontColor());
        holder.textContent.setTextSize(mList.get(position).getFontSize());

        holder.tvMore.setTag(position);
        holder.llSupports.setTag(position);
        holder.llSendMessage.setTag(position);
        holder.llCallBack.setTag(position);

        holder.tvMore.setOnClickListener(this);
        holder.llSupports.setOnClickListener(this);
        holder.llCallBack.setOnClickListener(this);
        holder.llSendMessage.setOnClickListener(this);

//        holder.tvMore.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        holder.llSupports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (v.getId() == holder.ivSupports.getId()) {
//                    if (holder.ivSupports.getImageAlpha() == R.drawable.supports_selected) {
//                        return;
//                    } else {
                if(holder.ivSupports.getDrawable().getConstantState().equals(context.getResources().getDrawable(R.drawable.supports_selected).getConstantState())){
                    return;
                }
                     Toast.makeText(context,"support",Toast.LENGTH_SHORT).show();
                        String strSupports = holder.tvSupportsNum.getText().toString();
                         int intSupports;
                        if (Tools.isEmpty(strSupports)) {
                            intSupports = 0;
                        }else{
                            intSupports = Integer.parseInt(strSupports);
                         }
                        holder.ivSupports.setImageResource(R.drawable.supports_selected);
                        holder.tsSupports.setText((++intSupports) + "");
                        holder.tvSupportsNum.setText(intSupports + "");
                        String url = String.format(updateSupportsUrl, mList.get(position).getId(),user.getId());
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject jsonObject) {
                                        Message msg = new Message();
                                        int statue = jsonObject.optInt("status");
                                        if (statue == 0) {
                                            msg.what = SUC;
                                            msg.obj = holder;
                                            Bundle bundle = new Bundle();
                                            bundle.putInt("supports", jsonObject.optInt("supports"));
                                            msg.setData(bundle);
                                        } else {
                                            msg.what = FL;
                                        }
                                        handler.sendMessage(msg);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {
                                        Toast.makeText(context, "数据解析错误", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(context, volleyError.toString(), Toast.LENGTH_LONG).show();
                                    }
                                });
                        mQueue.add(jsonObjectRequest);
//                    }
                }
//            }
        });
//
//        holder.llCallBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        long endTime = System.currentTimeMillis();
        Log.e(TAG, (endTime - startTime) + "秒" + position);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.item_mainfragment_tv_more:
                itemClickListener.onTvMoreClick(v, (Integer) v.getTag());
                break;
            case R.id.ll_item_supports:
                itemClickListener.onllSuportsClick(v, (Integer) v.getTag());
                break;
            case R.id.ll_item_call_back:
                itemClickListener.onllCallBackClick(v, (Integer) v.getTag());
                break;
            case R.id.ll_item_send_message:
                itemClickListener.onllSendMessageClick(v, (Integer) v.getTag());
                break;
        }
    }

    public interface OnDynamicInfoItemClickListener{
        public void onTvMoreClick(View view,int position);

        public void onllSuportsClick(View view,int position);

        public void onllCallBackClick(View view, int position);

        public void onllSendMessageClick(View view, int position);
    }

    private class MainFragmentViewHolder{
        TextView username;
        TextView attrs;
        TextView time;
        NetworkCircleImageView userFace;
        NetworkImageView imageContent;
        TextView textContent;
        TextSwitcher tsSupports;
        TextView tvSupportsNum;
        ImageView ivSendMessage;
        ImageView ivSupports;
        LinearLayout llSendMessage;
        LinearLayout llCallBack;
        LinearLayout llSupports;
        TextView tvMore;
    }
}
