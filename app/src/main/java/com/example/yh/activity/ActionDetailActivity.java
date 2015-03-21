package com.example.yh.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.example.yh.context.MainApplication;
import com.example.yh.smartcycle.R;
import com.example.yh.view.NetworkCircleImageView;

public class ActionDetailActivity extends ActionBarActivity {

    private NetworkImageView mIvInfoContent;
    private TextView mTvInfoContent;
    private RelativeLayout mRlInfoContent;
    private NetworkCircleImageView mIvUserFace;
    private TextView mTvUserName;
    private TextView mTvLoction;
    private TextView mTvTime;
    private TextView mTvAbout;
    private TextView mTvJoinNum;
    private TextView mTvJoin;
    private LinearLayout mLlSendMessage;
    private LinearLayout mLlComment;
    private LinearLayout mLlFocus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_detail);
        findViews();
        initViews();
    }

    private void initViews() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MainApplication.ScreenWidth, 9 * (MainApplication.ScreenWidth / 16));
        mRlInfoContent.setLayoutParams(lp);
        mIvInfoContent.setDefaultImageResId(R.drawable.img_error);
    }

    private void findViews() {
        mRlInfoContent = (RelativeLayout) findViewById(R.id.acti_action_detail_layout_info);
        mIvInfoContent = (NetworkImageView) findViewById(R.id.acti_action_detail_iv_info_content);
        mTvInfoContent = (TextView) findViewById(R.id.acti_action_detail_tv_info_content);
        mIvUserFace = (NetworkCircleImageView) findViewById(R.id.acti_action_detail_user_face);
        mTvUserName = (TextView) findViewById(R.id.acti_action_detail_tv_user_name);
        mTvLoction = (TextView) findViewById(R.id.acti_action_detail_tv_location);
        mTvTime = (TextView) findViewById(R.id.acti_action_detail_tv_time);
        mTvJoinNum = (TextView) findViewById(R.id.acti_action_detail_tv_person_num);
        mTvJoin = (TextView) findViewById(R.id.acti_action_detail_tv_join);
        mLlSendMessage = (LinearLayout) findViewById(R.id.acti_action_detail_layout_send_message);
        mLlComment = (LinearLayout) findViewById(R.id.acti_action_detail_layout_comment);
        mLlFocus = (LinearLayout) findViewById(R.id.acti_action_detail_layout_focus);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_action_detail, menu);
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
