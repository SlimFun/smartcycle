package com.example.yh.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.yh.activity.ChatActivity;
import com.example.yh.activity.MainActivity;
import com.example.yh.adapter.FriendsFragmentAdapter;
import com.example.yh.data.Constants;
import com.example.yh.data.Friend;
import com.example.yh.data.User;
import com.example.yh.netword.RequestManager;
import com.example.yh.smartcycle.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yalantis.com.sidemenu.interfaces.ScreenShotable;

/**
 * Created by yh on 2015/2/11.
 */
public class FriendsFragment extends Fragment implements ScreenShotable {

    private View mRootView;
    private ListView mListView;
    private List mListViewData;
    private static String getFriendsUrl = Constants.BASE_URL + "GetFriendsServlet?userId=%s&page=%s";
    private FriendsFragmentAdapter mAdapter;
    private RequestQueue mQueue;
    private User mUser;
    private View emptyView;
    

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_friends, container, false);
        init();
        findViews();
        setViews();
        return mRootView;
    }

    private void init() {
        User.init(getActivity());
        mUser = User.getInstance();
        mQueue = RequestManager.getRequestQueue();
    }

    private void setViews() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.setMargins(0,Constants.ACTION_BAR_HEIGHT,0,0);
        mListView.setLayoutParams(lp);
        mListViewData = new ArrayList();
        mAdapter = new FriendsFragmentAdapter(mListViewData, getActivity());
        TextView emptyViewTv = (TextView) emptyView.findViewById(R.id.emptyView_textView);
        emptyViewTv.setText("一个朋友也没有");
        mListView.setEmptyView(emptyView);
        mListView.setAdapter(mAdapter);
        getFriends(1);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Friend friend = (Friend) mAdapter.getItem(position);
                Intent intent = new Intent();
                intent.setClass(getActivity(), ChatActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getFriends(final int page) {
        String url = String.format(getFriendsUrl, mUser.getId(), page);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        int status = jsonObject.optInt("status");
                        if (status == 0) {
                            JSONArray jsonArray = jsonObject.optJSONArray("users");
                            Toast.makeText(getActivity(),"length:"+jsonArray.length(),Toast.LENGTH_SHORT).show();
                            if (jsonArray.length() == 0 && page == 1) {
                                emptyView.setVisibility(View.VISIBLE);
                            }
                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonObject = (JSONObject) jsonArray.opt(i);
                                String id = jsonObject.optString("id");
                                String username = jsonObject.optString("username");
                                String attrs = jsonObject.optString("attrs");
                                String sex = jsonObject.optString("sex");
                                String face = jsonObject.optString("face");
                                Friend friend = new Friend(id, username, attrs, sex, face);
                                Toast.makeText(getActivity(),username,Toast.LENGTH_SHORT).show();
                                mListViewData.add(friend);
                            }
                            mAdapter.notifyDataSetChanged();
                        }else {
                            if (page == 1) {
                                emptyView.setVisibility(View.VISIBLE);
                            }
                            Toast.makeText(getActivity(), "服务器出错了", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if(page == 1){
                            emptyView.setVisibility(View.VISIBLE);
                        }
                        Toast.makeText(getActivity(),"解析错误",Toast.LENGTH_SHORT).show();
                    }
                });
        mQueue.add(jsonObjectRequest);
    }

    private void findViews() {
        mListView = (ListView) mRootView.findViewById(R.id.fragment_friends_listView);
        emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.empty_view, null);
    }

    @Override
    public void takeScreenShot() {

    }

    @Override
    public Bitmap getBitmap() {
        return null;
    }
}
