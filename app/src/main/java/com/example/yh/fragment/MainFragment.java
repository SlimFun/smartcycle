package com.example.yh.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ecloud.pulltozoomview.PullToZoomListViewEx;
import com.example.yh.activity.ActionDetailActivity;
import com.example.yh.activity.CallBackActivity;
import com.example.yh.activity.LoginActivity;
import com.example.yh.activity.MainActivity;
import com.example.yh.activity.RegisterActivity;
import com.example.yh.activity.SendDynamicInfoActivity;
import com.example.yh.adapter.MainFragmentAdapter;
import com.example.yh.context.MainApplication;
import com.example.yh.data.Constants;
import com.example.yh.data.DynamicInfo;
import com.example.yh.data.User;
import com.example.yh.http.HttpHelper;
import com.example.yh.netword.RequestManager;
import com.example.yh.network.image.ImageCacheManager;
import com.example.yh.smartcycle.R;
import com.example.yh.util.Tools;
import com.example.yh.view.CircleImageView;
import com.example.yh.view.LoadingFooter;
import com.example.yh.view.NetworkCircleImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;

import yalantis.com.sidemenu.interfaces.ScreenShotable;

/**
 * Created by yh on 2015/2/10.
 */
public class MainFragment extends Fragment implements ScreenShotable, TextView.OnClickListener,PullToZoomListViewEx.RefreshInterface {

