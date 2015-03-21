package com.example.yh.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.yh.data.User;
import com.example.yh.smartcycle.R;

public class ChangeUserNameActivity extends ActionBarActivity {

    private EditText mEtUserName;
    private ImageView mIvBack;
    private ImageView mIvOK;
    private User user;
    public static final int SUCC = 400;
    public static final int FL = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_name);
        init();
        findViews();
        setView();
        setListener();
    }

    private void setListener() {
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(FL);
                ChangeUserNameActivity.this.finish();
            }
        });

        mIvOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mEtUserName.getText().toString();
                Intent data = new Intent();
                data.putExtra("username", username);
                setResult(SUCC, data);
                finish();
            }
        });
    }

    private void init() {
        User.init(getApplicationContext());
        user = User.getInstance();
    }

    @Override
    public void onBackPressed() {
        setResult(FL);
        ChangeUserNameActivity.this.finish();
    }

    private void setView() {
        mEtUserName.setText(user.getUsername());
    }

    private void findViews() {
        mEtUserName = (EditText) findViewById(R.id.acti_change_username_et);
        mIvBack = (ImageView) findViewById(R.id.acti_change_username_back);
        mIvOK = (ImageView) findViewById(R.id.acti_change_username_OK);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_change_user_name, menu);
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
