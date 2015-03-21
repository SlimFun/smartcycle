package com.example.yh.activity;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.example.yh.adapter.CallBackActivityAdapter;
import com.example.yh.context.MainApplication;
import com.example.yh.data.Comment;
import com.example.yh.data.Constants;
import com.example.yh.data.User;
import com.example.yh.netword.RequestManager;
import com.example.yh.network.image.ImageCacheManager;
import com.example.yh.smartcycle.R;
import com.example.yh.util.Tools;
import com.example.yh.view.LoadingFooter;
import com.example.yh.view.NetworkCircleImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class CallBackActivity extends ActionBarActivity{

    private static final String TAG = "CallBackActivity";
    private String infoOwnerId;
    private String userName;
    private String userFace;
    private String sex;
    private String attrs;
    private String imageContent;
    private String textContent;
    private String infoId;
    private int supports;
    private TextView mTvUserName;
    private TextView mTvAttrs;
    private View mHeaderView;
    private NetworkImageView mImageContent;
    private NetworkImageView imageView;
    private NetworkCircleImageView mUserFace;
    private TextView mTvAddFriend;
    private TextView mTvTextContent;
    private TextView mTvActionBarUserName;
    private ImageView mIvActionBarSex;
    private ListView mListView;
    private CallBackActivityAdapter mAdapter;
//    private RecyclerView mListView;
    private ImageView mIvBack;
    private View emptyView;
    private RelativeLayout mRlSendMessage;
    private RelativeLayout mRlCallBack;
    private RelativeLayout mRlSupports;
    private LinearLayout mLlBottom;
    private LinearLayout mLlSubmit;
    private TextView mTvSubmit;
    private EditText mEtCallBack;
    private TextView mTvSupports;
    private TextSwitcher mTsSupports;
    private ImageView mIvSupports;
    private boolean isFriend;
    private boolean haveSupports;

    private final RequestQueue mQueue = RequestManager.getRequestQueue();
    private User user;
//    private CallBackActivityAdapter mAdapter;
    private int pageNum = 0;
    private LoadingFooter mLoadingFooter;
    private List mListData = new ArrayList();
    private static String getInfoCommentsUrl = Constants.BASE_URL + "GetInfoCommentsServlet?pageNum=%s&infoId=%s&infoOwnerId=%s&userId=%s";
    private static String addCommentUrl = Constants.BASE_URL + "AddCommentServlet?ownerId=%s&content=%s&infoId=%s";
    private static String addFriendUrl = Constants.BASE_URL + "AddFriendServlet?userId=%s&friendId=%s";
    private final String updateSupportsUrl = Constants.BASE_URL + "UpdataSupportsServlet?infoId=%s&userId=%s";
    private static String callBackUrl = Constants.BASE_URL + "CallBackServlet?content=%s&callBackCommentId=%s&ownerId=%s&callBackFloor=%s&infoId=%s";

    private static final int SUC = 1;
    private static final int FL = 2;

    public static final String DRAWING_START_LOCATION = "drawing_start_location";
    private int startingLocation;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == FL) {
                Toast.makeText(CallBackActivity.this,"服务器端出问题了",Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_back);
        Intent intent = getIntent();
        getDataFromIntent(intent);
        User.init(getApplicationContext());
        user = User.getInstance();
        findViews();
        setViews();
        setListeners();
        if (savedInstanceState == null) {
            mListView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mListView.getViewTreeObserver().removeOnPreDrawListener(this);
                    startIntroAnimation();
                    return true;
                }
            });
        }
    }

    private void startIntroAnimation() {
        mListView.setScaleY(0.1f);
        mListView.setPivotY(startingLocation);
        mLlBottom.setTranslationY(100);

        mListView.animate()
                .scaleY(1)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        animateContent();
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();
    }

    private void animateContent() {
        mAdapter.notifyDataSetChanged();
        mLlBottom.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(200)
                .start();
    }

    private void hideSoftInputView() {
        InputMethodManager manager = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null) {
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    private void setListeners() {
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallBackActivity.this.finish();
            }
        });
        mRlCallBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLlBottom.setVisibility(View.GONE);
                mLlSubmit.setVisibility(View.VISIBLE);
                mEtCallBack.setFocusableInTouchMode(true);
                mEtCallBack.setHint("发表评论");
            }
        });

        mRlSupports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIvSupports.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.supports_selected).getConstantState())) {
                    return;
                }
                String strSupports = mTvSupports.getText().toString();
                int intSupports;
                if (Tools.isEmpty(strSupports)) {
                    intSupports = 0;
                }else{
                    intSupports = Integer.parseInt(strSupports);
                }
                mIvSupports.setImageResource(R.drawable.supports_selected);
                mTsSupports.setText((++intSupports) + "");
                mTvSupports.setText(intSupports + "");
                String url = String.format(updateSupportsUrl, infoId,user.getId());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                Message msg = new Message();
                                int statue = jsonObject.optInt("status");
                                if (statue == 0) {
                                    msg.what = SUC;
                                } else {
                                    msg.what = FL;
                                }
                                handler.sendMessage(msg);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Toast.makeText(CallBackActivity.this, "数据解析错误", Toast.LENGTH_SHORT).show();
                                Toast.makeText(CallBackActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                mQueue.add(jsonObjectRequest);
            }
        });

        mLlSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String callBackContent = mEtCallBack.getText().toString();
                if (Tools.isEmpty(callBackContent)) {
                    mTvSubmit.setAnimation(AnimationUtils.loadAnimation(CallBackActivity.this, R.anim.shake_error));
                    return;
                }
                final ProgressDialog dialog = ProgressDialog.show(CallBackActivity.this, "发表评论", "正在发送");
                try {
                    callBackContent = URLEncoder.encode(callBackContent, "UTF-8");
                    String url = String.format(addCommentUrl, user.getId(), callBackContent, infoId);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject jsonObject) {
                                    int status = jsonObject.optInt("status");
                                    if (status == 0) {
                                        Toast.makeText(CallBackActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                                        mListData.clear();
                                        getComments(pageNum + 1);
                                        mEtCallBack.setText("");
                                        mEtCallBack.setHint("");
                                        mLlBottom.setVisibility(View.VISIBLE);
                                        mLlSubmit.setVisibility(View.GONE);
//                                        mListView.setSelection(mAdapter.getCount() - 1);
                                    } else {
                                        Toast.makeText(CallBackActivity.this, "发送失败，可能是服务器端出错了", Toast.LENGTH_SHORT).show();
                                    }
                                    dialog.dismiss();
                                    hideSoftInputView();
                                    mAdapter.notifyDataSetChanged();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    dialog.dismiss();
                                    Toast.makeText(CallBackActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(CallBackActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                                    hideSoftInputView();
                                }
                            });
                    mQueue.add(jsonObjectRequest);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        mTvAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = String.format(addFriendUrl, user.getId(), infoOwnerId);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                int status = jsonObject.optInt("status");
                                if (status == 0) {
                                    Toast.makeText(CallBackActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(CallBackActivity.this,"添加成功,服务器端出问题了",Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Toast.makeText(CallBackActivity.this,"添加失败",Toast.LENGTH_SHORT).show();
                            }
                        });
                mQueue.add(jsonObjectRequest);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(mLlBottom.getVisibility() == View.VISIBLE){
                CallBackActivity.this.finish();
            }else if(mLlBottom.getVisibility() == View.GONE) {
//                mEtCallBack.setText("");
                mEtCallBack.setHint("");
                mLlBottom.setVisibility(View.VISIBLE);
                mLlSubmit.setVisibility(View.GONE);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setViews() {
        mTvActionBarUserName.setText(userName);
        mImageContent.setImageUrl(imageContent, ImageCacheManager.getInstance().getImageLoader());
        mUserFace.setImageUrl(userFace, ImageCacheManager.getInstance().getImageLoader());
        mUserFace.setDefaultImageResId(R.drawable.img_error);
        mTvTextContent.setText(textContent);
        if (haveSupports) {
            mIvSupports.setImageResource(R.drawable.supports_selected);
        }
        mTvAttrs.setText(attrs);
        mTsSupports.setText(supports + "");
        mTvSupports.setText(supports+"");
        mTvUserName.setText(userName);

        if(sex.equals("male")){
            mTvUserName.setBackgroundResource(R.drawable.male_user_shape);
            mIvActionBarSex.setImageResource(R.drawable.action_bar_sex_male);
        }else{
            mTvUserName.setBackgroundResource(R.drawable.female_shape);
            mIvActionBarSex.setImageResource(R.drawable.action_bar_sex_female);
        }
        if(isFriend) {
            mTvAddFriend.setVisibility(View.GONE);
            mTvAddFriend.setClickable(false);
        }
        mAdapter = new CallBackActivityAdapter(CallBackActivity.this,mListData, new CallBackActivityAdapter.CallBackInterface() {
            @Override
            public void callBack(final int position, final List<Comment> list) {
                Log.e(TAG, "list.size():" + list.size());
                mLlBottom.setVisibility(View.GONE);
                mLlSubmit.setVisibility(View.VISIBLE);

                mEtCallBack.setHint("回复" + (position+1) + "楼:");

                mLlSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String content = mEtCallBack.getText().toString();
                        try {
                            content = URLEncoder.encode(content, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            Toast.makeText(CallBackActivity.this, "解码失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String url = String.format(callBackUrl, content, list.get(position).getId(), user.getId(), (position + 1) + "", list.get(position).getInfoId());
                        mListData.clear();
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject jsonObject) {
                                        int status = jsonObject.optInt("status");
                                        if (status == 0) {
                                            JSONArray jsonArray = jsonObject.optJSONArray("comments");
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                jsonObject = jsonArray.optJSONObject(i);
                                                String commentOwnerName = jsonObject.optString("userName");
                                                String commentOwnerId = jsonObject.optString("ownerId");
                                                String commentOwnerSex = jsonObject.optString("sex");
                                                String commentOwnerAttrs = jsonObject.optString("attrs");
                                                String commentContent = jsonObject.optString("content");
                                                String infoId = jsonObject.optString("infoId");
                                                String commentId = jsonObject.optString("id");
                                                String comentTime = jsonObject.optString("time");
                                                String callBackCommentId = jsonObject.optString("callBackCommentId");
                                                int callBackFloor = jsonObject.optInt("callBackFloor");
                                                String callBackCommentContent = jsonObject.optString("callBackComment");
                                                Comment comment = new Comment(commentOwnerId, commentId, commentOwnerName, commentOwnerSex, commentOwnerAttrs, comentTime, commentContent, infoId, callBackCommentId, callBackFloor, callBackCommentContent);
                                                mListData.add(comment);
                                            }
                                            mAdapter.notifyDataSetChanged();
                                            Toast.makeText(CallBackActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                                            mEtCallBack.setText("");
                                            mLlSubmit.setVisibility(View.GONE);
                                            mLlBottom.setVisibility(View.VISIBLE);
                                        } else {
                                            Toast.makeText(CallBackActivity.this, "发送失败,可能是服务器端出问题了", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {
                                        Toast.makeText(CallBackActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(CallBackActivity.this, volleyError.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                        mQueue.add(jsonObjectRequest);
                    }
                });

            }
        });
        mListView.addHeaderView(mHeaderView);
        mListView.addFooterView(mLoadingFooter.getView());
        mListView.setEmptyView(emptyView);
        mListView.setAdapter(mAdapter);
        emptyView.setVisibility(View.GONE);

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mLoadingFooter.getState() == LoadingFooter.State.TheEnd
                        || mLoadingFooter.getState() == LoadingFooter.State.TheEnd) {
                    return;
                }
                if (firstVisibleItem + visibleItemCount > totalItemCount - 1
                        && totalItemCount != 0
                        && totalItemCount != mListView.getHeaderViewsCount() + mListView.getFooterViewsCount()
                        && mListView.getChildCount() > 0) {
                    loadNextPage();
                }
            }
        });
        loadNextPage();
    }

    private void loadNextPage() {
        getComments(pageNum + 1);
    }

    private void getComments(final int page) {
        if (mLoadingFooter.getState() != LoadingFooter.State.Idle) {
            return;
        }
        Log.e(TAG, "loadNextPager :" + page);
        mLoadingFooter.setState(LoadingFooter.State.Loading);
        String url = String.format(getInfoCommentsUrl, page + "", infoId, infoOwnerId, user.getId());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        int status = jsonObject.optInt("status");
                        if (status == 0) {
                            pageNum ++;
                            JSONArray jsonArray = jsonObject.optJSONArray("comments");
                            if (jsonArray.length() == 0) {
                                if (page == 1) {
                                    mListView.addHeaderView(mHeaderView);
                                    emptyView.setVisibility(View.VISIBLE);
                                    mListView.setEmptyView(emptyView);
//                                    mListView.addView(emptyView);
//                                    mListData.add(null);
//                                    mAdapter.notifyDataSetChanged();
                                }
                                mLoadingFooter.setState(LoadingFooter.State.TheEnd);
                            }
                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonObject = jsonArray.optJSONObject(i);
                                String commentOwnerName = jsonObject.optString("userName");
                                String commentOwnerId = jsonObject.optString("ownerId");
                                String commentOwnerSex = jsonObject.optString("sex");
                                String commentOwnerAttrs = jsonObject.optString("attrs");
                                String commentContent = jsonObject.optString("content");
                                String infoId = jsonObject.optString("infoId");
                                String commentId = jsonObject.optString("id");
                                String comentTime = jsonObject.optString("time");
                                String callBackCommentId = jsonObject.optString("callBackCommentId");
                                isFriend = jsonObject.optBoolean("friend");
                                int callBackFloor = jsonObject.optInt("callBackFloor");
                                String callBackCommentContent = jsonObject.optString("callBackComment");
                                Comment comment = new Comment(commentOwnerId, commentId, commentOwnerName, commentOwnerSex, commentOwnerAttrs, comentTime, commentContent, infoId, callBackCommentId, callBackFloor, callBackCommentContent);
                                mListData.add(comment);
                            }
                            mAdapter.notifyDataSetChanged();
                            pageNum++;
                            mLoadingFooter.setState(LoadingFooter.State.Idle, 3000);
                        } else {
                            Toast.makeText(CallBackActivity.this, "获取数据失败，可能是服务器端出问题了", Toast.LENGTH_SHORT).show();
                            if (page == 1) {
                                emptyView.setVisibility(View.VISIBLE);
                            }
                            mLoadingFooter.setState(LoadingFooter.State.Idle, 3000);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(CallBackActivity.this, "解析数据失败", Toast.LENGTH_SHORT).show();
                        Toast.makeText(CallBackActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                        if (page == 1) {
                            emptyView.setVisibility(View.VISIBLE);
                        }
                        mLoadingFooter.setState(LoadingFooter.State.Idle, 3000);
                    }
                });
        mQueue.add(jsonObjectRequest);
    }

    private void findViews() {
        mHeaderView = getLayoutInflater().inflate(R.layout.header_view,null);
        mTvSupports = (TextView) findViewById(R.id.acti_call_back_tv_supports);
        mImageContent = (NetworkImageView) mHeaderView.findViewById(R.id.acti_call_back_image_content);
        mTvAddFriend = (TextView) mHeaderView.findViewById(R.id.acti_call_back_tv_add_friend);
        mTvAttrs = (TextView) mHeaderView.findViewById(R.id.acti_call_back_tv_attrs);
        mTvUserName = (TextView) mHeaderView.findViewById(R.id.acti_call_back_tv_username);
        mUserFace = (NetworkCircleImageView) mHeaderView.findViewById(R.id.acti_call_back_image_user_face);
        mTvTextContent = (TextView) mHeaderView.findViewById(R.id.acti_call_back_tv_text_content);
        mTvActionBarUserName = (TextView) findViewById(R.id.acti_call_back_tv_actionBar_userName);
        mIvActionBarSex = (ImageView) findViewById(R.id.acti_call_back_tv_actionBar_sex);
        mIvBack = (ImageView) findViewById(R.id.acti_call_back_iv_back);
        mRlSendMessage = (RelativeLayout) findViewById(R.id.acti_call_back_rl_sendMessage);
        mRlCallBack = (RelativeLayout) findViewById(R.id.acti_call_back_rl_call_back);
        mRlSupports = (RelativeLayout) findViewById(R.id.acti_call_back_rl_supports);
        mLlBottom = (LinearLayout) findViewById(R.id.acti_call_back_ll_bottom);
        mLlSubmit = (LinearLayout) findViewById(R.id.acti_call_back_ll_bottom_submit);
        mEtCallBack = (EditText) findViewById(R.id.acti_call_back_et);
//        mListView = (ListView) findViewById(R.id.acti_call_back_listView);
        mListView = (ListView) findViewById(R.id.acti_call_back_listview);
        mIvSupports = (ImageView) findViewById(R.id.acti_call_back_iv_supports);
        mTsSupports = (TextSwitcher) findViewById(R.id.acti_call_back_ts_supports);

        mLoadingFooter = new LoadingFooter(CallBackActivity.this);
        mLoadingFooter.setState(LoadingFooter.State.Idle);
        emptyView = findViewById(R.id.acti_call_back_lv_empty_view);
        mTvSubmit = (TextView) findViewById(R.id.acti_call_back_tv_submit);
    }

    private void getDataFromIntent(Intent intent) {
        userName = intent.getStringExtra("userName");
        userFace = intent.getStringExtra("userFace");
        sex = intent.getStringExtra("sex");
        attrs = intent.getStringExtra("attrs");
        imageContent = intent.getStringExtra("imageContent");
        textContent = intent.getStringExtra("textContent");
        supports = intent.getIntExtra("supports", 0);
        infoOwnerId = intent.getStringExtra("infoOwnerId");
        infoId = intent.getStringExtra("infoId");
        haveSupports = intent.getBooleanExtra("haveSupports",false);
        startingLocation = intent.getIntExtra(DRAWING_START_LOCATION, 0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_call_back, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