    private PullToZoomListViewEx listView;
    private TextView mTvLogin;
    private TextView mTvRegister;
    private NetworkCircleImageView mFace;
    private File mTempFile;
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
    private static final int PHOTO_REQUEST_CAMERA = 100;
    private static final int PHOTO_REQUEST_GALLERY = 200;
    private static final int PHOTO_REQUEST_CUT = 300;
    private static final int MSG_SUC = 999;
    private static final int MSG_FL = 888;
    private RelativeLayout mRlEditDynamicInfo;
    private Bitmap mUserBitmap;
    private LinearLayout mLinearLayout;
    private TextView mTvUsername;
    private User user;
    private LoadingFooter mLoadingFooter;
    private int pageNum = 1;
    private View emptyView;
    private ListView mListView;
    private MainFragmentAdapter mAdapter;
    private View rootView;
    private static final String TAG = "MainFragemntTAG";
    private List<DynamicInfo> mItemData = new ArrayList<>();
    private RequestQueue mRequestQueue = RequestManager.getRequestQueue();
    private static String uploadFaceUrl = Constants.BASE_URL + "UploadFaceUrlServlet";
    private static String getDynamicInfoListUrl = Constants.BASE_URL + "DynamicInfoListServlet?pageNum=%s&userId=%s";
    private static String deleteDynamicInfoUrl = Constants.BASE_URL + "DeleteDynamicInfoServlet?infoId=%s";
    private State state = State.Idle;
    private Context mContext;
    private ImageView mIvEditInfo;
    private enum State{
        Loading,Idle
    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == MSG_SUC){
                JSONObject result = (JSONObject) msg.obj;
                int status = result.optInt("status");
                if(status == 0){
                    user.setFaceUrl(result.optString("faceUrl"));
                    Toast.makeText(getActivity(),"上传成功",Toast.LENGTH_SHORT).show();
                    mAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(getActivity(),"上传失败",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getActivity(),"上传失败",Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        User.init(getActivity());
        user = User.getInstance();
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mLoadingFooter = new LoadingFooter(getActivity());
        mContext = getActivity();
        initData();
        setView(rootView);
        setListView();
        initUserDate();
        mFace.setDefaultImageResId(R.drawable.img_error);
        setOnClickListener();
        refreshDynamicInfo();
        return rootView;
    }

    private void initData() {
        mItemData.clear();
        if (MainActivity.pendingLoading) {
            Log.e(TAG, "initData");
            getDynamicInfos(pageNum);
        }

    }

    private void setListView() {
        mListView = listView.getListView(this);

//        mListView.setEmptyView(emptyView);
        emptyView.setVisibility(View.GONE);

        mListView.addFooterView(mLoadingFooter.getView());
        mLoadingFooter.setState(LoadingFooter.State.Idle);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(mLoadingFooter.getState() == LoadingFooter.State.Loading
                        || mLoadingFooter.getState() == LoadingFooter.State.TheEnd){
                    return;
                }
                if(firstVisibleItem + visibleItemCount >= totalItemCount
                        && totalItemCount != 0
                        && totalItemCount != mListView.getHeaderViewsCount() + mListView.getFooterViewsCount()
                        && mListView.getCount() > 0){
                    loadNextPage();
                }
            }
        });
    }

    private void loadNextPage() {
        getDynamicInfos(pageNum);
    }

    private void initUserDate() {
        if(user.isLogin()){
            change2LoginStyle();
        }
    }

    private void refreshDynamicInfo() {
//        JsonObjectRequest request = new JsonObjectRequest();
    }

    private void findViews(View rootView){
        listView = (PullToZoomListViewEx) rootView.findViewById(R.id.fragment_main_listView);
        mTvLogin = (TextView) rootView.findViewById(R.id.tv_login);
        mTvRegister = (TextView) rootView.findViewById(R.id.tv_register);
        mFace = (NetworkCircleImageView) rootView.findViewById(R.id.iv_user_face);
        mLinearLayout = (LinearLayout) rootView.findViewById(R.id.ll_action_button);
        mTvUsername = (TextView) rootView.findViewById(R.id.tv_user_name);
        emptyView = rootView.findViewById(R.id.textView_empty);
        mRlEditDynamicInfo = (RelativeLayout) rootView.findViewById(R.id.fragment_main_rl_edti_info);

    }

    private void setOnClickListener() {
        mTvLogin.setOnClickListener(this);
        mTvRegister.setOnClickListener(this);
        mFace.setOnClickListener(this);
        mRlEditDynamicInfo.setOnClickListener(this);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (user == null) {
                    return true;
                }
                final DynamicInfo info = (DynamicInfo) mAdapter.getItem(position);
                if (info.getUserId().equals(user.getId())) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("删除记录")
                            .setMessage("确定要删除吗？")
                            .setPositiveButton("是",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteInfo(info,position);
                                }
                            })
                            .setNegativeButton("否",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            }).create().show();
                }
                return false;
            }
        });
    }

    private void deleteInfo(DynamicInfo info, final int position) {
        String url = String.format(deleteDynamicInfoUrl, info.getId());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        int status = jsonObject.optInt("status");
                        if (status == 0) {
                            mItemData.remove(position - 1);
                            mListView.setSelection(position - 1);
                            Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getActivity(),"删除失败",Toast.LENGTH_SHORT).show();
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(),"删除失败",Toast.LENGTH_SHORT).show();
                    }
                });
        mRequestQueue.add(jsonObjectRequest);
    }

    private void setView(View rootView) {

        findViews(rootView);
        mAdapter = new MainFragmentAdapter(getActivity(),mItemData,new MainFragmentAdapter.OnDynamicInfoItemClickListener() {
            @Override
            public void onTvMoreClick(View view, int position) {
                Intent intent = new Intent();
                intent.setClass(mContext, ActionDetailActivity.class);
                intent.putExtra("infoId", mItemData.get(position).getId());
                mContext.startActivity(intent);
            }

            @Override
            public void onllSuportsClick(View view, int position) {

            }

            @Override
            public void onllCallBackClick(View view, int position) {
                Intent intent = new Intent();
                intent.putExtra("infoOwnerId", mItemData.get(position).getUserId());
                intent.putExtra("userName", mItemData.get(position).getUsername());
                intent.putExtra("userFace", mItemData.get(position).getUserFace());
                intent.putExtra("textContent", mItemData.get(position).getContent());
                intent.putExtra("imageContent", mItemData.get(position).getImageUrl());
                intent.putExtra("sex", mItemData.get(position).getSex());
                intent.putExtra("attrs", mItemData.get(position).getAttrs());
                intent.putExtra("supports", mItemData.get(position).getSupports());
                intent.putExtra("infoId",mItemData.get(position).getId());
                intent.putExtra("haveSupports",mItemData.get(position).isHaveSupports());
                int[] startingLocation = new int[2];
                view.getLocationOnScreen(startingLocation);
                intent.putExtra(CallBackActivity.DRAWING_START_LOCATION, startingLocation[1]);
                intent.setClass(mContext, CallBackActivity.class);
                mContext.startActivity(intent);
                getActivity().overridePendingTransition(0, 0);
            }

            @Override
            public void onllSendMessageClick(View view, int position) {

            }
        });
        listView.setAdapter(mAdapter);
        try {
            mFace.setImageUrl(user.getFaceUrl(), ImageCacheManager.getInstance().getImageLoader());
        } catch (NullPointerException e) {
            Toast.makeText(getActivity(),"头像加载失败",Toast.LENGTH_SHORT).show();
        }
        AbsListView.LayoutParams localObject = new AbsListView.LayoutParams(MainApplication.ScreenWidth, (int) (9.0F * (MainApplication.ScreenWidth / 16.0F)));
        listView.setHeaderLayoutParams(localObject);

    }

    @Override
    public void takeScreenShot() {
        if(getFragmentManager() != null)
             getFragmentManager().beginTransaction().replace(R.id.content_frame, this).commit();
    }

    @Override
    public Bitmap getBitmap() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_login:
                Intent loginIntent = new Intent();
                loginIntent.setClass(getActivity(), LoginActivity.class);
                startActivityForResult(loginIntent,LoginActivity.LOGIN);
                break;
            case R.id.tv_register:
                Intent registerIntent = new Intent();
                registerIntent.setClass(getActivity(), RegisterActivity.class);
                startActivityForResult(registerIntent,RegisterActivity.REGISTER);
                break;
            case R.id.iv_user_face:
                setFaceImageView();
                break;
            case R.id.fragment_main_rl_edti_info:
                if (user.isLogin()) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), SendDynamicInfoActivity.class);
                    int[] startingLoaction = new int[2];
                    mRlEditDynamicInfo.getLocationOnScreen(startingLoaction);
                    startingLoaction[0] += mRlEditDynamicInfo.getWidth() / 2;
                    intent.putExtra(SendDynamicInfoActivity.STARTING_LOCATION, startingLoaction);
                    startActivityForResult(intent, SendDynamicInfoActivity.SEND_INFO);
                }else{
                    Toast.makeText(getActivity(),"请先登录",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void setFaceImageView() {
        Dialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle("从本地获取头像")
                .setMessage("选择获取图片方式")
                .setPositiveButton("相册",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gallery();
                    }
                })
                .setNegativeButton("相机",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        camera();
                    }
                })
                .create();
        alertDialog.show();
    }

    private void camera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        if(hasSDCard()) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME)));
        }
        startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
    }

    private boolean hasSDCard() {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return true;
        }else{
            return false;
        }
    }

    private void gallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,PHOTO_REQUEST_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PHOTO_REQUEST_GALLERY){
            if(data != null){
                Uri uri = data.getData();
                crop(uri);
            }
        }else if(requestCode == PHOTO_REQUEST_CAMERA) {
            if (hasSDCard()) {
                mTempFile = new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME);
                crop(Uri.fromFile(mTempFile));
            }else{
                Toast.makeText(getActivity(),"未找到储存卡，无法存储照片",Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == PHOTO_REQUEST_CUT) {
            if(data != null){
                mUserBitmap = data.getParcelableExtra("data");
                uploadFace();
                mFace.setImageBitmap(mUserBitmap);
            }
        }else if(requestCode == LoginActivity.LOGIN || requestCode == RegisterActivity.REGISTER) {
            if (resultCode == LoginActivity.SUCCESS) {
                change2LoginStyle();
            }
        }else if (requestCode == SendDynamicInfoActivity.SEND_INFO) {
            if (resultCode == SendDynamicInfoActivity.SUCC) {
                refresh();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadFace() {
        final ProgressDialog dialog = ProgressDialog.show(getActivity(),"提交数据","正在提交...");
        String faceString = "";
        faceString = Tools.bitmap2String(mUserBitmap);
        final String param = new StringBuffer()
                .append("userId=")
                .append(user.getId())
                .append("&face=")
                .append(faceString)
                .toString();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String json = HttpHelper.sendPost(uploadFaceUrl, param);
                JSONObject result;
                dialog.dismiss();
                Message msg = new Message();
                try {
                    result = new JSONObject(json);
                    msg.obj = result;
                    msg.what = MSG_SUC;
                    handler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                    msg.what = MSG_FL;
                    handler.sendMessage(msg);
                }
            }
        });
    }

    private void change2LoginStyle() {
        mLinearLayout.setVisibility(View.GONE);
        mTvUsername.setText(user.getUsername());
        mTvUsername.setVisibility(View.VISIBLE);

//        if(user.getFaceUrl()!=null)
//        Log.e(TAG,user.getFaceUrl());
//        if (!Tools.isEmpty(user.getFaceUrl())) {
        try {
            mFace.setImageUrl(user.getFaceUrl(), ImageCacheManager.getInstance().getImageLoader());
        } catch (NullPointerException e) {
            Toast.makeText(getActivity(),"头像加载失败",Toast.LENGTH_SHORT).show();
        }

//        }
    }

    private void crop(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");

        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);

        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    public void getDynamicInfos(final int page) {
        Log.e(TAG, "getDynamicInfo page :" + page);
        if (state != State.Idle) {
            return;
        }
        state = State.Loading;
        String url = String.format(getDynamicInfoListUrl, page + "", user.getId());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.e(TAG, jsonObject.toString());
                        int status = jsonObject.optInt("status");
                        if (status == 0) {
                            pageNum++;
                            JSONArray infosArray = null;
                            try {
                                infosArray = (JSONArray) jsonObject.get("infos");
                                if (infosArray.length() == 0) {
                                    if (page == 1) {
//                                        emptyView.setVisibility(View.VISIBLE);
                                        listView.addView(emptyView);
                                        emptyView.setVisibility(View.VISIBLE);
                                    } else {
//                                        Toast.makeText(getActivity(),"已经没有更多了",Toast.LENGTH_SHORT).show();
                                    }
                                    mLoadingFooter.setState(LoadingFooter.State.TheEnd);
                                    state = State.Idle;
                                    return;
                                }
                                for (int i = 0; i < infosArray.length(); i++) {
                                    JSONObject object = (JSONObject) infosArray.get(i);
                                    String userId = object.getString("userId");
                                    String id = object.getString("id");
                                    String imageUrl = object.getString("imageUrl");
                                    int supports = object.getInt("supports");
                                    String time = object.getString("time");
                                    String content = object.getString("content");
                                    String username = object.optString("username");
                                    String sex = object.optString("sex");
                                    String userFace = object.optString("userFace");
                                    String attrs = object.optString("attrs");
                                    int fontSize = object.optInt("fontSize");
                                    String fontType = object.optString("fontType");
                                    int fontColor = object.optInt("fontColor");
                                    boolean haveSupported = object.optBoolean("haveSupported");
                                    DynamicInfo info = new DynamicInfo(id, userId, imageUrl, supports, content, time, username, sex, attrs, userFace, fontType, fontSize, fontColor, haveSupported);

                                    mItemData.add(info);
                                }
                                mLoadingFooter.setState(LoadingFooter.State.Idle, 3000);
                                mAdapter.notifyDataSetChanged();
                                state = State.Idle;
                            } catch (JSONException e) {
                                e.printStackTrace();
                                state = State.Idle;
                            }

                        } else {
                            Toast.makeText(getActivity(), "加载失败，可能是服务器端出错了", Toast.LENGTH_SHORT).show();
                            if (page == 1) {
//                                emptyView.setVisibility(View.VISIBLE);
                            }
                            mLoadingFooter.setState(LoadingFooter.State.TheEnd);
                            state = State.Idle;
                            return;
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, volleyError.toString());
                        Toast.makeText(getActivity(), volleyError.toString(), Toast.LENGTH_LONG).show();
                        if (page == 1) {
//                            emptyView.setVisibility(View.VISIBLE);
                            mLoadingFooter.setState(LoadingFooter.State.TheEnd);
                        }
                        mLoadingFooter.setState(LoadingFooter.State.Idle, 3000);
                        state = State.Idle;
                    }
                });
        mRequestQueue.add(jsonObjectRequest);
    }

    @Override
    public void refresh() {
        Toast.makeText(getActivity(),state+"",Toast.LENGTH_SHORT).show();
        if(state == State.Idle){
            Toast.makeText(getActivity(),"refresh",Toast.LENGTH_SHORT).show();
            mItemData.clear();
            getDynamicInfos(1);
            pageNum = 1;
            mLoadingFooter.setState(LoadingFooter.State.Idle);
        }else{
            return;
        }
    }
}
